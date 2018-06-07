package wash.rocket.xor.rocketwash.services.firebase;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.PendingRequestListener;

import wash.rocket.xor.rocketwash.model.ProfileResult;
import wash.rocket.xor.rocketwash.requests.MessageTokenRequest;
import wash.rocket.xor.rocketwash.services.JacksonGoogleHttpClientSpiceServiceEx;
import wash.rocket.xor.rocketwash.util.Preferences;

public class TokenUpdatingService extends FirebaseInstanceIdService {

    private static final String TAG = TokenUpdatingService.class.getSimpleName();

    private SpiceManager spiceManager = new SpiceManager(JacksonGoogleHttpClientSpiceServiceEx.class);

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Firebase token updated: " + refreshedToken);

        registerTokenToServer(refreshedToken);
    }

    private void registerTokenToServer(String refreshedToken) {
        Preferences preferences = new Preferences(this);
        String userToken = preferences.getSessionID();

        if (userToken == null) {
            Log.d(TAG, "User token is null. Device didn't registered on the server");
            return;
        }

        if (!spiceManager.isStarted()) {
            spiceManager.start(this);
        }

        spiceManager.execute(
                new MessageTokenRequest(userToken, refreshedToken),
                null,
                DurationInMillis.ALWAYS_EXPIRED,
                new PendingRequestListener<ProfileResult>() {
                    @Override
                    public void onRequestNotFound() {
                        Log.e(TAG, "Token update request not found");
                    }

                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                        Log.e(TAG, "Token update exception");
                        spiceException.printStackTrace();
                    }

                    @Override
                    public void onRequestSuccess(ProfileResult profileResult) {
                        Log.d(TAG, "Token refresh successful");
                    }
                });
    }

    @Override
    public boolean stopService(Intent name) {
        if (spiceManager.isStarted()) {
            spiceManager.shouldStop();
        }
        return super.stopService(name);
    }
}
