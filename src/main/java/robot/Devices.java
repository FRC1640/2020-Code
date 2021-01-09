package robot;

import java.util.HashMap;

import edu.wpi.first.wpilibj.Compressor;
import systems.climber.Climber;
import systems.drive.controllers.SwerveController;
import systems.drive.pivot.CVTPivot;
import systems.drive.pivot.Pivot;
import systems.drive.pivot.PivotConfig;
import systems.scoring.Indexer;
import systems.scoring.Intake;
import systems.scoring.Shooter;
import systems.spinner.Spinner;
import utilities.Vector2;

public class Devices {

	public static Devices instance = null;

	private Gyro gyro;
	private HashMap<Pivot,Vector2> pivotMap;
	private Controller driverController;
	private Controller opController;
	private Compressor compressor;
	private SwerveController swerveController;
	private Shooter shooter;
	private Intake intake;
	private Indexer indexer;
	private Limelight limeLight;
	private Spinner spinner;
	private Climber climber;

	private Devices () {
		gyro = new Gyro();

		PivotConfig.loadConfigs("/home/lvuser/deploy/config/pivotcfg.json");
		pivotMap = new HashMap<>();

		driverController = new Controller(0);
		opController = new Controller(1);

		climber = new Climber(13, 4, 2, 3);

		pivotMap.put(new CVTPivot("1"), new Vector2()); // FL
		pivotMap.put(new CVTPivot("2"), new Vector2()); // FR
		pivotMap.put(new CVTPivot("3"), new Vector2()); // BL
		pivotMap.put(new CVTPivot("4"), new Vector2()); // BR

		swerveController = new SwerveController(pivotMap);
		limeLight = new Limelight();

		compressor = new Compressor();
		compressor.setClosedLoopControl(true);

		spinner = new Spinner(7, 0); //Add after ports are determined
		
		shooter = new Shooter(14, 3, 4); 
		intake = new Intake(10, 9, 1);
		indexer = new Indexer(5, 9, 8); // 9, 8
	}

	public static void init () {
		if (instance == null) {
			instance = new Devices();
		}
	}

	public static HashMap<Pivot,Vector2> getPivotMap () {
		return instance.pivotMap;
	}

	public static SwerveController getSwerveController () {
		return instance.swerveController;
	}

	public static Shooter getShooter () {
		return instance.shooter;
	}

	public static Intake getIntake () {
		return instance.intake;
	}

	public static Indexer getIndexer () {
		return instance.indexer;
	}
	
	public static Spinner getSpinner () {
		return instance.spinner;
	}

	public static Climber getClimber () {
		return instance.climber;
	}

	public static Controller getDriverController () {
		return instance.driverController;
	}

	public static Controller getOpController () {
		return instance.opController;
	}

	public static Gyro getGyro () {
		return instance.gyro;
	}

	public static Limelight getLimelight () {
		return instance.limeLight;
	}

	public static Compressor getCompressor () {
		return instance.compressor;
	}

}