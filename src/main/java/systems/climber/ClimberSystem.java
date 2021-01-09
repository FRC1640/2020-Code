package systems.climber;

import java.util.HashMap;

import robot.Devices;
import systems.RobotSystem;
import systems.climber.controllers.ClimberFullAutoController;
import systems.climber.controllers.ClimberManualController;
import utilities.IController;

public class ClimberSystem extends RobotSystem {

	public static enum ClimberController { MANUAL_CLIMB, FULL_AUTO };

	private HashMap<ClimberController, IController> climberControllerMap;

	private Climber climber;

	private ClimberManualController currentController;
	
	public ClimberSystem() {
		super("Climber System");

		climber = Devices.getClimber();

		currentController = new ClimberManualController(climber);

		// climberControllerMap = new HashMap<ClimberController, IController>();
		// climberControllerMap.put(ClimberController.MANUAL_CLIMB, new ClimberManualController(climber));
		// climberControllerMap.put(ClimberController.FULL_AUTO, new ClimberFullAutoController(climber));
	}


	@Override
	public void init() {
		// TODO Auto-generated method stub"
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
		// TODO Auto-generated method stub
		currentController.update();
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
		currentController.activate();
	}

	@Override
	public void disable() throws Exception {
		// TODO Auto-generated method stub
		currentController.deactivate();
	}
}