package auton.routines;

import auton.commands.AutoAlign;
import auton.commands.AutomateIndexer;
import auton.commands.DeployIntake;
import auton.commands.DriveEncoder;
import auton.commands.ParallelCommand;
import auton.commands.PointWheels;
import auton.commands.RunIntake;
import auton.commands.SequentialCommand;
import auton.commands.Shoot;
import auton.commands.SpeedShooter;

public class GoMOE extends SequentialCommand {

    public GoMOE () {

        super(
            new SpeedShooter(0.8, 2.0),
            new Shoot(4000),
            new PointWheels(90),
            new ParallelCommand(
                new DeployIntake(),
                new RunIntake(1.0, 3.5),
                new DriveEncoder(0.0, 0.5, 0.0, 60)
            ),
            // new ParallelCommand(
            //     new AutomateIndexer(0, 1, 2.5),
            //     new DriveEncoder(-0.2, -0.25, 0.0, 8)
            // ),
            new ParallelCommand(
                new AutomateIndexer(0, 2, 3.5),
                new DriveEncoder(0.25, 0.3, 0.0, 24)
            ),
            new ParallelCommand(
                new DriveEncoder(0.0, -0.5, 0.0, 5),
                new AutoAlign(),
                new SpeedShooter(0.8, 3)
            ),
            new Shoot(4000) 
            );
		    

    }

}