package com.brianbleck.campuscompass.model.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.drawable.Drawable;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.brianbleck.campuscompass.model.utility.TokenType;
import com.google.gson.annotations.SerializedName;

@Entity
public class Token implements Cloneable {

  //************ FIELDS THAT MATCH UNM OPEN DATA *************************//

  @Nullable
  @ColumnInfo(index = true, collate = ColumnInfo.NOCASE)
  private String title;

  @SerializedName("buildingnum")
  @ColumnInfo(name = "building_num")
  @Nullable
  private String buildingNum;

  @Nullable
  private String abbr;

  @Nullable
  private String campus;

  @Nullable
  private String keywords;

  @Nullable
  private double longitude;

  @Nullable
  private double latitude;

  @Nullable
  private String image;

  @Nullable
  private String link;

  @ColumnInfo(name = "token_id")
  @PrimaryKey
  @NonNull
  private String id;

  @Nullable
  private String description;

  //************************* FIELDS THAT DO NOT MATCH UNM OPEN DATA *********************//

  @ColumnInfo(name = "token_type", index = true)
  private TokenType tokenType;

  @Ignore
  private Drawable drawable;




  @Override
  public Object clone() throws CloneNotSupportedException {
    Token theClone = new Token();
    theClone.setDescription(getDescription());
    theClone.setImage(getImage());
    theClone.setLink(getLink());
    theClone.setLatitude(getLatitude());
    theClone.setLongitude(getLongitude());
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

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
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
}
