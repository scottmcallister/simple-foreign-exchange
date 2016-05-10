package mrscottmcallister.com.simpleforeignexchange;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHandler extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "country.db";
    public static final String TABLE_COUNTRIES = "countries";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_CODE = "code";
    public static final String COLUMN_KEYWORDS = "keywords";

    public DbHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE IF NOT EXISTS " + TABLE_COUNTRIES + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_CODE + " TEXT, " +
                COLUMN_KEYWORDS + "TEXT ) ;";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_COUNTRIES + ";");
        onCreate(db);
    }

    // add country to db
    public void addCountry(Country country){
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, country.get_name());
        values.put(COLUMN_CODE, country.get_code());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_COUNTRIES, null, values);
        db.close();
    }

    // delete country from db
    public void deleteCountry(String nameToDelete){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM "+TABLE_COUNTRIES+" WHERE "+COLUMN_NAME+"=\""+nameToDelete+"\"");
    }

    // print out db as a string
    public String dbToString(){
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM "+TABLE_COUNTRIES+";";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex("name")) != null){
                dbString += c.getString(c.getColumnIndex("name"));
                dbString += "\n";
            }
            c.moveToNext();
        }
        db.close();
        return dbString;
    }

}
