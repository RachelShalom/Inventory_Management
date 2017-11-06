package com.example.rachel.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.rachel.inventory.data.InventoryContract.InventoryEntry;
/**
 * Created by Rachel on 28/09/2017.
 */

public class ProductDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "inventory.db";
    private static final String SQL_CREATE_INVENTORY_TABLE="CREATE TABLE " +InventoryEntry.TABLE_NAME +" ( "+
            InventoryEntry._ID +" INTEGER PRIMARY KEY, " +
            InventoryEntry.COLUMN_PRODUCT_NAME +" TEXT NOT NULL, "+
            InventoryEntry.COLUMN_PRODUCT_QUANTITY +" INTEGER NOT NULL DEFAULT 0, "+
            InventoryEntry.COLUMN_PRODUCT_PRICE +" INTEGER NOT NULL, "+
            InventoryEntry.COLUMN_PRODUCT_SUPLLIER+" INTEGER)";
    private static final String SQL_CREATE_INVENTORY_TABLE_TWO="CREATE TABLE " +InventoryEntry.TABLE_NAME +" ( "+
            InventoryEntry._ID +" INTEGER PRIMARY KEY, " +
            InventoryEntry.COLUMN_PRODUCT_NAME +" TEXT NOT NULL, "+
            InventoryEntry.COLUMN_PRODUCT_QUANTITY +" INTEGER NOT NULL DEFAULT 0, "+
            InventoryEntry.COLUMN_PRODUCT_QUANTITY_UNIT+" TEXT, "+
            InventoryEntry.COLUMN_PRODUCT_PRICE +" INTEGER NOT NULL, "+
            InventoryEntry.COLUMN_PRODUCT_PIC +" BLOB, "+
            InventoryEntry.COLUMN_PRODUCT_SUPLLIER+" INTEGER)";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "+InventoryEntry.TABLE_NAME;



    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_INVENTORY_TABLE_TWO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
