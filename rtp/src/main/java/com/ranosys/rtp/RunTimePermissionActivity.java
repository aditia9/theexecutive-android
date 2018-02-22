package com.ranosys.rtp;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RunTimePermissionActivity extends AppCompatActivity {

    List<Boolean> isPermissionGrantList;
    List<String> userReqiuedPermssion;
    public  static final int REQUEST_CODE_ASK_PERMISSIONS = 101;
    int permissionsGrantedCount;
    int permissionsDeniedCount;
    int permissionsCount;
    public IsPermissionGrantedInterface IsPermissionGrantedInterface;


    public void getPermission(final List<String> permissionsList , IsPermissionGrantedInterface IsPermissionGrantedInterface){

        permissionsCount = 0;
        permissionsGrantedCount = 0;
        permissionsCount = 0;
        this.IsPermissionGrantedInterface = IsPermissionGrantedInterface;
        isPermissionGrantList = new ArrayList<>();
        userReqiuedPermssion = new ArrayList<>();

        for(int j = 0 ; j < permissionsList.size(); j++){
            userReqiuedPermssion.add(permissionsList.get(j));
        }

        for(int i = 0 ; i < permissionsList.size(); i++){
            boolean isPermissionGrant = Helper.isPermissionGranted(RunTimePermissionActivity.this, permissionsList.get(i));
            isPermissionGrantList.add(isPermissionGrant);
        }

        permissionsList.clear();

        for(int i = 0 ; i < isPermissionGrantList.size(); i++){
            if(!isPermissionGrantList.get(i)){
                permissionsCount++;
                permissionsList.add(userReqiuedPermssion.get(i));
            }
        }


        if(permissionsCount == 0){
            IsPermissionGrantedInterface.Done(true);
        }else {
            Helper.getPermissions(this, permissionsList, REQUEST_CODE_ASK_PERMISSIONS);}
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,  @NonNull int[] grantResults) {

        switch (requestCode) {

            case REQUEST_CODE_ASK_PERMISSIONS:

                Map<String, Integer> perms = new HashMap<String, Integer>();
                for(int i = 0 ; i < userReqiuedPermssion.size() ; i++){
                    perms.put(userReqiuedPermssion.get(i), PackageManager.PERMISSION_GRANTED);
                }

                for (int j = 0; j < permissions.length; j++)
                    perms.put(permissions[j], grantResults[j]);

                if (isPermissionGranted(perms)) {
                    IsPermissionGrantedInterface.Done(true);
                }
                else  if (isPermissionDenied(perms)) {
                    boolean showRationale = shouldShowRequestPermissionRationale(android.Manifest.permission.READ_PHONE_STATE);
                    if (! showRationale) {
                       // Toast.makeText(getApplicationContext() , "showRatinal is false" , Toast.LENGTH_SHORT).show();
                        // user also CHECKED "never ask again"
                        // you can either enable some fall back,
                        // disable features of your app
                        // or open another dialog explaining
                        // again the permission and directing to
                        // the app setting

                     //   Helper.showAppInfo(this);
                    }else {
                        IsPermissionGrantedInterface.Done(false);
                    }
                    IsPermissionGrantedInterface.Done(false);
                }

                return;
        }
    }

    public boolean isPermissionGranted(Map<String, Integer> perms ){
        for (Map.Entry<String, Integer> e : perms.entrySet()) {
            String key = e.getKey();
            Integer value = e.getValue();
            if( value == PackageManager.PERMISSION_GRANTED){
                permissionsGrantedCount ++;
                if(perms.size() ==  permissionsGrantedCount) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isPermissionDenied(Map<String, Integer> perms ){
        for (Map.Entry<String, Integer> e : perms.entrySet()) {
            String key = e.getKey();
            Integer value = e.getValue();
            if(value == PackageManager.PERMISSION_DENIED){
                permissionsDeniedCount ++;
            }
        }
        if(permissionsDeniedCount > 0) {
            return true;
        }else

            return false;
    }
}