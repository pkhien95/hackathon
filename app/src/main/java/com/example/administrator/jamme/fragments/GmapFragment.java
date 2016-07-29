package com.example.administrator.jamme.fragments;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;

import com.example.administrator.jamme.activities.DetailEventActivity;
import com.example.administrator.jamme.adapter.GooglePlacesAutocompleteAdapter;
import com.example.administrator.jamme.libs.ApiClient;
import com.example.administrator.jamme.libs.LocationHelper;
import com.example.administrator.jamme.model.ApiInterface;
import com.example.administrator.jamme.model.Event;
import com.example.administrator.jamme.model.EventReponse;
import com.example.administrator.jamme.utlis.Utlis;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import com.example.administrator.jamme.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import net.i2p.android.ext.floatingactionbutton.FloatingActionButton;


public class GmapFragment extends Fragment implements OnMapReadyCallback,  GoogleMap.OnInfoWindowClickListener{

    private GoogleMap mMap;
    private CircleProgressBar mPrgBar;
    private FloatingActionButton mFabPostEvent, mFabFindRoute;
    private Context mContext;
    private RelativeLayout layout_map;
    private AutoCompleteTextView autoSearch;

    /***Using storage a dictionary marker and integer***/
    private LinkedHashMap<LatLng, Integer> markerMap;
    private List<Event> events;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        Firebase.setAndroidContext(mContext);

        mFabPostEvent = (FloatingActionButton) view.findViewById(R.id.fabPostEvent);
        mFabFindRoute = (FloatingActionButton) view.findViewById(R.id.fabFindRoute);
        mPrgBar = (CircleProgressBar) view.findViewById(R.id.prgLoading);
        layout_map = (RelativeLayout) view.findViewById(R.id.layout_map);

        autoSearch = (AutoCompleteTextView) view.findViewById(R.id.autoSearch);
        autoSearch.setAdapter(new GooglePlacesAutocompleteAdapter(mContext, R.layout.custom_list_item));
        autoSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String address = autoSearch.getText().toString();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LocationHelper(mContext).getLatLng(address)));
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        markerMap = new LinkedHashMap<>();

        mFabPostEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                PostEventFragment mPostEventFragment = new PostEventFragment ();
                mPostEventFragment.show(getFragmentManager(), "Demo");
            }
        });

        getEventData();
        return view;

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        LocationHelper curLocation = new LocationHelper(mContext);
        LatLng curPoint = new LatLng(curLocation.getLocation().getLatitude(), curLocation.getLocation().getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(curPoint));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
        mMap.setOnInfoWindowClickListener(this);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        int markerID = markerMap.get(marker.getPosition());
        Event eventData = events.get(markerID);

        Intent i = new Intent(mContext, DetailEventActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(Utlis.INTENT_HOME_DETAIL_KEY, eventData);
        i.putExtras(mBundle);
        startActivity(i);
    }

    private void getEventData(){
        Firebase ref = new Firebase(Utlis.FIREBASE_URL + "/event");
        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                events = new ArrayList<Event>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Event tmp = postSnapshot.getValue(Event.class);
                    events.add(tmp);
                }

                updateUI();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    private void updateUI(){
        int count = 0;
        if (events == null) return;
        for (Event event : events) {
            LatLng point = new LatLng(event.getLat(), event.getLng());

            BitmapDescriptor tmpIcon = null;
            String typeMsg = "";

            switch (event.getType()) {
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

            typeMsg = Utlis.eventMap.get(event.getType()).toUpperCase();
            MarkerOptions tmpMarker = new MarkerOptions().position(point).icon(tmpIcon).snippet(new LocationHelper(mContext).getAddress(point));
            mMap.addMarker(tmpMarker).setTitle(typeMsg);

            markerMap.put(point, count);
            count++;

        }
    }
    /*Using for retrofit
    private void getDataEvent(){
        ApiInterface apiService = ApiClient.retrofitExecute(Utlis.BASE_URL).create(ApiInterface.class);

        Handler Asynchronous without AsyncTask
        Call<EventReponse> call = apiService.getEventMap();
        call.enqueue(new Callback<EventReponse>() {
            @Override
            public void onResponse(Call<EventReponse> call, Response<EventReponse> response) {
                events = response.body().getEvents();
                updateUI();
            }

            @Override
            public void onFailure(Call<EventReponse> call, Throwable t) {

            }
        });
    }

    private void updateUI(){
        int count = 0;
        for (Event event: events){
            LatLng point = new LatLng(event.getLat(), event.getLng());

            BitmapDescriptor tmpIcon = null;
            String typeMsg = "";

            switch (event.getType()){
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

            typeMsg = Utlis.eventMap.get(event.getType()).toUpperCase();
            MarkerOptions tmpMarker = new MarkerOptions().position(point).icon(tmpIcon).snippet(new LocationHelper(mContext).getAddress(point));
            mMap.addMarker(tmpMarker).setTitle(typeMsg);

            markerMap.put(point , count);
            count++;
        }
    }*/
}
