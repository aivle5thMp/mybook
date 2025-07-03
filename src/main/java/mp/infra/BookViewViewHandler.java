package mp.infra;

import lombok.RequiredArgsConstructor;
import mp.domain.BookPurchased;
import mp.domain.BookView;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookViewViewHandler {

    private final BookViewRepository bookViewRepository;

    @KafkaListener(topics = "mp", groupId = "mybook")
    public void handleBookPurchased(BookPurchased event) {
        if (!event.validate()) return;

        // 포인트가 0일 때만 상태 갱신
        if (event.getPoint() != 0) return;

        // userId + bookId로 BookView 찾기
        List<BookView> bookViewList = bookViewRepository.findByUserId(event.getUserId());
        for (BookView bookView : bookViewList) {
            if (bookView.getBookId().equals(event.getBookId())) {
                bookView.setBookStatus("PURCHASED");
                bookViewRepository.save(bookView);
            }
        }
    }
}



