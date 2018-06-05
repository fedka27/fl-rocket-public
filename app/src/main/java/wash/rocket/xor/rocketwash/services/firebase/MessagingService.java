package wash.rocket.xor.rocketwash.services.firebase;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import wash.rocket.xor.rocketwash.R;

public class MessagingService extends FirebaseMessagingService {

    private final int NOTIFICATION_ID = 245;
    private NotificationManager notificationManager;

    public MessagingService() {
        this.notificationManager = new NotificationManager(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        //todo message
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        if (notification != null) {

            notificationManager.showNotification(NOTIFICATION_ID,
                    R.drawable.ic_action_notifications,
                    notification.getTitle(),
                    notification.getBody());
        }
    }
}
