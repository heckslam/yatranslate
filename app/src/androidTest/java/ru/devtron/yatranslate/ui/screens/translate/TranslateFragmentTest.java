package ru.devtron.yatranslate.ui.screens.translate;

import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ru.devtron.yatranslate.R;
import ru.devtron.yatranslate.ui.screens.root.RootActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasFocus;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class TranslateFragmentTest {
    private static final String TEST_TRANSLATE_SENTENCE = "Hello world!";
    @Rule
    public ActivityTestRule<RootActivity> mActivityTestRule = new ActivityTestRule<RootActivity>(RootActivity.class);
    private ViewInteraction mTranslatePanel;
    private ViewInteraction mTranslateEt;
    private ViewInteraction mPlaySoundBtn;
    private ViewInteraction mClearBtn;

    @Before
    public void setUp() {
        mTranslatePanel = onView(withId(R.id.translate_panel));
        mTranslateEt = onView(withId(R.id.translate_et));
        mPlaySoundBtn = onView(withId(R.id.play_sound_btn));
        mClearBtn = onView(withId(R.id.clear_btn));
    }

    @Test
    public void click_on_translate_panel_SHOW_FOCUSED_DRAWABLE() throws Exception {
        mTranslatePanel.perform(click());
        mTranslatePanel.check(matches(hasFocus()));
    }

    @Test
    public void input_valid_translation_word() throws Exception {
        mTranslateEt.perform(click());
        mTranslateEt.perform(typeTextIntoFocusedView(TEST_TRANSLATE_SENTENCE));
        mTranslateEt.check(matches(withText(TEST_TRANSLATE_SENTENCE)));
        mPlaySoundBtn.check(matches(isDisplayed()));
        mClearBtn.check(matches(isDisplayed()));
    }

    @Test
    public void input_clears_entered_text() throws Exception {
        mTranslateEt.perform(click());
        mTranslateEt.perform(typeTextIntoFocusedView(TEST_TRANSLATE_SENTENCE));
        mClearBtn.check(matches(isDisplayed()));
        mClearBtn.perform(click());
        mTranslateEt.check(matches(withText("")));
    }

}