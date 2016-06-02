package cc.isotopestudio.Targeting;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Created by Mars on 5/25/2016.
 * Copyright ISOTOPE Studio
 */
public class Mars extends AdvancedRobot {

    private int count = 0;

    public void run() {
        setBodyColor(new Color(255, 81, 125));
        while (true) {
            turnGunRight(360);
            setBodyColor(new Color(255, 81, 125));
        }
    }

    private double oldEnemyHeading = -999;

    public void onScannedRobot(ScannedRobotEvent e) {
        count++;
        if (count == 5) {
            count = 0;
        }

        //
        if (oldEnemyHeading == -999) {
            oldEnemyHeading = e.getHeadingRadians();
            return;
        }

        double bulletPower = Math.min(3.0, getEnergy());
        double myX = getX();
        double myY = getY();
        double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
        double enemyX = getX() + e.getDistance() * Math.sin(absoluteBearing);
        double enemyY = getY() + e.getDistance() * Math.cos(absoluteBearing);
        double enemyHeading = e.getHeadingRadians();
        double enemyHeadingChange = enemyHeading - oldEnemyHeading;
        double enemyVelocity = e.getVelocity();
        oldEnemyHeading = enemyHeading;

        double deltaTime = 0;
        double battleFieldHeight = getBattleFieldHeight(),
                battleFieldWidth = getBattleFieldWidth();
        double predictedX = enemyX, predictedY = enemyY;
        while ((++deltaTime) * (20.0 - 3.0 * bulletPower) <
                Point2D.Double.distance(myX, myY, predictedX, predictedY)) {
            predictedX += Math.sin(enemyHeading) * enemyVelocity;
            predictedY += Math.cos(enemyHeading) * enemyVelocity;
            enemyHeading += enemyHeadingChange;

            if (predictedX < 18.0
                    || predictedY < 18.0
                    || predictedX > battleFieldWidth - 18.0
                    || predictedY > battleFieldHeight - 18.0) {

                predictedX = Math.min(Math.max(18.0, predictedX),
                        battleFieldWidth - 18.0);
                predictedY = Math.min(Math.max(18.0, predictedY),
                        battleFieldHeight - 18.0);
                break;
            }
        }
        double theta = Utils.normalAbsoluteAngle(Math.atan2(
                predictedX - getX(), predictedY - getY()));

        setTurnRadarRightRadians(Utils.normalRelativeAngle(
                absoluteBearing - getRadarHeadingRadians()));
        setTurnGunRightRadians(Utils.normalRelativeAngle(
                theta - getGunHeadingRadians()));
        if (getGunHeat() == 0)
            fireColor(3);

    }

    private void fireColor(int p) {
        switch (count) {
            case (0): {
                setBodyColor(new Color(255, 81, 125));
                break;
            }
            case (1): {
                setBodyColor(new Color(141, 255, 65));
                break;
            }
            case (2): {
                setBodyColor(new Color(94, 166, 255));
                break;
            }
            case (3): {
                setBodyColor(new Color(255, 237, 15));
                break;
            }
            case (4): {
                setBodyColor(new Color(200, 58, 255));
                break;
            }
        }
        fire(p);
    }

}
