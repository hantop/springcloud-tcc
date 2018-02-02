package com.tuandai.transaction.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendData {
	private static final Logger logger = LoggerFactory.getLogger(SendData.class);

	private static String checkPointFileName = "checkpoint";

	private static final String checkPointSplit = "\t";

	private static File checkPointFile = null;

	private static File checkFile = null;

	private static int checkPoint = 0;

	public static Boolean writerCheckpoint() {
		OutputStreamWriter writer = null;
		if (null == checkFile) {
			return false;
		}

		try {
			checkPointFile = new File(
					LogMapFile.getDirectory().getCanonicalPath() + File.separatorChar + checkPointFileName);
			if (!checkPointFile.exists()) {
				checkPointFile.createNewFile();
			}
			writer = new OutputStreamWriter(new FileOutputStream(checkPointFile));
			writer.write(checkFile.getCanonicalPath() + SendData.checkPointSplit + checkPoint);
			writer.flush();
		} catch (Exception e) {
			logger.error("write checkpoint file error : {}", e);
			return false;
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				logger.error("write checkpoint close error: {}", e);
			}
		}
		return true;
	}

	public static List<Event> getCheckpointData(int maxLine) throws IOException {
		List<Event> sendEvents = new ArrayList<Event>();
		if (null == checkFile) {
			getLatestCheckFile();
		}

		if (null == checkFile) {
			return sendEvents;
		}

		InputStreamReader inReader = null;
		BufferedReader br = null;
		try {
			inReader = new InputStreamReader(new FileInputStream(checkFile), "utf8");
			br = new BufferedReader(inReader);
			br.skip(checkPoint);

			String line = "";
			int iline = 0;
			int bytesize = 0;
			while (((line = br.readLine()) != null) && iline < maxLine) {
				if (LogMapFile.RPC_FILE_END_MARK.equals(line)) {
					break;
				}
				bytesize = bytesize + line.length() + 1;
				logger.error("==================read line: {} , size : {}  , bytesize: {}", line , line.length(),bytesize);
				sendEvents.add(EventBuilder.withBody(line, Charset.forName("UTF-8")));
				iline++;
			}

			if (LogMapFile.RPC_FILE_END_MARK.equals(line)) {
				checkFile.renameTo(new File(checkFile.getCanonicalPath() + ".done"));
				checkFile = null;
				getLatestCheckFile();
			} else {
				if (bytesize > 0) {
					checkPoint = checkPoint + bytesize;
				}
			}
		} catch (IOException e) {
			logger.error("read checkpoint file error: {}", e);
		} finally {
			if (null != inReader) {
				inReader.close();
			}
			if (null != br) {
				br.close();
			}
		}

		return sendEvents;
	}


	private static void getLatestCheckFile() {
		try {
			File[] listFile = LogMapFile.getDirectory().listFiles(new FileNameSelector("rpc"));
			if (0 == listFile.length) {
				checkPoint = 0;
				checkFile = null;
			} else {
				sortListfile(listFile);
				checkPoint = 0;
				checkFile = new File(listFile[0].getCanonicalPath());
			}
		} catch (IOException e) {
			logger.error("get check file error: {}", e);
		}
	}

	/**
	 * 文件列表排序
	 * 
	 * @param listFile
	 */
	private static void sortListfile(File[] listFile) {
		Arrays.sort(listFile, new Comparator<File>() {
			public int compare(File f1, File f2) {
				long diff = f1.lastModified() - f2.lastModified();
				if (diff > 0)
					return 1;
				else if (diff == 0)
					return 0;
				else
					return -1;
			}

			public boolean equals(Object obj) {
				return true;
			}
		});
	}

}
