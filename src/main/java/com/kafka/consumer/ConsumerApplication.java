package com.kafka.consumer;

import java.util.Properties;
import java.util.Collections;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

@SpringBootApplication
public class ConsumerApplication {

	private static String kafka_server = System.getenv("KAFKA_SERVER");
	private static String kafka_topic = System.getenv("KAFKA_TOPIC");
	private static String kafka_count_msg = System.getenv("KAFKA_COUNT_MSG");
	private static String kafka_key = System.getenv("KAFKA_KEY");
	private static String kafka_pass = System.getenv("KAFKA_PASS");

	public static void main(String[] args) {
		SpringApplication.run(ConsumerApplication.class, args);
		runConsumer();
	}

	public interface KafkaConstants {
		int num_msg_kafka = Integer.parseInt(kafka_count_msg);
		public static String KAFKA_BROKERS = kafka_server;
		public static String TOPIC_NAME = kafka_topic;
		public static String GROUP_ID_CONFIG = "consumerGroupItau";
		public static Integer MAX_NO_MESSAGE_FOUND_COUNT = num_msg_kafka;
		public static String OFFSET_RESET_LATEST = "latest";
		public static String OFFSET_RESET_EARLIER = "earliest";
		public static Integer MAX_POLL_RECORDS = 1;
	}

	public static Consumer<Long, String> createConsumer() {
		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConstants.KAFKA_BROKERS);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaConstants.GROUP_ID_CONFIG);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, KafkaConstants.MAX_POLL_RECORDS);
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, KafkaConstants.OFFSET_RESET_EARLIER);
		props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
		props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, kafka_key);
		props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, kafka_pass);
		
		props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, kafka_key);
		props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, kafka_pass);
		props.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, kafka_pass);
		Consumer<Long, String> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Collections.singletonList(KafkaConstants.TOPIC_NAME));
		return consumer;
	}

	static void runConsumer() {
		Consumer<Long, String> consumer = createConsumer();
		int noMessageFound = 0;
		while (true) {
			ConsumerRecords<Long, String> consumerRecords = consumer.poll(10000);
			if (consumerRecords.count() == 0) {
				noMessageFound++;
				if (noMessageFound > KafkaConstants.MAX_NO_MESSAGE_FOUND_COUNT)
					break;
				else
					continue;
			}
			// print each record.
			consumerRecords.forEach(record -> {
				System.out.printf("------------ message consumer --------- \n");
				System.out.println("Record Key: " + record.key());
				System.out.println("Record value: " + record.value());
				System.out.println("Record partition: " + record.partition());
				System.out.println("Record offset: " + record.offset());
				System.out.println("Server Kafka: " + kafka_server);
				System.out.printf("--------------------------------------- \n");
			});
			// commits the offset of record to broker.
			consumer.commitAsync();
		}
		consumer.close();
	}
	
}
