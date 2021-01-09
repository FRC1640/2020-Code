package auton.routines;

import auton.commands.DriveAlongPath;
import auton.commands.PointWheels;
import auton.commands.SequentialCommand;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Waypoint;

public class TestPath extends SequentialCommand{

    // Waypoint[] points = new Waypoint[] {
    //     new Waypoint(-4, -1, Pathfinder.d2r(-45)),      // Waypoint @ x=-4, y=-1, exit angle=-45 degrees
    //     new Waypoint(-2, -2, 0),                        // Waypoint @ x=-2, y=-2, exit angle=0 radians
    //     new Waypoint(0, 0, 0)                           // Waypoint @ x=0, y=0,   exit angle=0 radians
    // };

    public TestPath () {

        super(
            new PointWheels(90),
            new DriveAlongPath()
        );

    }
    
}