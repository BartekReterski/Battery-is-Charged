package com.checkyourbattery.batteryischarged;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yarolegovich.lovelydialog.LovelyChoiceDialog;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import abak.tr.com.boxedverticalseekbar.BoxedVertical;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       BattteryWidgetLogic();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.itemInfo:

                new LovelyInfoDialog(this)
                        .setTopColorRes(R.color.colorPrimary)
                        .setIcon(R.drawable.info)
                        //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
                        .setTitle("Battery is charged")
                        .setMessage("Charge battery up to chosen percent. Set your charging value and preferable notification method. Get info when battery is charged")
                        .show();

                return true;

            case R.id.notifiOff:

                new LovelyInfoDialog(this)
                        .setTopColorRes(R.color.colorWarning)
                        .setIcon(R.drawable.notification_off)
                        //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
                        .setTitle("Notifications disabled")
                        .setMessage("Notification are disabled for charging level under 20%")
                        .show();
                return  true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }



        private void BattteryWidgetLogic() {

            final TextView chargingText = findViewById(R.id.chargingText);
            final BoxedVertical batterySeek = findViewById(R.id.seekbar_battery);

            batterySeek.setOnBoxedPointsChangeListener(new BoxedVertical.OnValuesChangeListener() {
                @Override
                public void onPointsChanged(BoxedVertical boxedPoints, final int value) {
                    System.out.println(value);
                    if(value==80){
                        Toast.makeText(getApplicationContext(),"This best charging value",Toast.LENGTH_LONG).show();
                    }
                    if(value<20){
                        Toast.makeText(getApplicationContext(),"Notification are disabled for charging level under 20%",Toast.LENGTH_SHORT).show();
                        //disabled buttona i ikonka notyfikacji na pasku pawiadomien z przekresloną kreską zeby toast sie nie pokazywał caly czas
                    }
                    chargingText.setText("Charge battery up to " + String.valueOf(value) + "%");
                }

                @Override
                public void onStartTrackingTouch(BoxedVertical boxedPoints) {
                    //Toast.makeText(MainActivity.this, "onStartTrackingTouch", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onStopTrackingTouch(BoxedVertical boxedPoints) {
                    //Toast.makeText(MainActivity.this, "onStopTrackingTouch", Toast.LENGTH_SHORT).show();
                }
            });
        }

        public void NotificationLogic(){

            Button buttonNotification= findViewById(R.id.buttonNotification);

            buttonNotification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                  /*  ArrayAdapter<DonationOption> adapter = new DonationAdapter(this, loadDonationOptions());
                    new LovelyChoiceDialog(this)
                            .setTopColorRes(R.color.darkGreen)
                            .setTitle(R.string.donate_title)
                            .setIcon(R.drawable.ic_local_atm_white_36dp)
                            .setMessage(R.string.donate_message)
                            .setItems(adapter, new LovelyChoiceDialog.OnItemSelectedListener<DonationOption>() {
                                @Override
                                public void onItemSelected(int position, DonationOption item) {
                                    Toast.makeText(context, getString(R.string.you_donated, item.amount),Toast.LENGTH_SHORT).show();
                                }
                            })
                            .show();*/

                }
            });
        }

}
