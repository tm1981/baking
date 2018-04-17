package il.co.techmobile.baking;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

import il.co.techmobile.baking.modal.Baking;
import il.co.techmobile.baking.modal.Ingredient;
import il.co.techmobile.baking.utilities.NetworkHelper;
import il.co.techmobile.baking.utilities.ParseJson;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeIngredientWidget extends AppWidgetProvider {

    private static final String TAG = "baking_request";
    private static Baking[] bakings;
    private static RequestQueue queue;
    static int position;



    static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager,
                                        final int appWidgetId) {



        position = AppWidgetConfigure.getPosition(context,appWidgetId);
        queue = Volley.newRequestQueue(context);
        String url =context.getString(R.string.baking_json_url);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        bakings = new ParseJson(response).Parse();

                        List<Ingredient> ingredients = bakings[position].getIngredients();
                        ArrayList<String> list = new ArrayList<>();
                        ArrayList<String> quantity = new ArrayList<>();
                        ArrayList<String> measure = new ArrayList<>();

                        for (Ingredient ingredient : ingredients) {
                            list.add(ingredient.getIngredient());
                            quantity.add(String.valueOf(ingredient.getQuantity()));
                            measure.add(ingredient.getMeasure());
                        }

                        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_ingredient_widget);

                        Intent intent = new Intent(context, ListWidgetService.class);
                        intent.putStringArrayListExtra("list",list);
                        intent.putStringArrayListExtra("quantity",quantity);
                        intent.putStringArrayListExtra("measure",measure);

                        Intent clickIntentTemplate = new Intent(context,MainActivity.class);
                        PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                                .addNextIntentWithParentStack(clickIntentTemplate)
                                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                        views.setPendingIntentTemplate(R.id.widget_list, clickPendingIntentTemplate);

                        views.setRemoteAdapter(R.id.widget_list,intent);
                        String widgetTitle = "Ingredients for " + bakings[position].getName();
                        views.setTextViewText(R.id.widget_title,widgetTitle);
                        views.setViewVisibility(R.id.text_view_no_connection, View.GONE);
                        views.setViewVisibility(R.id.widget_list,View.VISIBLE);
                        appWidgetManager.updateAppWidget(appWidgetId, views);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        boolean networkOk = NetworkHelper.hasNetworkAccess(context);
        if (networkOk) {
            queue.add(stringRequest);
            stringRequest.setTag(TAG);
        } else {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_ingredient_widget);
            views.setViewVisibility(R.id.text_view_no_connection, View.VISIBLE);
            views.setViewVisibility(R.id.widget_list,View.GONE);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);

        }


    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        if (queue != null) {
            queue.cancelAll(TAG);
        }
    }

}

