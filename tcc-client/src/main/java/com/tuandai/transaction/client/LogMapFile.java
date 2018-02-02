package com.tuandai.transaction.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogMapFile {
	private static final Logger logger = LoggerFactory.getLogger(LogMapFile.class);

	public static final Integer MAX_FILE_SIZE = 1000000;

	private static File directory;

	public static final String RPC_FILE_END_MARK = "RPC_FILE_END";

	private static String rpcCurrentFileName = "";

	private static File rpcFile = null;
	

	private static final String rpcFileExtension = ".rpc";

	public static void setDirectory(String dir) {
		LogMapFile.directory = new File(dir);

		// 检查目录权限
		if (!LogMapFile.directory.exists()) {
			if (!LogMapFile.directory.mkdirs()) {
				throw new IllegalArgumentException("begin.map.directory is not a directory");
			}
		} else if (!LogMapFile.directory.canWrite()) {
			throw new IllegalArgumentException("begin.map.directory can not write");
		}
	}
	
	public static File getDirectory(){
		return LogMapFile.directory;
	}

	/**
	 * 持久化刷盤
	 */
	public static synchronized Boolean writeFile(String data) {
		// 确立文件名
		if (null == rpcFile || null == rpcCurrentFileName || rpcCurrentFileName.length() < 3) {
			getNewFileName();
		}

		// 写文件
		OutputStreamWriter writer = null;
		try {
			// 检查文件大小，创建新文件
			if (rpcFile.length() > MAX_FILE_SIZE) {
				writer = new OutputStreamWriter(new FileOutputStream(rpcFile, true));
				writer.append(LogMapFile.RPC_FILE_END_MARK);
				writer.flush();
				writer.close();
				getNewFileName();
			}

			writer = new OutputStreamWriter(new FileOutputStream(rpcFile, true));
			writer.append(data + "\n");
			writer.flush();
		} catch (Exception e) {
			logger.error("write logMap file error : {}", e);
			return false;
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				logger.error("write logMap close error: {}", e);
			}
		}

		return true;
	}

	private static String getNewFileName() {
		try {
			Date dt = new Date();
			rpcCurrentFileName = dt.getTime() + rpcFileExtension;
			rpcFile = new File(LogMapFile.directory.getCanonicalPath() + File.separatorChar + rpcCurrentFileName);
			if (!rpcFile.exists()) {
				rpcFile.createNewFile();
			}
		} catch (IOException e1) {
			logger.error("get new rpc file error: {}", e1);
		}
		return rpcCurrentFileName;
	}

}
