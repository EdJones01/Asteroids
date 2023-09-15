package com.example.asteroids;


import java.util.Random;

public class Explosion {
	static final double HUGE = 5.0;
	static final double BIG = 3.0;
	static final double SMALL = 1.0;
	
	Random r = new Random();
	int maxParticles = 16;
	int minParticles = 8;
	
	Particle[] particles;
	int alpha = 255;

	public Explosion(double x, double y, double modifier) {
		particles = new Particle[r.nextInt((maxParticles - minParticles) + minParticles)*(int) modifier];
		for(int i = 0; i < particles.length; i++) {
			Vector vel = Vector.random(-1, 1, -1, 1);
			particles[i] = new Particle(x, y, vel.x, vel.y);
		}
	}

	void move() {
		for(Particle p : particles)
			p.move();
		alpha -=3;
	}
}