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
    private int mWidth;
    private int mHeight;

    public ScreenInfoProvider(Context context, int height, int width) {
        mContext = context;
        mHeight = height;
        mWidth = width;
    }

    @Override
    public int getScreenHeight() {
        return mHeight;
    }

    @Override
    public int getScreenWidth() {
        return mWidth;
    }

}
