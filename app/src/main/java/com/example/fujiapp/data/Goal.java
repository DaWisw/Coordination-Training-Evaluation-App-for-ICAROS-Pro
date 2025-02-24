package com.example.fujiapp.data;

public class Goal {
    // private double goalX;
    private double goalY;
    private double goalZ;
    private double duration;

    public Goal(double goalY, double goalZ, double duration) {
        // this.goalX = goalX;
        this.goalY = goalY;
        this.goalZ = goalZ;
        this.duration = duration;
    }

    //public double getGoalX() {return goalX;}

    //public void setGoalX(double goalX) { this.goalX = goalX;}

    public double getGoalY() {
        return goalY;
    }

    public void setGoalY(double goalY) {
        this.goalY = goalY;
    }

    public double getGoalZ() {
        return goalZ;
    }

    public void setGoalZ(double goalZ) {
        this.goalZ = goalZ;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }
}
