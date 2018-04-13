package il.co.techmobile.baking;


import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import il.co.techmobile.baking.adapters.IngredientsAdapter;
import il.co.techmobile.baking.modal.Ingredient;

public class IngredientsFragment extends Fragment {
    private static final String ING_LIST = "ing_list";
    private List<Ingredient> ingredients;


    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public IngredientsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        if(savedInstanceState != null) {

            ingredients = savedInstanceState.getParcelableArrayList(ING_LIST);
        }


        IngredientsAdapter ingredientsAdapter = new IngredientsAdapter(getContext(),ingredients);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());


        View rootView = inflater.inflate(R.layout.fragment_ingredients,container,false);
        RecyclerView recyclerView = rootView.findViewById(R.id.rv_ingredients_list);
        recyclerView.setLayoutManager(linearLayoutManager);

        if (getActivity() != null ) {
            recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                    DividerItemDecoration.VERTICAL));
        }

        recyclerView.setAdapter(ingredientsAdapter);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        outState.putParcelableArrayList(ING_LIST, (ArrayList<? extends Parcelable>) ingredients);
    }
}
