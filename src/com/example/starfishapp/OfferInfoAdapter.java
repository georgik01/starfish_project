package com.example.starfishapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
import java.util.Map;

public class OfferInfoAdapter implements GoogleMap.InfoWindowAdapter {
  Context mContext;
  ImgCache cache = new ImgCache();
  Map<Marker, Offer> offers = new HashMap<Marker, Offer>();

  public OfferInfoAdapter(Context context) {
    mContext = context;
  }

  public void addOffer(Offer offer, Marker marker) {
    offers.put(marker, offer);
  }

  @Override
  public View getInfoWindow(Marker marker) {
    return null;
  }

  @Override
  public View getInfoContents(final Marker marker) {
    View resultView;
    LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
    resultView = inflater.inflate(R.layout.offer_info_layout, null);
    if (resultView != null) {
      ImageView imageView = (ImageView) resultView.findViewById(R.id.offer_image);
      TextView nameView = (TextView) resultView.findViewById(R.id.offer_name);
      TextView descriptionView = (TextView) resultView.findViewById(R.id.offer_description);
      TextView priceView = (TextView) resultView.findViewById(R.id.offer_price);
      final Offer offer = offers.get(marker);
      if (offer != null) {
        if(offer.image == null){
        ImgCache.ImgDownload d = cache.new ImgDownload(offer.url, imageView){
          @Override
          protected void onPostExecute(Bitmap pic) {
            super.onPostExecute(pic);
            offer.image = pic;
            if(marker.isInfoWindowShown()){
//              marker.hideInfoWindow();
              marker.showInfoWindow();
            }
          }
        };
        d.execute();}else{
        imageView.setImageBitmap(offer.image);}
        nameView.setText(Html.fromHtml(offer.name));
        descriptionView.setText(Html.fromHtml(offer.description));
        priceView.setText(offer.price + "(" + offer.priceCoupon + ")");
        return resultView;
      }
    }
    resultView = new TextView(mContext);
    ((TextView) resultView).setText("Nothing");
    return resultView;
  }

  public Context getContext() {
    return mContext;
  }
}
