package auton.commands;

public abstract class Command {
	long startTime;

	public void init() {
		System.out.println(toString());
		startTime = currentTime();
	}

	public long currentTime () {
		return System.currentTimeMillis();
	}

	public abstract void run();

	public abstract boolean isDone();

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}

}