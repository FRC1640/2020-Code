/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code	  */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package robot;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import auton.commands.*;
import auton.routines.*;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import robot.Limelight.LedEnum;
import systems.TestSystem;
import systems.climber.ClimberSystem;
import systems.drive.DriveSystem;
import systems.scoring.ScoringSystem;
import systems.spinner.SpinnerSystem;
import utilities.LogUtil;
import utilities.MathUtil;
import utilities.TimingUtil2;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

	public static Robot self = null;

	public static enum RobotState { DISABLED, TELEOP, AUTONOMOUS, TEST };

	Limelight limelight;
	private SequentialCommand autonScheduler;

	private NetworkTableEntry delayEntry;
	private SendableChooser<Command> sChooser;
	private boolean autonReady;
	private AddressableLED led;
	private AddressableLEDBuffer ledBuffer;

	public static RobotState getState () {
		if (self == null || self.isDisabled()) { return RobotState.DISABLED; }
		else if (self.isOperatorControl()) { return RobotState.TELEOP; }
		else if (self.isAutonomous()) { return RobotState.AUTONOMOUS; }
		else if (self.isTest()) { return RobotState.TEST; }
		return RobotState.DISABLED;
	}

	/**
	 * This function is run when the robot is first started up and should be used
	 * for any initialization code.
	 */
	@Override
	public void robotInit () {
		self = this;

		TimingUtil2.registerOneTimeCallback(500, () -> { LogUtil.log("TimingUtil2Verification", "TimingUtil2 one time callback worked!"); });
		LogUtil.log("INIT", "Startin g LogUtil");
		Devices.init();
		Devices.getGyro().resetGyro();
		// led = new AddressableLED(5);
		// ledBuffer = new AddressableLEDBuffer(60); //Change this value
		// led.setLength(ledBuffer.getLength());
		// led.setData(ledBuffer);
    	// led.start();
		// limelight = Devices.getLimelight();
		
		new ScoringSystem();
		new DriveSystem();
		// new ClimberSystem();
		// new SpinnerSystem();
		// new TestSystem();

		// Auton Stuff
		delayEntry = NetworkTableInstance.getDefault().getTable("Shuffleboard").getEntry("autonDelay");
		delayEntry.setDouble(0.0);

		sChooser = new SendableChooser<Command>();
		// List<Class> cList = ClasspathInspector.getMatchingClasses(IRoutine.class);
		sChooser.addOption("Chaos", new Chaos());
		sChooser.addOption("PathTest", new DriveAlongPath());
		sChooser.addOption("Trench Straight", new TrenchStraight());
		sChooser.addOption("Shoot Drive Back", new ShootDriveBack());
		sChooser.addOption("Trash", new CantShootBot());
		sChooser.addOption("MOE", new GoMOE());
		sChooser.setDefaultOption("Test Ramp", new TestRamp());
		sChooser.setDefaultOption("None", new None());
		sChooser.addOption("Turn", new Turn());
		sChooser.addOption("Generate Path", new GeneratePath());
		sChooser.addOption("Trench Path", new TrenchPath());

		// for (Class<Command> c : cList) {
		// 	try {
		// 		sChooser.addOption(c.getName(), c.getDeclaredConstructor().newInstance());
				
		// 	} catch (Exception exception) {
		// 		exception.printStackTrace();
		// 	}
		// }

		Shuffleboard.getTab("Auton").add(sChooser);
	}

	@Override public void disabledInit () { LogUtil.log("ROBOT_STATE", "DISABLED");  }

	@Override public void autonomousInit () {
		LogUtil.log("ROBOT_STATE", "AUTON ENABLED");
		
		autonScheduler = (SequentialCommand) sChooser.getSelected();

		autonScheduler.init();

		TimingUtil2.registerOneTimeCallback((long) delayEntry.getDouble(0.0)*1000, () -> {
			autonReady = true;
		});
	}

	@Override public void teleopInit () { LogUtil.log("ROBOT_STATE", "TELEOP ENABLED");  }

	@Override public void testInit () { LogUtil.log("ROBOT_STATE", "TEST ENABLED"); }
	
	@Override public void robotPeriodic() {

	 }

	@Override public void disabledPeriodic () { }
	
	@Override public void autonomousPeriodic () { 
		if (autonReady) {
			if (!autonScheduler.isDone()) {
				autonScheduler.run();
			}
		}
	}
  
	@Override public void teleopPeriodic () {

		// for (int i = 0; i < ledBuffer.getLength(); i++) {
		// 	ledBuffer.setRGB(i, 0, 0, 255);
		// 	ledBuffer.setRGB(i + 2, 255, 255, 0);
		// } 

	}
  
	@Override public void testPeriodic () { }

    // private static Class[] getClasses(String packageName)
    //         throws ClassNotFoundException, IOException {
    //     ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    //     assert classLoader != null;
    //     String path = packageName.replace('.', '/');
    //     Enumeration resources = classLoader.getResources(path);
    //     List dirs = new ArrayList();
    //     while (resources.hasMoreElements()) {
    //         URL resource = resources.nextElement();
    //         dirs.add(new File(resource.getFile()));
    //     }
    //     ArrayList classes = new ArrayList();
    //     for (File directory : dirs) {
    //         classes.addAll(findClasses(directory, packageName));
    //     }
    //     return classes.toArray(new Class[classes.size()]);
    // }
}
