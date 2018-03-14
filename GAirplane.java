
/*
 * File: GAirplane.java
 * --------------------------
 * Author: Tylor Jik
 * Section Leader: Benson Kung
 * 
 * This class keeps track of the airplane's characteristics. In here lie
 * its velocity, acceleration, orientation, etc. They can be accessed by
 * public method within.
 */

import acm.program.*;

import java.awt.Color;

import acm.graphics.*;

public class GAirplane extends GraphicsProgram implements GAirplaneConstants {

	/*
	 * Using coordinates (x, y, z). x is to the right and left, z is up and
	 * down, and y is front and back.
	 */

	// Constructor things...
	public GAirplane() {
		// Set up our math stuff
		eq = new GAirplaneMath();
	}

	/*
	 * getVel method:: Takes no parameters and returns the velocity of the
	 * plane.
	 */
	public double[] getVel() {
		return velocity;
	}

	/*
	 * getVelMag method:: Takes no parameters and returns magnitude of plane
	 * velocity.
	 */
	public double getVelMag() {
		return eq.magnitude(velocity);
	}

	/*
	 * setVel method: Takes a double[] and returns nothing. This method sets the
	 * velocity of the plane, doesn't let its magnitude exceed 200, accounts for
	 * gravity at different speeds, and also simulates stalling.
	 */
	public void setVel(double[] newVel) {
		double magVel = eq.magnitude(newVel);
		if (magVel < 200) {
			for (int i = 0; i < 3; i++) {
				velocity[i] = newVel[i];
			}
			if (position[2] > GAirplaneConstants.TOTAL_HEIGHT && magVel > 25) {
				gravityRot();
			} else if (position[2] > GAirplaneConstants.TOTAL_HEIGHT && magVel <= 25) {
				velocity[2] -= 2;
				if (acceleration < 0) {
					double[] normVel = eq.norm(velocity);
					double[] newVelocity = eq.vecSum(velocity, eq.scalarProd((-acceleration), normVel));
					for (int j = 0; j < 3; j++) {
						velocity[j] = newVelocity[j];
					}
				}
			}
		} else if (magVel > 200) {
			for (int k = 0; k < 3; k++) {
				acceleration = 0;
			}
		}
	}

	/*
	 * gravityRot method:: Takes nothing and returns nothing. This method
	 * simulates a gravitational force acting on the plane when it isn't going
	 * very fast.
	 */
	private void gravityRot() {
		double[] newVel = new double[3];
		for (int i = 0; i < 3; i++) {
			newVel[i] = velocity[i];
		}
		double magVel = eq.magnitude(newVel);
		double[] newOrient = new double[3];
		for (int i = 0; i < 3; i++) {
			newOrient[i] = orientation[i];
		}
		double[] rotAxis = eq.cross(newVel, newOrient);
		double angle = 0;
		if (magVel < 30) {
			angle = -Math.PI / 360;
		} else if (magVel < 50) {
			angle = -Math.PI / 520;
		} else if (magVel < 80) {
			angle = -Math.PI / 720;
		}
		if (newVel[1] < 0) {
			angle = -angle;
		}
		if (newOrient[2] < 0) {
			angle = -angle;
		}
		double[][] rotationMatrix = eq.rotXCalc(angle);
		newVel = eq.transformTimes(rotationMatrix, newVel);
		newOrient = eq.cross(rotAxis, newVel);
		newOrient = eq.norm(newOrient);
		for (int i = 0; i < 3; i++) {
			velocity[i] = newVel[i];
			orientation[i] = newOrient[i];
		}
	}

	/*
	 * getPos method:: Takes nothing and returns the double[] position of the
	 * plane.
	 */
	public double[] getPos() {
		return position;
	}

	/*
	 * setPos method:: Takes a double[] and returns nothing. This method sets
	 * the position of the plane, but doesn't let it go below the ground.
	 */
	public void setPos(double[] newPos) {
		if (newPos[2] >= GAirplaneConstants.TOTAL_HEIGHT) {
			for (int i = 0; i < 3; i++) {
				position[i] = newPos[i];
			}
			// See if the plane has landed
		} else if (Math.abs(velocity[2]) < 10
				&& (GAirplaneWorld.checkPointRunway(newPos) || GAirplaneWorld.checkPointRunway2(newPos))) {
			position[2] = GAirplaneConstants.TOTAL_HEIGHT;
			velocity[2] = 0;
			acceleration = 0;
			println("Nice landing!");
			// If we get to here, the player has crashed.
		} else {
			GraphicsContest.setLiveliness(false);
			position[2] = GAirplaneConstants.TOTAL_HEIGHT;
			acceleration = 0;
		}
	}

	/*
	 * getOrient method:: Takes nothing and returns the double[] orientation of
	 * the plane
	 */
	public double[] getOrient() {
		return orientation;
	}

	/*
	 * setOrient method:: Takes a double[] and returns nothing. This method sets
	 * the orientation of the plane equal to whatever double[] it is given.
	 */
	public void setOrient(double[] newOrient) {
		for (int i = 0; i < 3; i++) {
			orientation[i] = newOrient[i];
		}
	}

	/*
	 * getAccel method:: Takes nothing and returns the double[] acceleration of
	 * the plane.
	 */
	public double getAccel() {
		return acceleration;
	}

	/*
	 * addAccel method:: Takes nothing and returns nothing. This method adds
	 * 0.225 to the acceleration, provided the total acceleration is less than
	 * 1.
	 */
	public void addAccel() {
		if (acceleration < 1) {
			acceleration += 0.225;
		}
	}

	/*
	 * minusAccel method:: Takes nothing and returns nothing. This method
	 * subtracts 0.225 from the acceleration, provided the total acceleration is
	 * greater than -1.
	 */
	public void minusAccel() {
		if (acceleration > -1) {
			acceleration -= 0.225;
		}
	}

	/*
	 * setAccel method:: Takes a double and returns nothing. This method sets
	 * the acceleration to the given double.
	 */
	public void setAccel(double newAcc) {
		acceleration = newAcc;
	}

	/*
	 * getPlane method:: Takes nothing and returns a GObject (the plane).
	 */
	public GObject getPlane() {
		double refX = GraphicsContest.APPLICATION_WIDTH / 2;
		double refY = GraphicsContest.APPLICATION_HEIGHT * GAirplaneConstants.TOTAL_WINDOW_RATIO;
		GCompound plane = new GCompound();
		GObject rear = getRear();
		plane.add(rear, refX, refY);
		GObject wings = getWings();
		plane.add(wings, refX, refY);
		GObject nose = getNose();
		plane.add(nose, refX - GAirplaneConstants.NOSE_WIDTH / 2, refY - GAirplaneConstants.NOSE_Y_OFFSET);
		GObject body = getBody();
		plane.add(body, refX, refY);
		GObject rudder = getRudder();
		plane.add(rudder, refX - GAirplaneConstants.RUDDER_WIDTH / 2, refY - GAirplaneConstants.RUDDER_HEIGHT);
		GObject landingGear = getLandingGear();
		plane.add(landingGear, refX, refY);
		return plane;
	}

	/*
	 * getRear method:: Takes nothing and returns a GObject.
	 */
	private GObject getRear() {
		// Set up the rear flap coordinates
		double rearLX = getWidth() / 2 - GAirplaneConstants.REAR_WIDTH / 2;
		double rearRX = getWidth() / 2 + GAirplaneConstants.REAR_WIDTH / 2;
		double rearY = 0;
		double upperLX = rearLX + (Math.sqrt(3) / 3) * GAirplaneConstants.REAR_HEIGHT;
		double upperY = -GAirplaneConstants.REAR_HEIGHT;
		double upperRX = rearRX - (Math.sqrt(3) / 3) * GAirplaneConstants.REAR_HEIGHT;

		GPolygon rear = new GPolygon();
		rear.addVertex(rearLX, rearY);
		rear.addVertex(upperLX, upperY);
		rear.addVertex(upperRX, upperY);
		rear.addVertex(rearRX, rearY);
		rear.setFilled(true);
		rear.setFillColor(Color.BLUE);
		return rear;
	}

	/*
	 * getRudder method:: Takes nothing and returns a GObject.
	 */
	private GObject getRudder() {

		double x = getWidth() / 2 - GAirplaneConstants.RUDDER_WIDTH / 2;
		double y = GAirplaneConstants.TOTAL_WINDOW_RATIO * getHeight() - GAirplaneConstants.RUDDER_HEIGHT;
		double width = GAirplaneConstants.RUDDER_WIDTH;
		double height = GAirplaneConstants.RUDDER_HEIGHT;
		GOval rudder = new GOval(x, y, width, height);
		rudder.setFilled(true);
		rudder.setFillColor(Color.BLUE);
		return rudder;
	}

	/*
	 * getWings method:: Takes nothing and returns a GObject.
	 */
	private GObject getWings() {

		double llx = getWidth() / 2 - GAirplaneConstants.WING_WIDTH / 2;
		double lrx = getWidth() / 2 + GAirplaneConstants.WING_WIDTH / 2;
		double lowerY = GAirplaneConstants.TOTAL_WINDOW_RATIO * getHeight() - GAirplaneConstants.WING_Y_OFFSET;
		double upperY = lowerY - GAirplaneConstants.WING_HEIGHT;
		double ulx = llx + (Math.sqrt(3) / 3) * GAirplaneConstants.WING_HEIGHT;
		double urx = lrx - (Math.sqrt(3) / 3) * GAirplaneConstants.WING_HEIGHT;

		GPolygon wings = new GPolygon();
		wings.addVertex(llx, lowerY);
		wings.addVertex(lrx, lowerY);
		wings.addVertex(urx, upperY);
		wings.addVertex(ulx, upperY);
		wings.setFilled(true);
		wings.setFillColor(Color.BLUE);
		return wings;
	}

	/*
	 * getNose method:: Takes nothing and returns a GObject.
	 */
	private GObject getNose() {
		double xCoord = getWidth() / 2 - GAirplaneConstants.NOSE_WIDTH / 2;
		double yCoord = GAirplaneConstants.TOTAL_WINDOW_RATIO * getHeight() - GAirplaneConstants.NOSE_Y_OFFSET;

		GArc nose = new GArc(xCoord, yCoord, GAirplaneConstants.NOSE_WIDTH, GAirplaneConstants.NOSE_WIDTH, 0, 180);
		nose.setFilled(true);
		nose.setFillColor(Color.BLACK);
		return nose;
	}

	/*
	 * getBody method:: Takes nothing and returns a GObject.
	 */
	private GObject getBody() {
		double cx = getWidth() / 2;

		double llx = cx - GAirplaneConstants.BODY_BOTTOM_WIDTH / 2;
		double lrx = cx + GAirplaneConstants.BODY_BOTTOM_WIDTH / 2;
		double lowerY = GAirplaneConstants.TOTAL_WINDOW_RATIO * getHeight() - GAirplaneConstants.REAR_HEIGHT;

		double ulx = cx - GAirplaneConstants.BODY_TOP_WIDTH / 2;
		double urx = cx + GAirplaneConstants.BODY_TOP_WIDTH / 2;
		double upperY = GAirplaneConstants.TOTAL_WINDOW_RATIO * getHeight() - GAirplaneConstants.WING_Y_OFFSET;

		GPolygon body = new GPolygon();
		body.addVertex(llx, lowerY);
		body.addVertex(lrx, lowerY);
		body.addVertex(urx, upperY);
		body.addVertex(ulx, upperY);
		body.setFilled(true);
		body.setFillColor(Color.BLACK);

		return body;
	}

	/*
	 * getLandingGear method:: Takes nothing and returns a GObject.
	 */
	private GObject getLandingGear() {
		GCompound landingGear = new GCompound();
		double ulx = getWidth() / 2 - GAirplaneConstants.BODY_TOP_WIDTH / 2;
		double urx = getWidth() / 2 + GAirplaneConstants.BODY_TOP_WIDTH / 2;
		double upperY = GAirplaneConstants.TOTAL_WINDOW_RATIO * getHeight() - GAirplaneConstants.WING_Y_OFFSET;
		double lowerY = upperY + GAirplaneConstants.LANDING_GEAR_HEIGHT;
		GLine leftLeg = new GLine(ulx, upperY, ulx, lowerY);
		landingGear.add(leftLeg);
		GLine rightLeg = new GLine(urx, upperY, urx, lowerY);
		landingGear.add(rightLeg);

		double leftWheelX = ulx - (GAirplaneConstants.LANDING_GEAR_WHEEL_DIAM) / 2;
		double rightWheelX = urx - (GAirplaneConstants.LANDING_GEAR_WHEEL_DIAM) / 2;
		GOval leftWheel = new GOval(leftWheelX, lowerY, GAirplaneConstants.LANDING_GEAR_WHEEL_DIAM,
				GAirplaneConstants.LANDING_GEAR_WHEEL_DIAM);
		leftWheel.setFilled(true);
		leftWheel.setFillColor(Color.BLACK);
		landingGear.add(leftWheel);
		GOval rightWheel = new GOval(rightWheelX, lowerY, GAirplaneConstants.LANDING_GEAR_WHEEL_DIAM,
				GAirplaneConstants.LANDING_GEAR_WHEEL_DIAM);
		rightWheel.setFilled(true);
		rightWheel.setFillColor(Color.BLACK);
		landingGear.add(rightWheel);
		return landingGear;
	}

	// The instance variables for the plane!
	// Based on (x, y, z) coordinates
	private double[] velocity = { 0, 0.01, 0 };
	private double acceleration = 0;
	private double[] orientation = { 0, 0, 1 };
	private double[] position = { 0, 0, GAirplaneConstants.TOTAL_HEIGHT };
	private GAirplaneMath eq;

}
