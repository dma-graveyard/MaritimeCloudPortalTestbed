package net.maritimecloud.portal;

import net.maritimecloud.portal.config.ApplicationTestConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Example of a spring configuration based test (... a legacy from the template project originally created by spring boot).
 * <p>
 * @author Christoffer Børrild
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTestConfig.class)
public class ApplicationTests {

    @Test
    public void contextLoads() {
    }

}
