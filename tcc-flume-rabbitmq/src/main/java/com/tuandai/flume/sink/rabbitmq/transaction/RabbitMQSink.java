package com.tuandai.flume.sink.rabbitmq.transaction;

import java.io.IOException;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.Transaction;
import org.apache.flume.conf.Configurable;
import org.apache.flume.sink.AbstractSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMQSink extends AbstractSink implements Configurable {
	private static final Logger logger = LoggerFactory.getLogger(RabbitMQSink.class);
	private ConnectionFactory _ConnectionFactory;
	private Connection _Connection;
	private Channel _Channel;
	private String _ExchangeName;
	private String _mapFileUri;
	private Integer _batchSize = 100;

	private static final String CHECK_ROUTING_KEY = "tcc-check";

	@Override
	public void configure(Context context) {
		_ConnectionFactory = RabbitMQUtil.getFactory(context);
		_ExchangeName = RabbitMQUtil.getExchangeName(context);
		_mapFileUri = context.getString(LogMapFile.CONFIG_BEGIN_MAP_URI);
	}

	@Override
	public synchronized void start() {
		logger.info("Starting {}...", getName());
		try {
			LogMapFile.setDirectory(_mapFileUri);
			LogMapFile.loadBeginMap();
			LogMapFile.loadEndMap();
		} catch (Exception e) {
			logger.error("Can't load begin map", e);
			return;
		}
		super.start();
		logger.info("Started {}.", getName());
	}

	@Override
	public synchronized void stop() {
		RabbitMQUtil.close(_Connection, _Channel);
		super.stop();
	}

	private void resetConnection() {
		if (logger.isWarnEnabled())
			logger.warn(this.getName() + " - Closing RabbitMQ connection and channel due to exception.");
		RabbitMQUtil.close(_Connection, _Channel);
		_Connection = null;
		_Channel = null;
	}

	@Override
	public Status process() throws EventDeliveryException {
		try {
			checkConnect();
		} catch (Exception ex) {
			if (logger.isErrorEnabled())
				logger.error(this.getName() + " - Exception while establishing connection.", ex);
			resetConnection();
			return Status.BACKOFF;
		}

		org.apache.flume.Channel flumeChannel = getChannel();
		Transaction tx = null;
		Status status = Status.BACKOFF;
		try {
			tx = flumeChannel.getTransaction();
			tx.begin();

			for (int i = 0; i < _batchSize; i++) {
				Event event = flumeChannel.take();
				if (event == null || event.getBody() == null || event.getBody().length == 0) {
					break;
				} else {
					logger.debug("{} start to process event", getName());
					JSONObject sendData = DealEvents.deal(event);
					if (null != sendData) {
						// 发送MQ
						try {
							logger.debug("send MQ event: {} ", sendData.toJSONString());
							_Channel.basicPublish(_ExchangeName, sendData.getString(DealEvents.RESURLS_FILED), null,
									sendData.toJSONString().getBytes());
						} catch (Exception ex) {
							resetConnection();
							throw ex;
						}
					}
				}
			}

			// beginMap 刷盘
			LogMapFile.writeFile();
			if (LogMapFile.checkArray.size() > 0) {
				try {
					_Channel.basicPublish(_ExchangeName, CHECK_ROUTING_KEY, null, LogMapFile.checkArray.toJSONString().getBytes());
				} catch (Exception ex) {
					resetConnection();
					throw ex;
				}
				LogMapFile.checkArray.clear();
			}

			LogMapFile.writeEndFile();

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

	private void checkConnect() throws IOException {
		if (null == _Connection) {
			if (logger.isInfoEnabled()) {
				logger.info(this.getName() + " - Opening connection to " + _ConnectionFactory.getHost() + ":"
						+ _ConnectionFactory.getPort());
			}
			_Connection = _ConnectionFactory.newConnection();
			_Channel = null;
		}

		if (null == _Channel) {
			if (logger.isInfoEnabled()) {
				logger.info(this.getName() + " - creating channel...");
			}
			_Channel = _Connection.createChannel();
			if (logger.isInfoEnabled()) {
				logger.info(this.getName() + " - Connected to " + _ConnectionFactory.getHost() + ":"
						+ _ConnectionFactory.getPort());
			}
		}
	}
}
