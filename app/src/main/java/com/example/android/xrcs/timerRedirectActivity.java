package com.example.android.xrcs;

import android.content.Intent;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Locale;

public class timerRedirectActivity extends AppCompatActivity {
    public TextView overlayHeadingTV;
    public TextView overlayTimerTV;
    public TextToSpeech t1;
    public int timerStartValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_redirect);

        Intent intent = getIntent();
        Bundle workoutDataBundle = intent.getBundleExtra("workoutDataBundle");

        String overlayHeadingText = intent.getStringExtra("timerHeading");
        overlayHeadingTV = findViewById(R.id.overlay_heading_tv);
        overlayHeadingTV.setText(overlayHeadingText);

        timerStartValue = intent.getIntExtra("timerStartValue", 0);
        overlayTimerTV = findViewById(R.id.overlay_text_tv);
        overlayTimerTV.setText(String.valueOf(timerStartValue));

        final Intent DetectorActivityIntent = new Intent(this, DetectorActivity.class);
        DetectorActivityIntent.putExtra("workoutDataBundle", workoutDataBundle);
        TextView overlayHeadingTV = findViewById(R.id.overlay_heading_tv);

        // Initialize the TextToSpeech Engine
        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });

        new CountDownTimer(timerStartValue * 1000 + 100, 1000) {

            public void onTick(long millisUntilFinished) {
                overlayTimerTV.setText(String.valueOf(millisUntilFinished / 1000));
                if (millisUntilFinished%1000 == 0) {
                    t1.speak(String.valueOf(timerStartValue), TextToSpeech.QUEUE_FLUSH, null, null);
                    timerStartValue--;
                }
                // TODO FIX THE SPEECH OUTPUT!!!!!!!!!!!!!!!!!!!!!!
            }
            public void onFinish() {
                startActivity(DetectorActivityIntent);
                finish();
            }
        }.start();
    }

}
