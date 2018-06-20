package com.amar.library.provider;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;

import com.amar.library.provider.interfaces.IScreenInfoProvider;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Created by Amar Jain on 17/03/17.
 */

@ParametersAreNonnullByDefault
public class ScreenInfoProvider implements IScreenInfoProvider{

    private final Context mContext;

    public ScreenInfoProvider(Context context) {
        mContext = context;
    }

    @Override
    public int getScreenHeight() {
        return getDeviceHeightDimension();
    }

    @Override
    public int getScreenWidth() {
        return getDeviceWidthDimension();
    }

    int getDeviceWidthDimension() {
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }


    int getDeviceHeightDimension() {
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        return metrics.heightPixels;
    }
}
