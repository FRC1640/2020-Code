package systems.spinner;

import java.awt.Color;

import edu.wpi.first.wpilibj.RobotBase;
import robot.Controller;
import robot.Devices;
import robot.Controller.Button;
import robot.Controller.ButtonEvent;
import systems.RobotSystem;

public class SpinnerSystem extends RobotSystem {

    public static enum SpinnerState {
        START,
        SPIN_UNTIL_COLOR_STAGE_2,
        SPIN_UNTIL_COLOR_STAGE_3,
        STOP,
        RETRACT;
    }

    private SpinnerState currentSpinnerState;
    private SpinnerState nextSpinnerState;
    private Controller opController;
    private Spinner spinner;
    private char firstColor = ' ';
    private int count;
    private char prevColor= ' ';
    private char currentColor = ' ';
    private long startTime = 0;

    public SpinnerSystem () {
        super ("Spinner System");
    }

    @Override
    public void init() {
        count = 0;
        opController = Devices.getOpController();
        spinner = Devices.getSpinner();
        spinner.retractSpinner();
        currentSpinnerState = null;
		nextSpinnerState = SpinnerState.START;
		
        opController.registerButtonListener(ButtonEvent.PRESS, Button.A, () -> {
			switch (spinner.getColor()) {
				case 'B': {
					firstColor = 'B';
				} break;
				case 'G': {
					firstColor = 'G';
				} break;
				case 'R': {
					firstColor = 'R';
				} break;
				case 'Y': {
					firstColor = 'Y';
				}
			}
        });
        opController.registerButtonListener(ButtonEvent.PRESS, Button.E, () -> {
            nextSpinnerState = SpinnerState.STOP;
        });
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
        currentSpinnerState = nextSpinnerState;
        currentColor = spinner.getColor();
        switch (currentSpinnerState) {
            case START: {
                spinner.retractSpinner();
                spinner.setSpinnerSpeed(0.0);
                if (opController.getButton(Button.A)) {
                    nextSpinnerState = SpinnerState.SPIN_UNTIL_COLOR_STAGE_2;
                }
                else if (opController.getButton(Button.B)) {
                    nextSpinnerState = SpinnerState.SPIN_UNTIL_COLOR_STAGE_3;
                }
            } break;
            case SPIN_UNTIL_COLOR_STAGE_2: {
                spinner.deploySpinner();
                spinner.setSpinnerSpeed(0.0);
                if (count <= 8) {
                    spinner.setSpinnerSpeed(1.0);
                    if (currentColor == firstColor && currentColor != prevColor) {
                        count++;
                        System.out.println(count);
                    }
                } else {
                    nextSpinnerState = SpinnerState.STOP;
                }
            } break;
            case SPIN_UNTIL_COLOR_STAGE_3: {
                spinner.deploySpinner();
                spinner.setSpinnerSpeed(0.0);
                char targetColor = spinner.getFMSColor();
                char currentColor = spinner.getColor();
                if (targetColor == 'R') {
                    spinner.setSpinnerSpeed(0.15);
                    if (currentColor == 'B') {
                        nextSpinnerState = SpinnerState.STOP;
                    }
                }
                else if (targetColor == 'G') {
                    spinner.setSpinnerSpeed(0.15);
                    if (currentColor == 'Y') {
                        nextSpinnerState = SpinnerState.STOP;
                    }
                }
                else if (targetColor == 'B') {
                    spinner.setSpinnerSpeed(0.15);
                    if (currentColor == 'R') {
                        nextSpinnerState = SpinnerState.STOP;
                    }
                }
                else if (targetColor == 'Y') {
                    spinner.setSpinnerSpeed(0.15);
                    if (currentColor == 'G') {
                        nextSpinnerState = SpinnerState.STOP;
                    }
                }
            } break;
            case STOP: {
                spinner.deploySpinner();
                spinner.setSpinnerSpeed(0.0);
                nextSpinnerState = SpinnerState.RETRACT;
            } break;
            case RETRACT: {
                spinner.retractSpinner();
                spinner.setSpinnerSpeed(0.0);
                nextSpinnerState = SpinnerState.START;
            }
        }
        prevColor = currentColor;
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

}