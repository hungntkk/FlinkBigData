package EntryProcessorsImpl;

import Entities.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hazelcast.core.HazelcastJsonValue;
import com.hazelcast.map.EntryProcessor;
import java.util.Map;

public class UserEntryProcessorImpl implements EntryProcessor<String, HazelcastJsonValue, User> {
    public User process ( Map.Entry<String, HazelcastJsonValue> entry ) {
        HazelcastJsonValue hazelcastJsonValue = entry.getValue();
        ObjectMapper mapper = new ObjectMapper();
        try {
            User user = mapper.readValue(hazelcastJsonValue.toString(), User.class);
            user.addjustCoin(10.0);
            User returnValues = user;
            ObjectNode userNode = user.getUserNode();
            entry.setValue(new HazelcastJsonValue(userNode.toString()));
            return returnValues;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public EntryProcessor<String, HazelcastJsonValue, User> getBackupProcessor() {
        return UserEntryProcessorImpl.this;
    }
}
