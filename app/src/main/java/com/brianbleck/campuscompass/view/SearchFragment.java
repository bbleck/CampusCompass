package com.brianbleck.campuscompass.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.brianbleck.campuscompass.R;

public class SearchFragment extends Fragment {
    private EditText refineSearch;
    private TextView searchTitle;
    private Button resetRefine;
    private RecyclerView recyclerView;
//    private SearchFragAdapter adapter;
    private LinearLayoutManager manager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View theView = inflater.inflate(R.layout.fragment_search_item, container, false);
        initViews(theView);

        return theView;
    }

    private void initViews(View theView) {
        refineSearch = theView.findViewById(R.id.edit_text_search_filter_words);
        searchTitle = theView.findViewById(R.id.tv_search_title);
        resetRefine = theView.findViewById(R.id.btn_search_reset);
        recyclerView = theView.findViewById(R.id.rv_search_list_items);
        
    }
}
