package de.foodshippers.foodship;

import android.content.Context;
import android.provider.Settings;

import java.util.LinkedList;
import java.util.List;

//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;

/**
 * Created by hannes on 06.11.16.
 */
@Deprecated
public class CommunicationManager {

    private final String android_id;
    private boolean Sendable = false;
    private List<String> PendingFooIds = new LinkedList<>();
    //    private RequestQueue queue;
    private String BaseURL = "http://api.foodshipper.de/v1/";
    private Context AppContext;


    public CommunicationManager(Context appcontext) {
        this.android_id = getUserId(appcontext);
//        queue = Volley.newRequestQueue(appcontext);
        this.AppContext = appcontext;
//        queue.start();
    }

    public void setSendable(boolean sendable) {
        Sendable = sendable;
        if (sendable) {
//            sendPending();
        }
    }

    public boolean isSendable() {
        return Sendable;
    }

    public boolean sendFood(String code) {
        if (isSendable()) {
//            queue.add(createStringRequest(code));
            return true;
        } else {
            PendingFooIds.add(code);
            System.out.println();
            return false;
        }

    }

//    public StringRequest createStringRequest(String foodID) {
//
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, BaseURL.concat("product/".concat(foodID)),
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Toast.makeText(AppContext, response, Toast.LENGTH_LONG).show();
//                    }
//                }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                if (error.networkResponse.statusCode == 404) {
//                    Toast.makeText(AppContext, "Produkt nicht gefunden.", Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(AppContext, "BIG Fehler" + error.getMessage(), Toast.LENGTH_LONG).show();
//                    System.out.println(error);
//                }
//            }
//        });
//        return stringRequest;
//    }
//
//    public boolean sendPending() {
//        Toast.makeText(AppContext, Integer.toString(PendingFooIds.size()), Toast.LENGTH_LONG).show();
//        for (String pendingFooId : PendingFooIds) {
//            queue.add(createStringRequest(pendingFooId));
//        }
//        return true;
//
//    }

    public static String getUserId(Context c) {
        return Settings.Secure.getString(c.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
