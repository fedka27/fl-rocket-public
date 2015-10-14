package wash.rocket.xor.rocketwash.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import wash.rocket.xor.rocketwash.R;

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

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");
        String message = getArguments().getString("message");
        final int id = getArguments().getInt("id");

        return new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_action_info_outline_blue)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.yeas,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //((Test) getActivity()).doPositiveClick();
                                //NearestWashServicesFragment.this.doPositiveClick(id);
                                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
                                dismiss();
                            }
                        }
                )
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //((Test) getActivity()).doNegativeClick();
                                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, null);
                                dismiss();
                            }
                        }
                )
                .create();
    }
}