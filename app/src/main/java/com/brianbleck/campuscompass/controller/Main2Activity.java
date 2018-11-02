package com.brianbleck.campuscompass.controller;


import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import com.brianbleck.campuscompass.R;
import com.brianbleck.campuscompass.model.db.CampusInfoDB;
import com.brianbleck.campuscompass.model.entity.Token;
import com.brianbleck.campuscompass.model.utility.TokenPrepper;
import com.brianbleck.campuscompass.model.utility.TokenType;
import com.brianbleck.campuscompass.view.InfoPopupFrag;
import com.brianbleck.campuscompass.view.MainMenuFragment;
import com.brianbleck.campuscompass.view.MainMenuFragment.MainMenuFragListener;
import com.brianbleck.campuscompass.view.MapsFragment;
import com.brianbleck.campuscompass.view.SearchFragment;
import com.brianbleck.campuscompass.view.SearchFragment.SearchFragListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Main2Activity extends AppCompatActivity implements SearchFragListener, MapsFragment.MapsFragmentListener,
    InfoPopupFrag.InfoPopupFragListener, MainMenuFragListener {

  private static final String TAG = "Main2Activity";

  private FragmentManager fragmentManager;
  private FrameLayout fragContainer;
  private Toolbar toolbar;
  private static boolean SHOULD_FILL_DB_W_TEST = true;
  private static int NON_SEARCH_TYPES = 2;
  private static int TOTAL_TYPES = TokenType.values().length - NON_SEARCH_TYPES;
  private Random rng;
  private int callingViewId;
  private Token targetItem;
  private InfoPopupFrag infoPopupFrag;
  private MapsFragment mapsFragment;
  private List<Token> dbTokens;
  private SearchFragment searchFragment;
  private CampusInfoDB database;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main_2);
    initViews();
    initFields();
    initData();
  }

  private void initData() {
    dbTokens = new LinkedList<>();
    fragmentManager = getSupportFragmentManager();
    callingViewId = R.id.building;
    targetItem = TokenPrepper.prep(this, new Token());
    if (SHOULD_FILL_DB_W_TEST) {
      fillDBwithTest();
    } else {
      fillDBwithAPI();
    }
  }

  private void initDB() {
    database = CampusInfoDB.getInstance(this);
  }

  private void initViews() {
    fragContainer = findViewById(R.id.frag_container_2);
    toolbar = findViewById(R.id.toolbar_main_2);
    setSupportActionBar(toolbar);
    toolbar.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        swapFrags(new MainMenuFragment());
      }
    });
  }
  public void fillDBwithAPI() {
    //todo: pull from db, prep data, and populate db
  }

  public void fillDBwithTest() {
    rng = new Random();
    List<Token> prepopulateList = new LinkedList<>();
    TokenType tempType = TokenType.BUILDING;
    for (int i = 0; i < TOTAL_TYPES; i++) {
      tempType = TokenType.values()[i];
      for (int j = 0; j < rng.nextInt(25)+25; j++) {
        Token tempToken = new Token();
        tempToken.setTokenType(tempType);
        prepopulateList.add(TokenPrepper.prep(getApplicationContext(), tempToken));
      }
    }
    Token[] tokenArr = prepopulateList.toArray(new Token[prepopulateList.size()]);
    new AddTask().execute(tokenArr);
  }

  private void initFields() {
    fragmentManager = getSupportFragmentManager();
  }

  protected void swapFrags(Fragment fragIn){
    if(fragIn==null){
      Log.d(TAG, "swapFrags: null fragment");
      return;
    }
    fragmentManager.beginTransaction()
        .replace(fragContainer.getId(), fragIn)
        .commit();
  }

  protected void swapFrags(Fragment fragIn, int callingViewId){
    this.callingViewId = callingViewId;
    if(fragIn==null){
      Log.d(TAG, "swapFrags: null fragment");
      return;
    }
    fragmentManager.beginTransaction()
        .replace(fragContainer.getId(), fragIn)
        .commit();
  }

  public int getCallingViewId() {
    return callingViewId;
  }

  public Token getTargetItem() {
    return targetItem;
  }

  public void setTargetItem(Token theNewTarget) {
    this.targetItem = null;
    this.targetItem = theNewTarget;
    if (infoPopupFrag != null) {
      infoPopupFrag.initData();
    }
  }

  private Fragment getSearchFrag() {
    searchFragment = new SearchFragment();
    return searchFragment;
  }

  public static String calcDistance(double tokenLongitude, double tokenLatitude) {
    double distance = 0.0;
    //todo: calculate distance
    return Double.toString(distance);
  }

  private void setRVList(int callingViewId) {
    switch(callingViewId){
      case R.id.iv_main_frag_0:
        new QueryTask().execute(TokenType.BUILDING);
        break;
      case R.id.iv_main_frag_1:
        new QueryTask().execute(TokenType.RESTROOM);
        break;
      case R.id.iv_main_frag_2:
        new QueryTask().execute(TokenType.BLUE_PHONE);
        break;
      case R.id.iv_main_frag_3:
        new QueryTask().execute(TokenType.COMPUTER_POD);
        break;
      case R.id.iv_main_frag_4:
        new QueryTask().execute(TokenType.HEALTHY_VENDING);
        break;
      case R.id.iv_main_frag_5:
        new QueryTask().execute(TokenType.DINING);
        break;
      case R.id.iv_main_frag_6:
        new QueryTask().execute(TokenType.LIBRARY);
        break;
      case R.id.iv_main_frag_7:
        new QueryTask().execute(TokenType.METERED_PARKING);
        break;
      case R.id.iv_main_frag_8:
        new QueryTask().execute(TokenType.SHUTTLE_STOP);
        break;
      default:
        Log.d(TAG, "setSearchTitle: unknown callingId error");
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.toolbar_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_home:
        swapFrags(new MainMenuFragment());
        break;
      default:
        return super.onOptionsItemSelected(item);
    }
    return true;
  }

  @Override
  protected void onStart() {
    super.onStart();
    initDB();
  }

  @Override
  protected void onStop() {
    database = null;
    CampusInfoDB.forgetInstance();
    super.onStop();

  }

  protected void sortDBTokens(){
    //todo: implement this method to sort the list based on distance from user
  }

  @Override
  public void setParentRefToInfoFrag(InfoPopupFrag infoFrag) {
    this.infoPopupFrag = infoFrag;
  }

  @Override
  public void setMainRefMapsFrag(MapsFragment mapsFrag) {
    this.mapsFragment = mapsFrag;

  }

  @Override
  public List<Token> getTokensList() {
    return dbTokens;
  }

  @Override
  public boolean isTestData(){
    return SHOULD_FILL_DB_W_TEST;
  }

  @Override
  public void goToSearchFrag(int iD) {
    swapFrags(new SearchFragment(), iD);
  }

  private class AddTask extends AsyncTask<Token, Void, Void> {

    @Override
    protected Void doInBackground(Token... tokens) {
      database.getTokenDao().nuke();
      List<Token> tokensList = new LinkedList<>();
      for (int i = 0; i < tokens.length; i++) {
        tokensList.add(tokens[i]);
      }
      database.getTokenDao().insert(tokensList);
      return null;
    }

  }

  private class QueryTask extends AsyncTask<TokenType, Void, List<Token>> {

    @Override
    protected List<Token> doInBackground(TokenType... types) {
      return database.getTokenDao().select(types[0]);
    }
    @Override
    protected void onPostExecute(List<Token> tokens) {
      dbTokens.clear();
      dbTokens.addAll(tokens);
      sortDBTokens();
      searchFragment.updateListInAdapter();
    }

  }

}
