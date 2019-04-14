package com.kafka.consumer;

import java.util.Properties;
import java.util.Collections;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

@SpringBootApplication
public class ConsumerApplication {
	
	private static String kafka_server = System.getenv("KAFKA_SERVER");
	private static String kafka_topic = System.getenv("KAFKA_TOPIC");
	private static String kafka_count_msg = System.getenv("KAFKA_COUNT_MSG");

	public static void main(String[] args) {
		SpringApplication.run(ConsumerApplication.class, args);
		runConsumer();
	}

	public interface IKafkaConstants {
		int num_msg_kafka = Integer.parseInt(kafka_count_msg);
		public static String KAFKA_BROKERS = kafka_server+":9092";
		public static Integer MESSAGE_COUNT = 1000;
		public static String CLIENT_ID = "client1";
		public static String TOPIC_NAME = kafka_topic;
		public static String GROUP_ID_CONFIG = "consumerGroup1";
		public static Integer MAX_NO_MESSAGE_FOUND_COUNT = num_msg_kafka;
		public static String OFFSET_RESET_LATEST = "latest";
		public static String OFFSET_RESET_EARLIER = "earliest";
		public static Integer MAX_POLL_RECORDS = 1;
	}

	public static Consumer<Long, String> createConsumer() {
		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, IKafkaConstants.KAFKA_BROKERS);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, IKafkaConstants.GROUP_ID_CONFIG);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, IKafkaConstants.MAX_POLL_RECORDS);
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, IKafkaConstants.OFFSET_RESET_EARLIER);
		Consumer<Long, String> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Collections.singletonList(IKafkaConstants.TOPIC_NAME));
		return consumer;
	}

	static void runConsumer() {
		Consumer<Long, String> consumer = createConsumer();
		int noMessageFound = 0;
		while (true) {
			ConsumerRecords<Long, String> consumerRecords = consumer.poll(1000);
			// 1000 is the time in milliseconds consumer will wait if no record is found at
			// broker.
			if (consumerRecords.count() == 0) {
				noMessageFound++;
				if (noMessageFound > IKafkaConstants.MAX_NO_MESSAGE_FOUND_COUNT)
					// If no message found count is reached to threshold exit loop.
					break;
				else
					continue;
			}
			// print each record.
			consumerRecords.forEach(record -> {
				System.out.println("Record Key " + record.key());
				System.out.println("Record value " + record.value());
				System.out.println("Record partition " + record.partition());
				System.out.println("Record offset " + record.offset());
			});
			// commits the offset of record to broker.
			consumer.commitAsync();
		}
		consumer.close();
	}

}