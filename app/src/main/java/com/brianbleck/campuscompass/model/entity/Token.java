package com.brianbleck.campuscompass.model.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.drawable.Drawable;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.brianbleck.campuscompass.model.utility.TokenType;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Entity class for use by {@link android.arch.persistence.room.Room} and by {@link
 * retrofit2.Retrofit}.
 */
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

  @Ignore
  private int distance;

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

  /**
   * Gets token type.
   *
   * @return the token type
   */
  public TokenType getTokenType() {
    return tokenType;
  }

  /**
   * Sets token type.
   *
   * @param tokenType the token type
   */
  public void setTokenType(TokenType tokenType) {
    this.tokenType = tokenType;
  }

  /**
   * Gets image.
   *
   * @return the image
   */
  public String getImage() {
    return image;
  }

  /**
   * Sets image.
   *
   * @param image the image
   */
  public void setImage(String image) {
    this.image = image;
  }

  /**
   * Gets distance.
   *
   * @return the distance
   */
  public int getDistance() {
    return distance;
  }

  /**
   * Set distance.
   *
   * @param distance the distance
   */
  public void setDistance(int distance){
    this.distance = distance;
  }

  /**
   * Gets link.
   *
   * @return the link
   */
  public String getLink() {
    return link;
  }

  /**
   * Sets link.
   *
   * @param link the link
   */
  public void setLink(String link) {
    this.link = link;
  }

  /**
   * Gets title.
   *
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets title.
   *
   * @param title the title
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Gets description.
   *
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets description.
   *
   * @param description the description
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets id.
   *
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Sets id.
   *
   * @param id the id
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gets drawable.
   *
   * @return the drawable
   */
  public Drawable getDrawable() {
    return drawable;
  }

  /**
   * Sets drawable.
   *
   * @param drawable the drawable
   */
  public void setDrawable(Drawable drawable) {
    this.drawable = drawable;
  }

  /**
   * Gets building num.
   *
   * @return the building num
   */
  public String getBuildingNum() {
    return buildingNum;
  }

  /**
   * Sets building num.
   *
   * @param buildingNum the building num
   */
  public void setBuildingNum(String buildingNum) {
    this.buildingNum = buildingNum;
  }

  /**
   * Gets abbr.
   *
   * @return the abbr
   */
  public String getAbbr() {
    return abbr;
  }

  /**
   * Sets abbr.
   *
   * @param abbr the abbr
   */
  public void setAbbr(String abbr) {
    this.abbr = abbr;
  }

  /**
   * Gets campus.
   *
   * @return the campus
   */
  public String getCampus() {
    return campus;
  }

  /**
   * Sets campus.
   *
   * @param campus the campus
   */
  public void setCampus(String campus) {
    this.campus = campus;
  }

  /**
   * Gets keywords.
   *
   * @return the keywords
   */
  public String getKeywords() {
    return keywords;
  }

  /**
   * Sets keywords.
   *
   * @param keywords the keywords
   */
  public void setKeywords(String keywords) {
    this.keywords = keywords;
  }

  /**
   * Gets raw longitude.
   *
   * @return the raw longitude
   */
  public String getRawLongitude() {
    return rawLongitude;
  }

  /**
   * Sets raw longitude.
   *
   * @param rawLongitude the raw longitude
   */
  public void setRawLongitude(String rawLongitude) {
    this.rawLongitude = rawLongitude;
  }

  /**
   * Gets raw latitude.
   *
   * @return the raw latitude
   */
  public String getRawLatitude() {
    return rawLatitude;
  }

  /**
   * Sets raw latitude.
   *
   * @param rawLatitude the raw latitude
   */
  public void setRawLatitude(String rawLatitude) {
    this.rawLatitude = rawLatitude;
  }


  /**
   * Gets m longitude.
   *
   * @return the m longitude
   */
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

  /**
   * Sets m longitude.
   *
   * @param mLongitude the m longitude
   */
  public void setMLongitude(Double mLongitude) {
    this.mLongitude = mLongitude;
  }


  /**
   * Gets m latitude.
   *
   * @return the m latitude
   */
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

  /**
   * Sets m latitude.
   *
   * @param mLatitude the m latitude
   */
  public void setMLatitude(Double mLatitude) {
    this.mLatitude = mLatitude;
  }
}
