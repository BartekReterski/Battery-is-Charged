package com.checkyourbattery.batteryischarged.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
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
import com.checkyourbattery.batteryischarged.service.AlarmReceiver;
import com.github.abara.library.batterystats.BatteryStats;
import com.github.florent37.viewtooltip.ViewTooltip;
import com.yarolegovich.lovelydialog.LovelyChoiceDialog;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import abak.tr.com.boxedverticalseekbar.BoxedVertical;
import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

  private SharedPreferences sharedPreferences;
  private SharedPreferences sharedPreferencesCheckboxNotDisch;
  private SharedPreferences sharedPreferencesCheckboxNotPluged;
  int choosen_battery_value;
  boolean check_box_value_not_disch;
  boolean check_box_value_not_pluged;
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
        TextView getDeviceInfoText= findViewById(R.id.getDeviceInfoText);
        getDeviceInfoText.getPaint().setUnderlineText(true);

        getDeviceInfoText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this,DeviceInfoActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        this.menuList = menu;

        //odebranie danych tymczasowych na temat checkboxów z menu
        SharedPreferences sharedPreferences2 = getSharedPreferences("PREFS_2", MODE_PRIVATE);
        check_box_value_not_disch=sharedPreferences2.getBoolean("check_not_disch",false);

        SharedPreferences sharedPreferences3 = getSharedPreferences("PREFS_3", MODE_PRIVATE);
        check_box_value_not_pluged=sharedPreferences3.getBoolean("check_not_pluged",false);

        //Toast.makeText(this,String.valueOf(check_box_value_not_disch),Toast.LENGTH_LONG).show();
        menu.findItem(R.id.itemNotDischOff).setChecked(check_box_value_not_disch);
        menu.findItem(R.id.itemNotPluged).setChecked(check_box_value_not_pluged);

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
                                + "\n"  + "\n" +"Version: "+versionName)
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

                Intent intent= new Intent(this, DeviceInfoActivity.class);
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
                } else {
                    item.setChecked(true);
                    editorCheck.putBoolean("check_not_disch", true);
                    editorCheck.apply();

                    DisChargingNoti();
                }

                return true;

            case R.id.itemNotPluged:

                //ustawienie checkboxa z notyfikacjami

                //wyslanie danych tymczasowych na temat checkboxa
                sharedPreferencesCheckboxNotPluged = getSharedPreferences("PREFS_3", MODE_PRIVATE);
                SharedPreferences.Editor editorCheck2 = sharedPreferencesCheckboxNotPluged.edit();


                if (item.isChecked()) {

                    item.setChecked(false);
                    editorCheck2.putBoolean("check_not_pluged", false);
                    editorCheck2.apply();
                } else {
                    item.setChecked(true);
                    editorCheck2.putBoolean("check_not_pluged", true);
                    editorCheck2.apply();



                }

                return  true;

            default:


                return super.onOptionsItemSelected(item);
        }

    }

    private void BattteryWidgetLogic() {

        final TextView chargingText = findViewById(R.id.chargingText);
        final BoxedVertical batterySeek = findViewById(R.id.seekbar_battery);

        //odebranie danych tymczasowych na temat wybranej wartosci baterii i przypisanie wartości do widgetu baterii
        SharedPreferences sharedPreferences1 = getSharedPreferences("PREFS", MODE_PRIVATE);
        choosen_battery_value=sharedPreferences1.getInt("battery_value",0);

        if(choosen_battery_value==0){
            batterySeek.setValue(20);
        }else{
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
                choosen_battery_value=sharedPreferences1.getInt("battery_value",0);

                chargingText.setText("Charge battery up to " + String.valueOf(choosen_battery_value) + "%");
            }

            @Override
            public void onStartTrackingTouch(BoxedVertical boxedPoints) {

            }

            @Override
            public void onStopTrackingTouch(BoxedVertical boxedPoints) {
                if (choosen_battery_value >= 80) {

                    ViewTooltip
                            .on(MainActivity.this,batterySeek)
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
                choosen_battery_value=sharedPreferences1.getInt("battery_value",0);

                //wyświetlenie informacji na temat tego, że nie można używać aplikacji gdy jest ustawione poniżej 20%
                if(choosen_battery_value<20){
                    menuList.performIdentifierAction(R.id.itemNotifiOff,0);
                }else{

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
                                    if(item.description.equals("System notification")){

                                        Toasty.success(MainActivity.this,"Notification will be shown when battery achieved "+choosen_battery_value+" %",Toast.LENGTH_LONG).show();
                                        //wykonaj metode
                                    }else{

                                        //wykonaj metode pod email

                                        Toasty.success(MainActivity.this,"E-mail will be sent when battery achieved "+choosen_battery_value+" %",Toast.LENGTH_LONG).show();
                                    }
                                }
                            })
                            .show();
                }

            }

        });
    }


    private List<OptionModel> loadChooseOptions() {
        List<OptionModel> result = new ArrayList<>();
        String[] raw = getResources().getStringArray(R.array.options);
        for (String op : raw) {
            String[] info = op.split("%");
            result.add(new OptionModel(info[0]));
        }
        return result;
    }


    public void DisChargingNoti(){

        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Intent
        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        intent.putExtra("notificationId", notificationId);

        // PendingIntent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                MainActivity.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT
        );

        // AlarmManager
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);


        // ustawienie czasu alarmu
        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, mHour);
        startTime.set(Calendar.MINUTE, mMinute);
        startTime.set(Calendar.SECOND, 0);
        long alarmStartTime = startTime.getTimeInMillis();



            // ustawienie alarmu
            Objects.requireNonNull(alarmManager).set(AlarmManager.RTC_WAKEUP, alarmStartTime, pendingIntent);



    }
}

