package com.sajotuna.books.book.service.impl;

import com.sajotuna.books.book.controller.request.BookBatchRequest;
import com.sajotuna.books.book.controller.request.BookCreateRequest;
import com.sajotuna.books.book.controller.response.BookResponse;
import com.sajotuna.books.book.controller.response.BookSummaryResponse;
import com.sajotuna.books.book.domain.Book;
import com.sajotuna.books.book.exception.BookNotFoundException;
import com.sajotuna.books.book.repository.BookRepository;
import com.sajotuna.books.search.BookSearchDocument;
import com.sajotuna.books.search.repository.BookSearchRepository;
import com.sajotuna.books.book.service.BookService;
import com.sajotuna.books.category.domain.BookCategory;
import com.sajotuna.books.category.domain.Category;
import com.sajotuna.books.tag.domain.BookTag;
import com.sajotuna.books.tag.domain.Tag;
import com.sajotuna.books.category.service.CategoryService;
import com.sajotuna.books.like.repository.LikeRepository;
import com.sajotuna.books.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // jakarta.transaction.Transactional 대신 이 임포트를 사용해주세요.

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional // Spring의 @Transactional 어노테이션 사용 권장
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookSearchRepository bookSearchRepository;
    private final CategoryService categoryService;
    private final TagService tagService;
    private final LikeRepository likeRepository;

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

        book.incrementViewCount();
        book.calculatePopularity();

        bookRepository.save(book); //db 반영
        bookSearchRepository.save(BookSearchDocument.from(book)); // Es 반영
        return new BookResponse(book);
    }

    @Override
    public void updateReviewInfo(String isbn, double rating) {
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));

        book.calculateRating(rating);
        book.incrementReviewCount();

        bookRepository.save(book); //  DB 반영

        // Elasticsearch에도 반영
        bookSearchRepository.save(BookSearchDocument.from(book));
    }


    @Override
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

        // 재고 초기화
        book.setStock(0);

        // 3. 카테고리 처리
        if (request.getCategories() != null && !request.getCategories().isEmpty()) {
            List<Category> categories = categoryService.findAllByCategoryIds(request.getCategories());
            Set<BookCategory> bookCategories = categories.stream()
                    .map(category -> {
                        BookCategory bookCategory = new BookCategory();
                        bookCategory.setBook(book);
                        bookCategory.setCategory(category);
                        return bookCategory;
                    })
                    .collect(Collectors.toSet());
            book.setBookCategories(bookCategories); // 새로 생성된 Set 할당
        }

        // 4. 태그 처리
        if (request.getTagNames() != null && !request.getTagNames().isEmpty()) {
            Set<String> tagNames = new HashSet<>(List.of(request.getTagNames().split(",")));
            Set<Tag> tags = tagService.findOrCreateTags(tagNames);
            Set<BookTag> bookTags = tags.stream()
                    .map(tag -> new BookTag(tag, book))
                    .collect(Collectors.toSet());
            book.setBookTags(bookTags); // 새로 생성된 Set 할당
        }

        // 5. 도서 저장
        Book savedBook = bookRepository.save(book);

        // Elasticsearch 문서도 업데이트
        bookSearchRepository.save(BookSearchDocument.from(savedBook));

        // 6. 응답 DTO 반환
        return new BookResponse(savedBook);
    }

    @Override
    public BookResponse updateBook(String isbn, BookCreateRequest request) {
        // 1. 해당 ISBN의 책이 존재하는지 확인
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));

        // 2. 도서 정보 업데이트 (Book 엔티티의 updateInfo 메서드 활용)
        book.updateInfo(request);

        // --- 수정된 부분 시작 ---
        // 3. 카테고리 업데이트 (기존 컬렉션에 직접 변경)
        // JPA의 orphanRemoval = true와 함께 사용할 때 clear() 후 addAll()이 권장되는 방식
        book.getBookCategories().clear(); // 기존 카테고리 연결 제거
        if (request.getCategories() != null && !request.getCategories().isEmpty()) {
            List<Category> categories = categoryService.findAllByCategoryIds(request.getCategories());
            Set<BookCategory> newBookCategoriesToAdd = categories.stream()
                    .map(category -> {
                        BookCategory bookCategory = new BookCategory();
                        bookCategory.setBook(book);
                        bookCategory.setCategory(category);
                        return bookCategory;
                    })
                    .collect(Collectors.toSet());
            book.getBookCategories().addAll(newBookCategoriesToAdd); // 기존 컬렉션에 새 요소 추가
        }

        // 4. 태그 업데이트 (기존 컬렉션에 직접 변경)
        book.getBookTags().clear(); // 기존 태그 연결 제거
        if (request.getTagNames() != null && !request.getTagNames().isEmpty()) {
            Set<String> tagNames = new HashSet<>(List.of(request.getTagNames().split(",")));
            Set<Tag> tags = tagService.findOrCreateTags(tagNames);
            Set<BookTag> newBookTagsToAdd = tags.stream()
                    .map(tag -> new BookTag(tag, book))
                    .collect(Collectors.toSet());
            book.getBookTags().addAll(newBookTagsToAdd); // 기존 컬렉션에 새 요소 추가
        }
        // --- 수정된 부분 끝 ---

        // 5. 도서 저장 (변경사항 반영)
        Book updatedBook = bookRepository.save(book);

        // Elasticsearch 문서도 업데이트
        bookSearchRepository.save(BookSearchDocument.from(updatedBook));

        // 6. 응답 DTO 반환
        return new BookResponse(updatedBook);
    }

    @Override
    public void deleteBook(String isbn) {
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));

        // 해당 도서와 연관된 좋아요 데이터를 먼저 삭제
        likeRepository.deleteByBook(book);

        // 도서 삭제 (BookCategory, BookTag는 Book 엔티티에 cascade 및 orphanRemoval 설정되어 있어 함께 삭제됨)
        bookRepository.delete(book);

        // Elasticsearch 문서도 삭제
        bookSearchRepository.deleteById(isbn);
    }

    @Override
    public List<BookSummaryResponse> getBooksByIsbns(BookBatchRequest request) {
        List<Book> books = bookRepository.findAllById(request.getIsbns());
        if (books.isEmpty()) {
            throw new BookNotFoundException("해당 ISBN의 도서가 없습니다.");
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
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));

        book.updateStock(stock); // Book 엔티티의 재고 필드를 직접 업데이트
        bookRepository.save(book); // 변경사항 저장

        // Elasticsearch 문서도 업데이트
        bookSearchRepository.save(BookSearchDocument.from(book));
    }

    @Override
    public BookResponse updateBookLikes(String isbn, Integer likes) {
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));
        book.updateLikes(likes);
        Book updatedBook = bookRepository.save(book);
        // Elasticsearch 문서도 업데이트
        bookSearchRepository.save(BookSearchDocument.from(updatedBook));
        return new BookResponse(updatedBook);
    }
}