package systems.drive;

import java.util.HashMap;

import com.revrobotics.AlternateEncoderType;
import com.revrobotics.CANEncoder;
import com.revrobotics.EncoderType;
import com.revrobotics.CANSparkMax.IdleMode;

import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import robot.Controller;
import robot.Devices;
import robot.Gyro;
import robot.Controller.Button;
import robot.Controller.ButtonEvent;
import systems.drive.controllers.NormalDriveController;
import systems.RobotSystem;
import systems.drive.controllers.GoalAlignDriveController;
import systems.drive.controllers.IDriveController;
import systems.drive.controllers.SwerveController;
import systems.drive.controllers.SwerveController.SwerveMode;
import systems.drive.pivot.Pivot;
import utilities.IController;
import utilities.Vector2;

public class DriveSystem extends RobotSystem {

	public static enum DriveController { NORMAL, GOAL_ALIGN, OPERATOR; }

	private SwerveController swerveController;
	private Controller driverController;
	private HashMap<DriveController,IController> driveControllerMap;

	private IController activeDriveController;
	private DriveController currentDriveController;
	private DriveController nextDriveController;

    private HashMap<Pivot, Vector2> pivotMap;
    private CANEncoder encoder;

	private Gyro gyro;

	public DriveSystem () {
		super("Drive System");
		swerveController = Devices.getSwerveController();
        pivotMap = Devices.getPivotMap();
	}

	@Override
	public void init () {
		driverController = Devices.getDriverController();

		gyro = Devices.getGyro();

		driveControllerMap = new HashMap<>();
		driveControllerMap.put(DriveController.NORMAL, new NormalDriveController(swerveController));
		driveControllerMap.put(DriveController.GOAL_ALIGN, new GoalAlignDriveController(swerveController));
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}

		currentDriveController = null;
		driverController.registerButtonListener(ButtonEvent.PRESS, Button.Y, () -> {
			nextDriveController = DriveController.GOAL_ALIGN;
			Devices.getLimelight().setProcessing(true);
		});
		driverController.registerButtonListener(ButtonEvent.RELEASE, Button.Y, () -> {
			nextDriveController = DriveController.NORMAL; 
		});

		for (Pivot pivot : pivotMap.keySet()) {
			if(pivot.getName().equals("BR")) {
				encoder = pivot.getEncoder();
			}
		}
		
		driverController.registerButtonListener(ButtonEvent.PRESS, Button.RB, () -> {
			swerveController.setRampRate(0.1);
		});
		driverController.registerButtonListener(ButtonEvent.RELEASE, Button.RB, () -> {
			swerveController.setRampRate(0.4);
		});


	}

	@Override
	public void disable () {
		swerveController.disable();	
	}

	@Override
	public void enable () {
		swerveController.enable();
	}

	@Override public void preStateUpdate () { }

	@Override public void postStateUpdate () { }

	@Override public void disabledInit () {
		driverController.vibrate(RumbleType.kLeftRumble, 0.0);
		driverController.vibrate(RumbleType.kRightRumble, 0.0);
		if (currentDriveController != null) {
			driveControllerMap.get(currentDriveController).deactivate();
			currentDriveController = null;
		}
	}

	@Override public void disabledUpdate () { }

	@Override public void autonInit () {
		swerveController.setRampIdle(IdleMode.kBrake, 0.4);
		swerveController.setSwerveMode(SwerveMode.ROBOT_CENTRIC);
		encoder.setPosition(0.0);
		currentDriveController = null;
		nextDriveController = DriveController.NORMAL;
	}

	@Override public void autonUpdate () { }

	@Override public void teleopInit () {
		encoder.setPosition(0.0);
		swerveController.setRampIdle(IdleMode.kCoast, 0.0);
		if (currentDriveController != null) { driveControllerMap.get(currentDriveController).deactivate(); }
		swerveController.setSwerveMode(SwerveMode.FIELD_CENTRIC);
		currentDriveController = null;
		nextDriveController = DriveController.NORMAL;
	}

	@Override
	public void teleopUpdate () {
		// System.out.println(gyro.getYaw());

		SwerveMode swerveMode = swerveController.getSwerveMode();

		// System.out.println(encoder.getPosition());

		/* *********** RUMBLE *********** */

		boolean rumbleDriver = (swerveMode == SwerveMode.ROBOT_CENTRIC) && (currentDriveController != DriveController.OPERATOR);

		driverController.vibrate(RumbleType.kLeftRumble, (rumbleDriver) ? 0.1 : 0.0);
		driverController.vibrate(RumbleType.kRightRumble, (rumbleDriver) ? 0.1 : 0.0);

		/* *********** CONTROLLERS *********** */

		if (nextDriveController != currentDriveController) {
			if (currentDriveController != null) {
				activeDriveController.deactivate();
			}
			currentDriveController = nextDriveController;
			activeDriveController = driveControllerMap.get(currentDriveController);
			activeDriveController.activate();
		}
		activeDriveController.update();
	}

	@Override public void testInit () { }

	@Override public void testUpdate () { }

}
