package com.tuandai.flume.sink.rabbitmq.transaction;

import org.junit.Before;
import org.junit.Test;

import com.alibaba.fastjson.JSONObject;

public class LogMapFileTest {
	private String mapDir;

    @Before
    public void createContext() {
    	mapDir = "E:\\tmp\\logM"; 

    	JSONObject value1 = JSONObject.parseObject("{'111':'aaa'}");
    	JSONObject value2 = JSONObject.parseObject("{'222':'bbbbb'}");

		LogMapFile.beginMap.put("111", value1);
		LogMapFile.beginMap.put("222", value2);
		LogMapFile.beginMap.put("222", value2);
		LogMapFile.beginMap.put("333", value1);
    	
		
    }
    
    @Test
    public void writeFile_test() {
    	LogMapFile.setDirectory(mapDir);
    	
    	LogMapFile.writeFile();
    }

    @Test
    public void loadLogMap_test() {
    	LogMapFile.setDirectory(mapDir);
    	
    	LogMapFile.loadBeginMap();
    }
    

}
