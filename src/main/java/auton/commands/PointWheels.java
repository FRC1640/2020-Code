package auton.commands;

import robot.Devices;
import systems.drive.controllers.SwerveController;

public class PointWheels extends Command {

    private double angle;

    private SwerveController swerveController;

    public PointWheels (double angle) {
        this.angle = angle;
        this.swerveController = Devices.getSwerveController();
    }

    @Override
    public void run() {
        swerveController.pointWheels(angle);

    }

    @Override
    public boolean isDone() {
        return currentTime() >= startTime + 500;
    }

}