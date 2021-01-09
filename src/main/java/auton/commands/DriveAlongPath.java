package auton.commands;

import java.io.File;
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

public class DriveAlongPath extends SequentialCommand {

    Waypoint[] points = new Waypoint[] {
        new Waypoint(0.001, 0.001, Pathfinder.d2r(90)),                           // Waypoint @ x=0, y=0,   exit angle=0 radians
        new Waypoint(0.001, 4, Pathfinder.d2r(90)),      // Waypoint @ x=-4, y=-1, exit angle=-45 degrees
        // new Waypoint(-.25, -.25, 0),                        // Waypoint @ x=-2, y=-2, exit angle=0 radians
        // new Waypoint(0, 0.001, Pathfinder.d2r(90))                           // Waypoint @ x=0, y=0,   exit angle=0 radians
    };
    private HashMap<Pivot, Vector2> pivotMap;
    private CANEncoder flEncoder;
    private CANEncoder frEncoder;
    private CANEncoder blEncoder;
    private CANEncoder brEncoder;
    final private double WHEEL_DIAMETER = 0.1;
    final private int ENCODER_TICKS = 1024;
    final private double MAX_VELOCITY = 0.65;
    EncoderFollower flFollower;
    EncoderFollower frFollower;
    EncoderFollower blFollower;
    EncoderFollower brFollower;

    public DriveAlongPath () {

        pivotMap = Devices.getPivotMap();

    }

    @Override
    public void init() {

        Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_HIGH, 0.02, MAX_VELOCITY, 0.25, 1);
        Trajectory trajectory = Pathfinder.generate(points, config);
        // File myFile = new File("/home/lvuser/auton/DriveStraights.csv");
        // Trajectory trajectory = Pathfinder.readFromCSV(myFile);
        SwerveModifier modifier = new SwerveModifier(trajectory).modify(0.66, 0.53, SwerveModifier.Mode.SWERVE_DEFAULT);

        flFollower = new EncoderFollower(modifier.getFrontLeftTrajectory());   // Front Left wheel
        frFollower = new EncoderFollower(modifier.getFrontRightTrajectory());   // Front Right wheel
        blFollower = new EncoderFollower(modifier.getBackLeftTrajectory());   // Back Left wheel
        brFollower = new EncoderFollower(modifier.getBackRightTrajectory());   // Back Right wheel

        for (Pivot pivot : pivotMap.keySet()) {
			if(pivot.getName().equals("FL")) {
                flEncoder = pivot.getEncoder();
            }
            else if(pivot.getName().equals("FR")) {
                frEncoder = pivot.getEncoder();
            }
            else if(pivot.getName().equals("BL")) {
                blEncoder = pivot.getEncoder();
            }
            else if(pivot.getName().equals("BR")) {
                brEncoder = pivot.getEncoder();
			}
		}

        flEncoder.setPosition(0);
        frEncoder.setPosition(0);
        blEncoder.setPosition(0);
        brEncoder.setPosition(0);
        flFollower.configureEncoder(Math.abs((int)flEncoder.getPosition()), ENCODER_TICKS, WHEEL_DIAMETER);
        flFollower.configurePIDVA(0.8, 0.0, 0.001, 1 / MAX_VELOCITY, 0);
        frFollower.configureEncoder(Math.abs((int)frEncoder.getPosition()), ENCODER_TICKS, WHEEL_DIAMETER);
        frFollower.configurePIDVA(0.8, 0.0, 0.001, 1 / MAX_VELOCITY, 0);
        blFollower.configureEncoder(Math.abs((int)blEncoder.getPosition()), ENCODER_TICKS, WHEEL_DIAMETER);
        blFollower.configurePIDVA(0.8, 0.0, 0.001, 1 / MAX_VELOCITY, 0);
        brFollower.configureEncoder(Math.abs((int)brEncoder.getPosition()), ENCODER_TICKS, WHEEL_DIAMETER);
        brFollower.configurePIDVA(0.8, 0.0, 0.001, 1 / MAX_VELOCITY, 0);

        // flFollower.configureEncoder((int)flEncoder.getPosition(), ENCODER_TICKS, WHEEL_DIAMETER);
        // flFollower.configurePIDVA(0.05, 0.0, 0.001, 1 / MAX_VELOCITY, 0);
        // frFollower.configureEncoder((int)frEncoder.getPosition(), ENCODER_TICKS, WHEEL_DIAMETER);
        // frFollower.configurePIDVA(0.05, 0.0, 0.001, 1 / MAX_VELOCITY, 0);
        // blFollower.configureEncoder((int)blEncoder.getPosition(), ENCODER_TICKS, WHEEL_DIAMETER);
        // blFollower.configurePIDVA(0.05, 0.0, 0.001, 1 / MAX_VELOCITY, 0);
        // brFollower.configureEncoder((int)brEncoder.getPosition(), ENCODER_TICKS, WHEEL_DIAMETER);
        // brFollower.configurePIDVA(0.05, 0.0, 0.001, 1 / MAX_VELOCITY, 0);

    }

    @Override
    public void run() {
       
        double flOutput = flFollower.calculate(Math.abs((int)(flEncoder.getPosition())));
        double flDesiredHeading = Pathfinder.boundHalfDegrees(Pathfinder.r2d(flFollower.getHeading()));    // Bound to -180..180 degrees
        double frOutput = frFollower.calculate(Math.abs((int)(frEncoder.getPosition())));
        double frDesiredHeading = Pathfinder.boundHalfDegrees(Pathfinder.r2d(frFollower.getHeading()));    // Bound to -180..180 degrees
        double blOutput = blFollower.calculate(Math.abs((int)(blEncoder.getPosition())));
        double blDesiredHeading = Pathfinder.boundHalfDegrees(Pathfinder.r2d(blFollower.getHeading()));    // Bound to -180..180 degrees
        double brOutput = brFollower.calculate(Math.abs((int)(brEncoder.getPosition())));
        double brDesiredHeading = Pathfinder.boundHalfDegrees(Pathfinder.r2d(brFollower.getHeading()));    // Bound to -180..180 degrees

        // double flOutput = flFollower.calculate((int)(flEncoder.getPosition())); //try removing the * ENCODER_TICKS and maybe change PPR
        // double flDesiredHeading = Pathfinder.boundHalfDegrees(Pathfinder.r2d(flFollower.getHeading()));    // Bound to -180..180 degrees
        // double frOutput = frFollower.calculate((int)(frEncoder.getPosition()));
        // double frDesiredHeading = Pathfinder.boundHalfDegrees(Pathfinder.r2d(frFollower.getHeading()));    // Bound to -180..180 degrees
        // double blOutput = blFollower.calculate((int)(blEncoder.getPosition()));
        // double blDesiredHeading = Pathfinder.boundHalfDegrees(Pathfinder.r2d(blFollower.getHeading()));    // Bound to -180..180 degrees
        // double brOutput = brFollower.calculate((int)(brEncoder.getPosition()));
        // double brDesiredHeading = Pathfinder.boundHalfDegrees(Pathfinder.r2d(brFollower.getHeading()));    // Bound to -180..180 degrees


        for (Pivot pivot : pivotMap.keySet()) {
			if(pivot.getName().equals("FL")) {
				pivot.setTargetAngleD(flDesiredHeading);
                pivot.setSpeed(flOutput * MAX_VELOCITY);
                // System.out.println("FL: " + flEncoder.getVelocity()/3.281);
                // if (flOutput * MAX_VELOCITY != 0) {
                //     System.out.println(flOutput * MAX_VELOCITY);
                // }
                
            }
            else if(pivot.getName().equals("FR")) {
				pivot.setTargetAngleD(frDesiredHeading);
                pivot.setSpeed(frOutput * MAX_VELOCITY); //maybe get rid of this * MAX_VELOCITY  part
                // System.out.println("FR: " + frEncoder.getVelocity()/3.281);
            }
            else if(pivot.getName().equals("BL")) {
                pivot.setTargetAngleD(blDesiredHeading);
                // System.out.println("BL: " + blEncoder.getVelocity()/3.281);
                pivot.setSpeed(blOutput * MAX_VELOCITY);
            }
            else if(pivot.getName().equals("BR")) {
                pivot.setTargetAngleD(brDesiredHeading);
                // System.out.println("BR: " + brEncoder.getVelocity()/3.281);
                pivot.setSpeed(brOutput * MAX_VELOCITY);
			}
        }

    }

    @Override
    public boolean isDone() {
        // if (flFollower.isFinished() && frFollower.isFinished() && blFollower.isFinished() && brFollower.isFinished()) {
        //     return true;
        // }
        return false;
    }
    
}