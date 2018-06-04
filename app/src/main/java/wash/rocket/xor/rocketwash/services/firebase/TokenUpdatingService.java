package wash.rocket.xor.rocketwash.services.firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class TokenUpdatingService extends FirebaseInstanceIdService {

    private static final String TAG = TokenUpdatingService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String refreshedToken = FirebaseInstanceId.getInstance().getId();
        Log.d(TAG, "Firebase token updated: " + refreshedToken);

        registerTokenToServer(refreshedToken);
    }

    private void registerTokenToServer(String refreshedToken) {
        //todo register device to server
    }
}
