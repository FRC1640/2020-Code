package auton.routines;

import auton.commands.*;

public class TrenchStraight extends SequentialCommand {
	public TrenchStraight() {
		super(
		new ParallelCommand(
			new SpeedShooter(0.8, 2),
			new AutoAlign(),
			new RotateHood(0.55)
		),
		new Shoot(4000),
		new TurnGyro(0.0),
		new PointWheels(90),
		new ParallelCommand(
			new DriveEncoder(0.0, 0.5, 0.0, 140),
			new DeployIntake(),
			new AutomateIndexer(0, 3, 5),
			new SpeedShooter(1.0, 5)
		),
		new	ParallelCommand(
			new AutoAlign(),
			new SpeedShooter(1.0, 2),
			new RotateHood(0.5)
		),
		new ShootFar()
		// new TurnGyro(90),
		// new PointWheels(90),
		// new ParallelCommand(
		// 	new Drive(0.0, 0.4, 0.0, 2.0, true),
		// 	new DeployIntake(),
		// 	new RunIntake(1.0, 2.0)
		// ),
		// new Drive(0.0, 0.0, 0.0, 300, true),
		// new TurnGyro(20),
		// new Shoot()
		);
	}
}