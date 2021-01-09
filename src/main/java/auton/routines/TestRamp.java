package auton.routines;

import auton.commands.*;

public class TestRamp extends SequentialCommand {
	public TestRamp() {
		super(new AutomateIndexer(0, 3, 4),
		new Print("Done", 1.0));
	}
}