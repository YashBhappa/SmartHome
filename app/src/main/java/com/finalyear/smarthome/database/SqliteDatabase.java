package com.finalyear.smarthome.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.finalyear.smarthome.models.Rooms;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Objects;

public class SqliteDatabase extends SQLiteOpenHelper {

    private	static final int DATABASE_VERSION =	5;
    private	static final String	DATABASE_NAME = "Rooms";
    private	static final String TABLE_ROOMS = "Room";// + Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()

    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "RoomName";


    public SqliteDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String	CREATE_ROOMS_TABLE = "CREATE TABLE " + TABLE_ROOMS + "(" + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_NAME + " TEXT" + ")";
        db.execSQL(CREATE_ROOMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROOMS);
        onCreate(db);
    }

    public ArrayList<Rooms> listRooms(){
        String sql = "select * from " + TABLE_ROOMS;
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Rooms> storeRooms = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst()){
            do{
                int id = Integer.parseInt(cursor.getString(0));
                String name = cursor.getString(1);
                storeRooms.add(new Rooms(id, name));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return storeRooms;
    }

    public void addRooms(Rooms rooms){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, rooms.getName());
        db.insert(TABLE_ROOMS, null, values);
    }

    public void updateRoom(Rooms rooms){
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, rooms.getName());
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_ROOMS, values, COLUMN_ID	+ "	= ?", new String[] { String.valueOf(rooms.getId())});
    }

    public void deleteRoom(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ROOMS, COLUMN_ID	+ "	= ?", new String[] { String.valueOf(id)});
    }
}
