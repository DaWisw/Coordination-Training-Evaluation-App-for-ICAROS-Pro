package com.example.fujiapp.connection.position;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class BackPositionData implements Parcelable {

    public static final Creator<BackPositionData> CREATOR = new Creator<BackPositionData>() {
        @Override
        public BackPositionData createFromParcel(Parcel in) {
            return new BackPositionData(in);
        }

        @Override
        public BackPositionData[] newArray(int size) {
            return new BackPositionData[size];
        }
    };


    private String tracker;

    private double x;
    private double y;
    private double z;

    private double qx;
    private double qy;
    private double qz;

    private double ex;
    private double ey;
    private double ez;

    public BackPositionData() {
    }

    public BackPositionData(Parcel parcel) {
        this.tracker = parcel.readString();
        this.x = parcel.readDouble();
        this.y = parcel.readDouble();
        this.z = parcel.readDouble();
        this.qx = parcel.readDouble();
        this.qy = parcel.readDouble();
        this.qz = parcel.readDouble();
        this.ex = parcel.readDouble();
        this.ey = parcel.readDouble();
        this.ez = parcel.readDouble();
    }

    public BackPositionData(String tracker, double x, double y, double z, double qx, double qy, double qz, double ex, double ey, double ez) {
        this.tracker = tracker;
        this.x = x;
        this.y = y;
        this.z = z;
        this.qx = qx;
        this.qy = qy;
        this.qz = qz;
        this.ex = ex;
        this.ey = ey;
        this.ez = ez;
    }

    public String getTracker() {
        return tracker;
    }

    public void setTracker(String tracker) {
        this.tracker = tracker;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getQx() {
        return qx;
    }

    public void setQx(double qx) {
        this.qx = qx;
    }

    public double getQy() {
        return qy;
    }

    public void setQy(double qy) {
        this.qy = qy;
    }

    public double getQz() {
        return qz;
    }

    public void setQz(double qz) {
        this.qz = qz;
    }

    public double getEx() {
        return ex;
    }

    public void setEx(double ex) {
        this.ex = ex;
    }

    public double getEy() {
        return ey;
    }

    public void setEy(double ey) {
        this.ey = ey;
    }

    public double getEz() {
        return ez;
    }

    public void setEz(double ez) {
        this.ez = ez;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BackPositionData that = (BackPositionData) o;
        return Double.compare(that.x, x) == 0 && Double.compare(that.y, y) == 0 && Double.compare(that.z, z) == 0 && Double.compare(that.qx, qx) == 0 && Double.compare(that.qy, qy) == 0 && Double.compare(that.qz, qz) == 0 && Double.compare(that.ex, ex) == 0 && Double.compare(that.ey, ey) == 0 && Double.compare(that.ez, ez) == 0 && Objects.equals(tracker, that.tracker);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tracker, x, y, z, qx, qy, qz, ex, ey, ez);
    }

    @Override
    public String toString() {
        return "BackPositionData{" +
                "tracker='" + tracker + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", qx=" + qx +
                ", qy=" + qy +
                ", qz=" + qz +
                ", ex=" + ex +
                ", ey=" + ey +
                ", ez=" + ez +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(tracker);
        parcel.writeDouble(x);
        parcel.writeDouble(y);
        parcel.writeDouble(z);
        parcel.writeDouble(qx);
        parcel.writeDouble(qy);
        parcel.writeDouble(qz);
        parcel.writeDouble(ex);
        parcel.writeDouble(ey);
        parcel.writeDouble(ez);
    }
}
