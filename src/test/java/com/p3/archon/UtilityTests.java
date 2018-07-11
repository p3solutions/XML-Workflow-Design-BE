/**
 * 
 */
package com.p3.archon;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.p3.archon.common.utils.Utility;

/**
 * @author saideepak
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UtilityTests {
	Utility utility = new Utility();

	@Test
	public void shouldChangeURL() {
		String url = utility.changeURLIfNeeded("C:\\users\\E843389\\Downloads\\Uploadedfiles\\Notification.xml");
		assertThat(url).doesNotContain("\\");
		assertThat(url).contains("/");
	}

}
