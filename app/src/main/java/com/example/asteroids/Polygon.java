package com.example.asteroids;

import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;

public class Polygon extends Path {
    RectF bounds;
    int	npoints = 0;
    int[] xpoints;
    int[] ypoints;
    Point[] points;

    public Polygon() {
        this(16);
    }

    public Polygon(int numPoints) {
        super();
        xpoints = new int[numPoints];
        ypoints = new int[numPoints];
        points = new Point[numPoints];
    }

    public Polygon(int[] xpoints, int[] ypoints, int npoints) {
        super();
        this.xpoints = xpoints;
        this.ypoints = ypoints;
        this.npoints = npoints;
        moveTo(xpoints[0], ypoints[0]);
        for (int p = 1; p < npoints; p++) {
            lineTo(xpoints[p], ypoints[p]);
        }
        close();
    }

    public void addPoint(int x, int y) {
        xpoints[npoints] = x;
        ypoints[npoints] = y;
        points[npoints] = new Point(x, y);
        if (npoints > 0) {
            lineTo(x, y);
        } else {
            moveTo(x, y);
        }
        npoints++;
    }

    @Override
    public void close() {
        super.close();
        //create bounds for this polygon
        bounds = new RectF();
        this.computeBounds(bounds, false);
    }
}
