package Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.kafka.common.protocol.types.Field;

public class CDCEventMap implements MapFunction<String, ObjectNode> {
    @Override
    public ObjectNode map(String stringEvent) throws Exception {
        ObjectNode jsonNode = (ObjectNode) new ObjectMapper().readTree(stringEvent);
        ObjectNode payloadNode = jsonNode.has("payload") ? (ObjectNode) jsonNode.get("payload") : null;
        ObjectNode event = JsonNodeFactory.instance.objectNode();
        ObjectNode meta = JsonNodeFactory.instance.objectNode();
        if (payloadNode!=null){
            String op = payloadNode.has("op") ? payloadNode.get("op").asText() : null;
            meta.put("op", op);
            ObjectNode sourceNode = payloadNode.has("source") ? (ObjectNode) payloadNode.get("source") : null;
            meta.put("connector",  sourceNode.has("connector") ? sourceNode.get("connector").asText() : null);
            meta.put("database",  sourceNode.has("db") ? sourceNode.get("db").asText() : null);
            meta.put("schema",  sourceNode.has("schema") ? sourceNode.get("schema").asText() : null);
            meta.put("table",  sourceNode.has("table") ? sourceNode.get("table").asText() : null);
            event.put("_meta", meta);
            if (op.equals("u"))
                event.put("before", payloadNode.has("before") ? (ObjectNode) payloadNode.get("before") : null);
            event.put("after", payloadNode.has("after") ? (ObjectNode) payloadNode.get("after") : null);
        }
        return event;
    }
}
