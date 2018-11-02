package com.brianbleck.campuscompass.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.brianbleck.campuscompass.R;
import com.brianbleck.campuscompass.controller.Main2Activity;

public class MainMenuFragment extends Fragment {

  private static final String TAG = "MainMenuFragment";
  public static final int COUNT_SEARCHES = 9;
  private ImageView[] imageItems = new ImageView[COUNT_SEARCHES];
  private TextView[] textItems = new TextView[COUNT_SEARCHES];
  private MainMenuFragListener mainMenuFragListener;

  public interface MainMenuFragListener {

    void goToSearchFrag(int iD);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View theView = inflater.inflate(R.layout.fragment_main_menu, container, false);
    mainMenuFragListener = (Main2Activity) getActivity();
    initViews(theView);

    return theView;
  }

  private void initViews(View theView) {
    for (int i = 0; i < COUNT_SEARCHES; i++) {
      final int id = getResources()
          .getIdentifier("iv_main_frag_" + i, "id", getContext().getPackageName());
      int idTxt = getResources()
          .getIdentifier("tv_main_frag_" + i, "id", getContext().getPackageName());
      final String idTxtSetTxt = getString(getResources()
          .getIdentifier("image_title_" + i, "string", getContext().getPackageName()));
      imageItems[i] = theView.findViewById(id);
      textItems[i] = theView.findViewById(idTxt);
      textItems[i].setText(idTxtSetTxt);
      imageItems[i].setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          mainMenuFragListener.goToSearchFrag(id);
        }
      });
      textItems[i].setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          mainMenuFragListener.goToSearchFrag(id);
        }
      });
    }
  }
}
