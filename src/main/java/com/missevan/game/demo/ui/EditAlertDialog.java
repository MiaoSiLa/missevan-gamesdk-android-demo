package com.missevan.game.demo.ui;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.missevan.game.demo.R;

/**
 * Created by yangya on 2019-10-24.
 */
public class EditAlertDialog {
    private AlertDialog mDialog;
    private TextView edit_title;
    private EditText edit_content;
    private Button edit_cancel;
    private Button edit_confirm;

    public EditAlertDialog(Context context) {
        mDialog = new AlertDialog.Builder(context).create();
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit, null);
        edit_title = dialogView.findViewById(R.id.edit_title);
        edit_content = dialogView.findViewById(R.id.edit_content);
        edit_cancel = dialogView.findViewById(R.id.edit_cancel);
        edit_confirm = dialogView.findViewById(R.id.edit_confirm);
        mDialog.setView(dialogView);
    }

    public EditAlertDialog setConfirmListener(View.OnClickListener listener) {
        edit_confirm.setOnClickListener(listener);
        return this;
    }

    public EditAlertDialog setCancelListener(View.OnClickListener listener) {
        edit_cancel.setOnClickListener(listener);
        return this;
    }

    public EditAlertDialog setEditTextInputType(int inputType) {
        edit_content.setInputType(inputType);
        return this;
    }

    public EditAlertDialog setTitle(String title) {
        edit_title.setText(title);
        return this;
    }

    public EditAlertDialog setEditHint(String hint) {
        edit_content.setHint(hint);
        return this;
    }

    public String getText() {
        return edit_content.getText().toString();
    }

    public void show() {
        mDialog.show();
    }

    public void dismiss() {
        mDialog.dismiss();
    }

}
