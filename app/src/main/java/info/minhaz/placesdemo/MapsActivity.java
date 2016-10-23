package info.minhaz.placesdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import info.minhaz.placesdemo.model.Place;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {
    private static final String PLACE ="mPlace";
    private static final String LONGITUDE ="longitude";
    private static final String TITLE ="title";
    private GoogleMap mMap;
    private double mLatitude=-1;
    private double mLongitude=-1;
    private Place mPlace;

    public static void startActivity(Context context, Place place){
        Intent intent=new Intent(context, MapsActivity.class);
        intent.putExtra(PLACE, place);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        setContentView(R.layout.activity_maps);
        Intent intent=getIntent();
        if (intent!=null){
            mPlace =intent.getParcelableExtra(PLACE);

        }
        findViewById(R.id.textViewDone).setOnClickListener(this);
        if (mPlace !=null && !TextUtils.isEmpty(mPlace.getName())){
            TextView titleTextView=(TextView)findViewById(R.id.textViewTitle);
            titleTextView.setText(mPlace.getName());
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latLng = new LatLng( 40.7128, -73.989308);
        if (mPlace!=null){
            latLng=new LatLng( mPlace.getLatitude(), mPlace.getLongitude());
        }
        //LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(latLng).title(mPlace.getName()));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera( CameraUpdateFactory.zoomTo( 17.0f ) );
    }

    @Override
    public void onClick(View view) {
        finish();
    }
}
