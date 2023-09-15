package com.example.asteroids;

public class Ship extends Particle  {
	double r = 15;
	double heading = 0;
	double rotation = 0;
	boolean boosting = false;
	Polygon shape;

	public Ship(int x, int y) {
		super(x, y);

		shape = new Polygon();
		shape.addPoint((int) (pos.x - r), (int) (pos.y + r));
		shape.addPoint((int) (pos.x + r), (int) (pos.y + r));
		shape.addPoint((int) (pos.x), (int) (pos.y - r));
	}

	void boosting(boolean b) {
	    boosting = b;
	}

	void move() {
		super.move();
		if(boosting)
			boost();
		vel.mul(0.99);

		shape = new Polygon();
		shape.addPoint((int) (pos.x - r), (int) (pos.y + r));
		shape.addPoint((int) (pos.x + r), (int) (pos.y + r));
		shape.addPoint((int) pos.x, (int) (pos.y - r));
	}

	void boost() {
	    vel.add(new Vector(Math.cos(Math.toRadians(heading))*0.15, Math.sin(Math.toRadians(heading))*0.15));
	}

	void turn() {
		heading += rotation;
	}

	Laser shoot() {
		Vector shipFront = new Vector(Math.cos(Math.toRadians(heading)) * 0.15, Math.sin(Math.toRadians(heading)) * 0.15).setMag(r);
		return new Laser(pos.x + shipFront.x, pos.y + shipFront.y, heading);
	}
	
	void edges(int width, int height) {
		if (pos.x > width + r)
			pos.x = -r;
		else if (pos.x < -r)
			pos.x = width + this.r;
		if (pos.y > height + r)
			pos.y = -r;
		else if (pos.y < -r)
			pos.y = height + r;
	}
}