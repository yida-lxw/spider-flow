package org.spiderflow.core.utils.file;

import java.io.Serializable;

/**
 * @author yida
 * @date 2024-08-15 09:03
 * @description Type your description over here.
 */
public class FileWalkResult implements Serializable {
	private static final long serialVersionUID = 5605638065384525402L;
	/**
	 * 是否增量模式
	 */
	private boolean deltaMode;

	private long fileStartTime;
	private long fileEndTime;

	/**
	 * 文件总个数
	 */
	private long totalFileCount;
	/**
	 * 文件夹总个数
	 */
	private long totalFolderCount;
	/**
	 * 文件总体积(单位:bytes)
	 */
	private long totalFileSize;

	public FileWalkResult() {
	}

	public FileWalkResult(boolean deltaMode, long totalFileCount, long totalFolderCount, long totalFileSize) {
		this.deltaMode = deltaMode;
		this.totalFileCount = totalFileCount;
		this.totalFolderCount = totalFolderCount;
		this.totalFileSize = totalFileSize;
	}

	public long getTotalFileCount() {
		return totalFileCount;
	}

	public void setTotalFileCount(long totalFileCount) {
		this.totalFileCount = totalFileCount;
	}

	public long getTotalFolderCount() {
		return totalFolderCount;
	}

	public void setTotalFolderCount(long totalFolderCount) {
		this.totalFolderCount = totalFolderCount;
	}

	public long getTotalFileSize() {
		return totalFileSize;
	}

	public void setTotalFileSize(long totalFileSize) {
		this.totalFileSize = totalFileSize;
	}

	public boolean isDeltaMode() {
		return deltaMode;
	}

	public void setDeltaMode(boolean deltaMode) {
		this.deltaMode = deltaMode;
	}

	public long getFileStartTime() {
		return fileStartTime;
	}

	public void setFileStartTime(long fileStartTime) {
		this.fileStartTime = fileStartTime;
	}

	public long getFileEndTime() {
		return fileEndTime;
	}

	public void setFileEndTime(long fileEndTime) {
		this.fileEndTime = fileEndTime;
	}
}
