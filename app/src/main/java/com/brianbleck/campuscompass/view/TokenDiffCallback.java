package com.brianbleck.campuscompass.view;

import android.support.v7.util.DiffUtil;
import com.brianbleck.campuscompass.model.entity.Token;
import java.util.List;

public class TokenDiffCallback extends DiffUtil.Callback {

  private final List<Token> mOldTokenList;
  private final List<Token> mNewTokenList;

  public TokenDiffCallback(
      List<Token> mOldTokenList,
      List<Token> mNewTokenList) {
    this.mOldTokenList = mOldTokenList;
    this.mNewTokenList = mNewTokenList;
  }

  @Override
  public int getOldListSize() {
    return mOldTokenList.size();
  }

  @Override
  public int getNewListSize() {
    return mNewTokenList.size();
  }

  @Override
  public boolean areItemsTheSame(int i, int i1) {
    return mOldTokenList.get(i).getId() == mNewTokenList.get(i1).getId();
  }

  @Override
  public boolean areContentsTheSame(int i, int i1) {
    return mOldTokenList.get(i).getDistance() == mNewTokenList.get(i1).getDistance();
  }
}
