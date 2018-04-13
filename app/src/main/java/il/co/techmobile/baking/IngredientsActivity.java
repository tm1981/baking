package il.co.techmobile.baking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


import java.util.List;
import il.co.techmobile.baking.modal.Baking;
import il.co.techmobile.baking.modal.Ingredient;

public class IngredientsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);

        if(savedInstanceState == null) {
            IngredientsFragment ingredientsFragment = new IngredientsFragment();

            Intent intent = getIntent();
            Baking baking = intent.getParcelableExtra("baking");
            List<Ingredient> ingredients = baking.getIngredients();
            String name = baking.getName();

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(getString(R.string.ingredients_for) + " " + name);

            }

            ingredientsFragment.setIngredients(ingredients);

            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.ingredients_container,ingredientsFragment)
                    .commit();
        }
    }
}
