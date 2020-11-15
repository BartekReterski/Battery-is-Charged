package com.checkyourbattery.batteryischarged.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.checkyourbattery.batteryischarged.BuildConfig;
import com.checkyourbattery.batteryischarged.R;
import com.checkyourbattery.batteryischarged.adapter.ChooseOptionAdapter;
import com.checkyourbattery.batteryischarged.models.OptionModel;
import com.checkyourbattery.batteryischarged.service.ChargingReceiver;
import com.github.florent37.viewtooltip.ViewTooltip;
import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.banners.IUnityBannerListener;
import com.unity3d.services.banners.UnityBanners;
import com.yarolegovich.lovelydialog.LovelyChoiceDialog;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import abak.tr.com.boxedverticalseekbar.BoxedVertical;
import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences sharedPreferencesCheckboxNotDisch;
    private SharedPreferences sharedPreferencesNotificatioDataSystemAlert;
    private SharedPreferences sharedPreferencesNotificatioDataEmail;
    int choosen_battery_value;
    boolean check_box_value_not_disch;
    private Menu menuList;
    private int notificationId = 1;
    private int notificationId2 = 1;

    //unity data
    private String unityGameId="3902031";
    private String bannerId="banner_ad";
    private Boolean testMode=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BattteryWidgetLogic();
        NotificationLogic();

        //usuniecie notyfikacji z receivera na samym powiadomieniu
        if (getIntent().hasExtra("deleteNotification")) {
            DeleteNotification();
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Objects.requireNonNull(notificationManager).cancel(notificationId);
            Objects.requireNonNull(notificationManager).cancel(notificationId2);

        }

        //zainicjowanie reklam Unity Ads( dodanie testowego modułu reklam aplikacji)
        UnityAds.initialize(MainActivity.this,unityGameId,testMode);
        UnityBanners.loadBanner(MainActivity.this,bannerId);

        IUnityBannerListener iUnityBannerListener= new IUnityBannerListener() {
            @Override
            public void onUnityBannerLoaded(String s, View view) {

                ((ViewGroup)findViewById(R.id.banner_ads_view)).removeView(view);
                ((ViewGroup)findViewById(R.id.banner_ads_view)).addView(view);
            }

            @Override
            public void onUnityBannerUnloaded(String s) {

            }

            @Override
            public void onUnityBannerShow(String s) {

            }

            @Override
            public void onUnityBannerClick(String s) {

            }

            @Override
            public void onUnityBannerHide(String s) {

            }

            @Override
            public void onUnityBannerError(String s) {
                UnityBanners.loadBanner(MainActivity.this,bannerId);
            }
        };
        UnityBanners.setBannerListener(iUnityBannerListener);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        this.menuList = menu;

        //odebranie danych tymczasowych na temat checkboxów z menu
        SharedPreferences sharedPreferences2 = getSharedPreferences("PREFS_2", MODE_PRIVATE);
        check_box_value_not_disch = sharedPreferences2.getBoolean("check_not_disch", false);

        menu.findItem(R.id.itemNotDischOff).setChecked(check_box_value_not_disch);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.itemInfo:

                String versionName = BuildConfig.VERSION_NAME;
                new LovelyInfoDialog(this)
                        .setTopColorRes(R.color.colorPrimary)
                        .setIcon(R.drawable.info)
                        .setTitle("Battery is charged")
                        .setMessage("Charge battery up to chosen percent. Set your charging value and preferable notification method. Get info when battery is charged"
                                + "\n" + "\n" + "Version: " + versionName)
                        .show();

                return true;

            case R.id.itemNotifiOff:

                new LovelyInfoDialog(this)
                        .setTopColorRes(R.color.colorWarning)
                        .setIcon(R.drawable.notification_off)
                        .setTitle("Notifications disabled")
                        .setMessage("Notifications are disabled for charging level under 20%")
                        .show();
                return true;

            case R.id.itemDeviceInfo:

                Intent intent = new Intent(this, DeviceInfoActivity.class);
                startActivity(intent);
                return true;

            case R.id.itemNotDischOff:
                //ustawienie checkboxa z notyfikacjami

                //wyslanie danych tymczasowych na temat checkboxa
                sharedPreferencesCheckboxNotDisch = getSharedPreferences("PREFS_2", MODE_PRIVATE);
                SharedPreferences.Editor editorCheck = sharedPreferencesCheckboxNotDisch.edit();


                if (item.isChecked()) {

                    item.setChecked(false);
                    editorCheck.putBoolean("check_not_disch", false);
                    editorCheck.apply();
                    DeleteNotification();
                    Toasty.info(MainActivity.this, "Notification removed", Toasty.LENGTH_LONG).show();


                } else {
                    item.setChecked(true);
                    editorCheck.putBoolean("check_not_disch", true);
                    editorCheck.apply();
                    createNotificationChannel();


                }

                return true;



            default:


                return super.onOptionsItemSelected(item);
        }

    }

    private void BattteryWidgetLogic() {

        //przypisanie przejscia do aktywnosci dla tekstu
        TextView getDeviceInfoText = findViewById(R.id.getDeviceInfoText);
        getDeviceInfoText.getPaint().setUnderlineText(true);

        getDeviceInfoText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DeviceInfoActivity.class);
                startActivity(intent);
            }
        });

        final TextView chargingText = findViewById(R.id.chargingText);
        final BoxedVertical batterySeek = findViewById(R.id.seekbar_battery);

        //odebranie danych tymczasowych na temat wybranej wartosci baterii i przypisanie wartości do widgetu baterii
        SharedPreferences sharedPreferences1 = getSharedPreferences("PREFS", MODE_PRIVATE);
        choosen_battery_value = sharedPreferences1.getInt("battery_value", 0);

        if (choosen_battery_value == 0) {
            batterySeek.setValue(20);
        } else {
            batterySeek.setValue(choosen_battery_value);
        }


        batterySeek.setOnBoxedPointsChangeListener(new BoxedVertical.OnValuesChangeListener() {
            @Override
            public void onPointsChanged(BoxedVertical boxedPoints, final int value) {

                //wyslanie danych tymczasowych na temat wybranej wartosci baterii
                sharedPreferences = getSharedPreferences("PREFS", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("battery_value", value);
                editor.apply();

                //odebranie danych tymczasowych na temat wybranej wartosci baterii
                SharedPreferences sharedPreferences1 = getSharedPreferences("PREFS", MODE_PRIVATE);
                choosen_battery_value = sharedPreferences1.getInt("battery_value", 0);

                chargingText.setText("Charge the battery up to " + String.valueOf(choosen_battery_value) + "%");
            }

            @Override
            public void onStartTrackingTouch(BoxedVertical boxedPoints) {

            }

            @Override
            public void onStopTrackingTouch(BoxedVertical boxedPoints) {
                if (choosen_battery_value >= 80) {

                    ViewTooltip
                            .on(MainActivity.this, batterySeek)
                            .autoHide(true, 3500)
                            .corner(30)
                            .position(ViewTooltip.Position.RIGHT)
                            .text("To increase battery lifespan charge it is recommended to charge it up to the 80%")
                            .show();
                }

            }
        });
    }

    public void NotificationLogic() {

        Button buttonNotification = findViewById(R.id.buttonNotification);

        buttonNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //odebranie danych tymczasowych na temat wybranej wartosci baterii i przypisanie wartości do widgetu baterii
                SharedPreferences sharedPreferences1 = getSharedPreferences("PREFS", MODE_PRIVATE);
                choosen_battery_value = sharedPreferences1.getInt("battery_value", 0);

                //wyświetlenie informacji na temat tego, że nie można używać aplikacji gdy jest ustawione poniżej 20%
                if (choosen_battery_value < 20) {
                    menuList.performIdentifierAction(R.id.itemNotifiOff, 0);
                } else {

                    ArrayAdapter<OptionModel> adapter = new ChooseOptionAdapter(MainActivity.this, loadChooseOptions());
                    new LovelyChoiceDialog(MainActivity.this)
                            .setTopColorRes(R.color.chooseOption)
                            .setTitle("Choose notification option")
                            .setIcon(R.drawable.notification_on)
                            .setMessage("Choose the notification option and get info when battery is charged, based on your previous chosen value")
                            .setItems(adapter, new LovelyChoiceDialog.OnItemSelectedListener<OptionModel>() {
                                @Override
                                public void onItemSelected(int position, OptionModel item) {
                                    // Toast.makeText(MainActivity.this, item.amount),Toast.LENGTH_SHORT).show();
                                    if (item.description.equals("System notification")) {
                                        Intent intent = new Intent(MainActivity.this, ChargingReceiver.class);
                                        intent.putExtra("choosen_battery_value", choosen_battery_value);
                                        intent.putExtra("notificationId", notificationId);
                                        intent.putExtra("notificationId2", notificationId2);
                                        intent.putExtra("check_box_on_dis", check_box_value_not_disch);
                                        intent.setAction("BackgroundProcess");

                                        //sprawdzenie czy jest ustawiona już notyfikacja
                                        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_NO_CREATE);

                                        if (pendingIntent!=null){

                                            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                                            alertDialogBuilder.setMessage("Notification is already set. Do you want to delete it and set new one?");
                                                    alertDialogBuilder.setPositiveButton("Set notification",
                                                            new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface arg0, int arg1) {

                                                                    DeleteNotification();
                                                                    createNotificationChannel();
                                                                }
                                                            });

                                                    alertDialogBuilder.setNegativeButton("Remove notification", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                            DeleteNotification();
                                                            Toasty.info(MainActivity.this, "Notification removed", Toasty.LENGTH_LONG).show();
                                                        }
                                                    });

                                            AlertDialog alertDialog = alertDialogBuilder.create();
                                            alertDialog.show();

                                        }else{

                                            createNotificationChannel();
                                        }

                                    } else {

                                        //creatNotificationEmail();

                                    }
                                }
                            })
                            .show();
                }

            }

        });
    }

    private List<OptionModel> loadChooseOptions() {

        //odebranie danych tymczasowych na temat wykonanej logiki notyfikacji
        SharedPreferences sharedPreferencesN = getSharedPreferences("PREFS_3", MODE_PRIVATE);
        String index_Alert = sharedPreferencesN.getString("alarm_value", "");

        //odebranie danych tymczasowych na temat wykonanej logiki notyfikacji
        SharedPreferences sharedPreferencesNN = getSharedPreferences("PREFS_4", MODE_PRIVATE);
        String index_Email = sharedPreferencesNN.getString("alarm_value_email", "");

        List<OptionModel> result = new ArrayList<>();
        String[] raw = new String[1];
        raw[0]="System notification/ "+index_Alert;
        //raw[1]="Send e-mail/ "+index_Email;
        for (String op : raw) {
            String[] info = op.split("/");
            result.add(new OptionModel(info[0],info[1]));
        }
        return result;
    }

    private void createNotificationChannel() {

        //odebranie danych tymczasowych na temat checkboxów z menu
        SharedPreferences sharedPreferences2 = getSharedPreferences("PREFS_2", MODE_PRIVATE);
        check_box_value_not_disch = sharedPreferences2.getBoolean("check_not_disch", false);

        try {
            //alarm podczas ładowania
            Intent intent = new Intent(this, ChargingReceiver.class);
            intent.putExtra("choosen_battery_value", choosen_battery_value);
            intent.putExtra("notificationId", notificationId);
            intent.putExtra("notificationId2", notificationId2);
            intent.putExtra("check_box_on_dis", check_box_value_not_disch);
            intent.setAction("BackgroundProcess");



            //Ustawienia alertu
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            assert alarmManager != null;

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 0, 10, pendingIntent);

            Toasty.success(MainActivity.this, "The notification will be shown when battery reaches " + choosen_battery_value + " %", Toast.LENGTH_LONG).show();

            //wyslanie danych tymczasowych na temat wykonanej logiki notyfikacji
            sharedPreferencesNotificatioDataSystemAlert = getSharedPreferences("PREFS_3", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferencesNotificatioDataSystemAlert.edit();
            editor.putString("alarm_value", "Notification: " +choosen_battery_value +"%");
            editor.apply();



        } catch (Exception ex) {

            System.out.println(ex.getMessage());
        }
    }

    private void DeleteNotification(){


        try {

            Intent intent = new Intent(this, ChargingReceiver.class);
            intent.putExtra("choosen_battery_value", choosen_battery_value);
            intent.putExtra("notificationId", notificationId);
            intent.putExtra("notificationId2", notificationId2);
            intent.putExtra("check_box_on_dis", check_box_value_not_disch);
            intent.setAction("BackgroundProcess");

            //Usuniecie notyfikacji
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            assert alarmManager != null;
            pendingIntent.cancel();

            alarmManager.cancel(pendingIntent);

            //usuniecie danych tymczasowych na temat wykonanej logiki notyfikacji
            sharedPreferencesNotificatioDataSystemAlert = getSharedPreferences("PREFS_3", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferencesNotificatioDataSystemAlert.edit();
            editor.remove("alarm_value");
            editor.apply();



        } catch (Exception ex) {

            System.out.println(ex.getMessage());
        }

    }

    //wysyłanie emejli onlinne w tle
    private void creatNotificationEmail(){

        try {

            //stworzenie booleana z informacja na true o tym, że dany email jest ustawiony. Dalej w LogicNotifcation()- if boolean jest true zmieniamy na false i false nie wlacza alarmu w receiverze

            //zadeklarowanie alert dialogu z konfiguracja danych emailowych do wysyłania wiadomości w tle
            ViewGroup viewGroup = findViewById(android.R.id.content);
            View dialogView = LayoutInflater.from(this).inflate(R.layout.email_config_layout, viewGroup, false);
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setView(dialogView);
            final AlertDialog alertDialog = builder.create();
            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.show();

            final EditText editEmail = alertDialog.findViewById(R.id.edit_email);
            final EditText editPassword = alertDialog.findViewById(R.id.edit_password);
            Button emailButton = alertDialog.findViewById(R.id.email_button);

            emailButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String email = editEmail.getText().toString();
                    String password = editPassword.getText().toString();

                /*    //wysłanie danych z formularza do receivera e-mail
                   *//* Intent intentEmail = new Intent(MainActivity.this, EmailReceiver.class);
                    intentEmail.putExtra("choosen_battery_valueEmail", choosen_battery_value);
                    intentEmail.putExtra("email", email);
                    intentEmail.putExtra("password", password);
                    intentEmail.setAction("BackgroundProcessEmail");*//*

                    //zadeklarowanie alarm managera, który uruchomi wysłanie mejla i przesłanie żądania do receivera
                   *//* PendingIntent pendingIntent=  PendingIntent.getBroadcast(MainActivity.this,0,intentEmail,0);
                    AlarmManager alarmManager= (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                    assert alarmManager != null;
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,0,10,pendingIntent);*//*
*/
                    Toasty.success(MainActivity.this, "An e-mail will be sent when battery reaches " + choosen_battery_value + " %", Toasty.LENGTH_LONG).show();

                    alertDialog.dismiss();

                }
            });


            //zadeklarowanie alertu z dodatkowa informacją na temat konnfiguracji Gmaila przez użytkownika
            ImageView infoEmail = alertDialog.findViewById(R.id.info_email);
            infoEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewGroup viewGroup = findViewById(android.R.id.content);
                    View dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.email_info_dialog, viewGroup, false);
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setView(dialogView);
                    final AlertDialog alertDialog = builder.create();
                    Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertDialog.show();

                    ImageView infoAlertClose = alertDialog.findViewById(R.id.info_alert_close);
                    infoAlertClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });

                }
            });


            //wyslanie danych tymczasowych na temat wykonanej logiki notyfikacji
            sharedPreferencesNotificatioDataEmail = getSharedPreferences("PREFS_4", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferencesNotificatioDataEmail.edit();
            editor.putString("alarm_value_email", "Notification: " + choosen_battery_value + "%");
            editor.apply();

        }catch (Exception ex){

            System.out.println(ex.getMessage());
        }
    }

}



