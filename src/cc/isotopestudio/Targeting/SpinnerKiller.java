package cc.isotopestudio.Targeting;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

import java.awt.*;

/**
 * Created by Mars on 5/26/2016.
 * Copyright ISOTOPE Studio
 */
public class SpinnerKiller extends AdvancedRobot {

    private int count = 0;

    public void run() {
        setBodyColor(new Color(255, 81, 125));
        while (true) {
            turnGunRight(360);

        }
    }

    double oldX = -1, oldY = -1;
    long oldTime = -1;

    public void onScannedRobot(ScannedRobotEvent e) {
        double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
        if (oldX == -1) {
            oldX = getX() + e.getDistance() * Math.sin(absoluteBearing);
            oldY = getY() + e.getDistance() * Math.cos(absoluteBearing);
            oldTime = e.getTime();
            return;
        }
        double newX = getX() + e.getDistance() * Math.sin(absoluteBearing);
        double newY = getY() + e.getDistance() * Math.cos(absoluteBearing);
        long now = e.getTime();
        long interval = now - oldTime;

        double dx = newX - oldX;
        double dy = newY - oldY;
        double xSpeed = dx / interval;
        double ySpeed = dy / interval;

        double distance = e.getDistance();
        final double bulletSpeed = 20 - 3 * 0.1;
        double predictedX = newX + xSpeed * bulletSpeed;
        double predictedY = newY + ySpeed * bulletSpeed;
        System.out.println(predictedX + " " + predictedY);
        double theta = Utils.normalAbsoluteAngle(Math.atan2(
                predictedX - getX(), predictedY - getY()));

       /* setTurnRadarRightRadians(Utils.normalRelativeAngle(
                absoluteBearing - getRadarHeadingRadians()));*/
        setTurnGunRightRadians(Utils.normalRelativeAngle(
                theta - getGunHeadingRadians()));
        oldX = -1;
        oldY = -1;
        oldTime = -1;
        fire(0.1);
    }

}
