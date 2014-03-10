package com.example.starfishapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {



  ArrayAdapter<Region> cities_adapter;
  ArrayAdapter<Country> countries_adapter;
  ArrayAdapter<Category> categories_adapter;
  private OfferAdapter offersAdapter;

  Spinner cities_spinner;
  Spinner countries_spinner;
  Spinner categories_spinner;
  ListView offersView;

  private Country selectedCountry;
  private Region selectedRegion;
  private Category selectedCategory;

  Button buttonMore;
  Button buttonMap;

  final Starfish starfish = new Starfish();

  int itemsToShow = 10;
  int page = 1;
  private List<Region> regions;
  private List<Country> countries;
  private List<Category> categories;
  private ArrayList<Offer> offers;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    offers = new ArrayList<Offer>();
    offersView = (ListView) findViewById(R.id.offers_view);
    offersAdapter = new OfferAdapter(this, offers);
    buttonMap = (Button) findViewById(R.id.map);
    buttonMap.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, MapActivity.class);
        MainActivity.this.startActivity(intent);
      }
    });
    buttonMore = new Button(this);
    buttonMore.setText("More");
    buttonMore.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        loadMore();
      }
    });
    offersView.addFooterView(buttonMore);
    offersView.setAdapter(offersAdapter);

    regions = starfish.loadRegions();
    countries = starfish.loadCountries();
    categories = starfish.loadCategories();

    cities_adapter = new ArrayAdapter<Region>(this, android.R.layout.simple_spinner_item, regions);
    countries_adapter = new ArrayAdapter<Country>(this, android.R.layout.simple_spinner_item, countries);
    categories_adapter = new ArrayAdapter<Category>(this, android.R.layout.simple_spinner_item, categories);
    cities_adapter.setNotifyOnChange(true);//MARK: what? read documentation.

    cities_spinner = (Spinner) findViewById(R.id.cities);
    countries_spinner = (Spinner) findViewById(R.id.countries);
    categories_spinner = (Spinner) findViewById(R.id.categories);

    cities_spinner.setAdapter(cities_adapter);
    countries_spinner.setAdapter(countries_adapter);
    categories_spinner.setAdapter(categories_adapter);

    final ArrayAdapter[] adapters = new ArrayAdapter[countries.size()];
    for (int i = 0; i < adapters.length; ++i) {
      List<Region> cities = countries.get(i).regions;
      final ArrayAdapter<Region> cities_adapter_temp = new ArrayAdapter<Region>(this, android.R.layout.simple_spinner_item, cities);
      cities_adapter_temp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      adapters[i] = cities_adapter_temp;
    }

    countries_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        cities_spinner.setAdapter(adapters[position]);
        selectedCountry = countries.get(position);
        regions = selectedCountry.regions;
      }

      @Override
      public void onNothingSelected(AdapterView<?> arg0) {
      }
    });

    cities_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        try {
          selectedRegion = selectedCountry.regions.get(position);
          int region_id = selectedRegion.id;
          if (selectedCategory != null) {
            int category_id = selectedCategory.id;
            String server_responce = reloadOffers(region_id, category_id);
            JSONObject parsedServerResponce = (JSONObject) new JSONTokener(server_responce).nextValue();
            showOffers(parsedServerResponce.getJSONArray("offers"));
            offersView.smoothScrollToPosition(0);
          }
        } catch (JSONException e) {
          Log.e("CITIESSpinnerBadOffers", e.getMessage());
          offersAdapter.clear();
        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> arg0) {
      }
    });

    categories_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedCategory = categories_adapter.getItem(position);
        int region_id = selectedRegion.id;
        if (selectedCategory != null) {
          int category_id = selectedCategory.id;
          String server_responce = reloadOffers(region_id, category_id);
          try {
            JSONObject parsedServerResponce = (JSONObject) new JSONTokener(server_responce).nextValue();
            showOffers(parsedServerResponce.getJSONArray("offers"));
            offersView.smoothScrollToPosition(0);
          } catch (JSONException e) {
            Log.e("CategoriesSpinner", e.getMessage());
          }
        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> adapterView) {

      }
    });
  }

  public String reloadOffers(int region_id, int category_id) {
    offersAdapter.clear();//FIXME: move
    return starfish.getOffers(region_id, category_id);
  }

  public void showOffers(JSONArray offersJSON) {
    for (int i = 0; i < offersJSON.length(); i++) {
      try {
        JSONObject offerObject = offersJSON.getJSONObject(i);
        //TODO: offer constructor accepting JSON
        Offer offer = starfish.parseOffer(offerObject);
        offersAdapter.add(offer);
      } catch (JSONException e) {
        e.printStackTrace();
        Log.e("ShowOffers", e.getMessage());
      }
    }
  }

  public void loadMore() {
    ++page;
    int itemsPerPage = 10;
    itemsToShow += itemsPerPage;
    String server_responce = starfish.getOffers(selectedRegion.id, selectedCategory.id);
    try {
      JSONObject parsedServerResponce = (JSONObject) new JSONTokener(server_responce).nextValue();
      showOffers(parsedServerResponce.getJSONArray("offers"));
    } catch (JSONException e) {
      Log.e("LoadMore", e.getMessage());
    }
  }


}