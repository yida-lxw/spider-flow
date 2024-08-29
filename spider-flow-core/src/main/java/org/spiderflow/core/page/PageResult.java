package org.spiderflow.core.page;

import java.util.Collection;

/**
 * @author yida
 * @package org.spiderflow.model
 * @date 2024-08-29 20:04
 * @description Type your description over here.
 */
public class PageResult<T> {
	/**
	 * 当前页码
	 */
	private int pageNum;
	/**
	 * 每页显示条数
	 */
	private int pageSize;
	/**
	 * 记录总条数
	 */
	private long totalSize;
	/**
	 * 总页数
	 */
	private int totalPages;

	/**
	 * 数据模型
	 */
	private Collection<T> data;

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public long getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public Collection<T> getData() {
		return data;
	}

	public void setData(Collection<T> data) {
		this.data = data;
	}
}
