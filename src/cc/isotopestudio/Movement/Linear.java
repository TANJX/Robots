package cc.isotopestudio.Movement;

import robocode.AdvancedRobot;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;

/**
 * Created by Mars on 5/26/2016.
 * Copyright ISOTOPE Studio
 */
public class Linear extends AdvancedRobot {
    public void run() {
        while (true) {
            ahead(100);
            turnRight(90);
        }
    }

    @Override
    public void onHitWall(HitWallEvent event) {
        back(110);
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {

    }

}
