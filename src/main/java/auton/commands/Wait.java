package auton.commands;

public class Wait extends Command {

	private double duration;

	public Wait(double duration) {
		this.duration = duration;
	}

	@Override
	public void init() {
		super.init();
	}

	@Override
	public void run() { }

	@Override
	public boolean isDone() {
		return currentTime() >= startTime + duration;
	}

}