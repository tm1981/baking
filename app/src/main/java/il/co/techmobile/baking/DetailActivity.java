package il.co.techmobile.baking;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import il.co.techmobile.baking.modal.Baking;
import il.co.techmobile.baking.modal.Ingredient;
import il.co.techmobile.baking.modal.Step;

public class DetailActivity extends AppCompatActivity implements MasterListFragment.OnListClickListener  {

    private Baking baking;
    private ArrayList<Step> stepsObj;
    private boolean mTwoPane;
    private FrameLayout stepsLayout;
    private FrameLayout ingredientsLayout;

    private Fragment masterListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        mTwoPane = getResources().getBoolean(R.bool.isTablet);

        Intent intent = getIntent();


        baking = intent.getParcelableExtra("baking");
        stepsObj = (ArrayList<Step>) baking.getSteps();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(baking.getName());
        }

        if (mTwoPane) {

            stepsLayout = findViewById(R.id.single_step_container);
            ingredientsLayout = findViewById(R.id.ingredients_container);
            if(savedInstanceState == null) {
                StepFragment stepFragment = new StepFragment();
                stepFragment.setSteps(stepsObj);

                IngredientsFragment ingredientsFragment = new IngredientsFragment();
                ingredientsFragment.setIngredients(baking.getIngredients());

                masterListFragment = new MasterListFragment();


                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .add(R.id.ingredients_container,ingredientsFragment).add(R.id.master_list_fragment,masterListFragment)
                        .commit();

                if (stepsLayout != null) {
                    stepsLayout.setVisibility(View.GONE);
                }

            }
            //set the current fragment in view after state change
            if (savedInstanceState != null) {
                String STEP_LAYOUT = "step_layout_visibility";
                stepsLayout.setVisibility(savedInstanceState.getInt(STEP_LAYOUT));
                String ING_LAYOUT = "ing_layout_visibility";
                ingredientsLayout.setVisibility(savedInstanceState.getInt(ING_LAYOUT));
                masterListFragment = getSupportFragmentManager().getFragment(savedInstanceState,"masterList");
            }
        }
    }



    @Override
    public void onItemListSelected(int position) {



        if (mTwoPane) {


            if (position>=1) {
                ingredientsLayout.setVisibility(View.GONE);
                stepsLayout.setVisibility(View.VISIBLE);
                StepFragment stepFragment = new StepFragment();
                stepFragment.setSteps(stepsObj);
                stepFragment.setStepIndex(position-1); //decrease position by 1 because the first item is the ingredients item
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.single_step_container,stepFragment)
                        .commit();
            } else {
                ingredientsLayout.setVisibility(View.VISIBLE);
                stepsLayout.setVisibility(View.GONE);
                IngredientsFragment ingredientsFragment = new IngredientsFragment();

                List<Ingredient> ingredients = baking.getIngredients();

                ingredientsFragment.setIngredients(ingredients);

                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.ingredients_container, ingredientsFragment)
                        .commit();

            }


        } else {
            if (position >=1) {
                final Intent intent = new Intent(this,StepActivity.class);
                intent.putExtra("baking",baking);
                intent.putExtra("position",position);
                startActivity(intent);
            } else {

                final Intent intent = new Intent(this,IngredientsActivity.class);
                intent.putExtra("baking",baking);
                startActivity(intent);
            }

        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mTwoPane) {
            outState.putInt("step_layout_visibility",stepsLayout.getVisibility());
            outState.putInt("ing_layout_visibility",ingredientsLayout.getVisibility());
            getSupportFragmentManager().putFragment(outState,"masterList",masterListFragment);
        }
    }
}
