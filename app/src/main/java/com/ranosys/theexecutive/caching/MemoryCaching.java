package com.ranosys.theexecutive.caching;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * Created by Mohammad Sunny on 26/3/18.
 */
public class MemoryCaching {

    private static LruCache<String, Bitmap> mMemoryCache;

    public static int getCacheSize(){
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        return cacheSize;

    }

    public MemoryCaching(){

        if(null == mMemoryCache)
        {
            mMemoryCache = new LruCache<String, Bitmap>(getCacheSize()) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    // The cache size will be measured in kilobytes rather than
                    // number of items.
                    return bitmap.getByteCount() / 1024;
                }
            };
        }
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.resize(getCacheSize());
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    public Bitmap getBitmapFromMemoryCache(String imageKey) {

        final Bitmap bitmap = getBitmapFromMemCache(imageKey);
        return bitmap;
    }
}
