package info.minhaz.placesdemo;

/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <minhaz@minhaz.info> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Minhaz Rafi Chowdhury
 *
 * You may obtain a copy of the License at
 *
 * https://fedoraproject.org/wiki/Licensing/Beerware
 * ----------------------------------------------------------------------------
 */
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.rey.material.app.BottomSheetDialog;
import com.rey.material.widget.Button;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import info.minhaz.placesdemo.model.AppObject;
import info.minhaz.placesdemo.model.StorageService;

public class LandingActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private static final int REQUEST_CODE_PLACE_PICKER_REQUEST = 1;
    private static final int REQUEST_CODE_CONTACT_PICKER_RESULT = 1001;
    private static final int REQUEST_CODE_CONTACT_READ_PERMISSION = 200;

    private static final String SENDER="sender";

    private GoogleApiClient mGoogleApiClient;
    private TextView mTextView;
    private RecyclerView mRecyclerView;
    private RecyclerAdapter mAdapter;
    private int colorPrimaryDark;


    //selected values
    private String selectedPlaceName;
    private double selectedPlaceLatitude;
    private double selectedPlaceLongitude;
    private String selectedPhoneNumber;



    private BottomSheetDialog mBottomSheetDialog;
    private final String[] PERMISSION_CONTACT = {Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        colorPrimaryDark=getResources().getColor(R.color.colorPrimaryDark);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(colorPrimaryDark);
        }


        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        findViewById(R.id.textViewShare).setOnClickListener(this);

        TextView titleTextView=(TextView)findViewById(R.id.textViewTitle);
        titleTextView.setText(getString(R.string.app_name));
        mTextView=(TextView)findViewById(R.id.textView);
        mRecyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter=new RecyclerAdapter(this, null);
        mRecyclerView.setAdapter(mAdapter);
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        reset();
        Uri data = this.getIntent().getData();
        if (data != null && data.isHierarchical()) {
            processIncomingRequest(data);
        }

    }
    private void processIncomingRequest(Uri data){
        String longitude=data.getQueryParameter(info.minhaz.placesdemo.model.Place.JsonKey.LONGITUDE);
        String latitude=data.getQueryParameter(info.minhaz.placesdemo.model.Place.JsonKey.LATITUDE);
        String name=data.getQueryParameter(info.minhaz.placesdemo.model.Place.JsonKey.NAME);
        String sender=data.getQueryParameter(SENDER);
        info.minhaz.placesdemo.model.Place place=new info.minhaz.placesdemo.model.Place();
        place.setName(name);
        place.setLongitude(Double.parseDouble(longitude));
        place.setLatitude(Double.parseDouble(latitude));
        StorageService.storeReceiveData(this, place, sender);
        AppObject object=new AppObject();
        object.setPhone(sender);
        try {
            object.setReceivedPlace(place.toJson().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        object.setTime(System.currentTimeMillis());

    }

    private void requestContactPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_CONTACTS)) {
            Snackbar.make(findViewById(R.id.activity_landing), R.string.permission_message,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(LandingActivity.this, PERMISSION_CONTACT,
                                    REQUEST_CODE_CONTACT_READ_PERMISSION);
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(this, PERMISSION_CONTACT,
                    REQUEST_CODE_CONTACT_READ_PERMISSION);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_CONTACT_READ_PERMISSION) {
            Map<String, Integer> perms = new HashMap<String, Integer>();
            for (int i = 0; i < permissions.length; i++)
                perms.put(permissions[i], grantResults[i]);
            if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && perms.get(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
                    && perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(findViewById(R.id.activity_landing), R.string.permission_granted,
                        Snackbar.LENGTH_SHORT).show();

                startLocationSelection();
            } else {
                Snackbar.make(findViewById(R.id.activity_landing), R.string.permission_not_granted,
                        Snackbar.LENGTH_SHORT).show();

            }
        }

    }
    @Override
    public void onResume(){
        super.onResume();

        LoaderManager manager=getSupportLoaderManager();
        Loader loader=manager.getLoader(R.id.loader);
        if (loader!=null){
            manager.restartLoader(R.id.loader, null,
                    new LoaderCallback(this));
        }else{
            manager.initLoader(R.id.loader, null,
                    new LoaderCallback(this));
        }
    }
    public void updateUi(Cursor cursor){
        mAdapter.swapCursor(cursor);
        mTextView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);

    }
    private void startLocationSelection(){
        selectedPlaceName =null;
        selectedPlaceLongitude =-1;
        selectedPlaceLatitude =-1;
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();


        try {
            startActivityForResult(builder.build(this), REQUEST_CODE_PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                selectedPlaceName =place.getName().toString();
                selectedPlaceLatitude =place.getLatLng().latitude;
                selectedPlaceLongitude =place.getLatLng().longitude;


            }
        }else if (requestCode == REQUEST_CODE_CONTACT_PICKER_RESULT) {
            if (resultCode == RESULT_OK) {
                if (!TextUtils.isEmpty(selectedPlaceName)){
                    selectedPhoneNumber=contactPicked(data);
                    /*if (!TextUtils.isEmpty(selectedPhoneNumber)){

                    }else{
                        Snackbar.make(findViewById(R.id.activity_landing), R.string.phone_required,
                                Snackbar.LENGTH_INDEFINITE);
                    }*/

                }

            }
        }
        displaySelectionWizard();
    }
    private void showAlert(String message){
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.Invalid_Entry))
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })

                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    private void shareLink(){
        if (TextUtils.isEmpty(selectedPlaceName)
                || selectedPlaceLatitude==-1
                ||selectedPlaceLongitude==-1
                || TextUtils.isEmpty(selectedPhoneNumber)){
            showAlert(getString(R.string.missing));
            return;
        }
        info.minhaz.placesdemo.model.Place place=new info.minhaz.placesdemo.model.Place();
        place.setLatitude(selectedPlaceLatitude);
        place.setLongitude(selectedPlaceLongitude);
        place.setName(selectedPlaceName);
        String placeString= null;
        try {
            placeString = place.toJson().toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        if (!TextUtils.isEmpty(placeString)){
            StorageService.storeSendData(this, place, selectedPhoneNumber);
            //Cache.insert(this, number, true, placeString);
        }
        TelephonyManager tMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();
        if (TextUtils.isEmpty(mPhoneNumber)){
            showAlert(getString(R.string.devcie_missing));
            return;
        }
        mBottomSheetDialog.dismiss();
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("minhaz.info")
                .appendPath("placesdemo")
                .appendQueryParameter(info.minhaz.placesdemo.model.Place.JsonKey.NAME, selectedPlaceName)
                .appendQueryParameter(SENDER, mPhoneNumber)
                .appendQueryParameter(info.minhaz.placesdemo.model.Place.JsonKey.LATITUDE, String.valueOf(selectedPlaceLatitude))
                .appendQueryParameter(info.minhaz.placesdemo.model.Place.JsonKey.LONGITUDE, String.valueOf(selectedPlaceLongitude));
        String myUrl = builder.build().toString();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, myUrl);
        sendIntent.setType("text/plain");
        reset();
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));

    }
    private String contactPicked(Intent data) {
        try {
            Uri uri = data.getData();
            String[] projection = { ContactsContract.CommonDataKinds.Phone.NUMBER};
            Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
            if (cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    public boolean isPermissionsGranted(){
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED)
                ||(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                ||(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED);
    }
    private void reset(){
        selectedPlaceLatitude=-1;
        selectedPlaceLongitude=-1;
        selectedPhoneNumber=null;
        selectedPlaceName=null;
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch (id){
            case R.id.textViewShare:

                displaySelectionWizard();
                /*if (isPermissionsGranted()) {
                    requestContactPermission();
                }else{
                    startLocationSelection();
                }*/

                break;

        }
    }
    private void displaySelectionWizard(){
        mBottomSheetDialog = new BottomSheetDialog(this, R.style.Material_App_BottomSheetDialog);
        View v = LayoutInflater.from(this).inflate(R.layout.share_wizard, null);

        Button locationButton = (Button)v.findViewById(R.id.buttonLocation);
        locationButton.setCompoundDrawables(null,null,new IconicsDrawable(this,
                FontAwesome.Icon.faw_map_marker).color(colorPrimaryDark).sizeDp(26),null);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.dismiss();
                if (isPermissionsGranted()) {
                    requestContactPermission();
                }else{
                    startLocationSelection();
                }
            }
        });
        TextView locationText=(TextView)v.findViewById(R.id.textViewSelectedLocation);;
        if (!TextUtils.isEmpty(selectedPlaceName)){
            locationText.setText(selectedPlaceName);
        }

        Button phoneButton = (Button)v.findViewById(R.id.buttonPhone);
        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.dismiss();
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.Contacts.CONTENT_URI);
                contactPickerIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(contactPickerIntent, REQUEST_CODE_CONTACT_PICKER_RESULT);
            }
        });
        phoneButton.setCompoundDrawables(null,null,new IconicsDrawable(this,
                FontAwesome.Icon.faw_phone_square).color(colorPrimaryDark).sizeDp(26),null);
        TextView phoneText=(TextView)v.findViewById(R.id.textViewSelectedPhone);;
        if (!TextUtils.isEmpty(selectedPhoneNumber)){
            phoneText.setText(selectedPhoneNumber);
        }


        Button buttonShare = (Button)v.findViewById(R.id.buttonShare);
        buttonShare.setCompoundDrawables(null,null,new IconicsDrawable(this,
                FontAwesome.Icon.faw_share).color(colorPrimaryDark).sizeDp(26),null);
        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareLink();
            }
        });

        mBottomSheetDialog.heightParam(ViewGroup.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.contentView(v).show();
    }

    class LoaderCallback implements LoaderManager.LoaderCallbacks<Cursor>{
        private final Context mContext;
        LoaderCallback(Context context){
            mContext=context;

        }
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (id==R.id.loader) {
                return new LocationLoader(mContext);
            }else {
                return null;
            }
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

            if (loader.getId()==R.id.loader){
                updateUi(data);
            }

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mAdapter.changeCursor(null);
        }
    }
}
