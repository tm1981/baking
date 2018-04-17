package il.co.techmobile.baking;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.util.DisplayMetrics;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import il.co.techmobile.baking.adapters.RecyclerViewAdapter;
import il.co.techmobile.baking.modal.Baking;
import il.co.techmobile.baking.utilities.NetworkHelper;
import il.co.techmobile.baking.utilities.ParseJson;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.ItemClickListener {

    private static final String TAG = "baking_request";
    private RequestQueue queue;

    private Baking[] bakings;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private ProgressBar progressBar;

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.rv_list);
        progressBar = findViewById(R.id.progressBar);
        TextView errorTV = findViewById(R.id.error_tv);

        progressBar.setVisibility(View.VISIBLE);
        errorTV.setVisibility(View.INVISIBLE);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN ); //set color for progressbar for api<21
        // Instantiate the RequestQueue.
        queue = Volley.newRequestQueue(this);

        //set number columns 2 or 4 depending of the device screen
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        //float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        GridLayoutManager gridLayoutManager;
        if (dpWidth > 480){
            //code for big screen (like tablet)
            gridLayoutManager = new GridLayoutManager(this, 2);

        }else{
            gridLayoutManager = new GridLayoutManager(this, 1);
        }

        recyclerView.setLayoutManager(gridLayoutManager);


        String url =getString(R.string.baking_json_url);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        bakings = new ParseJson(response).Parse();
                        adapter = new RecyclerViewAdapter(getApplicationContext(),bakings);
                        adapter.setClickListener(MainActivity.this);
                        recyclerView.setAdapter(adapter);
                        progressBar.setVisibility(View.INVISIBLE);
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

            //update the widget when opening the app
            Intent intent = new Intent(this, RecipeIngredientWidget.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
            // since it seems the onUpdate() is only fired on that:
            int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), RecipeIngredientWidget.class));
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
            sendBroadcast(intent);
        } else {
            Toast.makeText(this, R.string.network_error_msg,Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);
            errorTV.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (queue != null) {
            queue.cancelAll(TAG);
        }
    }

    @Override
    public void onItemClick(int position) {
        HandleItemClick(position,bakings);
    }

    private void HandleItemClick(int position, Baking[] bakings) {
        if (bakings != null ) {
            if (bakings.length > 0 ) {
                Baking baking = bakings[position];
                Intent intent = new Intent(MainActivity.this,DetailActivity.class);
                intent.putExtra("baking",baking);
                startActivity(intent);
            }

        }
    }
}
