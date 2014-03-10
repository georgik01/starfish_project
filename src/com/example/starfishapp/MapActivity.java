package com.example.starfishapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.*;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


public class MapActivity extends FragmentActivity implements
  GooglePlayServicesClient.ConnectionCallbacks,
  GooglePlayServicesClient.OnConnectionFailedListener, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {

  private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
  private final static String ROUTESKEY = "com.example.starfishapp.routes";
  private final static String LOCATIONKEY = "com.example.starfishapp.location";
  private final static String MARKERSKEY = "com.example.starfishapp.markers";
  private final static String RADIUSKEY = "com.example.starfishapp.radius";
  private final static String OFFERSKEY = "com.example.starfishapp.offers";


  final Starfish starfish = new Starfish();
  private final List<Category> selectedCategories = new LinkedList<Category>();
  final private Random rng = new Random();
  Circle circle = null;
  SparseArray<SparseArray<List<Offer>>> offers = new SparseArray<SparseArray<List<Offer>>>();
  private GoogleMap map;
  private LocationClient mLocationClient;
  private List<Marker> offerMarkers = new ArrayList<Marker>();
  //  private List<Offer> offers = new ArrayList<Offer>();
  private List<List<LatLng>> mRoutes = new ArrayList<List<LatLng>>();
  private Marker mFirstMarker = null;
  private double mRadius = 100;
  private OfferInfoAdapter offerInfoAdapter = new OfferInfoAdapter(this);
  private List<Category> categories;
  private List<Country> countries;
  private List<Region> regions;
  private LatLng location;
  //TODO: fragment
  private ArrayAdapter<Region> cities_adapter;
  //  private ArrayAdapter<Category> categories_adapter;
  private ArrayAdapter<Country> countries_adapter;
  private Spinner cities_spinner;
  private Spinner countries_spinner;
  private MultiSelectionSpinner categories_spinner;
  private Country selectedCountry = new Country("",-1);
  private Region selectedRegion = new Region("",-1,-1);

  @Override
  public void onMapClick(LatLng latLng) {
    location = latLng;
    circle.setCenter(location);
  }

  @Override
  public boolean onMarkerClick(Marker marker) {
    if (mFirstMarker == null) {
      mFirstMarker = marker;
    } else {
      showDirections(mFirstMarker.getPosition(), marker.getPosition());
      mFirstMarker = null;
    }
    return false;
  }

  public void showDirections(LatLng start, LatLng end) {
    String url = getDirectionsUrl(start, end, "");
    AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
      @Override
      protected String doInBackground(String[] params) {
        return new DownloadWebPageTask().execute();
      }

      @Override
      protected void onPostExecute(String result) {
        DirectionsJSONParser parser = new DirectionsJSONParser();
        try {
          List<List<LatLng>> routes = parser.parse(new JSONObject(result));
          for (List<LatLng> route : routes) {
            showRoute(route);
          }
        } catch (JSONException e) {
          e.printStackTrace();
          Log.e("BadDirectionsJSON", e.getMessage());
        }
      }


    };
    task.execute(url);
  }

  public String getDirectionsUrl(LatLng start, LatLng end, String mode) {
    return "http://maps.googleapis.com/maps/api/directions/json?"
      + "origin=" + start.latitude + "," + start.longitude
      + "&destination=" + end.latitude + "," + end.longitude
      + "&sensor=false&mode=" + mode;
  }

  private void showRoute(List<LatLng> route) {
    PolylineOptions rectLine = new PolylineOptions().width(3).color(
      Color.RED);
    for (LatLng point : route) {
      rectLine.add(point);
    }
    map.addPolyline(rectLine);
    mRoutes.add(route);
  }

  @Override
  public void onInfoWindowClick(Marker marker) {
    Intent intent = new Intent(this, OfferActivity.class);
    Offer offer = offerInfoAdapter.offers.get(marker);
    intent.putExtra("name", offer.name);
    intent.putExtra("description", offer.description);
    intent.putExtra("price", offer.price);
    intent.putExtra("image_url", offer.url);
    intent.putExtra("coupon", offer.priceCoupon);
    startActivity(intent);
  }

  @Override
  protected void onActivityResult(
    int requestCode, int resultCode, Intent data) {
    // Decide what to do based on the original request code
    switch (requestCode) {
      case CONNECTION_FAILURE_RESOLUTION_REQUEST:
            /* If the result code is Activity.RESULT_OK, try
             * to connect again
             */
        switch (resultCode) {
          case Activity.RESULT_OK:
            mLocationClient.connect();
            break;
        }

    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_map);
    mLocationClient = new LocationClient(this, this, this);
    SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
    if (mapFragment != null) {
      mapFragment.setMenuVisibility(true);
      map = mapFragment.getMap();
      map.setOnMapClickListener(this);
      map.setOnMarkerClickListener(this);
//      map.setMyLocationEnabled(true);
      map.setInfoWindowAdapter(offerInfoAdapter);
      map.setOnInfoWindowClickListener(this);
    } else {
      Log.e("MapActivity", "Couldn't find mapFragment. Null returned");
    }
    cities_spinner = (Spinner) findViewById(R.id.region_spinner);
    countries_spinner = (Spinner) findViewById(R.id.country_spinner);
    categories_spinner = (MultiSelectionSpinner) findViewById(R.id.category_spinner);
    final ArrayList<ArrayAdapter> adapters = new ArrayList<ArrayAdapter>();
    AsyncTask<Object, Object, Object> task = new AsyncTask<Object, Object, Object>() {
      @Override
      protected Object doInBackground(Object[] params) {
        categories = starfish.loadCategories();
        countries = starfish.loadCountries();
        regions = starfish.loadRegions();
        return null;
      }

      @Override
      protected void onPostExecute(Object o) {
        cities_adapter = new ArrayAdapter<Region>(MapActivity.this, android.R.layout.simple_spinner_item, regions);
        countries_adapter = new ArrayAdapter<Country>(MapActivity.this, android.R.layout.simple_spinner_item, countries);
        cities_spinner.setAdapter(cities_adapter);
        countries_spinner.setAdapter(countries_adapter);
        categories_spinner.setItems(categories);
        for (Country country : countries) {
          List<Region> cities = country.regions;
          final ArrayAdapter<Region> cities_adapter_temp = new ArrayAdapter<Region>(MapActivity.this, android.R.layout.simple_spinner_item, cities);
          cities_adapter_temp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          adapters.add(cities_adapter_temp);
        }
      }
    };
    task.execute();
//    categories_adapter = new ArrayAdapter<Category>(this, android.R.layout.simple_spinner_item, categories);

    countries_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        cities_spinner.setAdapter(adapters.get(position));
        selectedCountry = countries.get(position);
        regions = selectedCountry.regions;
      }

      @Override
      public void onNothingSelected(AdapterView<?> arg0) {
      }
    });

    cities_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedRegion = selectedCountry.regions.get(position);
        final int region_id = selectedRegion.id;
        List<Integer> sc = categories_spinner.getSelectedIndices();
        int[] category_ids = new int[sc.size()];
        for (int i = 0; i < category_ids.length; ++i) {
          category_ids[i] = categories.get(sc.get(i)).id;
        }
        reloadOffers(region_id, category_ids);
      }

      @Override
      public void onNothingSelected(AdapterView<?> arg0) {
      }
    });

    categories_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        List<Integer> sc = categories_spinner.getSelectedIndices();
        int[] category_ids = new int[sc.size()];
        for (int i = 0; i < category_ids.length; ++i) {
          category_ids[i] = categories.get(sc.get(i)).id;
        }
        final int region_id = selectedRegion.id;
        reloadOffers(region_id, category_ids);
      }

      @Override
      public void onNothingSelected(AdapterView<?> adapterView) {

      }
    });

    int[] radii = {100, 200, 500, 1000};
    RadioGroup group = (RadioGroup) findViewById(R.id.radio_group);
    for (int i : radii) {
      final int r = i;
      RadioButton radius = new RadioButton(this);
      radius.setText(String.valueOf(r));
      group.addView(radius);
      radius.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
          if (isChecked) {
            mRadius = r;
            circle.setRadius(mRadius);
          }
        }
      });
    }

    Button clearButton = (Button) findViewById(R.id.button_clear);
    clearButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        clear();
      }
    });
  }

  @Override
  protected void onStart() {
    super.onStart();
    if (isGooglePlayServicesAvailable()) {
      mLocationClient.connect();
    }

  }

  private boolean isGooglePlayServicesAvailable() {
    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
    if (ConnectionResult.SUCCESS == resultCode) {
      // In debug mode, log the status
      Log.d("Location Updates", "Google Play services is available.");
      return true;
    } else {
      // Get the error dialog from Google Play services
      Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode,
        this,
        CONNECTION_FAILURE_RESOLUTION_REQUEST);

      // If Google Play services can provide an error dialog
      if (errorDialog != null) {
        // Create a new DialogFragment for the error dialog
        ErrorDialogFragment errorFragment = new ErrorDialogFragment();
        errorFragment.setDialog(errorDialog);
//        errorFragment.show(getSupportFragmentManager(), "Location Updates");
      }

      return false;
    }
  }

  @Override
  protected void onStop() {
    // Disconnecting the client invalidates it.
    mLocationClient.disconnect();
    super.onStop();
  }

  private void clear() {
    offerMarkers.clear();
    mRoutes.clear();
    offerInfoAdapter.offers.clear();
    map.clear();
//    if(circle != null){
//    map.addCircle(new CircleOptions()
//      .radius(circle.getRadius())
//      .strokeColor(circle.getStrokeColor())
//      .strokeWidth(circle.getStrokeWidth())
//      .fillColor(circle.getFillColor()));
//    }
  }

  private void reloadOffers(final int region_id, final int[] categories) {
//    final LinkedList<Offer> r = new LinkedList<Offer>();
    clear();
    for (final int category_id : categories) {
      if (offers.get(region_id) == null) {
        offers.put(region_id, new SparseArray<List<Offer>>());
      }
      List<Offer> res = offers.get(region_id).get(category_id);
      if (res == null) {
        final AsyncTask<Object, List<Offer>, List<Offer>> task = new AsyncTask<Object, List<Offer>, List<Offer>>() {
          @Override
          protected List<Offer> doInBackground(Object... params) {
            List<Offer> res = starfish.loadOffers(region_id, category_id);
            return res;
          }

          @Override
          protected void onPostExecute(List<Offer> o) {
            offers.get(region_id).put(category_id, o);
            showOffers(o);
          }
        };
        task.execute();
      }else{
        showOffers(res);
      }
    }
  }

  private void showOffers(List<Offer> offers) {
    for (Offer o : offers) {
      showOffer(o);
    }
  }

  public void showOffer(Offer offer) {
    double lat = location.latitude - 0.005 + rng.nextDouble() * .01;
    double lng = location.longitude - 0.005 + rng.nextDouble() * .01;
    offer.latLng = new LatLng(lat, lng);
    Marker m = map.addMarker(new MarkerOptions()
      .position(offer.latLng));
    offerMarkers.add(m);
    offerInfoAdapter.addOffer(offer, m);
  }

  /*
   * Called by Location Services when the request to connect the
   * client finishes successfully. At this point, you can
   * request the current location or start periodic updates
   */
  @Override
  public void onConnected(Bundle dataBundle) {
    // Display the connection status
    Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
    Location location = mLocationClient.getLastLocation();
//    Criteria criteria = new Criteria();
//    Location location = mLocationManager.getLastKnownLocation(mLocationManager.getBestProvider(criteria, false));
    if (location != null) {
      LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
      CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
      map.animateCamera(cameraUpdate);
      this.location = new LatLng(location.getLatitude(), location.getLongitude());
    } else {
      Toast.makeText(this, "Couldn't pinpoint your location.\nSorry.;(", Toast.LENGTH_SHORT).show();
      LatLng latLng = new LatLng(50.3, 30.3);
      this.location = latLng;
      CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
      map.animateCamera(cameraUpdate);
    }
    circle = map.addCircle(new CircleOptions()
      .center(this.location)
      .fillColor(Color.argb(128, 255, 0, 0))
      .strokeColor(Color.argb(0, 0, 0, 0))
      .radius(mRadius));
  }

  @Override
  public void onDisconnected() {
    // Display the connection status
    Toast.makeText(this, "Disconnected. Please re-connect.",
      Toast.LENGTH_SHORT).show();
  }

  /*
   * Called by Location Services if the attempt to
   * Location Services fails.
   */
  @Override
  public void onConnectionFailed(ConnectionResult connectionResult) {
    /*
     * Google Play services can resolve some errors it detects.
     * If the error has a resolution, try sending an Intent to
     * start a Google Play services activity that can resolve
     * error.
     */
    if (connectionResult.hasResolution()) {
      try {
        // Start an Activity that tries to resolve the error
        connectionResult.startResolutionForResult(
          this,
          CONNECTION_FAILURE_RESOLUTION_REQUEST);
            /*
            * Thrown if Google Play services canceled the original
            * PendingIntent
            */
      } catch (IntentSender.SendIntentException e) {
        // Log the error
        Log.e("ConnectionFailed", e.getMessage());
      }
    } else {
      Toast.makeText(this, "Sorry. Location services not available to you", Toast.LENGTH_LONG).show();
    }
  }

  // Define a DialogFragment that displays the error dialog
  public static class ErrorDialogFragment extends DialogFragment {

    // Global field to contain the error dialog
    private Dialog mDialog;

    // Default constructor. Sets the dialog field to null
    public ErrorDialogFragment() {
      super();
      mDialog = null;
    }

    // Set the dialog to display
    public void setDialog(Dialog dialog) {
      mDialog = dialog;
    }

    // Return a Dialog to the DialogFragment.
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      return mDialog;
    }
  }
}

