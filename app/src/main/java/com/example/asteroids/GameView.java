package com.example.asteroids;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {
    MainThread thread;
    Random r = new Random();
    Paint paint = new Paint();

    Ship ship;
    ArrayList<Asteroid> asteroids = new ArrayList<>();
    ArrayList<Laser> lasers = new ArrayList<>();
    ArrayList<Explosion> explosions = new ArrayList<>();
    ArrayList<Trail> trail = new ArrayList<>();

    Timer respawn;

    int lives;
    int startingLives = 3;
    int respawnCount = 0;
    boolean gameStarted = false;
    boolean gameOver = false;
    boolean mute = false;
    View buttons;

    int minAsteroids = 15;
    int score;

    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        respawn = new Timer(new Runnable() {
            @Override
            public void run() {
                respawnCount++;
                if (respawnCount % 3 == 0) {
                    respawn.stopTimer();
                    reset();
                }
            }
        }, 1000, false);

        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        restart();

        thread = new MainThread(getHolder(), this);
        thread.setRunning(true);
        thread.start();
    }

    public void update() {
        showButtons();

        while (asteroids.size() < minAsteroids) {
            Asteroid newA = newAsteroid();
            if (newA != null)
                asteroids.add(newA);
        }

        ship.turn();
        ship.move();
        ship.edges(getScreenWidth(), getScreenHeight());
        if (ship.boosting && !respawn.isRunning()) {
            Vector shipBack = new Vector(Math.cos(Math.toRadians(ship.heading)) * 0.15, Math.sin(Math.toRadians(ship.heading)) * 0.15).negate().setMag(ship.r);
            for (int i = 0; i < 5; i++) {
                trail.add(new Trail(ship.pos.x + shipBack.x, ship.pos.y + shipBack.y, new Vector(Math.cos(Math.toRadians(ship.heading)) * 0.15, Math.sin(Math.toRadians(ship.heading)) * 0.15), Math.toRadians(40)));
            }
        }
        for (Asteroid a : asteroids) {
            a.move();
            a.edges(getScreenWidth(), getScreenHeight());
            if (!gameOver && !respawn.isRunning())
                if (a.colliding(ship)) {
                    loseLife();
                    playSound(R.raw.banglarge);
                }
        }

        for (int i = 0; i < lasers.size(); i++) {
            Laser l = lasers.get(i);
            if (l.offScreen(getScreenWidth(), getScreenHeight())) {
                lasers.remove(l);
                break;
            }
            for (int j = 0; j < asteroids.size(); j++) {
                Asteroid a = asteroids.get(j);
                if (l.hit(a)) {
                    score += (int) (Math.ceil(((Asteroid.maxR * 3) - a.r * 2) / 10) * 10);
                    explosions.add(new Explosion(a.pos.x, a.pos.y, Explosion.HUGE));
                    if (r.nextBoolean())
                        playSound(R.raw.bangmedium);
                    else
                        playSound(R.raw.bangsmall);
                    if (a.split(a) != null) {
                        asteroids.add(a.split(a));
                        asteroids.add(a.split(a));
                    }
                    asteroids.remove(a);
                    lasers.remove(l);
                }
            }
            l.move();
        }

        for (int i = 0; i < explosions.size(); i++) {
            Explosion e = explosions.get(i);
            e.move();
            if (e.alpha <= 0)
                explosions.remove(e);
        }

        for (int i = 0; i < trail.size(); i++) {
            Trail t = trail.get(i);
            t.move();
            if (t.alpha <= 0)
                trail.remove(t);
        }
    }

    void reset() {
        ship = new Ship(getScreenWidth() / 2, getScreenHeight() / 2);
        asteroids = new ArrayList<>();
        lasers = new ArrayList<>();
        explosions = new ArrayList<>();
        trail = new ArrayList<>();
    }

    void restart() {
        lives = startingLives;
        score = 0;
        gameOver = false;
        reset();
    }

    void loseLife() {
        lives--;
        if (lives <= 0)
            gameOver = true;
        else
            respawn.startTimer();
        explosions.add(new Explosion(ship.pos.x, ship.pos.y, Explosion.BIG));
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            if(e.getSource() != R.id.mButton) {
                if (gameOver)
                    restart();
                if (!gameStarted) {
                    restart();
                    gameStarted = true;
                }
            }
        }
        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchAction(v.getId(), true);
                break;
            case MotionEvent.ACTION_UP:
                touchAction(v.getId(), false);
                break;
        }
        return true;
    }

    void touchAction(int buttonId, boolean pressed) {
        if (!respawn.isRunning()) {
            if (buttonId == R.id.lButton)
                if (pressed)
                    ship.rotation = -Math.toDegrees(0.1);
                else
                    ship.rotation = 0;
            if (buttonId == R.id.rButton)
                if (pressed)
                    ship.rotation = Math.toDegrees(0.1);
                else
                    ship.rotation = 0;
            if (buttonId == R.id.bButton)
                if (pressed)
                    ship.boosting(true);
                else
                    ship.boosting(false);
            if (buttonId == R.id.sButton) {
                if (pressed) {
                    lasers.add(ship.shoot());
                    playSound(R.raw.fire);
                }
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        paint.setTypeface(getResources().getFont(R.font.font));
        paint.setAntiAlias(true);

        if (!gameStarted) {
            drawStart(canvas);
            drawAsteroids(canvas);
        } else {
            drawAsteroids(canvas);
            drawLasers(canvas);
            drawExplosions(canvas);
            drawScore(canvas);
            drawTrail(canvas);

            if (gameOver)
                drawGameOver(canvas);
            else {
                drawLives(canvas);

                if (!respawn.isRunning())
                    drawShip(canvas);
            }

            if (respawn.isRunning())
                drawRespawn(canvas);
        }
    }

    void drawAsteroids(Canvas canvas) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(2);
        for (Asteroid a : asteroids) {
            canvas.drawPath(a.shape, paint);
        }
    }

    void drawLasers(Canvas canvas) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(3);
        for (Laser l : lasers) {
            canvas.drawPoint((float) l.pos.x, (float) l.pos.y, paint);
        }
    }

    void drawExplosions(Canvas canvas) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(3);
        for (Explosion e : explosions) {
            paint.setColor(Color.argb(e.alpha, 255, 255, 255));
            for (Particle p : e.particles)
                canvas.drawPoint((float) p.pos.x, (float) p.pos.y, paint);
        }
    }

    void drawTrail(Canvas canvas) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        for (Trail t : trail) {
            paint.setColor(Color.argb(t.alpha, 255, 100, 0));
            canvas.drawPoint((float) t.pos.x, (float) t.pos.y, paint);
        }
    }

    void drawScore(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTextSize(40);
        float yOffset = (float) (paint.getTextSize()*1.2);
        String score = ""+this.score;
        if(this.score > 1000)
            score = getHumanReadableNumber(this.score);
        canvas.drawText("Score: " + score, (float) (getScreenWidth() - paint.measureText("Score: " + score) * 1.1), yOffset, paint);
    }

    void drawGameOver(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTextSize(80);
        canvas.drawText("Game Over", (getScreenWidth() / 2) - (paint.measureText("Game Over")) / 2, getScreenHeight() / 3, paint);
        paint.setTextSize(30);
        paint.setColor(Color.RED);
        canvas.drawText("Press anywhere to play again", (getScreenWidth() / 2) - (paint.measureText("Press anywhere to play again")) / 2, 3 * (getScreenHeight() / 6), paint);
    }

    void drawStart(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTextSize(90);
        canvas.drawText("Asteroids", (getScreenWidth() / 2) - (paint.measureText("Asteroids")) / 2, getScreenHeight() / 3, paint);
        paint.setTextSize(50);
        canvas.drawText("by Ed Jones", (getScreenWidth() / 2) - (paint.measureText("by Ed Jones")) / 2, 5 * (getScreenHeight() / 12), paint);
        paint.setTextSize(30);
        canvas.drawText("CONTROLS:", (getScreenWidth() / 2) - (paint.measureText("CONTROLS:")) / 2, 16 * (getScreenHeight() / 24), paint);
        paint.setTextSize(20);
        canvas.drawText("L - Rotate anti-clockwise", (getScreenWidth() / 2) - (paint.measureText("L - Rotate anti-clockwise")) / 2, 17 * (getScreenHeight() / 24), paint);
        canvas.drawText("R - Rotate clockwise", (getScreenWidth() / 2) - (paint.measureText("R - Rotate clockwise")) / 2, 18 * (getScreenHeight() / 24), paint);
        canvas.drawText("B - Boost", (getScreenWidth() / 2) - (paint.measureText("B - Boost")) / 2, 19 * (getScreenHeight() / 24), paint);
        canvas.drawText("S - Shoot", (getScreenWidth() / 2) - (paint.measureText("S - Shoot")) / 2, 20 * (getScreenHeight() / 24), paint);
        paint.setTextSize(40);
        paint.setColor(Color.RED);
        canvas.drawText("Press anywhere to play", (getScreenWidth() / 2) - (paint.measureText("Press anywhere to play")) / 2, 13 * (getScreenHeight() / 24), paint);
    }

    void drawLives(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTextSize(40);
        float yOffset = (float) (paint.getTextSize()*1.2);
        canvas.drawText("Lives:", (float) (paint.measureText("Lives") * 0.1), yOffset, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        yOffset += 3*ship.r;
        for(int i = 0; i < lives; i++) {
            float xOffset = (float) ((paint.measureText("Lives") * 0.1) + (i*(4*ship.r)));
            drawTriangle(canvas, (float) xOffset, (float) (yOffset + ship.r), (float) (xOffset + 2*ship.r), (float) (yOffset + ship.r), (float) (xOffset + ship.r), (float) (yOffset - ship.r), paint);
        }
    }

    void drawShip(Canvas canvas) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(2);
        canvas.save();
        canvas.translate((float) ship.pos.x, (float) ship.pos.y);
        canvas.rotate((float) (ship.heading + 90));
        drawTriangle(canvas, (float) (-ship.r/1.5), (float) ship.r, (float) (ship.r/1.5), (float) ship.r, 0, (float) -ship.r, paint);
        canvas.restore();
    }

    void drawRespawn(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTextSize(70);
        canvas.drawText("Respawning in", (getScreenWidth() / 2) - (paint.measureText("Respawning in")) / 2, getScreenHeight() / 3, paint);
        paint.setTextSize(60);
        canvas.drawText(3 - respawnCount % 3 + "...", (getScreenWidth() / 2) - (paint.measureText(3 - respawnCount % 3 + "")) / 2, 5 * (getScreenHeight() / 12), paint);
    }

    void drawTriangle(Canvas canvas, float x1, float y1, float x2, float y2, float x3, float y3, Paint paint) {
        canvas.drawLine(x1, y1, x2, y2, paint);
        canvas.drawLine(x2, y2, x3, y3, paint);
        canvas.drawLine(x3, y3, x1, y1, paint);
    }

    void addButtons(View buttons) {
        this.buttons = buttons;
    }

    public void showButtons() {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                if(!gameStarted) {
                    buttons.findViewById(R.id.mButton).setVisibility(View.VISIBLE);
                    buttons.bringToFront();
                }
                else
                    buttons.findViewById(R.id.mButton).setVisibility(View.INVISIBLE);
                if (!gameOver && gameStarted && !respawn.isRunning()) {
                    buttons.findViewById(R.id.lButton).setVisibility(View.VISIBLE);
                    buttons.findViewById(R.id.rButton).setVisibility(View.VISIBLE);
                    buttons.findViewById(R.id.bButton).setVisibility(View.VISIBLE);
                    buttons.findViewById(R.id.sButton).setVisibility(View.VISIBLE);
                } else {
                    buttons.findViewById(R.id.lButton).setVisibility(View.INVISIBLE);
                    buttons.findViewById(R.id.rButton).setVisibility(View.INVISIBLE);
                    buttons.findViewById(R.id.bButton).setVisibility(View.INVISIBLE);
                    buttons.findViewById(R.id.sButton).setVisibility(View.INVISIBLE);

                }
            }
        });
    }

    void pushbackAsteroids(Vector from, double mag) {
        for(Asteroid a : asteroids) {
            a.pos = a.pos.copy().add(a.pos.copy().sub(from).setMag(mag));
        }
    }


    private Activity getActivity() {
        Context context = getContext();
        return (MainActivity) context;
    }

    void playSound(int resID) {
        if(!mute) {
            MediaPlayer sound = MediaPlayer.create(this.getContext(), resID);
            sound.start();
        }
    }

    Asteroid newAsteroid() {
        Vector pos = randomOffscreenPos();
        boolean valid = true;
        for (Asteroid a : asteroids) {
            if (pos.distance(a.pos) <= a.r) {
                valid = false;
                break;
            }
        }
        if (valid)
            return new Asteroid(pos.x, pos.y);
        else
            return null;
    }

    Vector randomOffscreenPos() {
        double x;
        double y;
        if (r.nextBoolean()) {
            x = r.nextInt(getWidth());
            if (r.nextBoolean())
                y = getScreenHeight() + 2 * Asteroid.maxR;
            else
                y = -(2 * Asteroid.maxR);
        } else {
            y = r.nextInt(getScreenHeight());
            if (r.nextBoolean())
                x = getScreenWidth() + 2 * Asteroid.maxR;
            else
                x = -(2 * Asteroid.maxR);
        }
        return new Vector(x, y);
    }

    String getHumanReadableNumber(double number) {
        if(number >= 1000000000)
            return String.format("%.2fB", number/ 1000000000.0);
        if(number >= 1000000)
            return String.format("%.2fM", number/ 1000000.0);
        if(number >=1000)
            return String.format("%.2fK", number/ 1000.0);
        return String.valueOf(number);
    }

    public int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }



    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retry = false;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }
}