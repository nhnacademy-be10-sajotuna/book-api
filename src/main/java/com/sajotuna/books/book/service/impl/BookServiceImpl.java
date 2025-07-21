package com.sajotuna.books.book.service.impl;


import com.sajotuna.books.book.OrderStockClient;
import com.sajotuna.books.book.controller.request.BookBatchRequest;
import com.sajotuna.books.book.controller.request.BookCreateRequest;
import com.sajotuna.books.book.controller.request.StockRequest;
import com.sajotuna.books.book.controller.response.BookResponse;
import com.sajotuna.books.book.controller.response.BookSummaryResponse;
import com.sajotuna.books.book.domain.Book;
import com.sajotuna.books.book.exception.BookNotFoundException; // 변경
import com.sajotuna.books.book.repository.BookRepository;
import com.sajotuna.books.search.BookSearchDocument;
import com.sajotuna.books.search.repository.BookSearchRepository;
import com.sajotuna.books.search.service.BookStatsService;
import com.sajotuna.books.book.service.BookService;
import com.sajotuna.books.category.domain.BookCategory;
import com.sajotuna.books.category.domain.Category;
import com.sajotuna.books.tag.domain.BookTag;
import com.sajotuna.books.tag.domain.Tag;
import com.sajotuna.books.category.service.CategoryService;
import com.sajotuna.books.like.repository.LikeRepository; // LikeRepository 추가
import com.sajotuna.books.tag.service.TagService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification; // 추가
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookSearchRepository bookSearchRepository;
    private final CategoryService categoryService;
    private final TagService tagService;
    private final LikeRepository likeRepository; // LikeRepository 주입
    private final OrderStockClient orderStockClient;
    private final EntityManager entityManager;
    private final BookStatsService bookStatsService;

    @Override
    public Page<BookResponse> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(BookResponse::new);
    }

    @Override
    public Page<BookResponse> searchBooks(Specification<Book> spec, Pageable pageable) {
        return bookRepository.findAll(spec, pageable)
                .map(BookResponse::new);
    }

    @Override
    public BookResponse getBookByIsbn(String isbn) {
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));

        // ES에서 조회수 증가 + 실시간 인기도 재계산
        bookStatsService.incrementViewCount(isbn);
        BookSearchDocument bookSearchDocument = bookSearchRepository.findById(isbn).orElseThrow(() -> new BookNotFoundException(isbn));

        return new BookResponse(book, bookSearchDocument);
    }

    @Override
    public void updateReviewInfo(String isbn, double rating) {
        // 책 존재 확인
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));

        // ES에서 현재 평점 정보 조회
        BookSearchDocument bookDoc = bookSearchRepository.findById(isbn).orElse(null);
        if (bookDoc == null) {
            // ES에 문서가 없으면 기본값으로 생성
            bookDoc = BookSearchDocument.from(book);
            bookSearchRepository.save(bookDoc);
        }
        
        // 평균 평점 계산: ((기존평균 * 기존리뷰수) + 새로운평점) / (기존리뷰수 + 1)
        double currentAverage = bookDoc.getAverageRating();
        int currentReviewCount = bookDoc.getReviewCount();
        
        double newAverageRating = ((currentAverage * currentReviewCount) + rating) / (currentReviewCount + 1);
        int newReviewCount = currentReviewCount + 1;
        
        // 소수점 1자리로 반올림
        newAverageRating = Math.round(newAverageRating * 10.0) / 10.0;
        
        // ES에서 평점/리뷰수 업데이트 (인기도 재계산 없음)
        bookStatsService.updateReviewStats(isbn, newAverageRating, newReviewCount);
    }



    @Override // 관리자
    public BookResponse getBookByIsbnByAdmin(String isbn) {
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));
        return new BookResponse(book);
    }


    @Override // 관리자
    public BookResponse createBook(BookCreateRequest request) {
        // 1. ISBN 중복 확인
        if (bookRepository.existsById(request.getIsbn())) {
            throw new IllegalArgumentException("이미 존재하는 ISBN입니다: " + request.getIsbn());
        }

        // 2. Book 엔티티 생성 및 기본 정보 설정
        Book book = new Book(
                request.getIsbn(),
                request.getTitle(),
                request.getAuthor(),
                request.getPublisher(),
                request.getPublicationDate(),
                request.getPageCount(),
                request.getImageUrl(),
                request.getDescription(),
                request.getOriginalPrice(),
                request.getSellingPrice(),
                request.getGiftWrappingAvailable(),
                request.getLikes()
        );

        // 3. 카테고리 처리
        if (request.getCategories() != null && !request.getCategories().isEmpty()) {
            List<Category> categories = categoryService.findAllByCategoryIds(request.getCategories());
            Set<BookCategory> bookCategories = new HashSet<>();
            for (Category category : categories) {
                BookCategory bookCategory = new BookCategory();
                bookCategory.setBook(book);
                bookCategory.setCategory(category);
                bookCategories.add(bookCategory);
            }
            book.setBookCategories(bookCategories);
        }

        // 4. 태그 처리
        if (request.getTagNames() != null && !request.getTagNames().isEmpty()) {
            Set<String> tagNames = new HashSet<>();
            List<String> tagList = List.of(request.getTagNames().split(","));
            tagList.forEach(tagName -> tagNames.add(tagName.trim()));
            Set<Tag> tags = tagService.findOrCreateTags(tagNames);
            Set<BookTag> bookTags = new HashSet<>();
            for (Tag tag : tags) {
                BookTag bookTag = new BookTag(tag, book);
                bookTags.add(bookTag);
            }
            book.setBookTags(bookTags);
        }

        // 5. 도서 저장
        Book savedBook = bookRepository.save(book);

        bookSearchRepository.save(BookSearchDocument.from(book)); // Es 반영
        // 6. 응답 DTO 반환
        return new BookResponse(savedBook);
    }

    @Override // 관리자
    public BookResponse updateBook(String isbn, BookCreateRequest request) {
        // 1. 해당 ISBN의 책이 존재하는지 확인
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));

        // 2. 도서 정보 업데이트 (Book 엔티티의 updateInfo 메서드 활용)
        book.updateInfo(request);

        // 3. 카테고리 업데이트 (기존 카테고리 삭제 후 새로 추가)
        book.getBookCategories().clear(); // 기존 카테고리 연결 제거
        if (request.getCategories() != null && !request.getCategories().isEmpty()) {
            List<Category> categories = categoryService.findAllByCategoryIds(request.getCategories());
            for (Category category : categories) {
                BookCategory bookCategory = new BookCategory();
                bookCategory.setBook(book);
                bookCategory.setCategory(category);
                book.getBookCategories().add(bookCategory);
            }
        }

        // 4. 태그 업데이트 (기존 태그 삭제 후 새로 추가)
        book.getBookTags().clear(); // 기존 태그 연결 제거
        entityManager.flush();
        if (request.getTagNames() != null && !request.getTagNames().isEmpty()) {
            Set<String> tagNames = new HashSet<>();
            List<String> tagList = List.of(request.getTagNames().split(","));
            tagList.forEach(tagName -> tagNames.add(tagName.trim()));
            Set<Tag> tags = tagService.findOrCreateTags(tagNames);
            for (Tag tag : tags) {
                BookTag bookTag = new BookTag(tag, book);
                book.getBookTags().add(bookTag);
            }
        }
        bookSearchRepository.save(BookSearchDocument.from(book)); // Es 반영

        // 5. 응답 DTO 반환
        return new BookResponse(book);
    }

    // 도서 삭제 (추가된 부분)
    @Override // 관리자
    public void deleteBook(String isbn) {
        // 1. 해당 ISBN의 책이 존재하는지 확인
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));

        // 2. 해당 도서와 연관된 좋아요 데이터를 먼저 삭제 (Book에 @OneToMany likes 매핑이 없을 경우)
        // 만약 Like 엔티티에 Book으로의 @ManyToOne 단방향 매핑만 있고, Book 엔티티에 Like에 대한 @OneToMany 매핑이 없다면
        // JPA의 cascade 기능이 작동하지 않으므로, 명시적으로 삭제해야 합니다.
        // likeRepository.deleteByBook(book); // LikeRepository에 해당 메서드 필요

        // 3. 도서 삭제 (BookCategory, BookTag는 Book 엔티티에 cascade 및 orphanRemoval 설정되어 있어 함께 삭제됨)
        bookRepository.delete(book);
        bookSearchRepository.deleteById(isbn); // Es 반영
    }

    @Override
    public List<BookSummaryResponse> getBooksByIsbns(BookBatchRequest request) {
        List<Book> books = bookRepository.findAllById(request.getIsbns());
        if (books.isEmpty()) {
            throw new BookNotFoundException(request.getIsbns().toString());
        }

        return books.stream()
                .map(book ->{
                    List<Long> categoryIds = book.getBookCategories().stream()
                            .flatMap(bookCategory -> bookCategory.getCategory().getPathFromRoot().stream())
                            .map(Category::getId)
                            .distinct()
                            .toList();
                    return BookSummaryResponse.from(book, categoryIds);
                })
                .toList();
    }

    @Override
    public void updateBookStock(String isbn, Integer stock) {
        if (!bookRepository.existsById(isbn)) {
            throw new BookNotFoundException(isbn);
        }
        // TODO: 외부 서비스와 연동하여 재고 업데이트

        orderStockClient.updateStock(new StockRequest(isbn,stock));

    }

    @Override
    public BookResponse updateBookLikes(String isbn, Integer likes) {
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));
        book.updateLikes(likes);
        Book updatedBook = bookRepository.save(book);
        return new BookResponse(updatedBook);
    }
}