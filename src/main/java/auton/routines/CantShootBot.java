package auton.routines;

import auton.commands.*;

public class CantShootBot extends SequentialCommand {
    public CantShootBot() {
        super(
        new PointWheels(90),
        new Drive(0.0, -0.3, 0.0, 5, false),
        new ShootClose()
        );

    }
}