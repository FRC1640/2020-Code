package utilities;

import systems.scoring.Shooter;

public final class MathUtil {

	private MathUtil() {
	}

	public static boolean inRange(double value, double lowerBound, double upperBound) {
		return (lowerBound <= value) && (value <= upperBound);
	}

	public static double constrain(double value, double lowerBound, double upperBound) {
		return Math.min(upperBound, Math.max(value, lowerBound));
	}

	public static double findClosestNumber(double nArr[], double target) {
		double closestNumber = nArr[0];

		for (double num : nArr) {
			if (Math.abs(num - target) < Math.abs(closestNumber - target)) {
				closestNumber = num;
			}
		}
		return closestNumber;
	}

	public static int findClosestNumber(int nArr[], int target) {
		int closestNumber = nArr[0];

		for (int num : nArr) {
			if (Math.abs(num - target) < Math.abs(closestNumber - target)) {
				closestNumber = num;
			}
		}
		return closestNumber;
		
	}

	public static float calculateDistanceFromTarget(double angleDegrees, double distanceToFloorInches, double heightOfTargetInches) {
		return (float) ((heightOfTargetInches-distanceToFloorInches)/(Math.tan(Math.toRadians(angleDegrees))));
	}

	public static double calculateHoodAngle(double ty) {
		double distance = MathUtil.calculateDistanceFromTarget(ty + Shooter.angleLimelight, Shooter.lHeight, Shooter.tHeight);
		return 1.0/(0.06*(distance - 10.0)) + (0.47);
	}
}
