package core;

public interface Callback<T> {
    void onDone(T result);
}
