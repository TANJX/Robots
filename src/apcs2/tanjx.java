package apcs2;

import robocode.AdvancedRobot;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;

import java.awt.*;

/**
 * Created by Mars on 6/1/2016.
 * Copyright ISOTOPE Studio
 */
public class tanjx extends AdvancedRobot {
    double X;
    double Y;
    private final static double border = 20;
    private boolean isFinished = false;

    @Override
    public void run() {
        X = getBattleFieldWidth();
        Y = getBattleFieldHeight();
        setAdjustRadarForRobotTurn(true);
        adjust();
        while (true) {
            if (getBackDistance() > getFowardDistance())
                go(getFowardDistance() - border);
            else
                go(-(getBackDistance() - border));
            isFinished = true;
            turnRight(90);
            turnRadarRight(360);
        }
    }

    public void go(double d) {
        ahead(d);
        setBodyColor(new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)));
    }

    private double getFowardDistance() {
        switch ((int) getHeading()) {
            case (0): {
                return Y - getY();
            }
            case (90): {
                return X - getX();
            }
            case (180): {
                return getY();
            }
            case (270): {
                return getX();
            }
        }
        return -1;
    }

    private double getBackDistance() {
        switch ((int) getHeading()) {
            case (180): {
                return Y - getY();
            }
            case (270): {
                return X - getX();
            }
            case (0): {
                return getY();
            }
            case (90): {
                return getX();
            }
        }
        return -1;
    }

    public void adjust() {
        if (getHeading() % 90 != 0) {
            turnRight(90 - getHeading() % 90);
        }
    }

    private byte scanDirection = 1;

    public void onScannedRobot(ScannedRobotEvent e) {
        scanDirection *= -1;
        // 在1和-1变换，代表着两个方向
        setTurnRadarRight(360 * scanDirection);
        adjust();
        if (isFinished)
            if (e.getDistance() < 150) {
                stop();
                boolean foward = getFowardDistance() > getBackDistance();
                double angle = e.getBearing();
                double a = getFowardDistance();
                double b = getBackDistance();
                if (angle < -90) {
                    if (getBackDistance() < 10) {
                        turnRight(90);
                        go(getFowardDistance() - 10);
                        return;
                    }
                    go(getFowardDistance() - border);
                } else if (angle < 0) {
                    if (getFowardDistance() < 10) {
                        turnLeft(90);
                        go(getBackDistance() - 10);
                        return;
                    }
                    go(-(getBackDistance() - border));
                } else if (angle < 90) {
                    if (getBackDistance() < 10) {
                        turnRight(90);
                        go(getFowardDistance() - 10);
                        return;
                    }
                    go(getFowardDistance() - border);
                } else {
                    if (getFowardDistance() < 10) {
                        turnLeft(90);
                        go(getBackDistance() - 10);
                        return;
                    }
                    go(-(getBackDistance() - border));
                }
            }
    }

    @Override
    public void onHitWall(HitWallEvent event) {
        stop();
    }

}
