package io.ikws4.weiju.storage;

import java.util.Set;
import java.util.function.Function;

interface StoreStrategy {
  String STORE_NAME = "script_store";

  String get(String k, String defValue);

  Set<String> get(String key, Function<Void, Set<String>> defValue);

  void put(String k, String v);

  void put(String k, Set<String> v);
}

