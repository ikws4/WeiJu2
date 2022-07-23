package io.ikws4.weiju.storage.strategy;

import java.util.Set;

public class EmptyStoreStrategy implements StoreStrategy {

    @Override
    public String get(String k, String defValue) {
        return "";
    }

    @Override
    public Set<String> get(String key, Set<String> defValue) {
        return defValue;
    }

    @Override
    public void put(String k, String v) {

    }

    @Override
    public void put(String k, Set<String> v) {

    }
}
