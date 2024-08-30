package org.spiderflow.core.enums;

import java.util.HashSet;
import java.util.Set;

/**
 * @author yida
 * @date 2023-08-17 11:43
 * @description 文件体积单位枚举
 */
public enum FileSizeUnit {
	BYTE(0, FileSizeUnit.BYTE_KEY),
	KB(1, FileSizeUnit.KB_KEY),
	MB(2, FileSizeUnit.MB_KEY),
	GB(3, FileSizeUnit.GB_KEY),
	TB(4, FileSizeUnit.TB_KEY),
	PB(5, FileSizeUnit.PB_KEY);

	private int code;
	private String unitKey;

	FileSizeUnit(int code, String unitKey) {
		this.code = code;
		this.unitKey = unitKey;
	}

	public static final String BYTE_KEY = "bytes";
	public static final String KB_KEY = "KB";
	public static final String MB_KEY = "MB";
	public static final String GB_KEY = "GB";
	public static final String TB_KEY = "TB";
	public static final String PB_KEY = "PB";

	public static final Set<String> fileSizeUnitSet = new HashSet<>();

	static {
		fileSizeUnitSet.add(BYTE_KEY);
		fileSizeUnitSet.add(KB_KEY);
		fileSizeUnitSet.add(MB_KEY);
		fileSizeUnitSet.add(GB_KEY);
		fileSizeUnitSet.add(TB_KEY);
		fileSizeUnitSet.add(PB_KEY);
	}

	public static FileSizeUnit of(String fileSizeUnitKey) {
		if (null == fileSizeUnitKey || "".equals(fileSizeUnitKey)) {
			throw new IllegalArgumentException("fileSizeUnitKey MUST NOT be null or empty.");
		}
		if (!fileSizeUnitSet.contains(fileSizeUnitKey)) {
			throw new UnsupportedOperationException("fileSizeUnitKey MUST BE in the fileSizeUnitSet.");
		}
		if (BYTE_KEY.equals(fileSizeUnitKey)) {
			return BYTE;
		}
		if (KB_KEY.equals(fileSizeUnitKey)) {
			return KB;
		}
		if (MB_KEY.equals(fileSizeUnitKey)) {
			return MB;
		}
		if (GB_KEY.equals(fileSizeUnitKey)) {
			return GB;
		}
		if (TB_KEY.equals(fileSizeUnitKey)) {
			return TB;
		}
		return PB;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getUnitKey() {
		return unitKey;
	}

	public void setUnitKey(String unitKey) {
		this.unitKey = unitKey;
	}
}
