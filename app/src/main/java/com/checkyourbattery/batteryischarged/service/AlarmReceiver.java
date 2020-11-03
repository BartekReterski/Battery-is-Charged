package com.checkyourbattery.batteryischarged.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.checkyourbattery.batteryischarged.R;
import com.checkyourbattery.batteryischarged.activities.MainActivity;
import com.github.abara.library.batterystats.BatteryStats;

import java.util.Calendar;
import java.util.Objects;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "CHANNEL_SAMPLE";
    int battery_pluged_state;
    int battery_level;
    boolean battery_is_charging;



    @Override
    public void onReceive(Context context, Intent intent) {

        // pobranie danych z intenta z Notifications
        int notificationId = intent.getIntExtra("notificationId", 0);

        //dane z biblioteki na temat obecnego stanu baterii
        BatteryStats batteryStats= new BatteryStats(intent);

        battery_pluged_state= batteryStats.getPluggedState();
        battery_is_charging=batteryStats.isCharging();
        battery_level=batteryStats.getLevel();

        // uruchomienie activity z powiadomienia
        Intent mainIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, mainIntent, 0);

        // inicjalizacja notificationManager
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            CharSequence channelName = "My Notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
        }


        // przygotowanie powiadomienia(konfiguracja)
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Charger is pluged")
                .setContentText("Do you want to set charging value?")
                .setContentIntent(contentIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);


     Objects.requireNonNull(notificationManager).notify(notificationId, builder.build());

    }

}
