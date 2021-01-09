package auton;

public class AutonBridge {
    public static boolean shootDone;
    
    public static void setShootDone(boolean value) {
        shootDone = value;
    }

    public static boolean getShootDone() {
        return shootDone;
    }
}