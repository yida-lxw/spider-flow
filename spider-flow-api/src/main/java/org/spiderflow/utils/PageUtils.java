package org.spiderflow.utils;

import com.github.pagehelper.PageInfo;
import org.spiderflow.core.page.PageResult;

/**
 * @author yida
 * @package org.spiderflow.utils
 * @date 2024-08-29 20:08
 * @description 分页工具类
 */
public class PageUtils {
	public static <T> PageResult<T> getPageResult(PageInfo<T> pageInfo) {
		PageResult<T> pageResult = new PageResult();
		pageResult.setPageNum(pageInfo.getPageNum());
		pageResult.setPageSize(pageInfo.getPageSize());
		pageResult.setTotalSize(pageInfo.getTotal());
		pageResult.setTotalPages(pageInfo.getPages());
		pageResult.setData(pageInfo.getList());
		return pageResult;
	}
}
