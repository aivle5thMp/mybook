package mp.infra;

import lombok.RequiredArgsConstructor;
import mp.domain.MyBook;
import mp.domain.MyBookRepository;
import mp.infra.dto.BookHistoryResponse;
import mp.infra.dto.BookReadRequest;
import mp.infra.dto.PurchaseRequest;
import mp.infra.kafka.BookKafkaProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/mybook")
@RequiredArgsConstructor
public class MyBookController {

    private final MyBookRepository myBookRepository;
    private final BookViewRepository bookViewRepository;
    private final BookKafkaProducer bookKafkaProducer;
    private final RestTemplate restTemplate;

    @Value("${books.service-url}")
    private String booksServiceUrl;

    // ✅ 1. 도서 구매
    @PostMapping("/purchase")
    public ResponseEntity<?> purchaseBook(@RequestBody PurchaseRequest request) {
        String userId = request.getUserId();
        boolean isSubscribed = request.isSubscribed(); // 클라이언트가 구독 여부도 함께 전달

        if (isSubscribed) {
            MyBook myBook = new MyBook();
            myBook.setUserId(userId);
            myBook.setBookId(request.getBookId());
            myBook.setType("PURCHASE");
            myBook.setUsedPoints(0); // 구독자는 포인트 사용 없음
            myBook.setCreatedAt(new Date());
            myBookRepository.save(myBook);

            return ResponseEntity.ok(Map.of(
                    "message", "✅ 구독자: 구매 완료",
                    "book_id", request.getBookId()
            ));
        }

        // 비구독자: 포인트 체크용 Kafka 전송
        bookKafkaProducer.requestPointCheck(userId, request.getBookId(), request.getPointUsed());

        return ResponseEntity.accepted().body(Map.of(
                "message", "🕐 비구독자: 포인트 확인 중",
                "book_id", request.getBookId()
        ));
    }

    // ✅ 2. 구매 이력 조회
    @GetMapping("/history")
    public List<BookHistoryResponse> getBookHistory(@RequestParam("user_id") String userId) {
        return bookViewRepository.findByUserId(userId).stream()
                .map(view -> new BookHistoryResponse(
                        view.getBookId(),
                        view.getAuthorName(),
                        view.getTitle(),
                        view.getCategory(),
                        view.getImageUrl()
                ))
                .collect(Collectors.toList());
    }

    // ✅ 3. 도서 열람 (단순 조회)
    @PostMapping("/read")
    public ResponseEntity<?> readBook(@RequestBody BookReadRequest request) {
        String url = booksServiceUrl + "/books/" + request.getBookId();
        Map<String, Object> bookData = restTemplate.getForObject(url, Map.class);

        return ResponseEntity.ok(Map.of(
                "content", bookData.get("content"),
                "audio_url", bookData.get("audio_url"),
                "image_url", bookData.get("image_url"),
                "updated_at", ZonedDateTime.now(ZoneId.systemDefault()).toString()
        ));
    }
}






