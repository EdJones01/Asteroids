package com.example.asteroids;

import java.util.Random;

public class Asteroid extends Particle {
	static Random ran = new Random();

	final double rOffset = 0.9;
	final int maxVertexes = 10;
	final int minVertexes = 7;
	static double minR = 20;
	static double maxR = 50;
	static double minV = -1;
	static double maxV = 1;

	double r;
	int total;
	Double[] offset;
	Polygon shape = new Polygon();

	public Asteroid(double x, double y) {
		super(x, y);
		vel = Vector.random(minV, maxV, minV, maxV);
		r = minR + ran.nextDouble() * (maxR - minR);

		total = ran.nextInt(maxVertexes - minVertexes) + minVertexes;

		double offsetR = r * rOffset;
		offset = new Double[total];
		for (int i = 0; i < total; i++) {
			offset[i] = ran.nextInt((int) offsetR)-(offsetR*0.5);
		}
	}

	Asteroid split(Asteroid a) {
		if(!((a.r/2.0) < minR)) {
			Asteroid child = new Asteroid(a.pos.x ,a.pos.y);
			child.r /= 1.75;
			return child;
		}
		else return null;
	}

	void edges(int width, int height) {
		if (pos.x-r*2 > width + r*2)
			pos.x = -(2*r);
		else if (pos.x < -r*4)
			pos.x = width + 2*r;
		if (pos.y - r*2 > height + r*2)
			pos.y = -(2*r);
		else if (pos.y < -r*4)
			pos.y = height + 2*r;
	}

	boolean colliding(Ship s) {
		return pos.distance(s.pos) <= r + s.r;
	}

	void move() {
		super.move();
		shape = new Polygon();
		for (int i = 0; i < total; i++) {
			double angle = ((Math.PI*2)/total) * i;
			double r1 = r + offset[i];
			double x = (r1 * Math.cos(angle)) + this.pos.x;
			double y = (r1 * Math.sin(angle)) + this.pos.y;
			shape.addPoint((int) x, (int) y);
		}
		shape.close();
	}
}