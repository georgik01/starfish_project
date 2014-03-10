package com.example.starfishapp;

import android.util.Log;
import android.util.SparseArray;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

public class Starfish {

  public static final String baseUrl = "http://starfishapp.pp.ua/app.php?action=";
  SparseArray<Country> countryById = new SparseArray<Country>();
  public List<Category> loadCategories() {
    List<Category> categories = null;
//    if (categories.size() == 0) {
      try {
        String current_url = "get_category";
        String server_responce = readWebpage(current_url);
        JSONObject parsedServerResponce = (JSONObject) new JSONTokener(server_responce).nextValue();
        JSONArray ja_categories = parsedServerResponce.getJSONArray("categories");
        categories = readCategories(ja_categories);
      }  catch (JSONException e) {
        Log.e("BadCategoryJSON", e.getMessage());
      }
//    }
    if(categories == null){
      return new ArrayList<Category>();
    }
    return categories;
  }

  List<Country> loadCountries() {
    List<Country> countries = null;
//    if (countries.size() == 0) {
      try {
        String current_url = "get_country";
        String server_responce = readWebpage(current_url);
        JSONObject parsedServerResponce = (JSONObject) new JSONTokener(server_responce).nextValue();
        JSONArray ja_countries = parsedServerResponce.getJSONArray("countries");
        countries = readCountries(ja_countries);
      }catch (JSONException e) {
        e.printStackTrace();
        Log.e("BadCountryJSON", e.getMessage());
      }catch(ClassCastException e){
        e.printStackTrace();
        Log.e("readCountries returned string", e.getMessage());
      }
//    }
    if(countries == null){
      return new ArrayList<Country>();
    }
    return countries;
  }

  List<Region> loadRegions() {
    List<Region> regions = null;
//    if (regions.size() == 0) {
      try {
        String current_url = "get_region";
        String server_responce = readWebpage(current_url);
        JSONObject parsedServerResponce = (JSONObject) new JSONTokener(server_responce).nextValue();
        JSONArray ja_regions = parsedServerResponce.getJSONArray("regions");
        regions = readRegions(ja_regions);
      }catch (JSONException e) {
        e.printStackTrace();
        Log.e("BadRegionJSON", e.getMessage());
      }catch(ClassCastException e){
        e.printStackTrace();
        Log.e("readRegions returned string", e.getMessage());
      }
//    }
    if(regions == null){
      return new ArrayList<Region>();
    }
    return regions;
  }

  public String readWebpage(String url){
    DownloadWebPageTask task = new DownloadWebPageTask();
    return task.execute(getUrl(url));
  }

  String getUrl(String u) {
    return baseUrl + u;
  }

  public List<Category> readCategories(JSONArray array) {
    ArrayList<Category> result = new ArrayList<Category>(array.length());
    for (int i = 0, end = array.length(); i < end; ++i) {
      try {
        JSONObject object = (JSONObject) array.get(i);
        result.add(parseCategory(object));
      } catch (JSONException e) {
        Log.e("BadCategory", e.getMessage());
      }
    }
    return result;
  }

  Category parseCategory(JSONObject object) throws JSONException {
    String name = object.getString("name");
    int id = object.getInt("id");
    return new Category(id, name);
  }


  public List<Country> readCountries(JSONArray countries) {
    ArrayList<Country> result = new ArrayList<Country>();
    for (int i = 0, end = countries.length(); i < end; ++i) {
      try {
        JSONObject c = countries.getJSONObject(i);
        Country country = parseCountry(c);
        result.add(country);
      } catch (JSONException e) {
        e.printStackTrace();
        Log.e("ReadCountries", e.getMessage());
      }
    }
    return result;
  }

  Country parseCountry(JSONObject object) throws JSONException {
    String name = object.getString("title");
    int id = object.getInt("id");
    Country country = new Country(name, id);
    countryById.put(id, country);
    return country;
  }


  public List<Region> readRegions(JSONArray regions) {
    ArrayList<Region> result = new ArrayList<Region>();
    for (int i = 0, end = regions.length(); i < end; ++i) {
      try {
        JSONObject r = regions.getJSONObject(i);
        String name = r.getString("name");
        int id = r.getInt("id");
        int countryId = r.getInt("country_id");
        Region region = new Region(name, id, countryId);
        Country c = countryById.get(countryId);
        if (c != null) {
          c.regions.add(region);
        }
        result.add(region);
      } catch (JSONException e) {
        Log.e("ReadRegions", e.getMessage());
      }
    }
    return result;
  }

//  interface Parser<T> {
//    T Parse(JSONObject object) throws JSONException;
//  }

  public List<Offer> loadOffers(int region_id, int category_id){
    String server_responce = getOffers(region_id, category_id);
    List<Offer> offers = null;
    try {
      JSONObject parsedServerResponce = (JSONObject) new JSONTokener(server_responce).nextValue();
      JSONArray array = parsedServerResponce.getJSONArray("offers");
      offers = readOffers(array);
    } catch (JSONException e) {
      e.printStackTrace();
      Log.e("bad offers responce", e.getMessage());
    }catch(ClassCastException e){
      e.printStackTrace();
      Log.e("readOffers returned string", e.getMessage());
    }
    return offers;
  }

  public List<Offer> loadOffers(int region_id, List<Integer> category_ids){
    List<Offer> res = new ArrayList<Offer>();
    for(int id:category_ids){
      res.addAll(loadOffers(region_id, id));
    }
    return res;
  }

  public String getOffers(int region_id, int category_id) {
    String server_responce;
    String action = "get_offers&region_id=" + region_id + "&category_id=" + category_id + "";
    server_responce = readWebpage(action);
    return server_responce;
  }

  public List<Offer> readOffers(JSONArray offersJSON) {
    List<Offer> offers = new ArrayList<Offer>(offersJSON.length());
    for (int i = 0; i < offersJSON.length(); i++) {
      try {
        JSONObject offerObject = offersJSON.getJSONObject(i);
        Offer offer = parseOffer(offerObject);
        offers.add(offer);
      } catch (JSONException e) {
        e.printStackTrace();
        Log.e("ShowOffers", e.getMessage());
      }
    }
    return offers;
  }
  public Offer parseOffer(JSONObject offerObject) throws JSONException {
    String url = offerObject.getString("url_img");
    String name = offerObject.getString("name");
    String description = offerObject.getString("description");
    int price = offerObject.getInt("price");
    int coupon = offerObject.getInt("pricecoupon");
    int id = offerObject.getInt("id");
    return new Offer(name, description, url, price, coupon, id);
  }
}
