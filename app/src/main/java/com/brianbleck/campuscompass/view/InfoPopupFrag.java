package com.brianbleck.campuscompass.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.brianbleck.campuscompass.R;
import com.brianbleck.campuscompass.controller.Main2Activity;
import com.brianbleck.campuscompass.model.entity.Token;

public class InfoPopupFrag extends DialogFragment {

  private static final String TAG = "InfoPopupFrag";

  public interface InfoPopupFragListener {

    void setParentRefToInfoFrag(InfoPopupFrag infoFrag);

    void goToSearchFrag(int iD);
  }

  private ImageView itemImage;
  private TextView itemTitle;
  private TextView itemUrl;
  private TextView itemDescription;
  private Button itemReturn;
  private Token theItem;
  private InfoPopupFragListener infoPopupFragListener;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View theView = inflater.inflate(R.layout.popup_frag_item_info, container, false);
    infoPopupFragListener.setParentRefToInfoFrag(this);
    initViews(theView);
//        initData();
    return theView;
  }

  @Override
  public void onResume() {
    super.onResume();
    initData();
  }

  public void initData() {
//        theItem = ((MainActivity)getActivity()).getTargetItem();
//        itemImage.setImageDrawable(theItem.getImage());//
//        itemDescription.setText(theItem.getDescription());
//        itemDescription.setMovementMethod(new ScrollingMovementMethod());
//        itemUrl.setText(theItem.getLink());
//        itemTitle.setText(theItem.getTitle());
    //todo: set the info as needed
  }

  private void initViews(View theView) {
    itemImage = theView.findViewById(R.id.iv_popup_info_image);
    itemDescription = theView.findViewById(R.id.tv_popup_info_description);
    itemReturn = theView.findViewById(R.id.btn_popup_info_return);
    itemUrl = theView.findViewById(R.id.tv_popup_info_url);
    itemTitle = theView.findViewById(R.id.tv_popup_info_title);
    itemReturn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ((Main2Activity) getActivity())
            .goToSearchFrag(((Main2Activity) getActivity()).getCallingViewId());
      }
    });
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    try {
      infoPopupFragListener = (InfoPopupFragListener) getActivity();
    } catch (ClassCastException e) {
      Log.e(TAG, "onAttach: ClassCastException" + e.getMessage());
    }

  }

}
