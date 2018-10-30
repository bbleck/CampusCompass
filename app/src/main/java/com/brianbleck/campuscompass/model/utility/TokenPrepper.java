package com.brianbleck.campuscompass.model.utility;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import com.brianbleck.campuscompass.R;
import com.brianbleck.campuscompass.controller.MainActivity;
import com.brianbleck.campuscompass.model.entity.Token;

public class TokenPrepper {

  public static Token prep(Activity activity, Token unprepped){
    Token prepped = new Token();

    if(unprepped.getDescription()==null){
      prepped.setDescription(activity.getResources().getString(R.string.default_description));
    }else{
      prepped.setDescription(unprepped.getDescription());
    }
    Drawable tempDrawable = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.ic_magnifying_glass, null);
    if(unprepped.getDrawable()==null){
      switch (unprepped.getTokenType()){
        case BLUE_PHONE:
//          tempDrawable = ResourcesCompat.getDrawable(activity.getResources(), R.drawable, null);
          break;
        case DINING:
          break;
        case LIBRARY:
          break;
        case RESTROOM:
          break;
        case COMPUTER_POD:
          break;
        case SHUTTLE_STOP:
          break;
        case HEALTHY_VENDING:
          break;
        case METERED_PARKING:
          break;
        case BUILDING:
          break;
      }
    }else{
      prepped.setDrawable(unprepped.getDrawable());
    }

    return prepped;
  }

}
