package auton.commands;

public class Print extends Command {

	private String printMessage;
	private double duration;

	public Print(String printMessage, double duration) {
		this.printMessage = printMessage;
		this.duration = duration*1000;
	}

	@Override
	public void run() {
		System.out.println("    " + printMessage);
	}

	@Override
	public boolean isDone() {
		return currentTime() >= startTime + duration;
	}

}