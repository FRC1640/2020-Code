package auton.commands;

import robot.Devices;
import systems.scoring.Intake;

public class DeployIntake extends Command {

	private Intake intake;

	public DeployIntake() {
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
		intake.setBarSolenoid(true);
	}

	@Override
	public boolean isDone() {
		// TODO Auto-generated method stub
		if (currentTime() >= startTime + 1000) {
			return intake.getSolenoidState();
		}
		return false;
	}

}
