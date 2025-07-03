//package mp.infra.dto;
//
//public class BookHistoryResponse {
//
//    private String bookId;
//    private String authorName;
//    private String title;
//    private String category;
//    private String imageUrl;
//
//    // 생성자
//    public BookHistoryResponse(String bookId, String authorName, String title, String category, String imageUrl) {
//        this.bookId = bookId;
//        this.authorName = authorName;
//        this.title = title;
//        this.category = category;
//        this.imageUrl = imageUrl;
//    }
//
//    // Getters & Setters
//    public String getBookId() {
//        return bookId;
//    }
//
//    public void setBookId(String bookId) {
//        this.bookId = bookId;
//    }
//
//    public String getAuthorName() {
//        return authorName;
//    }
//
//    public void setAuthorName(String authorName) {
//        this.authorName = authorName;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public String getCategory() {
//        return category;
//    }
//
//    public void setCategory(String category) {
//        this.category = category;
//    }
//
//    public String getImageUrl() {
//        return imageUrl;
//    }
//
//    public void setImageUrl(String imageUrl) {
//        this.imageUrl = imageUrl;
//    }
//}

package mp.infra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookHistoryResponse {

    private String bookId;
    private String authorName;
    private String title;
    private String category;
    private String imageUrl;
}


