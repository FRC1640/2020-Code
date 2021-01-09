package auton.commands;

import java.util.HashMap;

import com.revrobotics.CANEncoder;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.followers.EncoderFollower;
import jaci.pathfinder.modifiers.SwerveModifier;
import robot.Devices;
import systems.drive.pivot.Pivot;
import utilities.Vector2;

public class DriveAlongPathTest extends Command {

    Waypoint[] points = new Waypoint[] {
        new Waypoint(-4, -1, Pathfinder.d2r(-45)),      // Waypoint @ x=-4, y=-1, exit angle=-45 degrees
        new Waypoint(-2, -2, 0),                        // Waypoint @ x=-2, y=-2, exit angle=0 radians
        new Waypoint(0, 0, 0)                           // Waypoint @ x=0, y=0,   exit angle=0 radians
    };

    private HashMap<Pivot, Vector2> pivotMap;
    private CANEncoder encoder;
    final private int WHEEL_DIAMETER = 4;
    Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_HIGH, 0.05, 1.7, 2.0, 60.0);
    Trajectory trajectory = Pathfinder.generate(points, config);
    SwerveModifier modifier = new SwerveModifier(trajectory).modify(0.5, 0.5, SwerveModifier.Mode.SWERVE_DEFAULT);
    EncoderFollower flFollower = new EncoderFollower(modifier.getFrontLeftTrajectory());   // Front Left wheel

    public DriveAlongPathTest () {

        pivotMap = Devices.getPivotMap();
    }

    @Override
    public void init() {

        // Trajectory trajectory = Pathfinder.generate(points, config);

        // // Wheelbase Width = 0.5m, Wheelbase Depth = 0.6m, Swerve Mode = Default
        // SwerveModifier modifier = new SwerveModifier(trajectory).modify(0.5, 0.6, SwerveModifier.Mode.SWERVE_DEFAULT);

        // // Do something with the new Trajectories...
        // Trajectory fl = modifier.getFrontLeftTrajectory();
        // Trajectory fr = modifier.getFrontRightTrajectory();
        // Trajectory bl = modifier.getBackLeftTrajectory();
        // Trajectory br = modifier.getBackRightTrajectory();

        for (Pivot pivot : pivotMap.keySet()) {
			if(pivot.getName().equals("FL")) {
                encoder = pivot.getEncoder();
			}
		}

        flFollower.configureEncoder((int)encoder.getPosition(), 2048, WHEEL_DIAMETER);
        flFollower.configurePIDVA(1.0, 0.0, 0.0, 1 / 4, 0);

    }

    @Override
    public void run() {
        
        double output = flFollower.calculate((int)encoder.getPosition());
        double desiredHeading = Pathfinder.boundHalfDegrees(Pathfinder.r2d(flFollower.getHeading()));    // Bound to -180..180 degrees

        for (Pivot pivot : pivotMap.keySet()) {
			if(pivot.getName().equals("FL")) {
				pivot.setTargetAngleR(desiredHeading);
                pivot.setSpeed(output);
			}
		}   

    }

    @Override
    public boolean isDone() {
        // TODO Auto-generated method stub
        return false;
    }
    
}