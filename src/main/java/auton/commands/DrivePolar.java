package auton.commands;

import edu.wpi.first.wpilibj.Timer;
import robot.Devices;
import systems.drive.controllers.SwerveController;
import systems.drive.controllers.SwerveController.StraightMode;

public class DrivePolar extends Command {

	private double angle;
	private double speed;
	private double x2;
	private boolean slowMode;
	private double duration;

	private SwerveController swerveController;

	public DrivePolar(double angle, double speed, double x2, boolean slowMode, double duration) {
		this.angle = angle;
		this.speed = speed;
		this.x2 = x2;
		this.slowMode = slowMode;
		this.duration = duration * 1000;

		this.swerveController = Devices.getSwerveController();
	}

	@Override
	public void init() {
		super.init();
		swerveController.drive(0.0, 0.0, 0.0, false);
		swerveController.setState(StraightMode.ENABLE_PID);
	}

	@Override
	public void run() {
		swerveController.drivePolar(speed, angle, x2, slowMode);
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