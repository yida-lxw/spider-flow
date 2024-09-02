package org.spiderflow.core.utils.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spiderflow.core.constants.Constants;
import org.spiderflow.core.enums.FileSizeUnit;
import org.spiderflow.core.utils.StringUtils;
import org.spiderflow.core.utils.file.filter.CanReadDirectoryFileFilter;
import org.spiderflow.core.utils.file.filter.FileNameExcludeFileFilter;
import org.spiderflow.core.utils.file.filter.FileNamePreffixFileFilter;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileChannel;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 文件处理工具类
 *
 * @author ruoyi
 */
public class FileUtils {
	private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

	public static String FILENAME_PATTERN = "[a-zA-Z0-9_\\-\\|\\.\\u4e00-\\u9fa5]+";

	/**
	 * @param sourceFilePath
	 * @param destFilePath
	 * @description 文件复制
	 * @author yida
	 * @date 2024-01-19 19:12:31
	 */
	public static void copyFileUsingFileChannels(String sourceFilePath, String destFilePath) {
		FileChannel inputChannel = null;
		FileChannel outputChannel = null;
		try {
			inputChannel = new FileInputStream(sourceFilePath).getChannel();
			outputChannel = new FileOutputStream(destFilePath).getChannel();
			outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
		} catch (Exception e) {
			logger.error("As calling the copyFileUsingFileChannels method, occur exception:\n{}", e.getMessage());
		} finally {
			try {
				inputChannel.close();
			} catch (Exception e) {
				logger.error("As closing the input FileChannel, occur exception:\n{}", e.getMessage());
			}
			try {
				outputChannel.close();
			} catch (Exception e) {
				logger.error("As closing the output FileChannel, occur exception:\n{}", e.getMessage());
			}
		}
	}

	/**
	 * @param content
	 * @param targetFilePath
	 * @param charsetName
	 * @param append
	 * @description 将字符串写入文件
	 * @author yida
	 * @date 2024-01-30 13:38:31
	 */
	public static boolean write2File(String content, String targetFilePath, String charsetName, boolean append) {
		if (StringUtils.isEmpty(charsetName)) {
			charsetName = Constants.DEFAULT_CHARSET;
		}
		boolean result = true;
		try (BufferedWriter bufferedWriter = new BufferedWriter(new CustomFileWriter(targetFilePath, charsetName, append))) {
			bufferedWriter.write(content);
		} catch (Exception e) {
			result = false;
			logger.error("As write the content into the file:[{}], occur exception:\n{}", targetFilePath, e.getMessage());
		}
		return result;
	}

	/**
	 * @param content
	 * @param targetFilePath
	 * @param append
	 * @description 将字符串写入文件
	 * @author yida
	 * @date 2024-01-30 13:38:31
	 */
	public static boolean write2File(String content, String targetFilePath, boolean append) {
		return write2File(content, targetFilePath, null, append);
	}

	public static boolean write2File(String content, String targetFilePath, String charsetName) {
		return write2File(content, targetFilePath, charsetName, false);
	}

	public static boolean write2File(String content, String targetFilePath) {
		return write2File(content, targetFilePath, null, false);
	}

	/**
	 * @param filePath
	 * @description 读取指定文本文件中的内容
	 * @author yida
	 * @date 2024-02-01 16:42:50
	 */
	public static String readAsString(String filePath) {
		return readAsString(filePath, null);
	}

	/**
	 * @param filePath
	 * @param charsetName
	 * @description 读取指定文本文件中的内容
	 * @author yida
	 * @date 2024-02-01 16:42:50
	 */
	public static String readAsString(String filePath, String charsetName) {
		if (StringUtils.isEmpty(charsetName)) {
			charsetName = Constants.DEFAULT_CHARSET;
		}
		StringBuilder stringBuilder = new StringBuilder();
		try (FileInputStream fis = new FileInputStream(filePath);
			 InputStreamReader isr = new InputStreamReader(fis, charsetName);
			 BufferedReader bufferedReader = new BufferedReader(isr)) {
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line).append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stringBuilder.toString().trim();
	}

	public static List<File> getFileList(String directory, FilenameFilter filenameFilter) {
		File dir = new File(directory);
		if (dir.isFile()) {
			return null;
		}
		File[] fileArray = dir.listFiles(filenameFilter);
		if (null == fileArray || fileArray.length <= 0) {
			return null;
		}
		List<File> fileList = Arrays.asList(fileArray);
		return fileList;
	}

	public static boolean deleteFile(String filePath) {
		File file = new File(filePath);
		if (!file.isFile() || !file.exists()) {
			return false;
		}
		try {
			return file.delete();
		} catch (Exception e) {
			logger.error("As deleting the file:[{}] occur exception:\n{}.", filePath, e.getMessage());
		 	return false;
		}
	}

	/**
	 * 输出指定文件的byte数组
	 *
	 * @param filePath 文件路径
	 * @param os       输出流
	 * @return
	 */
	public static void writeBytes(String filePath, OutputStream os) throws IOException {
		FileInputStream fis = null;
		try {
			File file = new File(filePath);
			if (!file.exists()) {
				throw new FileNotFoundException(filePath);
			}
			fis = new FileInputStream(file);
			byte[] b = new byte[1024];
			int length;
			while ((length = fis.read(b)) > 0) {
				os.write(b, 0, length);
			}
		} catch (IOException e) {
			throw e;
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	/**
	 * 文件名称验证
	 *
	 * @param filename 文件名称
	 * @return true 正常 false 非法
	 */
	public static boolean isValidFilename(String filename) {
		return filename.matches(FILENAME_PATTERN);
	}

	private static final String schema = "file:///";
	private static final int keysize = 128;
	private static final String KEY = "9de1cdaa-0bea-4d1d-935c-af9559d2dd76";

	private static final String algorithm = "AES";
	private static final String algorithm_nopadding = "AES/CBC/PKCS5Padding";
	private static final String charset = "utf-8";

	private static final int BUFFER_SIZE = 1024 * 1024;
	private static final int KEY_ITERATIONS = 65535;
	private static final int DEFAULT_KEY_BITS = 128;

	private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
	private static final String TRANSFORMATION = "AES";
	private static final String PBKDF_2_WITH_HMAC_SHA_256 = "PBKDF2WithHmacSHA256";
	private static final int IV_SIZE = 16;

	/**
	 * @param filePath
	 * @return String
	 * @description 从文件路径中获取文件名称(包含文件后缀名)
	 * @author yida
	 * @date 2023-08-07 18:34:54
	 */
	public static String getFileName(String filePath) {
		if (filePath.contains("\\")) {
			filePath = filePath.replaceAll("\\\\", "/");
		}
		return filePath.substring(filePath.lastIndexOf("/") + 1);
	}

	/**
	 * @param fileName
	 * @return String
	 * @description 获取文件名称(不包含后缀名)
	 * @author yida
	 * @date 2023-08-07 18:46:29
	 */
	public static String getFileNameWithoutSuffix(String fileName) {
		return fileName.substring(0, fileName.lastIndexOf("."));
	}

	public static boolean copyFile(String sourceFilePath, String destinationFilePath) {
		sourceFilePath = replaceBackSlash(sourceFilePath);
		destinationFilePath = replaceBackSlash(destinationFilePath);
		File file = new File(sourceFilePath);
		if (!file.exists()) {
			return false;
		}
		long fileSize = file.length();
		return copyFile(sourceFilePath, destinationFilePath, fileSize);
	}

	public static boolean copyFile(String sourceFilePath, String destinationFilePath, long fileSize) {
		boolean copyResult = false;
		if (fileSize > Constants.DEFAULT_2G_FILE_SIZE) {
			copyResult = copyBigFile(sourceFilePath, destinationFilePath);
			logger.info("File:[{}] was found which was large file with greater than 2g file-size, and was copied to [{}] with [{}] method {}.",
					sourceFilePath, destinationFilePath, "copyBigFile", copyResult ? "successfully" : "failed");
		} else {
			copyResult = copySmallFile(sourceFilePath, destinationFilePath);
			logger.info("File:[{}] was found which was large file with less than 2g file-size, and was copied to [{}] with [{}] method {}.",
					sourceFilePath, destinationFilePath, "copyFile", copyResult ? "successfully" : "failed");
		}
		return copyResult;
	}

	/**
	 * @param sourceFilePath
	 * @param destinationFilePath
	 * @return boolean
	 * @description 零拷贝方式复制文件(适合于小文件, 小于2G)
	 * @author yida
	 * @date 2024-03-20 16:02:46
	 */
	public static boolean copySmallFile(String sourceFilePath, String destinationFilePath) {
		sourceFilePath = replaceBackSlash(sourceFilePath);
		destinationFilePath = replaceBackSlash(destinationFilePath);
		String destparentPath = destinationFilePath.substring(0, destinationFilePath.lastIndexOf("/"));
		boolean result = true;
		File destFile = new File(destinationFilePath);
		if (!destFile.exists()) {
			try {
				File parentFolder = new File(destparentPath);
				if (!parentFolder.exists()) {
					parentFolder.mkdirs();
				}
			} catch (Exception e) {
				logger.error("Make the parent folder:[{}] for the destinationFile:[{}] failed.", destparentPath, destinationFilePath);
			}
		}
		try (FileChannel fromChannel = new FileInputStream(sourceFilePath).getChannel();
			 FileChannel toChannel = new FileOutputStream(destinationFilePath).getChannel();
		) {
			// 效率高，底层会利用操作系统的零拷贝进行优化，上限是 2g 数据
			//fromChannel.transferTo(0, fromChannel.size(), toChannel);
			long size = fromChannel.size();
			for (long left = size; left > 0; ) {
				left -= fromChannel.transferTo((size - left), left, toChannel);
			}
		} catch (IOException e) {
			logger.error("while transferring sourceFile:[{}] to destFile:[{}], but occur exception.", sourceFilePath, destinationFilePath);
			result = false;
		}
		return result;
	}

	/**
	 * @param sourceFilePath
	 * @param destinationFilePath
	 * @return boolean
	 * @description 通过直接IO异步复制文件(适合于大文件, 2G或2G以上)
	 * @author yida
	 * @date 2024-03-20 16:18:57
	 */
	public static boolean copyBigFile(String sourceFilePath, String destinationFilePath) {
		sourceFilePath = replaceBackSlash(sourceFilePath);
		destinationFilePath = replaceBackSlash(destinationFilePath);
		String destparentPath = destinationFilePath.substring(0, destinationFilePath.lastIndexOf("/"));
		Path source = Paths.get(sourceFilePath);
		Path target = Paths.get(destinationFilePath);
		boolean result = true;
		File destFile = new File(destinationFilePath);
		if (!destFile.exists()) {
			try {
				File parentFolder = new File(destparentPath);
				if (!parentFolder.exists()) {
					parentFolder.mkdirs();
				}
			} catch (Exception e) {
				logger.error("Make the parent folder:[{}] for the destinationFile:[{}] failed.", destparentPath, destinationFilePath);
			}
		}
		try (AsynchronousFileChannel sourceChannel = AsynchronousFileChannel.open(source, StandardOpenOption.READ);
			 AsynchronousFileChannel targetChannel = AsynchronousFileChannel.open(target, new OpenOption[]{
					 StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING,
					 StandardOpenOption.CREATE});
		) {
			ByteBuffer buffer = ByteBuffer.allocate(1 * 1024 * 1024);
			long readPos = 0L;
			BlockingQueue<Boolean> blockingQueue = new ArrayBlockingQueue<>(1);
			do {
				Future<Integer> future = sourceChannel.read(buffer, readPos);
				Integer readByteSize = future.get();
				if (readByteSize <= 0) {
					break;
				}
				buffer.flip();
				targetChannel.write(buffer, readPos, blockingQueue, new CompletionHandler<Integer, BlockingQueue<Boolean>>() {
					@Override
					public void completed(Integer result, BlockingQueue<Boolean> attachment) {
						try {
							attachment.put(true);
						} catch (InterruptedException e) {
						}
					}

					@Override
					public void failed(Throwable exc, BlockingQueue<Boolean> attachment) {

					}
				});
				blockingQueue.take();
				readPos += Long.valueOf(readByteSize);
				buffer.clear();
			} while (readPos > 0L);
		} catch (Exception e) {
			logger.error("copy big file:{} to {} occur exception:\n{}.", sourceFilePath, destinationFilePath, e.getMessage());
			result = false;
		}
		return result;
	}

	/**
	 * @param sourceFolderPath
	 * @param targetFolderPath
	 * @description 目录复制(递归模式)
	 * @author yida
	 * @date 2024-08-14 18:23:35
	 */
	public static void copyDirectory(String sourceFolderPath, String targetFolderPath) {
		File sourceFile = new File(sourceFolderPath);
		if (!sourceFile.canRead()) {
			logger.info("Orignal folder:[{}]can't be read, so we can't copy it.", sourceFolderPath);
		} else {
			File targetFile = new File(targetFolderPath);
			if (!targetFile.exists()) {
				targetFile.mkdirs();
			}
			File[] files = sourceFile.listFiles();
			for (int i = 0; i < files.length; i++) {
				File curFile = files[i];
				String curFileName = curFile.getName();
				String curFilePath = curFile.getAbsolutePath();
				if (curFile.isFile()) {
					copyFileUsingFileChannels(curFilePath, targetFolderPath + "/" + curFileName);
				} else if (files[i].isDirectory()) {
					copyDirectory(curFilePath, targetFolderPath + "/" + curFileName);
				}
			}
		}
	}

	public static List<File> getFileList(String directory) {
		File dir = new File(directory);
		if (dir.isFile()) {
			return null;
		}
		File[] fileArray = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				String dirPath = dir.getAbsolutePath();
				dirPath = dirPath.replaceAll("\\\\", "/");
				if (!dirPath.endsWith("/")) {
					dirPath = dirPath + "/";
				}
				String imageFilePath = dirPath + name;
				File imageFile = new File(imageFilePath);
				boolean isFile = imageFile.isFile();
				if (!isFile) {
					return false;
				}
				return true;
			}
		});
		if (null == fileArray || fileArray.length <= 0) {
			return null;
		}
		List<File> fileList = new ArrayList<>();
		for (File file : fileArray) {
			fileList.add(file);
		}
		return fileList;
	}

	public static void encryptFile(String src, String dest, String password, boolean deleteOrignalFile) {
		try (InputStream inputStream = new FileInputStream(src);
			 OutputStream outputStream = new FileOutputStream(dest)) {
			int pwdLen = password.length();
			byte[] salt = new byte[pwdLen];
			for (int i = 0; i < pwdLen; i = i + 1) {
				salt[i] = ((byte) password.charAt(i));
			}

			char[] passwordCharArray = password.toCharArray();
			SecretKeyFactory factory = SecretKeyFactory.getInstance(PBKDF_2_WITH_HMAC_SHA_256);

			KeySpec spec = new PBEKeySpec(passwordCharArray, salt, KEY_ITERATIONS, DEFAULT_KEY_BITS);
			SecretKeySpec secretKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), TRANSFORMATION);
			Cipher ecipher = Cipher.getInstance(ALGORITHM);
			try (CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, ecipher)) {
				byte[] iv = new byte[IV_SIZE];
				SecureRandom random = new SecureRandom();
				random.nextBytes(iv);
				IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
				outputStream.write(iv, 0, IV_SIZE);
				ecipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
				byte[] buffer = new byte[BUFFER_SIZE];
				int bytesRead;
				while ((bytesRead = inputStream.read(buffer)) >= 0) {
					cipherOutputStream.write(buffer, 0, bytesRead);
				}
			}
			if (deleteOrignalFile) {
				File srcFile = new File(src);
				if (srcFile.exists()) {
					srcFile.delete();
					logger.info("We had deleted the orignal decrypted file:[{}] successfully.", src);
				}
			}
		} catch (Exception e) {
			logger.error("As encrypt the source file:[{}] with password:[{}], but occur exception:[{}]", src, password, e.getMessage());
		}
	}

	public static void decryptFile(String srcFile, String destFile, String password, boolean deleteOrignalFile) {
		try (InputStream is = new FileInputStream(srcFile);
			 OutputStream out = new FileOutputStream(destFile)) {
			int pwdLen = password.length();
			byte[] salt = new byte[pwdLen];
			for (int i = 0; i < pwdLen; i = i + 1) {
				salt[i] = ((byte) password.charAt(i));
			}

			char[] passwordCharArray = password.toCharArray();
			SecretKeyFactory factory = SecretKeyFactory.getInstance(PBKDF_2_WITH_HMAC_SHA_256);

			KeySpec spec = new PBEKeySpec(passwordCharArray, salt, KEY_ITERATIONS, DEFAULT_KEY_BITS);
			SecretKeySpec secretKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), TRANSFORMATION);
			Cipher dcipher = Cipher.getInstance(ALGORITHM);
			try (CipherInputStream cis = new CipherInputStream(is, dcipher)) {
				byte[] iv = new byte[IV_SIZE];
				is.read(iv, 0, IV_SIZE);
				IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
				dcipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

				byte[] buffer = new byte[1024 * 1024];
				int bytesRead;

				while ((bytesRead = cis.read(buffer)) >= 0) {
					out.write(buffer, 0, bytesRead);
				}
			}
			if (deleteOrignalFile) {
				File sourceFile = new File(srcFile);
				if (sourceFile.exists()) {
					sourceFile.delete();
					logger.info("We had deleted the orignal encrypted file:[{}] successfully.", srcFile);
				}
			}
		} catch (Exception e) {
			logger.error("As decrypt the source file:[{}] with password:[{}], but occur exception:[{}]", srcFile, password, e.getMessage());
		}
	}

	/**
	 * @param orignalFilePath
	 * @param targetFolderPath
	 * @return boolean
	 * @description 实现文件剪切
	 * @author yida
	 * @date 2024-07-15 15:06:12
	 */
	public static boolean cutFile(String orignalFilePath, String targetFolderPath) throws IOException {
		File orignalFile = new File(orignalFilePath);
		if (!orignalFile.exists()) {
			return false;
		}
		File targetFolder = new File(targetFolderPath);
		if (!targetFolder.exists()) {
			targetFolder.mkdirs();
		}
		try {
			org.apache.commons.io.FileUtils.copyFileToDirectory(orignalFile, targetFolder);
			org.apache.commons.io.FileUtils.forceDelete(orignalFile);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * @param orignalFilePath
	 * @param targetFilePath
	 * @return boolean
	 * @description 文件重命名
	 * @author yida
	 * @date 2024-07-15 15:18:58
	 */
	public static boolean renameFile(String orignalFilePath, String targetFilePath) throws IOException {
		File sourceFile = new File(orignalFilePath);
		File targetFile = new File(targetFilePath);
		boolean result = false;
		try {
			org.apache.commons.io.FileUtils.moveFile(sourceFile, targetFile);
			result = true;
		} catch (Exception e) {
			result = false;
		}
		return result;
	}


	public static String encrypt(String res) {
		return keyGenerator(res, true);
	}

	public static String decrypt(String res) {
		return keyGenerator(res, false);
	}

	private static String parseByte2HexStr(byte[] buf) {
		StringBuilder sb = new StringBuilder();
		for (byte b : buf) {
			String hex = Integer.toHexString(b & 255);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}

	private static byte[] parseHexStr2Byte(String hexStr) {
		if (hexStr.length() < 1) {
			return null;
		} else {
			byte[] result = new byte[hexStr.length() / 2];

			for (int i = 0; i < hexStr.length() / 2; ++i) {
				int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
				int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
				result[i] = (byte) (high * 16 + low);
			}

			return result;
		}
	}

	private static String keyGenerator(String res, boolean encrypt) {
		try {
			KeyGenerator kg = KeyGenerator.getInstance(algorithm);
			if (keysize == 0) {
				SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
				secureRandom.setSeed(FileUtils.KEY.getBytes(charset));
				kg.init(keysize, secureRandom);
			} else {
				SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
				secureRandom.setSeed(FileUtils.KEY.getBytes(charset));
				kg.init(keysize, secureRandom);
			}

			SecretKey sk = kg.generateKey();
			SecretKeySpec sks = new SecretKeySpec(sk.getEncoded(), algorithm);
			Cipher cipher = Cipher.getInstance(algorithm);
			if (encrypt) {
				cipher.init(Cipher.ENCRYPT_MODE, sks);
				byte[] resBytes = res.getBytes(charset);
				return parseByte2HexStr(cipher.doFinal(resBytes));
			} else {
				cipher.init(Cipher.DECRYPT_MODE, sks);
				return new String(cipher.doFinal(Objects.requireNonNull(parseHexStr2Byte(res))));
			}
		} catch (Exception var10) {
			var10.printStackTrace();
			return null;
		}
	}

	/**
	 * @param path
	 * @return String
	 * @description 获取盘符
	 * @author yida
	 * @date 2023-04-23 18:44:39
	 */
	public static String diskName(String path) {
		int len = schema.length();
		if (path.startsWith(schema)) {
			path = path.substring(len);
		}
		path = path.substring(0, path.indexOf(":"));
		return path;
	}

	/**
	 * @param path
	 * @return String
	 * @description 移除掉路径中的盘符部分
	 * @author yida
	 * @date 2023-04-23 18:50:05
	 */
	public static String removeDiskPrefix(String path) {
		int len = schema.length();
		if (path.startsWith(schema)) {
			path = path.substring(len);
		}
		path = StringUtils.replaceBackSlash(path);
		return path.substring(path.indexOf("/") + 1);

	}

	/**
	 * @param path
	 * @return {@link List}
	 * @description 将路径字符串按照斜杠切分开以List形式返回
	 * @author yida
	 * @date 2023-04-23 20:38:11
	 */
	public static List<String> splitDirPath(String path, String baseDir) {
		path = StringUtils.replaceBackSlash(path);
		if (path.endsWith("/")) {
			path = path.substring(0, path.lastIndexOf("/"));
		}
		if (StringUtils.isNotEmpty(baseDir)) {
			baseDir = StringUtils.replaceBackSlash(baseDir);
			if (!baseDir.endsWith("/")) {
				baseDir = baseDir + "/";
			}
			path = path.substring(path.lastIndexOf(baseDir) + baseDir.length());
		}
		String[] dirArray = path.split("/");
		if (null == dirArray || dirArray.length <= 0) {
			return null;
		}
		if (dirArray.length == 1 && StringUtils.isEmpty(dirArray[0])) {
			return null;
		}
		return Arrays.asList(dirArray);
	}

	public static String beautifyFileSizeShowText(BigDecimal fileSize) {
		return beautifyFileSizeShowText(fileSize, null);
	}

	/**
	 * @param fileSize     文件体积的数值(若fileSizeUnit参数未指定，则默认判定单位为字节)
	 * @param fileSizeUnit fileSize参数对应的单位
	 * @description 文件体积转换为合适的单位进行显示
	 * @author yida
	 * @date 2023-09-03 14:43:49
	 */
	public static String beautifyFileSizeShowText(BigDecimal fileSize, FileSizeUnit fileSizeUnit) {
		if (null == fileSizeUnit) {
			fileSizeUnit = FileSizeUnit.BYTE;
		}
		BigDecimal fileSizeInBytes = null;
		if (FileSizeUnit.BYTE.equals(fileSizeUnit)) {
			fileSizeInBytes = fileSize;
		} else if (FileSizeUnit.KB.equals(fileSizeUnit)) {
			fileSizeInBytes = fileSize.multiply(Constants.ONE_UNIT);
		} else if (FileSizeUnit.MB.equals(fileSizeUnit)) {
			fileSizeInBytes = fileSize.multiply(Constants.TWO_UNIT);
		} else if (FileSizeUnit.GB.equals(fileSizeUnit)) {
			fileSizeInBytes = fileSize.multiply(Constants.THREE_UNIT);
		} else if (FileSizeUnit.TB.equals(fileSizeUnit)) {
			fileSizeInBytes = fileSize.multiply(Constants.FOUR_UNIT);
		} else {
			fileSizeInBytes = fileSize.multiply(Constants.FIVE_UNIT);
		}

		double actualFileSizeInBytes = fileSizeInBytes.doubleValue();
		if (actualFileSizeInBytes < Constants.ONE_KB_BYTES) {
			String actualFileSizeInBytes2ShowText = fileSizeInBytes.setScale(Constants.DEFAULT_BIGDECIMAL_SCALE, RoundingMode.HALF_UP).toPlainString();
			return actualFileSizeInBytes2ShowText + FileSizeUnit.BYTE.getUnitKey();
		}
		if (actualFileSizeInBytes >= Constants.ONE_KB_BYTES && actualFileSizeInBytes < Constants.ONE_MB_BYTES) {
			String actualFileSizeInBytes2ShowText = fileSizeInBytes
					.divide(Constants.ONE_UNIT, Constants.DEFAULT_BIGDECIMAL_SCALE, BigDecimal.ROUND_HALF_UP).toPlainString();
			return actualFileSizeInBytes2ShowText + FileSizeUnit.KB.getUnitKey();
		}
		if (actualFileSizeInBytes >= Constants.ONE_MB_BYTES && actualFileSizeInBytes < Constants.ONE_GB_BYTES) {
			String actualFileSizeInBytes2ShowText = fileSizeInBytes
					.divide(Constants.TWO_UNIT, Constants.DEFAULT_BIGDECIMAL_SCALE, BigDecimal.ROUND_HALF_UP).toPlainString();
			return actualFileSizeInBytes2ShowText + FileSizeUnit.MB.getUnitKey();
		}
		if (actualFileSizeInBytes >= Constants.ONE_GB_BYTES && actualFileSizeInBytes < Constants.ONE_TB_BYTES) {
			String actualFileSizeInBytes2ShowText = fileSizeInBytes
					.divide(Constants.THREE_UNIT, Constants.DEFAULT_BIGDECIMAL_SCALE, BigDecimal.ROUND_HALF_UP).toPlainString();
			return actualFileSizeInBytes2ShowText + FileSizeUnit.GB.getUnitKey();
		}
		if (actualFileSizeInBytes >= Constants.ONE_TB_BYTES && actualFileSizeInBytes < Constants.ONE_PB_BYTES) {
			String actualFileSizeInBytes2ShowText = fileSizeInBytes
					.divide(Constants.FOUR_UNIT, Constants.DEFAULT_BIGDECIMAL_SCALE, BigDecimal.ROUND_HALF_UP).toPlainString();
			return actualFileSizeInBytes2ShowText + FileSizeUnit.TB.getUnitKey();
		}
		String actualFileSizeInBytes2ShowText = fileSizeInBytes
				.divide(Constants.FIVE_UNIT, Constants.DEFAULT_BIGDECIMAL_SCALE, BigDecimal.ROUND_HALF_UP).toPlainString();
		return actualFileSizeInBytes2ShowText + FileSizeUnit.PB.getUnitKey();
	}

	/**
	 * @param filePath
	 * @description 读取指定文件的体积大小(单位 : 字节)
	 * @author yida
	 * @date 2023-09-15 16:53:56
	 */
	public static long getFileSize(String filePath) {
		try (FileChannel fileChannel = FileChannel.open(Paths.get(filePath), StandardOpenOption.READ)) {
			long fileSize = fileChannel.size();
			return fileSize;
		} catch (IOException e) {
			logger.error("Read the size of the current file:[{}] with the FileChannel, but occur exception.", filePath);
		}
		return 0L;
	}

	public static void deleteSameNameFolder(File file, String suffix) {
		String parentFolderPath = file.getParent().replaceAll("\\\\", "/") + "/";
		String folderName = file.getName().replace(suffix, "");
		String folderPath = parentFolderPath + folderName;
		File folder = new File(folderPath);
		if (folder.exists()) {
			if (folder.isDirectory()) {
				File[] childFiles = folder.listFiles();
				if (null != childFiles && childFiles.length > 0) {
					String burnPath = folderPath + "/burn";
					File burnFolder = new File(burnPath);
					if (burnFolder.exists() && burnFolder.isDirectory()) {
						File[] childFiles4BurnFolder = burnFolder.listFiles();
						if (null != childFiles4BurnFolder && childFiles4BurnFolder.length > 0) {
							for (File childFile : childFiles4BurnFolder) {
								if (childFile.exists() && childFile.isFile()) {
									childFile.delete();
								}
							}
							childFiles4BurnFolder = burnFolder.listFiles();
							if (null == childFiles4BurnFolder || childFiles4BurnFolder.length <= 0) {
								boolean result = burnFolder.delete();
								if (result) {
									folder.delete();
								}
							}
						} else {
							burnFolder.delete();
						}
					}

				} else {
					boolean deleteResult = folder.delete();
					logger.info("We had delete the parent folder of the tdd/label-data file:[{}]-[deleteResult:{}] as the iso had been burn into the CD successfullu.",
							folderPath, deleteResult);
				}
			}
		}
	}

	/**
	 * @param directoryPath
	 * @param preffix
	 * @description 在指定目录下查找文件名以指定前缀开头的文件, 并删除较旧的文件, 只保留最新的那个文件
	 * @author yida
	 * @date 2023-10-11 06:37:15
	 */
	public static void findOldFilesAndDelete(String directoryPath, String preffix) {
		File directory = new File(directoryPath);
		if (directory.exists() && directory.isDirectory()) {
			File[] files = directory.listFiles();
			if (files.length <= 0) {
				return;
			}
			List<File> fileList = new ArrayList<>();
			for (File file : files) {
				if (file.isFile() && file.getName().startsWith(preffix)) {
					fileList.add(file);
				}
			}
			int listSize = fileList.size();
			if (listSize == 1) {
				return;
			}
			Collections.sort(fileList, new Comparator<File>() {
				@Override
				public int compare(File file1, File file2) {
					long lastModified1 = file1.lastModified();
					long lastModified2 = file2.lastModified();
					// 按照最后一个修改时间升序排序
					if (lastModified1 < lastModified2) {
						return -1;
					} else if (lastModified1 > lastModified2) {
						return 1;
					} else {
						return 0;
					}
				}
			});
			//删除末尾最新的那个文件(最新的文件不需要删除)
			int lastIndex = fileList.size() - 1;
			fileList.remove(lastIndex);
			for (File file : fileList) {
				if (file.isFile() && file.exists()) {
					file.delete();
				}
			}
		}
	}

	/**
	 * @param parentDir
	 * @param dirPreffix
	 * @param excludeDirectoryName
	 * @description 删除指定前缀的目录(包含目录下的所有子目录和子文件)
	 * @author yida
	 * @date 2024-07-16 09:14:01
	 */
	public static void deleteDirectoryWithPreffix(String parentDir, String dirPreffix, String excludeDirectoryName) {
		CanReadDirectoryFileFilter canReadDirectoryFileFilter = new CanReadDirectoryFileFilter();
		FileNamePreffixFileFilter fileNamePreffixFileFilter = new FileNamePreffixFileFilter(dirPreffix);
		FileNameExcludeFileFilter fileNameExcludeFileFilter = new FileNameExcludeFileFilter(excludeDirectoryName);
		FileFilter combineFileFilter = f -> canReadDirectoryFileFilter.accept(f) && fileNamePreffixFileFilter.accept(f) && fileNameExcludeFileFilter.accept(f);
		File parentDirectory = new File(parentDir);
		if (parentDirectory.exists() && parentDirectory.isDirectory()) {
			File[] directoryArray = parentDirectory.listFiles(combineFileFilter);
			if (null == directoryArray || directoryArray.length <= 0) {
				return;
			}
			for (File directory : directoryArray) {
				Path curDirPath = directory.toPath();
				try {
					Files.walkFileTree(curDirPath,
							new SimpleFileVisitor<Path>() {
								@Override
								public FileVisitResult visitFile(Path file,
																 BasicFileAttributes attrs) throws IOException {
									Files.delete(file);
									logger.info("File:[{}] was deleted successfully.", file);
									return FileVisitResult.CONTINUE;
								}

								@Override
								public FileVisitResult postVisitDirectory(Path dir,
																		  IOException exc) throws IOException {
									Files.delete(dir);
									logger.info("Folder:[{}] was deleted successfully.", dir);
									return FileVisitResult.CONTINUE;
								}
							}
					);
				} catch (Exception e) {
					logger.error("While traversing and deleting all files and directories in the specified directory:[{}] with the preffix:[{}],an exception occurred:\n{}",
							parentDir, dirPreffix, e.getMessage());
				}
			}

			for (File directory : directoryArray) {
				if (directory.exists()) {
					directory.delete();
					logger.info("Parent Folder:[{}] was deleted successfully.", directory.getAbsolutePath());
				}
			}
		}
	}

	public static long findFilesWithPreffixAndDelete(String directoryPath, String preffix) {
		File directory = new File(directoryPath);
		if (directory.exists() && directory.isDirectory()) {
			File[] files = directory.listFiles();
			if (files.length <= 0) {
				return 0L;
			}
			List<File> fileList = new ArrayList<>();
			for (File file : files) {
				if (file.isFile() && file.getName().startsWith(preffix)) {
					fileList.add(file);
				}
			}
			int listSize = fileList.size();
			if (listSize <= 0) {
				return 0L;
			}
			long fileSize = 0L;
			for (File file : fileList) {
				if (file.isFile() && file.exists()) {
					deleteSameNameFolder(file, ".iso");
					long curFileSize = file.length();
					boolean deleteResult = file.delete();
					if (deleteResult) {
						fileSize += curFileSize;
					}
					logger.info("We had delete the ISO file:[{}] left over from the previous round that were not successfully deleted." +
							"deleteResult:[{}]", file.getAbsolutePath(), deleteResult);
				}
			}
			return fileSize;
		}
		return 0L;
	}

	/**
	 * @param directoryPath
	 * @param preffix
	 * @return {@link List}
	 * @description 查找指定目录下文件名称以指定前缀开头的文件
	 * @author yida
	 * @date 2023-10-18 14:02:12
	 */
	public static List<File> findFilesWithPreffix(String directoryPath, String preffix) {
		File directory = new File(directoryPath);
		if (directory.exists() && directory.isDirectory()) {
			File[] files = directory.listFiles();
			if (files.length <= 0) {
				return null;
			}
			List<File> fileList = new ArrayList<>();
			for (File file : files) {
				if (file.isFile() && file.getName().startsWith(preffix)) {
					fileList.add(file);
				}
			}
			return fileList;
		}
		return null;
	}


	/**
	 * @param filePath
	 * @return String
	 * @description 获取指定文件的所在目录路径
	 * @author yida
	 * @date 2023-10-11 06:43:14
	 */
	public static String getParentDirPath(String filePath) {
		Path isoPath = Paths.get(filePath);
		Path directoryPath = isoPath.getParent();
		return directoryPath.toAbsolutePath().toString();
	}

	public static String fixSharedFilePath(String backupBaseDir) {
		if (backupBaseDir.startsWith("file://///")) {
			backupBaseDir = backupBaseDir.replace("file://///", "//");
		} else if (backupBaseDir.startsWith("file:////")) {
			backupBaseDir = backupBaseDir.replace("file:////", "//");
		} else if (backupBaseDir.startsWith("file:///")) {
			backupBaseDir = backupBaseDir.replace("file:///", "//");
		} else if (backupBaseDir.startsWith("file://")) {
			backupBaseDir = backupBaseDir.replace("file://", "//");
		} else if (backupBaseDir.startsWith("file:/")) {
			backupBaseDir = backupBaseDir.replace("file:/", "//");
		}
		return backupBaseDir;
	}

	/**
	 * 替换路径中的反斜杠为斜杠
	 *
	 * @param appRootPath
	 */
	public static String replaceBackSlash(String appRootPath) {
		if (null != appRootPath && !"".equals(appRootPath)) {
			appRootPath = appRootPath.replaceAll("\\\\", "/");
		}
		return appRootPath;
	}

	public static Long calculateFileSize(BigDecimal fileSizeNum, String unit) {
		BigDecimal bytes = new BigDecimal("0");
		switch (unit.toLowerCase()) {
			case "byte":
				bytes = fileSizeNum;
				break;
			case "kb":
				bytes = fileSizeNum.multiply(new BigDecimal("1024"));
				break;
			case "mb":
				bytes = fileSizeNum.multiply(new BigDecimal("1024")).multiply(new BigDecimal("1024"));
				break;
			case "gb":
				bytes = fileSizeNum.multiply(new BigDecimal("1024")).multiply(new BigDecimal("1024")).multiply(new BigDecimal("1024"));
				break;
			case "tb":
				bytes = fileSizeNum.multiply(new BigDecimal("1024")).multiply(new BigDecimal("1024"))
						.multiply(new BigDecimal("1024")).multiply(new BigDecimal("1024"));
				break;
			case "pb":
				bytes = fileSizeNum.multiply(new BigDecimal("1024")).multiply(new BigDecimal("1024"))
						.multiply(new BigDecimal("1024")).multiply(new BigDecimal("1024"))
						.multiply(new BigDecimal("1024"));
				break;
			default:
				logger.info("Invalid unit provided");
		}
		return bytes.longValue();
	}

	/**
	 * 获取文件后缀名(不包含点号)
	 *
	 * @param fileName 文件名称
	 * @return
	 * @author yida
	 */
	public static String getFileType(String fileName) {
		int index = fileName.lastIndexOf(".");
		if (index != -1) {
			String suffix = fileName.substring(index + 1);
			return suffix;
		} else {
			return null;
		}
	}

	/**
	 * <b>function:</b> 根据文件名和类型数组验证文件类型是否合法，flag是否忽略大小写
	 *
	 * @param fileName   文件名
	 * @param allowTypes 类型数组
	 * @param flag       是否获得大小写
	 * @return 是否验证通过
	 * @author yida
	 */
	public static boolean validTypeByName(String fileName, String[] allowTypes, boolean flag) {
		String suffix = getFileType(fileName);
		boolean valid = false;
		if (allowTypes.length > 0 && "*".equals(allowTypes[0])) {
			valid = true;
		} else {
			for (String type : allowTypes) {
				if (flag) {//不区分大小写后缀
					if (suffix != null && suffix.equalsIgnoreCase(type)) {
						valid = true;
						break;
					}
				} else {//严格区分大小写
					if (suffix != null && suffix.equals(type)) {
						valid = true;
						break;
					}
				}
			}
		}
		return valid;
	}

	/**
	 * <b>function:</b> 在path目录下创建目录
	 *
	 * @param path
	 * @param dirName
	 * @return
	 * @author yida
	 */
	public static boolean mkDirs(String path, String dirName) {
		boolean success = false;
		path = FileUtils.replaceBackSlash(path);
		if(!path.endsWith("/")) {
			path = path + "/";
		}
		File file = new File(path + dirName);
		if (!file.exists()) {
			success = file.mkdirs();
		}
		return success;
	}

	/**
	 * @description 目录复制，从sourceFolder复制到targetFolder内,
	 * @author yida
	 * @date 2024-08-30 10:49:17
	 * @param sourceFolder     源目录路径
	 * @param targetFolder     目标目录路径
	 * @param deltaMode        是否增量复制
	 * @param startTime        文件最后一次修改时间的起止时间(用于增量复制)
	 * @param endTime          文件最后一次修改时间的结束时间(用于增量复制)
	 */
	public static void copyDirectory(String sourceFolder, String targetFolder, boolean deltaMode, long startTime, long endTime) {
		if (endTime <= 0L) {
			endTime = System.currentTimeMillis();
		}
		sourceFolder = replaceBackSlash(sourceFolder);
		targetFolder = replaceBackSlash(targetFolder);
		long start = System.currentTimeMillis();
		try {
			Path sourceFolderPath = new File(sourceFolder).toPath();
			long finalStartTime = startTime;
			long finalEndTime = endTime;
			String finalSourceFolder = sourceFolder;
			String finalTargetFolder = targetFolder;
			Files.walkFileTree(sourceFolderPath,
					new SimpleFileVisitor<Path>() {
						@Override
						public FileVisitResult visitFile(Path path,
														 BasicFileAttributes attrs) throws IOException {
							String absolutePath = path.toString();
							absolutePath = FileUtils.replaceBackSlash(absolutePath);
							File curFile = new File(absolutePath);
							String destinationFilePath = absolutePath.replace(finalSourceFolder, finalTargetFolder);
							boolean isDir = curFile.isDirectory();
							boolean canRead = curFile.canRead();
							long lastModifiedTime = curFile.lastModified();
							long fileSize = curFile.length();
							if (!isDir && canRead) {
								if (deltaMode) {
									if (lastModifiedTime > finalStartTime && lastModifiedTime <= finalEndTime) {
										copyFile(absolutePath, destinationFilePath, fileSize);
									}
								} else {
									copyFile(absolutePath, destinationFilePath, fileSize);
								}
							}
							return FileVisitResult.CONTINUE;
						}

						@Override
						public FileVisitResult postVisitDirectory(Path path,
																  IOException exc) throws IOException {
							String absolutePath = path.toString();
							absolutePath = FileUtils.replaceBackSlash(absolutePath);
							File curFile = new File(absolutePath);
							boolean isDir = curFile.isDirectory();
							boolean canRead = curFile.canRead();
							if (isDir && canRead) {
								String destinationFolderPath = absolutePath.replace(finalSourceFolder, finalTargetFolder);
								File destFolder = new File(destinationFolderPath);
								if (!destFolder.exists()) {
									destFolder.mkdirs();
								}
							}
							return FileVisitResult.CONTINUE;
						}

						@Override
						public FileVisitResult visitFileFailed(Path path, IOException exc) throws IOException {
							return FileVisitResult.CONTINUE;
						}
					}
			);
		} catch (Exception e) {
			logger.error("While traversing all files in the specified directory:[{}] for the file copy,an exception occurred:\n{}",
					sourceFolder, e.getMessage());
		}
		long end = System.currentTimeMillis();
		logger.info("It had taken [{}] ms to traverse the folder:[{}].", (end - start), sourceFolder);
	}

	/**
	 * @param sourceFolder
	 * @description 获取指定文件夹下的文件总个数
	 * @author yida
	 * @date 2024-08-14 19:16:30
	 */
	public static FileWalkResult traverseFileCount(String sourceFolder) {
		return traverseFileCount(sourceFolder, false, 0L, 0L);
	}

	/**
	 * @param sourceFolder
	 * @param deltaMode
	 * @param startTime
	 * @param endTime
	 * @description 获取指定文件夹下的文件总个数
	 * @author yida
	 * @date 2024-08-14 19:15:34
	 */
	public static FileWalkResult traverseFileCount(String sourceFolder, boolean deltaMode, long startTime, long endTime) {
		if (endTime <= 0L) {
			endTime = System.currentTimeMillis();
		}
		AtomicLong totalFileCount = new AtomicLong(0L);
		AtomicLong totalFolderCount = new AtomicLong(0L);
		AtomicLong totalFileSize = new AtomicLong(0L);
		long start = System.currentTimeMillis();
		try {
			Path sourceFolderPath = new File(sourceFolder).toPath();
			long finalStartTime = startTime;
			long finalEndTime = endTime;
			Files.walkFileTree(sourceFolderPath,
					new SimpleFileVisitor<Path>() {
						@Override
						public FileVisitResult visitFile(Path path,
														 BasicFileAttributes attrs) throws IOException {
							String absolutePath = path.toString();
							absolutePath = FileUtils.replaceBackSlash(absolutePath);
							File curFile = new File(absolutePath);
							boolean isDir = curFile.isDirectory();
							boolean canRead = curFile.canRead();
							long lastModifiedTime = curFile.lastModified();
							long fileSize = curFile.length();
							if (!isDir && canRead) {
								if (deltaMode) {
									if (lastModifiedTime > finalStartTime && lastModifiedTime <= finalEndTime) {
										totalFileCount.incrementAndGet();
										if (fileSize > 0L) {
											totalFileSize.addAndGet(fileSize);
										}
										logger.info("File:[{}] was found with [delta] mode, There are already [{}] files found.", absolutePath, totalFileCount.get());
									}
								} else {
									totalFileCount.incrementAndGet();
									if (fileSize > 0L) {
										totalFileSize.addAndGet(fileSize);
									}
									logger.info("File:[{}] was found with [full] mode, There are already [{}] files found.", absolutePath, totalFileCount.get());
								}
							}
							return FileVisitResult.CONTINUE;
						}

						@Override
						public FileVisitResult postVisitDirectory(Path path,
																  IOException exc) throws IOException {
							String absolutePath = path.toString();
							absolutePath = FileUtils.replaceBackSlash(absolutePath);
							File curFile = new File(absolutePath);
							boolean isDir = curFile.isDirectory();
							boolean canRead = curFile.canRead();
							if (isDir && canRead) {
								totalFolderCount.incrementAndGet();
							}
							return FileVisitResult.CONTINUE;
						}

						@Override
						public FileVisitResult visitFileFailed(Path path, IOException exc) throws IOException {
							return FileVisitResult.CONTINUE;
						}
					}
			);
		} catch (Exception e) {
			logger.error("While traversing all files in the specified directory:[{}] for the file count,an exception occurred:\n{}",
					sourceFolder, e.getMessage());
		}
		long end = System.currentTimeMillis();
		logger.info("It had taken [{}] ms to traverse the folder:[{}], current file count was:[{}], current folder count was:[{}], total file size:[{}].",
				(end - start), sourceFolder, totalFileCount.get(), totalFolderCount.get(),
				FileUtils.beautifyFileSizeShowText(new BigDecimal(String.valueOf(totalFileSize.get()))));
		FileWalkResult fileWalkResult = new FileWalkResult(deltaMode, totalFileCount.get(), totalFolderCount.get(), totalFileSize.get());
		fileWalkResult.setFileStartTime(startTime);
		fileWalkResult.setFileEndTime(endTime);
		return fileWalkResult;
	}

	/**
	 * 下载文件名重新编码
	 *
	 * @param request  请求对象
	 * @param fileName 文件名
	 * @return 编码后的文件名
	 */
	public static String setFileDownloadHeader(HttpServletRequest request, String fileName)
			throws UnsupportedEncodingException {
		final String agent = request.getHeader("USER-AGENT");
		String filename = fileName;
		if (agent.contains("MSIE")) {
			// IE浏览器
			filename = URLEncoder.encode(filename, "utf-8");
			filename = filename.replace("+", " ");
		} else if (agent.contains("Firefox")) {
			// 火狐浏览器
			filename = new String(fileName.getBytes(), "ISO8859-1");
		} else if (agent.contains("Chrome")) {
			// google浏览器
			filename = URLEncoder.encode(filename, "utf-8");
		} else {
			// 其它浏览器
			filename = URLEncoder.encode(filename, "utf-8");
		}
		return filename;
	}

	/**
	 * 文件下载状态
	 */
	public enum DownloadStatus {
		URL_ERROR(1, "URL错误"),
		FILE_EXIST(2, "文件存在"),
		TIME_OUT(3, "连接超时"),
		DOWNLOAD_FAIL(4, "下载失败"),
		DOWNLOAD_SUCCESS(5, "下载成功");

		private int code;

		private String name;

		DownloadStatus(int code, String name) {
			this.code = code;
			this.name = name;
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	public static DownloadStatus downloadFile(String savePath, String fileUrl, boolean downNew) {
		URL urlfile = null;
		HttpURLConnection httpUrl = null;
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		if (fileUrl.startsWith("//")) {
			fileUrl = "http:" + fileUrl;
		}
		String fileName;
		try {
			urlfile = new URL(fileUrl);
			String urlPath = urlfile.getPath();
			fileName = urlPath.substring(urlPath.lastIndexOf("/") + 1);
		} catch (MalformedURLException e) {
			logger.error("URL异常", e);
			return DownloadStatus.URL_ERROR;
		}
		File path = new File(savePath);
		if (!path.exists()) {
			path.mkdirs();
		}
		File file = new File(savePath + File.separator + fileName);
		if (file.exists()) {
			if (downNew) {
				file.delete();
			} else {
				logger.info("文件已存在不重新下载！");
				return DownloadStatus.FILE_EXIST;
			}
		}
		try {
			httpUrl = (HttpURLConnection) urlfile.openConnection();
			httpUrl.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:68.0) Gecko/20100101 Firefox/68.0");
			//读取超时时间
			httpUrl.setReadTimeout(60000);
			//连接超时时间
			httpUrl.setConnectTimeout(60000);
			httpUrl.connect();
			bis = new BufferedInputStream(httpUrl.getInputStream());
			bos = new BufferedOutputStream(new FileOutputStream(file));
			int len = 2048;
			byte[] b = new byte[len];
			int readLen = 0;
			while ((readLen = bis.read(b)) != -1) {
				bos.write(b, 0, readLen);
			}
			logger.info("远程文件下载成功:" + fileUrl);
			bos.flush();
			httpUrl.disconnect();
			return DownloadStatus.DOWNLOAD_SUCCESS;
		} catch (SocketTimeoutException e) {
			logger.error("读取文件超时", e);
			return DownloadStatus.TIME_OUT;
		} catch (Exception e) {
			logger.error("远程文件下载失败", e);
			return DownloadStatus.DOWNLOAD_FAIL;
		} finally {
			try {
				if (bis != null) {
					bis.close();
				}
				if (bos != null) {
					bos.close();
				}
			} catch (Exception e) {
				logger.error("下载出错", e);
			}
		}
	}
}
