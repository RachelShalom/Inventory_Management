package com.example.rachel.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.rachel.inventory.R;
import com.example.rachel.inventory.data.InventoryContract.InventoryEntry;

/**
 * Created by Rachel on 30/09/2017.
 */

public class ProductProvider extends ContentProvider {
    private ProductDbHelper mProductDbHelper;
    private static final int PRODUCTS = 2;
    private static final int PRODUCT_ID = 1;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_PRODUCTS, PRODUCTS);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_PRODUCT_ID,PRODUCT_ID );
    }

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = ProductProvider.class.getSimpleName();

    @Override
    public boolean onCreate() {
        mProductDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mProductDbHelper.getReadableDatabase();
        // This cursor will hold the result of the query
        Cursor cursor;
        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                // For the PRODUCT ID code, extract out the ID from the URI
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the Products table with an ID to return a
                // Cursor containing that row of the table.
                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }
    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return InventoryEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
        public Uri insert(Uri uri, ContentValues contentValues) {
            final int match = sUriMatcher.match(uri);
            switch (match) {
                case PRODUCTS:
                    return insertProduct(uri, contentValues);
                default:
                    throw new IllegalArgumentException("Insertion is not supported for " + uri);
            }
        }

        /**
         * Insert a product into the database with the given content values. Return the new content URI
         * for that specific row in the database.
         */
        private Uri insertProduct(Uri uri,ContentValues values) {
            // Check that the name is not null
            if(values.containsKey(InventoryEntry.COLUMN_PRODUCT_NAME)) {
                String name = values.getAsString(InventoryEntry.COLUMN_PRODUCT_NAME);
                if (name == null) {
                    throw new IllegalArgumentException("Product requires a name");
                }
            }

            if(values.containsKey(InventoryEntry.COLUMN_PRODUCT_QUANTITY)){
                Integer quantity = values.getAsInteger(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
                if( quantity!= null && quantity<0){
                    throw new IllegalArgumentException("product requires valid wieght ");
                }

            }

            // Gets the data repository in write mode
            SQLiteDatabase db = mProductDbHelper.getWritableDatabase();
            long id = db.insert(InventoryEntry.TABLE_NAME, null, values);
            Log.v("Editor activity", "new row id "+id);
            if(id>0) {
                Toast.makeText(getContext(), R.string.products_saved, Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getContext(),R.string.products_not_saved, Toast.LENGTH_SHORT).show();
                Log.e(LOG_TAG, "Failed to insert row for " + uri);
                return null;
            }
            getContext().getContentResolver().notifyChange(uri,null);
            // Once we know the ID of the new row in the table,
            // return the new URI with the ID appended to the end of it
            return ContentUris.withAppendedId(uri, id);

        }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mProductDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                if(rowsDeleted!=0){
                    getContext().getContentResolver().notifyChange(uri,null);
                    return rowsDeleted;
                }
            case PRODUCT_ID:
                // Delete a single row given by the ID in the URI
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                if(rowsDeleted!=0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch(match){
            case PRODUCTS:
                return updateProduct(uri, values, selection, selectionArgs);
            case PRODUCT_ID:
                // For the PRODUCT_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateProduct(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);

        }
    }
    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the Product name key is present,
        // check that the name value is not null.
        if (values.containsKey(InventoryEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(InventoryEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }

        // If the Product weight key is present,
        // check that the weight value is valid.
        if (values.containsKey(InventoryEntry.COLUMN_PRODUCT_QUANTITY)) {
            // Check that the weight is greater than or equal to 0 kg
            Integer weight = values.getAsInteger(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            if (weight != null && weight < 0) {
                throw new IllegalArgumentException("Product requires valid weight");
            }
        }
        //check if the product price key is present
        //check that the price value is valid

        if(values.containsKey(InventoryEntry.COLUMN_PRODUCT_PRICE)){

        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        /// Gets the data repository in write mode
        SQLiteDatabase db = mProductDbHelper.getWritableDatabase();
        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = db.update(InventoryEntry.TABLE_NAME, values, selection, selectionArgs);
        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            Toast.makeText(getContext(), R.string.product_update, Toast.LENGTH_SHORT).show();
            getContext().getContentResolver().notifyChange(uri, null);
        }else{
            Toast.makeText(getContext(), R.string.products_update_failed, Toast.LENGTH_SHORT).show();
        }
        // Return the number of rows updated
        return rowsUpdated;

    }
}
