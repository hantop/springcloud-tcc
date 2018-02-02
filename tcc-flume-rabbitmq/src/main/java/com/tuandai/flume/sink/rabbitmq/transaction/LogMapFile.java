package com.tuandai.flume.sink.rabbitmq.transaction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class LogMapFile {
	private static final Logger logger = LoggerFactory.getLogger(LogMapFile.class);

	public static final String CONFIG_BEGIN_MAP_URI = "mapFileUri";

	private static File directory;

	private static final String mapFile = "begin.map";

	private static final String mapEndFile = "end.map";

	private static final String mapCheckFile = "begin.map.check";

	public static ConcurrentHashMap<String, JSONObject> beginMap = new ConcurrentHashMap<String, JSONObject>();
	public static ConcurrentHashMap<String, JSONObject> endMap = new ConcurrentHashMap<String, JSONObject>();

	public static JSONArray checkArray = new JSONArray();

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

	public static void addLog(String key, JSONObject value) {
		LogMapFile.beginMap.put(key, value);
	}

	public static void addEndLog(String key, JSONObject value) {
		LogMapFile.endMap.put(key, value);
	}

	/**
	 * 持久化刷盤
	 */
	public static JSONArray writeFile() {
		OutputStreamWriter writer = null;
		OutputStreamWriter checkWriter = null;
		Date dt = new Date();
		try {
			File file = new File(LogMapFile.directory.getCanonicalPath() + File.separatorChar + mapFile + ".tmp");
			File checkFile = new File(LogMapFile.directory.getCanonicalPath() + File.separatorChar + mapCheckFile);

			if (!file.exists()) {
				file.createNewFile();
			}

			if (!checkFile.exists()) {
				checkFile.createNewFile();
			}

			writer = new OutputStreamWriter(new FileOutputStream(file));
			checkWriter = new OutputStreamWriter(new FileOutputStream(checkFile, true));
			Iterator<Entry<String, JSONObject>> iterator = LogMapFile.beginMap.entrySet().iterator();
			while(iterator.hasNext()) {
				Entry<String, JSONObject> entry = iterator.next();
				JSONObject jolog = new JSONObject();
				jolog.put("key", entry.getKey());
				jolog.put("val", entry.getValue());

				JSONObject evalue = entry.getValue();
				if (isCheckLog(evalue, dt)) {
					checkWriter.append(jolog.toJSONString() + "\n");
					checkArray.add(jolog);
					iterator.remove();
				} else {
					writer.write(jolog.toJSONString() + "\n");
				}
			}
			writer.flush();
			checkWriter.flush();
			file.renameTo(new File(LogMapFile.directory.getCanonicalPath() + File.separatorChar + mapFile));
		} catch (Exception e) {
			logger.error("write logMap file error : {}", e);
			return checkArray;
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
				if (checkWriter != null) {
					checkWriter.close();
				}
			} catch (IOException e) {
				logger.error("write logMap close error: {}", e);
			}
		}

		return checkArray;
	}

	private static Boolean isCheckLog(JSONObject evalue, Date dt) {
		Long dtl = dt.getTime();
		if (60000 < (dtl - evalue.getLongValue(DealEvents.TIME_FILED))) {
			return true;
		}
		return false;
	}

	public static Boolean writeEndFile() {

		OutputStreamWriter endWriter = null;
		try {
			File endFile = new File(LogMapFile.directory.getCanonicalPath() + File.separatorChar + mapEndFile + ".tmp");

			if (!endFile.exists()) {
				endFile.createNewFile();
			}

			endWriter = new OutputStreamWriter(new FileOutputStream(endFile));

			for (Iterator<Entry<String, JSONObject>> iterator = LogMapFile.endMap.entrySet().iterator(); iterator
					.hasNext();) {
				Entry<String, JSONObject> entry = iterator.next();
				JSONObject jolog = new JSONObject();
				jolog.put("key", entry.getKey());
				jolog.put("val", entry.getValue());
				endWriter.write(jolog.toJSONString() + "\n");
			}
			endWriter.flush();
			endFile.renameTo(new File(LogMapFile.directory.getCanonicalPath() + File.separatorChar + mapEndFile));
		} catch (Exception e) {
			logger.error("write logMap file error : {}", e);
			return false;
		} finally {
			try {
				if (endWriter != null) {
					endWriter.close();
				}
			} catch (IOException e) {
				logger.error("write logMap close error: {}", e);
			}
		}

		return true;
	}

	public static ConcurrentHashMap<String, JSONObject> loadBeginMap() {
		Reader reader = null;
		LogMapFile.beginMap.clear();
		try {
			File file = new File(LogMapFile.directory.getCanonicalPath() + File.separatorChar + mapFile);
			if (!file.exists()) {
				file.createNewFile();
			}
			reader = new InputStreamReader(new FileInputStream(file));
			BufferedReader bufferedReader = new BufferedReader(reader);
			String keyValueStr = null;
			while ((keyValueStr = bufferedReader.readLine()) != null) {
				JSONObject jo = JSONObject.parseObject(keyValueStr);
				LogMapFile.beginMap.put(jo.getString("key"), jo.getJSONObject("val"));
			}
			bufferedReader.close();
			logger.info("load logmap: " + LogMapFile.beginMap.toString());
		} catch (Exception e) {
			logger.error("load logmap error: {}", e);
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				logger.error("close reader error: {}", e);
			}
		}

		return LogMapFile.beginMap;

	}

	public static ConcurrentHashMap<String, JSONObject> loadEndMap() {
		Reader reader = null;
		LogMapFile.endMap.clear();
		try {
			File file = new File(LogMapFile.directory.getCanonicalPath() + File.separatorChar + mapEndFile);
			if (!file.exists()) {
				file.createNewFile();
			}
			reader = new InputStreamReader(new FileInputStream(file));
			BufferedReader bufferedReader = new BufferedReader(reader);
			String keyValueStr = null;
			while ((keyValueStr = bufferedReader.readLine()) != null) {
				JSONObject jo = JSONObject.parseObject(keyValueStr);
				LogMapFile.endMap.put(jo.getString("key"), jo.getJSONObject("val"));
			}
			bufferedReader.close();
			logger.info("load logEndMap: " + LogMapFile.endMap.toString());
		} catch (Exception e) {
			logger.error("load endMap error: {}", e);
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				logger.error("close reader error: {}", e);
			}
		}

		return LogMapFile.endMap;

	}

}
