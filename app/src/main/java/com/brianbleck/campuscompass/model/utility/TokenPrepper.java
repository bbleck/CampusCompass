package com.brianbleck.campuscompass.model.utility;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import com.brianbleck.campuscompass.R;
import com.brianbleck.campuscompass.model.entity.Token;

/**
 * Preps a {@link Token} prior to entry into the {@link android.arch.persistence.room.Database}.
 */
public class TokenPrepper {

  private static final String TAG = "TokenPrepper";

  /**
   * Constructor.
   *
   * @param activity the activity
   * @param unprepped the {@link Token} to be prepped
   * @return the prepped {@link Token}
   */
  public static Token prep(Context activity, Token unprepped){
    Token prepped = new Token();
    fixTokenType(unprepped, prepped);
    fixDescription(activity, unprepped, prepped);
    fixDrawable(activity, unprepped, prepped);
    prepped.setBuildingNum(unprepped.getBuildingNum());
    fixImageUrlString(activity, unprepped, prepped);
    prepped.setId(unprepped.getId());
    prepped.setMLatitude(unprepped.getMLatitude());
    prepped.setMLongitude(unprepped.getMLongitude());
    fixTitle(activity, unprepped, prepped);
    fixLinkString(activity, unprepped, prepped);

    return prepped;
  }

  /**
   * A method to set String link, ensuring there is never a null object for link.
   * @param activity
   * @param unprepped
   * @param prepped
   */
  private static void fixLinkString(Context activity, Token unprepped, Token prepped) {
    if(unprepped.getLink()==null || unprepped.getLink().isEmpty() ){
      prepped.setLink(activity.getString(R.string.default_link_string));
    }else{
      prepped.setLink(unprepped.getLink());
    }
  }

  /**
   * A method to set String title, ensuring there is never a null object for title.
   * @param activity
   * @param unprepped
   * @param prepped
   */
  private static void fixTitle(Context activity, Token unprepped, Token prepped) {
    if(unprepped.getTitle()==null || unprepped.getTitle().isEmpty()){
      prepped.setTitle(activity.getString(R.string.default_title));
    }else{
      prepped.setTitle(unprepped.getTitle());
    }
  }

  /**
   * A method to set String image, ensuring there is never a null object for image.
   * @param activity
   * @param unprepped
   * @param prepped
   */
  private static void fixImageUrlString(Context activity, Token unprepped, Token prepped) {
    if(unprepped.getImage()==null || unprepped.getImage().isEmpty()){
      prepped.setImage(activity.getString(R.string.default_image_url_string));
    }else{
      prepped.setImage(unprepped.getImage());
    }
  }

  /**
   * A method to set TokenType, ensuring there is never a null object for TokenType.
   * @param unprepped
   * @param prepped
   */
  private static void fixTokenType(Token unprepped, Token prepped) {
    if(unprepped.getTokenType()==null){
      prepped.setTokenType(TokenType.BUILDING);
    }else{
      prepped.setTokenType(unprepped.getTokenType());
    }
  }

  /**
   * A method to set drawable, ensuring there is never a null object for drawable.
   * @param activity
   * @param unprepped
   * @param prepped
   */
  private static void fixDrawable(Context activity, Token unprepped, Token prepped) {
    Drawable tempDrawable = ResourcesCompat
        .getDrawable(activity.getResources(), R.drawable.ic_magnifying_glass, null);
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

  /**
   * A method to set String description, ensuring there is never a null object for description.
   * @param activity
   * @param unprepped
   * @param prepped
   */
  private static void fixDescription(Context activity, Token unprepped, Token prepped) {
    if(unprepped.getDescription()==null || unprepped.getDescription().isEmpty()){
      prepped.setDescription(activity.getResources().getString(R.string.default_description));
    }else{
      prepped.setDescription(unprepped.getDescription());
    }
  }

}
