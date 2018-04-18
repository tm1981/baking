package il.co.techmobile.baking;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeIngredientWidget extends AppWidgetProvider {

    private static RemoteViews views;



    static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager,
                                        final int appWidgetId) {

        String list = AppWidgetConfigure.loadList(context,appWidgetId);
        String quantity = AppWidgetConfigure.loadQuantityList(context,appWidgetId);
        String measure = AppWidgetConfigure.loadMeasureList(context,appWidgetId);

        views = new RemoteViews(context.getPackageName(), R.layout.recipe_ingredient_widget);

        ArrayList listArray;
        ArrayList quantityArray;
        ArrayList measureArray;

        Gson gson = new Gson();
        listArray = gson.fromJson(list,ArrayList.class);
        quantityArray = gson.fromJson(quantity,ArrayList.class);
        measureArray = gson.fromJson(measure,ArrayList.class);

        ArrayList<String> listStrings = new ArrayList<>();
        ArrayList<String> quantityStrings = new ArrayList<>();
        ArrayList<String> measureStrings = new ArrayList<>();

        if (listArray != null) {
            for (int i=0; i<listArray.size(); i++) {
                listStrings.add((String) listArray.get(i));
                quantityStrings.add((String) quantityArray.get(i));
                measureStrings.add((String) measureArray.get(i));
            }
        }

        Intent intent = new Intent(context, ListWidgetService.class);
        intent.putStringArrayListExtra("list",listStrings);
        intent.putStringArrayListExtra("quantity",quantityStrings);
        intent.putStringArrayListExtra("measure",measureStrings);
        intent.setData(Uri.fromParts("content",String.valueOf(appWidgetId),null));

        String widgetTitle = AppWidgetConfigure.loadTitlePref(context,appWidgetId);

        views.setRemoteAdapter(R.id.widget_list,intent);
        views.setTextViewText(R.id.widget_title,widgetTitle);

        appWidgetManager.updateAppWidget(appWidgetId, views);

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

    }



}

