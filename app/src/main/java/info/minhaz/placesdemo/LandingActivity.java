package info.minhaz.placesdemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONException;

import info.minhaz.placesdemo.model.Cache;

public class LandingActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private static final int REQUEST_CODE_PLACE_PICKER_REQUEST = 1;
    private static final int REQUEST_CODE_CONTACT_PICKER_RESULT = 1001;
    private static final int REQUEST_CODE_CONTACT_READ_PERMISSION = 200;

    private GoogleApiClient mGoogleApiClient;
    private TextView mTextView;
    private RecyclerView mRecyclerView;
    private RecyclerAdapter mAdapter;


    private String placeName;
    private double placeLatitude;
    private double placeLongitude;

    private final String[] PERMISSION_CONTACT = {Manifest.permission.READ_CONTACTS};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
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
       // MapsActivity.startActivity(this, -1, -1, "88109834");

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
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
        if (cursor!=null && cursor.getCount()>0){
            mAdapter.swapCursor(cursor);
            mTextView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }

    }
    private void startLocationSelection(){
        placeName=null;
        placeLongitude=-1;
        placeLatitude=-1;
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
                placeName=place.getName().toString();
                placeLatitude=place.getLatLng().latitude;
                placeLongitude=place.getLatLng().longitude;
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.Contacts.CONTENT_URI);
                contactPickerIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(contactPickerIntent, REQUEST_CODE_CONTACT_PICKER_RESULT);
            }
        }else if (requestCode == REQUEST_CODE_CONTACT_PICKER_RESULT) {
            if (resultCode == RESULT_OK) {
                if (!TextUtils.isEmpty(placeName)){
                    String number=contactPicked(data);
                    if (!TextUtils.isEmpty(number)){
                        info.minhaz.placesdemo.model.Place place=new info.minhaz.placesdemo.model.Place();
                        place.setLatitude(placeLatitude);
                        place.setLongitude(placeLongitude);
                        place.setName(placeName);
                        String placeString= null;
                        try {
                            placeString = place.toJson().toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (!TextUtils.isEmpty(placeString)){
                            Cache.insert(this, number, true, placeString);
                        }
                        Uri.Builder builder = new Uri.Builder();
                        builder.scheme("http")
                                .authority("minhaz.info")
                                .appendPath("placesdemo")
                                .appendQueryParameter(info.minhaz.placesdemo.model.Place.JsonKey.NAME, placeName)
                                .appendQueryParameter(info.minhaz.placesdemo.model.Place.JsonKey.LATITUDE, String.valueOf(placeLatitude))
                                .appendQueryParameter(info.minhaz.placesdemo.model.Place.JsonKey.LONGITUDE, String.valueOf(placeLongitude));
                        String myUrl = builder.build().toString();
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, myUrl);
                        sendIntent.setType("text/plain");
                        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
                    }else{
                        Snackbar.make(findViewById(R.id.activity_landing), R.string.phone_required,
                                Snackbar.LENGTH_INDEFINITE);
                    }

                }

            }
        }
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

    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch (id){
            case R.id.textViewShare:
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestContactPermission();
                }else{
                    startLocationSelection();
                }

                break;

        }
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
