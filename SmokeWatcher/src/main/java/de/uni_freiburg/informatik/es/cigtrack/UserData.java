package de.uni_freiburg.informatik.es.cigtrack;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by elio on 7/13/17.
 */

public class UserData extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "user_profile.db";
    public static final String TABLE_NAME = "user_table";
    public static final String COL_1 =  "ID";
    public static final String COL_2 =  "NAME";
    public static final String COL_3 =  "BIRTHDAY";
    public static final String COL_4 =  "WEIGHT";
    public static final String COL_5 =  "PET_NAME";


    public UserData(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create table " + TABLE_NAME +" (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, BIRTHDAY TEXT, WEIGHT TEXT, PET_NAME TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String name,String birthday,String weight,String pet_name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,name);
        contentValues.put(COL_3,birthday);
        contentValues.put(COL_4,weight);
        contentValues.put(COL_5,pet_name);
        long result =  db.insert(TABLE_NAME,null,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor readData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from "+TABLE_NAME, null);
        return result;
    }

    public String readCOL_5Data(){
        String petname;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from "+TABLE_NAME, null);
        if(result.getCount()<1){
            result.close();
            petname = "Not Found";
            return petname;
        }
        result.moveToFirst();
        petname = result.getString(result.getColumnIndex(COL_5));
        result.close();
        return petname;
    }

    public boolean updateData(String id,String name,String birthday,String weight,String pet_name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,id);
        contentValues.put(COL_2,name);
        contentValues.put(COL_3,birthday);
        contentValues.put(COL_4,weight);
        contentValues.put(COL_5,pet_name);
        long result =  db.update(TABLE_NAME,contentValues, "ID = ?",new String[]{id});
        if(result == -1)
            return false;
        else
            return true;
    }
}
