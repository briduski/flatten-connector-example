package bri.kafka.producer;

import bri.avro.kafka.flatten.dto.Address;
import bri.avro.kafka.flatten.dto.Client;
import bri.avro.kafka.flatten.dto.Email;
import bri.avro.kafka.flatten.dto.Name;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class KafkaProducerClientAvro implements Runnable {
    public static final String BROKER = "localhost:29092";
    public static final String SCHEMA_REGISTRY = "http://localhost:8081";
    public static final String TOPIC = "client-state";

    Producer<String, Client> producer;
    char exit = 0;

    public static void main(String[] args) {
        KafkaProducerClientAvro avroProducer = new KafkaProducerClientAvro();
        Thread t1 = new Thread(avroProducer);
        t1.start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        avroProducer.finish();
    }

    public KafkaProducerClientAvro() {
        this.producer = new KafkaProducer<>(getProperties());
    }
    public void finish() {
        System.out.println("Bye!");
        exit = 'y';
    }
    @Override
    public void run() {
        try {
            System.out.println(Client.getClassSchema());
            System.out.println(Client.SCHEMA$.getName());
            String key = "A1";
            System.out.println("record:key:::: " + key);
            Client Client = generateAvro(key);
            send(key, Client);
            producer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void send(String key, Client Client) {
        ProducerRecord<String, Client> record = new ProducerRecord<>(TOPIC, key, Client);
        producer.send(record, ((metadata, exception) -> {
                    if (exception == null) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("key", key);
                        data.put("value", Client.toString());
                        data.put("topic", metadata.topic());
                        data.put("partition", metadata.partition());
                        data.put("offset", metadata.offset());
                        data.put("timestamp", metadata.timestamp());
                        System.out.println("Producer => " + data.toString());
                    } else {
                        exception.printStackTrace();
                    }
                })
        );
    }
    public static Client generateAvro(String key) {
        Client client1 = new Client();
        client1.setStatus("Active");
        System.out.println("record:id:::: " + key);
        client1.setId(key);
        client1.setUnusedid("A0A1A1");
        client1.setTimestamp(System.currentTimeMillis());
        client1.setPet(Arrays.asList("Cat1", "Dog1"));
        client1.setName(Name.newBuilder().setFirstname("Mary").setLastname("Streep").setMiddlename("Louise").setShortname("Meryl Streep").build());
        Email email1 = Email.newBuilder().setType("Primary").setEmail("bbbb@gmail.com").build();
        Email email2 = Email.newBuilder().setType("Secondary").setEmail("aaaa@gmail.com").build();
        client1.setEmail(Arrays.asList(email1, email2));
        Address addr1 = Address.newBuilder().setAddress("Oxford Street").setCountry("UK")
                .setPostalArea("London").setPostalCode("W1D 1BS").setType("Primary").build();
        Address addr2 = Address.newBuilder().setAddress("Abbey Road").setCountry("UK")
                .setPostalArea("London").setPostalCode("NW8 0AE").setType("Secondary").build();
        client1.setAddress(Arrays.asList(addr1, addr2));
        return client1;
    }
    static Properties getProperties() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BROKER);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "client-producer-1");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, SCHEMA_REGISTRY);
        return props;
    }
}
