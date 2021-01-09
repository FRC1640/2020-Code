package utilities;

import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

public class ServoSendable extends Servo implements Sendable {
	
	public ServoSendable(int channel) {
		super(channel);
	}

	@Override
	public void initSendable(SendableBuilder builder) {
		// TODO Auto-generated method stub
		builder.setSmartDashboardType("Number Slider");
		builder.setSafeState(() -> {
			super.set(0.0);
		});
		builder.addDoubleProperty("Value", super::get, super::set);
	}
}