package com.checkyourbattery.batteryischarged.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.SystemClock;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.checkyourbattery.batteryischarged.R;
import com.checkyourbattery.batteryischarged.activities.MainActivity;
import com.github.abara.library.batterystats.BatteryStats;

import java.util.Calendar;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

import static android.content.Context.BATTERY_SERVICE;
import static android.content.Context.MODE_PRIVATE;


public class AlertReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "CHANNEL_SAMPLE";


    @Override
    public void onReceive(Context context, Intent intent) {

        //odebranie danych tymczasowych na temat baterii
        int notificationId = intent.getIntExtra("notificationId", 0);
        int choosenBatteryValue = intent.getIntExtra("choosen_battery_value", 0);



        //pobranie danych na temat procentu baterii
        BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
        assert bm != null;
        int battery_level = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);





      // uruchomienie activity z powiadomienia
        Intent mainIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, mainIntent, 0);

        //uruchomienie powiadomienia gdy zgadza się wartośc baterii oraz ładuje się telefon
        if(choosenBatteryValue==battery_level) {

            Toast.makeText(context,"DZIALA",Toast.LENGTH_SHORT).show();

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
                    .setSmallIcon(R.drawable.notification_mini_icon)
                    .setContentTitle("Battery info update")
                    .setContentText("Battery achieved chosen value "+battery_level+"%")
                    .setContentIntent(contentIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);


            Objects.requireNonNull(notificationManager).notify(notificationId, builder.build());

        }

    }

}
