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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.GeoApiContext;
import com.google.maps.android.SphericalUtil;
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

/**
 * The main controller for the application.
 */
public class Main2Activity extends AppCompatActivity implements SearchFragListener,
    MapsFragment.MapsFragmentListener,
    InfoPopupFrag.InfoPopupFragListener, MainMenuFragListener, SearchFragAdapterListener,
    OnMapReadyCallback{

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
  private static final String SHUTTLES_API = "unmshuttles.json";
  private static final int UPDATE_INTERVAL_MS = 10000;
  private static final int FASTEST_INTERVAL_MS = 5000;
  private static boolean SHOULD_FILL_DB_W_TEST = false;
  private static int NON_SEARCH_TYPES = 2;
  private static int TOTAL_TYPES = TokenType.values().length - NON_SEARCH_TYPES;

  private FragmentManager fragmentManager;
  private FrameLayout fragContainer;
  private FrameLayout mapFragContainer;
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
  private GoogleMap myMap;
  private GeoApiContext mGeoApiContext;
  private List<String> serviceEndPoints;
  private HashMap<String, TokenType> serviceTypeMap;
  private int retries;
  private List<Marker> mapMarkers = new LinkedList<>();
  private boolean isMainFrag = true;
  private List<Token> filteredList;
  private int retriesAllowed;
  private int boundsToShowRVItems;

  /**
   * Initializes {@link android.arch.persistence.room.Database}, initializes {@link View}, initializes data, and initializes location callback.
   *
   * @param savedInstanceState contains information from a previous invocation of the class.
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main_2);
    initDB();
    initViews();
    initData();
    initLoc();
    initMapFrag();
  }

  private void initMapFrag() {
    goToMapFrag();
  }

  /**
   * Checks to make sure permission is granted for location information and GPS information.
   * If permissions are needed, begins the process to acquire permissions.
   * If permissions are granted, starts location updates.
   */
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
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.toolbar_menu, menu);
    return true;
  }

  /**
   * Switches on {@link Toolbar} {@link MenuItem} that has been pressed.  There is just a single potential selection, which sends the user to the Main Menu.
   * @param item the item that the user selected.
   * @return boolean true if this method handled the item selected event.
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_home:
        isMainFrag = true;
        swapFrags(new MainMenuFragment());
        break;
      default:
        return super.onOptionsItemSelected(item);
    }
    return true;
  }

  /**
   * Initializes the {@link android.arch.persistence.room.Database}.
   */
  @Override
  protected void onStart() {
    super.onStart();
    initDB();
  }

  /**
   * Removes reference to the {@link android.arch.persistence.room.Database}.
   */
  @Override
  protected void onStop() {
    database = null;
    CampusInfoDB.forgetInstance();
    super.onStop();

  }

  /**
   * Stops location updates.
   */
  @Override
  protected void onPause() {
    super.onPause();
    stopLocationUpdates();
  }

//  private void toggleMapContainer(){
//    if(mapFragContainer.getVisibility()==View.VISIBLE){
//      mapFragContainer.setVisibility(View.GONE);
//    }else{
//      mapFragContainer.setVisibility(View.VISIBLE);
//    }
//  }

  private void stopLocationUpdates() {
    fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
  }

  private void initDB() {
    database = CampusInfoDB.getInstance(this);
  }

  private void initViews() {
    callingViewId = R.id.building;
    fragContainer = findViewById(R.id.frag_container_2);
    mapFragContainer = findViewById(R.id.frag_container_2b);
    toolbar = findViewById(R.id.toolbar_main_2);
    toolbar.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        isMainFrag = true;
        swapFrags(new MainMenuFragment());
      }
    });
  }

  private void initLoc() {
    createLocationRequest();
    mLocationCallback = new LocationCallback() {
      @Override
      public void onLocationResult(LocationResult locationResult) {
        if (locationResult == null) {
          return;
        }
        mCurrentLocation = locationResult.getLastLocation();
//        Toast.makeText(getBaseContext(), "Lat: " + mCurrentLocation.getLatitude() + "     Long: " + mCurrentLocation.getLongitude(), Toast.LENGTH_LONG).show();
        setTokenDistances();
        sortDBTokens();
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

  /**
   * Creates a {@link LocationRequest} object that is stored in a field variable.
   */
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
    builder.setMessage(R.string.requires_gps)
        .setCancelable(false)
        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
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

  /**
   * Determines whether GPS is enabled on the device.
   *
   * @return boolean true if GPS is enabled on the device.
   */
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

  /**
   * Determines whether Google Play Services can be used on the device.
   *
   * @return boolean true if the user is clear to make map requests.
   */
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

  /**
   * Handles the result from a permission request for user location.
   * @param requestCode used to ensure it was a request that can be handled by this method.
   * @param permissions used by super.
   * @param grantResults representation of user grants of permission.
   */
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

  /**
   *  Callback from activity asking for user to enable GPS permission.  If permission is granted,
   *  then starts user location updates.
   * @param requestCode Used to ensure that this method can process the data.
   * @param resultCode Used by the call to super.
   * @param data Used by the call to super.
   */
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
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

  private void fillDBwithAPI(final String endPoint) {
      Service service = retrofit.create(Service.class);
      Call<List<Token>> call = service.get(endPoint);
      call.enqueue(new Callback<List<Token>>() {
        @Override
        public void onResponse(@NonNull Call<List<Token>> call,
            @NonNull Response<List<Token>> response) {
          if (!response.isSuccessful()) {
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
    retriesAllowed = 11;
    if(retries% retriesAllowed ==0){
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

  private void fillDBwithTest() {
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

  /**
   * Swaps the passed {@link Fragment} into the fragment container, to bring it into view. Handles
   * changing the visibility of the container for the map.  Handles changing the title according to
   * the state of the app.
   *
   * @param fragIn the {@link Fragment} that will be put into the fragment container.
   */
  protected void swapFrags(Fragment fragIn) {
    if (fragIn == null) {
      return;
    }
      fragmentManager.beginTransaction()
          .replace(fragContainer.getId(), fragIn)
          .commit();
    if(!isMainFrag){
      mapFragContainer.setVisibility(View.VISIBLE);
      setTitle(R.string.return_to_main_menu);
    }else{
      mapFragContainer.setVisibility(View.GONE);
      setTitle(R.string.app_name);
    }
  }

  /**
   * Swaps the {@link Fragment} parameter into the fragment container, to bring it into view. Uses
   * the int parameter to set a new value for callingViewId, to keep track of which {@link View} the
   * user chose to use.
   *
   * @param fragIn the {@link Fragment} that will be put into the fragment container.
   * @param callingViewId a reference to the {@link View} the user clicked on.
   */
  protected void swapFrags(Fragment fragIn, int callingViewId) {
    this.callingViewId = callingViewId;
    if (fragIn == null) {
      return;
    }
    swapFrags(fragIn);
  }

  /**
   * Getter for callingViewId, which is the {@link View} representation of which {@link TokenType}
   * the user wants.
   *
   * @return the {@link View} that the user clicked on.
   */
  public int getCallingViewId() {
    return callingViewId;
  }

  /**
   * Getter for targetItem, which is the {@link Token} the user has requested more information for.
   * @return {@link Token} the user has most recently selected.
   */
  @Override
  public Token getTargetItem() {
    return targetItem;
  }

  /**
   * Setter for targetItem, which is the {@link Token} the user has requested more information for.
   *
   * @param theNewTarget {@link Token} the user has most recently selected.
   */
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


  /**
   * Method to sort the private field dbTokens using {@link Collections} method to sort.
   */
  protected void sortDBTokens() {
    dbTokens.sort(new Comparator<Token>() {
      @Override
      public int compare(Token o1, Token o2) {
        return (Double.compare(o1.getDistance(), o2.getDistance()));
      }
    });
  }

  /**
   * Sets a reference to the most recently called {@link InfoPopupFrag} object.
   * @param infoFrag a reference to a {@link InfoPopupFrag} object.
   */
  @Override
  public void setParentRefToInfoFrag(InfoPopupFrag infoFrag) {
    this.infoPopupFrag = infoFrag;
  }

  /**
   * Sets a reference to the most recently called {@link MapsFragment}.
   * @param mapsFrag a reference to a {@link MapsFragment}.
   */
  @Override
  public void setMainRefMapsFrag(MapsFragment mapsFrag) {
    this.mapsFragment = mapsFrag;

  }

  /**
   * Calls getMapAsync() on the {@link SupportMapFragment} parameter
   * @param supportMapFragment the object that getMapAsync() will be called on.
   */
  @Override
  public void callMapAsync(SupportMapFragment supportMapFragment) {
    supportMapFragment.getMapAsync(Main2Activity.this);
  }

  /**
   * Getter for dbTokens.
   * @return {@link List} of {@link Token} objects.
   */
  @Override
  public List<Token> getTokensList() {
    return dbTokens;
  }

  @Override
  public void onSearchFiltered() {
    beginFilteredMarkerUpdate();
  }

  @Override
  public void updateFilteredList(List<Token> filteredList) {
    this.filteredList = filteredList;
  }

  /**
   * Method to swap to a {@link SearchFragment}.  Uses the integer parameter to query the {@link android.arch.persistence.room.Database} for
   * the correct {@link List} of {@link Token} that will be passed to a {@link android.support.v7.widget.RecyclerView} in {@link SearchFragment}.
   * @param iD a reference to the {@link View} the user clicked on.
   */
  public void goToSearchFrag(int iD) {
    searchFragment = getSearchFrag();
    setRVList(iD);
    isMainFrag = false;
    swapFrags(searchFragment, iD);
  }

  /**
   * Method to swap to a {@link MapsFragment}. Uses the {@link Token} parameter to add a {@link Marker} to the {@link GoogleMap}.
   *
   */
  @Override
  public void goToMapFrag() {
    MapsFragment mapsFragment = new MapsFragment();
    fragmentManager.beginTransaction()
        .replace(mapFragContainer.getId(), mapsFragment)
        .commit();
  }

  /**
   * Populates a {@link List} of {@link Token} objects that represent the items that are visible to the user in the {@link android.support.v7.widget.RecyclerView}.
   *
   * @param position the position of the most recently bound item in the {@link android.support.v7.widget.RecyclerView}.
   */
  @Override
  public void beginMarkerUpdate(int position){
    int first = 0;
    boundsToShowRVItems = 2;
    if(position> boundsToShowRVItems){
      first = position - boundsToShowRVItems;
    }
    List<Token> visibles = new LinkedList<>();
    List<Token> fullList = filteredList;
    int last = fullList.size() - 1;
    if(position < last - boundsToShowRVItems){
      last = position + boundsToShowRVItems;
    }
    for (int i = first; i < last; i++) {
      visibles.add(fullList.get(i));
    }

    if (visibles.size()>0) {
      updateMapMarkers(visibles);
    }
  }

  /**
   * Marks the map at the appropriate location for the user's selection.
   * @param single the {@link Token} to center the map on
   */
  @Override
  public void beginSingleMarkerUpdate(Token single){
    List<Token> visibles = new LinkedList<>();
    visibles.add(single);
    if(visibles.size()>0){
      updateMapMarkers(visibles);
    }
  }

  private void beginFilteredMarkerUpdate(){
    List<Token> visibles = new LinkedList<>();
    if (filteredList.size()>0) {
      visibles.add(filteredList.get(0));
      updateMapMarkers(visibles);
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
      searchFragment.redrawListInAdapter();
    }

  }

  private void setTokenDistances() {
    for (Token token :
        dbTokens) {
      Location tempLoc = mCurrentLocation;
      LatLng myLoc = new LatLng(tempLoc.getLatitude(), tempLoc.getLongitude());
      LatLng unmToken = new LatLng(token.getMLatitude(), token.getMLongitude());
      token.setDistance((int) SphericalUtil.computeDistanceBetween(unmToken, myLoc));
    }
  }

  /**
   * Method used by {@link GoogleMap} to supply a map object.  {@link Marker} and user location is set
   * onto the map.
   * @param googleMap a map object.
   */
  @Override
  public void onMapReady(GoogleMap googleMap) {
    if(mGeoApiContext == null){
      mGeoApiContext = new GeoApiContext.Builder().apiKey(getString(R.string.google_maps_key)).build();
    }
    myMap = googleMap;
    if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      return;
    }
    myMap.setMyLocationEnabled(true);
  }

private void updateMapMarkers(List<Token> visible){
  for (Marker marker :
      mapMarkers) {
    marker.remove();
  }
  mapMarkers.clear();
  for (Token token :
      visible) {
    LatLng tempLatLng = new LatLng(token.getMLatitude(), token.getMLongitude());
    mapMarkers.add(myMap.addMarker(new MarkerOptions()
        .position(tempLatLng)
        .title(token.getTitle())
        ));
  }

  if (visible.size()>0) {
    myMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(visible.get(0).getMLatitude(), visible.get(0).getMLongitude())));
  }
  myMap.animateCamera(CameraUpdateFactory.zoomTo(18.0f));
}

  /**
   * Getter for user's current {@link Location}
   * @return current {@link Location}
   */
  @Override
  public Location getmCurrentLocation() {
    return mCurrentLocation;
  }

}
