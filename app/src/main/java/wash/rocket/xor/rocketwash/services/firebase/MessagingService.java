package wash.rocket.xor.rocketwash.services.firebase;

import android.content.Intent;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import wash.rocket.xor.rocketwash.R;
import wash.rocket.xor.rocketwash.model.NotificationData;
import wash.rocket.xor.rocketwash.ui.MainActivity;
import wash.rocket.xor.rocketwash.util.Preferences;

public class MessagingService extends FirebaseMessagingService {
    private static final String TAG = MessagingService.class.getSimpleName();
    private final int NOTIFICATION_ID = 245;
    private Preferences preferences;
    private NotificationManager notificationManager;
//    private ObjectMapper objectMapper;

    public MessagingService() {
        this.notificationManager = new NotificationManager(this);
        this.preferences = new Preferences(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);
        //todo message

        NotificationData notificationData = null;

        try {
            JSONObject jsonObject = new JSONObject(remoteMessage.getData());
            notificationData = new NotificationData(
                    jsonObject.getString("title"),
                    jsonObject.getString("message")
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
//                objectMapper.readValue(remoteMessage.getData().get("data"), NotificationData.class);

        if (notificationData != null && preferences.getSessionID() != null) {

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(MainActivity.EXTRA_NOTIFICATION, notificationData);

            notificationManager.showNotification(NOTIFICATION_ID,
                    R.drawable.ic_notification_small,
                    R.drawable.ic_launcher,
                    notificationData.getTitle(),
                    notificationData.getMessage(),
                    intent);
        }
    }
}
