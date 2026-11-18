package com.securitylab.demo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final String PREFS = "security_lab_demo";
    private static final String KEY_ACTIVE = "demo_active";
    private static final String KEY_PIN = "demo_pin";
    private static final String DEFAULT_PIN = "1234";
    private static final long DEMO_DURATION_MS =
            24L * 60L * 60L * 1000L;

    private final Handler handler =
            new Handler(Looper.getMainLooper());

    private TextView timerView;
    private EditText pinInput;
    private TextView feedbackView;
    private long endTime;

    private final Runnable timerTick = new Runnable() {
        @Override
        public void run() {
            if (timerView == null) return;

            long remaining = Math.max(
                    0L, endTime - System.currentTimeMillis());

            long hours = remaining / 3600000L;
            long minutes = (remaining / 60000L) % 60L;
            long seconds = (remaining / 1000L) % 60L;

            timerView.setText(String.format(
                    Locale.getDefault(), "%02d:%02d:%02d",
                    hours, minutes, seconds));

            handler.postDelayed(this, 1000L);
        }
    };

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        getWindow().setStatusBarColor(Color.rgb(105, 10, 18));
        getWindow().setNavigationBarColor(Color.rgb(30, 8, 12));

        boolean active = getPreferences(MODE_PRIVATE)
                .getBoolean(KEY_ACTIVE, true);

        if (active) {
            if (!getPreferences(MODE_PRIVATE)
                    .contains("timer_end")) {
                endTime = System.currentTimeMillis()
                        + DEMO_DURATION_MS;

                getPreferences(MODE_PRIVATE).edit()
                        .putLong("timer_end", endTime).apply();
            } else {
                endTime = getPreferences(MODE_PRIVATE)
                        .getLong("timer_end",
                        System.currentTimeMillis()
                        + DEMO_DURATION_MS);
            }
            showDemo();
        } else {
            showDismissedScreen();
        }
    }

    private int dp(int value) {
        return (int) (value *
                getResources().getDisplayMetrics().density + 0.5f);
    }

    private GradientDrawable rounded(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radius));
        return d;
    }

    private TextView label(String value, int size,
                           int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.CENTER);

        if (bold) {
            t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        }
        return t;
    }

    private void add(LinearLayout parent, View child, int topDp) {
        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(-1, -2);
        p.topMargin = dp(topDp);
        parent.addView(child, p);
    }

    private void showDemo() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);

        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setGravity(Gravity.CENTER_HORIZONTAL);
        page.setPadding(dp(22), dp(24), dp(22), dp(22));

        GradientDrawable background = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{
                    Color.rgb(190, 24, 31),
                    Color.rgb(126, 8, 20),
                    Color.rgb(45, 8, 14)
                });
        page.setBackground(background);

        add(page, label("☠", 46, Color.WHITE, true), 0);

        add(page, label(
                "⚠  EDUCATIONAL SIMULATION  ⚠",
                13, Color.rgb(255, 220, 95), true), 8);

        add(page, label(
                "SECURITY ALERT", 29, Color.WHITE, true), 4);

        add(page, label(
                "SIMULATED DEVICE WARNING",
                15, Color.rgb(255, 225, 225), true), 5);

        LinearLayout timerPanel = new LinearLayout(this);
        timerPanel.setOrientation(LinearLayout.VERTICAL);
        timerPanel.setGravity(Gravity.CENTER);
        timerPanel.setPadding(dp(16), dp(14), dp(16), dp(14));
        timerPanel.setBackground(
                rounded(Color.rgb(65, 8, 15), 14));

        timerPanel.addView(label(
                "DEMO TIMER — NO FILES ARE AFFECTED",
                11, Color.rgb(255, 190, 190), true));

        timerView = label("24:00:00", 30, Color.WHITE, true);
        timerPanel.addView(timerView);
        add(page, timerPanel, 18);

        add(page, label(
                "This is a reversible security-awareness exercise. "
                + "No files are encrypted, deleted, uploaded or "
                + "modified. Your Android settings remain unchanged.",
                14, Color.WHITE, false), 16);

        add(page, label(
                "ENTER THE DEMONSTRATION PIN TO DISMISS",
                12, Color.rgb(255, 220, 170), true), 12);

        pinInput = new EditText(this);
        pinInput.setSingleLine(true);
        pinInput.setGravity(Gravity.CENTER);
        pinInput.setTextSize(18);
        pinInput.setTextColor(Color.WHITE);
        pinInput.setHintTextColor(Color.rgb(245, 190, 190));
        pinInput.setHint("Enter demo PIN");
        pinInput.setInputType(
                InputType.TYPE_CLASS_NUMBER
                | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        pinInput.setPadding(dp(12), dp(8), dp(12), dp(8));
        pinInput.setBackground(
                rounded(Color.rgb(75, 9, 17), 10));
        add(page, pinInput, 8);

        Button unlock = new Button(this);
        unlock.setText("UNLOCK DEMONSTRATION");
        unlock.setAllCaps(false);
        unlock.setTextColor(Color.rgb(110, 8, 18));
        unlock.setBackground(rounded(Color.WHITE, 10));
        add(page, unlock, 8);
        unlock.setOnClickListener(v -> unlockDemo());

        feedbackView = label(
                "Default demo PIN: 1234",
                12, Color.rgb(255, 220, 220), false);
        add(page, feedbackView, 4);

        add(page, label(
                "You can always leave this app using Android's "
                + "normal system controls.",
                11, Color.rgb(255, 220, 220), false), 18);

        scroll.addView(page);
        setContentView(scroll);

        handler.removeCallbacks(timerTick);
        handler.post(timerTick);
    }

    private void unlockDemo() {
        String entered = pinInput.getText().toString();

        String saved = getPreferences(MODE_PRIVATE)
                .getString(KEY_PIN, DEFAULT_PIN);

        if (saved.equals(entered)) {
            getPreferences(MODE_PRIVATE).edit()
                    .putBoolean(KEY_ACTIVE, false).apply();

            handler.removeCallbacks(timerTick);
            showDismissedScreen();
        } else {
            feedbackView.setText("Incorrect PIN. Try again.");
            pinInput.setText("");
        }
    }

    private void showDismissedScreen() {
        handler.removeCallbacks(timerTick);

        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setGravity(Gravity.CENTER);
        page.setPadding(dp(24), dp(24), dp(24), dp(24));
        page.setBackgroundColor(Color.rgb(22, 34, 30));

        page.addView(label(
                "DEMONSTRATION DISMISSED",
                23, Color.WHITE, true));

        add(page, label(
                "The simulation has ended. No device files or "
                + "system settings were changed.",
                15, Color.WHITE, false), 14);

        Button replay = new Button(this);
        replay.setText("RUN DEMONSTRATION AGAIN");
        replay.setAllCaps(false);
        add(page, replay, 20);

        replay.setOnClickListener(v -> {
            endTime = System.currentTimeMillis()
                    + DEMO_DURATION_MS;

            getPreferences(MODE_PRIVATE).edit()
                    .putBoolean(KEY_ACTIVE, true)
                    .putLong("timer_end", endTime)
                    .apply();

            showDemo();
        });

        setContentView(page);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(timerTick);
        super.onDestroy();
    }
}
