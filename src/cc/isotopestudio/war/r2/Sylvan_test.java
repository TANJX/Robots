package cc.isotopestudio.war.r2;

/**
 * Created by Mars on 6/17/2016.
 * Copyright ISOTOPE Studio
 */

import robocode.*;
import robocode.util.Utils;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

public class Sylvan_test extends AdvancedRobot {

    //initial
    private static Boolean hithithit = false;
    private final enemyState enemy = new enemyState();
    //pattern match
    private static final int MAX_PATTERN_LENGTH = 30;
    private static final Map<String, int[]> matcher = new HashMap<>(40000);
    private static String enemyHistory;
    //predict
    private static double FIRE_POWER = 3;
    private static double FIRE_SPEED = Rules.getBulletSpeed(FIRE_POWER);
    //move
    private static final double BASE_MOVEMENT = 180;
    private static double movement;

    private static int fireCount = 0;

    public void run() {
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setBodyColor(Color.BLACK);
        setGunColor(Color.BLACK);
        setRadarColor(Color.BLACK);
        setScanColor(Color.BLACK);
        fireCount = 0;
        enemyHistory = "";
        movement = Double.POSITIVE_INFINITY;
        setTurnRadarRight(400);
        while (true) {
            scan();
            if (getDistanceRemaining() == 0) {
                ahead(50);
                turnRight(60);
                hithithit = false;
            }
        }
    }

    ////////////////////////////**EVENT**/////////////////////////////////////
    public void onHitWall(HitWallEvent e) {
        if (Math.abs(movement) > BASE_MOVEMENT) {
            movement = BASE_MOVEMENT;
        }
    }

    public void onRobotDeath(RobotDeathEvent e) {
        setTurnRadarRight(400);
    }

    public void onHitByBullet(HitByBulletEvent e) {
        setTurnRadarRight(400);
    }

    public void onHitRobot(HitRobotEvent e) {
        if (!hithithit) {
            double absoluteBearing = e.getBearingRadians() + getHeadingRadians();
            turnRadarRightRadians(Utils.normalRelativeAngle(absoluteBearing - getRadarHeadingRadians()));
            hithithit = true;
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        //updata
        enemy.update(e, this);
        //fire
        if (getGunHeat() == 0 && getGunTurnRemaining() == 0 && getEnergy() > 1) {
            smartFire();
        }
        //track
        trackHim();
        // memorize.
        if (enemy.thisStep == (char) -1) {
            return;
        }
        record(enemy.thisStep);
        enemyHistory = (char) enemy.thisStep + enemyHistory;
        // aim
        Point2D.Double myP = new Point2D.Double(getX(), getY());
        Point2D.Double enemyP = project(myP, enemy.absoluteBearing, e.getDistance());
        String pattern = enemyHistory;
        for (double d = 0; d < myP.distance(enemyP); d += FIRE_SPEED) {
            int nextStep = predict(pattern);
            enemy.decode(nextStep);
            enemyP = project(enemyP, enemy.headingRadian, enemy.velocity);
            pattern = (char) nextStep + pattern;
        }

        enemy.absoluteBearing = Math.atan2(enemyP.x - myP.x, enemyP.y - myP.y);
        double gunTurn = enemy.absoluteBearing - getGunHeadingRadians();
        setTurnGunRightRadians(Utils.normalRelativeAngle(gunTurn));
    }
    ////////////////////////////////**MYFUNCTION**/////////////////////////////


    private void smartFire() {
        if (fireCount >= 10) {
            return;
        }
        FIRE_POWER = Math.min(Math.min(getEnergy() / 6d, 1000d / enemy.distance), enemy.energy / 3d);
        FIRE_SPEED = Rules.getBulletSpeed(FIRE_POWER);
        fire(FIRE_POWER);
        fireCount++;
    }

    private void trackHim() {
        double RadarOffset;
        RadarOffset = Utils.normalRelativeAngle(enemy.absoluteBearing - getRadarHeadingRadians());
        setTurnRadarRightRadians(RadarOffset * 1.2);
    }

    private void record(int thisStep) {
        int maxLength = Math.min(MAX_PATTERN_LENGTH, enemyHistory.length());
        for (int i = 0; i <= maxLength; ++i) {
            String pattern = enemyHistory.substring(0, i);
            int[] frequencies = matcher.get(pattern);
            if (frequencies == null) {
                // frequency tables need to hold 21 possible dh values times 17 possible v values
                frequencies = new int[21 * 17];
                matcher.put(pattern, frequencies);
            }
            ++frequencies[thisStep];
        }

    }

    private int predict(String pattern) {
        int[] frequencies = null;
        for (int patternLength = Math.min(pattern.length(), MAX_PATTERN_LENGTH); frequencies == null; --patternLength) {
            frequencies = matcher.get(pattern.substring(0, patternLength));
        }
        int nextTick = 0;
        for (int i = 1; i < frequencies.length; ++i) {
            if (frequencies[nextTick] < frequencies[i]) {
                nextTick = i;
            }
        }
        return nextTick;
    }

    private static Point2D.Double project(Point2D.Double p, double angle,
                                          double distance) {
        double x = p.x + distance * Math.sin(angle);
        double y = p.y + distance * Math.cos(angle);
        return new Point2D.Double(x, y);
    }
}

//////////////////////**ENEMY_CLASS**///////////////////////////////////////

class enemyState {

    double headingRadian = 0.0D;
    double distance = 0.0D;
    double absoluteBearing = 0.0D;
    double velocity = 0.0D;
    double energy = 100.0D;
    //addition
    private double lastEnemyHeading = 0;
    int thisStep = 0;

    //the currently data is important, we should get it when we use it.
    void update(ScannedRobotEvent e, AdvancedRobot me) {
        headingRadian = e.getHeadingRadians();
        double bearingRadian = e.getBearingRadians();
        distance = e.getDistance();
        absoluteBearing = bearingRadian + me.getHeadingRadians();
        velocity = e.getVelocity();
        energy = e.getEnergy();
        //addition
        thisStep = encode(headingRadian - lastEnemyHeading, velocity);
        lastEnemyHeading = headingRadian;
    }

    private static int encode(double dh, double v) {
        if (Math.abs(dh) > Rules.MAX_TURN_RATE_RADIANS) {
            return (char) -1;
        }
        //È¡Õý
        //-10<toDegrees(dh)<10 ; -8<v<8 ;
        //so we add with 10 and 8
        int dhCode = (int) Math.rint(Math.toDegrees(dh)) + 10;
        int vCode = (int) Math.rint(v + 8);
        return (char) (17 * dhCode + vCode);
    }

    void decode(int symbol) {
        headingRadian += Math.toRadians(symbol / 17 - 10);
        velocity = symbol % 17 - 8;
    }
}