package com.brianbleck.campuscompass.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.brianbleck.campuscompass.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

public class MapsFragment extends Fragment {

    private static final String TAG = "MapsFragment";
    private MapsFragmentListener mapsfragListener;
    private FragmentActivity mActivity;

    public interface MapsFragmentListener{
        void setMainRefMapsFrag(MapsFragment mapsFrag);
        void callMapAsync(SupportMapFragment supportMapFragment);
    }

    private GoogleMap googleMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View theView = inflater.inflate(R.layout.fragment_maps, container, false);
        mapsfragListener.setMainRefMapsFrag(this);
        initMaps();
        return theView;
    }

    public void initMaps() {
        SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.gm_map_frag);
      if (mapFragment == null) {
        mapFragment = new SupportMapFragment();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frag_container_2, mapFragment)
            .commit();
      }
//        mapFragment.getMapAsync(this);
        mapsfragListener.callMapAsync(mapFragment);
//      Toast.makeText(getActivity(), "getmapasync called.", Toast.LENGTH_SHORT).show();
    }

//    @Override
//    public void onMapReady(GoogleMap gMap) {
//      Toast.makeText(getActivity(), "into onMapReady", Toast.LENGTH_SHORT).show();
//        this.googleMap = gMap;
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        this.googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mapsfragListener = (MapsFragmentListener) getActivity();
            mActivity = getActivity();
        } catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException" + e.getMessage());
        }

    }

  @Override
  public void onDestroyView() {

    super.onDestroyView();
  }
}
