# xrcs
Find the Android App in the PlayStore [here](https://play.google.com/store/apps/details?id=com.xrcs.android).

## Phone placement
This app uses your smartphone's front camera to track you doing pushups or pullups. Upon starting workout tracking, place your phone around 2 meters in front of you, in an upright position, with the screen facing you.

## About
This is a proof-of-concept app. It is my hope to gather some user feedback on automated workout tracking.

xrcs is, and likely will remain, beta.

It's based on Tensorflow demo app and simply tracks "bounding boxes" around a detected individual, to count repetitions of pullups or pushups.

A user can specify a workout by its type (Pushup or Pullup), as well as the reps, sets, and optionally a target time for a set, as well as a rest time between sets.

## Noteworthy
- There can only be one person "onscreen" at a time. A popup will notify you about this!
- You'll need a reasonably powerful phone. This app has proved to work on a Galaxy S9, as well as on a Huawei Mate 10 Pro.
- Don't go too quickly with your repetitions, or the App might miss it.
- You can easily fool the app. Any kind of movement that increases your height on the screen, then decrease it, will trigger a repetition. Yes, that's how simple this algorithm works right now!

## Feedback
If you have any feedback on this App, I'm highly excited to hear about it. Would you be excited to see a stationary & much, much improved version of something like this in a gym? Think of a setup that seamlessly tracks any of your free-weights & body-weight workout!
