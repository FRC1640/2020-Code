package systems.drive.controllers;

import java.util.HashMap;
import java.util.Map.Entry;

import com.revrobotics.CANSparkMax.IdleMode;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;

import robot.Devices;
import robot.Gyro;
import systems.drive.pivot.CVTPivot;
import systems.drive.pivot.Pivot;
import utilities.LogUtil;
import utilities.Vector2;

public class SwerveController {

	public static final double W = 21.0;
	public static final double L = 27.25;

	public static enum SwerveMode {
		ROBOT_CENTRIC,
		FIELD_CENTRIC;
	}

	public static enum StraightMode {
		RESET_PID,
		DISABLE_PID,
		ENABLE_PID;
	}

	private SwerveMode swerveMode;
	private boolean habDrivePivots;

	private StraightMode current;
	private StraightMode next;
	
	private PIDController x2PID;

	private double targetHeading = 0.0;
	private double x2PidValue;

	HashMap<Pivot,Vector2> pivotMap;

	public SwerveController (HashMap<Pivot,Vector2> pivotMap) {
		this.pivotMap = pivotMap;
		swerveMode = SwerveMode.ROBOT_CENTRIC;
		LogUtil.log("SwerveMode", "Switching to: " + swerveMode.toString());

		current = null;
		next = StraightMode.ENABLE_PID;
		PIDSource x2Src = new PIDSource() {
			@Override
			public void setPIDSourceType(PIDSourceType pidSource) {
			}

			@Override
			public double pidGet() {
				double dAngle = (targetHeading - (Devices.getGyro().getYaw()-180));
				return Math.sin(dAngle * Math.PI / 180.0);
			}

			@Override
			public PIDSourceType getPIDSourceType() {
				return PIDSourceType.kDisplacement;
			}
		};

		PIDOutput x2Out = new PIDOutput() {

			@Override
			public void pidWrite(double output) {
				x2PidValue = output;
			}
		};
		// x2PID = new PIDController(1.25, 0.0125, 0.0, x2Src, x2Out); 
		x2PID = new PIDController(1.9, 0.0, 0.0, x2Src, x2Out); 


		
	}

	public void setRampIdle(IdleMode idleMode, double rampRate) {
		for (Pivot piv : pivotMap.keySet()) {
			piv.getDriveMotor().setIdleMode(idleMode);
			piv.getDriveMotor().setOpenLoopRampRate(rampRate);
			piv.getDriveMotor().burnFlash();
		}
	}

	/*
	 *	Swerve Mode Stuff
	 */
	public void setSwerveMode (SwerveMode sm) {
		if (sm != swerveMode) { LogUtil.log("SwerveMode", "Switching to: " + sm.toString()); }
		synchronized (swerveMode) { swerveMode = sm; }
	}

	public SwerveMode getSwerveMode() {
		return swerveMode;
	}

	public void setRampRate(double rate) {
		for (Pivot piv : pivotMap.keySet()) {
			piv.getDriveMotor().setOpenLoopRampRate(rate);
			piv.getDriveMotor().burnFlash();
		}
	}

	public void toggleFieldCentric () {
		synchronized (swerveMode) {
			if (swerveMode == SwerveMode.FIELD_CENTRIC) { swerveMode = SwerveMode.ROBOT_CENTRIC; }
			else { swerveMode = SwerveMode.FIELD_CENTRIC; }
		}
	}

	public void resetPID () {
		x2PID.reset();
		x2PidValue = 0;
	}

	/*
	 *	Hab Drive Stuff
	 */
	public void setHabDrivePivots (boolean value) { habDrivePivots = value; }

	public boolean getHabDrivePivots () { return habDrivePivots; }

	/*
	 *	Drive Code
	 */

	public double getx2PIDValue () {
		return x2PidValue;
	}

	public void setx2PIDValue (double val) {
		x2PidValue = val;
	}

	public void setState (StraightMode state) {
		next = state;
	}

	public void drive (double x1, double y1, double x2, boolean isSlow) {

		drive(x1, y1, x2, isSlow, true);

	}

	public void drivePolar (double mag, double angD, double x2, boolean slowMode) {
		double angR = Math.toRadians(angD);
		drive(mag * Math.cos(angR), mag * Math.sin(angR), x2, slowMode);
	}
	
	public void updatePivotDirections (String pivId, double pivSpeed, double pivAngle) {

		double x = pivSpeed * (Math.sin(pivAngle));
		double y = pivSpeed * (Math.cos(pivAngle));

		Vector2 vt = new Vector2(x, y);

		for (Pivot piv : pivotMap.keySet()) {

			if (piv.getName() == pivId) {
				pivotMap.get(piv).set(vt);
			}	
		}
	}

	public void setPivotDirections () {
		for (Entry<Pivot,Vector2> entry : pivotMap.entrySet()) {
			Pivot piv = entry.getKey();
			Vector2 v = entry.getValue();
			
			double mag = v.magnitude();
				((CVTPivot)piv).setSpeed(mag, true);

				if (v.magnitude() != 0) { piv.setTargetAngleR(v.angleR()); }

				try {
				} catch (Exception e) {
					LogUtil.warn(getClass(), String.format("%s is not a CVTPivot!", piv.getName()));
				}

		}
	}

	public void drive (double x1, double y1, double x2, boolean isSlow, boolean enableWheels) {
		current = next;

		switch (current) {
			case RESET_PID : {
				x2PID.disable();
				x2PID.reset();
				x2PidValue = 0;
				targetHeading = 0;
				next = StraightMode.ENABLE_PID;
			} break;
			case DISABLE_PID: {
				if (x2PID.isEnabled()) {
					x2PID.disable();
				}
				x2PID.disable();
			} break;
			case ENABLE_PID: {
				if (!x2PID.isEnabled()) {
					x2PID.enable();
				}
				targetHeading = x2;
				x2 = x2PidValue;
			} break;
		}
			
			

		// Clamp x1, y1, and x2 to be between -1 and 1
		// Really only matters for x2, since that can go over for gyro-correction
		x1 = Math.max(-1.0, Math.min(x1, 1.0));
		y1 = Math.max(-1.0, Math.min(y1, 1.0));
		x2 = Math.max(-1.0, Math.min(x2, 1.0));

		double yaw = Math.toRadians(Devices.getGyro().getYaw());
		double sin = Math.sin(yaw);
		double cos = Math.cos(yaw);

		if (Math.abs(x2) < 0.15) {
			x2 = 0;
		}

		boolean isFieldCentric;
		synchronized (swerveMode) { isFieldCentric = (swerveMode == SwerveMode.FIELD_CENTRIC); }

		if (isFieldCentric) {
			double temp = x1 * cos + y1 * sin;
			y1 =  -x1 * sin + y1 * cos;
			x1 = temp;
		}

		Vector2 tVec = new Vector2(x1, y1);
		if (tVec.magnitude() < 0.03) { tVec.reset(); }

		double max = 0.0;
		for (Pivot piv : pivotMap.keySet()) {
			Vector2 vt = piv.getPosition().copy().rotateD(-90.0).unit().multiply(x2).add(tVec);
			max = Math.max(max, vt.magnitude());
			pivotMap.get(piv).set(vt);
		}
		
		double s = Math.max(Math.sqrt(x1*x1 + y1*y1), Math.abs(x2));

		for (Pivot piv : pivotMap.keySet()) {
			if (max > 1e-14) {
				pivotMap.get(piv).multiply(s/max);
			}
			else {
				pivotMap.get(piv).reset();
			}
		}

		/* *********** PIVOT CONTROL UPDATES *********** */

		for (Entry<Pivot,Vector2> entry : pivotMap.entrySet()) {
			Pivot piv = entry.getKey();
			Vector2 v = entry.getValue();
			
			double mag = v.magnitude();

			if(!habDrivePivots) {
				((CVTPivot)piv).setSpeed(enableWheels ? mag : 0, isSlow);

				if (v.magnitude() != 0) { piv.setTargetAngleR(v.angleR()); }

				try {
				} catch (Exception e) {
					LogUtil.warn(getClass(), String.format("%s is not a CVTPivot!", piv.getName()));
				}
			}

		}
	}

	public void testDrive (double x1, double y1) {
		for (Pivot piv : pivotMap.keySet()) {
			piv.setMotorDirect(x1);
			piv.setRotationSpeed(y1);
			// System.out.println(((CVTPivot)piv).getPivotAngleD());
		}
	}

	public void pointWheels (double angle) {
		drive(Math.cos(Math.toRadians(angle)), Math.sin(Math.toRadians(angle)), 0.0, false, false);
	}

	public void enable () {
		for (Pivot piv : pivotMap.keySet()) {
			piv.enable();
		}
	}

	public void disable () {
		for (Pivot piv : pivotMap.keySet()) {
			piv.disable();
		}
	}
}