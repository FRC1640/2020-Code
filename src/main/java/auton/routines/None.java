package auton.routines;

import auton.commands.SequentialCommand;
import auton.commands.Wait;

public class None extends SequentialCommand {
	public None() {
		super(new Wait(1000));
	}
}