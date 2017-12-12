package com.tuandai.architecture.componet;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tuandai.architecture.config.SpringBootConfig;

@Component
public class ThresholdsTimeManage {

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
		if (currentThreshods > threshods.length) {
			calendar.add(Calendar.SECOND, Integer.valueOf(threshods[threshods.length - 1]));
			return calendar.getTime();
		} else {
			calendar.add(Calendar.SECOND, Integer.valueOf(threshods[currentThreshods - 1]));
			return calendar.getTime();
		}
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
		if (currentThreshods > threshods.length) {
			calendar.add(Calendar.SECOND, Integer.valueOf(threshods[threshods.length - 1]));
			return calendar.getTime();
		} else {
			calendar.add(Calendar.SECOND, Integer.valueOf(threshods[currentThreshods - 1]));
			return calendar.getTime();
		}
	}

}
