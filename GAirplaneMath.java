
/*
 * File: GAirplane.java
 * --------------------------
 * Author: Tylor Jilk
 * Section Leader: Benson Kung
 * 
 * This file does all of the dirty behind-the-scenes math work which 
 * somehow makes everything turn out ok. It contains many public method
 * that do complex vector algebra.
 */

import acm.program.*;

import java.awt.Color;

import acm.graphics.*;

public class GAirplaneMath {

	// Contructor stuff
	public GAirplaneMath() {

	}

	/*
	 * norm method:: Takes a double array and returns a normalized double array,
	 * of any number of dimensions.
	 */
	public double[] norm(double[] vector) {
		int dim = vector.length;
		double[] norm = new double[dim];
		double squares = 0;
		for (int i = 0; i < (dim); i++) {
			squares += Math.pow(vector[i], 2);
		}
		double length = Math.sqrt(squares);
		for (int j = 0; j < (dim); j++) {
			norm[j] = (1 / length) * vector[j];
		}
		return norm;
	}

	/*
	 * dotProd method:: Takes two double arrays and returns the dot product of
	 * the two as a double. It works for any number of dimensions.
	 */
	public double dotProd(double[] vec1, double[] vec2) {
		int dim = vec1.length;
		int dim2 = vec2.length;
		double dotProduct = 0;
		if (dim == dim2) {
			for (int k = 0; k < dim; k++) {
				dotProduct += vec1[k] * vec2[k];
			}
		}
		return dotProduct;
	}

	/*
	 * cross method:: Takes two arrays and returns another array, the cross
	 * product between the two. It only works in 3 dimensions.
	 */
	public double[] cross(double[] vec1, double[] vec2) {
		double entry1 = vec1[1] * vec2[2] - vec1[2] * vec2[1];
		double entry2 = -(vec1[0] * vec2[2] - vec1[2] * vec2[0]);
		double entry3 = vec1[0] * vec2[1] - vec1[1] * vec2[0];
		double[] cross = { entry1, entry2, entry3 };
		return cross;
	}

	/*
	 * scalarProd method:: Takes a double and a double[], and returns a
	 * double[]. It does scalar multiplication between the given double and
	 * double[], and returns the product double[]. It works in any number of
	 * dimensions.
	 */
	public double[] scalarProd(double scalar, double[] vec) {
		int length = vec.length;
		double[] scalarProd = new double[length];
		for (int i = 0; i < length; i++) {
			scalarProd[i] = scalar * vec[i];
		}
		return scalarProd;
	}

	/*
	 * vecSum method:: Takes two double[]'s and returns a double[]. This method
	 * calculates the vector sum between two vectors of the same arbitrary
	 * length and returns the sum.
	 */
	public double[] vecSum(double[] vec1, double[] vec2) {
		double[] vecSum = new double[vec1.length];
		if (vec1.length == vec2.length) {
			for (int i = 0; i < vec1.length; i++) {
				vecSum[i] = vec1[i] + vec2[i];
			}
		}
		return vecSum;
	}

	/*
	 * transform method:: Takes a double[][] and returns a double[][]. This
	 * method takes a set of basis vectors in a 2D array, and calculates a
	 * transformation matrix. If you multiply a vector in the original
	 * coordinates by this transformation matrix, the resulting vector will be
	 * the same vector, just represented in the standard fixed Cartesian
	 * coordinates, rather than the original coordinates. This is important for
	 * the image rendering because it takes the ray vector that is in the
	 * plane's moving coordinates and transforms it into the fixed coordinates
	 * of the world. Only works in 3 dimensions.
	 */
	public double[][] transform(double[][] orig) {
		double[][] transform = new double[3][3];
		// Get all of the values for calculations!
		double a = orig[0][0];
		double b = orig[1][0];
		double c = orig[2][0];
		double d = orig[0][1];
		double f = orig[1][1];
		double g = orig[2][1];
		double h = orig[0][2];
		double j = orig[1][2];
		double k = orig[2][2];

		// Assign the values of the transformation matrix
		// Ouch.
		transform[0][0] = (-g * j + f * k) / (-c * f * h + b * g * h + c * d * j - a * g * j - b * d * k + a * f * k);
		transform[0][1] = (-g * h + d * k) / (c * f * h - b * g * h - c * d * j + a * g * j + b * d * k - a * f * k);
		transform[0][2] = (f * h - d * j) / (c * f * h - b * g * h - c * d * j + a * g * j + b * d * k - a * f * k);

		transform[1][0] = (-c * j + b * k) / (c * f * h - b * g * h - c * d * j + a * g * j + b * d * k - a * f * k);
		transform[1][1] = (-c * h + a * k) / (-c * f * h + b * g * h + c * d * j - a * g * j - b * d * k + a * f * k);
		transform[1][2] = (b * h - a * j) / (-c * f * h + b * g * h + c * d * j - a * g * j - b * d * k + a * f * k);

		transform[2][0] = (c * f - b * g) / (c * f * h - b * g * h - c * d * j + a * g * j + b * d * k - a * f * k);
		transform[2][1] = (c * d - a * g) / (-c * f * h + b * g * h + c * d * j - a * g * j - b * d * k + a * f * k);
		transform[2][2] = (b * d - a * f) / (c * f * h - b * g * h - c * d * j + a * g * j + b * d * k - a * f * k);

		return transform;
	}

	/*
	 * transformTimes method:: Takes a double[][] and a double[], and returns a
	 * double[]. This method multiplies a matrix by a vector and returns the
	 * vector resultant.
	 */
	public double[] transformTimes(double[][] transform, double[] vec) {
		double[] transformTimes = new double[3];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				transformTimes[i] += transform[i][j] * vec[j];
			}
		}
		return transformTimes;
	}

	/*
	 * rotZCalc method:: Takes a double and returns a double[][]. This method
	 * takes an angle and constructs a rotation matrix that rotates things about
	 * the z axis.
	 */
	public double[][] rotZCalc(double angle) {
		double[][] rotMat = new double[3][3];
		rotMat[0][0] = Math.cos(angle);
		rotMat[0][1] = -Math.sin(angle);
		rotMat[0][2] = 0;

		rotMat[1][0] = Math.sin(angle);
		rotMat[1][1] = Math.cos(angle);
		rotMat[1][2] = 0;

		rotMat[2][0] = 0;
		rotMat[2][1] = 0;
		rotMat[2][2] = 1;

		return rotMat;
	}

	/*
	 * rotXCalc method:: Takes a double and returns a double[][]. This method
	 * takes an angle and constructs a rotation matrix that rotates things about
	 * the x axis.
	 */
	public double[][] rotXCalc(double angle) {
		double[][] rotMat = new double[3][3];

		rotMat[0][0] = 1;
		rotMat[0][1] = 0;
		rotMat[0][2] = 0;

		rotMat[1][0] = 0;
		rotMat[1][1] = Math.cos(angle);
		rotMat[1][2] = -Math.sin(angle);

		rotMat[2][0] = 0;
		rotMat[2][1] = Math.sin(angle);
		rotMat[2][2] = Math.cos(angle);

		return rotMat;
	}

	/*
	 * rotYCalc method:: Takes a double and returns a double[][]. This method
	 * takes an angle and constructs a rotation matrix that rotates things about
	 * the y axis.
	 */
	public double[][] rotYCalc(double angle) {
		double[][] rotMat = new double[3][3];

		rotMat[0][0] = Math.cos(angle);
		rotMat[0][1] = 0;
		rotMat[0][2] = Math.sin(angle);

		rotMat[1][0] = 0;
		rotMat[1][1] = 1;
		rotMat[1][2] = 0;

		rotMat[2][0] = -Math.sin(angle);
		rotMat[2][1] = 0;
		rotMat[2][2] = Math.cos(angle);

		return rotMat;
	}

	/*
	 * magnitude method:: Takes a double[] and returns a double. This method
	 * calculates the magnitude of a given vector and returns this result.
	 */
	public double magnitude(double[] vec) {
		double mag = 0;
		for (int i = 0; i < 3; i++) {
			mag += Math.pow(vec[i], 2);
		}
		mag = Math.sqrt(mag);
		return mag;
	}
}
