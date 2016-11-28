package de.foodshippers.foodship;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import de.foodshippers.foodship.api.RestClient;
import de.foodshippers.foodship.api.service.ProductService;
import de.foodshippers.foodship.db.FoodshipContract;
import de.foodshippers.foodship.db.FoodshipDbHelper;
import retrofit2.Call;
import retrofit2.Callback;

import static de.foodshippers.foodship.db.FoodshipContract.ProductTypeTable.CN_NAME;


public class UnknownFoodDialog extends DialogFragment implements Callback {
    private static final String ARG_EAN = "unknownEan";
    private static final String TAG = "UnknownFoodDialog";
    private String unknownEan;
    Button mPositiveBtn = null;

    public static UnknownFoodDialog newInstance(String ean) {
        UnknownFoodDialog fragment = new UnknownFoodDialog();
        Bundle args = new Bundle();
        args.putString(ARG_EAN, ean);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
            unknownEan = getArguments().getString(ARG_EAN);
            LayoutInflater inflater = getActivity().getLayoutInflater();
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            View v = inflater.inflate(R.layout.dialog_unknown_food, null);
            final AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) v.findViewById(R.id.unknownFoodAutocompleteTextView);
            SQLiteDatabase db = new FoodshipDbHelper(getActivity().getApplicationContext()).getReadableDatabase();
            final FoodTypeFilterAdapter adapter = new FoodTypeFilterAdapter(getActivity().getApplicationContext(), db);

            autoCompleteTextView.setAdapter(adapter);
            autoCompleteTextView.setThreshold(0);
            final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                    .setView(v)
                    .setIcon(R.drawable.ic_local_grocery_store_black_24dp)
                    .setTitle(R.string.unknown_food_dialog_title)
                    .setPositiveButton(R.string.alert_dialog_ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    UnknownFoodDialog.this.doPositiveClick(autoCompleteTextView.getText().toString());
                                }
                            }
                    )
                    .setNegativeButton(R.string.alert_dialog_cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    UnknownFoodDialog.this.doNegativeClick();
                                }
                            }
                    )
                    .create();

            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    mPositiveBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    mPositiveBtn.setEnabled(false);
                }
            });
            autoCompleteTextView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (adapter.isKnownType(editable.toString())) {
                        if (mPositiveBtn != null) {
                            mPositiveBtn.setEnabled(true);
                        }
                    }
                }
            });
            return dialog;

        }
        return null;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
    }

    public void doPositiveClick(String type) {
        Log.d(TAG, "doPositiveClick: Add EAN with type " + type);
        ProductService productService = RestClient.getInstance().getProductService();
        Call<Void> pCall = productService.addProduct(unknownEan, null, type);
        pCall.enqueue(this);

    }

    public void doNegativeClick() {
        Log.d(TAG, "doPositiveClick: Cancelled");
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * Invoked for a received HTTP response.
     * <p>
     * Note: An HTTP response may still indicate an application-level failure such as a 404 or 500.
     * Call {@link retrofit2.Response#isSuccessful()} to determine if the response indicates success.
     *
     * @param call
     * @param response
     */
    @Override
    public void onResponse(Call call, retrofit2.Response response) {
        if (response.isSuccessful()) {
            Log.d(TAG, "onResponse: Added Product!");
        } else {
            Log.d(TAG, "onResponse: Could not add product: " + response.code());
        }
    }

    /**
     * Invoked when a network exception occurred talking to the server or when an unexpected
     * exception occurred creating the request or processing the response.
     *
     * @param call
     * @param t
     */
    @Override
    public void onFailure(Call call, Throwable t) {

    }

    private class FoodTypeFilterAdapter extends SimpleCursorAdapter {
        private SQLiteDatabase mDb;

        public FoodTypeFilterAdapter(Context context, SQLiteDatabase db) {
            super(context,
                    R.layout.dropdown_item,
                    null,
                    new String[]{CN_NAME},
                    new int[]{android.R.id.text1},
                    FLAG_REGISTER_CONTENT_OBSERVER);
            mDb = db;
        }

        @Override
        public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
            String filter = "";
            if (constraint == null) {
                filter = "";
            } else {
                filter = constraint.toString();
            }
            Cursor query = mDb.query(FoodshipContract.ProductTypeTable.TABLE_NAME,
                    new String[]{FoodshipContract.ProductTypeTable._ID, CN_NAME},
                    CN_NAME + " LIKE '%' || ? || '%' OR " + CN_NAME + " = ? ",
                    new String[]{filter, "undefined"},
                    null,
                    null,
                    null);
            this.setStringConversionColumn(query.getColumnIndex(CN_NAME));
            return query;
        }

        public boolean isKnownType(String type) {
            Cursor query = mDb.query(FoodshipContract.ProductTypeTable.TABLE_NAME,
                    new String[]{FoodshipContract.ProductTypeTable._ID, CN_NAME},
                    CN_NAME + " = ? ",
                    new String[]{type},
                    null,
                    null,
                    null);
            boolean result = query.moveToFirst();
            query.close();
            return result;
        }
    }

    private void onFoodTypeSent() {
        Log.d(TAG, "onFoodTypeSent: Type sent successfully");
    }
}
