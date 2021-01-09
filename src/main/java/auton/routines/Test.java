package auton.routines;

import auton.commands.*;

public class Test extends SequentialCommand {

    public Test () {
        super(new RotateHood(0.6));
    }

}