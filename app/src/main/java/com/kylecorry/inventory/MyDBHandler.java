package com.kylecorry.inventory;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

/**
 * Created by kyle on 6/24/15.
 */
public class MyDBHandler extends SQLiteOpenHelper {

    // database information
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "inventory.db";
    public static final String TABLE_INVENTORY = "inventory";
    public static final String COLUMN_ID = "id";
    public static final String ITEM_NAME = "itemName";
    public static final String ITEM_AMOUNT = "itemAmount";
    Context c;
    SharedPreferences preferences;

    public MyDBHandler(Context context, SQLiteDatabase.CursorFactory factory) {
        // required by sqlite
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        // get the base context of the app
        c = context;
        // get the user preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(c);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create a table with the needed columns defined above
        String query = "CREATE TABLE " + TABLE_INVENTORY + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ITEM_NAME + " TEXT, " +
                ITEM_AMOUNT + " INTEGER " +
                ");";
        // run the query
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // only needed if column names are changed -- won't be used
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVENTORY);
        onCreate(db);
    }

    public void addItem(String name, int amount) {
        // map the column name to the value
        ContentValues values = new ContentValues();
        values.put(ITEM_NAME, name);
        values.put(ITEM_AMOUNT, amount);
        // get a database to write
        SQLiteDatabase db = getWritableDatabase();
        // insert the values into the database and close it
        db.insert(TABLE_INVENTORY, null, values);
        db.close();
    }

    public void deleteItem(String name) {
        // get a database to write
        SQLiteDatabase db = getWritableDatabase();
        // delete the item from the database and close it
        db.delete(TABLE_INVENTORY, ITEM_NAME + "=\"" + name + "\";", null);
        db.close();
    }

    public void updateItem(String name, int amount) {
        // map the column name to amount
        ContentValues values = new ContentValues();
        // if item is out...
        if (amount <= 0) {
            // store it as 0 (instead of negative)
            amount = 0;
            // email if out
            Intent email = new Intent(Intent.ACTION_SEND);
            // to
            email.putExtra(Intent.EXTRA_EMAIL, new String[]{preferences.getString("email", Settings.MAIN_EMAIL)});
            // subject
            email.putExtra(Intent.EXTRA_SUBJECT, "Inventory item " + name.toLowerCase() + " out of stock");
            // message
            email.putExtra(Intent.EXTRA_TEXT, "Sent from Inventory app because " + name.toLowerCase() + " is out of stock");
            email.setType("plain/text");
            // bring up email chooser dialog
            c.startActivity(Intent.createChooser(email, "Email using..."));
        }
        // map amount to its column
        values.put(ITEM_AMOUNT, amount);
        // get a database to write
        SQLiteDatabase db = getWritableDatabase();
        // update the item's amount and close db
        db.update(TABLE_INVENTORY, values, ITEM_NAME + "=\"" + name + "\";", null);
        db.close();
    }

    // gives me a string array to display
    public String[] dbToString() {
        // get a database to read
        SQLiteDatabase db = getReadableDatabase();
        // get all the items from the database
        String query = "SELECT * FROM " + TABLE_INVENTORY + " WHERE 1";
        // loop through the items
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        // array of strings that is the size of the number of items in the db
        String[] items = new String[c.getCount()];
        int i = 0;
        // while there are items left
        while (!c.isAfterLast()) {
            // if there is an item here
            if (c.getString(c.getColumnIndex(ITEM_NAME)) != null) {
                // get its name and amount and add it to the array
                items[i] = c.getString(c.getColumnIndex(ITEM_NAME)) + "     --     " + c.getInt(c.getColumnIndex(ITEM_AMOUNT));
                // move the array index forward
                i++;
            }
            // move to the next item
            c.moveToNext();
        }
        // close the database
        db.close();
        // items is the array of strings for the items
        return items;
    }
}
