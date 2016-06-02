package cc.isotopestudio.Movement;

import robocode.AdvancedRobot;
import robocode.HitWallEvent;

/**
 * Created by Mars on 5/26/2016.
 * Copyright ISOTOPE Studio
 */
public class Linear extends AdvancedRobot {
    public void run() {
        while (true) {
            ahead(300);
            turnRight(180 * Math.random());
        }
    }

    @Override
    public void onHitWall(HitWallEvent event) {
        back(100);
        turnRight(180 * Math.random());
    }

}
