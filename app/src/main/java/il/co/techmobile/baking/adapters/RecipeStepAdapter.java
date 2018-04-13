package il.co.techmobile.baking.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import il.co.techmobile.baking.R;


public class RecipeStepAdapter extends RecyclerView.Adapter<RecipeStepAdapter.ViewHolder> {

    private final LayoutInflater mInflater;
    private RecipeStepAdapter.ItemClickListener mClickListener;
    private final List<String> steps;
    private final Context mContext;
    private int lastPositionClicked = 0;

    public void setLastPositionClicked(int lastPositionClicked) {
        this.lastPositionClicked = lastPositionClicked;
    }





    public RecipeStepAdapter(Context context, List<String> steps) {
        this.mInflater = LayoutInflater.from(context);
        this.steps = steps;
        mContext = context;
    }

    @NonNull
    @Override
    public RecipeStepAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.rv_step_single_item,parent,false);
        return new RecipeStepAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position == 0) {
            holder.recipeStepDescription.setText(mContext.getString(R.string.ingredients_list));

        } else {
            String text;
            //remove the step text from the Recipe Introduction
            if (position-1 == 0) {
                text = steps.get(position - 1);
            }
            else {
                text = mContext.getString(R.string.step_label) + " "+ (position-1) + ": " + steps.get(position - 1);
            }
            holder.recipeStepDescription.setText(text);
        }

        //handel showing that the list item is selected by changing colors on the item in tablets
        if (mContext.getResources().getBoolean(R.bool.isTablet)) {
            if (position == lastPositionClicked) {
                holder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
                holder.recipeStepDescription.setTextColor(mContext.getResources().getColor(R.color.colorWhite));

            } else {
                holder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.cardview_light_background));
                holder.recipeStepDescription.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
            }
        }


        
    }

    @Override
    public int getItemCount() {
        return steps.size() +1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView recipeStepDescription;
        final CardView cardView;

        ViewHolder(View itemView) {
            super(itemView);
            recipeStepDescription = itemView.findViewById(R.id.single_step_rv);
            cardView = itemView.findViewById(R.id.step_card);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            if (mClickListener != null) mClickListener.onItemClick(getAdapterPosition());
        }
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(int position);
    }

}
