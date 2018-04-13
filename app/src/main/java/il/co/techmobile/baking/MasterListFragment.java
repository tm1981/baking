package il.co.techmobile.baking;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import il.co.techmobile.baking.adapters.RecipeStepAdapter;
import il.co.techmobile.baking.modal.Baking;
import il.co.techmobile.baking.modal.Step;


public class MasterListFragment extends Fragment implements RecipeStepAdapter.ItemClickListener {


    // Define a new interface OnImageClickListener that triggers a callback in the host activity
    private OnListClickListener mCallback;
    private RecyclerView recyclerView;
    private RecipeStepAdapter recipeStepAdapter;
    private Parcelable recyclerViewState;
    private int lastPositionClicked;


    // OnImageClickListener interface, calls a method in the host activity named onImageSelected
    public interface OnListClickListener {
        void onItemListSelected(int position);
    }

    public MasterListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        Baking baking;

        View rootView = inflater.inflate(R.layout.fragment_master_list,container,false);

        if (getActivity() != null) {
            Intent intent = getActivity().getIntent();
            baking = intent.getParcelableExtra("baking");
            List<Step> stepsObj = baking.getSteps();
            ArrayList<String> stepsDescription = new ArrayList<>();
            for (int i=0; i<stepsObj.size(); i++) {
                stepsDescription.add(stepsObj.get(i).getShortDescription());
            }
            recipeStepAdapter = new RecipeStepAdapter(getContext(),stepsDescription);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

            recyclerView = rootView.findViewById(R.id.rv_steps_list);
            recyclerView.setLayoutManager(linearLayoutManager);
            recipeStepAdapter.setClickListener(this);

            recyclerView.setAdapter(recipeStepAdapter);

            if (savedInstanceState != null) {
                recyclerViewState = savedInstanceState.getParcelable("list");
                recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                lastPositionClicked = savedInstanceState.getInt("position");
                recipeStepAdapter.setLastPositionClicked(lastPositionClicked);
                //update the changes to the adapter
                recipeStepAdapter.notifyDataSetChanged();

            }

        }
        return rootView;
    }

    @Override
    public void onItemClick(int position) {
        mCallback.onItemListSelected(position);
        // update the adapter on the last clicked position
        lastPositionClicked = position;
        recipeStepAdapter.setLastPositionClicked(position);
        //update the changes to the adapter
        recipeStepAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mCallback = (OnListClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnListClickListener");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();

        outState.putParcelable("list",recyclerViewState);
        outState.putInt("position",lastPositionClicked);
        super.onSaveInstanceState(outState);
    }

}
