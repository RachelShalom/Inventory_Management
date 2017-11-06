package com.example.rachel.inventory;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rachel.inventory.data.InventoryContract.InventoryEntry;

/**
 * Created by Rachel on 30/09/2017.
 */

public class ProductCursorAdapter extends CursorAdapter {


    /**
     * Constructs a new {@link ProductCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    int mPriceNumber = 0;

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the product data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current product can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     **/

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        // Find fields to populate in inflated template
        TextView ProductName = (TextView) view.findViewById(R.id.product_name);
        TextView ProductQuantity = (TextView) view.findViewById(R.id.product_quantity);
        TextView ProductQuantityUnit = (TextView) view.findViewById(R.id.weight_unit);
        ImageView ProductPic = (ImageView) view.findViewById(R.id.list_product_image);
        TextView Price = (TextView) view.findViewById(R.id.price_number);
        final Context finalContext = context;

        // Extract properties from cursor
        String name = cursor.getString(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRODUCT_NAME));
        ProductName.setText(name);

        int quantity = 0;
        quantity = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRODUCT_QUANTITY));
        //Update the quantity value
        Log.v("Product cursor adapter", "Old quantity is " + String.valueOf(quantity));
        // Populate fields with extracted properties
        ProductQuantity.setText(String.valueOf(quantity));
        //Get the item id which should be updated
        final int item_id = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryEntry._ID));
        String QuantityUnit = cursor.getString(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRODUCT_QUANTITY_UNIT));
        ProductQuantityUnit.setText(QuantityUnit);
        mPriceNumber = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRODUCT_PRICE));
        Price.setText(String.valueOf(mPriceNumber));
        byte[] image_blob = cursor.getBlob(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRODUCT_PIC));
        if (image_blob != null) {
            ProductPic.setImageBitmap(BitmapFactory.decodeByteArray(image_blob, 0, image_blob.length));
        }
        //find the button for incrementing the quantity
        Button plus = (Button) view.findViewById(R.id.plus_button);
        //find the button for decrementing the quantity
        Button minus = (Button) view.findViewById(R.id.minus_button);

        final int finalQuantity = quantity;
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Set up a content values object to hold the quantity when updated
                ContentValues incrementValue = new ContentValues();

                int newQuantity = finalQuantity + 1;
                Log.v("Product cursor adapter", "new  quantity is " + String.valueOf(newQuantity));
                incrementValue.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);
                //Selection claus which will point to the item_sold_id which will be updated
                String selection = InventoryEntry._ID + "=?";
                Log.v("Product cursor adapter", String.valueOf("ID is " + item_id));
                String[] selectionArgs = {String.valueOf(item_id)};
                int updated = finalContext.getContentResolver().update(InventoryEntry.CONTENT_URI, incrementValue, selection, selectionArgs);
                Log.v("Product cursor adapter", String.valueOf(updated));
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newQuantity;
                //Set up a content values object to hold the quantity when updated
                ContentValues incrementValue = new ContentValues();
                if (finalQuantity > 0) {
                    newQuantity = finalQuantity - 1;
                } else {
                    newQuantity = finalQuantity;
                }
                Log.v("Product cursor adapter", "new  quantity is " + String.valueOf(newQuantity));
                incrementValue.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);
                //Selection claus which will point to the item_sold_id which will be updated
                String selection = InventoryEntry._ID + "=?";
                Log.v("Product cursor adapter", String.valueOf("ID is " + item_id));
                String[] selectionArgs = {String.valueOf(item_id)};
                int updated = finalContext.getContentResolver().update(InventoryEntry.CONTENT_URI, incrementValue, selection, selectionArgs);
                Log.v("Product cursor adapter", String.valueOf(updated));
            }
        });


        // Populate fields with extracted properties
        ProductQuantity.setText(String.valueOf(quantity));
    }
}

