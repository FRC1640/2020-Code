package utilities;

import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

public class CANSparkMaxSendable extends CANSparkMax implements Sendable {

	public CANSparkMaxSendable(int deviceID, MotorType type) {
		super(deviceID, type);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initSendable(SendableBuilder builder) {
		// TODO Auto-generated method stub
		builder.setSmartDashboardType("Speed Controller");
		builder.setSafeState(() -> {
			super.set(0.0);
		});
		builder.addDoubleProperty("Value", super::get, super::set);
	}

}