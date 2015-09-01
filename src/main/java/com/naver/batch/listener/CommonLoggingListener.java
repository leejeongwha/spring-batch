package com.naver.batch.listener;

import org.apache.log4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

/**
 * @author nhn
 * @since 2015. 6. 19.
 */
@Component("commonLoggingListener")
public class CommonLoggingListener implements JobExecutionListener {
	private static final Logger logger = Logger.getLogger(CommonLoggingListener.class);
		
	public void beforeJob(JobExecution jobExecution) {
		logger.info("##############board user batch beforeJob");
	}

	public void afterJob(JobExecution jobExecution) {
		logger.info("##############board user batch afterJob");
	}

}
