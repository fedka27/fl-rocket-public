package wash.rocket.xor.rocketwash.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
public class LoginQuickFragment extends BaseFragment {

    public static final String TAG = "LoginQuickFragment";
    private static final int MINUTES_WAIT = 1;
    private static final int DIALOG_COUNTRY = 2;
    private static final String DIALOG_COUNTRY_TAG = "DIALOG_COUNTRY";

    private EditText edPhone;
    private EditText edPinCode;
    private Button btnReplyPin;
    private Button btnLogin;
    private LinearLayout caption;
    private ProgressBar progressBar;

    private Timer timer;
    private long mLastTime = -1;
    private boolean waiting = false;

    private EditText edPhoneCode;
    private DialoglistCountries dlg_country;

    public LoginQuickFragment() {
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
        return inflater.inflate(R.layout.fragment_login_quick, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getView() == null)
            return;

        caption = (LinearLayout) getView().findViewById(R.id.caption);
        edPhoneCode = (EditText) getView().findViewById(R.id.edPhoneCode);
        edPhone = (EditText) getView().findViewById(R.id.edPhone);
        edPinCode = (EditText) getView().findViewById(R.id.edPinCode);
        btnReplyPin = (Button) getView().findViewById(R.id.btnReplyPin);

        btnLogin = (Button) getView().findViewById(R.id.btnShare);

        progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        btnReplyPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();

                if (waiting)
                    return;

                if (TextUtils.isEmpty(edPhone.getText().toString().trim())) {
                    Toast.makeText(getActivity(), R.string.fragment_login_phone_empty_error, Toast.LENGTH_LONG).show();
                    return;
                }

                waiting = true;
                pref.setLastTimeClick(System.currentTimeMillis());
                createTimer(pref.getLastTimeClick());
                getSpiceManager().execute(new PinRequest(edPhoneCode.getText().toString() + edPhone.getText().toString()), "pin", DurationInMillis.ALWAYS_EXPIRED, new PinRequestListener());
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();

                progressBar.setVisibility(View.VISIBLE);
                getSpiceManager().execute(new LoginRequest(pref.getLastUsedPhoneCode() + edPhone.getText().toString(), edPinCode.getText().toString()), "login", DurationInMillis.ALWAYS_EXPIRED, new LoginRequestListener());
            }
        });

        edPhoneCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg_country = DialoglistCountries.newInstance();
                dlg_country.setTargetFragment(LoginQuickFragment.this, DIALOG_COUNTRY);
                dlg_country.show(getFragmentManager(), DIALOG_COUNTRY_TAG);
            }
        });

        edPhoneCode.setText(getDefaultPhonePrefix());
        edPhone.setText(pref.getLastUsedPhone());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
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
        private final WeakReference<LoginQuickFragment> mFragment;

        TimerHandler(LoginQuickFragment fragment) {
            mFragment = new WeakReference<LoginQuickFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            LoginQuickFragment fragment = mFragment.get();
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
        caption.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onKeyBoardShow() {
        caption.setVisibility(View.GONE);
    }

    public final class LoginRequestListener implements RequestListener<LoginResult> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            showToastError("Ошибка при авторизации, попробуйте еще раз позже");
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onRequestSuccess(final LoginResult result) {

            progressBar.setVisibility(View.GONE);
            Log.d("onRequestSuccess", (result == null ? "null" : result.toString()));

            if (result != null) {
                if (Constants.SUCCESS.equals(result.getStatus())) {
                    pref.setSessionID(result.getData().getSession_id());
                    pref.setProfile(result.getData().getProfile());
                    if (mCallback != null)
                        mCallback.onLogged();
                } else {
                    final int res = getResources().getIdentifier("login_" + result.getData().getResult(), "string", getActivity().getPackageName());
                    String error = res == 0 ? result.getData().getResult() : getString(res);
                    showToastError(error);
                }
            } else
                showToastError("Ошибка при авторизации, попробуйте еще раз позже");
        }
    }

    public final class PinRequestListener implements RequestListener<PinResult> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            showToastError(R.string.request_pin_error);
            resetPINtimer();
        }

        @Override
        public void onRequestSuccess(final PinResult result) {
            if (result != null && Constants.SUCCESS.equals(result.getStatus())) {
                showToastOk(R.string.request_pin_success);
            } else {
                resetPINtimer();
                showToastError(R.string.request_pin_phone_error);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case DIALOG_COUNTRY:
                    edPhoneCode.setText("+" + data.getStringExtra("id"));
                    pref.setLastUsedPhoneCode("+" + data.getStringExtra("id"));
                    break;
            }
        }
    }

    private void resetPINtimer() {
        waiting = false;
        stopCalculateTimer();
        pref.setLastTimeClick(-1);
        btnReplyPin.setText(R.string.fragment_login_btn_retry_pin);
    }
}
