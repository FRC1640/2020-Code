package auton.routines;

import auton.commands.*;

public class ShootDriveBack extends SequentialCommand {
	public ShootDriveBack() {
		super(
		new AutoAlign(),
		new Shoot(4000),
		new PointWheels(90),
		new Drive(0.0, 0.3, 0.0, 2.0, false)
		);
	}
}