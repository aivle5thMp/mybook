package mp.infra;

import lombok.RequiredArgsConstructor;
import mp.domain.MyBook;
import mp.domain.MyBookRepository;
import mp.infra.dto.BookReadRequest;
import mp.infra.dto.PurchaseRequest;
import mp.util.UserHeaderUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/myBook")
@RequiredArgsConstructor
public class MyBookController {

    private final MyBookRepository myBookRepository;
    private final RestTemplate restTemplate;

    @Value("${books.service-url}")
    private String booksServiceUrl;

    // ✅ 1. 도서 구매
    @PostMapping("/purchase")
    public ResponseEntity<?> purchaseBook(@RequestBody PurchaseRequest request, HttpServletRequest httpRequest) {
        // 인증 확인
        if (!UserHeaderUtil.isAuthenticated(httpRequest)) {
            return ResponseEntity.status(401).body(Map.of("message", "인증이 필요합니다."));
        }

        try {
            // Gateway에서 전달한 사용자 정보 가져오기
            UUID userId = UserHeaderUtil.getUserId(httpRequest);
            boolean isSubscribed = UserHeaderUtil.isUserSubscribed(httpRequest);

            // MyBook 엔티티 생성 및 저장
            MyBook myBook = new MyBook();
            myBook.setUserId(userId);
            myBook.setBookId(request.getBookId());
            myBook.setTitle(request.getTitle());
            myBook.setAuthorName(request.getAuthorName());
            myBook.setCategory(request.getCategory());
            myBook.setImageUrl(request.getImageUrl());
            myBook.setCreatedAt(new Date());
            
            if (isSubscribed) {
                // 구독자: 포인트 사용 없음
                myBook.setPoint(0);
            } else {
                // 비구독자: 책 가격만큼 포인트 사용
                int point = request.getPoint();
                if (point < 0) {
                    return ResponseEntity.badRequest().body(Map.of(
                        "message", "포인트는 0 이상이어야 합니다."
                    ));
                }
                myBook.setPoint(point);
            }
            
            myBookRepository.save(myBook);

            String message = isSubscribed ? "✅ 구독자: 구매 완료 (포인트 0원)" : "✅ 비구독자: 구매 완료 (포인트 " + request.getPoint() + "원)";
            
            return ResponseEntity.ok(Map.of(
                    "message", message,
                    "book_id", request.getBookId(),
                    "point", myBook.getPoint(),
                    "title", myBook.getTitle(),
                    "author_name", myBook.getAuthorName(),
                    "category", myBook.getCategory(),
                    "image_url", myBook.getImageUrl()
            ));
        } catch (Exception e) {
            e.printStackTrace(); // 로그에 스택 트레이스 출력
            return ResponseEntity.status(500).body(Map.of(
                    "message", "도서 구매 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }

    // ✅ 2. 구매 이력 조회
    @GetMapping("/history")
    public ResponseEntity<?> getBookHistory(HttpServletRequest request) {
        // 인증 확인
        if (!UserHeaderUtil.isAuthenticated(request)) {
            return ResponseEntity.status(401).body(Map.of("message", "인증이 필요합니다."));
        }

        try {
            // Gateway에서 전달한 사용자 ID 가져오기
            UUID userId = UserHeaderUtil.getUserId(request);
            
            // 구매 이력 조회
            List<MyBook> myBooks = myBookRepository.findByUserId(userId);
            
            return ResponseEntity.ok(myBooks);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "message", "구매 이력 조회 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }

    // ✅ 3. 도서 열람
    @GetMapping("/read/{bookId}")
    public ResponseEntity<?> readBook(@PathVariable UUID bookId, HttpServletRequest httpRequest) {
        // 인증 확인
        if (!UserHeaderUtil.isAuthenticated(httpRequest)) {
            return ResponseEntity.status(401).body(Map.of("message", "인증이 필요합니다."));
        }

        try {
            // Gateway에서 전달한 사용자 ID 가져오기
            UUID userId = UserHeaderUtil.getUserId(httpRequest);

            // 구매한 도서 정보 가져오기
            Optional<MyBook> myBookOpt = myBookRepository.findByUserId(userId).stream()
                    .filter(book -> book.getBookId().equals(bookId))
                    .findFirst();

            if (myBookOpt.isEmpty()) {
                return ResponseEntity.status(403).body(Map.of("message", "구매하지 않은 도서입니다."));
            }

            MyBook myBook = myBookOpt.get();

            // Books 서비스의 read 엔드포인트 호출 (조회수 증가 + 도서 내용 반환)
            String url = booksServiceUrl + "/books/read/" + bookId;
            Map<String, Object> bookData = restTemplate.getForObject(url, Map.class);

            if (bookData == null) {
                return ResponseEntity.notFound().build();
            }

            // MyBook 정보와 Books 서비스 정보를 합쳐서 응답
            Map<String, Object> response = new HashMap<>();
            response.put("book_id", myBook.getBookId());
            response.put("title", myBook.getTitle());
            response.put("author_name", myBook.getAuthorName());
            response.put("category", myBook.getCategory());
            response.put("image_url", myBook.getImageUrl());
            response.put("audio_url", bookData.get("audioUrl"));
            response.put("content", bookData.get("content"));
            response.put("created_at", myBook.getCreatedAt());
            // 로그 출력
            System.out.println("response: " + response);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "message", "도서 열람 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }

    // ✅ 4. 도서 구매 확인
    @GetMapping("/check/{bookId}")
    public ResponseEntity<?> checkPurchase(@PathVariable UUID bookId, HttpServletRequest request) {
        // 인증 확인
        if (!UserHeaderUtil.isAuthenticated(request)) {
            return ResponseEntity.status(401).body(Map.of("message", "인증이 필요합니다."));
        }

        try {
            // Gateway에서 전달한 사용자 ID 가져오기
            UUID userId = UserHeaderUtil.getUserId(request);

            // 구매 여부 확인
            boolean isPurchased = myBookRepository.findByUserId(userId).stream()
                    .anyMatch(book -> book.getBookId().equals(bookId));

            return ResponseEntity.ok(Map.of("is_purchased", isPurchased));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "message", "구매 확인 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }
}






