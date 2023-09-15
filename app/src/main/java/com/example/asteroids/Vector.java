package com.example.asteroids;

import java.util.Random;

public class Vector {
    static Random r = new Random();
    public double x, y;


    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector() {
        this(0, 0);
    }

    public static Vector random(double xMin, double xMax, double yMin, double yMax) {
        return new Vector(xMin + r.nextDouble() * (xMax - xMin), yMin + r.nextDouble() * (yMax - yMin));
    }

    public Vector copy() {

        return new Vector(x, y);
    }

    public Vector add(Vector other) {
        x += other.x;
        y += other.y;
        return this;
    }

    public Vector sub(Vector other) {
        x -= other.x;
        y -= other.y;
        return this;
    }

    public Vector mul(double scalar) {
        x *= scalar;
        y *= scalar;
        return this;
    }

    public Vector div(double scalar) {
        x /= scalar;
        y /= scalar;
        return this;
    }

    public double mag() {
        return Math.sqrt(mag2());
    }

    public Vector setMag(double mag) {
        if (mag > 0) {
            normalize();
            mul(mag);
        }
        return this;
    }

    public double mag2() {
        return x*x + y*y;
    }

    public Vector normalize() {
        double mag = (double) mag();
        if (mag > 0) {
            div(mag);
        }
        return this;
    }

    public Vector negate() {
        x = -x;
        y = -y;
        return this;
    }

    public double distance(Vector other) {
        return other.copy().sub(this).mag();
    }

    public Vector limit(double mag) {
        if (mag() > mag) {
            setMag(mag);
        }
        return this;
    }

    public double getTheta() {
        return Math.atan2(y, x);
    }

    public String toString() {
        return "x: " + x + " y: " + y;
    }
}