package systems.climber.controllers;

import robot.Controller;
import robot.Devices;
import robot.Gyro;
import robot.Controller.Button;
import robot.Controller.ButtonEvent;
import systems.climber.Climber;
import systems.climber.Climber.GearPosition;
import systems.drive.controllers.SwerveController;
import systems.drive.controllers.SwerveController.StraightMode;
import systems.drive.controllers.SwerveController.SwerveMode;
import utilities.IController;
import utilities.TimingUtil2;

public class ClimberFullAutoController implements IController {
	
	private enum ClimbState {
		IDLE, INIT, ROTATE, DEPLOY, RAISE, DRIVE, GEAR_TORQUE, PULL_UP, FIN
	}

	private Climber climber;

	private Gyro gyro;

	private SwerveController swerveController;

	private Controller opController;


	private ClimbState currentState;
	private ClimbState nextState;

	public ClimberFullAutoController(Climber c) {
		this.climber = c;
		this.swerveController = Devices.getSwerveController();
		this.gyro = Devices.getGyro();
		this.opController = Devices.getOpController();

		currentState = ClimbState.INIT;
	}

	@Override
	public void activate() {
		// TODO Auto-generated method stub
		climber.setSpeed(0.0);
		swerveController.drive(0.0, 0.0, 0.0, false);
	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub
		climber.setSpeed(0.0);
		swerveController.drive(0.0, 0.0, 0.0, false);
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
		boolean onChange = (nextState != currentState);
		currentState = nextState;

		if (opController.getButton(Button.START) && opController.getButton(Button.SELECT)) {
			nextState = ClimbState.INIT;
		}

		switch (currentState) {
			case IDLE: {
				if (onChange) {
					climber.setSpeed(0.0);
					swerveController.drive(0.0, 0.0, 0.0, false);
				}

				if (opController.getButton(Button.RT) && opController.getButton(Button.LT)) {
					nextState = ClimbState.INIT;
				}
			} break;
			case INIT: {
				climber.shiftGear(GearPosition.SPEED);

				if (climber.getShiftGearState()) {
					nextState = ClimbState.ROTATE;
				}
			} break;
			case ROTATE: {
				if (onChange) {
					swerveController.setState(StraightMode.ENABLE_PID);
				}
				double targetAngle = 0.0; // TODO: FIGURE OUT ALIGNMENT ANGLE

				swerveController.drive(0.0, 0.0, targetAngle, true);

				if (Math.abs(gyro.getYaw()-targetAngle) < 0.5) {
					nextState = ClimbState.DEPLOY;
				}
			} break;
			case DEPLOY: {
				if (onChange) {
					climber.setPistons(true);

					TimingUtil2.registerOneTimeCallback(300, () -> {
						nextState = ClimbState.RAISE;
					});
				}
			} break;
			case RAISE: {
				climber.setSpeed(1.0); // TODO: make sure this is right direction

				if (climber.getEncoderCount() >= 3000.0) { // TODO: fix value
					climber.setSpeed(0.0);
					nextState = ClimbState.GEAR_TORQUE;
				}
			} break;
			case GEAR_TORQUE: {
				if (onChange) {
					climber.shiftGear(GearPosition.TORQUE);

					nextState = ClimbState.DRIVE;
				}

				climber.setSpeed(0.0);
			} break;
			case DRIVE: {
				if (onChange) {
					TimingUtil2.registerOneTimeCallback(300, () -> {
						nextState = ClimbState.PULL_UP;
					});

					swerveController.setSwerveMode(SwerveMode.ROBOT_CENTRIC);
				}
				
				swerveController.drive(0.4, 0.0, 0.0, true);
			} break;
			case PULL_UP: {
				swerveController.drive(0.0, 0.0, 0.0, false);

				climber.setSpeed(-1.0);

				if (climber.getEncoderCount() <= 100) { // TODO: figure out value
					nextState = ClimbState.FIN;
				}
			} break;
			case FIN: {
				swerveController.drive(0.0, 0.0, 0.0, false);
				climber.setSpeed(0.0);
			} break;
		}
	}
}