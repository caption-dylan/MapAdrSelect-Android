package com.zym.permission.dialog;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;

/**
 *   文件作用   [权限申请 弹出框]
 *   使用方式：
 *   1、 ShowPermissionDialog.newInstance(fragment == null ? activity : fragment.getActivity(), permission);
 *   第一参数：Fragment或Activity
 *   第二参数：需要的权限值 一般是在  onDenied 中调用
 *   Created by bigStuart on 2017/12/18
 *   @author bigStuart
 *   @date  2017/12/18 23:23
 */

public class ShowPermissionDialog extends Fragment {

    /****
     * 所需要的权限
     */
    public static final String NEED_PERMISSION = "needPermission";

    private Activity activity;

    /****
     * 创建 Fragment
     * @param activity
     * @param needPermission
     */
    public static void newInstance(Activity activity, String needPermission){
        FragmentTransaction content = activity.getFragmentManager().beginTransaction();
        ShowPermissionDialog updateChecker = new ShowPermissionDialog();
        Bundle args = new Bundle();
        args.putString(NEED_PERMISSION,needPermission);
        updateChecker.setArguments(args);
        content.add(updateChecker, null).commit();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
        Bundle args = getArguments();
        String needPermission = args.getString(NEED_PERMISSION);
        show(needPermission);
    }

    /****
     * 显示
     * @param needPermission        需要的权限
     */
    private void show(String needPermission){

        String chinesePermission = permissionToChinese(needPermission);

        PermissionDialog pd = new PermissionDialog();
        Bundle args = new Bundle();
        args.putString(NEED_PERMISSION, chinesePermission);
        pd.setCancelable(false);
        pd.setArguments(args);
        pd.show(activity.getFragmentManager(), null);
    }

    private String permissionToChinese(String needPermission){
        String chinesePermission = "";
        if(needPermission.indexOf("LOCATION".toUpperCase()) != -1){
            chinesePermission = "位置信息";
        }else if(needPermission.indexOf("STORAGE".toUpperCase()) != -1){
            chinesePermission = "存储空间";
        }else if (isPhonePermission(needPermission)){
            chinesePermission = "电话";
        }else if(needPermission.indexOf("CAMERA") != -1){
            chinesePermission = "相机";
        }else if(isSMSPermission(needPermission)){
            chinesePermission = "短信";
        }else if(isContactsPermission(needPermission)){
            chinesePermission = "通讯录";
        }else if(needPermission.indexOf("RECORD_AUDIO") != -1){
            chinesePermission = "麦克风";
        }else if(needPermission.indexOf("CALENDAR") != -1){
            chinesePermission = "日历";
        }else if(needPermission.indexOf("BODY_SENSORS") != -1){
            chinesePermission = "身体传感器";
        }
        return chinesePermission;
    }

    /****
     * 判断是否有电话权限
     * @param needPermission
     * @return
     */
    private boolean isPhonePermission(String needPermission){
        String[] phonePermission = new String[]{
                "android.permission.READ_CALL_LOG",
                "android.permission.READ_PHONE_STATE",
                "android.permission.CALL_PHONE",
                "android.permission.WRITE_CALL_LOG",
                "android.permission.USE_SIP",
                "android.permission.PROCESS_OUTGOING_CALLS",
                "com.android.voicemail.permission.ADD_VOICEMAIL",
        };
        return isHasPermission(phonePermission, needPermission);
    }

    /****
     * 判断是否有联系人权限
     * @param needPermission
     * @return
     */
    private boolean isContactsPermission(String needPermission){
        String[] contactsPermission = new String[]{
                "android.permission.WRITE_CONTACTS",
                "android.permission.GET_ACCOUNTS",
                "android.permission.READ_CONTACTS"
        };
        return isHasPermission(contactsPermission, needPermission);
    }

    /****
     * 是否有短信权限
     * @param needPermission
     * @return
     */
    private boolean isSMSPermission(String needPermission){
        String[] smsPermission = new String[]{
                "android.permission.READ_SMS",
                "android.permission.RECEIVE_WAP_PUSH",
                "android.permission.RECEIVE_MMS",
                "android.permission.RECEIVE_SMS",
                "android.permission.SEND_SMS",
                "android.permission.READ_CELL_BROADCASTS",
        };
        return isHasPermission(smsPermission, needPermission);
    }

    /****
     * 判断是否有权限
     * @param permissions        权限数组
     * @param needPermission     指定的权限
     * @return
     */
    private boolean isHasPermission(String[] permissions, String needPermission){
        for (String permission : permissions){
            if (permission.equals(needPermission)){
                return true;
            }
        }
        return false;
    }


}
