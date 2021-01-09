package systems.drive.controllers;

import com.revrobotics.CANSparkMax.IdleMode;

import edu.wpi.first.wpilibj.controller.PIDController;
import robot.Controller;
import robot.Devices;
import robot.Limelight;
import robot.Controller.Axis;
import robot.Limelight.LedEnum;
import systems.drive.controllers.SwerveController.StraightMode;
import utilities.IController;
import utilities.LogUtil;
import utilities.MathUtil;

public class GoalAlignDriveController implements IController {

    private SwerveController swerveController;
    private Controller driveController;
    private Limelight limelight;
    private PIDController limelightController;

    public GoalAlignDriveController (SwerveController swerveController) {
        this.swerveController = swerveController;
        driveController = Devices.getDriverController();
        limelight = Devices.getLimelight();
        limelightController = new PIDController(0.045, 0.0, 0.0055);
        // limelightController = new PIDController(0.04, 0.0, 0.0);
    }

    @Override
    public void activate() {
        LogUtil.log(getClass(), "Activating");
        limelightController.reset();
        limelight.setLEDOn(LedEnum.FORCE_ON);
        limelight.setProcessing(true);
        swerveController.setRampIdle(IdleMode.kBrake, 0.4);
    }

    @Override
    public void deactivate() {
        LogUtil.log(getClass(), "Deactivating");
        limelight.setLEDOn(LedEnum.FORCE_OFF);
        swerveController.setState(StraightMode.RESET_PID);
        swerveController.setRampIdle(IdleMode.kCoast, 0.0);
    }

    @Override
    public void update() {
		if (limelight.getProcessing()) {
			limelight.setLEDOn(LedEnum.FORCE_ON);
			double x1 = driveController.getAxis(Axis.LX);
			double y1 = driveController.getAxis(Axis.LY);
            double x2 = limelightController.calculate(limelight.getTargetX(0.5), 0);
			swerveController.drive(x1, y1, -x2, false);
		}
    }

}