package auton.commands;

import edu.wpi.first.wpilibj.Timer;
import robot.Devices;
import systems.drive.controllers.SwerveController;
import systems.drive.controllers.SwerveController.StraightMode;

public class Drive extends Command {

	private double x1;
	private double y1;
	private double x2;
	private double duration;
	private boolean driveStraight;

	private SwerveController swerveController;

	public Drive(double x1, double y1, double x2, double duration, boolean driveStraight) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.duration = duration * 1000;
		this.driveStraight = driveStraight;

		this.driveStraight = driveStraight;

		this.swerveController = Devices.getSwerveController();
	}

	@Override
	public void init() {
		super.init();
		swerveController.drive(0.0, 0.0, 0.0, false);
		if (driveStraight) {
			swerveController.setState(StraightMode.ENABLE_PID);
		} else {
			swerveController.setState(StraightMode.DISABLE_PID);
		}
		
	}

	@Override
	public void run() {
		swerveController.drive(x1, y1, x2, false);
	}

	@Override
	public boolean isDone() {
		if (currentTime() >= startTime + duration) {
			swerveController.drive(0.0, 0.0, 0.0, false);
			swerveController.setState(StraightMode.DISABLE_PID);
			return true;
		}
		return false;
	}

}