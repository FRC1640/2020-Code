package auton.commands;

import java.util.HashMap;

import com.revrobotics.CANEncoder;

import edu.wpi.first.wpilibj.Timer;
import robot.Devices;
import systems.drive.controllers.SwerveController;
import systems.drive.controllers.SwerveController.StraightMode;
import systems.drive.pivot.Pivot;
import systems.scoring.Intake;
import utilities.Vector2;

public class DriveEncoder extends Command {

	private double x1;
	private double y1;
	private double x2;
	private double distance;
	private boolean useIntake;
	private Intake intake;
	private double initialCount;

	private SwerveController swerveController;
	private HashMap<Pivot, Vector2> pivotMap;

	private CANEncoder encoder;

	public DriveEncoder(double x1, double y1, double x2, double distance) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.distance = distance;

		this.swerveController = Devices.getSwerveController();

		pivotMap = Devices.getPivotMap();

		for (Pivot pivot : pivotMap.keySet()) {
			if(pivot.getName().equals("BR")) {
				encoder = pivot.getEncoder();
			}
		}
	}

	@Override
	public void init() {
		super.init();
		
		initialCount = encoder.getPosition();
		swerveController.drive(0.0, 0.0, 0.0, false);
		swerveController.setState(StraightMode.ENABLE_PID);
		
	}

	@Override
	public void run() {
		// System.out.println(encoder.getPosition());
		swerveController.drive(x1, y1, x2, true);
		// System.out.println(encoder.getPosition());
	}

	@Override
	public boolean isDone() {
		if (Math.abs(encoder.getPosition() - initialCount) >= distance) {
			swerveController.drive(0.0, 0.0, 0.0, false);
			swerveController.setState(StraightMode.DISABLE_PID);
			return true;
		}
		return false;
	}

}