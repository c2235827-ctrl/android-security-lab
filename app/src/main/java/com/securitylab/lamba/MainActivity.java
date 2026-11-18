package com.securitylab.lamba;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.widget.*;

public class MainActivity extends Activity {

    // CUSTOMIZE THESE VALUES
    private static final String PIN = "1234";
    private static final String TITLE = "LAMBA SECURITY DEMO";

    private static final String MESSAGE =
        "This is a reversible security-awareness exercise. " +
        "No files are encrypted, deleted, uploaded or modified. " +
        "Your Android settings remain unchanged.";

    private SharedPreferences prefs;
    private EditText pinBox;

    private int dp(int n) {
        return (int) (n * getResources()
            .getDisplayMetrics().density + 0.5f);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("lamba_demo", MODE_PRIVATE);

        if (prefs.getBoolean("dismissed", false)) {
            finished();
        } else {
            demo();
        }
    }

    private TextView label(String value, int size, boolean bold) {
        TextView t = new TextView(this);

        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(Color.WHITE);
        t.setGravity(Gravity.CENTER);

        t.setPadding(dp(8), dp(8), dp(8), dp(8));

        t.setTypeface(
            Typeface.DEFAULT,
            bold ? Typeface.BOLD : Typeface.NORMAL
        );

        return t;
    }

    private GradientDrawable shape(int color) {
        GradientDrawable d = new GradientDrawable();

        d.setColor(color);
        d.setCornerRadius(dp(16));

        return d;
    }

    private void demo() {

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(Color.BLACK);

        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setGravity(Gravity.CENTER_HORIZONTAL);

        page.setPadding(
            dp(22), dp(24), dp(22), dp(24)
        );

        scroll.addView(page);

        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.ic_lamba);

        page.addView(
            logo,
            new LinearLayout.LayoutParams(dp(76), dp(76))
        );

        page.addView(
            label("EDUCATIONAL SIMULATION", 14, true)
        );

        page.addView(
            label(TITLE, 26, true)
        );

        page.addView(
            label("SIMULATED DEVICE WARNING", 17, true)
        );

        TextView info = label(
            "DEMO MODE — NO FILES ARE AFFECTED",
            18,
            true
        );

        info.setBackground(shape(0xFF292929));

        LinearLayout.LayoutParams infoParams =
            new LinearLayout.LayoutParams(-1, dp(86));

        infoParams.setMargins(0, dp(24), 0, dp(20));

        page.addView(info, infoParams);

        page.addView(
            label(MESSAGE, 16, false)
        );

        page.addView(
            label(
                "ENTER THE DEMONSTRATION PIN TO DISMISS",
                14,
                true
            )
        );

        pinBox = new EditText(this);

        pinBox.setSingleLine(true);
        pinBox.setGravity(Gravity.CENTER);
        pinBox.setTextColor(Color.WHITE);
        pinBox.setHintTextColor(0xFFBBBBBB);
        pinBox.setHint("Enter demo PIN");

        pinBox.setInputType(2 | 16);
        pinBox.setBackground(shape(0xFF292929));

        page.addView(
            pinBox,
            new LinearLayout.LayoutParams(-1, dp(56))
        );

        Button unlock = new Button(this);

        unlock.setText("UNLOCK DEMONSTRATION");
        unlock.setAllCaps(false);

        unlock.setOnClickListener(v -> {

            if (PIN.equals(pinBox.getText().toString())) {

                prefs.edit()
                    .putBoolean("dismissed", true)
                    .apply();

                finished();

            } else {

                new AlertDialog.Builder(this)
                    .setMessage("Incorrect PIN. Try again.")
                    .setPositiveButton(
                        "OK",
                        (dialog, which) -> pinBox.setText("")
                    )
                    .show();
            }
        });

        LinearLayout.LayoutParams buttonParams =
            new LinearLayout.LayoutParams(-1, dp(56));

        buttonParams.topMargin = dp(12);

        page.addView(unlock, buttonParams);

        page.addView(
            label("Default demonstration PIN: 1234", 13, true)
        );

        page.addView(
            label(
                "Android Home, Back and Recent Apps controls remain available.",
                12,
                false
            )
        );

        setContentView(scroll);
    }

    private void finished() {

        LinearLayout page = new LinearLayout(this);

        page.setOrientation(LinearLayout.VERTICAL);
        page.setGravity(Gravity.CENTER);

        page.setPadding(
            dp(24), dp(24), dp(24), dp(24)
        );

        page.setBackgroundColor(Color.BLACK);

        page.addView(
            label("LAMBA", 28, true)
        );

        page.addView(
            label("Demonstration dismissed.", 17, false)
        );

        Button again = new Button(this);

        again.setText("Restart demonstration");

        again.setOnClickListener(v -> {

            prefs.edit()
                .putBoolean("dismissed", false)
                .apply();

            demo();
        });

        page.addView(again);

        setContentView(page);
    }
}
