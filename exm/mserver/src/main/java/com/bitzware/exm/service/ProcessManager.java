package com.bitzware.exm.service;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.bitzware.exm.dao.ServerPropertyDao;
import com.bitzware.exm.model.PlayerInfo;


/**
 * Class that manages child processes.
 * 
 * @author finagle
 */
public class ProcessManager {
	
	protected Logger logger = Logger.getLogger(this.getClass());

	private static class ProcessHandle {
		private Process process;

		public Process getProcess() {
			return process;
		}

		public void setProcess(Process process) {
			this.process = process;
		}

	}
	
	private class ProcessWaitThread extends Thread {
		private ProcessHandle processHandle;

		public ProcessWaitThread(ProcessHandle processHandle) {
			this.processHandle = processHandle;
		}

		@Override
		public void run() {
			Process process;
			synchronized (processMutex) {
				process = processHandle.getProcess();
			}
			
			if (process != null) {
				try {
					process.waitFor();
				} catch (InterruptedException e) {
					logger.warn("Waiting for process interrupted!");
				}
			}
			
			synchronized (processMutex) {
				processHandle.setProcess(null);
			}
		}
		
	}

	private ProcessHandle processHandle = null;
	private final Object processMutex = new Object();

	private ServerPropertyDao serverPropertyDao;

	public ProcessManager() {
	}

	public ServerPropertyDao getServerPropertyDao() {
		return serverPropertyDao;
	}

	public void setServerPropertyDao(ServerPropertyDao serverPropertyDao) {
		this.serverPropertyDao = serverPropertyDao;
	}

	/**
	 * Starts the presentation.
	 */
	public boolean startApplication(PlayerInfo player) throws IOException {
		synchronized (processMutex) {
			if (isRunning()) {
				return false;
			}

			String presentationPath = getPresentationPath();
			ProcessBuilder processBuilder;

			if (player != null) {
				int commandLineLength = 1;
				String path = null;
				String parametersAfter = null;

				if (!StringUtils.isEmpty(player.getPath())) {
					commandLineLength++;
					path = player.getPath();
				}
				if (!StringUtils.isEmpty(player.getParametersAfter())) {
					commandLineLength++;
					parametersAfter = player.getParametersAfter();
				}

				String[] commandLine = new String[commandLineLength];
				int index = 0;

				if (path != null) {
					commandLine[index] = path;
					index++;
				}

				commandLine[index] = presentationPath;
				index++;

				if (parametersAfter != null) {
					commandLine[index] = parametersAfter;
					index++;
				}

				processBuilder = new ProcessBuilder(commandLine);
			} else {
				processBuilder = new ProcessBuilder(presentationPath);
			}
			
			processHandle = new ProcessHandle();
			processHandle.setProcess(processBuilder.start());
			new ProcessWaitThread(processHandle).start();
		}

		return true;
	}

	/**
	 * Kills the presentation.
	 */
	public boolean killApplication() {
		synchronized (processMutex) {
			if (!isRunning()) {
				return false;
			}

			processHandle.getProcess().destroy();
			processHandle = null;
		}

		return true;
	}

	/**
	 * Returns true if and only if the presentation is running.
	 */
	public boolean isRunning() {
		synchronized (processMutex) {
			return processHandle != null && processHandle.getProcess() != null;
		}
	}

	private String getPresentationPath() {
		final String presentationDir =
			serverPropertyDao.getStringProperty(ConfigManager.PRESENTATION_DIR);
		final String presentationFile =
			serverPropertyDao.getStringProperty(ConfigManager.PRESENTATION_FILE);

		return new File(presentationDir, presentationFile).getAbsolutePath();
	}

}
