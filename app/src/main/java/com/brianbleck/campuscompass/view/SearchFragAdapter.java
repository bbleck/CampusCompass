package com.brianbleck.campuscompass.view;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.brianbleck.campuscompass.R;
import com.brianbleck.campuscompass.model.Token;

import java.util.List;

public class SearchFragAdapter extends RecyclerView.Adapter<SearchFragAdapter.Holder>{

    private List<Token> listForRecycler;
    private Activity baseParent;



    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class Holder extends RecyclerView.ViewHolder{

        private ImageView itemImage;
        private TextView itemTitle;
        private TextView distance;
        private Button infoButton;
        private Button goButton;

        public Holder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.iv_search_item_image);
            itemTitle = itemView.findViewById(R.id.tv_search_item_title);
            distance = itemView.findViewById(R.id.tv_search_item_distance);
            infoButton = itemView.findViewById(R.id.btn_search_item_info);
            goButton = itemView.findViewById(R.id.btn_search_item_go);
        }
    }
}
