package com.example.starfishapp;

import java.util.ArrayList;
import java.util.List;

class Country {
  final String name;
  final int id;
  final List<Region> regions;

  Country(String name, int id) {
    this.name = name;
    this.id = id;
    regions = new ArrayList<Region>();
  }

  @Override
  public String toString() {
    return name;
  }
}
