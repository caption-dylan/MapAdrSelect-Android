package com.zym.permission.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.zym.permission.R;
import com.zym.permission.utils.AndroidRomUtil;
import com.zym.permission.utils.SettingUiUtils;

import static com.zym.permission.dialog.ShowPermissionDialog.NEED_PERMISSION;

/**
 *   文件作用   [判断机型]
 *   Created by bigStuart on 2017/12/18
 *   @author bigStuart
 *   @date  2017/12/18 23:22
 */

public class PermissionDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle mBundle = getArguments();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.PermissionDialog);
        builder.setTitle(R.string.permission_dialog_title);
        String content = getActivity().getResources().getString(R.string.permission_dialog_content);
        String permission = mBundle.getString(NEED_PERMISSION);
        content = String.format(content, permission);
        int index = content.indexOf(permission);
        SpannableStringBuilder ssb = new SpannableStringBuilder(content);
        ForegroundColorSpan greenSpan = new ForegroundColorSpan(ContextCompat.getColor(getActivity(), R.color.permission_dialog));
        ssb.setSpan(greenSpan, index, permission.length()+index, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setMessage(ssb);
        builder.setPositiveButton(R.string.permission_dialog_setting, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                gotoSetting();
                dismiss();
            }
        });
        builder.setNegativeButton(R.string.permission_dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dismiss();
            }
        });
        return builder.create();
    }

    /****
     * 打开 当前包名的应用程序界面
     */
    private void gotoSetting(){
        if(AndroidRomUtil.isEMUI()){            //华为
            SettingUiUtils.gotoHuaweiPermission(getActivity());
        }else  if(AndroidRomUtil.isMIUI()){     //小米
            SettingUiUtils.gotoMiuiPermission(getActivity());
        }else if (AndroidRomUtil.isFlyme()){    //魅族
            SettingUiUtils.gotoMeizuPermission(getActivity());
        }else{                                  //未知
            SettingUiUtils.gotoAppDetailSetting(getActivity());
        }
    }
}
