package auton.routines;

import auton.commands.SequentialCommand;
import auton.commands.TurnGyro;
import auton.commands.Wait;

public class Turn extends SequentialCommand {
	public Turn() {
		super(
			new TurnGyro(0.0),
			new Wait(10),
			new TurnGyro(45.0),
			new Wait(10),
			new TurnGyro(90.0),
			new Wait(10),
			new TurnGyro(135.0),
			new Wait(10),
			new TurnGyro(180.0),
			new Wait(10),
			new TurnGyro(225.0),
			new Wait(10),
			new TurnGyro(270.0),
			new Wait(10),
			new TurnGyro(315.0));
	}
}