package mrscottmcallister.com.simpleforeignexchange;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class DbHandler extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_PATH = "/data/data/mrscottmcallister.com.simpleforeignexchange/databases/";
    private static final String DATABASE_NAME = "country.db";
    public static final String TABLE_COUNTRIES = "country";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_CODE = "code";
    public static final String COLUMN_KEYWORDS = "keywords";
    private SQLiteDatabase myDataBase;
    private Context myContext;

    public DbHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        myContext = context;
    }

    public void createDataBase() throws IOException{

        boolean dbExist = checkDataBase();

        if(dbExist){
            //do nothing - database already exist
        }else{

            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {

                throw new Error("Error copying database");

            }
        }

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = DATABASE_PATH + DATABASE_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        }catch(SQLiteException e){

            //database does't exist yet.

        }

        if(checkDB != null){

            checkDB.close();

        }

        return checkDB != null ? true : false;
    }

    private void copyDataBase() throws IOException{

        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DATABASE_NAME);

        // Path to the just created empty db
        String outFileName = DATABASE_PATH + DATABASE_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException {

        //Open the database
        String myPath = DATABASE_PATH + DATABASE_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {

        if(myDataBase != null)
            myDataBase.close();

        super.close();

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

    public ArrayList<String> dbToArray(){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM "+TABLE_COUNTRIES+" ORDER BY name ASC;";
        Cursor c = db.rawQuery(query, null);
        ArrayList<String> results = new ArrayList();
        c.moveToFirst();
        int i = 0;
        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex("name")) != null){
                results.add(i, c.getString(c.getColumnIndex("name")));
                i++;
            }
            c.moveToNext();
        }
        return results;
    }

}
