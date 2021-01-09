package auton.commands;

import java.util.ArrayDeque;

import auton.commands.Command;

public class SequentialCommand extends Command {

    private ArrayDeque<Command> autonDeque;
    private Command activeCommand = null;

    public SequentialCommand (Command... commands) {
        autonDeque = new ArrayDeque<>();
        for (Command c : commands) {
            autonDeque.add(c);
        }
    }

    public void run () {
        if ((activeCommand == null  || activeCommand.isDone()) && !autonDeque.isEmpty()) {
            activeCommand = autonDeque.pop();  
            activeCommand.init();
        } 
        if (activeCommand != null && !activeCommand.isDone()) {
            activeCommand.run();
        }
    }

    @Override
    public boolean isDone() {
        return (activeCommand == null || activeCommand.isDone()) && autonDeque.isEmpty();
    }

}