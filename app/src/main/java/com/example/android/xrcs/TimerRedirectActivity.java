package com.example.android.xrcs;

import android.content.Intent;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.Locale;

public class TimerRedirectActivity extends AppCompatActivity {
    public TextView overlayHeadingTV;
    public TextView overlayTimerTV;
    public TextToSpeech t1;
    public int timerStartValue;
    public CountDownTimer myCountDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_redirect);

        Intent intent = getIntent();
        Bundle workoutDataBundle = intent.getBundleExtra("workoutDataBundle");
        int currentSet = intent.getIntExtra("currentSet", 1);

        String overlayHeadingText = intent.getStringExtra("timerHeading");
        overlayHeadingTV = findViewById(R.id.overlay_heading_tv);
        overlayHeadingTV.setText(overlayHeadingText);

        timerStartValue = intent.getIntExtra("timerStartValue", 0);
        overlayTimerTV = findViewById(R.id.overlay_text_tv);
        overlayTimerTV.setText(String.valueOf(timerStartValue));

        final Intent DetectorActivityIntent = new Intent(this, DetectorActivity.class);
        DetectorActivityIntent.putExtra("workoutDataBundle", workoutDataBundle);

        DetectorActivityIntent.putExtra("currentSet", currentSet);
        TextView overlayHeadingTV = findViewById(R.id.overlay_heading_tv);

        // Initialize the TextToSpeech Engine
        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                    myCountDownTimer = new CountDownTimer(timerStartValue * 1000 + 300, 1000) {
                        public void onTick(long millisUntilFinished) {
                            Long secondsLeftLong = (millisUntilFinished / 1000);
                            int secondsLeft = secondsLeftLong.intValue();
                            if (secondsLeft != 0) {
                                overlayTimerTV.setText(String.valueOf(secondsLeft));
                                if (secondsLeft <= 5 || secondsLeft % 5 == 0) {
                                    t1.speak(String.valueOf(secondsLeft), TextToSpeech.QUEUE_FLUSH, null, null);
                                }
                            } else {
                                overlayTimerTV.setText("Go!");
                                t1.speak("Go!", TextToSpeech.QUEUE_FLUSH, null, null);
                            }
                            secondsLeft--;
                        }

                        public void onFinish() {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                            }
                            startActivity(DetectorActivityIntent);
                            finish();
                        }
                    }.start();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        myCountDownTimer.cancel();
        super.onBackPressed();
    }
}
