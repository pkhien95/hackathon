package com.example.administrator.jamme.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.jamme.libs.LocationHelper;
import com.example.administrator.jamme.model.Event;
import com.example.administrator.jamme.utlis.Utlis;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nineoldandroids.view.ViewHelper;

import com.example.administrator.jamme.R;

public class DetailEventActivity extends AppCompatActivity implements OnMapReadyCallback, ObservableScrollViewCallbacks {

    private GoogleMap mMap;
    private Toolbar mToolbarView;
    private ObservableScrollView mScrollView;
    private int mParallaxImageHeight;
    private Event eventData;

    private ImageView mImg, imgDetail;
    private TextView tvTitle, tvAddress, tvDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_event);

        // Get dimension value from dimens.xml
        Resources mRes = getResources();
        mParallaxImageHeight = mRes.getDimensionPixelSize(R.dimen.parallax_image_height);

        mImg = (ImageView) findViewById(R.id.image);
        imgDetail = (ImageView) findViewById(R.id.imgDetail);
        mToolbarView = (Toolbar) findViewById(R.id.toolbar);
        mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        tvDesc = (TextView) findViewById(R.id.tvDesc);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setSupportActionBar(mToolbarView);
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().
                getColor(R.color.colorPrimary)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mScrollView.setScrollViewCallbacks(this);

        eventData = (Event) getIntent().getSerializableExtra(Utlis.INTENT_HOME_DETAIL_KEY);
        tvTitle.setText(Utlis.eventMap.get(eventData.getType()));
        tvAddress.setText(new LocationHelper(this).getAddress(new LatLng(
                eventData.getLat(), eventData.getLng())));
        tvDesc.setText(eventData.getMessage());

        //set background for event
        switch (eventData.getType()) {
            case 0:
                mImg.setBackgroundResource(R.mipmap.bg_detail_sos);
                break;
            case 1:
                mImg.setBackgroundResource(R.mipmap.bg_detail_jam);
                break;
            case 2:
                mImg.setBackgroundResource(R.mipmap.bg_detail_accident);
                break;
            default:
                mImg.setBackgroundResource(R.mipmap.bg_detail_ohter);
        }
        // decode base64 to bitmap
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onScrollChanged(mScrollView.getCurrentScrollY(), false, false);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setMyLocationEnabled(true);

        BitmapDescriptor tmpIcon = null;
        switch (eventData.getType()) {
            case 0:
                tmpIcon = BitmapDescriptorFactory.fromResource(R.mipmap.event_sos);
                break;
            case 1:
                tmpIcon = BitmapDescriptorFactory.fromResource(R.mipmap.event_jam);
                break;
            case 2:
                tmpIcon = BitmapDescriptorFactory.fromResource(R.mipmap.event_accident);
                break;
            default:
                tmpIcon = BitmapDescriptorFactory.fromResource(R.mipmap.event_other);
        }
        LatLng point = new LatLng(eventData.getLat(), eventData.getLng());
        MarkerOptions tmpMarker = new MarkerOptions().position(point).icon(tmpIcon).snippet(new LocationHelper(this).getAddress(point));
        mMap.addMarker(tmpMarker).setTitle(eventData.getMessage());

        mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        int baseColor = getResources().getColor(R.color.colorPrimary);
        float alpha = Math.min(1, (float) scrollY / mParallaxImageHeight);
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
        ViewHelper.setTranslationY(mImg, scrollY / 2);

        if(alpha == 1){
            mToolbarView.setTitle("CHI TIẾT SỰ KIỆN");
        }else{
            mToolbarView.setTitle("");
        }
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }
}
