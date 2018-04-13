package il.co.techmobile.baking;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import java.util.ArrayList;

import il.co.techmobile.baking.modal.Baking;
import il.co.techmobile.baking.modal.Step;

public class StepActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_step);
        Intent intent = getIntent();
        Baking baking = intent.getParcelableExtra("baking");
        int position = intent.getIntExtra("position",0);
        ArrayList<Step> stepsObj = (ArrayList<Step>) baking.getSteps();

        if(savedInstanceState == null) {

            StepFragment stepFragment = new StepFragment();

            stepFragment.setSteps(stepsObj);
            stepFragment.setStepIndex(position-1);

            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.single_step_container,stepFragment)
                    .commit();
        }
    }

}
