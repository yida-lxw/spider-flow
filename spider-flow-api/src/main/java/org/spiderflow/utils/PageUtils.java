package org.spiderflow.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.spiderflow.core.page.PageResult;

/**
 * @author yida
 * @package org.spiderflow.utils
 * @date 2024-08-29 20:08
 * @description 分页工具类
 */
public class PageUtils {
	public static <T> PageResult<T> getPageResult(IPage<T> pageInfo) {
		PageResult<T> pageResult = new PageResult();
		pageResult.setPageNum(Long.valueOf(pageInfo.getCurrent()).intValue());
		pageResult.setPageSize(Long.valueOf(pageInfo.getSize()).intValue());
		pageResult.setTotalSize(pageInfo.getTotal());
		pageResult.setTotalPages(Long.valueOf(pageInfo.getPages()).intValue());
		pageResult.setData(pageInfo.getRecords());
		return pageResult;
	}
}
