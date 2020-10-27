package com.checkyourbattery.batteryischarged;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

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
                        .setNotShowAgainOptionEnabled(0)
                        .setNotShowAgainOptionChecked(true)
                        .setTitle("Battery is charged")
                        .setMessage("Charge battery up to chosen percent. Set your charging value and set your preferable notification method. Get info when battery is charged")
                        .show();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }



        private void BattteryWidgetLogic() {

            final TextView chargingText = findViewById(R.id.chargingText);
            BoxedVertical batterySeek = findViewById(R.id.seekbar_battery);

            batterySeek.setOnBoxedPointsChangeListener(new BoxedVertical.OnValuesChangeListener() {
                @Override
                public void onPointsChanged(BoxedVertical boxedPoints, final int value) {
                    System.out.println(value);
                    chargingText.setText("Charge battery up to " + String.valueOf(value) + "%");
                }

                @Override
                public void onStartTrackingTouch(BoxedVertical boxedPoints) {
                    Toast.makeText(MainActivity.this, "onStartTrackingTouch", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onStopTrackingTouch(BoxedVertical boxedPoints) {
                    Toast.makeText(MainActivity.this, "onStopTrackingTouch", Toast.LENGTH_SHORT).show();
                }
            });
        }

}
