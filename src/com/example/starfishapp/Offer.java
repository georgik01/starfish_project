package com.example.starfishapp;

import android.graphics.Bitmap;
import com.google.android.gms.maps.model.LatLng;
import java.io.Serializable;

class Offer implements Serializable {
   Bitmap image;
   final String name;
   final String description;
   final String url;
   final int price;
   final int priceCoupon;
   final int id;
   LatLng latLng;
  public Offer(String name, String description, String url, int price, int priceCoupon, int id) {
    this.name = name;
    this.description = description;
    this.price = price;
    this.priceCoupon = priceCoupon;
    this.id = id;
    this.url = url;
    this.latLng = new LatLng(10,10);
  }
}
