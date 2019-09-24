package core;

import java.util.List;

/**
 * Created by bogdantodasca on 15/09/2019.
 */
public abstract class FXDownloader {
    public abstract void parsePage();
    abstract String getURL();
    public abstract void onDone(List<Currency> data);
    public abstract void showProgress(final String s);
    protected abstract void updateDate(String date);
}
