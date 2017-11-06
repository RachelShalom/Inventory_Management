package com.example.rachel.inventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Rachel on 28/09/2017.
 */

public class InventoryContract {

    public static final String CONTENT_AUTHORITY="com.example.android.inventory";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODUCTS= "products";
    public static final String PATH_PRODUCT_ID ="products/#";


    private InventoryContract() {
    }

     /* Inner class that defines the table contents */

    public static abstract class InventoryEntry implements BaseColumns {
        //name of the table
        public static final String TABLE_NAME = "products";
        //name of the columns
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "name";
        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
        public static final String COLUMN_PRODUCT_QUANTITY_UNIT = "quantityUnit";
        public static final String COLUMN_PRODUCT_SUPLLIER = "supllier";
        public static final String COLUMN_PRODUCT_PRICE = "price";
        public static final String COLUMN_PRODUCT_PIC ="pics";
        //possible values for supllier column
        public static final int SUPPLIER_1 = 1;
        public static final int SUPPLIER_2 = 2;
        public static final int SUPPLIER_3 = 3;
        public static final int SUPPLIER_UNKNOWN = 0;
        //possible values for QUANTITY_UNIT column
        public static final String KG = "Kg";
        public static final String LITER ="Liters";
        public static final String MILLILITER ="Milliliter";
        public static final String MILLIGRAM ="Milligram";
        public static final String GRAM ="grams";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);
        /**
         * The MIME type  for a list of product.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;
        /**
         * The MIME type  for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;


    }
}