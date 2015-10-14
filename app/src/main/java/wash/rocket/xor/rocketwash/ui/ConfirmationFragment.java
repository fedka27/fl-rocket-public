package wash.rocket.xor.rocketwash.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import wash.rocket.xor.rocketwash.R;
import wash.rocket.xor.rocketwash.model.PinResult;
import wash.rocket.xor.rocketwash.model.ProfileResult;
import wash.rocket.xor.rocketwash.requests.PinRequest;
import wash.rocket.xor.rocketwash.requests.VerifyPhoneRequest;
import wash.rocket.xor.rocketwash.util.Constants;
import wash.rocket.xor.rocketwash.util.util;

/**
 * A placeholder fragment containing a simple view.
 */
public class ConfirmationFragment extends BaseFragment {

    public static final String TAG = "ConfirmationFragment";

    private static final int MINUTES_WAIT = 1;

    private static final String ID_CAR_BRAND = "ID_CAR_BRAND";
    private static final String ID_CAR_MODEL = "ID_CAR_MODEL";
    private static final String CAR_NUM = "CAR_NUM";
    private static final String NAME = "NAME";

    private TextView txtCaption;
    private Button btnConfirm;
    private Button btnRequest;

    private EditText edPinCode;

    private Timer timer;
    private long mLastTime = -1;
    private boolean waiting = false;

    private ProgressBar progressBar;

    public ConfirmationFragment() {
    }

    public static ConfirmationFragment newInstance(int id_car_brand, int id_car_model, String name, String car_num) {
        ConfirmationFragment fragment = new ConfirmationFragment();
        Bundle args = new Bundle();
        args.putInt(ID_CAR_BRAND, id_car_brand);
        args.putInt(ID_CAR_MODEL, id_car_model);
        args.putString(NAME, name);
        args.putString(CAR_NUM, car_num);

        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (IFragmentCallbacksInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement IFragmentCallbacksInterface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_confirmation, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getView() == null)
            return;

        txtCaption = (TextView) getView().findViewById(R.id.txtCaption);
        btnRequest = (Button) getView().findViewById(R.id.btnRequest);
        btnConfirm = (Button) getView().findViewById(R.id.btnConfirm);
        edPinCode = (EditText) getView().findViewById(R.id.edPinCode);

        progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        txtCaption.setText(String.format(getActivity().getString(R.string.fragment_confirmation_caption), pref.getLastUsedPhoneCode() + pref.getLastUsedPhone()));

        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard();
                if (waiting)
                    return;
                waiting = true;
                pref.setLastTimeClickSMS(System.currentTimeMillis());
                createTimer(pref.getLastTimeClickSMS());
                getSpiceManager().execute(new PinRequest(pref.getLastUsedPhone()), "pin", DurationInMillis.ONE_MINUTE, new PinRequestListener());
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                progressBar.setVisibility(View.VISIBLE);
                getSpiceManager().execute(new VerifyPhoneRequest(edPinCode.getText().toString(), pref.getSessionID()), "pin", DurationInMillis.ONE_MINUTE, new VerifyPhoneRequestListener());
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (pref.getLastTimeClickSMS() > -1) {
            waiting = true;
            createTimer(pref.getLastTimeClickSMS());
        }
    }

    @TargetApi(3)
    private void hideKeyboard() {
        if (Build.VERSION.SDK_INT < 3) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edPinCode.getWindowToken(), 0);
        RelativeLayout rl = (RelativeLayout) getView().findViewById(R.id.main);
        rl.requestFocus();
    }


    private void stopCalculateTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    private void createTimer(long lasttime) {
        stopCalculateTimer();
        mLastTime = lasttime;

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.obtainMessage(1).sendToTarget();
            }

        }, 0, 1000);
    }

    TimerHandler mHandler = new TimerHandler(this);

    static class TimerHandler extends Handler {
        private final WeakReference<ConfirmationFragment> mFragment;

        TimerHandler(ConfirmationFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            ConfirmationFragment fragment = mFragment.get();
            if (fragment != null) {
                fragment.handleTimer(msg);
            }
        }
    }

    private void handleTimer(Message msg) {
        long t = 60 * MINUTES_WAIT - ((System.currentTimeMillis() - mLastTime) / 1000);

        if (getActivity() == null)
            return;

        if (t <= 0) {
            waiting = false;
            stopCalculateTimer();
            pref.setLastTimeClick(-1);
            btnRequest.setText(R.string.fragment_login_btn_retry_pin);
        } else
            btnRequest.setText(getActivity().getString(R.string.fragment_login_btn_retry_pin_after) + " " + util.SecondsToMS(t));
    }


    public final class PinRequestListener implements RequestListener<PinResult> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(getActivity(), R.string.request_pin_error, Toast.LENGTH_SHORT).show();
            // progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onRequestSuccess(final PinResult result) {
            // progressBar.setVisibility(View.GONE);
            //Toast.makeText(getActivity(), "login success", Toast.LENGTH_SHORT).show();
            Log.d("onRequestSuccess", result.getStatus() == null ? "null" : result.getStatus());

            if (Constants.SUCCESS.equals(result.getStatus())) {
                Toast.makeText(getActivity(), R.string.request_pin_success, Toast.LENGTH_SHORT).show();
            } else {
                // final int res = getResources().getIdentifier("login_" + result.getData().getResult(), "string", getActivity().getPackageName());
                // String error = res == 0 ? result.getData().getResult() : getString(res);
                // Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();

                //XXX сбросить таймер ?
                Toast.makeText(getActivity(), R.string.request_pin_phone_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public final class VerifyPhoneRequestListener implements RequestListener<ProfileResult> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(getActivity(), R.string.request_verify_error, Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onRequestSuccess(final ProfileResult result) {
            progressBar.setVisibility(View.GONE);
            //Toast.makeText(getActivity(), "login success", Toast.LENGTH_SHORT).show();
            Log.d("onRequestSuccess", result.getStatus() == null ? "null" : result.getStatus());

            if (Constants.SUCCESS.equals(result.getStatus())) {
                // Toast.makeText(getActivity(), R.string.request_pin_success, Toast.LENGTH_SHORT).show();
                pref.setProfile(result.getData());
                mCallback.onLogged();

            } else {

                pref.setLastTimeClick(-1);
                btnRequest.setText(R.string.button_next);
                waiting = false;
                stopCalculateTimer();

                Toast.makeText(getActivity(), "не удалось отправить данные, повторите попозже", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
