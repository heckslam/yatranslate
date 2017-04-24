package ru.devtron.yatranslate.data.managers;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static ru.devtron.yatranslate.data.managers.PreferencesManager.IS_INTRO_SHOWN;

public class PreferencesManagerTest {
    @Mock
    SharedPreferences mockSharedPreferences;
    @Mock
    SharedPreferences.Editor mockEditor;
    @Mock
    Context mockContext;
    private PreferencesManager mPreferencesManager;

    private Map<String, Boolean> fakeBooleanMap = new HashMap<>();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        given(mockContext.getSharedPreferences(anyString(), anyInt())).willReturn(mockSharedPreferences);
        given(mockSharedPreferences.edit()).willReturn(mockEditor);

        mPreferencesManager = new PreferencesManager(mockContext);
    }

    @Test
    public void isIntroShown() throws Exception {
        //given
        preparePutBooleanStub();

        //when
        boolean actualResult = mPreferencesManager.isIntroShown();

        //then
        assertFalse(actualResult);
    }

    @Test
    public void setIntroShown() throws Exception {
        //given
        preparePutBooleanStub();

        //when
        mPreferencesManager.setIntroShown(true);

        //then
        then(mockEditor).should(times(1)).apply();
        assertEquals(true, mockSharedPreferences.getBoolean(IS_INTRO_SHOWN, false));
    }

    private void preparePutBooleanStub() {
        given(mockEditor.putBoolean(anyString(), anyBoolean())).willAnswer(invocation -> {
            String key = invocation.getArgument(0);
            Boolean value = invocation.getArgument(1);
            fakeBooleanMap.put(key, value);
            return null;
        });

        given(mockSharedPreferences.getBoolean(anyString(), anyBoolean())).willAnswer(invocation -> {
            String key = invocation.getArgument(0);
            Boolean value = fakeBooleanMap.get(key);
            if (value == null) {
                value = invocation.getArgument(1);
            }
            return value;
        });
    }
}