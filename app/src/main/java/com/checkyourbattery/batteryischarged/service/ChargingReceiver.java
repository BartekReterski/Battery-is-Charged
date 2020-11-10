package com.checkyourbattery.batteryischarged.service;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Build;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;
import com.checkyourbattery.batteryischarged.R;
import com.checkyourbattery.batteryischarged.activities.MainActivity;
import java.util.Objects;
import static android.content.Context.BATTERY_SERVICE;



public class ChargingReceiver extends BroadcastReceiver {

private static final String CHANNEL_ID = "CHANNEL_SAMPLE";
private static final String CHANNEL_ID_2 = "CHANNEL_SAMPLE2";

    @Override
    public void onReceive(Context context, Intent intent) {


        //odebranie danych tymczasowych na temat baterii
        int notificationId = intent.getIntExtra("notificationId", 0);
        int notificationId2 = intent.getIntExtra("notificationId2", 0);
        int choosenBatteryValue = intent.getIntExtra("choosen_battery_value", 0);
        boolean checkboxNotDisch=intent.getBooleanExtra("check_box_on_dis",false);


        //pobranie danych na temat procentu baterii
        BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
        assert bm != null;
        int battery_level = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        int battery_status=bm.getIntProperty(BatteryManager.BATTERY_STATUS_CHARGING);

       // Toast.makeText(context,battery_level + "  "+batter_status,Toast.LENGTH_LONG).show();


        // uruchomienie activity z powiadomienia
        Intent mainIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, mainIntent, 0);

        //usuniecie notyfikacji
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.putExtra("deleteNotification", true);
        PendingIntent pendingIntentDelete = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        //sprawdzanie czy wartość  jest dodatnia i na tej podstawie wyświetlenie odpowiedniej notyfikacji
        if(battery_status>0) {

            //powiadomienia dla ładowania baterii
            if (choosenBatteryValue == battery_level) {

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
                        .setContentText("Battery achieved chosen value " + battery_level + "%")
                        .setContentIntent(contentIntent)
                        .setVibrate(new long[3000])
                        .addAction(R.drawable.clear,"Remove",pendingIntentDelete)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);


                Objects.requireNonNull(notificationManager).notify(notificationId, builder.build());

            }

        }
        //jesli wartości energi w telefonie jest na minusie oraz wartość checkboxaDischarge jest na true to wykonuje tą drugą notyfikacje
        if(battery_status<0 && checkboxNotDisch){

            //powiadomienie dla rozładowywania baterii
            if (choosenBatteryValue == battery_level) {

                // inicjalizacja notificationManager
                NotificationManager notificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    CharSequence channelName = "My Notification2";
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;

                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID_2, channelName, importance);
                    Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
                }


                // przygotowanie powiadomienia(konfiguracja)
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_2)
                        .setSmallIcon(R.drawable.notification_mini_icon)
                        .setContentTitle("Battery info update")
                        .setContentText("Battery achieved chosen value " + battery_level + "%")
                        .setContentIntent(contentIntent)
                        .setVibrate(new long[3000])
                        .addAction(R.drawable.clear,"Remove",pendingIntentDelete)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);


                Objects.requireNonNull(notificationManager).notify(notificationId2, builder.build());

            }

        }

    }


}
