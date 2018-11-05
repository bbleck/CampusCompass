package com.brianbleck.campuscompass.controller;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.brianbleck.campuscompass.R;
import com.brianbleck.campuscompass.model.db.CampusInfoDB;
import com.brianbleck.campuscompass.model.entity.Token;
import com.brianbleck.campuscompass.model.utility.TokenPrepper;
import com.brianbleck.campuscompass.model.utility.TokenType;
import com.brianbleck.campuscompass.view.InfoPopupFrag;
import com.brianbleck.campuscompass.view.MainMenuFragment;
import com.brianbleck.campuscompass.view.MapsFragment;
import com.brianbleck.campuscompass.view.SearchFragment;
import com.brianbleck.campuscompass.view.SearchFragment.SearchFragListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = "MainActivity";

  public static final int MAIN_MENU_FRAG_PAGER_NUMBER = 0;
  public static final int MAPS_FRAG_PAGER_NUMBER = 1;
  public static final int SEARCH_FRAG_PAGER_NUMBER = 2;
  public static final int INFO_POPUP_FRAG = 3;
  private static boolean SHOULD_FILL_DB_W_TEST = true;
  private static int NON_SEARCH_TYPES = 2;
  private static int TOTAL_TYPES = TokenType.values().length - NON_SEARCH_TYPES;
  private Random rng;
  private SectionsStatePagerAdapter mSSPagerAdapter;
  private ViewPager mViewPager;
  private int callingViewId;
  private FragmentManager fragmentManager;
  private Toolbar toolbar;
  private Token targetItem;
  private InfoPopupFrag infoPopupFrag;
  private MapsFragment mapsFragment;
  private List<Token> dbTokens;
  private SearchFragment searchFragment;
  private CampusInfoDB database;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    database = CampusInfoDB.getInstance(this);
    initViews();
    initFields();
    initData();
  }

  private void initViews() {
    toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    toolbar.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        setmViewPager(MainActivity.MAIN_MENU_FRAG_PAGER_NUMBER);
      }
    });
  }

  private void initData() {
    dbTokens = new LinkedList<>();
    fragmentManager = getSupportFragmentManager();
    targetItem = TokenPrepper.prep(this, new Token());
    if (SHOULD_FILL_DB_W_TEST) {
      fillDBwithTest();
    } else {
      fillDBwithAPI();
    }
  }

  private void initFields() {
    mSSPagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
    mViewPager = findViewById(R.id.container);
    setupPager(mViewPager);
  }

  private void setupPager(ViewPager pager) {
    SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
    adapter.addFragment(new MainMenuFragment(), "MainMenuFragment");
    adapter.addFragment(new MapsFragment(), "MapsFragment");
    adapter.addFragment(getSearchFrag(), "SearchFragment");
    adapter.addFragment(new InfoPopupFrag(), "InfoPopupFragment");
    pager.setAdapter(adapter);
  }

  private Fragment getSearchFrag() {
    searchFragment = new SearchFragment();
    return searchFragment;
  }

  public void setmViewPager(int fragmentNumber, int id) {
    callingViewId = id;
    setRVList(callingViewId);
    setmViewPager(fragmentNumber);
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

  public void setmViewPager(int fragmentNumber) {
    mViewPager.setCurrentItem(fragmentNumber);
  }

  public void setmViewPager(int fragmentNumber, Token item) throws CloneNotSupportedException {
    targetItem = (Token) item.clone();
    setmViewPager(fragmentNumber);
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

  public static String calcDistance(double tokenLongitude, double tokenLatitude) {
    double distance = 0.0;
    //todo: calculate distance
    return Double.toString(distance);
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
        setmViewPager(MainActivity.MAIN_MENU_FRAG_PAGER_NUMBER);
        break;
      default:
        return super.onOptionsItemSelected(item);
    }
    return true;
  }

//  @Override
//  public void setParentRefToInfoFrag(InfoPopupFrag infoFrag) {
//    this.infoPopupFrag = infoFrag;
//  }
//
//  @Override
//  public void goToSearchFrag(int iD) {
//
//  }
//
//  @Override
//  public void setMainRefMapsFrag(MapsFragment mapsFrag) {
//    this.mapsFragment = mapsFrag;
//
//  }

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

//  @Override
//  public List<Token> getTokensList() {
//    return dbTokens;
//  }
//
//  @Override
//  public boolean isTestData(){
//    return SHOULD_FILL_DB_W_TEST;
//  }

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

//  private class DeleteTask extends AsyncTask<Pick, Void, Integer> {
//
//    private int position;
//
//    public DeleteTask(int position) {
//      this.position = position;
//    }
//
//    @Override
//    protected void onPostExecute(Integer rowsAffected) {
//      if (position < 0) {
//        picks.clear();
//        adapter.notifyDataSetChanged();
//        Toast.makeText(MainActivity.this,
//            getString(R.string.clear_all_format, rowsAffected), Toast.LENGTH_LONG).show();
//      } else {
//        picks.remove(position);
//        adapter.notifyItemRemoved(position);
//        adapter.notifyItemRangeChanged(position, picks.size() - position);
//      }
//    }
//
//    @Override
//    protected Integer doInBackground(Pick... picks) {
//      if (picks.length == 0) {
//        return database.getPickDao().nuke();
//      } else {
//        return database.getPickDao().delete(picks[0]);
//      }
//    }
//
//  }

  @Override
  protected void onStart() {
    super.onStart();
    database = CampusInfoDB.getInstance(this);
//    new QueryTask().execute();
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
}
