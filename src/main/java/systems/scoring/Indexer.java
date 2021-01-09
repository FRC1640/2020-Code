package systems.scoring;

import java.util.HashMap;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.DigitalInput;
import robot.Devices;
import utilities.CANSparkMaxSendable;
import utilities.IController;

public class Indexer {

	private CANSparkMaxSendable motor;
	private Indexer indexer;

	private HashMap<Integer, DigitalInput> digitalInputMap;

    public Indexer(int motorChannel, int proxInput1, int proxInput2) {
		motor = new CANSparkMaxSendable(motorChannel, MotorType.kBrushless);

		digitalInputMap = new HashMap<>();

		digitalInputMap.put(1, new DigitalInput(proxInput1));
		digitalInputMap.put(2, new DigitalInput(proxInput2));
	}

	public Indexer(int motorChannel) {
		motor = new CANSparkMaxSendable(motorChannel, MotorType.kBrushless);
	}


	public DigitalInput getProximity(int id) {
		if (digitalInputMap == null) {
			return new DigitalInput(0);
		}
		return digitalInputMap.get(id);
	}

	public CANSparkMaxSendable getMotor() {
		return this.motor;
	}

	public void setSpeed(double speed) {
		motor.set(-speed);
	}

	public double getCurrent () {
		return motor.getOutputCurrent();
	}
}