package com.brianbleck.campuscompass.view;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.brianbleck.campuscompass.R;
import com.brianbleck.campuscompass.controller.Main2Activity;
import com.brianbleck.campuscompass.model.entity.Token;
import java.util.List;

public class SearchFragAdapter extends RecyclerView.Adapter<SearchFragAdapter.Holder> {

  private static final String TAG = "SearchFragAdapter";

  private List<Token> listForRecycler;
  private Activity mActivity;

  public SearchFragAdapter(Activity mActivity, List<Token> listForRecycler) {
    this.mActivity = mActivity;
    this.listForRecycler = listForRecycler;
  }

  public interface SearchFragAdapterListener {

    void goToMapFrag(Token item);
  }


  @NonNull
  @Override
  public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.rv_item_search_frag, parent, false);
    return new Holder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull final Holder holder, int position) {
    holder.itemImage.setImageDrawable(listForRecycler.get(position).getDrawable()); //set image
    holder.itemTitle.setText(listForRecycler.get(position).getTitle());
    String tempDistance = mActivity.getResources().getString(R.string.distance_away)
        + Main2Activity.calcDistance(listForRecycler.get(position).getMLongitude(),
        listForRecycler.get(position).getMLatitude());
    holder.distance.setText(tempDistance);
    holder.goButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
//              Toast.makeText(mActivity.getBaseContext(), "Clicked Go", Toast.LENGTH_SHORT).show();
        ((Main2Activity) mActivity).goToMapFrag(listForRecycler.get(holder.getAdapterPosition()));
      }
    });
    holder.infoButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ((Main2Activity) mActivity).setTargetItem(listForRecycler.get(holder.getAdapterPosition()));
        startInfoPopup();
      }
    });
  }

  private void startInfoPopup() {
    InfoPopupFrag infoPopupFrag = new InfoPopupFrag();
    FragmentManager fm = ((Main2Activity) mActivity).getSupportFragmentManager();
    infoPopupFrag.show(fm, "InfoPopupFrag");
  }


  @Override
  public int getItemCount() {
    return listForRecycler.size();
  }

  class Holder extends RecyclerView.ViewHolder {

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
