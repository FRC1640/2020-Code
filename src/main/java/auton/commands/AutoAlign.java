package auton.commands;

import edu.wpi.first.wpilibj.controller.PIDController;
import robot.Devices;
import robot.Limelight;
import robot.Limelight.LedEnum;
import systems.drive.controllers.SwerveController;
import systems.drive.controllers.SwerveController.StraightMode;

public class AutoAlign extends Command {

    private Limelight limelight;
    private SwerveController swerveController;
    private PIDController limelightController;

    private int count = 0;

    public AutoAlign () {

        swerveController = Devices.getSwerveController();
        limelight = Devices.getLimelight();
        limelightController = new PIDController(0.045, 0.0, 0.0055);

    }

    @Override
    public void init () {
        super.init();
        limelightController.reset();
        swerveController.setState(StraightMode.DISABLE_PID);
        limelight.setLEDOn(LedEnum.FORCE_ON);
        limelight.setProcessing(true);
    }

    @Override
    public void run() {
        limelight.setLEDOn(LedEnum.FORCE_ON);
		limelight.setProcessing(true);
		
        if (!limelight.getProcessing()) {
			limelight.setLEDOn(LedEnum.FORCE_ON);
            limelight.setProcessing(true);
        } else {
            limelight.setLEDOn(LedEnum.FORCE_ON);
            double x2 = limelightController.calculate(limelight.getTargetX(0.5), 0);
			swerveController.drive(0.0, 0.0, -x2, false);
			// System.out.println(limelight.getTargetX(1.0));
            if (limelight.getTargetX(1.0) == 0.0) {
                count++;
            } else {
				count = 0;
			}
        }
    }

    @Override
    public boolean isDone() {
        if (limelight.getProcessing() && count >= 50) {
            // System.out.println("Done");
            swerveController.drive(0.0, 0.0, 0.0, true);
            limelight.setLEDOn(LedEnum.FORCE_OFF);
            return true;
        }
        return false;
    }



}