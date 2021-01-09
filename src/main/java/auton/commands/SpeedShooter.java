package auton.commands;

import robot.Devices;
import systems.scoring.Shooter;

public class SpeedShooter extends Command {

    private double speed = 0.0;
    private double duration = 0.0;
    private Shooter shooter;

    public SpeedShooter (double speed, double duration) {

        this.shooter = Devices.getShooter();

        this.duration = duration * 1000;
        this.speed = speed;

    }

    @Override
    public void run() {
        
        shooter.setShooterSpeed(speed);

    }

    @Override
    public boolean isDone() {
        return (currentTime() >= duration + startTime);
    }



}