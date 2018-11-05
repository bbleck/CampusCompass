package com.brianbleck.campuscompass.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.brianbleck.campuscompass.R;
import com.google.android.gms.maps.SupportMapFragment;

public class MapsFragment extends Fragment {

    private static final String TAG = "MapsFragment";
    private MapsFragmentListener mapsFragmentListener;

    public interface MapsFragmentListener{
        void setMainRefMapsFrag(MapsFragment mapsFrag);
        void callMapAsync(SupportMapFragment supportMapFragment);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View theView = inflater.inflate(R.layout.fragment_maps, container, false);
        mapsFragmentListener.setMainRefMapsFrag(this);
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
        mapsFragmentListener.callMapAsync(mapFragment);
    }
    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mapsFragmentListener = (MapsFragmentListener) getActivity();
        } catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException" + e.getMessage());
        }

    }
}
