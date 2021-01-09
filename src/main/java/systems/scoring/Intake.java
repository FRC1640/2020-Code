package systems.scoring;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.Solenoid;
import utilities.CANSparkMaxSendable;

public class Intake {

	private boolean intakeState = false; 
	private CANSparkMaxSendable barMotor;
	private CANSparkMaxSendable funnelMotor;

	private Solenoid barSolenoid;

    public Intake(int barMotorChannel, int funnelMotorChannel, int solenoidChannel) {
		barMotor = new CANSparkMaxSendable(barMotorChannel, MotorType.kBrushless);
		// funnelMotor = new CANSparkMaxSendable(funnelMotorChannel, MotorType.kBrushed); //For Deux
		funnelMotor = new CANSparkMaxSendable(funnelMotorChannel, MotorType.kBrushless); //For Prime

		barSolenoid = new Solenoid(solenoidChannel);
	}

	public Intake(int barMotorChannel, int funnelMotorChannel) {
		// barMotor = new CANSparkMaxSendable(barMotorChannel, MotorType.kBrushless);
		// funnelMotor = new CANSparkMaxSendable(barMotorChannel, MotorType.kBrushless);
	}


	public void retractBar() {
		barSolenoid.set(false);
	}

	public void ejectBar() {
		barSolenoid.set(true);
	}

	public void setBar(double speed) {
		// System.out.println("Set Bar: " + speed);
		barMotor.set(-speed);
	}

	public void setBarSolenoid(boolean value) {
		intakeState = value;
	}

	public void setFunnel(double speed) {
		funnelMotor.set(-speed);
	}

	public Solenoid getSolenoid() {
		return this.barSolenoid;
	}

	public boolean getSolenoidState () {
		return barSolenoid.get();
	}
	
	public CANSparkMaxSendable getBarMotor() {
		return this.barMotor;
	}
	
	public CANSparkMaxSendable getFunnelMotor() {
		return this.funnelMotor;
	}

	public void update () {
		barSolenoid.set(intakeState);
	}
}