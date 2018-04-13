package il.co.techmobile.baking.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import il.co.techmobile.baking.R;
import il.co.techmobile.baking.modal.Ingredient;


public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ViewHolder> {

    private final LayoutInflater mInflater;
    private final List<Ingredient> ingredients;
    private final Context context;

    public IngredientsAdapter(Context context, List<Ingredient> ingredient) {
        this.mInflater = LayoutInflater.from(context);
        this.ingredients = ingredient;
        this.context = context;
    }

    @NonNull
    @Override
    public IngredientsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.rv_ingredients_item,parent,false);
        return new IngredientsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name = context.getString(R.string.ingredient_label) + " " + ingredients.get(position).getIngredient();
        holder.ingredientName.setText(name);
        String quantity = "Quantity: " + ingredients.get(position).getQuantity();
        holder.ingredientQuantity.setText(quantity);

        String measure = "Measure: " + ingredients.get(position).getMeasure();
        holder.measure.setText(measure);

    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        final TextView ingredientName;
        final TextView ingredientQuantity;
        final TextView measure;

        ViewHolder(View itemView) {
            super(itemView);
            ingredientName = itemView.findViewById(R.id.ingredient_name_tv);
            ingredientQuantity = itemView.findViewById(R.id.tv_quantity);
            measure = itemView.findViewById(R.id.tv_measure);
        }
    }
}
