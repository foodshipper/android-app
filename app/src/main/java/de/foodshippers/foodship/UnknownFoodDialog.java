package de.foodshippers.foodship;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


public class UnknownFoodDialog extends DialogFragment {
    private static final String ARG_EAN = "unknownEan";
    private static final String TAG = "UnknownFoodDialog";
    private String unknownEan;
    private SendFoodTypeTask mTask = null;

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
            autoCompleteTextView.setAdapter(new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_dropdown_item_1line, new String[]{"tomato"}));
            autoCompleteTextView.setThreshold(0);
            return new AlertDialog.Builder(getActivity())
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

        }
        return null;
    }

    public void doPositiveClick(String type) {
        Log.d(TAG, "doPositiveClick: Add EAN with type " + type);
        mTask = new SendFoodTypeTask();
        mTask.sendFoodType(unknownEan, type);
    }

    public void doNegativeClick() {
        Log.d(TAG, "doPositiveClick: Cancelled");
    }

    private class SendFoodTypeTask {
        private RequestQueue queue;
        private Context mContext;

        void sendFoodType(final String ean, final String type) {
            mContext = getActivity().getApplicationContext();
            queue = Volley.newRequestQueue(mContext);
            Uri.Builder uriBuilder = new Uri.Builder();
            uriBuilder.scheme("http")
                    .authority("api.foodshipper.de")
                    .appendPath("v1")
                    .appendPath("product")
                    .appendPath(ean)
                    .appendQueryParameter("name", "Unknown")
                    .appendQueryParameter("type", type);

            StringRequest rq = new StringRequest(Request.Method.PUT, uriBuilder.build().toString(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    UnknownFoodDialog.this.onFoodTypeSent();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "onErrorResponse: " + error);
                    Toast.makeText(mContext, R.string.error_set_location, Toast.LENGTH_LONG).show();
                }
            });
            queue.add(rq);
            queue.start();
        }

        public void stop() {
            if (queue != null) {
                queue.stop();
            }
        }
    }

    private void onFoodTypeSent() {
        Log.d(TAG, "onFoodTypeSent: Type sent successfully");
    }
}
