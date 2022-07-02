package io.ikws4.weiju.storage;

class EmptyStoreStrategy implements StoreStrategy {

    @Override
    public String get(String k) {
        return "";
    }

    @Override
    public void put(String k, String v) {

    }
}
