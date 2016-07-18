package com.it5.uitesting;

import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

import org.junit.Test;

/**
 * Created by IT5 on 2016/7/15.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mActivity;
    private TextView mContentView;

    public MainActivityTest() {
        // 所有的ActivityInstrumentationTestCase2子类都需要调用该父类的super(String)构造方法
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        // @Before注解表示在执行所有的testCase之前要做的事情
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());

        // getActivity()方法会在开始所有的testCase之前启动相应的Activity
        mActivity = getActivity();
        // findViewById()很熟悉吧？找到我要测试的控件
        mContentView = (TextView) mActivity.findViewById(R.id.textView);

    }


    @Test
    public void testPreconditions() {
        // @Test注解表示一个测试用例方法
        assertNotNull("AppStartActivity is null", mActivity);
    }

    @Test
    public void testContentDisplayed() {
        // 这里就是我们测试的目标，判断目标控件的text不为空
        String content = mContentView.getText().toString();
        assertNotNull("AppStartActivity Content is Null", content);
    }
}
