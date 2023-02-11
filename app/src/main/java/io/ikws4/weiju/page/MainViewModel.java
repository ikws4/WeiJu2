package io.ikws4.weiju.page;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import io.ikws4.weiju.utils.MutableLiveDataExt;

public class MainViewModel extends ViewModel {
    public MutableLiveDataExt<Boolean> progressBarStatus = new MutableLiveDataExt<>(false);

    public LiveData<Boolean> getProgressBarStatus() {
        return progressBarStatus;
    }

    public void hideProgressBar() {
        progressBarStatus.postValue(false);
    }

    public void showProgressBar() {
        progressBarStatus.postValue(true);
    }
}
