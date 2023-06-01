package com.example.myapplication.SupportClasses;
//410211102598-ug99uocplb3m1f45q5na5p4ia1eaig02.apps.googleusercontent.com

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TokenCreator{

    static String access_token;
    static long express_in = 0;

    public interface ServerResponseCallback {
        void onSuccess(JSONObject jsonObject) throws JSONException;
        void onError(String error);
    }

    public interface DoSmthCallback {
        void UseToken(String token);
    }

    static String generateServerToken(Context context) {
        GoogleSignInAccount acc = GoogleSignIn.getLastSignedInAccount(context);
        if(acc != null) {
            return acc.getServerAuthCode();
        }
        return null;
    }

    public static void UseToken(Context context, GoogleSignInAccount acc, ServerResponseCallback callback){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://www.googleapis.com/oauth2/v4/token";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        callback.onSuccess(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.e(TAG, error.toString());
                    error.printStackTrace();
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("grant_type", "authorization_code");
                params.put("client_id", "410211102598-ug99uocplb3m1f45q5na5p4ia1eaig02.apps.googleusercontent.com");
                params.put("client_secret", "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");
                params.put("redirect_uri", "");
                params.put("code", generateServerToken(context));
                params.put("id_token", acc.getIdToken());
                return params;
            }
        };

        queue.add(stringRequest);
    }

    private static void useAccessToken(Context context, GoogleSignInAccount acc, DoSmthCallback callback) {
        UseToken(context, acc, new ServerResponseCallback() {
                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        access_token = jsonObject.optString("access_token");
                        getExpressTime(jsonObject.optInt("expires_in"));
                        callback.UseToken(access_token);
                    }

                    @Override
                    public void onError(String error) {

                    }
                }
        );
    }

    public static void UseAccessToken(Context context, GoogleSignInAccount acc, DoSmthCallback callback){
        if(isTimeOver() || access_token == null) {
            useAccessToken(context, acc, callback);
            return;
        }
        callback.UseToken(access_token);
    }

    private static void getExpressTime(int timeInSecond) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        express_in = currentTime + (timeInSecond * 1000L);
    }

    private static boolean isTimeOver() {
        return express_in <= Calendar.getInstance().getTimeInMillis();
    }
}