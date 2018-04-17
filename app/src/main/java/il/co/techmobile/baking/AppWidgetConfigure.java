package il.co.techmobile.baking;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

import il.co.techmobile.baking.modal.Baking;
import il.co.techmobile.baking.utilities.NetworkHelper;
import il.co.techmobile.baking.utilities.ParseJson;

public class AppWidgetConfigure extends AppCompatActivity {

    private static final String TAG = "baking_request";
    private static final String PREFS_NAME = "il.co.techmobile.baking.RecipeIngredientWidget";
    private static final String PREF_PREFIX_KEY = "widget";
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private Baking[] bakings;

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
    }


    public AppWidgetConfigure() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setResult(RESULT_CANCELED);

        setContentView(R.layout.activity_app_widget_configure);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Select a recipe");
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        String url =getString(R.string.baking_json_url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        bakings = new ParseJson(response).Parse();

                        List<String> recipes = new ArrayList<>();

                        for (Baking baking : bakings) {
                            recipes.add(baking.getName());
                        }


                        ArrayAdapter arrayAdapter = new ArrayAdapter<>(AppWidgetConfigure.this, android.R.layout.simple_list_item_1,android.R.id.text1, recipes);
                        ListView listView = findViewById(R.id.list_view_recipes);
                        listView.setAdapter(arrayAdapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @SuppressLint("ApplySharedPref")
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Context context = AppWidgetConfigure.this;
                                WidgetItem(position,context,mAppWidgetId);
                            }
                        });

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        boolean networkOk = NetworkHelper.hasNetworkAccess(this);
        if (networkOk) {
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
            stringRequest.setTag(TAG);

        } else {
            Toast.makeText(this, R.string.network_error_msg,Toast.LENGTH_LONG).show();
        }

    }

    private void WidgetItem(int position, Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME,0).edit();
        prefs.putInt(PREF_PREFIX_KEY + appWidgetId, position);
        prefs.apply();

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RecipeIngredientWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    static int getPosition(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getInt(PREF_PREFIX_KEY + appWidgetId, 0);
    }
}
