package com.brianbleck.campuscompass.view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.brianbleck.campuscompass.R;
import com.brianbleck.campuscompass.controller.MainActivity;
import com.brianbleck.campuscompass.model.Token;

import java.util.List;

public class SearchFragAdapter extends RecyclerView.Adapter<SearchFragAdapter.Holder>{

    private List<Token> listForRecycler;
    private Activity mActivity;

    public SearchFragAdapter(Activity mActivity, List<Token> listForRecycler){
        this.mActivity = mActivity;
        this.listForRecycler = listForRecycler;
    }


    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_search_frag, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int position) {
        holder.itemImage.setImageDrawable(listForRecycler.get(position).getImage());
        holder.itemTitle.setText(listForRecycler.get(position).getTitle());
        String tempDistance = mActivity.getResources().getString(R.string.distance_away)
                + MainActivity.calcDistance(listForRecycler.get(position).getLongitude(),
                listForRecycler.get(position).getLatitude());
        holder.distance.setText(tempDistance);
        holder.goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mActivity.getBaseContext(), "Clicked Go", Toast.LENGTH_SHORT).show();
                ((MainActivity)mActivity).setmViewPager(MainActivity.MAPS_FRAG_PAGER_NUMBER, listForRecycler.get(holder.getAdapterPosition()));
            }
        });
        holder.infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MainActivity)mActivity).setTargetItem(listForRecycler.get(holder.getAdapterPosition()));
                ((MainActivity)mActivity).setmViewPager(MainActivity.INFO_POPUP_FRAG, listForRecycler.get(holder.getAdapterPosition()));
            }
        });
    }


    @Override
    public int getItemCount() {
        return listForRecycler.size();
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
