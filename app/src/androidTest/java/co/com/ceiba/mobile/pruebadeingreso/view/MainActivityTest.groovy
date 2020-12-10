package co.com.ceiba.mobile.pruebadeingreso.view

import android.support.test.espresso.Espresso
import android.support.test.espresso.NoMatchingViewException
import android.support.test.espresso.ViewAssertion
import android.support.test.rule.ActivityTestRule
import android.support.v7.widget.RecyclerView
import android.view.View
import androidx.test.espresso.contrib.RecyclerViewActions
import junit.extensions.ActiveTestSuite
import lombok.experimental.var
import org.junit.Rule
import org.junit.Test

class MainActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> mTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void recyclTest(){
        onView(withId(R.id.recyclerViewSearchResults)).check(new RecyclerViewItemCountAssertion(10));
        /*Espresso.onView(withId(R.id.recyclerViewSearchResults))
        .perform(RecyclerViewActions.actionOnItem()*/
    }
    public class RecyclerViewItemCountAssertion implements ViewAssertion {
        private final int expectedCount;

        public RecyclerViewItemCountAssertion(int expectedCount) {
            this.expectedCount = expectedCount;
        }
        @Override
        public void check(View view, NoMatchingViewException noViewFoundException) {
            if (noViewFoundException != null) {
                throw noViewFoundException;
            }
            RecyclerView recyclerView = (RecyclerView) view;
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            assertThat(adapter.getItemCount(), is(expectedCount));
        }
    }
}
