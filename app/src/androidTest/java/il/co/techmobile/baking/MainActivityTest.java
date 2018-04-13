package il.co.techmobile.baking;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;


import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.allOf;


@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);




    @Test
    public void mainActivityTest() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.rv_list),
                        childAtPosition(
                                withId(android.R.id.content),
                                0)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction recyclerView2 = onView(
                allOf(withId(R.id.rv_steps_list),
                        childAtPosition(
                                withId(R.id.master_list_fragment),
                                0)));
        recyclerView2.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Navigate up"),
                        childAtPosition(
                                allOf(withId(R.id.action_bar),
                                        childAtPosition(
                                                withId(R.id.action_bar_container),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction recyclerView3 = onView(
                allOf(withId(R.id.rv_steps_list),
                        childAtPosition(
                                withId(R.id.master_list_fragment),
                                0)));
        recyclerView3.perform(actionOnItemAtPosition(1, click()));

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.button_next), withText("Next Step"),
                        childAtPosition(
                                allOf(withId(R.id.constraint),
                                        childAtPosition(
                                                withId(R.id.step_linear_layout),
                                                1)),
                                1)));
        appCompatButton.perform(scrollTo(), click());


        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.button_prev), withText("Previous Step"),
                        childAtPosition(
                                allOf(withId(R.id.constraint),
                                        childAtPosition(
                                                withId(R.id.step_linear_layout),
                                                1)),
                                2)));
        appCompatButton2.perform(scrollTo(), click());

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
