package il.co.techmobile.baking.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import il.co.techmobile.baking.R;
import il.co.techmobile.baking.modal.Baking;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private final Baking[] bakings;

    public RecyclerViewAdapter(Context context, Baking[] bakings) {
        this.mInflater = LayoutInflater.from(context);
        this.bakings = bakings;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.rv_main_single_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(bakings[position].getName());
        String ingText = "Number of ingredients needed: " + bakings[position].getIngredients().size();
        String stepsText = "Number of steps needed to complete: " + bakings[position].getSteps().size();
        holder.numOfIng.setText(ingText);
        holder.numOfSteps.setText(stepsText);
    }


    @Override
    public int getItemCount() {
        return bakings.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView name;
        final TextView numOfIng;
        final TextView numOfSteps;


        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textView);
            numOfIng = itemView.findViewById(R.id.ing_tv);
            numOfSteps = itemView.findViewById(R.id.steps_tv);
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
