package com.example.starfishapp;

//import android.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class OfferActivity extends Activity{
  ImgCache cache = new ImgCache();
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.offer_layout);
    Intent intent = getIntent();
    ImageView imageView = (ImageView) findViewById(R.id.offer_image);
    TextView nameView = (TextView) findViewById(R.id.offer_name);
    TextView descriptionView = (TextView) findViewById(R.id.offer_description);
    TextView priceView = (TextView) findViewById(R.id.offer_price);
    cache.bind(intent.getStringExtra("image_url"), imageView);
    nameView.setText(intent.getStringExtra("name"));
    descriptionView.setText(Html.fromHtml(intent.getStringExtra("description")));
    priceView.setText(intent.getIntExtra("price",0) + "(" + intent.getIntExtra("coupon",0) + ")");
    Button button = (Button) findViewById(R.id.offer_buy_button);
    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

      }
    });
  }
}
