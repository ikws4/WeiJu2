package io.ikws4.codeeditor.task;

public interface TaskFinishedListener<T> {
    void onFinished(T data);
}
