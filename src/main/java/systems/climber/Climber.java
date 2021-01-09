package systems.climber;

import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import utilities.CANSparkMaxSendable;

public class Climber {

	public static enum GearPosition { SPEED, TORQUE };

	private CANSparkMaxSendable liftMotor;
	private Solenoid gearShifter;

	private DoubleSolenoid deploySolenoid;

	public Climber(int liftMotorChannel, int gearSolenoidChannel, int forwardSolenoidChannel, int reverseSolenoidChannel) {
		liftMotor = new CANSparkMaxSendable(liftMotorChannel, MotorType.kBrushless);
		gearShifter = new Solenoid(gearSolenoidChannel);

		deploySolenoid = new DoubleSolenoid(forwardSolenoidChannel, reverseSolenoidChannel);
	}

	public void setPistons(boolean bState) {
		Value vState = (bState) ? Value.kForward : Value.kReverse;
		deploySolenoid.set(vState);
	}

	public void setSpeed(double speed) {
		liftMotor.set(speed);
	}

	public void shiftGear(GearPosition gp) {
		if (gp == GearPosition.SPEED) {
			gearShifter.set(false); // ASSUMES True = Speed Mode
		} else {
			gearShifter.set(true); // ASSUMES False = Torque Mode
		}
	}

	public double getEncoderCount() {
		return liftMotor.getEncoder().getPosition();
	}


	public CANSparkMaxSendable getLiftMotor() {
		return this.liftMotor;
	}

	public Solenoid getGearPiston() {
		return this.gearShifter;
	}

	public DoubleSolenoid getDeploySolenoid() {
		return this.deploySolenoid;
	}

	public boolean getShiftGearState() {
		return this.gearShifter.get();
	}

}