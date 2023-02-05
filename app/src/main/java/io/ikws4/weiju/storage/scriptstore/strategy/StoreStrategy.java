package io.ikws4.weiju.storage.scriptstore.strategy;

import java.util.Set;

public interface StoreStrategy {
    String STORE_NAME = "script_store";
    String DUMMY_KEY = "dummy_key";

    boolean canRead();

    String get(String k, String defValue);

    Set<String> get(String key, Set<String> defValue);

    void put(String k, String v);

    void put(String k, Set<String> v);
}

