package auton.routines;

import auton.commands.*;

public class Chaos extends SequentialCommand {

	public Chaos() {
		super(
        new PointWheels(90),
        new ParallelCommand(
            new DeployIntake(),
            new RunIntake(1.0, 2.25),
	          new Drive(0.0, 0.6, 0.0, 2.25, true)
        ),
        new ParallelCommand(
          new RunIntake(1.0, .3),
          new Drive(0.0, -1.0, 0.0, .5, false)
        ),
        new ParallelCommand(
          new RetractIntake(),
          new Drive(-0.7, -0.3, 0.0, 2.25, false)
        ),
        new AutoAlign(),
        new Print("AUTO ALIGN DONE", 0.05),
        new Shoot(4000)
		);
    }
    
}