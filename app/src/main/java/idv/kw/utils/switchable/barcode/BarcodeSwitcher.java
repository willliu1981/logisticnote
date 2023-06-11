package idv.kw.utils.switchable.barcode;

import android.content.Context;
import android.widget.Toast;

import idv.kw.utils.switchable.Switchable;
import idv.kw.utils.switchable.ViewUpdater;

public class BarcodeSwitcher implements Switchable<String> {
    Context context;
    ViewUpdater<String> viewUpdater;
    private boolean switchToLast4 = true;

    public BarcodeSwitcher(Context context, ViewUpdater<String> updater) {
        init(context, updater);
    }


    private String processBarcode(String barcode, boolean ingoreErr) {
        if (barcode.length() == 13) {
            if (switchToLast4) {
                return barcode; // 還原尾4碼

            } else {
                return barcode.substring(0, barcode.length() - 4); // 去除尾4碼

            }
        } else {
            if (!ingoreErr) {
                Toast.makeText(this.context, "code 格式錯誤", Toast.LENGTH_LONG).show();

            }

            return barcode;
        }
    }


    @Override
    public void init(Context context, ViewUpdater<String> updater) {
        this.context = context;
        this.viewUpdater = updater;
    }

    @Override
    public String handle(String s, boolean shouldToggle, boolean ingoreErr) {
        if (shouldToggle) {
            this.switchToLast4 = !this.switchToLast4;

        }

        String s1 = processBarcode(s, ingoreErr);

        return s1;
    }

    @Override
    public boolean getStatus() {
        return switchToLast4;
    }

    @Override
    public ViewUpdater<String> getViewUpdater() {
        return this.viewUpdater;
    }


}
