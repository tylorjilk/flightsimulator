
/*
 * File: GAirplane.java
 * --------------------------
 * Author: Tylor Jilk
 * Section Leader: Benson Kung
 * 
 * This file keeps track of the world around our GAirplane. It uses a lot of lines and planes
 * to calculate every pixel on the screen.
 */

import acm.program.*;

import java.awt.Color;

import acm.graphics.*;

public class GAirplaneWorld extends GraphicsProgram implements GAirplaneConstants {

	private GAirplaneMath eq;
	// The viewing angle
	private static final double ALPHA = 120;

	// Constructor stuff
	public GAirplaneWorld() {
		eq = new GAirplaneMath();
	}

	/*
	 * updateView method:: Takes a GAirplane and returns an int[][]. This method
	 * uses parameters from the GAirplane to calculate a 2D array of pixels that
	 * represent the view of the world. It basically takes the position,
	 * velocity, and orientation of the plane, finds where the viewing window
	 * should be in that 3d world, calculates a line that goes through every
	 * pixel in that viewing window and finds where that line intersects with
	 * the ground, and finally determines if that point is in the viewing window
	 * and what color it should be. This color value is stored as an RGB int in
	 * the int[][] array.
	 */
	public int[][] updateView(GAirplane plane) {
		// Set up the local variables
		int[][] pixels = new int[GraphicsContest.APPLICATION_HEIGHT][GraphicsContest.APPLICATION_WIDTH];
		double[] pos = plane.getPos();
		double[] vel = plane.getVel();
		double[] orient = plane.getOrient();

		// Construct normalized coordinates
		vel = eq.norm(vel);
		orient = eq.norm(orient);
		double[] xCoord = eq.cross(vel, orient);
		xCoord = eq.norm(xCoord);
		double[][] coord = { xCoord, vel, orient };
		double[][] transform = eq.transform(coord);

		// Calculate constants
		// D is the distance from the observer to the viewing screen
		double D = 1 / (Math.tan((Math.PI * ALPHA / 180) / 2));
		double dh = 2 / ((double) GraphicsContest.APPLICATION_HEIGHT - 1);
		double dw = 2 / ((double) GraphicsContest.APPLICATION_WIDTH - 1);

		// Construct all of the ray vectors...
		double h = 1;
		for (int hPixel = 0; hPixel < GraphicsContest.APPLICATION_HEIGHT; hPixel += 2) {
			double w = -1;
			for (int wPixel = 0; wPixel < GraphicsContest.APPLICATION_WIDTH; wPixel += 2) {
				// Construct the ray vector though that pixel
				double[] ray = { w, D, h };
				// Transform it into static coord and normalize it.
				ray = eq.transformTimes(transform, ray);
				ray = eq.norm(ray);
				// Calculate where it intersects the ground
				double t = -pos[2] / ray[2];
				double[] point = eq.vecSum(pos, eq.scalarProd(t, ray));

				// Set the color of the pixel based on this point
				int colorInt = 0;
				if (checkPointRunway(point) && t >= 0) {
					Color color = Color.DARK_GRAY;
					colorInt = color.getRGB();
					if (checkPointRunwayStripe(point)) {
						color = Color.YELLOW;
						colorInt = color.getRGB();
					}
				} else if (checkPointRunway2(point) && t >= 0) {
					Color color = Color.DARK_GRAY;
					colorInt = color.getRGB();
					if (checkPointRunway2Stripe(point)) {
						color = Color.YELLOW;
						colorInt = color.getRGB();
					}
				} else if (checkPointSquares(point) && t >= 0) {
					Color color = Color.GREEN;
					colorInt = color.getRGB();
				} else if (checkPointGround(point) && t >= 0) {
					Color color = new Color(139, 69, 19);
					colorInt = color.getRGB();
				} else {
					Color color = Color.CYAN;
					colorInt = color.getRGB();
				}

				// To speed up the process, we will actually only look at one
				// pixel of each 4-pack, and then set those four pixels the same
				// color.
				pixels[hPixel][wPixel] = colorInt;
				pixels[hPixel][wPixel + 1] = colorInt;
				pixels[hPixel + 1][wPixel] = colorInt;
				pixels[hPixel + 1][wPixel + 1] = colorInt;

				w += 2 * dw;
			}
			h -= 2 * dh;
		}
		return pixels;
	}

	/*
	 * checkPointRunway method:: Takes a double[] and returns a boolean. This
	 * method checks if a point lies on the runway and returns a boolean of
	 * that.
	 */
	public static boolean checkPointRunway(double[] point) {
		boolean p = false;
		if (point[0] >= runwayXMin && point[0] <= runwayXMax) {
			if (point[1] >= runwayYMin && point[1] <= runwayYMax) {
				p = true;
			}
		}
		return p;
	}

	/*
	 * checkPointRunway2 method:: Takes a double[] and returns a boolean. This
	 * method checks if a point lies on the second runway and returns a boolean
	 * of that.
	 */
	public static boolean checkPointRunway2(double[] point) {
		boolean p = false;
		if (point[0] >= runway2XMin && point[0] <= runway2XMax) {
			if (point[1] >= runway2YMin && point[1] <= runway2YMax) {
				p = true;
			}
		}
		return p;
	}

	/*
	 * checkPointRunwayStripe method:: Takes a double[] and returns a boolean.
	 * This method checks if a point lies on one of the yellow stripes on the
	 * runway and returns a boolean of that.
	 */
	private boolean checkPointRunwayStripe(double[] point) {
		boolean p = false;
		if (point[0] >= runwayStripeXMin && point[0] <= runwayStripeXMax) {
			for (int i = 0; i < runwayStripeNum * 2; i += 2) {
				if (point[1] >= i * runwayStripeYLength && point[1] <= (i + 1) * runwayStripeYLength) {
					p = true;
				}
			}
		}
		return p;
	}

	/*
	 * checkPointRunway2Stripe method:: Takes a double[] and returns a boolean.
	 * This method checks if a point lies on one of the yellow stripes on the
	 * second runway and returns a boolean of that.
	 */
	private boolean checkPointRunway2Stripe(double[] point) {
		boolean p = false;
		if (point[0] >= runway2StripeXMin && point[0] <= runway2StripeXMax) {
			for (int i = 0; i < runway2StripeNum * 2; i += 2) {
				if (point[1] >= i * runway2StripeYLength + runway2YMin
						&& point[1] <= (i + 1) * runway2StripeYLength + runway2YMin) {
					p = true;
				}
			}
		}
		return p;
	}

	/*
	 * checkPointSquares method:: Takes a double[] and returns a boolean. This
	 * method checks if a point lies on any of the green squares, and returns a
	 * boolean of that.
	 */
	private boolean checkPointSquares(double[] point) {
		boolean p = false;
		for (int j = 2; j < numSquares * 2; j += 2) {
			if (point[0] >= j * squareXLength && point[0] <= (j + 1) * squareXLength) {
				for (int i = 0; i < numSquares * 2; i += 2) {
					if (point[1] >= i * squareYLength && point[1] <= (i + 1) * squareYLength) {
						p = true;
					}
				}
			}
		}

		for (int j = 2; j < numSquares * 2; j += 2) {
			if (point[0] <= -j * squareXLength && point[0] >= -(j + 1) * squareXLength) {
				for (int i = 0; i < numSquares * 2; i += 2) {
					if (point[1] >= i * squareYLength && point[1] <= (i + 1) * squareYLength) {
						p = true;
					}
				}
			}
		}

		return p;
	}

	/*
	 * checkPointGround method:: Takes a double[] and returns a boolean. This
	 * method checks if a point lies on a giant square which represents the
	 * ground, and returns a boolean of that.
	 */
	private boolean checkPointGround(double[] point) {
		boolean p = false;

		if (point[0] >= groundXMin && point[0] <= groundXMax) {
			if (point[1] >= groundYMin && point[1] <= groundYMax) {
				p = true;
			}
		}

		return p;
	}

	// Here are the values of many constants.
	public static final double runwayXMin = -GAirplaneConstants.RUNWAY_WIDTH / 2;
	public static final double runwayXMax = GAirplaneConstants.RUNWAY_WIDTH / 2;
	public static final double runwayYMin = 0;
	public static final double runwayYMax = GAirplaneConstants.RUNWAY_LENGTH;

	public static final double runway2XMin = -GAirplaneConstants.RUNWAY_WIDTH / 2;
	public static final double runway2XMax = GAirplaneConstants.RUNWAY_WIDTH / 2;
	public static final double runway2YMin = 4 * GAirplaneConstants.RUNWAY_LENGTH;
	public static final double runway2YMax = 5 * GAirplaneConstants.RUNWAY_LENGTH;

	private final double runway2StripeNum = 50;
	private final double runway2StripeXMin = -GAirplaneConstants.RUNWAY_WIDTH * GAirplaneConstants.RUNWAY_STRIPE_RATIO;
	private final double runway2StripeXMax = GAirplaneConstants.RUNWAY_WIDTH * GAirplaneConstants.RUNWAY_STRIPE_RATIO;
	private final double runway2StripeYLength = GAirplaneConstants.RUNWAY_LENGTH / (runway2StripeNum * 2);

	private final double groundXMin = -500000;
	private final double groundXMax = 500000;
	private final double groundYMin = -500000;
	private final double groundYMax = 500000;

	private final double runwayStripeNum = 50;
	private final double runwayStripeYLength = GAirplaneConstants.RUNWAY_LENGTH / (runwayStripeNum * 2);
	private final double runwayStripeXMin = -GAirplaneConstants.RUNWAY_WIDTH * GAirplaneConstants.RUNWAY_STRIPE_RATIO;
	private final double runwayStripeXMax = GAirplaneConstants.RUNWAY_WIDTH * GAirplaneConstants.RUNWAY_STRIPE_RATIO;

	private final double numSquares = 20;
	private final double squareYLength = GAirplaneConstants.RUNWAY_LENGTH / (numSquares * 2);
	private final double squareXLength = GAirplaneConstants.RUNWAY_WIDTH;
}
