package auton.commands;

import robot.Devices;
import systems.scoring.Intake;

public class RunIntake extends Command {

	private Intake intake;
	private double intakeSpeed;
	private double duration;

	public RunIntake(double intakeSpeed, double duration) {
		this.intake = Devices.getIntake();
		this.intakeSpeed = intakeSpeed;
		this.duration = duration * 1000.0;
	}

	@Override
	public void init() {
		super.init();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		intake.setBar(this.intakeSpeed);
	}

	@Override
	public boolean isDone() {
		// TODO Auto-generated method stub
		if (currentTime() >= startTime + duration) {
			intake.setBar(0.0);
			return true;
		}
		return false;
	}

}