package idv.kw.utils.switchable;

import android.content.Context;

public interface Switchable<T> {

    void init(Context context, ViewUpdater<T> updater);

    default void toggle() {
        T data = handle(this.getViewUpdater().getData(), true, false);
        updateView(getViewUpdater(), data);
    }

    default void refresh() {
        T handle = handle(this.getViewUpdater().getData(), false, true);
        updateView(getViewUpdater(), handle);
    }

    default void updateView(ViewUpdater updater, T data) {

        updater.updateText(data, getStatus());
    }

    T handle(T t, boolean shouldToggle, boolean ingoreErr);

    boolean getStatus();

    ViewUpdater<T> getViewUpdater();


}