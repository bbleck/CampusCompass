package com.brianbleck.campuscompass.model.utility;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import com.brianbleck.campuscompass.R;
import com.brianbleck.campuscompass.model.entity.Token;

public class TokenPrepper {

  private static final String TAG = "TokenPrepper";

  public static Token prep(Context activity, Token unprepped){
    Token prepped = new Token();
    fixTokenType(unprepped, prepped);
    fixDescription(activity, unprepped, prepped);
    fixDrawable(activity, unprepped, prepped);
    prepped.setBuildingNumber(unprepped.getBuildingNumber());
    fixImageUrlString(activity, unprepped, prepped);
    prepped.setId(unprepped.getId());
    prepped.setLatitude(unprepped.getLatitude());
    prepped.setLongitude(unprepped.getLongitude());
    fixTitle(activity, unprepped, prepped);
    fixLinkString(activity, unprepped, prepped);

    return prepped;
  }

  private static void fixLinkString(Context activity, Token unprepped, Token prepped) {
    if(unprepped.getLink()==null){
      prepped.setLink(activity.getString(R.string.default_link_string));
    }else{
      prepped.setLink(unprepped.getLink());
    }
  }

  private static void fixTitle(Context activity, Token unprepped, Token prepped) {
    if(unprepped.getTitle()==null){
      prepped.setTitle(activity.getString(R.string.default_title));
    }else{
      prepped.setTitle(unprepped.getTitle());
    }
  }

  private static void fixImageUrlString(Context activity, Token unprepped, Token prepped) {
    if(unprepped.getImage()==null){
      prepped.setImage(activity.getString(R.string.default_image_url_string));
    }else{
      prepped.setImage(unprepped.getImage());
    }
  }

  private static void fixTokenType(Token unprepped, Token prepped) {
    if(unprepped.getTokenType()==null){
      prepped.setTokenType(TokenType.BUILDING);
      Log.d(TAG, "prep: error with null tokentype");
    }else{
      prepped.setTokenType(unprepped.getTokenType());
    }
  }

  private static void fixDrawable(Context activity, Token unprepped, Token prepped) {
    Drawable tempDrawable = ResourcesCompat
        .getDrawable(activity.getResources(), R.drawable.ic_magnifying_glass, null);
    if(unprepped == null){
      Log.d(TAG, "fixDrawable: unprepped is null");
    }
    if(unprepped.getDrawable() == null){
      switch (prepped.getTokenType()){
        case BLUE_PHONE:
          tempDrawable = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.blue_phone, null);
          break;
        case DINING:
          tempDrawable = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.food, null);
          break;
        case LIBRARY:
          tempDrawable = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.library, null);
          break;
        case RESTROOM:
          tempDrawable = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.restroom, null);
          break;
        case COMPUTER_POD:
          tempDrawable = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.computer_pod, null);
          break;
        case SHUTTLE_STOP:
          tempDrawable = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.shuttle_stop, null);
          break;
        case HEALTHY_VENDING:
          tempDrawable = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.vending, null);
          break;
        case METERED_PARKING:
          tempDrawable = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.parking, null);
          break;
        case BUILDING:
          tempDrawable = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.building, null);
          break;
      }
    }else{
      prepped.setDrawable(unprepped.getDrawable());
    }
    prepped.setDrawable(tempDrawable);
  }

  private static void fixDescription(Context activity, Token unprepped, Token prepped) {
    if(unprepped.getDescription()==null){
      prepped.setDescription(activity.getResources().getString(R.string.default_description));
    }else{
      prepped.setDescription(unprepped.getDescription());
    }
  }

}
