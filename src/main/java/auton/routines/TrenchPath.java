package auton.routines;

import auton.commands.*;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Waypoint;

public class TrenchPath extends SequentialCommand {

    public TrenchPath() {

        super(
            new PointWheels(90),
            new SequentialCommand(
                new DeployIntake(),
                new RunIntake(1.0, 5),
                new DriveAlongPath()
            ),
            new SequentialCommand(
                new AutoAlign(),
                new SpeedShooter(0.8, 3)
            ),
            new Shoot(4000)
        );

    }
    
}
