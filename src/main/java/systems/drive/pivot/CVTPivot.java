package systems.drive.pivot;

import utilities.MathUtil;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

public class CVTPivot extends Pivot implements Sendable {

	private Servo servo;

	public static final double kMinAngle = 25; // 50.0;
	public static final double kMaxAngle = 145; // 130.0;
	public static final double kNeutralAngle = (kMaxAngle + kMinAngle) / 2.0; // 120;
	private static final double kRange = (kMaxAngle - kMinAngle);
	private static final double kHalfRange = kRange / 2.0;

	PIDController pidController;
	PIDOutput out;
	PIDSource src;
	double speed = 0;
	Encoder encoder;
	double eSpeed = 3000;
	boolean isPID = false;
	CVTPivot self;

	public CVTPivot(String id) {
		super(id);
		servo = new Servo(config.cvtChannel);
		setTransmission(0.0);
		src = new PIDSource() {

			@Override
			public void setPIDSourceType(PIDSourceType pidSource) {
			}

			@Override
			public double pidGet() {
				// TODO Auto-generated method stub
				return eSpeed - getNeoSpeed();
			}

			@Override
			public PIDSourceType getPIDSourceType() {
				// TODO Auto-generated method stub
				return PIDSourceType.kRate;
			}
		};

		out = new PIDOutput() {

			@Override
			public void pidWrite(double output) {
				setMotorDirect(-output);
			}
		};

		pidController = new PIDController(1.0 / 22500, 0, 0.00005, src, out);

		pidController.disable();

	}

	/**
	 * 0 - 1 range
	 * 
	 * @param transmission
	 */
	public void setTransmission(double transmission) {
		transmission = MathUtil.constrain(transmission, 0.0, 1.0);
		transmission *= kRange;

		servo.setAngle(transmission);
	}

	public void setServoAngle(double angle) {
		servo.setAngle(angle);
	}

	public double getTransmission() {
		return servo.getAngle()/kRange;
	}

	public double getServoAngle() {
		return servo.getAngle();
	}

	public void setSpeed(double speed, boolean isSlow) {

		if (Math.abs(getNeoSpeed()) > 3500 && isSlow == false) {
			setTransmission(Math.pow(speed, 8));
		} else if (isSlow == true) {
			setTransmission(0.0);
		} else {
			setTransmission(0.0);
		}
		super.setSpeed(speed);
	}

	@Override
	public void initSendable(SendableBuilder builder) {
		// TODO Auto-generated method stub
		builder.setSmartDashboardType("Number Slider");
		builder.setSafeState(() -> {
			setTransmission(0.0);
		});
		builder.addDoubleProperty("CVT Angle", this::getTransmission, this::setTransmission);
	}

}
