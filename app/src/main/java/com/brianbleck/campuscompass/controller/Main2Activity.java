package com.brianbleck.campuscompass.controller;


import static com.brianbleck.campuscompass.Constants.ERROR_DIALOG_REQUEST;
import static com.brianbleck.campuscompass.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.brianbleck.campuscompass.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

import android.Manifest.permission;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.brianbleck.campuscompass.R;
import com.brianbleck.campuscompass.model.api.Service;
import com.brianbleck.campuscompass.model.db.CampusInfoDB;
import com.brianbleck.campuscompass.model.entity.Token;
import com.brianbleck.campuscompass.model.utility.TokenPrepper;
import com.brianbleck.campuscompass.model.utility.TokenType;
import com.brianbleck.campuscompass.view.InfoPopupFrag;
import com.brianbleck.campuscompass.view.MainMenuFragment;
import com.brianbleck.campuscompass.view.MainMenuFragment.MainMenuFragListener;
import com.brianbleck.campuscompass.view.MapsFragment;
import com.brianbleck.campuscompass.view.SearchFragAdapter.SearchFragAdapterListener;
import com.brianbleck.campuscompass.view.SearchFragment;
import com.brianbleck.campuscompass.view.SearchFragment.SearchFragListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.android.SphericalUtil;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.gson.GsonConverterFactory;

public class Main2Activity extends AppCompatActivity implements SearchFragListener,
    MapsFragment.MapsFragmentListener,
    InfoPopupFrag.InfoPopupFragListener, MainMenuFragListener, SearchFragAdapterListener,
    OnMapReadyCallback {

  private static final String TAG = "Main2Activity";

  private static final String BLUE_PHONE_API = "bluephonesnorth.json";
  private static final String BLUE_PHONE_SOUTH_API = "bluephonessouth.json";
  private static final String BUILDING_API = "abqbuildings.json";
  private static final String COMPUTER_POD_API = "computerpods.json";
  private static final String DINING_API = "dining.json";
  private static final String HEALTHY_VENDING_API = "healthyvending.json";
  private static final String LIBRARIES_API = "libraries.json";
  private static final String PARKING_API = "meteredparking.json";
  private static final String RESTROOMS_API = "restrooms.json";
  private static final String SHUTTLES_API = "nmshuttles.json";
  private static final int UPDATE_INTERVAL_MS = 10000;
  private static final int FASTEST_INTERVAL_MS = 5000;
  private static boolean SHOULD_FILL_DB_W_TEST = false;
  private static int NON_SEARCH_TYPES = 2;
  private static int TOTAL_TYPES = TokenType.values().length - NON_SEARCH_TYPES;

  private FragmentManager fragmentManager;
  private FrameLayout fragContainer;
  private Toolbar toolbar;
  private int callingViewId;
  private Token targetItem;
  private InfoPopupFrag infoPopupFrag;
  private MapsFragment mapsFragment;
  private List<Token> dbTokens;
  private SearchFragment searchFragment;
  private CampusInfoDB database;
  private Retrofit retrofit;
  private boolean mLocationPermissionGranted = false;
  private FusedLocationProviderClient fusedLocationProviderClient;
  private LocationRequest mLocationRequest;
  private LocationCallback mLocationCallback;
  private Location mCurrentLocation;
  private LatLng mGoToLocation;
  private String mGoToLocationTitle;
  private GoogleMap myMap;
  private LatLngBounds myMapBounds;
  private GeoApiContext mGeoApiContext;
  private List<String> serviceEndPoints;
  private HashMap<String, TokenType> serviceTypeMap;
  private int retries;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main_2);
    initDB();
    initViews();
    initData();
    initFields();
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (checkMapServices()) {
      if (mLocationPermissionGranted) {
        startLocationUpdates();
      } else {
        getLocationPermission();
      }
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    stopLocationUpdates();
  }

  private void stopLocationUpdates() {
    fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
  }

  private void initDB() {
    database = CampusInfoDB.getInstance(this);
  }

  private void initViews() {
    callingViewId = R.id.building;
    fragContainer = findViewById(R.id.frag_container_2);
    toolbar = findViewById(R.id.toolbar_main_2);
    toolbar.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        swapFrags(new MainMenuFragment());
      }
    });
  }

  private void initFields() {
    createLocationRequest();
    mLocationCallback = new LocationCallback() {
      @Override
      public void onLocationResult(LocationResult locationResult) {
        if (locationResult == null) {
          return;
        }
        mCurrentLocation = locationResult.getLastLocation();
        setTokenDistances();
        sortDBTokens();
        if (searchFragment != null) {
          if (searchFragment.getAdapter() != null) {
            searchFragment.updateListInAdapter();
          }
        }
      }
    };
  }

  private void initServices(){
    serviceEndPoints.add(BLUE_PHONE_API);
    serviceTypeMap.put(BLUE_PHONE_API, TokenType.BLUE_PHONE);

    serviceEndPoints.add(BLUE_PHONE_SOUTH_API);
    serviceTypeMap.put(BLUE_PHONE_SOUTH_API, TokenType.BLUE_PHONE);

    serviceEndPoints.add(BUILDING_API);
    serviceTypeMap.put(BUILDING_API, TokenType.BUILDING);

    serviceEndPoints.add(COMPUTER_POD_API);
    serviceTypeMap.put(COMPUTER_POD_API, TokenType.COMPUTER_POD);

    serviceEndPoints.add(DINING_API);
    serviceTypeMap.put(DINING_API, TokenType.DINING);

    serviceEndPoints.add(HEALTHY_VENDING_API);
    serviceTypeMap.put(HEALTHY_VENDING_API, TokenType.HEALTHY_VENDING);

    serviceEndPoints.add(LIBRARIES_API);
    serviceTypeMap.put(LIBRARIES_API, TokenType.LIBRARY);

    serviceEndPoints.add(PARKING_API);
    serviceTypeMap.put(PARKING_API, TokenType.METERED_PARKING);

    serviceEndPoints.add(RESTROOMS_API);
    serviceTypeMap.put(RESTROOMS_API, TokenType.RESTROOM);

    serviceEndPoints.add(SHUTTLES_API);
    serviceTypeMap.put(SHUTTLES_API, TokenType.SHUTTLE_STOP);


    retrofit = new Builder()
        .baseUrl(getString(R.string.unm_base_url))
        .addConverterFactory(GsonConverterFactory.create())
        .build();

  }

  private void initData() {
    dbTokens = new LinkedList<>();
    serviceEndPoints = new LinkedList<>();
    serviceTypeMap = new HashMap<>();
    retries = 0;
    targetItem = TokenPrepper.prep(this, new Token());
    fragmentManager = getSupportFragmentManager();
    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    getLastKnownLocation();
    setSupportActionBar(toolbar);
    initServices();
    if (SHOULD_FILL_DB_W_TEST) {
      fillDBwithTest();
    } else {
      for (String endPoint :
          serviceEndPoints) {
        fillDBwithAPI(endPoint);
      }
    }
    swapFrags(new MainMenuFragment());
  }

  private void startLocationUpdates() {
    if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      return;
    }
    fusedLocationProviderClient.requestLocationUpdates(mLocationRequest,
        mLocationCallback,
        null /* Looper */);
  }

  protected void createLocationRequest() {
    mLocationRequest = new LocationRequest();
    mLocationRequest.setInterval(UPDATE_INTERVAL_MS);
    mLocationRequest.setFastestInterval(FASTEST_INTERVAL_MS);
    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
  }

  private void getLastKnownLocation() {
    if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      return;
    }
    fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this,
        new OnSuccessListener<Location>() {
          @Override
          public void onSuccess(Location location) {
            if (location != null) {
              mCurrentLocation = location;
            }
          }
        });
  }

  private boolean checkMapServices() {
    if (isServicesOK()) {
      return isMapsEnabled();
    }
    return false;
  }

  private void buildAlertMessageNoGps() {//prompts user with dialog to enable gps
    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
        .setCancelable(false)
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
          public void onClick(@SuppressWarnings("unused") final DialogInterface dialog,
              @SuppressWarnings("unused") final int id) {
            Intent enableGpsIntent = new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
          }
        });
    final AlertDialog alert = builder.create();
    alert.show();
  }

  public boolean isMapsEnabled() {//determines whether gps is enabled on the device
    final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
      buildAlertMessageNoGps();
      return false;
    }
    return true;
  }

  private void getLocationPermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
    if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
        android.Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED) {
      mLocationPermissionGranted = true;
      startLocationUpdates();
    } else {
      ActivityCompat.requestPermissions(this,
          new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
          PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }
  }

  public boolean isServicesOK() {//this process determines whether google play services can be used on device
    Log.d(TAG, getString(R.string.check_google_svc_version));

    int available = GoogleApiAvailability.getInstance()
        .isGooglePlayServicesAvailable(Main2Activity.this);

    if (available == ConnectionResult.SUCCESS) {
      //everything is fine and the user can make map requests
      Log.d(TAG, getString(R.string.google_play_svcs_is_working));
      return true;
    } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
      //an error occured but we can resolve it
      Log.d(TAG, getString(R.string.google_play_svcs_error_can_fix));
      Dialog dialog = GoogleApiAvailability.getInstance()
          .getErrorDialog(Main2Activity.this, available, ERROR_DIALOG_REQUEST);
      dialog.show();
    } else {
      Toast.makeText(this, getString(R.string.cannot_make_map_requests), Toast.LENGTH_SHORT).show();
    }
    return false;
  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
      @NonNull String permissions[],
      @NonNull int[] grantResults) {
    mLocationPermissionGranted = false;
    switch (requestCode) {
      case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          mLocationPermissionGranted = true;
        }
      }
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Log.d(TAG, "onActivityResult: called.");
    switch (requestCode) {
      case PERMISSIONS_REQUEST_ENABLE_GPS: {
        if (mLocationPermissionGranted) {
          startLocationUpdates();
        } else {
          getLocationPermission();//asking for permission to use location services
        }
      }
    }

  }

  public void fillDBwithAPI(final String endPoint) {
      Service service = retrofit.create(Service.class);
      Call<List<Token>> call = service.get(endPoint);
      call.enqueue(new Callback<List<Token>>() {
        @Override
        public void onResponse(@NonNull Call<List<Token>> call,
            @NonNull Response<List<Token>> response) {
          if (!response.isSuccessful()) {
            Log.d(TAG, "onResponse: code " + response.code());
            return;
          }
          List<Token> tokensFromApi = response.body();
          if(tokensFromApi!=null){
            for (Token token :
                tokensFromApi) {
              token.setTokenType(getServiceTokenType(endPoint));
              token = TokenPrepper.prep(Main2Activity.this, token);
            }
            Token[] tokenArr = tokensFromApi.toArray(new Token[0]);
            new AddTask().execute(tokenArr);
          }
          resetRetries();
        }

        @Override
        public void onFailure(@NonNull Call<List<Token>> call, @NonNull Throwable t) {
          Log.d(TAG, "onFailure: shuttle" + t.getMessage());
          if(shouldStopRetrying()){
            Toast.makeText(getBaseContext(), getString(R.string.could_not_load_resource) + endPoint, Toast.LENGTH_SHORT).show();
            resetRetries();
          }else{
            fillDBwithAPI(endPoint);
          }
        }
      });
  }

  private boolean shouldStopRetrying() {
    if(retries%11==0){
      retries++;
      return true;
    } else {
      retries++;
      return false;
    }
  }

  private void resetRetries(){
    retries = 1;
  }

  private TokenType getServiceTokenType(String endPoint) {
    if(serviceTypeMap.containsKey(endPoint)){
      return serviceTypeMap.get(endPoint);
    }
    return TokenType.BUILDING;
  }

  public void fillDBwithTest() {
    Random rng = new Random();
    List<Token> prepopulateList = new LinkedList<>();
    TokenType tempType = TokenType.BUILDING;
    for (int i = 0; i < TOTAL_TYPES; i++) {
      tempType = TokenType.values()[i];
      for (int j = 0; j < rng.nextInt(25) + 25; j++) {
        Token tempToken = new Token();
        tempToken.setTokenType(tempType);
        prepopulateList.add(TokenPrepper.prep(getApplicationContext(), tempToken));
      }
    }
    Token[] tokenArr = prepopulateList.toArray(new Token[0]);
    new AddTask().execute(tokenArr);
  }

  protected void swapFrags(Fragment fragIn) {
    if (fragIn == null) {
      return;
    }
    fragmentManager.beginTransaction()
        .replace(fragContainer.getId(), fragIn)
        .commit();
  }

  protected void swapFrags(Fragment fragIn, int callingViewId) {
    this.callingViewId = callingViewId;
    if (fragIn == null) {
      return;
    }
    fragmentManager.beginTransaction()
        .replace(fragContainer.getId(), fragIn)
        .commit();
    swapFrags(fragIn);
  }

  protected void swapFrags(Fragment fragIn, Token item) throws CloneNotSupportedException {
    targetItem = (Token) item.clone();
    if (fragIn == null) {
      return;
    }
    fragmentManager.beginTransaction()
        .replace(fragContainer.getId(), fragIn)
        .commit();
    swapFrags(fragIn);
  }

  public int getCallingViewId() {
    return callingViewId;
  }

  @Override
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

  private SearchFragment getSearchFrag() {
    searchFragment = new SearchFragment();
    return searchFragment;
  }

  private void setRVList(int callingViewId) {
    switch (callingViewId) {
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
        //by design, do nothing
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

  protected void sortDBTokens() {
    Collections.sort(dbTokens, new Comparator<Token>() {
      @Override
      public int compare(Token o1, Token o2) {
        return o1.getDistance()-o2.getDistance();
      }
    });
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
  public void callMapAsync(SupportMapFragment supportMapFragment) {
    supportMapFragment.getMapAsync(Main2Activity.this);

  }

  @Override
  public List<Token> getTokensList() {
    return dbTokens;
  }

  @Override
  public boolean isTestData() {
    return SHOULD_FILL_DB_W_TEST;
  }

  @Override
  public void goToSearchFrag(int iD) {
    searchFragment = getSearchFrag();
    setRVList(iD);
    swapFrags(searchFragment, iD);
  }

  @Override
  public void goToMapFrag(Token item) {
    MapsFragment mapsFragment = new MapsFragment();
    mGoToLocation = new LatLng(item.getMLatitude(), item.getMLongitude());
    mGoToLocationTitle = item.getTitle();

    try {
      swapFrags(mapsFragment, item);
    } catch (CloneNotSupportedException e) {
      //by design, do nothing.
    }
  }

  private class AddTask extends AsyncTask<Token, Void, Void> {

    @Override
    protected Void doInBackground(Token... tokens) {
      List<Token> tokensList = new LinkedList<>();
      tokensList.addAll(Arrays.asList(tokens));
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
      setTokenDistances();
      sortDBTokens();
      searchFragment.updateListInAdapter();
    }

  }

  public void setTokenDistances() {
    for (Token token :
        dbTokens) {
      Location tempLoc = mCurrentLocation;
      LatLng myLoc = new LatLng(tempLoc.getLatitude(), tempLoc.getLongitude());
      LatLng unmToken = new LatLng(token.getMLatitude(), token.getMLongitude());
      token.setDistance((int) SphericalUtil.computeDistanceBetween(unmToken, myLoc));
    }
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    if(mGeoApiContext == null){
      mGeoApiContext = new GeoApiContext.Builder().apiKey(getString(R.string.google_maps_key)).build();
    }
    myMap = googleMap;
    LatLng userLoc = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
    myMap.addMarker(new MarkerOptions()
        .position(userLoc)
        .title(getString(R.string.start_loc)));
    myMap.addMarker(new MarkerOptions()
    .position(mGoToLocation)
    .title(mGoToLocationTitle));
    calculateDirections();
    if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      return;
    }
    myMap.setMyLocationEnabled(true);
    myMap.moveCamera(CameraUpdateFactory.newLatLng(userLoc));
    myMap.animateCamera(CameraUpdateFactory.zoomTo(18.0f));
  }

  //mostly from coding with mitch
  private void calculateDirections(){
    Log.d(TAG, "calculateDirections: calculating directions.");

    com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
        mGoToLocation.latitude,
        mGoToLocation.longitude
    );
    DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);
    directions.mode(TravelMode.WALKING);
    directions.alternatives(true);
    directions.origin(
        new com.google.maps.model.LatLng(
            mCurrentLocation.getLatitude(),
            mCurrentLocation.getLongitude()
        )
    );
    directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
      @Override
      public void onResult(DirectionsResult result) {
        addPolylinesToMap(result);
      }

      @Override
      public void onFailure(Throwable e) {
        Toast.makeText(getBaseContext(), getString(R.string.google_directions_error), Toast.LENGTH_SHORT).show();
      }
    });
  }

  //from coding with mitch
  private void addPolylinesToMap(final DirectionsResult result){
    //must use Handler and Looper.getMainLooper to use main thread to make changes to google map
    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        Log.d(TAG, "run: result routes: " + result.routes.length);

        for(DirectionsRoute route: result.routes){
          Log.d(TAG, "run: leg: " + route.legs[0].toString());
          List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());
          List<LatLng> newDecodedPath = new ArrayList<>();
          // This loops through all the LatLng coordinates of ONE polyline.
          for(com.google.maps.model.LatLng latLng: decodedPath){
            newDecodedPath.add(new LatLng(
                latLng.lat,
                latLng.lng
            ));
          }
          Polyline polyline = myMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
          polyline.setColor(ContextCompat.getColor(Main2Activity.this, R.color.darkGray));
          polyline.setClickable(true);

        }
      }
    });
  }

  private void setCameraView(){

  }

}
