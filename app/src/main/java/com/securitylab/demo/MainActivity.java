package com.securitylab.demo;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.*;

public class MainActivity extends Activity {

    private static final String DEMO_PIN = "1234";

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        showDemo();
    }

    private void showDemo() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(32, 40, 32, 40);
        layout.setBackgroundColor(Color.rgb(28, 12, 18));

        TextView title = new TextView(this);
        title.setText("SECURITY LAB");
        title.setTextColor(Color.WHITE);
        title.setTextSize(30);
        title.setGravity(Gravity.CENTER);

        TextView warning = new TextView(this);
        warning.setText(
            "SIMULATED SECURITY INCIDENT\n\n" +
            "This is an educational lock-screen demonstration.\n" +
            "Your files and Android security settings are untouched."
        );
        warning.setTextColor(Color.WHITE);
        warning.setTextSize(16);
        warning.setGravity(Gravity.CENTER);
        warning.setPadding(10, 35, 10, 35);

        EditText pin = new EditText(this);
        pin.setHint("Enter demo PIN");
        pin.setTextColor(Color.WHITE);
        pin.setHintTextColor(Color.LTGRAY);
        pin.setGravity(Gravity.CENTER);
        pin.setInputType(2);
        pin.setSingleLine(true);

        Button unlock = new Button(this);
        unlock.setText("UNLOCK DEMO");

        TextView result = new TextView(this);
        result.setTextColor(Color.WHITE);
        result.setTextSize(16);
        result.setGravity(Gravity.CENTER);
        result.setPadding(10, 25, 10, 25);

        unlock.setOnClickListener(v -> {
            if (DEMO_PIN.equals(pin.getText().toString())) {
                result.setText("SIMULATION UNLOCKED");
                result.setTextColor(Color.GREEN);
                pin.setEnabled(false);
                unlock.setEnabled(false);
            } else {
                result.setText("Incorrect PIN. Try again.");
                result.setTextColor(Color.RED);
            }
        });

        Button exit = new Button(this);
        exit.setText("EXIT DEMONSTRATION");
        exit.setOnClickListener(v -> finish());

        layout.addView(title);
        layout.addView(warning);
        layout.addView(pin);
        layout.addView(unlock);
        layout.addView(result);
        layout.addView(exit);

        setContentView(layout);
    }
}
