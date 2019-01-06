package com.brianbleck.campuscompass.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.brianbleck.campuscompass.R;
import com.brianbleck.campuscompass.controller.Main2Activity;
import com.brianbleck.campuscompass.model.entity.Token;
import com.google.maps.android.SphericalUtil;
import java.util.LinkedList;
import java.util.List;

/**
 * A {@link Fragment} that contains a {@link RecyclerView} representing a list of locations of
 * interest.
 */
public class SearchFragment extends Fragment {

  /**
   * An interface to communicate to parent instantiating activities..
   */
  public interface SearchFragListener {

    /**
     * Gets {@link List} of {@link Token}.
     *
     * @return the {@link List} of {@link Token}.
     */
    List<Token> getTokensList();

    /**
     * Method to communicate to controller that the search list has been filtered.
     */
    void onSearchFiltered();

    /**
     * Update filtered list.
     *
     * @param filteredList the {@link List} of {@link Token} representing the filtered list.
     */
    void updateFilteredList(List<Token> filteredList);

    /**
     * Gets current location of user.
     *
     * @return user's current {@link Location}
     */
    Location getmCurrentLocation();
  }

  private static final String TAG = "SearchFragment";

  private EditText refineSearch;
  private TextView searchTitle;
  private Button resetRefine;
  private RecyclerView recyclerView;
  private SearchFragAdapter adapter;
  private LinearLayoutManager manager;
  private List<Token> listForRecycler;
  private int callingViewId;
  private SearchFragListener searchFragListener;
  private String searchInput;


  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View theView = inflater.inflate(R.layout.fragment_search_item, container, false);
    initViews(theView);
    initData();
    initRecyclerView();
    return theView;
  }

  @Override
  public void onResume() {
    super.onResume();
    resetEditText();
    updateListInAdapter();
    redrawListInAdapter();
  }

  /**
   * Initializes the recycler view.
   */
  private void initRecyclerView() {
    adapter = new SearchFragAdapter(getActivity(), listForRecycler);
    manager = new LinearLayoutManager(getContext());
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(manager);
  }

  /**
   * Initializes the data references.
   */
  private void initData() {
    try {
      callingViewId = ((Main2Activity) getActivity()).getCallingViewId();
    } catch (NullPointerException e) {
      callingViewId = R.id.building;
    }
    setSearchTitle();
    listForRecycler = searchFragListener.getTokensList();
  }

  /**
   * A method containing a switch that sets the search title.
   */
  private void setSearchTitle() {
    switch (callingViewId) {
      case R.id.iv_main_frag_0:
        searchTitle.setText(R.string.image_title_0);
        break;
      case R.id.iv_main_frag_1:
        searchTitle.setText(R.string.image_title_1);
        break;
      case R.id.iv_main_frag_2:
        searchTitle.setText(R.string.image_title_2);
        break;
      case R.id.iv_main_frag_3:
        searchTitle.setText(R.string.image_title_3);
        break;
      case R.id.iv_main_frag_4:
        searchTitle.setText(R.string.image_title_4);
        break;
      case R.id.iv_main_frag_5:
        searchTitle.setText(R.string.image_title_5);
        break;
      case R.id.iv_main_frag_6:
        searchTitle.setText(R.string.image_title_6);
        break;
      case R.id.iv_main_frag_7:
        searchTitle.setText(R.string.image_title_7);
        break;
      case R.id.iv_main_frag_8:
        searchTitle.setText(R.string.image_title_8);
        break;
      default:
        searchTitle.setText(getString(R.string.no_title_available));
    }
  }

  /**
   * A method that initializes the views for a given View object.
   * @param theView
   */
  private void initViews(View theView) {
    refineSearch = theView.findViewById(R.id.edit_text_search_filter_words);
    searchTitle = theView.findViewById(R.id.tv_search_title);
    resetRefine = theView.findViewById(R.id.btn_search_reset);
    recyclerView = theView.findViewById(R.id.rv_search_list_items);
    resetRefine.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        resetEditText();
        searchInput="";
        updateListInAdapter();
        redrawListInAdapter();
      }
    });

    refineSearch.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      /**
       * A method that filters the list each time the text is changed.
       * @param s
       */
      @Override
      public void afterTextChanged(Editable s) {
        searchInput = refineSearch.getText().toString().toLowerCase();
        filterList(searchInput);
        searchFragListener.onSearchFiltered();
      }
    });
  }

  /**
   * A method that resets the search text.
   */
  private void resetEditText() {
    refineSearch.setText("", TextView.BufferType.NORMAL);
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    try {
      searchFragListener = (SearchFragListener) getActivity();
    } catch (ClassCastException e) {
      //do nothing until/unless a way of handling this is discovered
    }

  }

  /**
   * Update list in adapter.
   * Scrub anything with a 0.0 lat/lng as that is  not a properly formed token
   */
  public void updateListInAdapter() {
    listForRecycler = new LinkedList<>();
    List<Token> tempTokensList = searchFragListener.getTokensList();
    for (int i = 0; i < tempTokensList.size(); i++) {
      if (tempTokensList.get(i).getMLatitude() == 0.0
          || tempTokensList.get(i).getMLongitude() == 0.0) {
      } else {
        tempTokensList.get(i).setDrawable(grabBearingDrawable(tempTokensList.get(i), searchFragListener.getmCurrentLocation()));
        listForRecycler.add(tempTokensList.get(i));
      }
    }
    searchFragListener.updateFilteredList(listForRecycler);
  }

  /**
   * A method that filters the recyclerview list.
   * @param searchTerm
   */
  private void filterList(String searchTerm){
    List<Token> filteredList = new LinkedList<>();
    for (Token token :
        listForRecycler) {
      if (token.getTitle() != null && token.getDescription() != null && (
          token.getTitle().toLowerCase().contains(searchTerm) || token
              .getDescription().toLowerCase().contains(searchTerm))) {
        filteredList.add(token);
      }
    }
    searchFragListener.updateFilteredList(filteredList);
    drawFilteredList(filteredList);
  }

  /**
   * A method that draws a new list in the recyclerview.
   * @param filteredList
   */
  private void drawFilteredList(List<Token> filteredList) {
    adapter = new SearchFragAdapter(getActivity(), filteredList);
    manager = new LinearLayoutManager(getContext());
    adapter.notifyDataSetChanged();
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(manager);
  }

  /**
   * Redraw list in adapter.
   */
  public void redrawListInAdapter(){
    adapter = new SearchFragAdapter(getActivity(), listForRecycler);
    manager = new LinearLayoutManager(getContext());
    adapter.notifyDataSetChanged();
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(manager);
  }

  /**
   * A method that retrieves the correct drawable based on a user's bearing.
   * @param token
   * @param userLoc
   * @return the correct bearing drawable
   */
  private Drawable grabBearingDrawable(Token token, Location userLoc){
    com.google.android.gms.maps.model.LatLng userLatLng = new com.google.android.gms.maps.model.LatLng(userLoc.getLatitude(), userLoc.getLongitude());
    com.google.android.gms.maps.model.LatLng destLatLng = new com.google.android.gms.maps.model.LatLng(token.getMLatitude(), token.getMLongitude());
    double theBearing = SphericalUtil.computeHeading(userLatLng, destLatLng);
    Drawable tempDrawable = null;
    if((Double.compare(theBearing, -22.5) >= 0 && Double.compare(theBearing, 0) <= 0 )
        || ( Double.compare(theBearing, 22.5) <= 0 && Double.compare(theBearing, 0) >= 0)){
      tempDrawable = ResourcesCompat.getDrawable(getActivity().getResources(), R.drawable.crop_compass_n, null);
    }else if(Double.compare(theBearing, 67.5) <= 0 && Double.compare(theBearing, 22.5) > 0){
      tempDrawable = ResourcesCompat.getDrawable(getActivity().getResources(), R.drawable.crop_compass_ne, null);
    }else if(Double.compare(theBearing, 112.5) <= 0 && Double.compare(theBearing, 67.5) > 0){
      tempDrawable = ResourcesCompat.getDrawable(getActivity().getResources(), R.drawable.crop_compass_e, null);
    }else if(Double.compare(theBearing, 157.5) <= 0 && Double.compare(theBearing, 112.5) > 0){
      tempDrawable = ResourcesCompat.getDrawable(getActivity().getResources(), R.drawable.crop_compass_se, null);
    }else if(Double.compare(theBearing, 157.5) > 0 || Double.compare(theBearing, -157.5) < 0){
      tempDrawable = ResourcesCompat.getDrawable(getActivity().getResources(), R.drawable.crop_compass_s, null);
    }else if(Double.compare(theBearing, -112.5) <= 0 && Double.compare(theBearing, -157.5) > 0){
      tempDrawable = ResourcesCompat.getDrawable(getActivity().getResources(), R.drawable.crop_compass_sw, null);
    }else if(Double.compare(theBearing, -67.5) <= 0 && Double.compare(theBearing, -112.5) > 0){
      tempDrawable = ResourcesCompat.getDrawable(getActivity().getResources(), R.drawable.crop_compass_w, null);
    }else if(Double.compare(theBearing, -22.5) <= 0 && Double.compare(theBearing, -67.5) > 0){
      tempDrawable = ResourcesCompat.getDrawable(getActivity().getResources(), R.drawable.crop_compass_nw, null);
    }
    return tempDrawable;
  }

  /**
   * A method that retrieves the correct drawable associated with a Token's TokenType.
   * @param token
   * @return the correct drawable
   */
  private Drawable grabDrawable(Token token) {
    Drawable tempDrawable = null;
    try {
      tempDrawable = ResourcesCompat
          .getDrawable(getActivity().getResources(), R.drawable.ic_magnifying_glass, null);
      switch (token.getTokenType()) {
        case BLUE_PHONE:
          tempDrawable = ResourcesCompat
              .getDrawable(getActivity().getResources(), R.drawable.blue_phone, null);
          tempDrawable.setTint(getResources().getColor(R.color.colorPrimary, null));
          break;
        case DINING:
          tempDrawable = ResourcesCompat
              .getDrawable(getActivity().getResources(), R.drawable.food, null);
          tempDrawable.setTint(getResources().getColor(R.color.foodBrown, null));
          break;
        case LIBRARY:
          tempDrawable = ResourcesCompat
              .getDrawable(getActivity().getResources(), R.drawable.library, null);
          tempDrawable.setTint(getResources().getColor(R.color.libraryOrange, null));
          break;
        case RESTROOM:
          tempDrawable = ResourcesCompat
              .getDrawable(getActivity().getResources(), R.drawable.restroom, null);
          tempDrawable.setTint(getResources().getColor(R.color.restroomGray, null));
          break;
        case COMPUTER_POD:
          tempDrawable = ResourcesCompat
              .getDrawable(getActivity().getResources(), R.drawable.computer_pod, null);
          break;
        case SHUTTLE_STOP:
          tempDrawable = ResourcesCompat
              .getDrawable(getActivity().getResources(), R.drawable.shuttle_stop, null);
          tempDrawable.setTint(getResources().getColor(R.color.shuttlePurple, null));
          break;
        case HEALTHY_VENDING:
          tempDrawable = ResourcesCompat
              .getDrawable(getActivity().getResources(), R.drawable.vending, null);
          tempDrawable.setTint(getResources().getColor(R.color.healthGreen, null));
          break;
        case METERED_PARKING:
          tempDrawable = ResourcesCompat
              .getDrawable(getActivity().getResources(), R.drawable.parking, null);
          tempDrawable.setTint(getResources().getColor(R.color.parkingBlue, null));
          break;
        case BUILDING:
          tempDrawable = ResourcesCompat
              .getDrawable(getActivity().getResources(), R.drawable.building, null);
          tempDrawable.setTint(getResources().getColor(R.color.darkGray, null));
          break;
      }
    } catch (NullPointerException e) {
      tempDrawable = null;
    }
    return tempDrawable;
  }

  /**
   * Gets {@link SearchFragAdapter}.
   *
   * @return the {@link SearchFragAdapter}.
   */
  public SearchFragAdapter getAdapter() {
    return adapter;
  }

  /**
   * Gets {@link LinearLayoutManager}
   *
   * @return the {@link LinearLayoutManager}
   */
  public LinearLayoutManager getManager() {
    return manager;
  }

  /**
   * Gets {@link List} of {@link Token} for the {@link RecyclerView}.
   *
   * @return the {@link List} of {@link Token}.
   */
  public List<Token> getListForRecycler() {
    return listForRecycler;
  }
}
