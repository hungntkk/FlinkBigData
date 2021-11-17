package Map;
import Entities.User;
import EntryProcessorsImpl.UserEntryProcessorImpl;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastJsonValue;
import com.hazelcast.map.IMap;
import org.apache.flink.api.common.functions.MapFunction;

public class CDCEventMapProcess implements MapFunction<ObjectNode, User> {
    @Override
    public User map(ObjectNode event) throws Exception {
        ObjectNode afterNode = (ObjectNode) event.get("after");

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setClusterName("dev");
        clientConfig.getNetworkConfig().addAddress("10.1.6.110:5702");

        HazelcastInstance client = HazelcastClient.newHazelcastClient(clientConfig);

        IMap<String, HazelcastJsonValue> wordMap = client.getMap("user2");

        HazelcastJsonValue hazelcastJsonValue = new HazelcastJsonValue(afterNode.toString());
        String id = afterNode.get("id").asText();
        //if (!wordMap.containsKey(id)){
        wordMap.put(id, hazelcastJsonValue);
        User user = new User(afterNode);
        /*
        }else{
            user = wordMap.executeOnKey(String.valueOf(id), new UserEntryProcessorImpl());
        }
         */
        return user;
    }
}
