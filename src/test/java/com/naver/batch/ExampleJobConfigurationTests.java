package com.naver.batch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

@ContextConfiguration(locations = {"classpath:/launch-context.xml", "classpath:/META-INF/spring/module-context.xml", "classpath:/test-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners(listeners = {DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class, TransactionalTestExecutionListener.class})
public class ExampleJobConfigurationTests {

	@Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
	
	private SimpleJdbcTemplate simpleJdbcTemplate;
	
	@Autowired
    public void setDataSource(DataSource dataSource) {
        this.simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);
    }

	@Test
	public void testLaunchJob() throws Exception {
		BatchStatus status = jobLauncherTestUtils.launchJob().getStatus();
		
		Assert.assertEquals(BatchStatus.COMPLETED, status);
		
		List<Map<String, Object>> queryForList = simpleJdbcTemplate.queryForList("select * from board_user", new HashMap<String, Object>());
		
		for(Map map : queryForList) {
			System.out.println("123");
		}
	}

}
