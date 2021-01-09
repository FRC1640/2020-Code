package auton.commands;

import java.util.HashMap;

import com.revrobotics.CANEncoder;

import robot.Devices;
import systems.drive.controllers.SwerveController;
import systems.drive.controllers.SwerveController.StraightMode;
import systems.drive.pivot.Pivot;
import utilities.CANSparkMaxSendable;
import utilities.TrapezoidalCurve;
import utilities.Vector2;

public class TrapezoidalDrive extends DriveRamp {

	private SwerveController swerveController;
	private HashMap<Pivot, Vector2> pivotMap;

	private double x1;
	private double y1;
	private double x2;
	private double distance;

	private TrapezoidalCurve curve;

	private CANEncoder encoder;

	public TrapezoidalDrive(double x1, double y1, double x2, double distance) {
		super(x1, y1, x2, false, 0.4, 0.0);
		this.distance = distance;

		swerveController = Devices.getSwerveController();
		pivotMap = Devices.getPivotMap();
		curve = new TrapezoidalCurve(0.5, 0.5, distance, x1, 0.0, 0.0);

		for (Pivot pivot : pivotMap.keySet()) {
			if(pivot.getName().equals("BL")) {
				encoder = pivot.getEncoder();
			}
		}
	}

	@Override
	public void init() {
		super.init();
	}

	@Override
	public void run() {
		swerveController.drive(curve.getY(encoder.getPosition()), 0.0, 0.0, false);
	}

	@Override
	public boolean isDone() {
		if (encoder.getPosition() >= distance) {
			swerveController.drive(0.0, 0.0, 0.0, false);
			swerveController.setState(StraightMode.DISABLE_PID);
			return true;
		}
		return false;
	}

}