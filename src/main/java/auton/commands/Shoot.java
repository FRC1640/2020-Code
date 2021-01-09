package auton.commands;

import auton.AutonBridge;
import robot.Devices;
import robot.Limelight;
import systems.scoring.Indexer;
import systems.scoring.Intake;
import systems.scoring.Shooter;
import systems.scoring.ScoringSystem.ScoringState;
import utilities.MathUtil;

public class Shoot extends Command {

	private double time;

	private Shooter shooter;
	private Indexer indexer;
	private Intake intake;
	private Limelight limelight;

	private ScoringState state;

	private double rpm = 0.0;

    public Shoot (double rpm) {

		time = 0.0;

		this.shooter = Devices.getShooter();
		this.indexer = Devices.getIndexer();
		this.intake = Devices.getIntake();
		
		this.limelight = Devices.getLimelight();

		this.state = ScoringState.ANGLE_HOOD_AND_SPIN_SHOOTER;

		this.rpm = rpm;
    }

    @Override
    public void init() {
        super.init();
		shooter.setServoAngle(1.0);
        shooter.setShooterSpeed(0.0);
		indexer.setSpeed(0.0);
		this.state = ScoringState.ANGLE_HOOD_AND_SPIN_SHOOTER;
		AutonBridge.setShootDone(false);
    }

    @Override
    public void run() {
		switch (state) {
			case ANGLE_HOOD_AND_SPIN_SHOOTER: {
				shooter.setShooterSpeed(0.8);
				shooter.setServoAngle(MathUtil.calculateHoodAngle(limelight.getTargetY()));
				double absShooterSpeed = shooter.getShooterSpeed();
				System.out.println(absShooterSpeed);
				if (Math.abs(-rpm - absShooterSpeed) < 250) {
					state = ScoringState.SHOOT;
				}
			} break;
			case SHOOT: {
				shooter.setShooterSpeed(0.8);

				shooter.setServoAngle(MathUtil.calculateHoodAngle(limelight.getTargetY()));
				indexer.setSpeed(1.0);
				intake.setBar(1.0);
				intake.setFunnel(1.0);

				if (time == 0.0) {
					time = currentTime();
				}

				if (currentTime() > time + 1500) {
					indexer.setSpeed(0.0);
					shooter.setShooterSpeed(0.0);
					shooter.setServoAngle(1.0);
					this.state = ScoringState.IDLE;
				}
			} break;
		}
    }

    @Override
    public boolean isDone() {
        return this.state == ScoringState.IDLE;
    }

    

}