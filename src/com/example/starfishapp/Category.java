package com.example.starfishapp;

 class Category {
   final int id;
   final String name;

  public Category(int id, String name) {
    this.id = id;
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
