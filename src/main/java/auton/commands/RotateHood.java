package auton.commands;

import auton.AutonBridge;
import robot.Devices;
import systems.scoring.Shooter;

public class RotateHood extends Command {
    
    private Shooter shooter;
    private double angle;

    public RotateHood(double angle) {
        this.shooter = Devices.getShooter();

        this.angle = angle;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        shooter.setServoAngle(angle);
    }

    @Override
    public boolean isDone() {
        return (currentTime() >= startTime + 1000);
    }
}