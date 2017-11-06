package com.example.rachel.inventory;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.rachel.inventory.data.InventoryContract.InventoryEntry;
import com.example.rachel.inventory.data.ProductDbHelper;

import java.io.ByteArrayOutputStream;


public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = EditorActivity.class.getSimpleName();

    // get the product uri to understand if this is a new product or existing product that needs edit
    private Uri current_product_uri;
    private static final int URL_LOADER_ONE = 1;
    // listen if the user had chnged the editText
    private boolean mProductHasChanged = false;

    private ProductDbHelper mDbHelper;

    //EditText field to enter the product's name

    private EditText mNameEditText;

    /**
     * EditText field to enter the product's price
     */
    private EditText mPriceEditText;

    /**
     * EditText field to enter the product's quantity
     */
    private EditText mQuantityEditText;

    /**
     * EditText field to enter the product's supplier
     */
    private Spinner mSupplierSpinner;

    /**
     * EditText field to enter the product's quantity
     */
    private Spinner mQuantitySpinner;
    /**
     * Image view to edit the product picture
     */
    private ImageView mImage;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    Bitmap imageBitmap;
    byte[] mImageData;

    int mSupplier;
    String mQuantityUnit;
    ArrayAdapter mSupplierSpinnerAdapter;
    ArrayAdapter mQunatitySpinnerAdapter;
    Button mOrderMoreButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Intent intent = getIntent();
        current_product_uri = intent.getData();
        if (current_product_uri != null) {
            this.setTitle(R.string.edit_product);
        } else {
            this.setTitle(R.string.insert_product);
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a pet that hasn't been created yet.)
            invalidateOptionsMenu();
        }
        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_product_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_product_weight);
        mSupplierSpinner = (Spinner) findViewById(R.id.spinner_supplier);
        mImage = (ImageView)findViewById(R.id.editor_product_image);
        mOrderMoreButton= (Button) findViewById(R.id.button);
        // check if the user changed any if these fields
        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierSpinner.setOnTouchListener(mTouchListener);
        mImage.setOnTouchListener(mTouchListener);
        mDbHelper = new ProductDbHelper(this);
        setupSpinner();
        setupQuantitySpinnner();
        getSupportLoaderManager().initLoader(URL_LOADER_ONE, null, this);

        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        mOrderMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailIntent();
            }
        });
    }
  @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            mImage.setImageBitmap(imageBitmap);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
           imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            mImageData = baos.toByteArray();
        }
    }




    /**
     * Setup the Quantity dropdown spinner that allows the user to select the typ eof quantity Kg or liters etc).
     */
    private void setupQuantitySpinnner() {
        mQuantitySpinner = (Spinner) findViewById(R.id.spinner_quantity_units);
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        mQunatitySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_quantity_options, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        mQunatitySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mQuantitySpinner.setAdapter(mQunatitySpinnerAdapter);
        mQuantitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(InventoryEntry.KG)) {
                        mQuantityUnit = InventoryEntry.KG;
                    } else if (selection.equals(InventoryEntry.GRAM)) {
                        mQuantityUnit = InventoryEntry.GRAM;
                    } else if (selection.equals(InventoryEntry.MILLIGRAM)) {
                        mQuantityUnit = InventoryEntry.MILLIGRAM;
                    } else mQuantityUnit = InventoryEntry.LITER;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            mQuantityUnit= "Kg";
            }
        });
    }
//this method takes a pucture with the built in camera
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    //this method sends an email to the supplier  prompt by clicking the order more button in the editor activity

    private void sendEmailIntent(){
        Log.i("Send email", "");
        String[] TO = {""};
        String[] CC = {"rshalom1984@gmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Ordering "+mNameEditText.getText().toString());
        emailIntent.putExtra(Intent.EXTRA_TEXT, "I would like to order the following:");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }



    /**
     * Setup the dropdown spinner that allows the user to select the supplier of the product.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        mSupplierSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_supplier_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        mSupplierSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mSupplierSpinner.setAdapter(mSupplierSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mSupplierSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.supplier_1))) {
                        mSupplier = InventoryEntry.SUPPLIER_1; //
                    } else if (selection.equals(getString(R.string.supplier_2))) {
                        mSupplier = InventoryEntry.SUPPLIER_2; //
                    } else if(selection.equals(getString(R.string.supplier_3))){
                        mSupplier = InventoryEntry.SUPPLIER_3; //
                    }else mSupplier=InventoryEntry.SUPPLIER_UNKNOWN;
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSupplier = InventoryEntry.SUPPLIER_UNKNOWN; // Unknown
            }
        });
    }

    int QuantityInt = 0;
    int PriceInt = 0;

    //get user input from the editor and save new product into database
    private void saveProduct() {
        String nameString = mNameEditText.getText().toString().trim();
        String pricedString = mPriceEditText.getText().toString().trim();
        String QuantityString = mQuantityEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(QuantityString)) {
            //Change the weight and price from string to int type
            QuantityInt = Integer.parseInt(QuantityString);
            PriceInt = Integer.parseInt(pricedString);
        }
        //check if we the user didn't type the name or the price of the product, in this case the product will not be saved and the acitvity will be closed
        if (TextUtils.isEmpty(nameString) && TextUtils.isEmpty(pricedString) && TextUtils.isEmpty(QuantityString)&& mSupplier == InventoryEntry.SUPPLIER_UNKNOWN) {
            return;
        }
        Log.v(".EditorActivity"," typed: "+String.valueOf(mSupplier));
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, PriceInt);
        values.put(InventoryEntry.COLUMN_PRODUCT_SUPLLIER, mSupplier);
        values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY_UNIT,mQuantityUnit);
        values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, QuantityInt);
        if(mImageData!=null){
        values.put(InventoryEntry.COLUMN_PRODUCT_PIC, mImageData);}
        if (current_product_uri == null) {
            getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
        } else {
            getContentResolver().update(current_product_uri, values, null, null);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file_paths.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {

            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // save new pet into database
                saveProduct();
                //exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //check if changes to a pet were made
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    // a method that will create a dialog
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        // If the product hasn't changed, continue with handling back button press
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };
        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }
    // this method is called by @invalidateOptionsMenu when the user is in insert new product mode. this hides the delete option from the menue
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (current_product_uri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }


    // a dialog for the user when clicking the delete option in the editor menu
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    /**
     * Perform the deletion of the pet in the database.
     */
    private void deleteProduct() {
        // Only perform the delete if this is an existing pet.
        if (current_product_uri != null) {
            // Call the ContentResolver to delete the pet at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(current_product_uri, null, null);
            if (rowsDeleted != 0)
                Toast.makeText(this, R.string.editor_delete_pet_successful, Toast.LENGTH_SHORT).show();
            else {
                Toast.makeText(this, R.string.editor_delete_pet_failed, Toast.LENGTH_SHORT).show();
            }
        }
        // Close the activity
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] Projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_PRICE,InventoryEntry.COLUMN_PRODUCT_SUPLLIER,
                InventoryEntry.COLUMN_PRODUCT_QUANTITY,InventoryEntry.COLUMN_PRODUCT_QUANTITY_UNIT,InventoryEntry.COLUMN_PRODUCT_PIC};
        if (current_product_uri != null) {
            switch (id) {
                case URL_LOADER_ONE:
                    // Define a projection that specifies which columns from the database to query
                    return new CursorLoader(this, current_product_uri, Projection, null, null,null);
            }
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data!=null && data.moveToFirst()){
            String name = data.getString(data.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRODUCT_NAME));
            String price = data.getString(data.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRODUCT_PRICE));
            String quantity = data.getString(data.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRODUCT_QUANTITY));
            String quantity_unit = data.getString(data.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRODUCT_QUANTITY_UNIT));
            byte[] image_blob = data.getBlob(data.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRODUCT_PIC));
            int supllier = data.getInt(data.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRODUCT_SUPLLIER));
            Log.e("supllier is ", String.valueOf(supllier));
            mNameEditText.setText(name);
            mPriceEditText.setText(price);
            mQuantityEditText.setText(quantity);
            if (image_blob!=null) {
                mImage.setImageBitmap(BitmapFactory.decodeByteArray(image_blob, 0, image_blob.length));
            }else{Log.v(LOG_TAG,"image blob is null");}

            int spinnerposition;
            switch (supllier) {
                case InventoryEntry.SUPPLIER_1:
                    spinnerposition = mSupplierSpinnerAdapter.getPosition("Supplier 1");
                    mSupplierSpinner.setSelection(spinnerposition, false);
                    break;
                case InventoryEntry.SUPPLIER_2:
                    spinnerposition = mSupplierSpinnerAdapter.getPosition("Supplier 2");
                    mSupplierSpinner.setSelection(spinnerposition, false);
                    break;
                case InventoryEntry.SUPPLIER_3:
                    spinnerposition = mSupplierSpinnerAdapter.getPosition("Supplier 3");
                    mSupplierSpinner.setSelection(spinnerposition, false);
                    break;
                case InventoryEntry.SUPPLIER_UNKNOWN:
                    spinnerposition = mSupplierSpinnerAdapter.getPosition("Supplier Unknown");
                    mSupplierSpinner.setSelection(spinnerposition, false);
                    break;

            }
            int QuantitySppinerposition;
            switch (quantity_unit){
                case InventoryEntry.KG:
                    QuantitySppinerposition=mQunatitySpinnerAdapter.getPosition("Kg");
                    mQuantitySpinner.setSelection(QuantitySppinerposition,false);
                    break;
                case InventoryEntry.GRAM:
                    QuantitySppinerposition=mQunatitySpinnerAdapter.getPosition("grams");
                    mQuantitySpinner.setSelection(QuantitySppinerposition,false);
                    break;
                case InventoryEntry.LITER:
                    QuantitySppinerposition=mQunatitySpinnerAdapter.getPosition("Liters");
                    mQuantitySpinner.setSelection(QuantitySppinerposition,false);
                    break;
                case InventoryEntry.MILLIGRAM:
                    QuantitySppinerposition=mQunatitySpinnerAdapter.getPosition("Milligram");
                    mQuantitySpinner.setSelection(QuantitySppinerposition,false);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierSpinner.setSelection(0, false);
        mQuantitySpinner.setSelection(0,false);
    }
}
