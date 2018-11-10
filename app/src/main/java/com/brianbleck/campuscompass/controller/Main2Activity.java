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
import com.brianbleck.campuscompass.model.api.BluePhoneApi;
import com.brianbleck.campuscompass.model.api.BluePhoneSouthApi;
import com.brianbleck.campuscompass.model.api.BuildingApi;
import com.brianbleck.campuscompass.model.api.ComputerPodApi;
import com.brianbleck.campuscompass.model.api.DiningApi;
import com.brianbleck.campuscompass.model.api.HealthyVendingApi;
import com.brianbleck.campuscompass.model.api.LibrariesApi;
import com.brianbleck.campuscompass.model.api.MeteredParkingApi;
import com.brianbleck.campuscompass.model.api.RestroomsApi;
import com.brianbleck.campuscompass.model.api.ShuttlesApi;
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
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.SphericalUtil;
import com.google.android.gms.tasks.Task;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Main2Activity extends AppCompatActivity implements SearchFragListener,
    MapsFragment.MapsFragmentListener,
    InfoPopupFrag.InfoPopupFragListener, MainMenuFragListener, SearchFragAdapterListener,
    OnMapReadyCallback {

  private static final String TAG = "Main2Activity";

  private FragmentManager fragmentManager;
  private FrameLayout fragContainer;
  private Toolbar toolbar;
  private static boolean SHOULD_FILL_DB_W_TEST = false;
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
  private Retrofit retrofit;
  private static int apiCall = 0;
  private boolean mLocationPermissionGranted = false;
  private FusedLocationProviderClient fusedLocationProviderClient;
  private LocationRequest mLocationRequest;
  private LocationCallback mLocationCallback;
  private Location mCurrentLocation;

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
    //todo: update data starting here rather than oncreate?
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
//        Toast.makeText(getBaseContext(), "onlocationresult updated", Toast.LENGTH_SHORT).show();
      }

      ;
    };
  }

  private void initData() {
    fragmentManager = getSupportFragmentManager();
    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    getLastKnownLocation();
    dbTokens = new LinkedList<>();
    setSupportActionBar(toolbar);
    targetItem = TokenPrepper.prep(this, new Token());
    if (SHOULD_FILL_DB_W_TEST) {
      fillDBwithTest();
    } else {
      fillDBwithAPI();
    }
    swapFrags(new MainMenuFragment());
  }

  private void startLocationUpdates() {
    if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      // TODO: Consider calling
      //    ActivityCompat#requestPermissions
      // here to request the missing permissions, and then overriding
      //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
      //                                          int[] grantResults)
      // to handle the case where the user grants the permission. See the documentation
      // for ActivityCompat#requestPermissions for more details.
      return;
    }
    fusedLocationProviderClient.requestLocationUpdates(mLocationRequest,
        mLocationCallback,
        null /* Looper */);
  }

  protected void createLocationRequest() {
    mLocationRequest = new LocationRequest();
    mLocationRequest.setInterval(10000);
    mLocationRequest.setFastestInterval(5000);
    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
        .addLocationRequest(mLocationRequest);
    SettingsClient client = LocationServices.getSettingsClient(this);
//    Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
  }

  private void getLastKnownLocation() {
    if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      // TODO: Consider calling
      //    ActivityCompat#requestPermissions
      // here to request the missing permissions, and then overriding
      //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
      //                                          int[] grantResults)
      // to handle the case where the user grants the permission. See the documentation
      // for ActivityCompat#requestPermissions for more details.
      return;
    }
    fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this,
        new OnSuccessListener<Location>() {
          @Override
          public void onSuccess(Location location) {
            if(location!=null){
              mCurrentLocation = location;
//              Toast.makeText(getBaseContext(), "GLKL: your location is: lat: "+mCurrentLocation.getLatitude()+" long: "+mCurrentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
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
    Log.d(TAG, "isServicesOK: checking google services version");

    int available = GoogleApiAvailability.getInstance()
        .isGooglePlayServicesAvailable(Main2Activity.this);

    if (available == ConnectionResult.SUCCESS) {
      //everything is fine and the user can make map requests
      Log.d(TAG, "isServicesOK: Google Play Services is working");
      return true;
    } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
      //an error occured but we can resolve it
      Log.d(TAG, "isServicesOK: an error occured but we can fix it");
      Dialog dialog = GoogleApiAvailability.getInstance()
          .getErrorDialog(Main2Activity.this, available, ERROR_DIALOG_REQUEST);
      dialog.show();
    } else {
      Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
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

  private void fillBluephoneData() {

    BluePhoneApi bluePhoneApi = retrofit.create(BluePhoneApi.class);
    Call<List<Token>> call = bluePhoneApi.getBluePhonesJson();
    call.enqueue(new Callback<List<Token>>() {
      @Override
      public void onResponse(Call<List<Token>> call, Response<List<Token>> response) {
        if (!response.isSuccessful()) {
          Log.d(TAG, "onResponse: code " + response.code());
          return;
        }
        List<Token> tokensFromApi = response.body();
        for (Token token :
            tokensFromApi) {
          token.setTokenType(TokenType.BLUE_PHONE);
          token = TokenPrepper.prep(Main2Activity.this, token);
        }
        Token[] tokenArr = tokensFromApi.toArray(new Token[tokensFromApi.size()]);
        new AddTask().execute(tokenArr);
      }

      @Override
      public void onFailure(Call<List<Token>> call, Throwable t) {
        Log.d(TAG, "onFailure: bluephone north: " + t.getMessage());
      }
    });
  }

  private void fillBluephoneSouthData() {

    BluePhoneSouthApi bluePhoneSouthApi = retrofit.create(BluePhoneSouthApi.class);
    Call<List<Token>> call = bluePhoneSouthApi.getBluePhonesSouthJson();
    call.enqueue(new Callback<List<Token>>() {
      @Override
      public void onResponse(Call<List<Token>> call, Response<List<Token>> response) {
        if (!response.isSuccessful()) {
          Log.d(TAG, "onResponse: code " + response.code());
          return;
        }
        List<Token> tokensFromApi = response.body();
        for (Token token :
            tokensFromApi) {
          token.setTokenType(TokenType.BLUE_PHONE);
          token = TokenPrepper.prep(Main2Activity.this, token);
        }
        Token[] tokenArr = tokensFromApi.toArray(new Token[tokensFromApi.size()]);
        new AddTask().execute(tokenArr);
      }

      @Override
      public void onFailure(Call<List<Token>> call, Throwable t) {
        Log.d(TAG, "onFailure: bluephone south: " + t.getMessage(), t);
      }
    });
  }

  private void fillBuildingData() {

    BuildingApi buildingApi = retrofit.create(BuildingApi.class);
    Call<List<Token>> call = buildingApi.getBuildingsJson();
    call.enqueue(new Callback<List<Token>>() {
      @Override
      public void onResponse(Call<List<Token>> call, Response<List<Token>> response) {
        if (!response.isSuccessful()) {
          Log.d(TAG, "onResponse: code " + response.code());
          return;
        }
        List<Token> tokensFromApi = response.body();
        for (Token token :
            tokensFromApi) {
          token.setTokenType(TokenType.BUILDING);
          token = TokenPrepper.prep(Main2Activity.this, token);
        }
        Token[] tokenArr = tokensFromApi.toArray(new Token[tokensFromApi.size()]);
        new AddTask().execute(tokenArr);
      }

      @Override
      public void onFailure(Call<List<Token>> call, Throwable t) {
        Log.d(TAG, "onFailure: building " + t.getMessage());
        fillBuildingData();
      }
    });
  }

  private void fillComputerPodData() {

    ComputerPodApi computerPodApi = retrofit.create(ComputerPodApi.class);
    Call<List<Token>> call = computerPodApi.getComputerPodsJson();
    call.enqueue(new Callback<List<Token>>() {
      @Override
      public void onResponse(Call<List<Token>> call, Response<List<Token>> response) {
        if (!response.isSuccessful()) {
          Log.d(TAG, "onResponse: code " + response.code());
          return;
        }
        List<Token> tokensFromApi = response.body();
        for (Token token :
            tokensFromApi) {
          token.setTokenType(TokenType.COMPUTER_POD);
          token = TokenPrepper.prep(Main2Activity.this, token);
        }
        Token[] tokenArr = tokensFromApi.toArray(new Token[tokensFromApi.size()]);
        new AddTask().execute(tokenArr);
      }

      @Override
      public void onFailure(Call<List<Token>> call, Throwable t) {
        Log.d(TAG, "onFailure: computer pod" + t.getMessage());
        fillComputerPodData();
      }
    });
  }

  private void fillDiningData() {

    DiningApi diningApi = retrofit.create(DiningApi.class);
    Call<List<Token>> call = diningApi.getDiningJson();
    call.enqueue(new Callback<List<Token>>() {
      @Override
      public void onResponse(Call<List<Token>> call, Response<List<Token>> response) {
        if (!response.isSuccessful()) {
          Log.d(TAG, "onResponse: code " + response.code());
          return;
        }
        List<Token> tokensFromApi = response.body();
        for (Token token :
            tokensFromApi) {
          token.setTokenType(TokenType.DINING);
          token = TokenPrepper.prep(Main2Activity.this, token);
        }
        Token[] tokenArr = tokensFromApi.toArray(new Token[tokensFromApi.size()]);
        new AddTask().execute(tokenArr);
      }

      @Override
      public void onFailure(Call<List<Token>> call, Throwable t) {
        Log.d(TAG, "onFailure: dining" + t.getMessage());
        fillDiningData();
      }
    });
  }

  private void fillHealthyVendData() {

    HealthyVendingApi healthyVendingApi = retrofit.create(HealthyVendingApi.class);
    Call<List<Token>> call = healthyVendingApi.getHealthyVendingJson();
    call.enqueue(new Callback<List<Token>>() {
      @Override
      public void onResponse(Call<List<Token>> call, Response<List<Token>> response) {
        if (!response.isSuccessful()) {
          Log.d(TAG, "onResponse: code " + response.code());
          return;
        }
        List<Token> tokensFromApi = response.body();
        for (Token token :
            tokensFromApi) {
          token.setTokenType(TokenType.HEALTHY_VENDING);
          token = TokenPrepper.prep(Main2Activity.this, token);
        }
        Token[] tokenArr = tokensFromApi.toArray(new Token[tokensFromApi.size()]);
        new AddTask().execute(tokenArr);
      }

      @Override
      public void onFailure(Call<List<Token>> call, Throwable t) {
        Log.d(TAG, "onFailure: healthy vending" + t.getMessage());
        fillHealthyVendData();
      }
    });
  }

  private void fillLibrariesData() {

    LibrariesApi librariesApi = retrofit.create(LibrariesApi.class);
    Call<List<Token>> call = librariesApi.getLibrariesJson();
    call.enqueue(new Callback<List<Token>>() {
      @Override
      public void onResponse(Call<List<Token>> call, Response<List<Token>> response) {
        if (!response.isSuccessful()) {
          Log.d(TAG, "onResponse: code " + response.code());
          return;
        }
        List<Token> tokensFromApi = response.body();
        for (Token token :
            tokensFromApi) {
          token.setTokenType(TokenType.LIBRARY);
          token = TokenPrepper.prep(Main2Activity.this, token);
        }
        Token[] tokenArr = tokensFromApi.toArray(new Token[tokensFromApi.size()]);
        new AddTask().execute(tokenArr);
      }

      @Override
      public void onFailure(Call<List<Token>> call, Throwable t) {
        Log.d(TAG, "onFailure: libraries " + t.getMessage());
        fillLibrariesData();
      }
    });
  }

  private void fillMeterParkingData() {

    MeteredParkingApi meteredParkingApi = retrofit.create(MeteredParkingApi.class);
    Call<List<Token>> call = meteredParkingApi.getMeteredParkingJson();
    call.enqueue(new Callback<List<Token>>() {
      @Override
      public void onResponse(Call<List<Token>> call, Response<List<Token>> response) {
        if (!response.isSuccessful()) {
          Log.d(TAG, "onResponse: code " + response.code());
          return;
        }
        List<Token> tokensFromApi = response.body();
        for (Token token :
            tokensFromApi) {
          token.setTokenType(TokenType.METERED_PARKING);
          token = TokenPrepper.prep(Main2Activity.this, token);
        }
        Token[] tokenArr = tokensFromApi.toArray(new Token[tokensFromApi.size()]);
        new AddTask().execute(tokenArr);
      }

      @Override
      public void onFailure(Call<List<Token>> call, Throwable t) {
        Log.d(TAG, "onFailure: parking " + t.getMessage());
        fillMeterParkingData();
      }
    });
  }

  private void fillRestroomsData() {
    Retrofit retrofit3 = new Retrofit.Builder()
        .baseUrl("https://datastore.unm.edu/locations/")
        .addConverterFactory(GsonConverterFactory.create())
        .build();
    RestroomsApi restroomsApi = retrofit3.create(RestroomsApi.class);
    Call<List<Token>> call = restroomsApi.getRestroomsJson();
    call.enqueue(new Callback<List<Token>>() {
      @Override
      public void onResponse(Call<List<Token>> call, Response<List<Token>> response) {
        if (!response.isSuccessful()) {
          Log.d(TAG, "onResponse: code " + response.code());
          return;
        }
        List<Token> tokensFromApi = response.body();
        for (Token token :
            tokensFromApi) {
          token.setTokenType(TokenType.RESTROOM);
          token = TokenPrepper.prep(Main2Activity.this, token);
        }
        Token[] tokenArr = tokensFromApi.toArray(new Token[tokensFromApi.size()]);
        new AddTask().execute(tokenArr);
      }

      @Override
      public void onFailure(Call<List<Token>> call, Throwable t) {
        Log.d(TAG, "onFailure: restrooms " + t.getMessage());
        fillRestroomsData();
      }
    });
  }

  private void fillShuttleData() {
    Retrofit retrofitShuttle = new Retrofit.Builder()
        .baseUrl("https://datastore.unm.edu/locations/")
        .addConverterFactory(GsonConverterFactory.create())
        .build();
    ShuttlesApi shuttlesApi = retrofitShuttle.create(ShuttlesApi.class);
    Call<List<Token>> call = shuttlesApi.getShuttlesJson();
    call.enqueue(new Callback<List<Token>>() {
      @Override
      public void onResponse(@NonNull Call<List<Token>> call,
          @NonNull Response<List<Token>> response) {
        if (!response.isSuccessful()) {
          Log.d(TAG, "onResponse: code " + response.code());
          return;
        }
        List<Token> tokensFromApi = response.body();
        for (Token token :
            tokensFromApi) {
          token.setTokenType(TokenType.SHUTTLE_STOP);
          token = TokenPrepper.prep(Main2Activity.this, token);
        }
        Token[] tokenArr = tokensFromApi.toArray(new Token[tokensFromApi.size()]);
        new AddTask().execute(tokenArr);
      }

      @Override
      public void onFailure(Call<List<Token>> call, Throwable t) {
        Log.d(TAG, "onFailure: shuttle" + t.getMessage());
        fillShuttleData();
      }
    });
  }


  public void fillDBwithAPI() {
    retrofit = new Retrofit.Builder()
        .baseUrl("https://datastore.unm.edu/locations/")
        .addConverterFactory(GsonConverterFactory.create())
        .build();
    fillBluephoneData();
    fillBluephoneSouthData();
    fillBuildingData();
    fillComputerPodData();
    fillDiningData();
    fillHealthyVendData();
    fillLibrariesData();
    fillMeterParkingData();
    fillRestroomsData();
    fillShuttleData();
    //todo:  create a single method to fill the DB for a particular call, and call it multiple times with new params rather than a bunch of methods and calling them all
//    apiCall = 0;
//    retrofit = new Retrofit.Builder()
//        .baseUrl("https://datastore.unm.edu/locations/")
//        .addConverterFactory(GsonConverterFactory.create())
//        .build();
//    RestroomsApi restroomsApi = retrofit.create(RestroomsApi.class);
//    Call<List<Token>> call = restroomsApi.getRestroomsJson();
//    call.enqueue(new Callback<List<Token>>() {
//      @Override
//      public void onResponse(Call<List<Token>> call, Response<List<Token>> response) {
//        if (!response.isSuccessful()) {
//          Log.d(TAG, "onResponse: code " + response.code());
//          return;
//        }
//        List<Token> tokensFromApi = response.body();
//        for (Token token :
//            tokensFromApi) {
//          switch (apiCall) {
//            case 0:
//              token.setTokenType(TokenType.RESTROOM);

//              token = TokenPrepper.prep(Main2Activity.this, token);
//          }
//        }
//        Token[] tokenArr = tokensFromApi.toArray(new Token[tokensFromApi.size()]);
//        new AddTask().execute(tokenArr);
//      }
//
//      @Override
//      public void onFailure(Call<List<Token>> call, Throwable t) {
//        Log.d(TAG, "onFailure: " + t.getMessage());
//      }
//    });
  }

  public void fillDBwithTest() {
    rng = new Random();
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
    Token[] tokenArr = prepopulateList.toArray(new Token[prepopulateList.size()]);
    new AddTask().execute(tokenArr);
  }

  protected void swapFrags(Fragment fragIn) {
    if (fragIn == null) {
      Log.d(TAG, "swapFrags: null fragment");
      return;
    }
    fragmentManager.beginTransaction()
        .replace(fragContainer.getId(), fragIn)
        .commit();
  }

  protected void swapFrags(Fragment fragIn, int callingViewId) {
    this.callingViewId = callingViewId;
    if (fragIn == null) {
      Log.d(TAG, "swapFrags: null fragment");
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
      Log.d(TAG, "swapFrags: null fragment");
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

  public static String calcDistance(double tokenLongitude, double tokenLatitude) {
    double distance = 0.0;
    //todo: calculate distance
    return Double.toString(distance);
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

  protected void sortDBTokens() {
    Collections.sort(dbTokens, new Comparator<Token>() {
      @Override
      public int compare(Token o1, Token o2) {
        if(o1.getDistance()==o2.getDistance()){
          return 0;
        }
        if(o1.getDistance()>o2.getDistance()){
          return 1;
        }
        return -1;
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
    try {
      swapFrags(mapsFragment, item);
    } catch (CloneNotSupportedException e) {
      Log.d(TAG, "goToMapFrag: CloneNotSupportedException: " + e.getMessage());
    }
  }

  private class AddTask extends AsyncTask<Token, Void, Void> {

    @Override
    protected Void doInBackground(Token... tokens) {
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
      for (Token token :
          dbTokens) {
        Location tempLoc = mCurrentLocation;
        LatLng myLoc = new LatLng(tempLoc.getLatitude(), tempLoc.getLongitude());
        LatLng unmToken = new LatLng(token.getMLatitude(),token.getMLongitude());
        token.setDistance((int) SphericalUtil.computeDistanceBetween(unmToken, myLoc));
      }
      sortDBTokens();
      searchFragment.updateListInAdapter();
    }

  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    LatLng Albuquerque = new LatLng(35.0843, -106.6198);
    googleMap.addMarker(new MarkerOptions()
        .position(Albuquerque)
        .title("UNM Campus Compass"));
    if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      // TODO: Consider calling
      //    ActivityCompat#requestPermissions
      // here to request the missing permissions, and then overriding
      //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
      //                                          int[] grantResults)
      // to handle the case where the user grants the permission. See the documentation
      // for ActivityCompat#requestPermissions for more details.
      return;
    }
    googleMap.setMyLocationEnabled(true);
    googleMap.moveCamera(CameraUpdateFactory.newLatLng(Albuquerque));
    googleMap.animateCamera(CameraUpdateFactory.zoomTo(18.0f));
  }

}
