package systems.scoring;

import robot.Controller;
import robot.Devices;
import robot.Controller.Axis;
import robot.Controller.Button;
import robot.Controller.ButtonEvent;
import systems.RobotSystem;

public class IntakeSystem extends RobotSystem {

	private Intake intake;
	private Controller driverController;

	public IntakeSystem() {
		super("Intake System");
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		driverController = Devices.getDriverController();
		intake = Devices.getIntake();

		driverController.registerButtonListener(ButtonEvent.PRESS, Button.A, () -> {
			intake.ejectBar();
		});
		driverController.registerButtonListener(ButtonEvent.PRESS, Button.B, () -> {
			intake.retractBar();
		});
	}

	@Override
	public void preStateUpdate() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void postStateUpdate() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void disabledInit() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void disabledUpdate() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void autonInit() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void autonUpdate() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void teleopInit() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void teleopUpdate() throws Exception {
		// TODO Auto-generated method stub
		if (driverController.getAxis(Axis.RT) > 0.2) {
			intake.setBar(-driverController.getAxis(Axis.RT));
		} else if (driverController.getAxis(Axis.LT) > 0.2) {
			intake.setBar(driverController.getAxis(Axis.LT));
		} else {
			intake.setBar(0.0);
		}
	}

	@Override
	public void testInit() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void testUpdate() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void enable() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void disable() throws Exception {
		// TODO Auto-generated method stub

	}
}