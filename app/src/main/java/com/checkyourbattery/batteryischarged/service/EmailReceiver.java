package com.checkyourbattery.batteryischarged.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Build;
import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;

import es.dmoral.toasty.Toasty;

import static android.content.Context.BATTERY_SERVICE;

public class EmailReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {

        //odebranie informacji z activity
        final int choosenBatteryValue = intent.getIntExtra("choosen_battery_valueEmail", 0);
        String email=intent.getStringExtra("email");
        String password=intent.getStringExtra("password");


        //pobranie danych na temat procentu baterii
        BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
        assert bm != null;
        int battery_level = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        int battery_status=bm.getIntProperty(BatteryManager.BATTERY_STATUS_CHARGING);


        //sprawdzenie czy wartość wybrana zgadza się z obecną wartośćią baterii
        if(battery_level==choosenBatteryValue) {
            String deviceModel = Build.MANUFACTURER
                    + " " + Build.MODEL;

            BackgroundMail.newBuilder(context)
                    .withUsername(email)
                    .withPassword(password)
                    .withSenderName("Battery is Charged")
                    .withMailTo("spammejl94@gmail.com")
                    .withType(BackgroundMail.TYPE_PLAIN)
                    .withSubject("Your device " + deviceModel +" achieved the chosen battery value of " + battery_level + "%")
                    .withBody("")
                    .withSendingMessage("Sending email")
                    .withOnSuccessCallback(new BackgroundMail.OnSendingCallback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onFail(Exception e) {

                            Toasty.error(context, "E-mail sent error" + e.getMessage(), Toasty.LENGTH_LONG).show();
                        }
                    })
                    .send();

        }
    }
}
