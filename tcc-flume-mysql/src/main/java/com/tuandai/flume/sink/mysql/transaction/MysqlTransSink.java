package com.tuandai.flume.sink.mysql.transaction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.flume.Channel;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.Transaction;
import org.apache.flume.conf.Configurable;
import org.apache.flume.sink.AbstractSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;


public class MysqlTransSink extends AbstractSink implements Configurable {
	private static Logger logger = LoggerFactory.getLogger(MysqlTransSink.class);

	public static final String DRIVER = "driver";
	public static final String DB_URL = "dbUrl";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String INIT_TABLES = "initTables";
	public static final String INSERT_CHECK_TRANS = "insertCheck";
	public static final String QUERY_CHECK_TRANS = "queryCheck";
	public static final String DELETE_CHECK_TRANS = "deleteCheck";
	public static final String INSERT_CC_TRANS = "insertCC";
	public static final String BATCH_SIZE = "batch";

	public static final String DEFAULT_DRIVER = "com.mysql.jdbc.Driver";
	public static final String DEFAULT_DB_URL = "jdbc:mysql://127.0.0.1:3306/tcc_trans?useUnicode=true&characterEncoding=utf-8&autoReconnect=true";
	public static final String DEFAULT_USERNAME = "root";
	public static final String DEFAULT_PASSWORD = "root";
	public static final int DEFAULT_BATCH = 100;


	public static final String SERVICE_NAME_FIELD = "serviceName";
	public static final String TRANS_TYPE_FIELD = "type";
	public static final String ID_FIELD = "uid";
	public static final String TIMESTAMP_FIELD = "time";

	private String driver;
	private String dbUrl;
	private String username;
	private String password;
	
	private String initTables;
	private String insertCheck;
	private String queryCheck;
	private String deleteCheck;
	private String insertCC;
	private int batchSize;

	private Connection connection;
    private PreparedStatement preparedInsertCheck; 
    private PreparedStatement preparedQueryCheck;
    private PreparedStatement preparedDeleteCheck;
    private PreparedStatement preparedInsertCC;

	@Override
	public void configure(Context context) {
		driver = context.getString(DRIVER, DEFAULT_DRIVER);
		dbUrl = context.getString(DB_URL, DEFAULT_DB_URL);
		username = context.getString(USERNAME, DEFAULT_USERNAME);
		password = context.getString(PASSWORD, DEFAULT_PASSWORD);
		
		initTables =  context.getString(INIT_TABLES);
		insertCheck = context.getString(INSERT_CHECK_TRANS);
		queryCheck = context.getString(QUERY_CHECK_TRANS);
		deleteCheck = context.getString(DELETE_CHECK_TRANS);
		insertCC = context.getString(INSERT_CC_TRANS);

		batchSize = context.getInteger(BATCH_SIZE, DEFAULT_BATCH);

		logger.info("host:{}, port:{}, username:{}, password:{}, dbName:{}, batch: {}",
				new Object[] { driver, username, password, dbUrl, batchSize });
	}

	@Override
	public synchronized void start() {
		logger.info("Starting {} ...", this.getName());
		try {
			Class.forName(driver);
			connection = DriverManager.getConnection(dbUrl, username, password);
			connection.setAutoCommit(false);  
            //创建Statement对象  
			if(connection.prepareStatement(initTables).execute()){
				logger.error("Initial failed:  {}.", initTables);
				System.exit(1);
			}
			preparedInsertCheck = connection.prepareStatement(insertCheck);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		super.start();
		logger.info("Started {}.", this.getName());
	}

	@Override
	public void stop() {
		stopPrepared(preparedInsertCheck);
		stopPrepared(preparedQueryCheck);
		stopPrepared(preparedDeleteCheck);
		stopPrepared(preparedInsertCC);
		
		logger.info("MySQL sink {} stopping", this.getName());
		if (connection != null) {
			logger.debug("Destroying connection to: {}:{}", dbUrl, username);
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		connection = null;
		super.stop();
		logger.debug("MySQL sink {} stopped.", this.getName());
	}

	@Override
	public Status process() throws EventDeliveryException {
		Channel channel = getChannel();
		Transaction tx = null;
		Status status = Status.BACKOFF;
		
		try {
			tx = channel.getTransaction();
			tx.begin();
			Map<String, List<JSONObject>> eventMap = new HashMap<String, List<JSONObject>>();
			for (int i = 0; i < batchSize; i++) {
				Event event = channel.take();
				if (event == null || event.getBody() == null || event.getBody().length == 0) {
					break;
				} else {
					logger.debug("{} start to process event", getName());
				}
			}
			
			tx.commit();
			status = Status.READY;
		} catch (Exception e) {
			logger.error("can't process events, rollback!", e);
			tx.rollback();
			status = Status.BACKOFF;
		} finally {
			if (tx != null) {
				tx.close();
			}
		}
		return status;
	}
	
	private void stopPrepared( PreparedStatement preparedStatement){
		if (preparedStatement != null) {  
            try {  
            	preparedStatement.close(); 
            	preparedStatement = null;
            } catch (SQLException e) {  
                e.printStackTrace();  
            }  
        } 
	}

}
