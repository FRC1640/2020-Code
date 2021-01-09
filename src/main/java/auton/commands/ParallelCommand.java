package auton.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParallelCommand extends Command {

    List<Command> commandArray;

    public ParallelCommand (Command... commandArray) {
		this.commandArray = new ArrayList<Command>();
		this.commandArray.addAll(Arrays.asList(commandArray));
    }

    @Override
    public void init() {
        super.init();
        for (Command command : commandArray) {
            command.init();
        }
    }

    @Override
    public void run() {
        System.out.println("Hello " + commandArray.size());
        for (Command command : commandArray) {
            if (!command.isDone()) {
                command.run();
            }
        }
    }

    @Override
    public boolean isDone() {
		for (int i = 0; i < commandArray.size(); i++) {
			if (commandArray.get(i).isDone()) {
				commandArray.remove(i--);
			}
		}
        return commandArray.isEmpty();
    }

}