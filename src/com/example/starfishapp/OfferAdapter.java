package com.example.starfishapp;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.List;

public class OfferAdapter extends ArrayAdapter<Offer> {
  ImgCache cache = new ImgCache();
//  MainActivity mContext;
  List<Offer> offers;


  public OfferAdapter(MainActivity context, List<Offer> objects) {
    super(context, 0, objects);
//    mContext = context;
    offers = objects;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
      convertView = inflater.inflate(R.layout.offer_layout, parent, false);
    }
    ImageView imageView = (ImageView) convertView.findViewById(R.id.offer_image);
    TextView nameView = (TextView) convertView.findViewById(R.id.offer_name);
    TextView descriptionView = (TextView) convertView.findViewById(R.id.offer_description);
    TextView priceView = (TextView) convertView.findViewById(R.id.offer_price);
    Offer offer = getItem(position);
    cache.bind(offer.url, imageView);
    imageView.setImageBitmap(offer.image);
    nameView.setText(offer.name);
    descriptionView.setText(Html.fromHtml(offer.description));
    priceView.setText(offer.price + "(" + offer.priceCoupon + ")");
    return convertView;
  }

  public void clear() {
    offers.clear();
    notifyDataSetChanged();
  }

}
