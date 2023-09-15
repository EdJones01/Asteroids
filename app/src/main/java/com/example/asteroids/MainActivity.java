package com.example.asteroids;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;

public class MainActivity extends Activity {
    GameView gameView;
    View buttons;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    int buttonLayout = R.layout.legacybuttonlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        FrameLayout game = new FrameLayout(this);
        gameView = new GameView (this);

        game.addView(gameView);
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        buttons = vi.inflate(buttonLayout, null);
        game.addView(buttons);
        buttons.findViewById(R.id.lButton).setOnTouchListener(gameView);
        buttons.findViewById(R.id.rButton).setOnTouchListener(gameView);
        buttons.findViewById(R.id.bButton).setOnTouchListener(gameView);
        buttons.findViewById(R.id.sButton).setOnTouchListener(gameView);
        ImageButton mButton = buttons.findViewById(R.id.mButton);
        gameView.mute = preferences.getBoolean("mute", false);
        if (gameView.mute)
            mButton.setImageResource(R.drawable.muted);
        else
            mButton.setImageResource(R.drawable.unmuted);
        mButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    ImageButton mButton = buttons.findViewById(R.id.mButton);
                    gameView.mute = !gameView.mute;
                    if (gameView.mute)
                        mButton.setImageResource(R.drawable.muted);
                    else
                        mButton.setImageResource(R.drawable.unmuted);
                    editor.putBoolean("mute", gameView.mute);
                    editor.commit();
                }
                return true;
            }
        });
        setContentView(game);

        gameView.addButtons(buttons);
    }
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int width  = Resources.getSystem().getDisplayMetrics().widthPixels;
        int height = Resources.getSystem().getDisplayMetrics().heightPixels;
        gameView.ship = new Ship(width/2, height/2);
        gameView.pushbackAsteroids(gameView.ship.pos, 100);
    }
}