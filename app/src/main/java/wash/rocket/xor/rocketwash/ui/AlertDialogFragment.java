package wash.rocket.xor.rocketwash.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import wash.rocket.xor.rocketwash.R;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class AlertDialogFragment extends DialogFragment {

    public static AlertDialogFragment newInstance(int title, String message, int id, Fragment target) {
        AlertDialogFragment frag = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        args.putInt("id", id);
        args.putString("message", message);
        frag.setArguments(args);
        frag.setTargetFragment(target, id);
        return frag;
    }

    public static AlertDialogFragment newInstance(int title, String message,
                                                  String buttonOk,
                                                  String buttonCancel,
                                                  int id,
                                                  Fragment target) {
        AlertDialogFragment frag = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        args.putInt("id", id);
        args.putString("message", message);
        args.putString("buttonOk", buttonOk);
        args.putString("buttonCancel", buttonCancel);

        frag.setArguments(args);
        frag.setTargetFragment(target, id);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");
        String message = getArguments().getString("message");
        final int id = getArguments().getInt("id");

        String buttonOk = getArguments().getString("buttonOk");
        String buttonCancel = getArguments().getString("buttonCancel");
        @Nullable String buttonNeutral = getArguments().getString("buttonNeutral");

        if (TextUtils.isEmpty(buttonOk))
            buttonOk = getActivity().getString(R.string.yeas);

        if (TextUtils.isEmpty(buttonCancel))
            buttonCancel = getActivity().getString(R.string.cancel);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme)
                .setIcon(R.drawable.ic_action_info_outline_blue)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(buttonOk,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, null);
                                dismiss();
                            }
                        }
                )
                .setNegativeButton(buttonCancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_CANCELED, null);
                                dismiss();
                            }
                        }
                );

        return builder.create();
    }
}