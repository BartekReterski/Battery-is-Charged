package com.checkyourbattery.batteryischarged.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.checkyourbattery.batteryischarged.BuildConfig;
import com.checkyourbattery.batteryischarged.R;
import com.checkyourbattery.batteryischarged.adapter.ChooseOptionAdapter;
import com.checkyourbattery.batteryischarged.models.OptionModel;
import com.checkyourbattery.batteryischarged.service.ChargingReceiver;
import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.github.florent37.viewtooltip.ViewTooltip;
import com.yarolegovich.lovelydialog.LovelyChoiceDialog;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import abak.tr.com.boxedverticalseekbar.BoxedVertical;
import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences sharedPreferencesCheckboxNotDisch;
    private SharedPreferences sharedPreferencesNotificatioData;
    int choosen_battery_value;
    boolean check_box_value_not_disch;
    private Menu menuList;
    private int notificationId = 1;
    private int notificationId2 = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BattteryWidgetLogic();
        NotificationLogic();


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

        //usuniecie notyfikacji z receivera na samym powiadomieniu
        if (getIntent().hasExtra("deleteNotification")) {
            DeleteNotification();
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Objects.requireNonNull(notificationManager).cancel(notificationId);
            Objects.requireNonNull(notificationManager).cancel(notificationId2);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        this.menuList = menu;

        //odebranie danych tymczasowych na temat checkboxów z menu
        SharedPreferences sharedPreferences2 = getSharedPreferences("PREFS_2", MODE_PRIVATE);
        check_box_value_not_disch = sharedPreferences2.getBoolean("check_not_disch", false);

        //Toast.makeText(this,String.valueOf(check_box_value_not_disch),Toast.LENGTH_LONG).show();
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

                chargingText.setText("Charge battery up to " + String.valueOf(choosen_battery_value) + "%");
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
                            .setMessage("Choose your preferable notification option and get info when battery is charged, based on your previous chosen value")
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

                                        creatNotificationEmail();

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
        String index = sharedPreferencesN.getString("alarm_value", "");

        List<OptionModel> result = new ArrayList<>();
        String[] raw = new String[2];
        raw[0]="System notification/ "+index;
        raw[1]="Send e-mail/1$";
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

            Toasty.success(MainActivity.this, "Notification will be shown when battery achieved " + choosen_battery_value + " %", Toast.LENGTH_LONG).show();

            //wyslanie danych tymczasowych na temat wykonanej logiki notyfikacji
            sharedPreferencesNotificatioData = getSharedPreferences("PREFS_3", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferencesNotificatioData.edit();
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

            alarmManager.cancel(pendingIntent);

            //usuniecie danych tymczasowych na temat wykonanej logiki notyfikacji
            sharedPreferencesNotificatioData = getSharedPreferences("PREFS_3", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferencesNotificatioData.edit();
            editor.remove("alarm_value");
            editor.apply();



        } catch (Exception ex) {

            System.out.println(ex.getMessage());
        }

    }

    private void creatNotificationEmail(){
        BackgroundMail.newBuilder(this)
                .withUsername("spammejl94@gmail.com")
                .withPassword("bobmarley20")
                .withMailto("spammejl94@gmail.com")
                .withType(BackgroundMail.TYPE_PLAIN)
                .withSubject("SWIEZY MEJL")
                .withBody("DUPA DUPA DUPA")
                .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                    @Override
                    public void onSuccess() {
                        //do some magic
                        Toasty.success(MainActivity.this, "E-mail will be sent when battery achieved " + choosen_battery_value + " %", Toast.LENGTH_LONG).show();
                    }
                })
                .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                    @Override
                    public void onFail() {
                        Toasty.info(MainActivity.this,"Can't send email",Toast.LENGTH_LONG).show();
                    }
                })
                .send();

    }

}



