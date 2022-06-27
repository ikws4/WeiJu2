package io.ikws4.weiju.storage;

interface StorageStrategy {
  String STORE_NAME = "script_store";

  String read(String k);

  void write(String k, String v);
}
