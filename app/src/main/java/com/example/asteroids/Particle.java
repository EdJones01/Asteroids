package com.example.asteroids;

public class Particle {
	Vector pos;
	Vector vel;

	public Particle() {
		pos = new Vector(0,0);
		vel = new Vector(0,0);
	}

	public Particle(double x, double y, double vx, double vy) {
		pos = new Vector(x, y);
		vel = new Vector(vx, vy);
	}
	
	public Particle(double x, double y) {
		pos = new Vector(x, y);
		vel = new Vector(0,0);
	}
	
	void move() {
		pos.add(vel);
	}
}