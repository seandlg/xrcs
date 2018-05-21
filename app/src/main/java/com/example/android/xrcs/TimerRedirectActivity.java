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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_redirect);

        Intent intent = getIntent();
        Bundle workoutDataBundle = intent.getBundleExtra("workoutDataBundle");
        int setsPerformedSoFar = intent.getIntExtra("setsPerformedSoFar",0);

        String overlayHeadingText = intent.getStringExtra("timerHeading");
        overlayHeadingTV = findViewById(R.id.overlay_heading_tv);
        overlayHeadingTV.setText(overlayHeadingText);

        timerStartValue = intent.getIntExtra("timerStartValue", 0);
        overlayTimerTV = findViewById(R.id.overlay_text_tv);
        overlayTimerTV.setText(String.valueOf(timerStartValue));

        final Intent DetectorActivityIntent = new Intent(this, DetectorActivity.class);
        DetectorActivityIntent.putExtra("workoutDataBundle", workoutDataBundle);

        DetectorActivityIntent.putExtra("setsPerformedSoFar",setsPerformedSoFar);
        TextView overlayHeadingTV = findViewById(R.id.overlay_heading_tv);

        // Initialize the TextToSpeech Engine
        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                    t1.speak("Hello Test!", TextToSpeech.QUEUE_FLUSH, null, null);
                    new CountDownTimer(timerStartValue * 1000 + 100, 1000) {
                        public void onTick(long millisUntilFinished) {
                            Long secondsLeftLong = (millisUntilFinished / 1000);
                            int secondsLeft = secondsLeftLong.intValue();
                            if (secondsLeft!=0){
                                overlayTimerTV.setText(String.valueOf(secondsLeft));
                            }
                            // If last second, then say "Go" instead of 0
                            if (millisUntilFinished < 500){
                                overlayTimerTV.setText("Go!");
                                t1.speak("Go!", TextToSpeech.QUEUE_FLUSH, null, null);
                            }
                            // If not second < 5, then just speech output every 5 seconds, else if 0 < seconds < 5 speech output every second
                            else if ((millisUntilFinished > 500) && (millisUntilFinished < 6000) || secondsLeft % 5 == 0) {
                                t1.speak(String.valueOf(secondsLeft), TextToSpeech.QUEUE_FLUSH, null, null);
                            }
                            secondsLeft--;
                        }

                        public void onFinish() {
                            startActivity(DetectorActivityIntent);
                            finish();
                        }
                    }.start();
                }
            }
        });
    }
}
