package com.brianbleck.campuscompass.model.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.drawable.Drawable;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.brianbleck.campuscompass.model.utility.TokenType;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity
public class Token implements Cloneable {

  //************ FIELDS THAT MATCH UNM OPEN DATA *************************//

  @Expose
  @Nullable
  @ColumnInfo(index = true, collate = ColumnInfo.NOCASE)
  private String title;

  @Expose
  @SerializedName("buildingnum")
  @ColumnInfo(name = "building_num")
  @Nullable
  private String buildingNum;

  @Expose
  @Nullable
  private String abbr;

  @Expose
  @Nullable
  private String campus;

  @Expose
  @Nullable
  private String keywords;

  @Expose
  @Ignore
  @SerializedName("longitude")
  private String rawLongitude;

  @Expose
  @Ignore
  @SerializedName("latitude")
  private String rawLatitude;

  @Expose
  @Nullable
  private String image;

  @Expose
  @Nullable
  private String link;

  @Expose
  @ColumnInfo(name = "token_id")
  @PrimaryKey
  @NonNull
  private String id;

  @Expose
  @Nullable
  private String description;

  //************************* FIELDS THAT DO NOT MATCH UNM OPEN DATA *********************//

  @ColumnInfo(name = "token_type", index = true)
  private TokenType tokenType;

  @Ignore
  private Drawable drawable;

  private Double mLongitude;

  private Double mLatitude;


  @Override
  public Object clone() throws CloneNotSupportedException {
    Token theClone = new Token();
    theClone.setDescription(getDescription());
    theClone.setImage(getImage());
    theClone.setLink(getLink());
    theClone.setMLatitude(getMLatitude());
    theClone.setMLongitude(getMLongitude());
    theClone.setTitle(getTitle());
    theClone.setTokenType(getTokenType());
    theClone.setId(getId());
    theClone.setDrawable(getDrawable());
    theClone.setBuildingNum(getBuildingNum());
    return theClone;
  }

  private void setDefaultImage() {
    //todo: implement method
  }

  public TokenType getTokenType() {
    return tokenType;
  }

  public void setTokenType(TokenType tokenType) {
    this.tokenType = tokenType;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
    //todo:  create drawable object for URL if it is non null
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Drawable getDrawable() {
    return drawable;
  }

  public void setDrawable(Drawable drawable) {
    this.drawable = drawable;
  }

  public String getBuildingNum() {
    return buildingNum;
  }

  public void setBuildingNum(String buildingNum) {
    this.buildingNum = buildingNum;
  }

  public String getAbbr() {
    return abbr;
  }

  public void setAbbr(String abbr) {
    this.abbr = abbr;
  }

  public String getCampus() {
    return campus;
  }

  public void setCampus(String campus) {
    this.campus = campus;
  }

  public String getKeywords() {
    return keywords;
  }

  public void setKeywords(String keywords) {
    this.keywords = keywords;
  }

  public String getRawLongitude() {
    return rawLongitude;
  }

  public void setRawLongitude(String rawLongitude) {
    this.rawLongitude = rawLongitude;
  }

  public String getRawLatitude() {
    return rawLatitude;
  }

  public void setRawLatitude(String rawLatitude) {
    this.rawLatitude = rawLatitude;
  }


  public Double getMLongitude() {
    if(mLongitude ==null){
      if(getRawLongitude()!=null) {
        try {
          setMLongitude(Double.parseDouble(getRawLongitude()));
          return mLongitude;
        } catch (NumberFormatException e) {
          //intentionally blank
        }
      }
      mLongitude = 0.0;
    }
    return mLongitude;
  }

  public void setMLongitude(Double mLongitude) {
    this.mLongitude = mLongitude;
  }


  public Double getMLatitude() {
    if(mLatitude ==null){
      if(getRawLatitude()!=null) {
        try {
          setMLatitude(Double.parseDouble(getRawLatitude()));
          return mLatitude;
        } catch (NumberFormatException e) {
          //intentionally blank
        }
      }
      mLatitude = 0.0;
    }
    return mLatitude;
  }

  public void setMLatitude(Double mLatitude) {
    this.mLatitude = mLatitude;
  }
}
