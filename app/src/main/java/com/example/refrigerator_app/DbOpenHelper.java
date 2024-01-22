package com.example.refrigerator_app;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpenHelper {

    private static final String DATABASE_NAME = "foodmanager.db";   //저장할 DB이름
    private static final int DATABASE_VERSION = 1;      //db의 버전 버전이 올라갈대마다 onUpgrade가 호출
    public static String _TABLENAME0 = "cold_table"; //테이블 명칭
    public static String _TABLENAME1 = "frz_table";
    public static String _TABLENAME2 = "room_table";

    public SQLiteDatabase mDB;
    private DatabaseHelper mDBHelper;
    private Context mCtx;

    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        //DB 테이블 생성
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + _TABLENAME0 + "("
                    + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "name TEXT,"
                    + "ep TEXT,"
                    + "quantity TEXT,"
                    + "keep TEXT"
                    + ");");

            db.execSQL("CREATE TABLE IF NOT EXISTS " + _TABLENAME1 + "("
                    + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "name TEXT,"
                    + "ep TEXT,"
                    + "quantity TEXT,"
                    + "keep TEXT"
                    + ");");

            db.execSQL("CREATE TABLE IF NOT EXISTS " + _TABLENAME2 + "("
                    + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "name TEXT,"
                    + "ep TEXT,"
                    + "quantity TEXT,"
                    + "keep TEXT"
                    + ");");
        }

        //버전 업그레이드 시 사용, 이전 버전을 지우고 새 버전을 생성
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + _TABLENAME0);
            db.execSQL("DROP TABLE IF EXISTS " + _TABLENAME1);
            db.execSQL("DROP TABLE IF EXISTS " + _TABLENAME2);
            onCreate(db);
        }
    }

    public DbOpenHelper(Context context) {
        this.mCtx = context;
    }

    //해당 데이터베이스를 열어서 사용할 수 있도록함 / getWritableDatabase()는 데이터를 읽고 쓸 수 있도록 해줌
    public DbOpenHelper open() throws SQLException {
        mDBHelper = new DatabaseHelper(mCtx);
        mDB = mDBHelper.getWritableDatabase();
        return this;
    }

    public void create() {
        mDBHelper.onCreate(mDB);
    }

    //해당 데이터 닫기
    public void close(){
        mDB.close();
    }

    //테이블에 데이터를 삽입
    public void insertColumn(String name, String ep, String quantity, String keep) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("ep", ep);
        values.put("quantity", quantity);
        values.put("keep", keep);
        mDB.insert(_TABLENAME0, null, values);
    }

    public void insertColumnfrz(String name, String ep, String quantity, String keep) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("ep", ep);
        values.put("quantity", quantity);
        values.put("keep", keep);
        mDB.insert(_TABLENAME1, null, values);
    }

    public void insertColumnroom(String name, String ep, String quantity, String keep) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("ep", ep);
        values.put("quantity", quantity);
        values.put("keep", keep);
        mDB.insert(_TABLENAME2, null, values);
    }

    public Cursor selectColumns() {
        return mDB.query(_TABLENAME0, null, null, null, null, null, null);
    }

    public Cursor selectColumnsfrz() {
        return mDB.query(_TABLENAME1, null, null, null, null, null, null);
    }

    public Cursor selectColumnsroom() {
        return mDB.query(_TABLENAME2, null, null, null, null, null, null);
    }


    public void deleteColumn(String name) {
        String whereClause = "name = ?";
        String[] whereArgs = {name};
        mDB.delete(_TABLENAME0, whereClause, whereArgs);
    }

    public void deleteColumnfrz(String name) {
        String whereClause = "name = ?";
        String[] whereArgs = {name};
        mDB.delete(_TABLENAME1, whereClause, whereArgs);
    }

    public void deleteColumnroom(String name) {
        String whereClause = "name = ?";
        String[] whereArgs = {name};
        mDB.delete(_TABLENAME2, whereClause, whereArgs);
    }
    public void deleteAllDataFromTable(String tableName) {
        mDB.delete(tableName, null, null);
    }

    //    public Cursor sortColumn(String sort) {
//        Cursor c = mDB.rawQuery("SELECT * FROM " + _TABLENAME0 + " ORDER BY " + sort + ";", null);
//        return c;
//    }
    public Cursor sortColumn(String tableName, String sort) {
        String[] projection = { "name" }; // "name" 열만 가져오기 위한 프로젝션 배열 선언
        Cursor cursor = mDB.query(tableName, projection, null, null, null, null, null);

        int nameIndex = cursor.getColumnIndex("name"); // "name" 열의 인덱스 확인

        if (nameIndex == -1) {
            // "name" 열이 존재하지 않는 경우 처리할 코드 작성
            // 예를 들어, 오류 처리를 수행하거나 기본값을 반환할 수 있습니다.
        } else {
            // "name" 열이 존재하는 경우 정렬 수행
            cursor = mDB.rawQuery("SELECT * FROM " + tableName + " ORDER BY " + sort + ";", null);
        }

        return cursor;
    }


}