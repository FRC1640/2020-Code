package auton.routines;

import auton.commands.*;
import auton.commands.SequentialCommand;

public class TenBall extends SequentialCommand {

	public TenBall() {
		// super(new SequentialCommand(
		// 	new RunIntake(1.0),
		// 	new Wait(1000),
		// 	new RunIntake(-1.0),
		// 	new RunIntake(0.0)
			// new Drive(0.0, 0.5, 0.0, 1500),
			// new Wait(1000),
			// new Drive(0.0, 0.5, 0.4, 1000),
			// new Drive(0.0, 0.5, 0.0, 2000),
			// new ParallelCommand(
			// 	new SequentialCommand( 
			// 		new Drive(0.0, .5, 0.0, 1000),
			// 		new Drive(0.0, -0.5, 0.0, 500)
			// 	),
			// 	new Print("Hello There!", 3000)
			// ),
			// new Print("Done!", 100)
		// ));
	}

}