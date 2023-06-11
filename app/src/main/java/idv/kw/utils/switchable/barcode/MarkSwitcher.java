package idv.kw.utils.switchable.barcode;

import android.content.Context;

import idv.kw.utils.switchable.Switchable;
import idv.kw.utils.switchable.ViewUpdater;

public class MarkSwitcher implements Switchable {
    boolean isMarkerMode=false;
    ViewUpdater viewUpdater;
    Context context;

    public MarkSwitcher(Context context,ViewUpdater updater){
        init(context,updater);
    }

    @Override
    public void init(Context context, ViewUpdater updater) {
        this.context=context;
        this.viewUpdater=updater;
    }

    @Override
    public Object handle(Object o, boolean shouldToggle, boolean ingoreErr) {
        if (shouldToggle) {
            this.isMarkerMode = !this.isMarkerMode;

        }

        return null;
    }

    @Override
    public boolean getStatus() {
        return isMarkerMode;
    }

    @Override
    public ViewUpdater getViewUpdater() {
        return this.viewUpdater;
    }
}
