package com.ranosys.rtp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.List;

/**
 * Created by ranosys-rashpal on 23/5/16.
 */
public class Helper {


    public static void getPermissions(final Activity activity, final List<String> permissionsList , final int permissioncode){

        if (permissionsList.size() > 0) {
            for(int i = 0 ; i<  permissionsList.size() ; i++) {
                int hasWriteContactsPermission = ContextCompat.checkSelfPermission(activity, permissionsList.get(i));

              /*  if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!activity.shouldShowRequestPermissionRationale(permissionsList.get(i))) {

                            showAppInfo(activity);

                            return;
                        }
                    }*/

                    ActivityCompat.requestPermissions(activity,
                            permissionsList.toArray(new String[permissionsList.size()]),
                            permissioncode);

                    return;
               // }
            }
        }
    }



    public static void showAppInfo(final Activity activity ) {

        AlertDialog mPermissionAlertDialog = null;

        AlertDialog.Builder builder = Helper.initAlertDialog(activity);
        builder
                .setTitle(R.string.title_appinfo_title)
                .setCancelable(false)
                .setMessage(R.string.title_appinfo_message)
                .setPositiveButton(R.string.title_btn_appinfo, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Helper.moveToSetting(activity);
                    }
                });

        mPermissionAlertDialog = builder.create();
        mPermissionAlertDialog.show();

    }
    public static AlertDialog.Builder initAlertDialog(Context mContext){
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(mContext);
        }
        return builder;
    }


    public static void moveToSetting(Activity activity) {
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + activity.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        activity.startActivity(i);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean isPermissionGranted(Context context, String permission){
        int hasWriteContactsPermission = context.checkSelfPermission(permission);
        if (hasWriteContactsPermission == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        else {
            return false;
        }
    }
}
