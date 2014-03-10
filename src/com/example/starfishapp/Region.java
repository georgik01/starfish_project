package com.example.starfishapp;

class Region {
  String name;
  int id;
  int countryId;
  Region(String name, int id, int countryId) {
    this.name = name;
    this.id = id;
    this.countryId = countryId;
  }

  public String toString(){
    return name;
  }
}
