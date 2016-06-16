package cc.isotopestudio.war.r1;

import robocode.ScannedRobotEvent;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * MyClass - a class by (your name here)
 */
public class TargetRobot {
    private String _name;
    private TargetData _currentTargetData;
    private ArrayList<TargetData> _targetHistory;


    public TargetRobot(String name) {
        _name = name;
        _targetHistory = new ArrayList<TargetData>();
    }

    public String getName() {
        return _name;
    }

    public boolean isUpdated(long currentTime) {
        return (currentTime - _currentTargetData.getTime() < 16);
    }

    public TargetData getCurrentTargetData() {
        return _currentTargetData;
    }

    public ArrayList<TargetData> getTargetHistory() {
        return _targetHistory;
    }

    public void recordScanEvent(ScannedRobotEvent scanEvent, Point2D.Double targetLocation) {
        _currentTargetData = new TargetData(scanEvent, targetLocation);
        _targetHistory.add(_currentTargetData);
    }
}
