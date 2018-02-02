package com.tuandai.architecture.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tuandai.architecture.util.CheckPointUtil.CheckPoint;

public class CheckPointUtil {

	private static final Logger logger = LoggerFactory.getLogger(CheckPointUtil.class);
	/**
	 * checkpoint文件路径
	 */
	public static final String PATH = System.getProperty("user.dir") + "/checkpiont";

	public static class CheckPoint implements Cloneable, Comparable<CheckPoint> {
		private long time;
		public static int size;

		public long getTime() {
			return time;
		}

		public void setTime(long time) {
			this.time = time;
		}

		static public void size(int size) {
			CheckPoint.size = size;
		}

		@Override
		public int compareTo(CheckPoint o) {
			if (o == null)
				throw new IllegalArgumentException("CheckPoint比较参数为空");
			return new Long(this.time - o.time).intValue();
		}

		@Override
		public Object clone() throws CloneNotSupportedException {
			return super.clone();
		}

		@Override
		public String toString() {
			return "checkpoint信息：{time:" + time + ",size:" + size + "}";
		}
	}

	/**
	 * 获取checkpoint信息
	 */
	static public CheckPoint check() {
		CheckPoint p = new CheckPoint();
		BufferedReader reader = null;
		try {
			reader = Files.newBufferedReader(path());
			String time = reader.readLine();
			if (time == null || time.length() == 0) {
				p.setTime(0l);
			} else {
				p.setTime(Long.parseLong(time));
			}
			logger.info("checkpoint is :{}", p.getTime());
		} catch (Exception e) {
			logger.error("chech文件失败：{}", e.getMessage());
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e) {
					logger.error("关闭读文件流异常：", e.getMessage());
				}
			}
		}
		return p;
	}

	static public CheckPoint checkNow(long intervalTime) {
		CheckPoint p = new CheckPoint();
		p.setTime(System.currentTimeMillis() - intervalTime);
		return p;
	}

	static public void update(CheckPoint point) {
		BufferedWriter writer = null;
		try {
			writer = Files.newBufferedWriter(path());
			writer.write("" + point.getTime());
			writer.flush();
		} catch (Exception e) {
			logger.error("更新checkpoint失败：{}", e.getMessage());
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {
					logger.error("关闭写文件流异常：", e.getMessage());
				}
			}
		}
	}

	static private Path path() {
		Path path = Paths.get(PATH);
		try {
			if (!Files.exists(path)) {
				Files.createFile(path);
			}
		} catch (Exception e) {
			logger.error("创建文件失败：{}", e.getMessage());
		}
		return path;
	}

	public static void main(String[] args) {
		CheckPointUtil.check();
		/*
		 * CheckPoint p = new CheckPoint(); p.setTime(99998752310l);
		 * CheckPointUtil.update(p);
		 */
	}
}
