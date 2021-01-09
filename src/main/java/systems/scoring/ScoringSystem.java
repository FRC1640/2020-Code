package systems.scoring;

import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax.IdleMode;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Timer;
import robot.Controller;
import robot.Devices;
import robot.Limelight;
import robot.Controller.Button;
import robot.Controller.ButtonEvent;
import robot.Limelight.LedEnum;
import systems.RobotSystem;
import utilities.LogUtil;
import utilities.MathUtil;

public class ScoringSystem extends RobotSystem {

	public static enum ScoringState {
		IDLE, SPINNING, FULL, SHOOT, ANGLE_HOOD_AND_SPIN_SHOOTER, LIMELIGHT_OFF, REVERSE, MANUAL
	}

	private ScoringState scoringCurrentState;
	private ScoringState scoringNextState;

	private Controller driverController;
	private Controller opController;
	private Limelight limelight;
	private Shooter shooter;
	private Indexer indexer;
	private Intake intake;

	private int count = 0;
	private int maxCount = 4000;

	private int ballCount;

	private NetworkTableEntry ballCountEntry;
	private NetworkTableEntry motorSpeedEntry;

	private double funnelSpeed = 0.0;
	private double shooterSpeed = 0.0;
	private double targetShooterRpm = 0.0;
	private double indexerSpeed = 0.0;
	private double intakeSpeed = 0.0;
	private double servoAngle = 0.0;

	private boolean intakeSolenoidState = false;
  
	private int timeCurrent = 0;
	private int timeSensor = 0;
	private int notSensor = 0;
  
	private boolean useCalculatedHoodAngle;
	private boolean isFastIndexer;

	public ScoringSystem() {
		super("Scoring System");
	}

	@Override
	public void init() {
		driverController = Devices.getDriverController();
		opController = Devices.getOpController();
		limelight = Devices.getLimelight();

		indexer = Devices.getIndexer();
		intake = Devices.getIntake();
		shooter = Devices.getShooter();

		scoringCurrentState = ScoringState.IDLE;
		scoringNextState = ScoringState.IDLE;

		useCalculatedHoodAngle = false;

		// Shoot
		driverController.registerButtonListener(ButtonEvent.PRESS, Button.LB, () -> {
			targetShooterRpm = -4000;
			shooterSpeed = 0.8;
			useCalculatedHoodAngle = true;
			isFastIndexer = false;
			scoringNextState = ScoringState.ANGLE_HOOD_AND_SPIN_SHOOTER;
		});
		driverController.registerButtonListener(ButtonEvent.RELEASE, Button.LB, () -> {
			scoringNextState = ScoringState.LIMELIGHT_OFF;
			useCalculatedHoodAngle = false;
		});

		// Fountain Close
		driverController.registerButtonListener(ButtonEvent.PRESS, Button.S, () -> {
			servoAngle = 0.75;
			shooterSpeed = 0.25;
			scoringNextState = ScoringState.ANGLE_HOOD_AND_SPIN_SHOOTER;
		});
		driverController.registerButtonListener(ButtonEvent.RELEASE, Button.S, () -> {
			scoringNextState = ScoringState.LIMELIGHT_OFF;
		});

		// Operator Shoot Close 
		opController.registerButtonListener(ButtonEvent.PRESS, Button.LB, () -> {
			shooterSpeed = 0.425;
			targetShooterRpm = -2000.0;
			useCalculatedHoodAngle = false;
			isFastIndexer = true;
			scoringNextState = ScoringState.ANGLE_HOOD_AND_SPIN_SHOOTER;
		});
		opController.registerButtonListener(ButtonEvent.RELEASE, Button.LB, () -> {
			scoringNextState = ScoringState.IDLE;
		});

		// Operator Spin-Up
		opController.registerButtonListener(ButtonEvent.PRESS, Button.RT, () -> {
			shooterSpeed = 0.8;
		});
		opController.registerButtonListener(ButtonEvent.RELEASE, Button.RT, () -> {
			shooterSpeed = 0.0;
		});

		// Increment
		opController.registerButtonListener(ButtonEvent.PRESS, Button.N, () -> {
			shooter.incrementValue(0.01);
			log(String.format("Incremented to: %f" , shooter.getIncrement()));
		});

		opController.registerButtonListener(ButtonEvent.PRESS, Button.S, () -> {
			shooter.incrementValue(-0.01);
			log(String.format("Decremented to: %f" , shooter.getIncrement()));
		});

		opController.registerButtonListener(ButtonEvent.PRESS, Button.START, () -> {
			shooter.setIncrement(0.0);
			log(String.format("Reset to: %f" , shooter.getIncrement()));
		});

		// NetworkTables Stuff
		ballCountEntry = NetworkTableInstance.getDefault().getTable("Shuffleboard").getEntry("ballCount");
		motorSpeedEntry = NetworkTableInstance.getDefault().getTable("Shuffleboard").getEntry("motorSpeed");
		motorSpeedEntry.setDouble(shooter.getShooterSpeed());
		ballCount = 0;
		ballCountEntry.setNumber(ballCount);

		shooter.getLeftMotor().setIdleMode(IdleMode.kCoast);
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

		intake.update();

		// if (count < maxCount) {
		// 	if (indexer.getMotor().getOutputCurrent() > 25) {
		// 		count++;
		// 	} else {
		// 		count = 0;
		// 	}
		// } else {
		// 	indexer.getMotor().disable();
		// }

		motorSpeedEntry.setDouble(shooter.getShooterSpeed());

		double barSpeed = 1.0;

		ballCountEntry.setNumber(ballCount);

		boolean scoringOnChange = (scoringNextState != scoringCurrentState); // TODO Check if necessary
		scoringCurrentState = scoringNextState;

		switch (scoringCurrentState) {
			case IDLE: {
				indexerSpeed = 0.0;
				shooterSpeed = 0.0;
				servoAngle = 1.0;
				funnelSpeed = 0.0;
				targetShooterRpm = 0.0;
				intakeSpeed = 0.0;

				if (driverController.getButton(Button.LT)) {
					intakeSolenoidState = true;
					intakeSpeed = -barSpeed;
					funnelSpeed = -1.0;
				} else if (driverController.getButton(Button.RT)) {
					intakeSolenoidState = true;
					intakeSpeed = barSpeed;
					if (ballCount != 4) {
						funnelSpeed = 1.0;
					} else {
						funnelSpeed = 0.0;
					}
				} else {
					intakeSolenoidState = false;
					intakeSpeed = 0.0;
					funnelSpeed = 0.0;
				}

				if (!indexer.getProximity(1).get()) {
					scoringNextState = ScoringState.SPINNING;
				}
				if (!indexer.getProximity(2).get()) {
					scoringNextState = ScoringState.FULL;
				}
			} break;
			case SPINNING: {
				if (scoringOnChange) {
					ballCount++;
				}

				funnelSpeed = 1.0;
				indexerSpeed = 0.75;

				if (!indexer.getProximity(2).get()) {
					funnelSpeed = 0.0;
					scoringNextState = ScoringState.FULL;
					break;
				}
				if (indexer.getProximity(1).get()) {
					scoringNextState = ScoringState.IDLE;
				}
				// if (indexer.getCurrent() > 30) {
				// 	timeCurrent++;
				// 	if (timeCurrent > 200) { // 3 seconds
				// 		timeCurrent = 0;
				// 		scoringNextState = ScoringState.REVERSE;
				// 	}
				// }
			} break;
			case FULL: {
				indexerSpeed = 0.0;
				ballCount = 4;

				// intake.setBarSolenoid(true);

				if (driverController.getButton(Button.LT)) {
					intakeSpeed = -barSpeed;
				} else if (driverController.getButton(Button.RT)) {
					intakeSpeed = barSpeed;
				} else {
					intakeSpeed = 0.0;
				}
			} break;
			case ANGLE_HOOD_AND_SPIN_SHOOTER: {
				if (scoringOnChange) {
					limelight.setLEDOn(LedEnum.FORCE_ON);
				}

				if (useCalculatedHoodAngle) {
					servoAngle = calculateHoodAngle() + shooter.getIncrement();
				}
				double absShooterSpeed = shooter.getShooterSpeed();

				if (Math.abs(targetShooterRpm - absShooterSpeed) < 100) {
					scoringNextState = ScoringState.SHOOT;
				}
			} break;
			case SHOOT: {
				if (scoringOnChange) {
					limelight.setLEDOn(LedEnum.FORCE_ON);
					if (isFastIndexer || MathUtil.calculateDistanceFromTarget(limelight.getTargetY() + shooter.angleLimelight, Shooter.lHeight, Shooter.tHeight) < 250) {
						indexerSpeed = 1.0;
					} else {
						indexerSpeed = 0.75;
					}
					funnelSpeed = 1.0;
					intakeSpeed = 1.0;
				}
				
				if (useCalculatedHoodAngle) {
					servoAngle = calculateHoodAngle() + shooter.getIncrement();
				}
			} break;
			case LIMELIGHT_OFF: {
				ballCount = 0;
				limelight.setProcessing(false);
				limelight.setLEDOn(LedEnum.FORCE_OFF);
				funnelSpeed = 0.0;
				intakeSpeed = 0.0;
				indexerSpeed = 0.0;
				scoringNextState = ScoringState.IDLE;
			} break;
			case REVERSE: {
				System.out.println("ok2");
				funnelSpeed = -1.0;
				intakeSpeed = -1.0;
				indexerSpeed = -1.0;
				if (indexer.getProximity(1).get()) {
					timeSensor++;
					if (timeSensor > 75) {
						timeSensor = 0;
						scoringNextState = ScoringState.IDLE;
					}
				}
				else if (!indexer.getProximity(1).get()) {
					timeSensor++;
					if (timeSensor > 600) {
						timeSensor = 0;
						scoringNextState = ScoringState.IDLE;
					}
				}
			} break;
			case MANUAL: {
				break;
			}
		}
		

		intake.setFunnel(funnelSpeed);
		intake.setBar(intakeSpeed);
		intake.setBarSolenoid(intakeSolenoidState);
		indexer.setSpeed(indexerSpeed);
		shooter.setShooterSpeed(shooterSpeed);
		shooter.setServoAngle(servoAngle);
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

	private double calculateHoodAngle() {
		limelight.setLEDOn(LedEnum.FORCE_ON);
		limelight.setProcessing(true);

		System.out.println(MathUtil.calculateDistanceFromTarget(limelight.getTargetY() + shooter.angleLimelight, Shooter.lHeight, Shooter.tHeight));
				if (driverController.getButton(Button.LB)) {
					if (MathUtil.calculateDistanceFromTarget(limelight.getTargetY() + shooter.angleLimelight, Shooter.lHeight, Shooter.tHeight) < 250) {
						shooterSpeed = 0.8;
						targetShooterRpm = -4000.0;
					} else if (MathUtil.calculateDistanceFromTarget(limelight.getTargetY() + shooter.angleLimelight, Shooter.lHeight, Shooter.tHeight) > 250 && MathUtil.calculateDistanceFromTarget(limelight.getTargetY() + shooter.angleLimelight, Shooter.lHeight, Shooter.tHeight) < 400) {
						shooterSpeed = 0.95;
						targetShooterRpm = -4400.0;
					} else if (MathUtil.calculateDistanceFromTarget(limelight.getTargetY() + shooter.angleLimelight, Shooter.lHeight, Shooter.tHeight) >= 400) {
						shooterSpeed = 1.0;
						targetShooterRpm = -4800;
					}
					// targetShooterRpm = shooter.getTargetRpm(servoAngle*66, MathUtil.calculateDistanceFromTarget(limelight.getTargetY() + shooter.angleLimelight, Shooter.lHeight, Shooter.tHeight));
					// shooterSpeed = targetShooterRpm/5000;
				}
		
		double hoodAngle = MathUtil.calculateHoodAngle(limelight.getTargetY());

        return hoodAngle;
	}
}