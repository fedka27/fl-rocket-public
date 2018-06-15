package wash.rocket.xor.rocketwash.services.firebase;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.annotation.DrawableRes;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class NotificationManager {
    private final String CHANNEL_ID = "RocketWash";

    private Context context;

    private int countNotification = 0;

    public NotificationManager(Context context) {
        this.context = context;
    }

    public void showNotification(int id,
                                 @DrawableRes int smallIcon,
                                 @DrawableRes int icon,
                                 String title,
                                 String message,
                                 Intent intentRun) {
        countNotification++;

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);

        taskStackBuilder.addNextIntent(intentRun);

        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(smallIcon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), icon))
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(false)
                .setNumber(countNotification)
                .setFullScreenIntent(pendingIntent, true)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        notificationManagerCompat.notify(id, builder.build());
    }
}
