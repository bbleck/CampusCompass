package com.brianbleck.campuscompass.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import com.brianbleck.campuscompass.R;
import com.brianbleck.campuscompass.controller.Main2Activity;
import com.brianbleck.campuscompass.model.entity.Token;
import com.squareup.picasso.Picasso;
import java.util.List;

/**
 * Describes an adapter for a {@link RecyclerView}.
 */
public class SearchFragAdapter extends RecyclerView.Adapter<SearchFragAdapter.Holder> {

  private static final String TAG = "SearchFragAdapter";

  private List<Token> listForRecycler;
  private Activity mActivity;
  private SearchFragAdapterListener searchFragAdapterListener;

  /**
   * Constructor for {@link SearchFragAdapter}.
   *
   * @param mActivity the instantiating {@link Activity}.
   * @param listForRecycler a {@link List} of {@link Token} objects that will be used to populate a
   * {@link RecyclerView}.
   */
  public SearchFragAdapter(Activity mActivity, List<Token> listForRecycler) {
    this.mActivity = mActivity;
    this.listForRecycler = listForRecycler;
  }

  /**
   * An interface to communicate to parent instantiating activities.
   */
  public interface SearchFragAdapterListener {

    /**
     * Begin marker update.
     *
     * @param position the position
     */
    void beginMarkerUpdate(int position);

    /**
     * Switch to the {@link MapsFragment}.
     */
    void goToMapFrag();
  }

  @NonNull
  @Override
  public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.rv_item_search_frag, parent, false);
    try {
      searchFragAdapterListener = (SearchFragAdapterListener) mActivity;
    } catch (ClassCastException e) {
      Log.e(TAG, "onAttach: ClassCastException" + e.getMessage());
      //do nothing else except log it for debugging purposes
    }
    return new Holder(view);
  }

  /**
   * Binds data to a {@link android.support.v7.widget.RecyclerView.ViewHolder}.
   * @param holder object that holds all of the {@link View}.
   * @param position the index in the {@link List} that is the source of data.
   */
  @Override
  public void onBindViewHolder(@NonNull final Holder holder, int position) {
    holder.itemImage.setImageDrawable(listForRecycler.get(position).getDrawable());
    holder.itemImage.setScaleType(ScaleType.CENTER_CROP);
    holder.itemTitle.setText(listForRecycler.get(position).getTitle());
    String tempDistance = mActivity.getResources().getString(R.string.distance_away)
        + " " + listForRecycler.get(position).getDistance();
    holder.distance.setText(tempDistance);
    holder.goButton.setOnClickListener(new View.OnClickListener() {
      /**
       * Sends the user into the {@link com.google.android.gms.maps.GoogleMap} app for directions to the {@link android.location.Location} associated with the parameter.
       * @param v a {@link View} that represents one location item in the {@link RecyclerView}.
       */
      @Override
      public void onClick(View v) {
        String directionsQuery = "google.navigation:q=" + listForRecycler.get(holder.getAdapterPosition()).getMLatitude().toString()
            + "," + listForRecycler.get(holder.getAdapterPosition()).getMLongitude().toString()
            + "&mode=w";
        Uri gmmIntentUri = Uri.parse(directionsQuery);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        mActivity.startActivity(mapIntent);

      }
    });
    holder.infoButton.setOnClickListener(new View.OnClickListener() {
      /**
       * Opens a dialog with additional information related to the location associated with the parameter.
       * @param v a {@link View} that represents one location item in the {@link RecyclerView}.
       */
      @Override
      public void onClick(View v) {
        ((Main2Activity) mActivity).setTargetItem(listForRecycler.get(holder.getAdapterPosition()));
        startInfoPopup();
      }
    });
    searchFragAdapterListener.beginMarkerUpdate(position);

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

  /**
   * Stores references to {@link View}.
   */
  class Holder extends RecyclerView.ViewHolder {

    private ImageView itemImage;
    private TextView itemTitle;
    private TextView distance;
    private Button infoButton;
    private Button goButton;

    /**
     * Instantiates a new Holder.
     *
     * @param itemView the item view
     */
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
