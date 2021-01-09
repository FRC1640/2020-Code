package auton.commands;

import robot.Devices;
import systems.scoring.Intake;

public class RetractIntake extends Command {

	private Intake intake;

	public RetractIntake() {
		this.intake = Devices.getIntake();
	}

	@Override
	public void init () {
		super.init();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		intake.update();
		intake.setBarSolenoid(false);
	}

	@Override
	public boolean isDone() {
		// TODO Auto-generated method stub
		if (currentTime() >= startTime + 1000) {
			return !intake.getSolenoidState();
		}
		return false;
	}

}