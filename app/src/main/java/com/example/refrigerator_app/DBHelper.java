package com.example.refrigerator_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAme="Cart.db";
    private static final String DB_TABLE="Cart_Table";
    //colums
    private static final String ID="ID";
    private static final String NAME="NAME";
    public static String getName() {
        return NAME;
    }
    private static final String CREATE_TABLE="CREATE TABLE "+DB_TABLE+" ("+ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+NAME+" TEXT "+")";
    public DBHelper(Context context) {
        super(context, DB_NAme, null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ DB_TABLE);
        onCreate(sqLiteDatabase);
    }
    //create method to insert data
    public boolean insertData(String name){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(NAME,name);
        long result=db.insert(DB_TABLE,null,contentValues);
        return result!=-1; //데이터 안들어간거
    }
    //view data 함수 만들기
    public Cursor viewData(){
        SQLiteDatabase db= this.getReadableDatabase();
        String query="Select * from "+DB_TABLE;
        Cursor cursor=db.rawQuery(query,null);
        return cursor;
    }
    public void deleteData(String item) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + DB_TABLE + " WHERE " + NAME + " = ?";
        Log.d("DBHelper", "Delete Query: " + query);
        db.execSQL(query, new String[]{item});
        db.close();
    }
}