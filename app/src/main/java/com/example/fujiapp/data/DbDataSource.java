package com.example.fujiapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.fujiapp.MainActivity;

import java.util.LinkedList;

public class DbDataSource {

    public static final String LOG_TAG = DbDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private DbHelper dbHelper;

    public DbDataSource(Context context){
        dbHelper = new DbHelper(context);
    }

    public void openDB(){
        Log.d(LOG_TAG, "DB open");
        database = dbHelper.getWritableDatabase();
    }

    public void closeDB(){
        Log.d(LOG_TAG, "DB open");
        database.close();
    }

    //GET
    public boolean containsUsername(String username){
        Cursor cursor = database.query(dbHelper.TABLE_USER, null, dbHelper.COLUM_USER_USERNAME + " LIKE \"" + username + "\"",null, null, null, null);
        String existingUsername = "";
        while(cursor.moveToNext()){
            int userName = cursor.getColumnIndex(dbHelper.COLUM_USER_USERNAME);
            existingUsername = cursor.getString(userName);
        }
        return existingUsername.equals("") ? false: true;
    }

    public User getUserByID(long userId){
        User user = new User();
        Cursor cursor = database.query(dbHelper.TABLE_USER, null, dbHelper.COLUM_USER_ID + "=" + userId,null, null, null, null);
        while(cursor.moveToNext()){
            int id = cursor.getColumnIndex(dbHelper.COLUM_USER_ID);
            int name = cursor.getColumnIndex(dbHelper.COLUM_USER_NAME);
            int firstName = cursor.getColumnIndex(dbHelper.COLUM_USER_FIRSTNAME);
            int sex = cursor.getColumnIndex(dbHelper.COLUM_USER_SEX);
            int birthday = cursor.getColumnIndex(dbHelper.COLUM_USER_BIRTHDAY);
            int userName = cursor.getColumnIndex(dbHelper.COLUM_USER_USERNAME);
            int password = cursor.getColumnIndex(dbHelper.COLUM_USER_PASSWORD);
            int weight = cursor.getColumnIndex(dbHelper.COLUM_USER_WEIGHT);
            int height = cursor.getColumnIndex(dbHelper.COLUM_USER_HEIGHT);

            user.setId(cursor.getLong(id));
            user.setName(cursor.getString(name));
            user.setFirstName(cursor.getString(firstName));
            user.setSex(cursor.getString(sex));
            user.setDate(cursor.getString(birthday));
            user.setUserName(cursor.getString(userName));
            user.setPassword(cursor.getString(password));
            user.setWeight(cursor.getDouble(weight));
            user.setHeight(cursor.getDouble(height));
        }
        return user;
    }

    public User getUserByLogIn(String uName, String pass){
        User user = new User();
        Cursor cursor = database.query(dbHelper.TABLE_USER, null, dbHelper.COLUM_USER_USERNAME + " = '" + uName + "' and " + dbHelper.COLUM_USER_PASSWORD + " = '" + pass + "'",null, null, null, null);
        while(cursor.moveToNext()){
            int id = cursor.getColumnIndex(dbHelper.COLUM_USER_ID);
            int name = cursor.getColumnIndex(dbHelper.COLUM_USER_NAME);
            int firstName = cursor.getColumnIndex(dbHelper.COLUM_USER_FIRSTNAME);
            int sex = cursor.getColumnIndex(dbHelper.COLUM_USER_SEX);
            int birthday = cursor.getColumnIndex(dbHelper.COLUM_USER_BIRTHDAY);
            int userName = cursor.getColumnIndex(dbHelper.COLUM_USER_USERNAME);
            int password = cursor.getColumnIndex(dbHelper.COLUM_USER_PASSWORD);
            int weight = cursor.getColumnIndex(dbHelper.COLUM_USER_WEIGHT);
            int height = cursor.getColumnIndex(dbHelper.COLUM_USER_HEIGHT);

            user.setId(cursor.getLong(id));
            user.setName(cursor.getString(name));
            user.setFirstName(cursor.getString(firstName));
            user.setSex(cursor.getString(sex));
            user.setDate(cursor.getString(birthday));
            user.setUserName(cursor.getString(userName).toLowerCase());
            user.setPassword(cursor.getString(password));
            user.setWeight(cursor.getDouble(weight));
            user.setHeight(cursor.getDouble(height));
        }
        return user;
    }

    public LinkedList<TrainingSession> getAllTrainingSessionFromUser(User user){
        LinkedList<TrainingSession> list = new LinkedList<>();
        Cursor cursor = database.query(dbHelper.TABLE_TRAININGSESSION, null, dbHelper.COLUM_TRAININGSESSION_USERID + "=" + user.getId(),null, null, null, null);
        while(cursor.moveToNext()){
            TrainingSession ts = new TrainingSession();
            int id = cursor.getColumnIndex(dbHelper.COLUM_TRAININGSESSION_ID);
            int userId = cursor.getColumnIndex(dbHelper.COLUM_TRAININGSESSION_USERID);
            int taskName = cursor.getColumnIndex(dbHelper.COLUM_TRAININGSESSION_TASKNAME);
            int date = cursor.getColumnIndex(dbHelper.COLUM_TRAININGSESSION_DATE);
            int time = cursor.getColumnIndex(dbHelper.COLUM_TRAININGSESSION_TIME);
            int min = cursor.getColumnIndex(dbHelper.COLUM_TRAININGSESSION_MINPULSE);
            int max = cursor.getColumnIndex(dbHelper.COLUM_TRAININGSESSION_MAXPULSE);
            int av = cursor.getColumnIndex(dbHelper.COLUM_TRAININGSESSION_AVERAGEPULSE);
            int resultBackposition = cursor.getColumnIndex(dbHelper.COLUM_TRAININGSESSION_RESULTBACKPOSITION);
            int resultTime = cursor.getColumnIndex(dbHelper.COLUM_TRAININGSESSION_RESULTTIME);
            int success = cursor.getColumnIndex(dbHelper.COLUM_TRAININGSESSION_SUCCESS);
            int minResp = cursor.getColumnIndex(dbHelper.COLUM_TRAININGSESSION_MINRESPIRATION);
            int maxResp = cursor.getColumnIndex(dbHelper.COLUM_TRAININGSESSION_MAXRESPIRATION);
            int avResp = cursor.getColumnIndex(dbHelper.COLUM_TRAININGSESSION_AVERAGERESPIRATION);
            int pulse = cursor.getColumnIndex(dbHelper.COLUM_TRAININGSESSION_PULSE);
            int resp = cursor.getColumnIndex(dbHelper.COLUM_TRAININGSESSION_RESPIRATION);

            ts.setId(cursor.getLong(id));
            ts.setUserID(cursor.getLong(userId));
            ts.setTaskName((cursor.getString(taskName)));
            ts.setDate(cursor.getString(date));
            ts.setTime(cursor.getString(time));
            ts.setMinPulse(cursor.getString(min));
            ts.setMaxPulse(cursor.getString(max));
            ts.setAveragePulse(cursor.getString(av));
            ts.setResultBackposition(cursor.getString(resultBackposition));
            ts.setResultTime(cursor.getString(resultTime));
            ts.setSuccess(cursor.getInt(success) == 0 ? false : true);
            ts.setMinRespiration(cursor.getString(minResp));
            ts.setMaxRespiration(cursor.getString(maxResp));
            ts.setAverageRespiration(cursor.getString(avResp));
            ts.setPulse(cursor.getString(pulse));
            ts.setRespiration(cursor.getString(resp));
            list.add(ts);
        }
        return list;
    }



    //SET/ Insert
    public long insertTrainingSession(TrainingSession ts){
        ContentValues values = new ContentValues();
        values.put(dbHelper.COLUM_TRAININGSESSION_USERID, ts.getUserID());
        values.put(dbHelper.COLUM_TRAININGSESSION_TASKNAME, ts.getTaskName());
        values.put(dbHelper.COLUM_TRAININGSESSION_DATE, ts.getDate());
        values.put(dbHelper.COLUM_TRAININGSESSION_TIME, ts.getTime());
        values.put(dbHelper.COLUM_TRAININGSESSION_MAXPULSE, ts.getMaxPulse());
        values.put(dbHelper.COLUM_TRAININGSESSION_MINPULSE, ts.getMinPulse());
        values.put(dbHelper.COLUM_TRAININGSESSION_AVERAGEPULSE, ts.getAveragePulse());
        values.put(dbHelper.COLUM_TRAININGSESSION_RESULTBACKPOSITION, ts.getResultBackposition());
        values.put(dbHelper.COLUM_TRAININGSESSION_RESULTTIME, ts.getResultTime());
        values.put(dbHelper.COLUM_TRAININGSESSION_SUCCESS,(ts.isSuccess() ? 1 : 0));
        values.put(dbHelper.COLUM_TRAININGSESSION_AVERAGERESPIRATION,ts.getAverageRespiration());
        values.put(dbHelper.COLUM_TRAININGSESSION_MINRESPIRATION,ts.getMinRespiration());
        values.put(dbHelper.COLUM_TRAININGSESSION_MAXRESPIRATION,ts.getMaxRespiration());
        values.put(dbHelper.COLUM_TRAININGSESSION_PULSE,ts.getPulse());
        values.put(dbHelper.COLUM_TRAININGSESSION_RESPIRATION,ts.getRespiration());

        long inserdID = database.insert(dbHelper.TABLE_TRAININGSESSION, null, values);
        return inserdID;
    }

    public long insertUser(User user){
        ContentValues values = new ContentValues();
        values.put(dbHelper.COLUM_USER_NAME, user.getName());
        values.put(dbHelper.COLUM_USER_FIRSTNAME, user.getFirstName());
        values.put(dbHelper.COLUM_USER_SEX, user.getSex());
        values.put(dbHelper.COLUM_USER_BIRTHDAY, user.getDate());
        values.put(dbHelper.COLUM_USER_USERNAME, user.getUserName());
        values.put(dbHelper.COLUM_USER_WEIGHT, user.getWeight());
        values.put(dbHelper.COLUM_USER_HEIGHT, user.getHeight());
        values.put(dbHelper.COLUM_USER_PASSWORD, user.getPassword());

        long inserdID = database.insert(dbHelper.TABLE_USER, null, values);
        return inserdID;
    }

    //Update

    public long updateUser(User user){
        ContentValues values = new ContentValues();
        values.put(dbHelper.COLUM_USER_NAME, user.getName());
        values.put(dbHelper.COLUM_USER_FIRSTNAME, user.getFirstName());
        values.put(dbHelper.COLUM_USER_SEX, user.getSex());
        values.put(dbHelper.COLUM_USER_BIRTHDAY, user.getDate());
        //values.put(dbHelper.COLUM_USER_USERNAME, user.getUserName());
        values.put(dbHelper.COLUM_USER_WEIGHT, user.getWeight());
        values.put(dbHelper.COLUM_USER_HEIGHT, user.getHeight());
        values.put(dbHelper.COLUM_USER_PASSWORD, user.getPassword());
        long updateID = database.update(dbHelper.TABLE_USER,values,dbHelper.COLUM_USER_ID + " == " + MainActivity.activUser.getId(),null);
        return updateID;
    }

}
