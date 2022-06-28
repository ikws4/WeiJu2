package io.ikws4.weiju.storage;

interface StoreStrategy {
  String STORE_NAME = "script_store";

  String get(String k);

  void put(String k, String v);
}

