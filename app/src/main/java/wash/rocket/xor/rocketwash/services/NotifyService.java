package wash.rocket.xor.rocketwash.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import wash.rocket.xor.rocketwash.R;
import wash.rocket.xor.rocketwash.ui.MainActivity;
import wash.rocket.xor.rocketwash.util.util;

/**
 * Сервис нотификации о заказе
 */

public class NotifyService extends Service {
    private static String TAG = "NotifyService";
    private static int NOTIFICATION_ID = 120544;

    public static String NOTIFY = "notify";
    public static String NOTIFY_TIME = "notify_time";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        HandleIntent(intent);
        return START_REDELIVER_INTENT;
    }

    private void HandleIntent(Intent intent) {
        Log.i(TAG, "HandleIntent");

        if (intent == null)
            return;

        if (intent.getIntExtra(NOTIFY, 0) > 0) {
            String time = intent.getStringExtra(NOTIFY_TIME);
            String ftime = util.dateToDMYHM(util.getDateS1(time));
            showNotify(R.drawable.ic_launcher, getApplicationContext().getString(R.string.app_name),
                    getApplicationContext().getString(R.string.order_notify) + " " + ftime);
        }
    }

    public void showNotify(int icon, String title, String message) {
        Context context = getApplicationContext();
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Resources res = context.getResources();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setContentIntent(contentIntent).setSmallIcon(icon)
                .setLargeIcon(BitmapFactory.decodeResource(res, icon))
                .setTicker(message).setWhen(System.currentTimeMillis()).setAutoCancel(true)
                .setContentTitle(title)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentText(message);
        nm.notify(NOTIFICATION_ID, builder.build());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
