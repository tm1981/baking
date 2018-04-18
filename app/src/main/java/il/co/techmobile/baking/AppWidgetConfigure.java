package il.co.techmobile.baking;

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
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import il.co.techmobile.baking.modal.Baking;
import il.co.techmobile.baking.modal.Ingredient;
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
                        listView.setOnItemClickListener(onItemClickListener);

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
    }


    private final AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Context context = AppWidgetConfigure.this;
            List<Ingredient> ingredients = bakings[position].getIngredients();

            ArrayList<String> list1 = new ArrayList<>();
            ArrayList<String> quantity1 = new ArrayList<>();
            ArrayList<String> measure1 = new ArrayList<>();

            String title = "Ingredients for " + bakings[position].getName();

            for (Ingredient ingredient : ingredients) {
                list1.add(ingredient.getIngredient());
                quantity1.add(String.valueOf(ingredient.getQuantity()));
                measure1.add(ingredient.getMeasure());
            }

            String listJson = new Gson().toJson(list1);
            String quantityJson = new Gson().toJson(quantity1);
            String measureJson = new Gson().toJson(measure1);


            saveData(context,mAppWidgetId,title,listJson,quantityJson,measureJson);


            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RecipeIngredientWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();

        }
    };

    static String loadList(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String list = prefs.getString("list" + appWidgetId, null);
        if (list != null) {
            return list;
        } else {
            return null;
        }
    }

    static String loadQuantityList(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String list = prefs.getString("quantity" + appWidgetId, null);
        if (list != null) {
            return list;
        } else {
            return null;
        }
    }

    static String loadMeasureList(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String list = prefs.getString("measure" + appWidgetId, null);
        if (list != null) {
            return list;
        } else {
            return null;
        }
    }

    static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return null;
        }
    }

    private static void saveData(Context context, int appWidgetId, String title, String list, String quantity, String measure) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, title);
        prefs.putString("list" + appWidgetId,list);
        prefs.putString("quantity" + appWidgetId,quantity);
        prefs.putString("measure" + appWidgetId,measure);

        prefs.apply();
    }

}
