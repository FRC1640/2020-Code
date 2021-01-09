package auton.commands;

import robot.Devices;
import systems.scoring.Indexer;
import systems.scoring.Intake;

public class AutomateIndexer extends Command {

	public static enum ScoringState {
		IDLE, SPINNING, FULL
	}

	private boolean scoringOnChange;
	private ScoringState scoringNextState;
	private ScoringState scoringCurrentState;

	private int ballCount;
	private int targetBallCount;

	private Indexer indexer;
	private Intake intake;

	private boolean isDone;

	private double funnelSpeed = 0.0;
	private double indexerSpeed = 0.0;
	private double intakeSpeed = 0.0;
	private double time;

	public AutomateIndexer(int startingBallCount, int targetBallCount, double time) {
		indexer = Devices.getIndexer();
		intake = Devices.getIntake();

		scoringOnChange = false;
		scoringCurrentState = ScoringState.IDLE;
		scoringNextState = ScoringState.IDLE;

		this.ballCount = startingBallCount;
		this.targetBallCount = targetBallCount;
		this.time = time * 1000;
	}

	@Override
	public void init () {
		super.init();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		// System.out.println(scoringCurrentState);

		boolean scoringOnChange = (scoringNextState != scoringCurrentState); // TODO Check if necessary
		scoringCurrentState = scoringNextState;

		switch (scoringCurrentState) {
			case IDLE: {
				indexerSpeed = 0.0;
				intakeSpeed = 1.0;
				funnelSpeed = 1.0;

				// if (ballCount >= targetBallCount) {
				// 	intakeSpeed = 0.0;
				// 	funnelSpeed = 0.0;
				// 	isDone = true;
				// }

				if (!indexer.getProximity(2).get()) {
					funnelSpeed = 0.0;
					scoringNextState = ScoringState.FULL;
					break;
				}
				if (!indexer.getProximity(1).get()) {
					scoringNextState = ScoringState.SPINNING;
				}
			} break;
			case SPINNING: {
				if (scoringOnChange) {
					ballCount++;
				}

				intakeSpeed = 1.0;
				indexerSpeed = 0.75;
				funnelSpeed = 1.0;

				if ((!indexer.getProximity(2).get() || ballCount == targetBallCount) && indexer.getProximity(1).get()) {
					funnelSpeed = 0.0;
					scoringNextState = ScoringState.FULL;
					break;
				}
				if (indexer.getProximity(1).get()) {
					scoringNextState = ScoringState.IDLE;
				}
			} break;
			case FULL: {
				funnelSpeed = 0.0;
				indexerSpeed = 0.0;
				ballCount = 4;
			} break;
		}

		intake.setFunnel(funnelSpeed);
		intake.setBar(intakeSpeed);
		indexer.setSpeed(indexerSpeed);
	}

	@Override
	public boolean isDone() {
		// TODO Auto-generated method stub
		if (scoringCurrentState == ScoringState.FULL || currentTime() >= startTime + time) {
			intake.setFunnel(0.0);
			indexer.setSpeed(0.0);
			intake.setBar(0.0);
			return true;
		}
		return false;
	}
}