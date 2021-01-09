package robot;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Gyro implements Sendable{

	private AHRS gyro;

	public Gyro() {
		gyro = new AHRS(SPI.Port.kMXP);
		gyro.reset();
		
		// gyro.setAngleAdjustment(180);
	}

	public double getRawYaw() {
		return (double) gyro.getYaw() * -1.0;
	}

	public double getYaw() {
		double val;
		synchronized (gyro) {
			val = gyro.getYaw() + 180;
		}
		return (-val + 360) % 360;
	}

	public double getRate() {
		return gyro.getRate();
	}

	public void resetGyro() {
		synchronized (gyro) {
			gyro.reset();
		}
	}

	public double getPitch() {
		synchronized (gyro) {
			return gyro.getPitch();
		}
	}

	public double getRoll() {
		synchronized (gyro) {
			return gyro.getRoll();
		}
	}

	@Override
	public void initSendable(SendableBuilder builder) {
		// TODO Auto-generated method stub
		builder.setSmartDashboardType("Gyro");
		builder.addDoubleProperty("Value", this::getRawYaw, null);
	}
}