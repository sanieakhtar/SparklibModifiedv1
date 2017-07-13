package de.uni_freiburg.informatik.es.cigtrack;

import android.content.ContentValues;
import android.content.Context;
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


    public UserData(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create table " + TABLE_NAME +" (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, BIRTHDAY TEXT, WEIGHT TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String name,String birthday,String weight){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,name);
        contentValues.put(COL_3,birthday);
        contentValues.put(COL_4,weight);
        long result =  db.insert(TABLE_NAME,null,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

}
