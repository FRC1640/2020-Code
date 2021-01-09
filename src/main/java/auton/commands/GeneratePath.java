package auton.commands;

import java.io.File;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;

public class GeneratePath extends SequentialCommand {

    Waypoint[] points = new Waypoint[] {
        new Waypoint(-2, -0.5, Pathfinder.d2r(-45)),      // Waypoint @ x=-4, y=-1, exit angle=-45 degrees
    new Waypoint(-1, -1, 0),                        // Waypoint @ x=-2, y=-2, exit angle=0 radians
    new Waypoint(0, 0, 0)                            // Waypoint @ x=0, y=0,   exit angle=0 radians
    };

    final private double MAX_VELOCITY = 0.65;

    public GeneratePath () {

    }

    @Override
    public void init () {

        Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_HIGH, 0.02, MAX_VELOCITY, 0.25, 1);
        Trajectory trajectory = Pathfinder.generate(points, config);

        File myFile = new File("/home/lvuser/auton/DriveStraights.csv");
        Pathfinder.writeToCSV(myFile, trajectory);

        System.out.println("Done");

    }
    
}