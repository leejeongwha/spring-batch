package com.naver.batch.tasklet;

import java.io.File;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.UnexpectedJobExecutionException;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

public class FileConfirmTasklet implements Tasklet, InitializingBean {
	private static final Logger logger = Logger.getLogger(FileConfirmTasklet.class);
	
	private Resource resource;

	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		File file = resource.getFile();
		
		logger.info("file.getAbsolutePath : " + file.getAbsolutePath());
		
		Assert.state(file.isFile());

		if(!file.exists()) {
			throw new UnexpectedJobExecutionException("Could not delete file " + file.getPath());
		}
		
		return RepeatStatus.FINISHED;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public void afterPropertiesSet() throws Exception {
        Assert.notNull(resource, "directory must be set");
    }

}
