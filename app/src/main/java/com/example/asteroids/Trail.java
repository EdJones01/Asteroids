package com.example.asteroids;

public class Trail extends Particle {
    int alpha = 255;

    public Trail(double x, double y, Vector relative, double tolerance) {
        super(x, y);
        vel = relative.copy();
        vel.add(Vector.random(-tolerance, tolerance, -tolerance, tolerance));
    }

    void move() {
        super.move();
        alpha -=3;
    }
}
