package systems.drive.pivot;

import com.revrobotics.AlternateEncoderType;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.EncoderType;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import utilities.CANSparkMaxSendable;
import utilities.Vector2;

public class Pivot {

	private CANSparkMaxSendable driveMotor;
	private CANSparkMaxSendable steerMotor;
	private AnalogInput resolver;
	private PIDController steerPidController;

	private double targetAngleD;
	private double targetSpeed;
	private double angleOffset;

	private boolean enabled;
	private boolean flipDrive;

	PivotConfig config;

	private double maxV;
	private double minV;

	/**
	 * 
	 * @param position           Position vector for where this pivot is located on
	 *                           the robot
	 * @param driveMotorChannel  Channel the drive motor is connected to
	 * @param steerMotorChannel  Channel the steering motor is connected to
	 * @param resolverChannel    Channel the resolver is connected to
	 * @param minResolverVoltage Minimum voltage recognized by the resolver
	 * @param maxResolverVoltage Maximum voltage recognized by the resolver
	 */
	public Pivot(String id) {
		maxV = Double.MIN_VALUE;
		minV = Double.MAX_VALUE;

		this.config = PivotConfig.getCfg(id);

		flipDrive = false;
		targetAngleD = 0.0;
		enabled = true;

		driveMotor = new CANSparkMaxSendable(config.driveChannel, MotorType.kBrushless);
		steerMotor = new CANSparkMaxSendable(config.steerChannel, MotorType.kBrushless) {
			@Override
			public void pidWrite(double output) {
				super.pidWrite(output);
			}
		};

		driveMotor.setInverted(config.reverseDrive);
		steerMotor.setInverted(config.reverseSteer);

		resolver = new AnalogInput(config.resolverChannel) {

			@Override
			public double pidGet() {
				double dAngle = (enabled) ? targetAngleD - getPivotAngleD() : 0.0;
				double dAngleAbs = Math.abs(dAngle);
				flipDrive = (90.0 <= dAngleAbs) && (dAngleAbs <= 270.0);
				double sin = Math.sin(dAngle * Math.PI / 180.0);
				sin = (flipDrive) ? -sin : sin;
				return sin;
			}

		};

		steerPidController = new PIDController(0.625, 0.0, 0.005, 0.0, resolver, steerMotor, 0.02);
	}

	public double getMaxRpm() {
		return config.rpmMax;
	}

	public String getId() {
		return config.id;
	}

	public String getName() {
		return config.name;
	}

	/**
	 * 
	 * @return Returns the position vector
	 */
	public Vector2 getPosition() {
		return config.position;
	}

	/**
	 * 
	 */
	public void enable() {
		if (!enabled) {
			enabled = true;
			steerPidController.enable();
		}
	}

	/**
	 * 
	 */
	public void disable() {
		if (enabled) {
			enabled = false;
			steerPidController.disable();
		}
	}

	public CANSparkMaxSendable getDriveMotor() {
		return driveMotor;
	}

	public CANSparkMaxSendable getSteerMotor() {
		return steerMotor;
	}

	/**
	 * 
	 * @return Returns the flip drive flag
	 */
	public boolean getFlipDrive() {
		return flipDrive;
	}

	public CANEncoder getEncoder() {
		return driveMotor.getAlternateEncoder(AlternateEncoderType.kQuadrature, 4096);
	}

	public double getEncoderPosition() {
		return driveMotor.getAlternateEncoder(AlternateEncoderType.kQuadrature, 4096).getPosition();
	}

	public double getNeoSpeed() {
		return driveMotor.getEncoder().getVelocity();
	}

	/**
	 * 
	 * @return Returns the target angle in degrees
	 */
	public double getTargetAngleD() {
		return targetAngleD;
	}

	/**
	 * 
	 * @return Returns the target angle in radians
	 */
	public double getTargetAngleR() {
		return targetAngleD * Math.PI / 180.0;
	}

	/**
	 * 
	 * @return Returns the target drive speed
	 */
	public double getTargetSpeed() {
		return targetSpeed;
	}

	/**
	 * 
	 * @return Returns the absolute angle this pivot is pointing, in radians
	 */
	public double getPivotAngleR() {
		return getPivotAngleD() * Math.PI / 180.0;
	}

	/**
	 * 
	 * @return Returns the absolute angle this pivot is pointing, in degrees
	 */
	public double getPivotAngleD() {
		double voltage = getRawVoltage();

		config.minVoltage = Math.min(config.minVoltage, voltage);
		config.maxVoltage = Math.max(config.maxVoltage, voltage);

		double vSlope = 360.0 / (config.maxVoltage - config.minVoltage);
		double vOffset = -vSlope * config.minVoltage;

		double angle = (vSlope * voltage + vOffset) - angleOffset;
		return (config.reverseAngle) ? 360 - angle : angle;
	}

	/**
	 * 
	 * @param angleD Target angle to set in degrees. 0-degrees is east (right), and
	 *               increases counter-clockwise
	 */
	public void setTargetAngleD(double angleD) {
		targetAngleD = (angleD - config.offset + 720.0) % 360.0;
	}

	/**
	 * 
	 * @param angleR Target angle to set in radians
	 */
	public void setTargetAngleR(double angleR) {
		setTargetAngleD(Math.toDegrees(angleR));
	}

	/**
	 * 
	 * @param speed Target speed for this pivot
	 */
	public void setSpeed(double speed) {
		setMotorDirect(flipDrive ? -speed : speed);
	}

	public void setMotorDirect(double speed) {
		if (!enabled) {
			speed = 0.0;
		}
		driveMotor.set(speed);
	}

	public void setRotationSpeed(double speed) {
		if (!enabled) {
			speed = 0.0;
		}
		steerMotor.set(speed);
	}

	public double getRawVoltage() {
		double v = resolver.getVoltage();
		maxV = Math.max(maxV, v);
		minV = Math.min(minV, v);
		return v;
	}

	public double getMaxVoltage() {
		return maxV;
	}

	public double getMinVoltage() {
		return minV;
	}

	// @Override
	// public void initSendable(SendableBuilder builder) {
	// 	// TODO Auto-generated method stub
	// 	// TODO Auto-generated method stub
	// 	builder.setSmartDashboardType("Speed Controller");
	// 	builder.setSafeState(() -> {
	// 		setSpeed(0.0);
	// 		setRotationSpeed(0.0);
	// 	});
	// }

}