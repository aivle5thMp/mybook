package mp.infra;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import mp.domain.MyBook;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PolicyHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "point-response", groupId = "mybook-group")
    public void handlePointResponse(String message) {
        try {
            JsonNode json = objectMapper.readTree(message);
            String userId = json.get("user_id").asText();
            String bookId = json.get("book_id").asText();
            int pointUsed = json.get("point_used").asInt();

            System.out.println("💰 [PointResponse] user_id: " + userId + ", book_id: " + bookId + ", pointUsed: " + pointUsed);

            if (pointUsed == 0) {
                MyBook myBook = new MyBook();
                myBook.setUserId(userId);
                myBook.setBookId(bookId);
                myBook.setUsedPoints(0);
                myBook.setType("PURCHASE");
                myBook.setCreatedAt(new java.util.Date());

                MyBook.repository().save(myBook);
            }

        } catch (Exception e) {
            System.err.println("❌ Failed to parse point-response message: " + e.getMessage());
        }
    }
}




