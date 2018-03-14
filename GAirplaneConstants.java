
/*
 * File: GAirplaneConstants.java
 * --------------------------
 * Author: Tylor Jilk
 * Section Leader: Benson Kung
 * 
 * This file contains constants for the GAirplane and related classes.
 */

import acm.program.*;
import acm.graphics.*;

public interface GAirplaneConstants{

	// The total length of the airplane
	public static final double TOTAL_LENGTH = 200;
	// How far down in the window the plane appears
	public static final double TOTAL_WINDOW_RATIO = 0.9;
	
	// The height of the rudder
	public static final double RUDDER_HEIGHT = 66.6667;
	// The width of the rudder
	public static final double RUDDER_WIDTH = 10;
	// The width of the rear wings
	public static final double REAR_WIDTH = 66.6667;
	// The height of the rear wings
	public static final double REAR_HEIGHT = 16.6667;
	// How far above the bottom of the plane do the wings begin
	public static final double WING_Y_OFFSET = 133.3333;
	// The width of the wings
	public static final double WING_WIDTH = 200;
	// The height of the wings
	public static final double WING_HEIGHT = 33.3333;
	// How far from the bottom of the plane is the top of the nose
	public static final double NOSE_Y_OFFSET = 193.3333;
	// The width of the nose
	public static final double NOSE_WIDTH = 53.3333;
	// The width of the top of the body
	public static final double BODY_TOP_WIDTH = 42.66667;
	// The width of the bottom of the body
	public static final double BODY_BOTTOM_WIDTH = 11;
	// The height of the landing gear legs
	public static final double LANDING_GEAR_HEIGHT = 60;
	// The diameter of the landing gear wheels
	public static final double LANDING_GEAR_WHEEL_DIAM = 10;
	// How far the plane is off the ground
	public static final double TOTAL_HEIGHT = LANDING_GEAR_HEIGHT + LANDING_GEAR_WHEEL_DIAM;
	
	// The width of the runway
	public static final double RUNWAY_WIDTH = 200;
	// The length of the runway
	public static final double RUNWAY_LENGTH = 10000;
	// The ratio of the runway stripe width to the width of the runway
	public static final double RUNWAY_STRIPE_RATIO = 0.025;
	
	// The minimum takeoff velocity
	public static final double MIN_TAKEOFF_VELOCITY = 50;
	
	// The diameter of the circle in the middle of the huds display
	public static final double HUDS_CIRCLE_DIAM = 20;
	
	
}
