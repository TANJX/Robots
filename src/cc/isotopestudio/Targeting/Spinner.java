package cc.isotopestudio.Targeting;

import robocode.AdvancedRobot;
import robocode.Robot;

import java.awt.*;

/**
 * Created by Mars on 5/26/2016.
 * Copyright ISOTOPE Studio
 */
public class Spinner extends AdvancedRobot {

    public void run() {
        // Set colors
        setBodyColor(Color.blue);
        setGunColor(Color.blue);
        setRadarColor(Color.black);
        setScanColor(Color.yellow);

        // Loop forever
        while (true) {
            // Tell the game that when we take move,
            // we'll also want to turn right... a lot.
            setTurnRight(1000);
            // Limit our speed to 5
            setMaxVelocity(2);
            // Start moving (and turning)
            ahead(1000);
            // Repeat.
        }
    }
}
