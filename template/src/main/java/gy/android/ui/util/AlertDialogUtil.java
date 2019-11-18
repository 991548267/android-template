package gy.android.ui.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import gy.android.R;


/**
 * Created by Chron on 2018/2/2.
 */

public class AlertDialogUtil {

    public static void showDialog(Context context, int messageId) {
        showDialog(context, context.getString(messageId));
    }

    public static void showDialog(Context context, int messageId, DialogInterface.OnClickListener ok) {
        showDialog(context, messageId, ok, null);
    }

    public static void showDialog(Context context, int messageId,
                                  DialogInterface.OnClickListener ok,
                                  DialogInterface.OnClickListener cancel) {
        showDialog(context, android.R.string.dialog_alert_title, messageId, ok, cancel);
    }

    public static void showDialog(Context context, int titleId, int messageId,
                                  DialogInterface.OnClickListener ok,
                                  DialogInterface.OnClickListener cancel) {
        showDialog(context, titleId, messageId,
                android.R.string.ok, ok,
                android.R.string.cancel, cancel);
    }

    public static void showDialog(Context context, int titleId, int messageId,
                                  int okId, DialogInterface.OnClickListener ok,
                                  int cancelId, DialogInterface.OnClickListener cancel) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        dialog.setTitle(titleId);
        dialog.setMessage(messageId);
        dialog.setCancelable(false);
        dialog.setPositiveButton(okId, ok);
        if (cancel != null)
            dialog.setNegativeButton(cancelId, cancel);
        dialog.show();
    }

    public static void showDialog(Context context, String message) {
        showDialog(context, message, null, null);
    }

    public static void showDialog(Context context, String message, DialogInterface.OnClickListener ok) {
        showDialog(context, message, ok, null);
    }

    public static void showDialog(Context context, String message,
                                  DialogInterface.OnClickListener ok,
                                  DialogInterface.OnClickListener cancel) {
        showDialog(context, context.getString(android.R.string.dialog_alert_title), message, ok, cancel);
    }

    public static void showDialog(Context context, String title, String message,
                                  DialogInterface.OnClickListener ok,
                                  DialogInterface.OnClickListener cancel) {
        showDialog(context, title, message,
                context.getString(R.string.ok), ok,
                context.getString(R.string.cancel), cancel);
    }

    public static void showDialog(Context context, String title, String message,
                                  String strOk, DialogInterface.OnClickListener ok,
                                  String strCancel, DialogInterface.OnClickListener cancel) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setPositiveButton(strOk, ok);
        if (cancel != null)
            dialog.setNegativeButton(strCancel, cancel);
        dialog.show();
    }

    public static void showDialog(Context context, int titleId, int messageId) {
        showDialog(context, titleId, messageId, null, null);
    }

    public static void showDialog(Context context, String title, String message) {
        showDialog(context, title, message, null, null);
    }
}
