package com.brianbleck.campuscompass.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.brianbleck.campuscompass.controller.MainActivity;
import com.brianbleck.campuscompass.model.entity.Token;

import java.util.LinkedList;
import java.util.List;

public class SearchFragment extends Fragment {

  public interface SearchFragListener{
    List<Token> getTokensList();
    boolean isTestData();
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
  private Activity mActivity;


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
    callingViewId = ((MainActivity) getActivity()).getCallingViewId();
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
    try{
      searchFragListener = (SearchFragListener) getActivity();
      mActivity = getActivity();
    } catch (ClassCastException e){
      Log.e(TAG, "onAttach: ClassCastException" + e.getMessage());
    }

  }

}
