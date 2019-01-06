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

/**
 * A {@link Fragment} that will display the map and related map information.
 */
public class MapsFragment extends Fragment {

  private static final String TAG = "MapsFragment";
  private MapsFragmentListener mapsFragmentListener;

  /**
   * An interface to communicate to parent instantiating activities.
   */
  public interface MapsFragmentListener {

    /**
     * Sets main reference to the {@link MapsFragment}.
     *
     * @param mapsFrag the {@link MapsFragment}.
     */
    void setMainRefMapsFrag(MapsFragment mapsFrag);

    /**
     * Call mapAsync() on a {@link SupportMapFragment}.
     *
     * @param supportMapFragment the {@link SupportMapFragment}.
     */
    void callMapAsync(SupportMapFragment supportMapFragment);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View theView = inflater.inflate(R.layout.fragment_maps, container, false);
    mapsFragmentListener.setMainRefMapsFrag(this);
    initMaps();
    return theView;
  }

  /**
   * Initializes the map container with a map fragment.
   */
  private void initMaps() {
    SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
        .findFragmentById(R.id.gm_map_frag);
    if (mapFragment == null) {
      mapFragment = new SupportMapFragment();
      getActivity().getSupportFragmentManager().beginTransaction()
          .replace(R.id.frag_container_2b, mapFragment)
          .commit();
      getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    mapsFragmentListener.callMapAsync(mapFragment);
  }

  /**
   * Sets a reference to the instantiating class to call listener methods on.
   * @param context the instantiating activity context.
   */
  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    try {
      mapsFragmentListener = (MapsFragmentListener) getActivity();
    } catch (ClassCastException e) {
      //do nothing for the time being
      //todo: discover if there is anything that can be done
    }

  }
}
