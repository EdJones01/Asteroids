package com.example.asteroids;

public class Laser extends Particle {

    public Laser(double x, double y, double heading) {
        super(x, y);
        vel = new Vector(Math.cos(Math.toRadians(heading)), Math.sin(Math.toRadians(heading))).mul(15);
    }

    void move(){
        super.move();
    }

    boolean hit(Asteroid a) {
        return pos.distance(a.pos) < a.r;
    }

    boolean offScreen(int width, int height) {
        boolean offScreen = false;
        if(pos.x > width || pos.x < 0 || pos.y < 0 || pos.y > height)
            offScreen = true;
        return offScreen;
    }
}