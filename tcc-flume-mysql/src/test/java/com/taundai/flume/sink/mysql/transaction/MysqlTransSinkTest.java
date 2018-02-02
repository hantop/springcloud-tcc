package com.taundai.flume.sink.mysql.transaction;

import org.apache.flume.Context;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.tuandai.flume.sink.mysql.transaction.MysqlTransSink;


/**
 *
 * @author jianggq
 */
public class MysqlTransSinkTest {
	@InjectMocks
	private MysqlTransSink mysqlTransSink;

    Context context = null;
    
    @Test
    public void configure_test() {
    	mysqlTransSink.configure(context);
    }
    
    
    @Before
    public void createContext() {
    	
        context = new Context();
        context.put(MysqlTransSink.DB_URL, "jdbc:mysql://10.100.11.75:3306/trans_test?useUnicode=true&characterEncoding=utf-8&autoReconnect=true&&useSSL=false");
        context.put(MysqlTransSink.DRIVER, "com.mysql.jdbc.Driver");
        context.put(MysqlTransSink.USERNAME, "root");
        context.put(MysqlTransSink.PASSWORD, "root");
        context.put(MysqlTransSink.ID_FIELD, "uid");

        context.put(MysqlTransSink.DRIVER, "com.mysql.jdbc.Driver");
        context.put(MysqlTransSink.DRIVER, "com.mysql.jdbc.Driver");
        context.put(MysqlTransSink.DRIVER, "com.mysql.jdbc.Driver");
        
        context.put(MysqlTransSink.INIT_TABLES, " CREATE TABLE IF NOT EXISTS t_trans ("
        		+ "         trans_id VARCHAR(100),"
        		+ "         service_name VARCHAR(50),"
        		+ "         check_url VARCHAR(1024),"
        		+ "         res_urls VARCHAR(2048),"
        		+ "         trans_state INT,"
        		+ "         check_times INT,"
        		+ "         check_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ,"
        		+ "         check_threshold INT,"
        		+ "         cc_times INT,"
        		+ "         cc_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP  ,"
        		+ "         cc_threshold INT,"
        		+ "         update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
        		+ "         create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ,"
        		+ "		 PRIMARY KEY (trans_id))"
        		+ "		 ENGINE=InnoDB  DEFAULT CHARSET=utf8;"
        		+ ");");
        
        
        mysqlTransSink = new MysqlTransSink();
		MockitoAnnotations.initMocks(this);
    }
    
}
