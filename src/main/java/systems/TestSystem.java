package systems;

import java.util.HashMap;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import robot.Devices;
import robot.Gyro;
import robot.Controller.Button;
import robot.Controller.ButtonEvent;
import systems.climber.Climber;
import systems.drive.pivot.CVTPivot;
import systems.drive.pivot.Pivot;
import systems.scoring.Indexer;
import systems.scoring.Intake;
import systems.scoring.Shooter;
import systems.spinner.Spinner;
import utilities.ServoSendable;
import utilities.Vector2;

public class TestSystem extends RobotSystem {

	private Gyro gyro;

	private Shooter shooter;
	private Intake intake;
	private Indexer indexer;
	private Spinner spinner;
	private Climber climber;
	private HashMap<Pivot, Vector2> pivotMap;

	private NetworkTableEntry intakeBarSolenoid;

	private boolean solenoidState = false;

	public TestSystem() {
		super("Test System");

		gyro = Devices.getGyro();
		
		pivotMap = Devices.getPivotMap();
		spinner = Devices.getSpinner();
		shooter = Devices.getShooter();
		intake = Devices.getIntake();
		indexer = Devices.getIndexer();

		intakeBarSolenoid = NetworkTableInstance.getDefault().getTable("Shuffleboard").getEntry("intakeBarSolenoid");

		intakeBarSolenoid.setBoolean(false);
		// climber = Devices.getClimber();
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		ShuffleboardTab testTab = Shuffleboard.getTab("Parts");

		// Climber

		// testTab.add("Climber Speed", climber.getMotor());
		// testTab.add("Climber Piston", climber.getGearPiston());
		// testTab.add("Gyro", gyro);		

		// Scoring System
		// testTab.add("Intake Piston", intake.getSolenoid());
		testTab.add("Intake Speed", intake.getBarMotor());
		testTab.add("Funnel Speed", intake.getFunnelMotor());

		

		testTab.add("Indexer Speed", indexer.getMotor());
		testTab.add("Proximity 1", indexer.getProximity(1));
		testTab.add("Proximity 2", indexer.getProximity(2));
		// testTab.add("Proximity 3", indexer.getProximity(3));
		// testTab.add("S0", new ServoSendable(0));
		// testTab.add("S1", new ServoSendable(1));
		// testTab.add("S2", new ServoSendable(2));
		// testTab.add("S3", new ServoSendable(3));
		// testTab.add("S4", new ServoSendable(4));

		testTab.add("Hood Angle", shooter.getHoodServo());
		testTab.add("Shooter Speed", shooter.getLeftMotor()); // whichever motor is set up as the main
		// Spinner
		testTab.add("Deploy Spinner", spinner.getPiston());
		testTab.add("Spinner Motor", spinner.getMotor());

		// Drive
		ShuffleboardTab driveTab = Shuffleboard.getTab("Drive");
		for (Pivot pivot : pivotMap.keySet()) {
			driveTab.add(((CVTPivot)pivot).getName() + " Angle", (CVTPivot) pivot);
			driveTab.add(((CVTPivot)pivot).getName() + " Speed", ((CVTPivot) pivot).getDriveMotor());
			driveTab.add(((CVTPivot)pivot).getName() + " Steer", ((CVTPivot) pivot).getSteerMotor());
		}
	}

	@Override
	public void preStateUpdate() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void postStateUpdate() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void disabledInit() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void disabledUpdate() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void autonInit() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void autonUpdate() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void teleopInit() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void teleopUpdate() throws Exception {
		// TODO Auto-generated method stub
		intake.setBarSolenoid(intakeBarSolenoid.getBoolean(false));
	}

	@Override
	public void testInit() throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public void testUpdate() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void enable() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void disable() throws Exception {
		// TODO Auto-generated method stub

	}

}