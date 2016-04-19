package com.voiceconf.voiceconf.ui.main;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;
import android.widget.EditText;

import com.voiceconf.voiceconf.R;
import com.voiceconf.voiceconf.networking.services.FriendService;
import com.voiceconf.voiceconf.util.Validator;

/**
 * Created by Attila Blenesi on 27 Dec 2015
 */
public class AddFriendDialog extends DialogFragment {

    private EditText mEmailInput;
    private TextInputLayout mInputLayout;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getContext().getString(R.string.add_friend))
                .setView(R.layout.dialog_add_friend)
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    dismiss();
                })
                .setPositiveButton(getContext().getString(R.string.add_friend), (dialog, which) -> {
                    mInputLayout.setError("");
                    if (mEmailInput != null && Validator.isValidEmail(mEmailInput.getText().toString())) {
                        FriendService.addNewFriend(getContext(), mEmailInput.getText().toString());
                    }
                    else {
                        Snackbar.make(mEmailInput, R.string.invalid_email, Snackbar.LENGTH_LONG).show();
                        mEmailInput.setText("");
                    }
                });

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        final AlertDialog alertDialog = (AlertDialog) getDialog();
        if (alertDialog != null) {
            mInputLayout = (TextInputLayout) alertDialog.findViewById(R.id.add_friend_input_layout);
            mInputLayout.setErrorEnabled(true);
            mEmailInput = (EditText) alertDialog.findViewById(R.id.add_friend_email_input);

            mEmailInput.requestFocus();
            alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
