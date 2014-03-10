package com.example.starfishapp;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

public class DirectionsJSONParser {

  /**
   * Receives a JSONObject and returns a list of lists containing latitude and longitude
   */
  public List<List<LatLng>> parse(JSONObject jObject) {

    List<List<LatLng>> routes = new ArrayList<List<LatLng>>();
    JSONArray jRoutes;
//    JSONArray jLegs;
//    JSONArray jSteps;

    try {

      jRoutes = jObject.getJSONArray("routes");

      /** Traversing all routes */
      for (int i = 0; i < jRoutes.length(); i++) {
//        jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
        List<LatLng> path;// = new ArrayList<LatLng>();
//
//        /** Traversing all legs */
//        for (int j = 0; j < jLegs.length(); j++) {
//          jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
//
//          /** Traversing all steps */
//          for (int k = 0; k < jSteps.length(); k++) {
            String polyline = ((JSONObject) jRoutes.get(i)).getJSONObject("overview_polyline").getString("points");
//          String  polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");

        path = decodePoly(polyline);
//          }
//          routes.add(path);
//        }
        routes.add(path);
      }

    } catch (JSONException e) {
      Log.e("BadDirectionsJSON", e.getMessage());
    } catch (Exception e) {
      Log.e("DirectionsParser", e.getMessage());
    }
    return routes;
  }

  /**
   * Method to decode polyline points
   * Courtesy : jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
   */
  private List<LatLng> decodePoly(String encoded) {

    List<LatLng> poly = new ArrayList<LatLng>();
    int index = 0, len = encoded.length();
    int lat = 0, lng = 0;

    while (index < len) {
      int b, shift = 0, result = 0;
      do {
        b = encoded.charAt(index++) - 63;
        result |= (b & 0x1f) << shift;
        shift += 5;
      } while (b >= 0x20);
      int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
      lat += dlat;

      shift = 0;
      result = 0;
      do {
        b = encoded.charAt(index++) - 63;
        result |= (b & 0x1f) << shift;
        shift += 5;
      } while (b >= 0x20);
      int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
      lng += dlng;

      LatLng p = new LatLng((((double) lat / 1E5)),
        (((double) lng / 1E5)));
      poly.add(p);
    }
    return poly;
  }
}