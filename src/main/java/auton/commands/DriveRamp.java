package auton.commands;

import java.util.HashMap;

import com.revrobotics.CANSparkMax.IdleMode;

import robot.Devices;
import systems.drive.controllers.SwerveController;
import systems.drive.controllers.SwerveController.StraightMode;
import systems.drive.pivot.Pivot;
import utilities.Vector2;

public class DriveRamp extends Drive {

	private double rampRate;
	private HashMap<Pivot, Vector2> pivotMap;
	private double prevRampRate;

	public DriveRamp(double x1, double y1, double x2, boolean driveStraight, double rampRate, double duration) {
		super(x1, y1, x2, duration, driveStraight);
		this.rampRate = rampRate;

		this.pivotMap = Devices.getPivotMap();
	}

	@Override
	public void init() {
		super.init();
		
		for (Pivot piv : pivotMap.keySet()) {
			this.prevRampRate = piv.getDriveMotor().getOpenLoopRampRate();
			piv.getDriveMotor().setOpenLoopRampRate(rampRate);
			piv.getDriveMotor().setIdleMode(IdleMode.kBrake);
			piv.getDriveMotor().burnFlash();
		}
		
	}

	@Override
	public boolean isDone() {
		if (super.isDone()) {
			for (Pivot piv : pivotMap.keySet()) {
				piv.getDriveMotor().setOpenLoopRampRate(prevRampRate);
				piv.getDriveMotor().setIdleMode(IdleMode.kCoast);
				piv.getDriveMotor().burnFlash();
			}
		}
		return super.isDone();
	}

}