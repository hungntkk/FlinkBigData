package KafkaConnectorFlink;

import Entities.User;
import Map.CDCEventMap;
import Map.CDCEventMapProcess;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.util.concurrent.*;

public class CDCApplication {
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        /*
        Properties p = new Properties();
        p.setProperty("bootstrap.servers", "10.1.6.110:9000");

        DataStream < String > kafkaData = env.addSource(new FlinkKafkaConsumer < String > ("bankserver1.bank.holding",
                            new SimpleStringSchema(),
                            p));
         */
        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers("10.1.6.110:9000")
                .setTopics("customerserver8.customer.users")
                .setGroupId("my-groupid")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();
        DataStream < String > kafkaData  = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source").setParallelism(5);

        DataStream <ObjectNode> objectStreamData = kafkaData.map(new CDCEventMap()).setParallelism(5);

        DataStream <User> UserObjStreamData = objectStreamData.map(new CDCEventMapProcess()).setParallelism(5);

        UserObjStreamData.addSink(
                JdbcSink.sink(
                        "insert into customer.users (id, username, email, phone, coin, isdeleted, isonline) " +
                            "values (?, ?, ?, ?, ?, ?, ?)" +
                            "ON CONFLICT (id)" +
                            "DO UPDATE SET username=EXCLUDED.username, email=EXCLUDED.email, phone=EXCLUDED.phone, " +
                            "   coin=EXCLUDED.coin, isdeleted=EXCLUDED.isdeleted, isonline=EXCLUDED.isonline",
                        (statement, user) -> {
                            statement.setInt(1, user.getId());
                            statement.setString(2, user.getUserName());
                            statement.setString(3, user.getEmailAddress());
                            statement.setString(4, user.getPhoneNumber());
                            statement.setDouble(5, user.getCoin());
                            statement.setInt(6, user.getIsOnline());
                            statement.setInt(7, user.getIsDeleted());
                        },
                        JdbcExecutionOptions.builder()
                                .withBatchSize(1000)
                                .withBatchIntervalMs(200)
                                .withMaxRetries(5)
                                .build(),
                        new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                                .withUrl("jdbc:postgresql://10.1.6.110:5433/bigdata")
                                .withDriverName("org.postgresql.Driver")
                                .withUsername("bigdata")
                                .withPassword("password")
                                .build()
                )).setParallelism(5);
        env.execute("Data pipeline");
    }
}