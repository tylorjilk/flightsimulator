
/*
 * File: GraphicsContest.java
 * --------------------------
 * Author: Tylor Jilk
 * Section Leader: Benson Kung
 * 
 * This file implements a flight simulator. It uses both images in the graphics
 * window and messages in the console to interact with the player. In the simulator,
 * the player must fly the plane as you would a normal plane. There are two runways,
 * the the player can land or take off on either of them.
 * 
 * First, click on the graphics window so the program can recognize keyboard commands.
 * The commands are as follows:
 * 
 * 		Space = add acceleration
 * 		Back space = subtract acceleration
 * 		Right = turn right
 * 		Left = turn left
 * 		Up = rotate upwards
 * 		Down = rotate downwards
 * 		Z = rotate counter-clockwise
 * 		X = rotate clockwise
 * 		H = toggle head up display on and off
 * 
 * The simulator will end when the plane crashes (doesn't land on one of the runways or
 * impacts the runway with a vertical velocity greater than 10). There is a minimum
 * takeoff velocity, and the plane will "stall" if it has a velocity magnitude less than
 * 25. Between the speeds 25 and 80, the plane will experience a "gravity" force that
 * rotates it downwards.
 * 
 * This class uses 4 other classes: GAirplane, GAirplaneConstants, GAirplaneWorld, and
 * GAirplaneMath.
 * 
 * So with that, enjoy the flight simulator!
 */

import acm.program.*;

import acm.graphics.*;

import java.awt.Color;
import java.awt.event.*;

public class GraphicsContest extends GraphicsProgram {

	// The width of the graphics window
	public static final int APPLICATION_WIDTH = 1000;
	// The height of the graphics window
	public static final int APPLICATION_HEIGHT = 1000;

	// The delay between each frame. For my computer, I set this to zero.
	private static final int DELAY = 0;
	// huds keeps track of whether or not the Head Up display is on
	private boolean huds = true;
	// liveliness keeps track of whether or not the player is alive
	public static boolean liveliness = true;

	// Here are some instance variables
	private GAirplane plane;
	private GAirplaneWorld world;
	// GAirplaneMath does all of the nasty math stuff
	private GAirplaneMath eq = new GAirplaneMath();
	private int[][] pixelArr;
	private GImage worldview;
	private GObject hudsDisplayObject;

	// Listen to the keyboard
	public void init() {
		addKeyListeners();
	}

	public void run() {
		println("Welcome to the flight simulator! The keyboard commands are as follows:");
		println("Space = add acceleration");
		println("Back space = subtract acceleration");
		println("Right = turn right");
		println("Left = turn left");
		println("Up = rotate upwards");
		println("Down = rotate downwards");
		println("Z = rotate counter-clockwise");
		println("X = rotate clockwise");
		println("H = toggle head up display on and off");
		println("Enjoy the flight!");
		plane = new GAirplane();
		world = new GAirplaneWorld();
		// Add the plane to the screen
		GObject planeObject = plane.getPlane();
		add(planeObject);
		planeObject.sendToFront();
		// Add the world to the screen
		pixelArr = world.updateView(plane);
		worldview = new GImage(pixelArr);
		add(worldview);
		worldview.sendToBack();

		// If huds is true, display the head up display.
		if (huds == true) {
			hudsDisplayObject = updateHuds(plane);
			add(hudsDisplayObject);
		}

		// While the player is alive, run the loop
		while (liveliness == true) {
			// While the image is on the screen, calculate the
			// pixel array for the new image
			pixelArr = world.updateView(plane);
			remove(worldview);
			remove(hudsDisplayObject);
			worldview = new GImage(pixelArr); // Create the new worldview
			add(worldview); // Add the new worldview
			worldview.sendToBack();
			// The plane's new position is its old position
			// plus its velocity
			double[] newPos = eq.vecSum(plane.getPos(), plane.getVel());
			plane.setPos(newPos);

			double[] velocity = plane.getVel();
			double acceleration = plane.getAccel();
			double magVel = eq.magnitude(velocity);
			// Update the velocity based on the acceleration
			// We can't have a negative velocity
			if (magVel + acceleration > 0) {
				double[] normVel = eq.norm(velocity);
				double[] newVel = eq.vecSum(velocity, eq.scalarProd(acceleration, normVel));
				plane.setVel(newVel);
			} else { // If the resulting velocity would have been negative
				plane.setAccel(0); // Set the acceleration equal to zero
			}

			// display the head up display if huds is true
			if (huds == true) {
				hudsDisplayObject = updateHuds(plane);
				add(hudsDisplayObject);
			}
			// pause the amound delay
			pause(DELAY);
		}
		// The game has ended. Print final info
		double[] velocity = plane.getVel();
		println("Your game has ended! You collided with the ground at a speed of " + (int) Math.abs(velocity[2]));
	}

	/*
	 * updateHuds method:: Takes a GAirplane and returns the GObject head up
	 * display. This method constructs all of the objects in the head up display
	 * based on the plane's characteristics.
	 */
	private GObject updateHuds(GAirplane plane) {
		double[] pos = plane.getPos();
		double[] vel = plane.getVel();
		double velMag = eq.magnitude(vel);

		GCompound hudsDisplay = new GCompound();
		// Color limeGreen = new Color(0, 255, 0);
		/*
		 * I was originally going to use lime green for the color of the head up
		 * display, but you can't see it very well. That means that limeGreen is
		 * actually black...
		 */
		Color limeGreen = Color.BLACK;

		// Create the center circle on the screen
		GOval hudsCircle = new GOval(APPLICATION_WIDTH / 2 - GAirplaneConstants.HUDS_CIRCLE_DIAM / 2,
				APPLICATION_HEIGHT / 2 - GAirplaneConstants.HUDS_CIRCLE_DIAM / 2, GAirplaneConstants.HUDS_CIRCLE_DIAM,
				GAirplaneConstants.HUDS_CIRCLE_DIAM);
		hudsCircle.setColor(limeGreen);
		hudsDisplay.add(hudsCircle);

		// Create the left horizontal line
		double[][] leftHorizPt = { { 150, 500 }, { 450, 500 } };
		GLine leftHoriz = new GLine(leftHorizPt[0][0], leftHorizPt[0][1], leftHorizPt[1][0], leftHorizPt[1][1]);
		leftHoriz.setColor(limeGreen);
		hudsDisplay.add(leftHoriz);

		// Create the right horizontal line
		double[][] rightHorizPt = { { 550, 500 }, { 850, 500 } };
		GLine rightHoriz = new GLine(rightHorizPt[0][0], rightHorizPt[0][1], rightHorizPt[1][0], rightHorizPt[1][1]);
		rightHoriz.setColor(limeGreen);
		hudsDisplay.add(rightHoriz);

		// Create the position label in the bottom
		// left corner
		String posString = "POSITION: (";
		for (int i = 0; i < 3; i++) {
			posString += (int) pos[i];
			if (i < 2) {
				posString += ", ";
			}
		}
		posString += ")";
		GLabel posLabel = new GLabel(posString);
		posLabel.setColor(limeGreen);
		posLabel.setLocation(100, 900);
		hudsDisplay.add(posLabel);

		// Draw the vert line by the altitude indicator
		GLine altitudeLine = new GLine(100, 100, 100, 400);
		altitudeLine.setColor(limeGreen);
		hudsDisplay.add(altitudeLine);

		// Draw the word "altitude" above the indicator
		GLabel altitudeLabel = new GLabel("ALTITUDE");
		altitudeLabel.setLocation(100 - altitudeLabel.getWidth() / 2, 100 - altitudeLabel.getHeight() / 2);
		altitudeLabel.setColor(limeGreen);
		hudsDisplay.add(altitudeLabel);
		double altitude = pos[2] - GAirplaneConstants.TOTAL_HEIGHT;

		// Create the altitude marker for the indicator
		GRect altitudeMarker = new GRect(30, 10);
		altitudeMarker.setColor(limeGreen);
		altitudeMarker.setFilled(true);
		altitudeMarker.setFillColor(limeGreen);
		if (altitude <= 500) {
			altitudeMarker.setFillColor(Color.RED);
		}
		altitudeMarker.setLocation(85, 390 - altitude / 50);
		hudsDisplay.add(altitudeMarker);

		// Create the altitude label for the indicator
		String altLabel = "";
		altLabel += (int) altitude;
		GLabel altitudeNum = new GLabel(altLabel);
		double altNumY = 400 - altitude / 50;
		altitudeNum.setLocation(120, altNumY);
		altitudeNum.setColor(limeGreen);
		hudsDisplay.add(altitudeNum);

		// Draw the line for the airspeed indicator
		GLine airspeedLine = new GLine(900, 100, 900, 400);
		airspeedLine.setColor(limeGreen);
		hudsDisplay.add(airspeedLine);

		// Draw the word "airspeed" above the indicator
		GLabel airspeedLabel = new GLabel("AIRSPEED");
		airspeedLabel.setLocation(900 - airspeedLabel.getWidth() / 2, 100 - airspeedLabel.getHeight() / 2);
		airspeedLabel.setColor(limeGreen);
		hudsDisplay.add(airspeedLabel);

		// Create the airspeed marker for the indicator
		GRect airspeedMarker = new GRect(30, 10);
		airspeedMarker.setColor(limeGreen);
		airspeedMarker.setFilled(true);
		if (velMag < 50) {
			airspeedMarker.setFillColor(Color.RED);
		} else if (velMag < 80) {
			airspeedMarker.setFillColor(Color.YELLOW);
		} else {
			airspeedMarker.setFillColor(Color.GREEN);
		}
		airspeedMarker.setLocation(885, 390 - 1.5 * velMag);
		hudsDisplay.add(airspeedMarker);

		// Create the airspeed label for the indicator
		String airspeedString = "";
		airspeedString += (int) velMag;
		GLabel airspeedNum = new GLabel(airspeedString);
		double airspeedNumY = 400 - 1.5 * velMag;
		airspeedNum.setLocation(860, airspeedNumY);
		airspeedNum.setColor(limeGreen);
		hudsDisplay.add(airspeedNum);

		// Create the vertical speed text
		String vertSpeedString = "VERTICAL SPEED: ";
		vertSpeedString += (int) vel[2];
		GLabel vertSpeedLabel = new GLabel(vertSpeedString);
		vertSpeedLabel.setColor(limeGreen);
		vertSpeedLabel.setLocation(900 - vertSpeedLabel.getWidth() / 2, 430);
		hudsDisplay.add(vertSpeedLabel);

		// Draw the line for the acceleration indicator
		GLine accelLine = new GLine(900, 790, 900, 900);
		accelLine.setColor(limeGreen);
		hudsDisplay.add(accelLine);

		// Draw the word "thrust" for the accel indicator
		GLabel accelLabel = new GLabel("THRUST");
		accelLabel.setLocation(900 - accelLabel.getWidth() / 2, 910 + accelLabel.getHeight() / 2);
		accelLabel.setColor(limeGreen);
		hudsDisplay.add(accelLabel);

		// Create the acceleration marker for the indicator
		GRect accelMarker = new GRect(30, 10);
		accelMarker.setColor(limeGreen);
		accelMarker.setFilled(true);
		accelMarker.setFillColor(limeGreen);
		accelMarker.setLocation(885, 840 - 50 * plane.getAccel());
		hudsDisplay.add(accelMarker);

		// Create the acceleration label for the indicator
		String accelString = "";
		double acceleration = 5 * plane.getAccel();
		accelString += (int) acceleration;
		GLabel accelNum = new GLabel(accelString);
		double accelNumY = 850 - 50 * plane.getAccel();
		accelNum.setLocation(860, accelNumY);
		accelNum.setColor(limeGreen);
		hudsDisplay.add(accelNum);

		// Draw the top vertical center line
		GLine topVert = new GLine(500, 100, 500, 450);
		topVert.setColor(limeGreen);
		hudsDisplay.add(topVert);

		// Draw the bottom vertical center line
		GLine botVert = new GLine(500, 550, 500, 650);
		botVert.setColor(limeGreen);
		hudsDisplay.add(botVert);

		return (hudsDisplay);
	}

	/*
	 * setLiveliness method:: Takes a boolean and returns nothing. This method
	 * sets the boolean liveliness to whatever the given boolean is.
	 */
	public static void setLiveliness(boolean p) {
		liveliness = p;
	}

	/*
	 * keyPressed method:: This method is called when a key is pressed on the
	 * keyboard.
	 */
	public void keyPressed(KeyEvent e) {
		// Save the key pressed as an int in key
		int key = e.getKeyCode();

		// Space bar = add acceleration
		if (key == KeyEvent.VK_SPACE) {
			plane.addAccel();
		}

		// Back space = subtract acceleration
		if (key == KeyEvent.VK_BACK_SPACE) {
			plane.minusAccel();
		}

		// H key = toggle head up display on and off
		if (key == KeyEvent.VK_H) {
			huds = !huds;
		}

		// Right key = rotate the plane to the right
		if (key == KeyEvent.VK_RIGHT) {
			double[] newOrient = plane.getOrient();
			double angle = -Math.PI / 180;
			// If the plane is upside down, flip angle
			if (newOrient[2] < 0) {
				angle = -angle;
			}
			double[][] rotationMatrix = eq.rotZCalc(angle);
			double[] newVel = plane.getVel();
			newVel = eq.transformTimes(rotationMatrix, newVel);
			newOrient = eq.transformTimes(rotationMatrix, newOrient);
			plane.setVel(newVel);
			plane.setOrient(newOrient);
		}

		// Left key = rotate the plane to the left
		if (key == KeyEvent.VK_LEFT) {
			double[] newOrient = plane.getOrient();
			double angle = Math.PI / 180;
			// If the plane is upside down, flip angle
			if (newOrient[2] < 0) {
				angle = -angle;
			}
			double[][] rotationMatrix = eq.rotZCalc(angle);
			double[] newVel = plane.getVel();
			newVel = eq.transformTimes(rotationMatrix, newVel);
			newOrient = eq.transformTimes(rotationMatrix, newOrient);
			plane.setVel(newVel);
			plane.setOrient(newOrient);
		}

		// Only rotate up and down if the velocity magnitude is greater than 25.
		// This mimics a stalling feature
		if (plane.getVelMag() > 25) {

			// Up key = rotate the plane upwards
			if (key == KeyEvent.VK_UP) {
				double[] pos = plane.getPos();
				double[] newVel = plane.getVel();
				// If we're on the runway, don't rotate up until we
				// hit the min takeoff velocity
				if (pos[2] == GAirplaneConstants.TOTAL_HEIGHT) {
					if (Math.abs(newVel[1]) >= GAirplaneConstants.MIN_TAKEOFF_VELOCITY) {
						double[] newOrient = plane.getOrient();
						double[] rotAxis = eq.cross(newVel, newOrient);
						double angle = Math.PI / 180;
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
						plane.setVel(newVel);
						plane.setOrient(newOrient);
					}
					// Otherwise rotate as normal
				} else {
					double[] newOrient = plane.getOrient();
					double[] rotAxis = eq.cross(newVel, newOrient);
					double angle = Math.PI / 180;
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
					plane.setVel(newVel);
					plane.setOrient(newOrient);
				}
			}

			// Down key = rotate plane downwards
			if (key == KeyEvent.VK_DOWN) {
				double[] newVel = plane.getVel();
				double[] newOrient = plane.getOrient();
				double[] rotAxis = eq.cross(newVel, newOrient);
				double angle = -Math.PI / 180;
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
				plane.setVel(newVel);
				plane.setOrient(newOrient);
			}
		}

		// Z key = rotate plane counter-clockwise
		if (key == KeyEvent.VK_Z) {
			double[] newVel = plane.getVel();
			double[] newOrient = plane.getOrient();
			double angle = -Math.PI / 180;
			if (newVel[1] < 0) {
				angle = -angle;
			}
			double[][] rotationMatrix = eq.rotYCalc(angle);
			newVel = eq.transformTimes(rotationMatrix, newVel);
			newOrient = eq.transformTimes(rotationMatrix, newOrient);
			plane.setVel(newVel);
			plane.setOrient(newOrient);
		}

		// X key = rotate plane clockwise
		if (key == KeyEvent.VK_X) {
			double[] newVel = plane.getVel();
			double[] newOrient = plane.getOrient();
			double angle = Math.PI / 180;
			if (newVel[1] < 0) {
				angle = -angle;
			}
			double[][] rotationMatrix = eq.rotYCalc(angle);
			newVel = eq.transformTimes(rotationMatrix, newVel);
			newOrient = eq.transformTimes(rotationMatrix, newOrient);
			plane.setVel(newVel);
			plane.setOrient(newOrient);
		}
	}
}
