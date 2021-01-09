package auton.commands;

import edu.wpi.first.wpilibj.controller.PIDController;
import robot.Devices;
import robot.Gyro;
import systems.drive.controllers.SwerveController;
import systems.drive.controllers.SwerveController.StraightMode;
import utilities.LogUtil;

public class TurnGyro extends Command {

    private double angle;

    private SwerveController swerveController;
    private Gyro gyro;

    private PIDController gyroPIDController;

    public TurnGyro (double angle) {
        this.swerveController = Devices.getSwerveController();
        this.gyro = Devices.getGyro();

        this.angle = angle;

		this.gyroPIDController = new PIDController(0.78, 0.0001, 0.0075);
    }

    @Override
    public void init () {
        angle = (gyro.getYaw() + angle + 720) % 360;
    }

    @Override
    public void run() {
        swerveController.setState(StraightMode.DISABLE_PID);
        double dAngle = angle - gyro.getYaw();
        double x2 = gyroPIDController.calculate(Math.sin(dAngle * Math.PI / 180.0), 0);
        LogUtil.log(getClass(), String.format("%f, %f,\n", gyro.getYaw(), x2));
        swerveController.drive(0.0, 0.0, x2, true, true);

    }

    @Override
    public boolean isDone() {
        if (Math.abs(gyro.getYaw()-angle) < 1.5) {
            swerveController.drive(0.0, 0.0, 0.0, true, false);
            return true;
        }   
        return false;
    }



}