package com.example.rachel.inventory;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.rachel.inventory.data.InventoryContract.InventoryEntry;
import com.example.rachel.inventory.data.ProductDbHelper;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int URL_LOADER = 0;
    private ProductDbHelper mDbHelper;
    // Define a projection that specifies which columns from the database
    String[] mProjection = {
            InventoryEntry._ID,
            InventoryEntry.COLUMN_PRODUCT_NAME,
            InventoryEntry.COLUMN_PRODUCT_PRICE, InventoryEntry.COLUMN_PRODUCT_SUPLLIER,
            InventoryEntry.COLUMN_PRODUCT_QUANTITY,InventoryEntry.COLUMN_PRODUCT_QUANTITY_UNIT, InventoryEntry.COLUMN_PRODUCT_PIC};
    ProductCursorAdapter mProductAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = new ProductDbHelper(this);
        // Find the ListView which will be populated with the product data
        ListView productList = (ListView) findViewById(R.id.list);
      //  View emptyView = findViewById(R.id.empty_view);
      //  productList.setEmptyView(emptyView);
        mProductAdapter = new ProductCursorAdapter(this, null);
        // Attach cursor adapter to the ListView
        productList.setAdapter(mProductAdapter);
        // open en edit mode for each item in the EditorActivity
        productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                Uri Item_uri = Uri.withAppendedPath(InventoryEntry.CONTENT_URI, String.valueOf(id));
                Log.e("URI is ", String.valueOf(Item_uri));
                //set the item uri to the intent
                intent.setData(Item_uri);
                startActivity(intent);

            }
        });
         /*
         * Initializes the CursorLoader. The URL_LOADER value is eventually passed
         * to onCreateLoader().
         */
        getSupportLoaderManager().initLoader(URL_LOADER, null,this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file_paths.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertProduct();
                return true;
            case R.id.action_delete_all_entries:
                int rowsDeleted = getContentResolver().delete(InventoryEntry.CONTENT_URI,null,null);
                Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
     */
    private void insertProduct() {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME,"Oil");
        values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, "10");
        values.put(InventoryEntry.COLUMN_PRODUCT_SUPLLIER,"1");
        values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, "20");
        values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY_UNIT, "Liters");
        // Insert the new row, returning the primary key value of the new row
        Uri new_dummy_product = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

    }




    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case URL_LOADER:
                // Define a projection that specifies which columns from the database to query
                return new CursorLoader(this, InventoryEntry.CONTENT_URI, mProjection, null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //the data- cursor is passed in to the adapter. this swaps the older cursor with a new one
        mProductAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
     /*
     * Clears out the adapter's reference to the Cursor.
     * This prevents memory leaks.
     */
        mProductAdapter.swapCursor(null);
    }
}
