package org.spiderflow.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spiderflow.core.constants.Constants;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author yida
 * @package org.spiderflow.core.utils
 * @date 2024-08-25 20:36
 * @description 字符串操作工具类
 */
public class StringUtils {
	private static Logger logger = LoggerFactory.getLogger(StringUtils.class);

	private static final String[] EMPTY_STRING_ARRAY = {};

	private static final String FOLDER_SEPARATOR = "/";

	private static final char FOLDER_SEPARATOR_CHAR = '/';

	private static final String WINDOWS_FOLDER_SEPARATOR = "\\";

	private static final String TOP_PATH = "..";

	private static final String CURRENT_PATH = ".";

	private static final char EXTENSION_SEPARATOR = '.';

	private static final int DEFAULT_TRUNCATION_THRESHOLD = 100;

	private static final String TRUNCATION_SUFFIX = " (truncated)...";

	public static String[] randomStrings = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u",
			"v", "w", "x", "y", "z"};

	private static final Pattern trimRegex = Pattern.compile("(^[ |　| |\\s]*)|([ |　| |\\s]*$)");

	/**
	 * 判断指定字符串是否为null或空字符串
	 *
	 * @param source
	 * @return
	 */
	public static <T extends CharSequence> boolean isEmpty(T source) {
		return (null == source || "".equals(source));
	}

	/**
	 * 判断指定字符串是否不为null且不为空字符串
	 *
	 * @param source
	 * @return
	 */
	public static <T extends CharSequence> boolean isNotEmpty(T source) {
		return !isEmpty(source);
	}

	/**
	 * 替换路径中的反斜杠为斜杠
	 *
	 * @param appRootPath
	 */
	public static String replaceBackSlash(String appRootPath) {
		if (null != appRootPath && !"".equals(appRootPath)) {
			appRootPath = appRootPath.replaceAll(Constants.BACK_SLASH_IN_REGEX, "/");
		}
		return appRootPath;
	}

	/**
	 * 删除字符串中包含的所有空格
	 *
	 * @param source
	 * @return
	 */
	public static String removeAllWhiteSpace(String source) {
		if (isEmpty(source)) {
			return source;
		}
		return org.apache.commons.lang3.StringUtils.deleteWhitespace(source);
	}

	/**
	 * 字符串转成ByteBuffer
	 *
	 * @param source
	 * @return
	 */
	public static ByteBuffer string2ByteBuffer(String source) {
		return ByteBuffer.wrap(source.getBytes());
	}

	/**
	 * @param c
	 * @return boolean
	 * @description 判断给定字符是否为中文字符
	 * @author yida
	 * @date 2023-05-12 10:33:21
	 */
	public static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}

	/**
	 * @param str
	 * @return boolean
	 * @description 判断给定字符串是否中文乱码
	 * @author yida
	 * @date 2023-05-12 10:34:01
	 */
	public static boolean isMessyCode(String str) {
		Pattern p = Pattern.compile("\\s*|\t*|\r*|\n*");
		Matcher m = p.matcher(str);
		String after = m.replaceAll("");
		String temp = after.replaceAll("\\p{P}", "");
		char[] ch = temp.trim().toCharArray();
		float chLength = 0;
		float count = 0;
		for (int i = 0; i < ch.length; i++) {
			char c = ch[i];
			if (!Character.isLetterOrDigit(c)) {
				if (!isChinese(c)) {
					count = count + 1;
				}
				chLength++;
			}
		}
		float result = count / chLength;
		if (result > 0.4) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param input
	 * @return String
	 * @description 将UserName转成user_name形式
	 * @author yida
	 * @date 2023-04-26 09:18:26
	 */
	public static String toSnakeCase(String input) {
		StringBuilder output = new StringBuilder();
		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (Character.isUpperCase(c)) {
				if (output.length() > 0 && output.charAt(output.length() - 1) != '_') {
					output.append('_');
				}
				output.append(Character.toLowerCase(c));
			} else {
				output.append(c);
			}
		}
		return output.toString();
	}

	/**
	 * @param input
	 * @return String
	 * @description 将user_name转换成UserName
	 * @author yida
	 * @date 2023-04-26 11:40:33
	 */
	public static String toCamel(String input) {
		StringBuilder sb = new StringBuilder();
		String[] parts = input.split("_");
		for (String part : parts) {
			// 处理开头和结尾是下划线的情况
			if (!part.isEmpty()) {
				char first = Character.toUpperCase(part.charAt(0));
				String rest = part.substring(1);
				sb.append(first).append(rest);
			}
		}
		return sb.toString();
	}

	/**
	 * @param input
	 * @return String
	 * @description 首字母转成小写
	 * @author yida
	 * @date 2023-04-26 11:41:38
	 */
	public static String convertFirstLetterToLowerCase(String input) {
		return org.apache.commons.lang3.StringUtils.uncapitalize(input);
	}

	/**
	 * @param input
	 * @return String
	 * @description 首字母转成大写
	 * @author yida
	 * @date 2023-04-26 11:41:38
	 */
	public static String convertFirstLetterToUpperCase(String input) {
		return org.apache.commons.lang3.StringUtils.capitalize(input);
	}

	public static boolean hasLength(String str) {
		return (str != null && !str.isEmpty());
	}

	public static String replace(String inString, String oldPattern, @Nullable String newPattern) {
		if (!hasLength(inString) || !hasLength(oldPattern) || newPattern == null) {
			return inString;
		}
		int index = inString.indexOf(oldPattern);
		if (index == -1) {
			// no occurrence -> can return input as-is
			return inString;
		}

		int capacity = inString.length();
		if (newPattern.length() > oldPattern.length()) {
			capacity += 16;
		}
		StringBuilder sb = new StringBuilder(capacity);

		int pos = 0;  // our position in the old string
		int patLen = oldPattern.length();
		while (index >= 0) {
			sb.append(inString, pos, index);
			sb.append(newPattern);
			pos = index + patLen;
			index = inString.indexOf(oldPattern, pos);
		}

		// append any characters to the right of a match
		sb.append(inString, pos, inString.length());
		return sb.toString();
	}

	public static String[] delimitedListToStringArray(@Nullable String str, @Nullable String delimiter) {
		return delimitedListToStringArray(str, delimiter, null);
	}

	public static String[] delimitedListToStringArray(
			@Nullable String str, @Nullable String delimiter, @Nullable String charsToDelete) {

		if (str == null) {
			return EMPTY_STRING_ARRAY;
		}
		if (delimiter == null) {
			return new String[]{str};
		}

		List<String> result = new ArrayList<>();
		if (delimiter.isEmpty()) {
			for (int i = 0; i < str.length(); i++) {
				result.add(deleteAny(str.substring(i, i + 1), charsToDelete));
			}
		} else {
			int pos = 0;
			int delPos;
			while ((delPos = str.indexOf(delimiter, pos)) != -1) {
				result.add(deleteAny(str.substring(pos, delPos), charsToDelete));
				pos = delPos + delimiter.length();
			}
			if (str.length() > 0 && pos <= str.length()) {
				// Add rest of String, but not in case of empty input.
				result.add(deleteAny(str.substring(pos), charsToDelete));
			}
		}
		return toStringArray(result);
	}

	public static String[] toStringArray(@Nullable Collection<String> collection) {
		return (!CollectionUtils.isEmpty(collection) ? collection.toArray(EMPTY_STRING_ARRAY) : EMPTY_STRING_ARRAY);
	}

	public static String deleteAny(String inString, @Nullable String charsToDelete) {
		if (!hasLength(inString) || !hasLength(charsToDelete)) {
			return inString;
		}

		int lastCharIndex = 0;
		char[] result = new char[inString.length()];
		for (int i = 0; i < inString.length(); i++) {
			char c = inString.charAt(i);
			if (charsToDelete.indexOf(c) == -1) {
				result[lastCharIndex++] = c;
			}
		}
		if (lastCharIndex == inString.length()) {
			return inString;
		}
		return new String(result, 0, lastCharIndex);
	}

	public static String collectionToDelimitedString(
			@Nullable Collection<?> coll, String delim, String prefix, String suffix) {

		if (CollectionUtils.isEmpty(coll)) {
			return "";
		}

		int totalLength = coll.size() * (prefix.length() + suffix.length()) + (coll.size() - 1) * delim.length();
		for (Object element : coll) {
			totalLength += String.valueOf(element).length();
		}

		StringBuilder sb = new StringBuilder(totalLength);
		Iterator<?> it = coll.iterator();
		while (it.hasNext()) {
			sb.append(prefix).append(it.next()).append(suffix);
			if (it.hasNext()) {
				sb.append(delim);
			}
		}
		return sb.toString();
	}

	public static String collectionToDelimitedString(@Nullable Collection<?> coll, String delim) {
		return collectionToDelimitedString(coll, delim, "", "");
	}

	public static String cleanPath(String path, boolean stripeFirstSlash) {
		if (!hasLength(path)) {
			return path;
		}

		String normalizedPath = replace(path, WINDOWS_FOLDER_SEPARATOR, FOLDER_SEPARATOR);
		String pathToUse = normalizedPath;

		// Shortcut if there is no work to do
		if (pathToUse.indexOf('.') == -1) {
			return pathToUse;
		}

		// Strip prefix from path to analyze, to not treat it as part of the
		// first path element. This is necessary to correctly parse paths like
		// "file:core/../core/io/Resource.class", where the ".." should just
		// strip the first "core" directory while keeping the "file:" prefix.
		int prefixIndex = pathToUse.indexOf(':');
		String prefix = "";
		if (prefixIndex != -1) {
			prefix = pathToUse.substring(0, prefixIndex + 1);
			if (prefix.contains(FOLDER_SEPARATOR)) {
				prefix = "";
			} else {
				pathToUse = pathToUse.substring(prefixIndex + 1);
			}
		}

		if (stripeFirstSlash) {
			if (pathToUse.startsWith(FOLDER_SEPARATOR)) {
				prefix = prefix + FOLDER_SEPARATOR;
				pathToUse = pathToUse.substring(1);
			}
		}


		String[] pathArray = delimitedListToStringArray(pathToUse, FOLDER_SEPARATOR);
		// we never require more elements than pathArray and in the common case the same number
		Deque<String> pathElements = new ArrayDeque<>(pathArray.length);
		int tops = 0;

		for (int i = pathArray.length - 1; i >= 0; i--) {
			String element = pathArray[i];
			if (CURRENT_PATH.equals(element)) {
				// Points to current directory - drop it.
			} else if (TOP_PATH.equals(element)) {
				// Registering top path found.
				tops++;
			} else {
				if (tops > 0) {
					// Merging path element with element corresponding to top path.
					tops--;
				} else {
					// Normal path element found.
					pathElements.addFirst(element);
				}
			}
		}

		// All path elements stayed the same - shortcut
		if (pathArray.length == pathElements.size()) {
			return normalizedPath;
		}
		// Remaining top paths need to be retained.
		for (int i = 0; i < tops; i++) {
			pathElements.addFirst(TOP_PATH);
		}
		// If nothing else left, at least explicitly point to current path.
		if (pathElements.size() == 1 && pathElements.getLast().isEmpty() && !prefix.endsWith(FOLDER_SEPARATOR)) {
			pathElements.addFirst(CURRENT_PATH);
		}

		final String joined = collectionToDelimitedString(pathElements, FOLDER_SEPARATOR);
		// avoid string concatenation with empty prefix
		return prefix.isEmpty() ? joined : prefix + joined;
	}

	/**
	 * 追加字符串
	 *
	 * @param s
	 * @param len    表示初始化时StringBuilder的长度
	 * @param params
	 * @return
	 */
	public static StringBuilder append(String s, int len, Object... params) {
		StringBuilder sb = new StringBuilder(len);
		if (s != null) sb.append(s);
		if (params != null && params.length > 0) {
			for (Object obj : params) {
				if (obj != null) {
					sb.append(obj.toString());
				}
			}
		}
		return sb;
	}

	/**
	 * 追加字符串,初始化长度使用64
	 *
	 * @param s
	 * @param params
	 * @return
	 */
	public static String append(String s, Object... params) {
		return append(s, 64, params).toString();
	}

	/**
	 * 创建uuid.会对UUID生成的字符串进行去-操作后变成32位,原来36位
	 * 通过系统的UUID.randomUUID()实现
	 *
	 * @return
	 */
	public static String getUuid() {
		String s = UUID.randomUUID().toString();
		s = s.replace("-", "");
		return s;
	}

	/**
	 * 左补齐byte数组
	 *
	 * @param bb
	 * @param len
	 * @param b
	 * @return
	 */
	public static byte[] alignLeft(byte[] bb, int len, byte b) {
		int olen = bb.length;
		if (olen < len) {
			byte[] buf = new byte[len];
			System.arraycopy(bb, 0, buf, len - olen, olen);
			for (int i = 0; i < len - olen; i++)
				buf[i] = b;
			return buf;
		}
		return bb;
	}

	/**
	 * 左补齐,超过补齐长度则忽略
	 *
	 * @param num
	 * @param len
	 * @return
	 */
	public static String alignLeft(String num, int len, char c) {
		if (num.length() < len) {
			char[] buf = new char[len - num.length()];
			for (int i = 0; i < buf.length; i++)
				buf[i] = c;
			return new String(buf) + num;
		}
		return num;
	}

	/**
	 * 左补齐成指定的倍数,注意空的时候也会进行填充
	 *
	 * @param num
	 * @param n   数值
	 * @param c
	 * @return
	 */
	public static String alignLeftMultiple(String num, int n, char c) {
		if (num == null || num.length() == 0) {
			char[] buf = new char[n];
			Arrays.fill(buf, 0, n, c);
			return new String(buf);
		}
		int len = num.length();
		if (len % n != 0) {
			int mod = len % n;
			char[] buf = new char[(len / n + 1) * n];
			Arrays.fill(buf, 0, n - mod, c);
			System.arraycopy(num.toCharArray(), 0, buf, n - mod, len);
			return new String(buf);
		}
		return num;
	}

	/**
	 * 由补齐成指定的倍数,注意空的时候也会进行填充
	 *
	 * @param num
	 * @param n   数值
	 * @param c
	 * @return
	 */
	public static String alignRightMultiple(String num, int n, char c) {
		if (num == null || num.length() == 0) {
			char[] buf = new char[n];
			Arrays.fill(buf, 0, n, c);
			return new String(buf);
		}
		int len = num.length();
		if (len % n != 0) {
			int mod = len % n;
			char[] buf = new char[(len / n + 1) * n];
			System.arraycopy(num.toCharArray(), 0, buf, 0, len);
			Arrays.fill(buf, len, buf.length, c);
			return new String(buf);
		}
		return num;
	}

	/**
	 * 左补齐,超过补齐长度则忽略
	 *
	 * @param num
	 * @param len
	 * @param c
	 * @return
	 */
	public static String alignLeft(String num, int len, String c) {
		int slen = num.length();
		if (slen < len) {
			int ll = c.length();
			StringBuilder sb = new StringBuilder();
			int count = (int) Math.ceil(((double) (len - slen)) / ll);
			for (int i = 0; i < count; i++) {
				sb = sb.append(c);
			}
			StringBuilder result = new StringBuilder();
			result.append(sb.substring(0, len - slen));
			result.append(num);
			return result.toString();
		}
		return num;
	}

	/**
	 * 右补齐byte数组
	 *
	 * @param bb
	 * @param len
	 * @param b
	 * @return
	 */
	public static byte[] alignRight(byte[] bb, int len, byte b) {
		int olen = bb.length;
		if (olen < len) {
			byte[] buf = new byte[len];
			System.arraycopy(bb, 0, buf, 0, olen);
			for (int i = olen; i < len; i++)
				buf[i] = b;
			return buf;
		}
		return bb;
	}

	/**
	 * 右补齐,超过补齐长度则忽略
	 *
	 * @param num
	 * @param len
	 * @return
	 */
	public static String alignRight(String num, int len, char c) {
		if (num.length() < len) {
			char[] buf = new char[len - num.length()];
			for (int i = 0; i < buf.length; i++)
				buf[i] = c;
			return num + new String(buf);
		}
		return num;
	}

	/**
	 * 左补齐,超过补齐长度则忽略
	 *
	 * @param num
	 * @param len
	 * @param c
	 * @return
	 */
	public static String alignRight(String num, int len, String c) {
		int slen = num.length();
		if (slen < len) {
			int ll = c.length();
			StringBuilder sb = new StringBuilder();
			int count = (int) Math.ceil(((double) (len - slen)) / ll);
			for (int i = 0; i < count; i++) {
				sb = sb.append(c);
			}
			StringBuilder result = new StringBuilder();
			result.append(num);
			result.append(sb.substring(0, len - slen));
			return result.toString();
		}
		return num;
	}

	/**
	 * 判断obj对象,如果非空则返回s1,否则返回s2
	 *
	 * @param str
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static String checkChoose(String str, String s1, String s2) {
		if (str == null || str.trim().length() == 0) return s2;
		return s1;
	}

	/**
	 * 通过CheckEqual判断选择返回s1还是s2 相等返回s1  否则s2
	 *
	 * @param str
	 * @param eqval
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static String checkEqualChoose(Object str, Object eqval, String s1, String s2) {
		if (checkEqual(str, eqval)) return s1;
		return s2;
	}

	/**
	 * 通过CheckEqualIgnoreCase判断选择返回s1还是s2 相等返回s1  否则s2
	 *
	 * @param str
	 * @param eqval
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static String checkEqualChooseIgnoreCase(Object str, Object eqval, String s1, String s2) {
		if (checkEqualIgnoreCase(str, eqval)) return s1;
		return s2;
	}

	/**
	 * 判断col对象,如果非空则返回s1,否则返回s2
	 *
	 * @param col
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static String checkCollectionChoose(Collection<?> col, String s1, String s2) {
		if (col == null || col.size() == 0) return s2;
		return s1;
	}

	/**
	 * 改变字符串编码
	 *
	 * @param s
	 * @param en1
	 * @param en2
	 * @return
	 */
	public static String changeEncoding(String s, String en1, String en2) {
		if (s == null) return "";
		try {
			return new String(s.getBytes(en1), en2);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return s;
	}

	/**
	 * 将字符串由 gb2312	 ==>	iso
	 */
	public static String changeGb23122Iso(String s) {
		return changeEncoding(s, "gb2312", "iso-8859-1");
	}

	/**
	 * 将字符串由 gb2312	==>	utf-8
	 */
	public static String changeGb23122Utf8(String s) {
		return changeEncoding(s, "gb2312", "utf-8");
	}

	/**
	 * 将字符串由 iso-8859-1	==>	gb2312
	 */
	public static String changeIso2Gb2312(String s) {
		return changeEncoding(s, "iso-8859-1", "gb2312");
	}

	/**
	 * 将字符串由 iso-8859-1	==>	utf-8
	 */
	public static String changeIso2Utf8(String s) {
		return changeEncoding(s, "iso-8859-1", "utf-8");
	}

	/**
	 * 将字符串由 utf-8	 ==>	gb2312
	 */
	public static String changeUtf82Gb2312(String s) {
		return changeEncoding(s, "utf-8", "gb2312");
	}

	/**
	 * 将字符串由 utf-8	 ==>	iso-8859-1
	 */
	public static String changeUtf82Iso(String s) {
		return changeEncoding(s, "utf-8", "iso-8859-1");
	}

	/**
	 * 所有参数对象中有不为empty的对象
	 *
	 * @param obj
	 * @param s1
	 * @param params
	 * @return
	 */
	public static boolean checkEmptyOr(Object obj, Object s1, Object... params) {
		if (checkEmpty(obj)) return true;
		if (checkEmpty(s1)) return true;
		if (params != null && params.length > 0) {
			for (Object o : params) {
				if (checkEmpty(o)) return true;
			}
		}
		return false;
	}

	/**
	 * 所有参数对象中所有对象不为empty
	 *
	 * @param obj
	 * @param s1
	 * @param params
	 * @return
	 */
	public static boolean checkEmptyAnd(Object obj, Object s1, Object... params) {
		if (!checkEmpty(obj)) return false;
		if (!checkEmpty(s1)) return false;
		if (params != null && params.length > 0) {
			for (Object o : params) {
				if (!checkEmpty(o)) return false;
			}
		}
		return true;
	}

	/**
	 * 检测对象非null.并且装换成字符串后长度大于0
	 * obj!=null && obj.toString().length()>0
	 *
	 * @param obj
	 * @return
	 */
	public static boolean checkEmpty(Object obj) {
		return obj != null && obj.toString().length() > 0;
	}

	/**
	 * 检测集合或字符串是否是空,(String,Collection,Map,Array)否则作为字符串判断,为空则返回false
	 *
	 * @param obj
	 * @return
	 */
	public static boolean checkEmptyEx(Object obj) {
		if (obj == null) return false;
		if (obj instanceof String) {
			return ((String) obj).trim().length() > 0;
		} else if (obj instanceof Collection) {
			return ((Collection<?>) obj).size() > 0;
		} else if (obj instanceof Map) {
			return ((Map<?, ?>) obj).size() > 0;
		} else if (obj.getClass().isArray()) {
			return Array.getLength(obj) > 0;
		} else {
			return obj.toString().length() > 0;
		}
	}

	/**
	 * 检测字符串是否为非空,非空返回false.注意不会进行trim
	 *
	 * @param s
	 * @return
	 */
	public static boolean checkEmpty(String s) {
		if (s == null) {
			return false;
		}
		return s.length() > 0;
	}

	/**
	 * 检测字符串数组,当长度大于0时返回true,否则false
	 *
	 * @param ss
	 * @return
	 */
	public static boolean checkEmpty(String[] ss) {
		if (ss == null) return false;
		return ss.length > 0;
	}

	/**
	 * 去空白的验证.去空白只是简单去除
	 *
	 * @param ss
	 * @return
	 */
	public static boolean checkTrimEmpty(String ss) {
		if (ss == null) return false;
		return ss.trim().length() > 0;
	}

	/**
	 * 获取首个非空字符串,如果未能获取到则返回null
	 *
	 * @param ss
	 * @return
	 */
	public static String getFirstNotEmpty(String... ss) {
		if (ss == null) return null;
		for (String s : ss) {
			if (checkEmpty(s)) return s;
		}
		return null;
	}

	/**
	 * 如果对象为空串则转换成null
	 *
	 * @param s
	 * @return
	 */
	public static String emptyToNull(String s) {
		if (s != null) {
			s = s.trim();
			if (s.length() == 0) return null;
			return s;
		}
		return null;
	}

	private static Pattern azAZ09 = Pattern.compile("[A-Za-z0-9]*");
	private static Pattern azAZ09_ = Pattern.compile("[A-Za-z0-9_]*");
	private static Pattern email = Pattern.compile("[A-Za-z0-9_\\-\\.]+@[A-Za-z0-9_\\-\\.]+");

	/**
	 * 检测对象是否匹配正则表达式
	 * 如果s为null将直接返回true
	 * 为空也会返回true
	 *
	 * @param s
	 * @param p
	 * @return
	 */
	public static boolean checkPattern(Object s, Pattern p) {
		if (s != null) {
			String ss = s.toString();
			if (ss.length() == 0) return true;
			return p.matcher(ss).matches();
		}
		return true;
	}

	/**
	 * 如果s为null将直接返回true
	 * 为空也会返回true .   匹配正则:[A-Za-z0-9]*
	 *
	 * @param s
	 * @return
	 */
	public static boolean checkAZaz09(Object s) {
		return checkPattern(s, azAZ09);
	}

	/**
	 * 如果s为null将直接返回true
	 * 为空也会返回true .   匹配正则:[A-Za-z0-9_]*
	 *
	 * @param s
	 * @return
	 */
	public static boolean checkAZaz09_(Object s) {
		return checkPattern(s, azAZ09_);
	}

	/**
	 * 简单匹配email格式. 不一定百分百符合email的规则
	 * 如果s为null将直接返回true
	 * 为空也会返回true .   匹配正则:[A-Za-z0-9_\\-\\.]+@[A-Za-z0-9_\\-\\.]+
	 *
	 * @param s
	 * @return
	 */
	public static boolean checkEmail(Object s) {
		return checkPattern(s, email);
	}

	/**
	 * 判断对象是否跟后面参数中的一个相等.  存在相等则返回true.否则false
	 *
	 * @param obj
	 * @param s1
	 * @param params
	 * @return
	 */
	public static boolean checkEqualOr(Object obj, Object s1, Object s2, Object... params) {
		if (checkEqual(obj, s1)) return true;
		if (checkEqual(obj, s2)) return true;
		if (params != null && params.length > 0) {
			for (Object p : params) {
				if (checkEqual(obj, p)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 判断对象是否跟后面参数中的一个相等.  存在相等则返回true.否则false
	 *
	 * @param obj
	 * @param s1
	 * @param params
	 * @return
	 */
	public static boolean checkEqualIgnoreCaseOr(Object obj, Object s1, Object s2, Object... params) {
		if (checkEqualIgnoreCase(obj, s1)) return true;
		if (checkEqualIgnoreCase(obj, s2)) return true;
		if (params != null && params.length > 0) {
			for (Object p : params) {
				if (checkEqualIgnoreCase(obj, p)) return true;
			}
		}
		return false;
	}

	/**
	 * 检测字符串相等. 如果同时为null  也会返回true
	 *
	 * @param s1
	 * @param s2
	 * @return
	 */

	public static boolean checkEqual(String s1, String s2) {
		if (s1 == null && s2 == null) return true;
		if (s1 != null) return s1.equals(s2);
		return false;
	}

	/**
	 * 检测字符串是否相等. 调用对象的toString方法后检测
	 *
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static boolean checkEqual(Object s1, Object s2) {
		if (s1 == null && s2 == null) return true;
		if (s1 != null && s2 != null) {
			return s1.toString().equals(s2.toString());
		}
		return false;
	}

	/**
	 * 检测字符串相等.忽略大小写. 如果同时为null  也会返回true
	 *
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static boolean checkEqualIgnoreCase(String s1, String s2) {
		if (s1 == null && s2 == null) return true;
		if (s1 != null) return s1.equalsIgnoreCase(s2);
		return false;
	}

	/**
	 * 检测字符串相等.忽略大小写. 如果同时为null  也会返回true
	 *
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static boolean checkEqualIgnoreCase(Object s1, Object s2) {
		if (s1 == null && s2 == null) return true;
		if (s1 == s2) return true;
		if (s1 != null && s2 != null) {
			return s1.toString().equalsIgnoreCase(s2.toString());
		}
		return false;
	}

	/**
	 * 检测对象的长度,大于等于slen
	 *
	 * @param obj  对象,集合,数组
	 * @param slen 下限,必须大于等于0
	 * @return
	 */
	public static boolean checkLength(Object obj, int slen) {
		if (slen < 0) {
			return false;
		}
		return checkLength(obj, slen, -1);
	}

	/**
	 * 检测对象的长度,包含上限和下限
	 * 上限下限同时不限时返回true
	 *
	 * @param obj  对象,Collection,Map,数组
	 * @param slen 下限,-1表示无下限,设置时必须大于-1
	 * @param elen 上限,-1表示无上限,设置时必须大于0
	 * @return
	 */
	public static boolean checkLength(Object obj, int slen, int elen) {
		int n = getLength(obj);
		if (slen < 0 && elen < 0) {
			return true;
		}
		if (slen > -1) {
			if (elen > -1) return n >= slen && n <= elen;
			else return n >= slen;
		} else return n <= elen;
	}

	/**
	 * 检测对象的长度,包含上限和下限  字算2个长度
	 * 上限下限同时不限时返回true
	 *
	 * @param obj  对象,Collection,Map,数组
	 * @param slen 下限,-1表示无下限,设置时必须大于-1
	 * @param elen 上限,-1表示无上限,设置时必须大于0
	 * @return
	 */
	public static boolean checkLength2(Object obj, int slen, int elen) {
		int n = getLength2(obj);
		if (slen < 0 && elen < 0) {
			return true;
		}
		if (slen > -1) {
			if (elen > -1) return n >= slen && n <= elen;
			else return n >= slen;
		} else return n <= elen;
	}

	/**
	 * 检查是否匹配
	 *
	 * @param s
	 * @param regx
	 * @return
	 */
	public static boolean checkMatch(String s, Pattern regx) {
		return regx.matcher(s).matches();
	}

	/**
	 * 检测对象是否为null. 注意只判断为null的情况
	 *
	 * @param obj
	 * @return
	 */
	public static boolean checkNull(Object obj) {
		if (obj == null) return false;
		return true;
	}

	/**
	 * 检测对象列表是否为为null.必须全非null. 对象个数为0也会返回false
	 *
	 * @param objs
	 * @return
	 */
	public static boolean checkNulls(Object... objs) {
		if (objs == null || objs.length < 1) return false;
		for (Object obj : objs) {
			if (obj == null) return false;
		}
		return true;
	}

	/**
	 * 如果为null则返回""
	 *
	 * @param s
	 * @return
	 */
	public static String clearNull(String s) {
		if (s == null) return "";
		return s;
	}

	/**
	 * 如果为null则返回""
	 *
	 * @param o
	 * @return
	 */
	public static String clearNull(Object o) {
		if (o == null) return "";
		return o.toString();
	}

	/**
	 * 将字符串切成指定长度,为空时返回 ""
	 */
	public static String cutString(String s, int len) {
		if (s == null) return "";
		if (s.length() > len) return s.substring(0, len);
		return s;
	}

	/**
	 * 单字长度为2  如果长度小于max则直接返回.否则min长度字符串,再加上suffix
	 *
	 * @param s
	 * @param min
	 * @param max
	 * @return
	 */
	public static String cutStringByte(String s, int min, int max, String suffix) {

		char[] cs = s.toCharArray();
		int tlen = 0;
		for (char c : cs) {
			if (c > 256) tlen += 2;
			else tlen += 1;
		}
		if (tlen <= max) return s;
		tlen = 0;
		StringBuilder sb = new StringBuilder();
		for (char c : cs) {
			if (c > 256) tlen += 2;
			else tlen += 1;
			if (tlen >= min) break;
			sb.append(c);
		}
		if (suffix != null) sb.append(suffix);
		return sb.toString();
	}

	/**
	 * 单字长度为2,如果长度小于max则直接返回.否则min长度字符串
	 *
	 * @param s
	 * @param min
	 * @param max
	 * @return
	 */
	public static String cutStringByte(String s, int min, int max) {
		return cutStringByte(s, min, max, null);
	}

	/**
	 * 截取字符串,从指定位置开始截取到结尾.
	 *
	 * @param s
	 * @param si 如果整数则从下标开始截取,负数则返回空""
	 * @return
	 */
	public static String substring(String s, int si) {
		if (s == null || s.length() == 0) return "";
		int l = s.length();
		if (si <= 0) return "";
		if (si >= l || si < 0) return "";
		return s.substring(si);
	}

	/**
	 * 安全的进行取字串.当原来substring会出现错误的参数都会返回空""
	 *
	 * @param s
	 * @param si 开始下标
	 * @param ei 结束下标. 如果大于最大长度则取最大长度,如果小于等于0则直接返回 ""
	 * @return
	 */
	public static String substring(String s, int si, int ei) {
		if (s == null || s.length() == 0) return "";
		int l = s.length();
		if (si >= l) return "";
		if (ei <= 0) return ""; //如果结束下标小于等于0则返回 ""
		if (ei > l) ei = l;
		if (si >= ei) return "";
		return s.substring(si, ei);
	}

	/**
	 * 安全的进行取字串.支持负数表示从后向前截取
	 *
	 * @param s
	 * @param si  开始下标
	 * @param len 长度.如果为负数则表示从后开始截取.0表示截取到结束
	 * @return
	 */
	public static String substr(String s, int si, int len) {
		if (s == null || s.length() == 0 || si < 0) return "";
		int size = s.length();
		if (si >= size) return "";
		if (len == 0) len = size; //当去只需要大于等于长度即可,下面有溢出判断.  设置成 Integer.MAX_VALUE 也可以
		int ei = si + len;
		if (len < 0) {
			ei = size + len;
		}
		if (ei >= size) ei = size;
		else if (ei <= si) return "";
		return s.substring(si, ei);
	}

	/**
	 * 通过指定前字符串和后字符串进行切取字符串,如果不能切取.则返回"". 任意一方未找到则为不能切取. self表示是否返回时包含了前后字符串本身.一般为false
	 *
	 * @param s
	 * @param s1   前查找字符串,支持""
	 * @param s2   后查找字符串,支持""
	 * @param self 是否包含s1和s2本身. 例如: 如果( 和 ) 截取  (abc), 如果true则返回(abc), false则返回的是abc
	 * @return
	 */
	public static String substringBy(String s, String s1, String s2, boolean self) {
		if (s == null) return "";
		if (s1 == null) s1 = "";
		if (s2 == null) s2 = "";

		if (s.length() < (s1.length() + s2.length())) return "";
		int i1 = s.indexOf(s1);
		int i2 = s.lastIndexOf(s2);
		if (i1 == -1 || i2 == -1) return "";
		if (!self) {
			i1 = i1 + s1.length();
		} else {
			i2 = i2 + s2.length();
		}
		return substring(s, i1, i2);
	}

	/**
	 * 与substringBy 区别在于,当不能切取时,返回原字符串   任意一方未找到则为不能切取 . self表示是否返回时包含了前后字符串本身.一般为false
	 *
	 * @param s
	 * @param s1   从左查找的字符串,支持""
	 * @param s2   从右查找的字符串,支持""
	 * @param self 是否包含s1和s2本身		表示是否切除查找字符串本身.true表保留查找字符串自身
	 * @return
	 */
	public static String substringBy2(String s, String s1, String s2, boolean self) {
		if (s == null) return "";
		if (s1 == null) s1 = "";
		if (s2 == null) s2 = "";

		if (s.length() < (s1.length() + s2.length())) return s;
		int i1 = s.indexOf(s1);
		int i2 = s.lastIndexOf(s2);
		if (i1 == -1 || i2 == -1) return s;
		if (!self) {
			i1 = i1 + s1.length();
		} else {
			i2 = i2 + s2.length();
		}
		return substring(s, i1, i2);
	}

	/**
	 * 通过左方向查找
	 *
	 * @param s
	 * @param s1   从左查找,支持""
	 * @param s2   从左查找,支持""
	 * @param self 表示是否切除查找字符串本身.true表保留查找字符串自身
	 * @return
	 */
	public static String substringLeftBy(String s, String s1, String s2, boolean self) {
		if (s == null) return "";
		if (s1 == null) s1 = "";
		if (s2 == null) s2 = "";

		if (s.length() < (s1.length() + s2.length())) return "";
		int i1 = s.indexOf(s1);
		int i2 = s.indexOf(s2, i1 + s1.length());
		if (i1 == -1 || i2 == -1) return "";
		if (!self) {
			i1 = i1 + s1.length();
		} else {
			i2 = i2 + s2.length();
		}
		return substring(s, i1, i2);
	}

	/**
	 * 跟substringLeftBy一样,如果不能切取则返回原字符串
	 *
	 * @param s
	 * @param s1   ,支持""
	 * @param s2   ,支持""
	 * @param self 表示是否切除查找字符串本身.true表保留查找字符串自身
	 * @return
	 */
	public static String substringLeftBy2(String s, String s1, String s2, boolean self) {
		if (s == null) return "";
		if (s1 == null) s1 = "";
		if (s2 == null) s2 = "";

		if (s.length() < (s1.length() + s2.length())) return s;
		int i1 = s.indexOf(s1);
		int i2 = s.indexOf(s2, i1 + s1.length());
		if (i1 == -1 || i2 == -1) return s;
		if (!self) {
			i1 = i1 + s1.length();
		} else {
			i2 = i2 + s2.length();
		}
		return substring(s, i1, i2);
	}

	/**
	 * 从右开始查找
	 *
	 * @param s
	 * @param s1   ,支持""
	 * @param s2   ,支持""
	 * @param self 表示是否切除查找字符串本身.true表保留查找字符串自身
	 * @return
	 */
	public static String substringRightBy(String s, String s1, String s2, boolean self) {
		if (s == null) return "";
		if (s1 == null) s1 = "";
		if (s2 == null) s2 = "";

		if (s.length() < (s1.length() + s2.length())) return "";
		int i2 = s.lastIndexOf(s2);
		int i1 = s.lastIndexOf(s1, i2);
		if (i1 == -1 || i2 == -1) return "";
		if (!self) {
			i1 = i1 + s1.length();
		} else {
			i2 = i2 + s2.length();
		}
		return substring(s, i1, i2);
	}

	/**
	 * 跟substringRightBy一样,如果不能切取则返回原字符串
	 *
	 * @param s
	 * @param s1   ,支持""
	 * @param s2   ,支持""
	 * @param self 表示是否切除查找字符串本身.true表保留查找字符串自身
	 * @return
	 */
	public static String substringRightBy2(String s, String s1, String s2, boolean self) {
		if (s == null) return "";
		if (s1 == null) s1 = "";
		if (s2 == null) s2 = "";

		if (s.length() < (s1.length() + s2.length())) return s;
		int i2 = s.lastIndexOf(s2);
		int i1 = s.lastIndexOf(s1, i2);
		if (i1 == -1 || i2 == -1) return s;
		if (!self) {
			i1 = i1 + s1.length();
		} else {
			i2 = i2 + s2.length();
		}
		return substring(s, i1, i2);
	}

	/**
	 * 将字符串进行gb2312进行url解码
	 */
	public static String decodeGb2312(String s) {
		return decodeString(s, "gb2312");
	}

	/**
	 * 解析&#???? 字符串,更多方法使用 @org.apache.commons.lang3.StringEscapeUtils
	 *
	 * @param html
	 * @return
	 */
	public static String decodeHtml(String html) {
		return StringEscapeUtils.unescapeHtml4(html);
	}

	/**
	 * 对字符串进行url进行解码
	 */
	public static String decodeString(String s, String encoding) {
		try {
			return URLDecoder.decode(s, encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return s;
	}

	/**
	 * 将字符串进行gb2312进行url编码
	 */
	public static String encodeGb2312(String s) {
		return encodeString(s, "gb2312");
	}

	/**
	 * 将字符串转换成 &#???? 形式,更多方法使用 @org.apache.commons.lang3.StringEscapeUtils
	 *
	 * @param html
	 * @return
	 */
	public static String encodeHtml(String html) {
		return StringEscapeUtils.escapeHtml4(html);
	}

	/**
	 * 将字符串进行url编码
	 */
	public static String encodeString(String s, String encoding) {
		try {
			return URLEncoder.encode(s, encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return s;
	}

	/**
	 * 将全角转换成半角
	 *
	 * @param fs
	 * @return
	 * @throws IOException
	 */
	public static String fullToHalf(String fs) {
		StringBuilder outStrBuf = new StringBuilder();
		String tempString = "";
		byte[] b = null;

		try {
			for (int i = 0; i < fs.length(); i++) {
				tempString = fs.substring(i, i + 1);
				if (tempString.equals("　")) {
					outStrBuf.append(" ");
					continue;
				}
				b = tempString.getBytes("unicode");
				if (b[2] == -1) {// 表示全角
					b[3] = (byte) (b[3] + 32);
					b[2] = 0;
					outStrBuf.append(new String(b, "unicode"));
				} else {
					outStrBuf.append(tempString);
				}
			}
		} catch (Exception e) {
			logger.error("Parse Full String(" + fs + ") to Half String Error:" + e.getMessage());
		}

		return outStrBuf.toString();
	}

	/**
	 * 获取随机字符串
	 *
	 * @param cs
	 * @param len
	 * @return
	 */
	public static String getRandomString(String[] cs, int len) {
		Random rd = new Random(System.nanoTime());
		StringBuilder sb = new StringBuilder();
		while (sb.length() < len) {
			for (int i = 0; i < len; i++) {
				int r = rd.nextInt(cs.length);
				sb.append(cs[r]);
			}
		}
		if (sb.length() == len) return sb.toString();
		return sb.substring(0, len);
	}

	/**
	 * @param len
	 * @return
	 */
	public static String getRandomString36(int len) {
		return getRandomString(randomStrings, len);
	}

	/**
	 * 计算subs在s中出现的次数
	 *
	 * @param s
	 * @param subs
	 * @return
	 */
	public static int getCount(String s, String subs) {
		int index = -1;
		int n = 0;
		int slen = subs.length();
		while ((index = s.indexOf(subs, index)) > -1) {
			index += slen;
			n++;
		}
		return n;
	}

	/**
	 * 获取对象的长度
	 *
	 * @param obj 对象(Collection,Map,数组 计算长度)
	 * @return
	 */
	public static int getLength(Object obj) {
		int n = 0;
		if (obj == null) return 0;
		if (obj instanceof String) {
			return ((String) obj).length();
		} else if (obj instanceof Collection) {
			n = ((Collection<?>) obj).size();
		} else if (obj instanceof Map) {
			n = ((Map<?, ?>) obj).size();
		} else if (obj.getClass().isArray()) {
			n = Array.getLength(obj);
		} else n = toString(obj).length();
		return n;
	}

	public static int getLength(String s) {
		if (s == null) return 0;
		return s.length();

	}

	/**
	 * 字算2个长度
	 *
	 * @param obj
	 * @return
	 */
	public static int getLength2(Object obj) {
		int n = 0;
		if (obj == null) return 0;
		if (obj instanceof String) {
			return getLength2((String) obj);
		} else if (obj instanceof Collection) {
			n = ((Collection<?>) obj).size();
		} else if (obj instanceof Map) {
			n = ((Map<?, ?>) obj).size();
		} else if (obj.getClass().isArray()) {
			n = Array.getLength(obj);
		} else n = getLength2(toString(obj));
		return n;
	}

	/**
	 * 字算2个长度
	 *
	 * @param s
	 * @return
	 */
	public static int getLength2(String s) {
		if (s == null) return 0;
		char[] cs = s.toCharArray();
		int tlen = 0;
		for (char c : cs) {
			if (c > 256) tlen += 2;
			else tlen += 1;
		}
		return tlen;
	}

	/**
	 * 获取字符串中指定正则表达式的匹配数组
	 *
	 * @param s     要搜索的字符串
	 * @param regx  正则表达式的字符串
	 * @param index 获取的正则匹配分组下标,默认为0
	 * @return
	 */
	public static List<String> getMatch(String s, Pattern regx, int index) {
		//		Pattern p=Pattern.compile(regx);
		Matcher m = regx.matcher(s);
		List<String> li = new ArrayList<String>();
		while (m.find()) {
			li.add(m.group(index));
		}
		return li;
	}

	/**
	 * 获取匹配,横向获取	String[][组下标]
	 * 不会获取全集匹配(group(0)),如果要获取全集在表达式中加()
	 *
	 * @param s
	 * @param regex
	 * @return
	 */
	public static List<List<String>> getMatchsh(String s, Pattern regex) {
		if (s == null || s.length() < 1) return new ArrayList<List<String>>();
		Matcher m = regex.matcher(s);
		int gcont = m.groupCount();
		List<List<String>> lists = new ArrayList<List<String>>();
		while (m.find()) {
			List<String> temp = new ArrayList<String>();
			for (int i = 0; i < gcont; i++) {
				temp.add(m.group(i + 1));
			}
			lists.add(temp);
		}
		return lists;
	}

	/**
	 * 获取匹配,纵向获取	String[组下标][]
	 * 不会获取全集匹配(group(0)),如果要获取全集在表达式中加()
	 *
	 * @param s
	 * @param regx
	 * @return
	 */
	public static List<List<String>> getMatchsv(String s, Pattern regx) {
		if (s == null || s.length() < 1) return new ArrayList<List<String>>();
		Matcher m = regx.matcher(s);
		int gcont = m.groupCount();
		List<List<String>> lists = new ArrayList<List<String>>();
		boolean init = false;
		while (m.find()) {
			if (init == false) {
				init = true;
				for (int i = 0; i < gcont; i++)
					lists.add(new ArrayList<String>());
			}
			for (int i = 0; i < gcont; i++) {
				lists.get(i).add(m.group(i + 1));
			}
		}
		return lists;
	}

	/**
	 * 是否包含
	 *
	 * @param s
	 * @param tag
	 * @return
	 */
	public static boolean isContains(String s, String tag) {
		if (s == null) {
			return false;
		} else {
			return s.indexOf(tag) != -1;
		}
	}

	/**
	 * 是否以tag开始.  如果s和tag同时空则也返回true
	 *
	 * @param s
	 * @param tag
	 * @return
	 */
	public static boolean isStartWith(String s, String tag) {
		if (checkEmpty(s)) {
			return s.startsWith(tag);
		} else {
			return !checkEmpty(tag);
		}
	}

	/**
	 * 是否以tag开始.  如果s和tag同时空则也返回true
	 *
	 * @param s
	 * @param tag
	 * @return
	 */
	public static boolean isEndWith(String s, String tag) {
		if (checkEmpty(s)) {
			return s.endsWith(tag);
		} else {
			return !checkEmpty(tag);
		}
	}

	/**
	 * 是否以任意一个作为开始. 如果s为空则后面参数任意一个为空则返回true
	 *
	 * @param s
	 * @param tag1
	 * @param tag2
	 * @param tags
	 * @return
	 */
	public static boolean isStartWithOr(String s, String tag1, String tag2, String... tags) {
		if (checkEmpty(s)) {
			if (s.startsWith(tag1) || s.startsWith(tag2)) return true;
			if (tags != null) {
				for (String tag : tags) {
					if (s.startsWith(tag)) return true;
				}
			}
		} else {
			if (!checkEmpty(tag1) || !checkEmpty(tag2)) return true;
			if (tags != null) {
				for (String tag : tags) {
					if (!checkEmpty(tag)) return true;
				}
			}
		}
		return false;
	}

	/**
	 * 判断字符,当字符串为空,为null,为"null"时返回true,不区分大小写
	 *
	 * @return
	 */
	public static boolean isNullString(String s) {
		if (s == null || s == "") return true;
		return "null".equalsIgnoreCase(s);
	}

	/**
	 * 通过utf-8获取bytes.如果有非负byte则返回false
	 *
	 * @param str
	 * @return
	 */
	public static boolean isMessyCodeUTF8(String str) {
		try {
			byte[] bb = str.getBytes("utf-8");
			for (byte b : bb) {
				if (b != 63 && b > 0) return false;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 通过gb2312获取bytes.如果有非负byte则返回false
	 *
	 * @param str
	 * @return
	 */
	public static boolean isMessyCodeGb2312(String str) {
		try {
			byte[] bb = str.getBytes("gb2312");
			for (byte b : bb) {
				if (b != 63 && b > 0) return false;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 通过iso-8859-1获取bytes.如果有非负byte且非63则返回false
	 *
	 * @param str
	 * @return
	 */
	public static boolean isMessyCodeIso(String str) {
		try {
			byte[] bb = str.getBytes("iso-8859-1");
			for (byte b : bb) {
				if (b != 63 && b > 0) return false;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 尝试获取到编码
	 *
	 * @param str
	 * @return
	 */
	public static String tryGetEncoding(String str) {
		if (!isMessyCodeUTF8(str)) return "utf-8";
		if (!isMessyCodeGb2312(str)) return "gb2312";
		if (!isMessyCodeIso(str)) return "iso-8859-1";
		return null;
	}

	/**
	 * 连接重复字符串. 例如("?",3,",")  返回    ?,?,?
	 *
	 * @param s
	 * @param count
	 * @param join
	 * @return
	 */
	public static String joinRepeat(String s, int count, String join) {
		if (count < 1) return "";
		StringBuilder sb = new StringBuilder(s);
		for (int i = 1; i < count; i++) {
			sb.append(join);
			sb.append(s);
		}
		return sb.toString();
	}

	/**
	 * 合并数组,
	 *
	 * @param t
	 * @param sep    分隔符
	 * @param prefix 前缀
	 * @param suffix 后缀
	 * @return
	 */
	public static <T> String join(T[] t, String sep, String prefix, String suffix) {
		StringBuilder sb = new StringBuilder();
		String temp = null;
		if (sep == null) sep = "";
		if (prefix == null) prefix = "";
		if (suffix == null) suffix = "";
		for (int i = 0; i < t.length; i++) {
			if (t[i] == null) temp = "";
			else temp = t[i].toString();
			if (i != 0) sb.append(sep);
			sb.append(prefix);
			sb.append(temp);
			sb.append(suffix);
		}
		return sb.toString();
	}

	/**
	 * 合并数组.非数组对象也能能合并
	 *
	 * @param array
	 * @param sep
	 * @param prefix
	 * @param suffix
	 * @return
	 */
	public static String join(Object array, String sep, String prefix, String suffix) {
		if (array == null) return "";
		if (sep == null) sep = "";
		if (prefix == null) prefix = "";
		if (suffix == null) suffix = "";
		if (array.getClass().isArray()) {
			StringBuilder sb = new StringBuilder();
			Object temp = null;
			for (int i = 0; i < Array.getLength(array); i++) {
				temp = Array.get(array, i);
				if (i != 0) sb.append(sep);
				sb.append(prefix);
				if (temp != null) sb.append(temp);
				sb.append(suffix);
			}
			return sb.toString();
		} else {
			return prefix + array + suffix;
		}
	}

	/**
	 * 合并数组,使用  , 连接  .非数组对象也能能合并
	 *
	 * @param array
	 * @return
	 */
	public static String join(Object array) {
		return join(array, ",", "", "");
	}

	/**
	 * 匹配查找型匹配,input为null时返回false,regex为null时返回true
	 *
	 * @param regex
	 * @param input
	 * @return
	 */
	public static boolean matchFind(Pattern regex, String input) {
		if (input == null) return false;
		if (regex == null) return true;
		return regex.matcher(input).find();
	}

	/**
	 * 匹配查找型匹配,input为null时返回false,regex为null时返回true
	 *
	 * @param regex
	 * @param input
	 * @return
	 */
	public static boolean matchFind(String regex, String input) {
		if (input == null) return false;
		if (regex == null) return true;
		return matchFind(Pattern.compile(regex), input);
	}

	/**
	 * 如果s为空则返回后面的默认字符串
	 *
	 * @param s
	 * @param def
	 * @return
	 */
	public static String checkEmpty(String s, String def) {
		if (checkEmpty(s)) return s;
		return def;
	}

	/**
	 * 如果s为空则返回后面的默认字符串
	 *
	 * @param s
	 * @param def
	 * @return
	 */
	public static String checkDefault(String s, String def) {
		if (checkEmpty(s)) return s;
		return def;
	}

	/**
	 * 将s进行trim,如果为空则返回def
	 *
	 * @param s
	 * @param def
	 * @return
	 */
	public static String checkTrimEmpty(String s, String def) {
		if (s == null) return def;
		s = s.trim();
		if (s.length() == 0) return def;
		return s;
	}

	/**
	 * 获取首个非空字符串,未进行去空格操作,如果全部为空则返回null
	 *
	 * @return
	 */
	public static String notempty(String... ss) {
		if (ss != null && ss.length > 0) {
			for (String s : ss) {
				if (s != null && s.length() > 0) {
					return s;
				}
			}
		}
		return null;
	}

	/**
	 * 获取首个非null对象,如果全部为空则返回null
	 *
	 * @param objs
	 * @return
	 */
	public static Object notnull(Object... objs) {
		if (objs != null && objs.length > 0) {
			for (Object obj : objs) {
				if (obj != null) return obj;
			}
		}
		return null;
	}

	/**
	 * 替换字符串中的字符,用于替代String类中的replace方法
	 *
	 * @param s
	 * @param oc
	 * @param nc
	 * @return
	 */
	public static String replaceChar(String s, char oc, char nc) {
		if (s == null) return "";
		char[] cs = s.toCharArray();
		for (int i = 0; i < cs.length; i++) {
			if (cs[i] == oc) cs[i] = nc;
		}
		return new String(cs);
	}

	/**
	 * 使用string的replace方法,只是增加空判断.null则返回""
	 * cungle	2019-01-08
	 *
	 * @param s
	 * @param oc
	 * @param nc
	 * @return
	 */
	public static String replaceChar(String s, String oc, String nc) {
		if (s == null) return "";
		return s.replace(oc, nc);
	}

	/**
	 * 使用字符串中的字符提醒指定字符
	 * 例子:	("2011-xx-xx","0101",'x')==>2011-01-01
	 * ts		被替换的字符串
	 * fs		用于替换的字符串集合
	 * c		需要替换的字符
	 *
	 * @return
	 */
	public static String replaceCharByString(String ts, String fs, char c) {
		char[] tc = ts.toCharArray();
		char[] fc = fs.toCharArray();
		int index = 0;
		for (int i = 0; i < tc.length; i++) {
			if (tc[i] == c) {
				tc[i] = fc[index++];
				if (index >= fc.length) break;
			}
		}

		return new String(tc);
	}

	/**
	 * 替换对象的html中的符号      #& -<>,."'
	 * 相对sql危险的符号
	 *
	 * @param obj
	 * @return
	 */
	public static String replaceHtml(Object obj) {
		if (obj == null) return "";
		String s = obj.toString();
		s = s.replace("&", "&amp;");
		s = s.replace("#", "&#35;");
		s = s.replace("<", "&lt;");
		s = s.replace(">", "&gt;");
		s = s.replace("\"", "&quot;");
		s = s.replace("'", "&#44;");
		s = s.replace("-", "&#45;");
		s = s.replace(".", "&#46;");
		s = s.replace("/", "&#47;");
		return s;
	}

	/**
	 * 反转字符串
	 *
	 * @param content
	 * @return
	 */
	public static String reverse(String content) {
		return new StringBuilder(content).reverse().toString();
	}

	/**
	 * 判空取默认值     如果s对象为null则返回def,否则返回s.
	 *
	 * @param s
	 * @param def
	 * @return
	 */
	public static Object toObject(Object s, Object def) {
		return s == null ? def : s;
	}

	/**
	 * 将对象转换成boolean,
	 * 如果对象是 null/空  则返回默认
	 * 为Number对象则大于0为真,如果为:"true","t", "y", "yes", "1", "ok", "success" 将返回true
	 *
	 * @param s
	 * @return
	 */
	public static boolean toBoolean(Object s, boolean def) {
		if (checkNull(s)) {
			try {
				if (s instanceof Number) {
					return ((Number) s).intValue() > 0 ? true : false;
				}
				String ss = s.toString();
				if (checkEqualIgnoreCaseOr(ss, "1", "true", "t", "y", "yes", "ok", "success")) {
					return true;
				}
				return Boolean.parseBoolean(ss);
			} catch (Exception e) {
				logger.error("Parse (" + s + ")to boolean error:" + e.getMessage());
			}
		}
		return def;
	}

	public static boolean toBoolean(Object s) {
		return toBoolean(s, false);
	}

	public static Byte toByter(Object s, Byte def) {
		if (checkNull(s)) {
			try {
				if (s instanceof Number) {
					return ((Number) s).byteValue();
				}
				return Byte.parseByte(s.toString());
			} catch (Exception e) {
				logger.error("Parse (" + s + ")to byte error:" + e.getMessage());
			}
		}
		return def;
	}

	/**
	 * 将对象转换成int,为Number对象则直接转换,否则作为字符串处理,异常以日志方式输出
	 *
	 * @param s
	 * @return
	 */
	public static byte toByte(Object s, byte def) {
		if (checkNull(s)) {
			try {
				if (s instanceof Number) {
					return ((Number) s).byteValue();
				}
				return Byte.parseByte(s.toString());
			} catch (Exception e) {
				logger.error("Parse (" + s + ")to byte error:" + e.getMessage());
			}
		}
		return def;
	}

	public static byte toByte(Object s) {
		return toByte(s, (byte) -1);
	}

	/**
	 * 通过转成字符串后获取getBytes();
	 *
	 * @param s
	 * @return
	 */
	public static byte[] toBytes(Object s) {
		if (checkNull(s)) {
			try {
				if (s instanceof String) {
					return ((String) s).getBytes();
				} else if (s instanceof byte[]) {
					return (byte[]) s;
				} else {
					return s.toString().getBytes();
				}
			} catch (Exception e) {
				logger.error("Parse (" + s + ")to byte[] error:" + e.getMessage());
			}
		}
		return new byte[0];
	}

	/**
	 * 将对象转换成double,为Number对象则直接转换,否则作为字符串处理,异常以日志方式输出
	 *
	 * @param s
	 * @return
	 */
	public static double toDouble(Object s, double def) {
		if (checkNull(s)) {
			try {
				if (s instanceof Number) {
					return ((Number) s).doubleValue();
				}
				return Double.parseDouble(s.toString());
			} catch (Exception e) {
				logger.error("Parse (" + s + ")to double error:" + e.getMessage());
			}
		}
		return def;
	}

	public static double toDouble(Object s) {
		return toDouble(s, -1);
	}

	/**
	 * 将对象转换成float,为Number对象则直接转换,否则作为字符串处理,异常以日志方式输出
	 *
	 * @param s
	 * @return
	 */
	public static float toFloat(Object s, float def) {
		if (checkNull(s)) {
			try {
				if (s instanceof Number) {
					return ((Number) s).floatValue();
				}
				return Float.parseFloat(s.toString());
			} catch (Exception e) {
				logger.error("Parse (" + s + ")to float error:" + e.getMessage());
			}
		}
		return def;
	}

	/**
	 * 转换成float,默认返回-1
	 *
	 * @param s
	 * @return
	 */
	public static float toFloat(Object s) {
		return toFloat(s, -1);
	}

	/**
	 * 将对象转换成int,为Number对象则直接转换,否则作为字符串处理,异常以日志方式输出
	 */
	public static int toInt(Object s, int def) {
		if (checkNull(s)) {
			try {
				if (s instanceof Number) {
					return ((Number) s).intValue();
				}
				return Integer.parseInt(s.toString());
			} catch (Exception e) {
				logger.error("Parse (" + s + ")to int error:" + e.getMessage());
			}
		}
		return def;
	}

	public static int toInt(Object s) {
		return toInt(s, -1);
	}

	/**
	 * 将对象转换成long,为Number对象则直接转换,否则作为字符串处理,异常以日志方式输出
	 *
	 * @param s
	 * @return
	 */
	public static long toLong(Object s, long def) {
		if (checkNull(s)) {
			try {
				if (s instanceof Number) {
					return ((Number) s).longValue();
				}
				return Long.parseLong(s.toString());
			} catch (Exception e) {
				logger.error("Parse (" + s + ")to long error:" + e.getMessage());
			}
		}
		return def;
	}

	public static long toLong(Object s) {
		return toLong(s, -1);
	}

	/**
	 * 防止def为空时,转换成int失败
	 *
	 * @param s
	 * @param def
	 * @return
	 */
	public static Integer toInteger(Object s, Integer def) {
		if (checkNull(s)) {
			try {
				if (s instanceof Number) {
					return ((Number) s).intValue();
				}
				//通过double转可以防止1.0这样的格式错误
				return Integer.valueOf((int) Double.parseDouble(s.toString()));
			} catch (Exception e) {
				logger.error("Parse (" + s + ")to int error:" + e.getMessage());
			}
		}
		return def;
	}

	public static Short toShorter(Object s, Short def) {
		if (checkNull(s)) {
			try {
				if (s instanceof Number) {
					return ((Number) s).shortValue();
				}
				return Short.parseShort(s.toString());
			} catch (Exception e) {
				logger.error("Parse (" + s + ")to int error:" + e.getMessage());
			}
		}
		return def;
	}

	/**
	 * 防止失败
	 *
	 * @param s
	 * @param def
	 * @return
	 */
	public static Long toLonger(Object s, Long def) {
		if (checkNull(s)) {
			try {
				if (s instanceof Number) {
					return ((Number) s).longValue();
				}
				//通过double转可以防止1.0这样的格式错误
				return Long.valueOf((long) Double.parseDouble(s.toString()));
			} catch (Exception e) {
				logger.error("Parse (" + s + ")to long error:" + e.getMessage());
			}
		}
		return def;
	}

	public static Float toFloater(Object s, Float def) {
		if (checkNull(s)) {
			try {
				if (s instanceof Number) {
					return ((Number) s).floatValue();
				}
				return Float.parseFloat(s.toString());
			} catch (Exception e) {
				logger.error("Parse (" + s + ")to float error:" + e.getMessage());
			}
		}
		return def;
	}

	public static Double toDoubler(Object s, Double def) {
		if (checkNull(s)) {
			try {
				if (s instanceof Number) {
					return ((Number) s).doubleValue();
				}
				return Double.parseDouble(s.toString());
			} catch (Exception e) {
				logger.error("Parse (" + s + ")to double error:" + e.getMessage());
			}
		}
		return def;
	}

	public static Boolean toBooleaner(Object s, Boolean def) {
		if (checkNull(s)) {
			return Boolean.valueOf(toBoolean(s));
		}
		return def;
	}

	/**
	 * 简单转换字符串
	 *
	 * @param obj
	 * @param def
	 * @return
	 */
	public static String toStringSimple(Object obj, String def) {
		if (obj == null) return def;
		return obj.toString();
	}

	/**
	 * 转换成字符串并进行simple trim
	 *
	 * @param obj
	 * @param def
	 * @return
	 */
	public static String toStringTrim(Object obj, String def) {
		return trimSimple(toString(obj, def));
	}

	/**
	 * 转换成字符串并进行simple trim
	 *
	 * @param obj
	 * @return
	 */
	public static String toStringTrim(Object obj) {
		return trimSimple(toString(obj));
	}

	/**
	 * 将对象转换为字符串,对象为null时,返回空字符串
	 *
	 * @param obj 需要转换的对象
	 * @return
	 */
	public static String toString(Object obj, String def) {
		if (obj != null) {
			return obj.toString();
		}
		return def;
	}

	/**
	 * 如果对象为null,将返回"" 空字符串
	 *
	 * @param obj
	 * @return
	 */
	public static String toString(Object obj) {
		return toString(obj, "");
	}

	/**
	 * 首字母大写
	 *
	 * @param s
	 * @return
	 */
	public static String toFirstUpper(String s) {
		if (s == null || s.length() == 0) return "";
		char[] cs = s.toCharArray();
		cs[0] = Character.toUpperCase(cs[0]);
		return new String(cs);
	}

	/**
	 * 转换成大写
	 *
	 * @param s
	 * @return
	 */
	public static String toUpper(String s) {
		if (s == null) return "";
		return s.toUpperCase();
	}

	/**
	 * 首字母小写
	 *
	 * @param s
	 * @return
	 */
	public static String toFirstLower(String s) {
		if (s == null || s.length() == 0) return "";
		char[] cs = s.toCharArray();
		cs[0] = Character.toLowerCase(cs[0]);
		return new String(cs);
	}

	public static String toLower(String s) {
		if (s == null) return "";
		return s.toLowerCase();
	}

	/**
	 * 编码base64
	 *
	 * @param s
	 * @return
	 */
	public static String encodeBase64(String s) {
		return Base64.getEncoder().encodeToString(s.getBytes());
	}

	/**
	 * 解码base64
	 *
	 * @param s
	 * @return
	 */
	public static String decodeBase64(String s) {
		return new String(Base64.getDecoder().decode(s));
	}

	/**
	 * 创建一个BigDecimal对象.直接使用new的方式
	 *
	 * @param obj
	 * @param def
	 * @return
	 */
	public static BigDecimal toBigDecimal(Object obj, Object def) {
		if (obj == null) return toBigDecimal(def, null);
		return toBigDecimal(obj, toBigDecimal(def, null));
	}

	/**
	 * 创建一个BigDecimal对象
	 *
	 * @param obj
	 * @param def
	 * @return
	 */
	public static BigDecimal toBigDecimal(Object obj, BigDecimal def) {
		if (obj != null) {
			if (obj instanceof BigDecimal) return (BigDecimal) obj;
			if (obj instanceof Number) {
				return BigDecimal.valueOf(((Number) obj).doubleValue());
			}
			String s = obj.toString();
			if (checkEmpty(s)) {
				return new BigDecimal(s);
			}
		}
		return def;
	}

	/**
	 * 转换成 BigInteger
	 *
	 * @param obj
	 * @param def
	 * @return
	 */
	public static BigInteger toBigInteger(Object obj, Object def) {
		if (obj == null) return toBigInteger(def, null);
		return toBigInteger(obj, toBigInteger(def, null));
	}

	/**
	 * 创建一个BigInteger对象
	 *
	 * @param obj
	 * @param def
	 * @return
	 */
	public static BigInteger toBigInteger(Object obj, BigInteger def) {
		if (obj != null) {
			if (obj instanceof BigInteger) return (BigInteger) obj;
			if (obj instanceof Number) {
				return BigInteger.valueOf(((Number) obj).longValue());
			}
			String s = obj.toString();
			if (checkEmpty(s)) {
				return new BigInteger(s);
			}
		}
		return def;
	}

	/**
	 * 去除空格,包含全角空格及其tab等空字符串,空或null字符串会返回""
	 *
	 * @param content
	 * @return
	 */

	public static String trim(String content) {
		if (content == null || content.length() < 1) return "";
		return trimRegex.matcher(content).replaceAll("");
	}

	/**
	 * 此方法进行普通空格的切除,null和空对象会被去掉,返回新数组
	 *
	 * @param ss
	 * @return
	 */
	public static String[] trim(String[] ss) {

		if (ss != null) {
			List<String> list = new ArrayList<String>();
			for (String s : ss) {
				if (s != null) {
					s = s.trim();
					if (s.length() > 0) list.add(s);
				}
			}
			return list.toArray(ss);
		}
		return null;
	}

	/**
	 * 简单的进行去除空格(直接调用字符串trim()),对象为null时返回""
	 *
	 * @param s
	 * @return
	 */
	public static String trimSimple(String s) {
		if (s == null) return "";
		return s.trim();
	}

	/**
	 * 此方法进行普通空格的切除,null和空对象会被去掉,返回新数组
	 *
	 * @param list
	 * @return
	 */
	public static List<String> trim(List<String> list) {
		if (list != null) {
			List<String> result = new ArrayList<String>();
			for (String s : list) {
				if (s != null) {
					s = s.trim();
					if (s.length() > 0) list.add(s);
				}
			}
			return result;
		}
		return null;
	}

	/**
	 * 通过两次切割字符串,转换成键值数组
	 *
	 * @param content
	 * @param reg1
	 * @param reg2
	 * @return
	 */
	public static List<KV> splitKV(String content, String reg1, String reg2) {
		List<KV> list = new ArrayList<KV>();
		String[] ss = content.split(reg1);
		for (String s : ss) {
			if (s != null && s.length() > 0) {

				String[] kv = s.split(reg2);
				if (kv != null && kv.length > 1) {
					String k = kv[0];
					String v = kv[1];
					if (k != null) k = k.trim();
					if (v != null) v = v.trim();
					list.add(new KV(k, v));
				}

			}
		}
		return list;
	}

	/**
	 * 将字符串转换成一行
	 *
	 * @param content
	 * @return
	 */
	public static String toOneLine(String content) {
		if (checkEmpty(content)) {
			return content.replace("\r", "").replace("\n", "");
		}
		return "";
	}

	/**
	 * 此方法效率不高
	 * 去除多余的空格和行.尽量少使用此方法, 使用正则表达式进行替换的.
	 *
	 * @param content
	 * @return
	 */
	public static String compressBlank(String content) {
		content = linePattern1.matcher(content).replaceAll(" ");
		content = linePattern2.matcher(content).replaceAll("\n");
		content = trimSimple(content);
		return content;
	}

	public static int indexOf(String text, String... param) {
		return indexOf(text, 0, param);
	}

	/**
	 * 查找参数首次出现的位置.
	 *
	 * @param param
	 * @return
	 */
	public static int indexOf(String text, int startIndex, String... param) {
		if (param == null || param.length == 0) return -1;
		if (text == null || text.length() == 0) return -1;
		char[] source = text.toCharArray();
		char[][] target = new char[param.length][];
		for (int i = 0, n = param.length; i < n; i++)
			target[i] = param[i].toCharArray();

		for (int ii = startIndex, ni = source.length; ii < ni; ii++) {
			for (int jj = 0, nj = target.length; jj < nj; jj++) {
				char[] t = target[jj];
				if (source.length < t.length) continue;
				boolean f = true;
				for (int index = 0, n = t.length; index < n; index++) {
					if (source[ii + index] != t[index]) {
						f = false;
						break;
					}
				}
				if (f) return ii;
			}

		}
		return -1;
	}

	/**
	 * @param text
	 * @param param
	 * @return
	 */
	public static int lastIndexOf(String text, String... param) {
		return lastIndexOf(text, text.length() - 1, param);
	}

	/**
	 * 查找参数首次出现的位置.
	 *
	 * @param text
	 * @param param
	 * @return
	 */
	public static int lastIndexOf(String text, int startIndex, String... param) {
		if (param == null || param.length == 0) return -1;
		char[] source = text.toCharArray();
		char[][] target = new char[param.length][];
		for (int i = 0, n = param.length; i < n; i++)
			target[i] = param[i].toCharArray();

		for (int ii = startIndex; ii >= 0; ii--) {
			for (int jj = 0, nj = target.length; jj < nj; jj++) {
				char[] t = target[jj];
				if (source.length < t.length) continue;
				boolean f = true;
				for (int index = t.length - 1; index >= 0; index--) {
					if (source[ii + index] != t[index]) {
						f = false;
						break;
					}
				}
				if (f) return ii;
			}

		}
		return -1;
	}

	/**
	 * 将驼峰式命名的字符串转换为下划线方式。如果转换前的驼峰式命名的字符串为空，则返回空字符串。首字母大写的会转换成小写</br>
	 * 例如：AbcEfg => abc_efg
	 *
	 * @param s 转换前的驼峰式命名的字符串
	 * @return 转换后下划线大写方式命名的字符串
	 */
	public static String camelToUnderline(String s) {
		StringBuilder sb = new StringBuilder();
		if (checkEmpty(s)) {
			char[] cs = s.toCharArray();
			char c = cs[0];
			sb.append(Character.toLowerCase(c));
			for (int i = 1, n = cs.length; i < n; i++) {
				c = cs[i];
				if (Character.isUpperCase(c)) { //如果为大写则增加下划线
					sb.append('_');
					sb.append(Character.toLowerCase(c));
				} else {
					sb.append(c);
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 将下划线大写方式命名的字符串转换为驼峰式。如果转换前的下划线大写方式命名的字符串为空，则返回空字符串。注意:其他字母会改变成小写.遇_才会转换成大写</br>
	 * 例如：abc_efg => abcEfg		Abc_efg => abcEfg
	 *
	 * @param s 转换前的下划线大写方式命名的字符串
	 * @return 转换后的驼峰式命名的字符串
	 */
	public static String underlineToCamel(String s) {
		StringBuilder sb = new StringBuilder();
		if (checkEmpty(s)) {
			char[] cs = s.toCharArray();

			boolean upper = false;
			for (char c : cs) {
				if (c == '_') {
					upper = true;
					continue;
				}
				if (upper) {
					c = Character.toUpperCase(c);
					upper = false;
				} else {
					c = Character.toLowerCase(c);
				}

				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * 通配时使用
	 *
	 * @param text
	 * @return
	 */
	private static String[] splitOnTokens(String text) {
		if (text.indexOf('?') == -1 && text.indexOf('*') == -1) {
			return new String[]{text};
		}

		char[] array = text.toCharArray();
		ArrayList<String> list = new ArrayList<String>();
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			if (array[i] == '?' || array[i] == '*') {
				if (buffer.length() != 0) {
					list.add(buffer.toString());
					buffer.setLength(0);
				}
				if (array[i] == '?') {
					list.add("?");
				} else if (list.isEmpty() || i > 0 && list.get(list.size() - 1).equals("*") == false) {
					list.add("*");
				}
			} else {
				buffer.append(array[i]);
			}
		}
		if (buffer.length() != 0) {
			list.add(buffer.toString());
		}

		return list.toArray(new String[list.size()]);
	}

	/**
	 * 通配时使用
	 *
	 * @param ignoreCase
	 * @param str
	 * @param strStartIndex
	 * @param search
	 * @return
	 */
	private static int checkIndexOf(String str, int strStartIndex, String search, boolean ignoreCase) {
		int endIndex = str.length() - search.length();
		if (endIndex >= strStartIndex) {
			for (int i = strStartIndex; i <= endIndex; i++) {
				if (str.regionMatches(ignoreCase, i, search, 0, search.length())) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * 判断是否是通配.是否出现  *  或者  ?
	 *
	 * @param value
	 * @return
	 */
	public static boolean isWildcard(String value) {
		return value.indexOf('*') > -1 || value.indexOf('?') > -1;
	}

	/**
	 * 通配符匹配,匹配 *(0或者多个字符)和?(1个字符)    支持中文,1个字等同1个字符
	 *
	 * @param value           原始字符串
	 * @param wildcardMatcher 匹配的模式字符串
	 * @param ignoreCase      是否
	 * @return
	 */
	public static boolean wildcardMatch(String value, String wildcardMatcher, boolean ignoreCase) {
		if (value == null && wildcardMatcher == null) {
			return true;
		}
		if (value == null || wildcardMatcher == null) {
			return false;
		}
		String[] wcs = splitOnTokens(wildcardMatcher);
		boolean anyChars = false;
		int textIdx = 0;
		int wcsIdx = 0;
		Stack<int[]> backtrack = new Stack<int[]>();
		do {
			if (backtrack.size() > 0) {
				int[] array = backtrack.pop();
				wcsIdx = array[0];
				textIdx = array[1];
				anyChars = true;
			}

			while (wcsIdx < wcs.length) {

				if (wcs[wcsIdx].equals("?")) {
					// ? so move to next text char
					textIdx++;
					if (textIdx > value.length()) {
						break;
					}
					anyChars = false;

				} else if (wcs[wcsIdx].equals("*")) {
					// set any chars status
					anyChars = true;
					if (wcsIdx == wcs.length - 1) {
						textIdx = value.length();
					}

				} else {
					// matching text token
					if (anyChars) {
						// any chars then try to locate text token
						//						textIdx = caseSensitivity.checkIndexOf(filename, textIdx, wcs[wcsIdx]);
						textIdx = checkIndexOf(value, textIdx, wcs[wcsIdx], ignoreCase);
						if (textIdx == -1) {
							// token not found
							break;
						}
						//						int repeat = caseSensitivity.checkIndexOf(filename, textIdx + 1, wcs[wcsIdx]);
						int repeat = checkIndexOf(value, textIdx + 1, wcs[wcsIdx], ignoreCase);
						if (repeat >= 0) {
							backtrack.push(new int[]{wcsIdx, repeat});
						}
					} else {
						// matching from current position
						//						if (!caseSensitivity.checkRegionMatches(filename, textIdx, wcs[wcsIdx])) {
						if (!value.regionMatches(ignoreCase, textIdx, wcs[wcsIdx], 0, wcs[wcsIdx].length())) {
							// couldnt match token
							break;
						}
					}

					// matched text token, move text index to end of matched token
					textIdx += wcs[wcsIdx].length();
					anyChars = false;
				}

				wcsIdx++;
			}

			// full match
			if (wcsIdx == wcs.length && textIdx == value.length()) {
				return true;
			}

		} while (backtrack.size() > 0);

		return false;
	}

	/**
	 * 分隔字符串.如果不能分割则返回原始对象
	 *
	 * @param str
	 * @return
	 */
	public static String[] split(String str, String sep) {
		if (checkEmpty(str)) {
			return str.split(sep);
		}
		return new String[0];
	}

	/**
	 * 获取byte[],如果对象为null则返回内容为byte[0]
	 * cungle	2019-01-14
	 *
	 * @param s
	 * @param charset
	 * @return
	 */
	public static byte[] getBytes(String s, Charset charset) {
		if (s == null) return new byte[0];
		return s.getBytes(charset);
	}

	public static byte[] getBytes(String s, String charset) {
		return getBytes(charset, Charset.forName(charset));
	}

	/**
	 * 判断是否是有bom格式
	 *
	 * @param buf
	 * @return
	 */
	public static boolean checkBom(byte[] buf) {
		if (buf != null && buf.length >= 3) {
			if (buf[0] == -17 && buf[1] == -69 && buf[2] == -65) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 移除内容中的Bom
	 *
	 * @param buf
	 * @return
	 */
	public static byte[] removeBom(byte[] buf) {
		if (checkBom(buf)) {
			return Arrays.copyOfRange(buf, 3, buf.length);
		}
		return buf;
	}

	public static final String EMPTY_JSON = "{}";
	public static final char C_BACKSLASH = '\\';
	public static final char C_DELIM_START = '{';

	/**
	 * 复制与HuTool
	 * 格式化字符串<br>
	 * 此方法只是简单将占位符 {} 按照顺序替换为参数<br>
	 * 如果想输出 {} 使用 \\转义 { 即可，如果想输出 {} 之前的 \ 使用双转义符 \\\\ 即可<br>
	 * 例：<br>
	 * 通常使用：format("this is {} for {}", "a", "b") =》 this is a for b<br>
	 * 转义{}： format("this is \\{} for {}", "a", "b") =》 this is \{} for a<br>
	 * 转义\： format("this is \\\\{} for {}", "a", "b") =》 this is \a for b<br>
	 *
	 * @param strPattern 字符串模板
	 * @param argArray   参数列表
	 * @return 结果
	 */
	public static String format(final String strPattern, final Object... argArray) {
		if (argArray.length == 0) {
			return strPattern;
		}
		final int strPatternLength = strPattern.length();

		// 初始化定义好的长度以获得更好的性能
		StringBuilder sbuf = new StringBuilder(strPatternLength + 50);

		int handledPosition = 0;// 记录已经处理到的位置
		int delimIndex;// 占位符所在位置
		for (int argIndex = 0; argIndex < argArray.length; argIndex++) {
			delimIndex = strPattern.indexOf(EMPTY_JSON, handledPosition);
			if (delimIndex == -1) {// 剩余部分无占位符
				if (handledPosition == 0) { // 不带占位符的模板直接返回
					return strPattern;
				}
				// 字符串模板剩余部分不再包含占位符，加入剩余部分后返回结果
				sbuf.append(strPattern, handledPosition, strPatternLength);
				return sbuf.toString();
			}

			// 转义符
			if (delimIndex > 0 && strPattern.charAt(delimIndex - 1) == C_BACKSLASH) {// 转义符
				if (delimIndex > 1 && strPattern.charAt(delimIndex - 2) == C_BACKSLASH) {// 双转义符
					// 转义符之前还有一个转义符，占位符依旧有效
					sbuf.append(strPattern, handledPosition, delimIndex - 1);
					sbuf.append(toString(argArray[argIndex], ""));
					handledPosition = delimIndex + 2;
				} else {
					// 占位符被转义
					argIndex--;
					sbuf.append(strPattern, handledPosition, delimIndex - 1);
					sbuf.append(C_DELIM_START);
					handledPosition = delimIndex + 1;
				}
			} else {// 正常占位符
				sbuf.append(strPattern, handledPosition, delimIndex);
				sbuf.append(toString(argArray[argIndex], null));
				handledPosition = delimIndex + 2;
			}
		}
		// 加入最后一个占位符后所有的字符
		sbuf.append(strPattern, handledPosition, strPattern.length());
		return sbuf.toString();
	}

	/**
	 * 分隔字符串
	 *
	 * @param str
	 * @return
	 */
	public static List<String> splitToList(String str, String sep) {
		return new ArrayList<>(Arrays.asList(split(str, sep)));
	}

	private static Pattern linePattern1 = Pattern.compile(" +");
	private static Pattern linePattern2 = Pattern.compile("((\n)|( \n))+");

	public static class KV {
		private String k;
		private String v;

		public KV(String k, String v) {
			this.k = k;
			this.v = v;
		}

		public String getK() {
			return k;
		}

		public void setK(String k) {
			this.k = k;
		}

		public String getV() {
			return v;
		}

		public void setV(String v) {
			this.v = v;
		}

		/**
		 * 获取int型k
		 *
		 * @return
		 */
		public int getKInt() {
			return toInt(k);
		}

		/**
		 * 获取long型k
		 *
		 * @return
		 */
		public long getKLong() {
			return toLong(k);
		}

		/**
		 * 获取int型v
		 *
		 * @return
		 */
		public int getVInt() {
			return toInt(v);
		}

		/**
		 * @return
		 */
		public long getVLong() {
			return toLong(v);
		}
	}

	/**
	 * 获取字符串第N此出现的位置
	 *
	 * @param string
	 * @param i
	 * @param character
	 * @return
	 */
	public static int getCharacterPosition(String string, int i, String character) {
		Matcher slashMatcher = Pattern.compile(character).matcher(string);
		int mIdx = 0;
		while (slashMatcher.find()) {
			mIdx++;
			if (mIdx == i) {
				break;
			}
		}
		return slashMatcher.start();
	}
}
