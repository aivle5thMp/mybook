package mp.infra.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class BookKafkaProducer {

    private static final String TOPIC = "point.check.v1"; // 포인트 확인용 토픽

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void requestPointCheck(String userId, String bookId, int pointUsed) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("user_id", userId);
        payload.put("book_id", bookId);
        payload.put("point_used", pointUsed);

        kafkaTemplate.send(TOPIC, payload);
    }
}

