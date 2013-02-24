package com.luckybrews.bbf2013;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.luckybrews.bbf2013.R;
import com.xtremelabs.robolectric.RobolectricTestRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class BBDF2013_RoboTest {

	@Test
	public void testName() throws Exception {
		String app_name = new MainActivity().getResources().getString(R.string.app_name);
		assertThat(app_name, equalTo("BBF2013"));
	}
}

