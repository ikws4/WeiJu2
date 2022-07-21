package io.ikws4.weiju.ext;

import androidx.lifecycle.MutableLiveData;

public class MutableLiveDataExt<T> extends MutableLiveData<T> {
    public MutableLiveDataExt() {
        super();
    }

    public MutableLiveDataExt(T value) {
        super(value);
    }

    public void publish() {
        setValue(getValue());
    }

    public void postPublish() {
        postValue(getValue());
    }
}
