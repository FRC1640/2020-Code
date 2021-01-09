package auton.routines;

import auton.commands.*;

public class TenBallChaosPost extends SequentialCommand {

    public TenBallChaosPost () {
        super(
            // new PointWheels(90),
            // new ParallelCommand(
            //     new DeployIntake(),
            //     new RunIntake(1.0),
            //     new Drive(0.0, 0.4, 0.0, 4.5)
            // ),
            // new RetractIntake(),
            // new Drive(0.0, -0.3, 0.0, 3),
            // new PointWheels(180),
            // new Drive(-0.55, 0.0, 0.0, 4),
            // new AutoAlign(),
            // new Shoot(),
            // new ParallelCommand(
            //     new DeployIntake(),
            //     new RunIntake(1.0),
            //     new Drive(-0.4, 0.4, 0.0, 3.5)
            // ),
            // new Drive(0.0, -0.4, 0.0, 1.5),
            // new Drive(-0.4, 0.0, 0.0, 1.5),
            // new TurnGyro(45),
            // new ParallelCommand(
            //     new DeployIntake(),
            //     new RunIntake(1.0),
            //     new Drive(0.0, 0.4, 0.0, 2)
            // ),
            // new Drive(-0.4, 0.0, 0.0, .5),
            // new AutoAlign(),
            // new Shoot()    
        );
    }
    
}