package io.ikws4.weiju.storage;

import java.util.Set;
import java.util.function.Function;

class EmptyStoreStrategy implements StoreStrategy {

    @Override
    public String get(String k, String defValue) {
        return "";
    }

    @Override
    public Set<String> get(String key, Function<Void, Set<String>> defValue) {
        return defValue.apply(null);
    }

    @Override
    public void put(String k, String v) {

    }

    @Override
    public void put(String k, Set<String> v) {

    }
}
