package systems.drive.controllers;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import robot.Controller;
import robot.Devices;
import robot.Gyro;
import robot.Controller.Axis;
import robot.Controller.Button;
import robot.Controller.ButtonEvent;
import systems.drive.controllers.SwerveController.StraightMode;
import utilities.IController;
import utilities.LogUtil;

public class NormalDriveController implements IController {

	public static enum RotationState {
		ROTATING,
		STOP_ROTATING,
		NOT_ROTATING,
		RESET;
	}

	private SwerveController swerveController;
	private Controller driverController;
	private List<Integer> driverCallbackIds;
	private RotationState curRotationState;
	private RotationState nextRotationState;
	private double targetHeading;
	private Gyro gyro;
	double prevServoAngle;
	boolean servoSlow = false;

	public NormalDriveController (SwerveController swerveController) {
		driverController = Devices.getDriverController();
		this.swerveController = swerveController;
		driverCallbackIds = new ArrayList<>();
		gyro = Devices.getGyro();
		curRotationState = null;
		nextRotationState = RotationState.NOT_ROTATING;
	}

	@Override
	public void activate() {
		LogUtil.log(getClass(), "Activating");

		driverController.registerButtonListener(ButtonEvent.PRESS, Button.B, () -> {
			setServoSlow(true);
		});
		driverController.registerButtonListener(ButtonEvent.PRESS, Button.X, () -> {
			setServoSlow(false);
		});
		driverController.registerButtonListener(ButtonEvent.PRESS, Button.SELECT, () -> {
			swerveController.toggleFieldCentric();
		});
	}

	public void setServoSlow(boolean servoSlow) {
		this.servoSlow = servoSlow;
	}

	public boolean getServoSlow() {
		return servoSlow;
	}

	@Override
	public void deactivate() {
		LogUtil.log(getClass(), "Deactivating");
		for (int id : driverCallbackIds) { driverController.unregisterButtonListener(id); }
	}

	public double scalar(double x) {
		return (0.5 * (Math.pow(x, 3)) + (0.5 * x));
		//.5x^3 + .5x
	}

	@Override
	public void update() {

			double x1 = scalar(driverController.getAxis(Axis.LX));
			double y1 = scalar(driverController.getAxis(Axis.LY));
			double x2Normal = scalar(driverController.getAxis(Axis.RX)) * 0.8;
			curRotationState = nextRotationState;

			switch (curRotationState) {
				case NOT_ROTATING: {
					swerveController.setState(StraightMode.DISABLE_PID);
					swerveController.drive(x1, y1, x2Normal, getServoSlow());
				} break;
				case ROTATING: {
					swerveController.setState(StraightMode.DISABLE_PID);
					swerveController.drive(x1, y1, x2Normal, getServoSlow());
					targetHeading = gyro.getYaw();
					if (driverController.getAxis(Axis.RX) < 0.15) {
						nextRotationState = RotationState.STOP_ROTATING;
					}
				} break;
				case STOP_ROTATING: {
					swerveController.drive(x1, y1, 0.0, getServoSlow());
					if (Math.abs(gyro.getRate()) < 0.1) {
						targetHeading = gyro.getYaw();
						nextRotationState = RotationState.NOT_ROTATING;
					}
				} break;
				case RESET: {
					swerveController.setState(StraightMode.RESET_PID);
					swerveController.drive(0.0, 0.0, 0.0, getServoSlow());
					Devices.getGyro().resetGyro(); 
					targetHeading = 0;
					swerveController.resetPID();
					nextRotationState = RotationState.NOT_ROTATING;
				} break;
			}

			if (driverController.getButton(Button.START)) {
				nextRotationState = RotationState.RESET;
			}
			else if (Math.abs(x2Normal) > 0.15) {
				nextRotationState = RotationState.ROTATING;
			} 

	}
}