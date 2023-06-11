package idv.kw.utils.switchable.barcode;

import android.content.Context;

import idv.kw.utils.switchable.Switchable;
import idv.kw.utils.switchable.ViewUpdater;

public class FlashLightSwitcher implements Switchable {
    boolean isLightOn=false;
    ViewUpdater viewUpdater;
    Context context;

    public FlashLightSwitcher( Context context,ViewUpdater viewUpdater) {
        this.viewUpdater = viewUpdater;
        this.context = context;
    }

    @Override
    public void init(Context context, ViewUpdater updater) {
        this.context=context;
        this.viewUpdater=updater;
    }

    @Override
    public Object handle(Object o, boolean shouldToggle, boolean ingoreErr) {
        if (shouldToggle) {
            this.isLightOn = !this.isLightOn;

        }

        return null;
    }

    @Override
    public boolean getStatus() {
        return this.isLightOn;
    }

    @Override
    public ViewUpdater getViewUpdater() {
        return this.viewUpdater;
    }
}
