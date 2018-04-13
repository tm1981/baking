package il.co.techmobile.baking;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

class ListRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {

    private final Context mContext;
    private List<String> list = new ArrayList<>();
    private final Intent intent;

    public ListRemoteViewFactory(Context applicationContext,Intent intent) {
        this.mContext = applicationContext;
        this.intent = intent;

    }

    @Override
    public void onCreate() {

        list = intent.getStringArrayListExtra("list");

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (list != null) {
            return list.size();
        }
        else {
            return 0;
        }
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (list == null) {
            return null;
        }

        RemoteViews views = new RemoteViews(mContext.getPackageName(),R.layout.widget_list_item);
        views.setTextViewText(R.id.widget_list_item,list.get(position));

        Intent fillIntent = new Intent();


        fillIntent.putExtra("position",position);
        fillIntent.putExtra("widget","widget");
        views.setOnClickFillInIntent(R.id.widget_list_item,fillIntent);

        return views;

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1; // Treat all items in the ListView the same
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
