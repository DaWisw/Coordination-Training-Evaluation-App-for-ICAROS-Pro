package com.example.fujiapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {

    private static final String  LOG_TAG = DbHelper.class.getSimpleName();

    public static final String DB_NAME = "Fuji2.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_USER = "user";
    public static final String TABLE_TRAININGSESSION = "trainingsession";

    public static final String COLUM_USER_ID = "id";
    public static final String COLUM_USER_NAME = "name";
    public static final String COLUM_USER_FIRSTNAME = "firstname";
    public static final String COLUM_USER_SEX = "sex";
    public static final String COLUM_USER_BIRTHDAY = "birthday";
    public static final String COLUM_USER_USERNAME = "username";
    public static final String COLUM_USER_PASSWORD = "password";
    public static final String COLUM_USER_HEIGHT = "height";
    public static final String COLUM_USER_WEIGHT = "weight";

    public static final String COLUM_TRAININGSESSION_ID = "id";
    public static final String COLUM_TRAININGSESSION_USERID = "userId";
    public static final String COLUM_TRAININGSESSION_DATE = "date";
    public static final String COLUM_TRAININGSESSION_TIME = "time";
    public static final String COLUM_TRAININGSESSION_MINPULSE = "minPulse";
    public static final String COLUM_TRAININGSESSION_MAXPULSE = "maxPulse";
    public static final String COLUM_TRAININGSESSION_AVERAGEPULSE = "averagePulse";
    public static final String COLUM_TRAININGSESSION_RESULTBACKPOSITION = "resultBackposition";
    public static final String COLUM_TRAININGSESSION_RESULTTIME = "resultTime";
    public static final String COLUM_TRAININGSESSION_SUCCESS = "success";
    public static final String COLUM_TRAININGSESSION_MINRESPIRATION = "minRespiration";
    public static final String COLUM_TRAININGSESSION_MAXRESPIRATION = "maxRespiration";
    public static final String COLUM_TRAININGSESSION_AVERAGERESPIRATION = "averageRespiration";
    public static final String COLUM_TRAININGSESSION_PULSE = "pulse";
    public static final String COLUM_TRAININGSESSION_RESPIRATION = "respiration";
    public static final String COLUM_TRAININGSESSION_TASKNAME = "taskName";
    public static final String SQL_CREATE_USER =
            "CREATE TABLE " + TABLE_USER +
                    "(" +   COLUM_USER_ID           + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                            COLUM_USER_NAME         + " TEXT NOT NULL," +
                            COLUM_USER_FIRSTNAME    + " TEXT NOT NULL," +
                            COLUM_USER_SEX          + " TEXT NOT NULL," +
                            COLUM_USER_BIRTHDAY     + " TEXT NOT NULL," +
                            COLUM_USER_USERNAME     + " TEXT NOT NULL," +
                            COLUM_USER_PASSWORD     + " TEXT NOT NULL," +
                            COLUM_USER_HEIGHT       + " DOUBLE NOT NULL," +
                            COLUM_USER_WEIGHT       + " DOUBLE NOT NULL);";

    public static final String SQL_CREATE_TRAININGSSESSION =
            "CREATE TABLE " + TABLE_TRAININGSESSION +
                    "(" +   COLUM_TRAININGSESSION_ID           + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                            COLUM_TRAININGSESSION_USERID       + " INTEGR NOT NULL," +
                            COLUM_TRAININGSESSION_TASKNAME     + " TEXT NOT NULL," +
                            COLUM_TRAININGSESSION_DATE         + " TEXT NOT NULL," +
                            COLUM_TRAININGSESSION_TIME         + " TEXT NOT NULL," +
                            COLUM_TRAININGSESSION_MAXPULSE     + " TEXT NOT NULL," +
                            COLUM_TRAININGSESSION_MINPULSE     + " TEXT NOT NULL," +
                            COLUM_TRAININGSESSION_AVERAGEPULSE + " TEXT NOT NULL," +
                            COLUM_TRAININGSESSION_RESULTBACKPOSITION       + " TEXT NOT NULL," +
                            COLUM_TRAININGSESSION_RESULTTIME       + " TEXT NOT NULL," +
                            COLUM_TRAININGSESSION_SUCCESS      + " INTEGR NOT NULL," +
                            COLUM_TRAININGSESSION_MINRESPIRATION        + " INTEGR NOT NULL," +
                            COLUM_TRAININGSESSION_MAXRESPIRATION        + " INTEGR NOT NULL," +
                            COLUM_TRAININGSESSION_AVERAGERESPIRATION    + " INTEGR NOT NULL," +
                            COLUM_TRAININGSESSION_PULSE + " TEXT," +
                            COLUM_TRAININGSESSION_RESPIRATION       + " TEXT," +
                            "FOREIGN KEY(" + COLUM_TRAININGSESSION_USERID + ") REFERENCES " + TABLE_USER + "(" + COLUM_USER_ID + "));";

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.d(LOG_TAG, "Datenbank wurde erstell:" + DB_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_USER);
        db.execSQL(SQL_CREATE_TRAININGSSESSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
