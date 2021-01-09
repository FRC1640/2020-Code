package systems.climber.controllers;

import robot.Controller;
import robot.Devices;
import robot.Controller.Axis;
import robot.Controller.Button;
import systems.climber.Climber;
import systems.climber.Climber.GearPosition;
import utilities.IController;

public class ClimberManualController implements IController {
		
	private Climber climber;

	private Controller opController;

	private enum ClimberState {
		UP,
		DOWN,
		IDLE
	}

	private ClimberState nextClimberState;
	private ClimberState currentClimberState;
	private boolean onChange;


	private final double deadband = 0.2;

	public ClimberManualController(Climber c) {
		this.climber = c;

		opController = Devices.getOpController();

		nextClimberState = ClimberState.IDLE;
		currentClimberState = ClimberState.IDLE;
	}
	
	@Override
	public void activate() {
		// TODO Auto-generated method stub
		// climber.setSpeed(0.0);
	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub
		// climber.setSpeed(0.0);
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		double ly = opController.getAxis(Axis.LY);

		if (ly > deadband) {
			nextClimberState = ClimberState.UP;
		} else if (ly < -deadband) {
			nextClimberState = ClimberState.DOWN;
		} else {
			nextClimberState = ClimberState.IDLE;
		}

		onChange = (nextClimberState != currentClimberState);
		currentClimberState = nextClimberState;

		switch (currentClimberState) {
			case IDLE: {
				climber.setSpeed(0.0);
			} break;
			case UP: {
				if (onChange) {
					climber.shiftGear(GearPosition.SPEED);
				}
				climber.setSpeed(ly);
			} break;
			case DOWN: {
				if (onChange) {
					climber.shiftGear(GearPosition.TORQUE);
				}
				climber.setSpeed(ly);
			} break;
		}


		if (opController.getButton(Button.RB) && opController.getButton(Button.X)) {
			climber.setPistons(true);
		} else if (opController.getButton(Button.RB) && opController.getButton(Button.Y)) {
			climber.setPistons(false);
		}
	}

}