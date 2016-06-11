package cc.isotopestudio.Movement;

import robocode.AdvancedRobot;
import robocode.HitRobotEvent;
import robocode.ScannedRobotEvent;

import java.awt.*;

import static robocode.util.Utils.normalRelativeAngleDegrees;

/**
 * Created by Mars on 6/11/2016.
 * Copyright ISOTOPE Studio
 */
public class Tracking extends AdvancedRobot {
    /**
     * Copyright (c) 2001-2016 Mathew A. Nelson and Robocode contributors
     * All rights reserved. This program and the accompanying materials
     * are made available under the terms of the Eclipse Public License v1.0
     * which accompanies this distribution, and is available at
     * http://robocode.sourceforge.net/license/epl-v10.html
     */
    private int count = 0; // Keeps track of how long we've
    // been searching for our target
    private double gunTurnAmt; // How much to turn our gun when searching
    private String trackName; // Name of the robot we're currently tracking

    /**
     * run:  Tracker's main run function
     */
    public void run() {
        // Set colors
        setBodyColor(new Color(128, 128, 50));
        setGunColor(new Color(50, 50, 20));
        setRadarColor(new Color(200, 200, 70));
        setScanColor(Color.white);
        setBulletColor(Color.blue);

        // Prepare gun
        trackName = null; // Initialize to not tracking anyone
        setAdjustRadarForRobotTurn(true); // Keep the gun still when we turn
        gunTurnAmt = 10; // Initialize gunTurn to 10

        // Loop forever
        while (true) {
            // turn the Gun (looks for enemy)
            turnRadarRight(360);
            // Keep track of how long we've been looking
        }
    }

    /**
     * onScannedRobot:  Here's the good stuff
     */
    public void onScannedRobot(ScannedRobotEvent e) {

        // If we have a target, and this isn't it, return immediately
        // so we can get more ScannedRobotEvents.
        if (trackName != null && !e.getName().equals(trackName)) {
            return;
        }
        // If we don't have a target, well, now we do!
        if (trackName == null) {
            trackName = e.getName();
            out.println("Tracking " + trackName);
        }
        // This is our target.  Reset count (see the run method)
        count = 0;
        // If our target is too far away, turn and move toward it.
        if (e.getDistance() > 150) {
            gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));

            turnGunRight(gunTurnAmt); // Try changing these to setTurnGunRight,
            turnRight(e.getBearing()); // and see how much Tracker improves...
            // (you'll have to make Tracker an AdvancedRobot)
            go(e.getDistance() - 140);
            return;
        }

        // Our target is close.
        gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));
        turnGunRight(gunTurnAmt);

        // Our target is too close!  Back up.
        if (e.getDistance() < 100) {
            if (e.getBearing() > -90 && e.getBearing() <= 90) {
                go(-40);
            } else {
                go(40);
            }
        }
        scan();
    }

    /**
     * onHitRobot:  Set him as our new target
     */
    public void onHitRobot(HitRobotEvent e) {
        // Only print if he's not already our target.
        if (trackName != null && !trackName.equals(e.getName())) {
            out.println("Tracking " + e.getName() + " due to collision");
        }
        // Set the target
        trackName = e.getName();
        // Back up a bit.
        // Note:  We won't get scan events while we're doing this!
        // An AdvancedRobot might use setBack(); execute();
        gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));
        turnGunRight(gunTurnAmt);
        go(-50);
    }


    public void go(double d) {
        ahead(d);
        setBodyColor(new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)));
    }
}
