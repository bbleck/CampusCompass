package com.brianbleck.campuscompass.controller;

import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.brianbleck.campuscompass.R;
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

public class MainActivity extends AppCompatActivity implements SearchFragListener, MapsFragment.MapsFragmentListener,
    InfoPopupFrag.InfoPopupFragListener {

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


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
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
    fragmentManager = getSupportFragmentManager();
//        targetItem = Token.createTestToken(this);//todo: set a default value for targetItem
    if (SHOULD_FILL_DB_W_TEST) {
      dbTokens = fillDBwithTest();
    } else {
      dbTokens = fillDBwithAPI();
    }
  }

  private void initFields() {
    mSSPagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
    mViewPager = (ViewPager) findViewById(R.id.container);
    setupPager(mViewPager);
  }

  private void setupPager(ViewPager pager) {
    SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
    adapter.addFragment(new MainMenuFragment(), "MainMenuFragment");
    adapter.addFragment(new MapsFragment(), "MapsFragment");
    adapter.addFragment(new SearchFragment(), "SearchFragment");
    adapter.addFragment(new InfoPopupFrag(), "InfoPopupFragment");
    pager.setAdapter(adapter);
  }

  public void setmViewPager(int fragmentNumber, int id) {
    callingViewId = id;
    setmViewPager(fragmentNumber);
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

  @Override
  public void setParentRefToInfoFrag(InfoPopupFrag infoFrag) {
    this.infoPopupFrag = infoFrag;
  }

  @Override
  public void setMainRefMapsFrag(MapsFragment mapsFrag) {
    this.mapsFragment = mapsFrag;

  }

  public List<Token> fillDBwithAPI() {
    return new LinkedList<>();
  }

  public List<Token> fillDBwithTest() {
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
    return prepopulateList;
  }

  @Override
  public List<Token> getTokensList() {
    return dbTokens;
  }

  @Override
  public boolean isTestData(){
    return SHOULD_FILL_DB_W_TEST;
  }

}
