package auton.routines;

import auton.commands.GeneratePath;
import auton.commands.SequentialCommand;

public class LoadPath extends SequentialCommand {

    public LoadPath () {
        super(
            new GeneratePath()
        );
    }
    
}