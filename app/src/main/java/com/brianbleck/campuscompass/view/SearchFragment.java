package com.brianbleck.campuscompass.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.brianbleck.campuscompass.R;
import com.brianbleck.campuscompass.controller.Main2Activity;
import com.brianbleck.campuscompass.model.entity.Token;
import java.util.LinkedList;
import java.util.List;

public class SearchFragment extends Fragment {

  public interface SearchFragListener {

    List<Token> getTokensList();
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

  private void initRecyclerView() {
    adapter = new SearchFragAdapter(getActivity(), listForRecycler);
    manager = new LinearLayoutManager(getContext());
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(manager);
  }

  private void initData() {
    try {
      callingViewId = ((Main2Activity) getActivity()).getCallingViewId();
    } catch (NullPointerException e) {
      callingViewId = R.id.building;
    }
    setSearchTitle();
    listForRecycler = searchFragListener.getTokensList();
  }

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
        Log.d(TAG, "setSearchTitle: unknown callingId error");
    }
  }


  private void initViews(View theView) {
    refineSearch = theView.findViewById(R.id.edit_text_search_filter_words);
    searchTitle = theView.findViewById(R.id.tv_search_title);
    resetRefine = theView.findViewById(R.id.btn_search_reset);
    recyclerView = theView.findViewById(R.id.rv_search_list_items);
    resetRefine.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        resetEditText();
      }
    });
    refineSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        Toast.makeText(getContext(), "Edit Text entered", Toast.LENGTH_SHORT).show();
        //todo: handle filtering of recyclerview contents to match edittext
        return false;
      }
    });
  }

  private void resetEditText() {
    refineSearch.setText("", TextView.BufferType.NORMAL);
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    try {
      searchFragListener = (SearchFragListener) getActivity();
    } catch (ClassCastException e) {
      Log.e(TAG, "onAttach: ClassCastException" + e.getMessage());
      //do nothing except logging it for debugging
    }

  }

  public void updateListInAdapter() {
    listForRecycler = new LinkedList<>();
    listForRecycler = searchFragListener.getTokensList();
    List<Token> cleanedList = new LinkedList<>();
    for (int i = 0; i < listForRecycler.size(); i++) {
      if (listForRecycler.get(i).getMLatitude() == 0.0
          || listForRecycler.get(i).getMLongitude() == 0.0) {
        Log.d(TAG, "updateListInAdapter: cleaned a 0.0 long/lat");
      } else {
          listForRecycler.get(i).setDrawable(grabDrawable(listForRecycler.get(i)));
        cleanedList.add(listForRecycler.get(i));
      }
    }
    adapter = new SearchFragAdapter(getActivity(), cleanedList);
    manager = new LinearLayoutManager(getContext());
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(manager);
  }

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

  public SearchFragAdapter getAdapter() {
    return adapter;
  }
}
