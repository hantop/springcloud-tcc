package com.tuandai.architecture.component;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tuandai.architecture.config.SpringBootConfig;

@Component
public class ThresholdsTimeManage {

	public static final int MIN_CHECK_THRESHOLD_TIME = 30;

	public static final int MIN_CC_THRESHOLD_TIME = 20;

	@Autowired
	SpringBootConfig springBootConfig;

	/**
	 * 获取CHECK回调接口，调用时间算法； 按照配置位，相应延迟多少秒
	 * 
	 * @param currentThreshods
	 * @return
	 */
	public Date createTccCheckTime(int currentThreshods) {
		if (currentThreshods < 1) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		String[] threshods = springBootConfig.getTccCheckThresholds().split(",");
		int threshodsTime = 0;
		if (currentThreshods > threshods.length) {
			threshodsTime = Integer.valueOf(threshods[threshods.length - 1]);
		} else {
			threshodsTime = Integer.valueOf(threshods[currentThreshods - 1]);
		}
		if (threshodsTime < MIN_CHECK_THRESHOLD_TIME) {
			threshodsTime = MIN_CHECK_THRESHOLD_TIME;
		}
		calendar.add(Calendar.SECOND, threshodsTime);
		return calendar.getTime();
	}

	/**
	 * 获取CC 调用时间算法； 按照配置位，相应延迟多少秒
	 * 
	 * @param currentThreshods
	 * @return
	 */
	public Date createCCTime(int currentThreshods) {
		if (currentThreshods < 1) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		String[] threshods = springBootConfig.getTccCCThresholds().split(",");

		int threshodsTime = 0;
		if (currentThreshods > threshods.length) {
			threshodsTime = Integer.valueOf(threshods[threshods.length - 1]);
		} else {
			threshodsTime = Integer.valueOf(threshods[currentThreshods - 1]);
		}

		if (threshodsTime < MIN_CC_THRESHOLD_TIME) {
			threshodsTime = MIN_CC_THRESHOLD_TIME;
		}
		calendar.add(Calendar.SECOND, threshodsTime);
		return calendar.getTime();

	}

}
