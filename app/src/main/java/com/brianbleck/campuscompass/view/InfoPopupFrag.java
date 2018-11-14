package com.brianbleck.campuscompass.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.brianbleck.campuscompass.R;
import com.brianbleck.campuscompass.model.entity.Token;

public class InfoPopupFrag extends DialogFragment {

  private static final String TAG = "InfoPopupFrag";

  public interface InfoPopupFragListener {

    void setParentRefToInfoFrag(InfoPopupFrag infoFrag);

    void goToSearchFrag(int iD);

    Token getTargetItem();
  }

  private ImageView itemImage;
  private TextView itemTitle;
  private TextView itemUrl;
  private TextView itemDescription;
  private TextView itemKeywords;
  private TextView itemAbbr;
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
    initData();
    return theView;
  }

  @Override
  public void onResume() {
    super.onResume();
    initData();
  }

  public void initData() {
        theItem = null;
        theItem = infoPopupFragListener.getTargetItem();
        itemImage.setImageDrawable(theItem.getDrawable());
        itemKeywords.setText(theItem.getKeywords());
        itemDescription.setMovementMethod(new ScrollingMovementMethod());
        itemTitle.setText(theItem.getTitle());
        itemUrl.setText(theItem.getLink());
        itemUrl.setTextIsSelectable(true);
        itemAbbr.setText(theItem.getAbbr());
        String tempDesc = theItem.getDescription();
        if(tempDesc.contains("<br")){
          Spanned htmlAsSpanned = Html.fromHtml(tempDesc);
          itemDescription.setText(htmlAsSpanned);
        }else{
          itemDescription.setText(theItem.getDescription());
        }

    //todo: set the info as needed
  }

  private void initViews(View theView) {
    itemImage = theView.findViewById(R.id.iv_popup_info_image);
    itemDescription = theView.findViewById(R.id.tv_popup_info_description);
    itemKeywords = theView.findViewById(R.id.tv_popup_info_keywords);
    itemReturn = theView.findViewById(R.id.btn_popup_info_return);
    itemUrl = theView.findViewById(R.id.tv_popup_info_url);
    itemTitle = theView.findViewById(R.id.tv_popup_info_title);
    itemAbbr = theView.findViewById(R.id.tv_popup_info_abbr);
    itemReturn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dismiss();
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
