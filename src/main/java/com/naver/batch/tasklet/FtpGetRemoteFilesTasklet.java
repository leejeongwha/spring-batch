package com.naver.batch.tasklet;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.file.remote.synchronizer.AbstractInboundFileSynchronizer;
import org.springframework.integration.sftp.filters.SftpSimplePatternFileListFilter;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizer;
import org.springframework.util.Assert;

public class FtpGetRemoteFilesTasklet implements Tasklet {
	private static final Logger logger = Logger.getLogger(FtpGetRemoteFilesTasklet.class);

	private String localDirectory;
	private File file;
	private AbstractInboundFileSynchronizer<?> ftpInboundFileSynchronizer;
	private SessionFactory ftpClientFactory;
	private boolean autoCreateLocalDirectory = true;
	private boolean deleteLocalFiles = true;
	private String fileNamePattern;
	private String remoteDirectory;
	private int downloadFileAttempts = 12;
	private long retryIntervalMilliseconds = 300000;
	private boolean retryIfNotFound = false;

	@PostConstruct
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(ftpClientFactory, "sessionFactory attribute cannot be null");
		Assert.notNull(localDirectory, "localDirectory attribute cannot be null");
		Assert.notNull(remoteDirectory, "remoteDirectory attribute cannot be null");
		Assert.notNull(fileNamePattern, "fileNamePattern attribute cannot be null");

		setupFileSynchronizer();

		file = new File(localDirectory);

		if (!file.exists()) {
			if (this.autoCreateLocalDirectory) {
				if (logger.isDebugEnabled()) {
					logger.debug("The '" + this.localDirectory + "' directory doesn't exist; Will create.");
				}
				file.mkdirs();
			} else {
				throw new FileNotFoundException(file.getName());
			}
		}
	}

	private void setupFileSynchronizer() {
		//sftp
		ftpInboundFileSynchronizer = new SftpInboundFileSynchronizer(ftpClientFactory);
		((SftpInboundFileSynchronizer)ftpInboundFileSynchronizer).setFilter(new SftpSimplePatternFileListFilter(fileNamePattern));

		ftpInboundFileSynchronizer.setRemoteDirectory(remoteDirectory);
	}

	private void deleteLocalFiles() {
		if (deleteLocalFiles) {
			SimplePatternFileListFilter filter = new SimplePatternFileListFilter(fileNamePattern);
			List<File> matchingFiles = filter.filterFiles(file.listFiles());
			if (CollectionUtils.isNotEmpty(matchingFiles)) {
				for (File file : matchingFiles) {
					FileUtils.deleteQuietly(file);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.step.tasklet.Tasklet#execute(org.springframework.batch.core.StepContribution, org.springframework.batch.core.scope.context.ChunkContext)
	 */
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		deleteLocalFiles();

		ftpInboundFileSynchronizer.synchronizeToLocalDirectory(file);

		if (retryIfNotFound) {
			SimplePatternFileListFilter filter = new SimplePatternFileListFilter(fileNamePattern);
			int attemptCount = 1;
			while (filter.filterFiles(file.listFiles()).size() == 0 && attemptCount <= downloadFileAttempts) {
				logger.info("File(s) matching " + fileNamePattern + " not found on remote site.  Attempt " + attemptCount + " out of " + downloadFileAttempts);
				Thread.sleep(retryIntervalMilliseconds);
				ftpInboundFileSynchronizer.synchronizeToLocalDirectory(file);
				attemptCount++;
			}

			if (attemptCount >= downloadFileAttempts && filter.filterFiles(file.listFiles()).size() == 0) {
				throw new FileNotFoundException("Could not find remote file(s) matching " + fileNamePattern + " after " + downloadFileAttempts + " attempts.");
			}
		}

		return null;
	}

	public String getLocalDirectory() {
		return localDirectory;
	}

	public void setLocalDirectory(String localDirectory) {
		this.localDirectory = localDirectory;
	}

	public SessionFactory getFtpClientFactory() {
		return ftpClientFactory;
	}

	public void setFtpClientFactory(SessionFactory ftpClientFactory) {
		this.ftpClientFactory = ftpClientFactory;
	}

	public String getFileNamePattern() {
		return fileNamePattern;
	}

	public void setFileNamePattern(String fileNamePattern) {
		this.fileNamePattern = fileNamePattern;
	}

	public String getRemoteDirectory() {
		return remoteDirectory;
	}

	public void setRemoteDirectory(String remoteDirectory) {
		this.remoteDirectory = remoteDirectory;
	}
}
