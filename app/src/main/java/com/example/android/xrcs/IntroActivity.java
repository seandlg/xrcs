package com.example.android.xrcs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class IntroActivity extends AppIntro {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance("Welcome to xrcs!", "This is a demo app to showcase how image recognition can be used to track your workout progress", R.drawable.hq_logo_background_light, getResources().getColor(R.color.colorPrimaryLight)));
        addSlide(AppIntroFragment.newInstance("A couple of tips", "xrcs is a simple demo app. It can be fooled easily, though it should work for what it is intended: tracking your bodyweight workout (pushups or pullups)", R.drawable.hq_logo_background_light, getResources().getColor(R.color.colorPrimaryLight)));
        addSlide(AppIntroFragment.newInstance("Enjoy the app!", "We hope you're enjoying the app. Leave us some feedback :)", R.drawable.hq_logo_background_light, getResources().getColor(R.color.colorPrimaryLight)));

        showStatusBar(true);
        setFadeAnimation();
    }

    @Override
    public void onNextPressed() {
        // Do something when users tap on Next button.
    }

    @Override
    public void onDonePressed() {
        // Do something when users tap on Done button.
        finish();
    }

    @Override
    public void onSlideChanged() {
        // Do something when slide is changed
    }
    @Override
    public void onSkipPressed(){
        finish();
    }
}
