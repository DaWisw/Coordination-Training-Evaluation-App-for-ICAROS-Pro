package com.example.fujiapp.data;

import java.util.LinkedList;

public class TrainingSession {

    private long id;
    private long userID;
    private String date;
    private String time;
    private String maxPulse;
    private String minPulse;
    private String averagePulse;
    private String maxRespiration;
    private String minRespiration;
    private String averageRespiration;
    private String resultBackposition;
    private String resultTime;
    private boolean success;

    private String pulse;
    private String respiration;

    private String taskName;

    public TrainingSession(){

    }

    public TrainingSession(long userID, String date, String time, String maxPulse, String minPulse, String averagePulse,String maxRespiration, String minRespiration, String averageRespiration, String resultBackposition, String resultTime, boolean success) {
        this.userID = userID;
        this.date = date;
        this.time = time;
        this.maxPulse = maxPulse;
        this.minPulse = minPulse;
        this.averagePulse = averagePulse;
        this.maxRespiration = maxRespiration;
        this.minRespiration = minRespiration;
        this.averageRespiration = averageRespiration;
        this.resultBackposition = resultBackposition;
        this.resultTime = resultTime;
        this.success = success;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMaxPulse() {
        return maxPulse;
    }

    public void setMaxPulse(String maxPulse) {
        this.maxPulse = maxPulse;
    }

    public String getMinPulse() {
        return minPulse;
    }

    public void setMinPulse(String minPulse) {
        this.minPulse = minPulse;
    }

    public String getAveragePulse() {
        return averagePulse;
    }

    public void setAveragePulse(String averagePulse) {
        this.averagePulse = averagePulse;
    }

    public String getMaxRespiration() {
        return maxRespiration;
    }

    public void setMaxRespiration(String maxRespiration) {
        this.maxRespiration = maxRespiration;
    }

    public String getMinRespiration() {
        return minRespiration;
    }

    public void setMinRespiration(String minRespiration) {
        this.minRespiration = minRespiration;
    }

    public String getAverageRespiration() {
        return averageRespiration;
    }

    public void setAverageRespiration(String averageRespiration) { this.averageRespiration = averageRespiration; }

    public String getResultBackposition() {
        return resultBackposition;
    }

    public void setResultBackposition(String resultBackposition) {
        this.resultBackposition = resultBackposition;
    }

    public String getResultTime() {
        return resultTime;
    }

    public void setResultTime(String resultTime) {
        this.resultTime = resultTime;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getPulse() { return pulse; }

    public void setPulse(String pulse) {
        this.pulse = pulse;
    }

    public String getRespiration() { return respiration; }

    public void setRespiration(String respiration) {
        this.respiration = respiration;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
}
