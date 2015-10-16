package wash.rocket.xor.rocketwash.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import wash.rocket.xor.rocketwash.R;
import wash.rocket.xor.rocketwash.model.LoginResult;
import wash.rocket.xor.rocketwash.model.PinResult;
import wash.rocket.xor.rocketwash.requests.LoginRequest;
import wash.rocket.xor.rocketwash.requests.PinRequest;
import wash.rocket.xor.rocketwash.util.Constants;
import wash.rocket.xor.rocketwash.util.util;

/**
 * A placeholder fragment containing a simple view.
 */
public class FastRecordFragment extends BaseFragment {

    public static final String TAG = "FastRecordFragment";

    private static final int MINUTES_WAIT = 1;

    private TextView txtCaption;
    private EditText edPhone;
    private EditText edPinCode;
    private Button btnReplyPin;
    private Button btnLogin;
    private Button btnRegister;
    private Button btnSkipRegistration;
    private ProgressBar progressBar;

    private Timer timer;
    private long mLastTime = -1;
    private boolean waiting = false;

    private LoginRequest loginRequest;

    public FastRecordFragment() {
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        // loginRequest = new LoginRequest("");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getView() == null)
            return;

        txtCaption = (TextView) getView().findViewById(R.id.txtCaption);
        edPhone = (EditText) getView().findViewById(R.id.edPhone);
        edPinCode = (EditText) getView().findViewById(R.id.edPinCode);
        btnReplyPin = (Button) getView().findViewById(R.id.btnReplyPin);

        btnLogin = (Button) getView().findViewById(R.id.btnShare);
        btnRegister = (Button) getView().findViewById(R.id.btnRegister);
        btnSkipRegistration = (Button) getView().findViewById(R.id.btnSkipRegistration);

        progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        btnReplyPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();

                if (waiting)
                    return;

                if (TextUtils.isEmpty(edPhone.getText().toString().trim())) {
                    showToastWarn(R.string.fragment_login_phone_empty_error);
                    return;
                }

                waiting = true;
                pref.setLastTimeClick(System.currentTimeMillis());
                createTimer(pref.getLastTimeClick());
                getSpiceManager().execute(new PinRequest(edPhone.getText().toString()), "pin", DurationInMillis.ALWAYS_EXPIRED, new PinRequestListener());
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new RegistrationFragment(), RegistrationFragment.TAG)
                        .addToBackStack(TAG).commit();
            }
        });

        btnSkipRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new WithoutRegistrationFragment(), WithoutRegistrationFragment.TAG)
                        .addToBackStack(TAG).commit();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();

                progressBar.setVisibility(View.VISIBLE);
                getSpiceManager().execute(new LoginRequest(edPhone.getText().toString(), edPinCode.getText().toString()), "login", DurationInMillis.ALWAYS_EXPIRED, new LoginRequestListener());
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
        if (pref.getLastTimeClick() > -1) {
            waiting = true;
            createTimer(pref.getLastTimeClick());
        }
        onKeyBoardHide();
    }

    @TargetApi(3)
    private void hideKeyboard() {
        if (Build.VERSION.SDK_INT < 3) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edPhone.getWindowToken(), 0);
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
        private final WeakReference<FastRecordFragment> mFragment;

        TimerHandler(FastRecordFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            FastRecordFragment fragment = mFragment.get();
            if (fragment != null) {
                fragment.handleTimer(msg);
            }
        }
    }

    private void handleTimer(Message msg) {
        if (getActivity() == null)
            return;
        long t = 60 * MINUTES_WAIT - ((System.currentTimeMillis() - mLastTime) / 1000);
        if (t <= 0) {
            waiting = false;
            stopCalculateTimer();
            pref.setLastTimeClick(-1);
            btnReplyPin.setText(R.string.fragment_login_btn_retry_pin);
        } else
            btnReplyPin.setText(String.format("%s %s", getActivity().getString(R.string.fragment_login_btn_retry_pin_after), util.SecondsToMS(t)));
    }


    @Override
    protected void onKeyBoardHide() {
        txtCaption.setVisibility(View.VISIBLE);
        btnRegister.setVisibility(View.VISIBLE);
        btnSkipRegistration.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onKeyBoardShow() {
        txtCaption.setVisibility(View.GONE);
        btnRegister.setVisibility(View.GONE);
        btnSkipRegistration.setVisibility(View.GONE);
    }

    public final class LoginRequestListener implements RequestListener<LoginResult> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            showToastError(R.string.error_loading_data);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onRequestSuccess(final LoginResult result) {
            progressBar.setVisibility(View.GONE);
            //Toast.makeText(getActivity(), "login success", Toast.LENGTH_SHORT).show();
            Log.d("onRequestSuccess", result.toString());

            if (result != null)
                if (Constants.SUCCESS.equals(result.getStatus())) {
                    pref.setSessionID(result.getData().getSession_id());
                    mCallback.onLogged();
                } else {
                    final int res = getResources().getIdentifier("login_" + result.getData().getResult(), "string", getActivity().getPackageName());
                    String error = res == 0 ? result.getData().getResult() : getString(res);
                    showToastError(error);
                }
        }
    }

    public final class PinRequestListener implements RequestListener<PinResult> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            //Toast.makeText(getActivity(), R.string.request_pin_error, Toast.LENGTH_SHORT).show();
            showToastError(R.string.request_pin_error);
            // progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onRequestSuccess(final PinResult result) {
            // progressBar.setVisibility(View.GONE);
            //Toast.makeText(getActivity(), "login success", Toast.LENGTH_SHORT).show();
            Log.d("onRequestSuccess", result.getStatus() == null ? "null" : result.getStatus());

            if (Constants.SUCCESS.equals(result.getStatus())) {
                //Toast.makeText(getActivity(), R.string.request_pin_success, Toast.LENGTH_SHORT).show();
                showToastOk(R.string.request_pin_success);
            } else {
                // final int res = getResources().getIdentifier("login_" + result.getData().getResult(), "string", getActivity().getPackageName());
                // String error = res == 0 ? result.getData().getResult() : getString(res);
                // Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();

                //XXX сбросить таймер ?
                //Toast.makeText(getActivity(), R.string.request_pin_phone_error, Toast.LENGTH_SHORT).show();
                showToastError(R.string.request_pin_phone_error);
            }
        }
    }

}
