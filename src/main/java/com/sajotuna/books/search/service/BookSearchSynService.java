package com.sajotuna.books.search.service;

import com.sajotuna.books.book.repository.BookRepository;
import com.sajotuna.books.search.BookSearchDocument;
import com.sajotuna.books.search.repository.BookSearchRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookSearchSynService {

    private final BookRepository bookRepository;
    private final BookSearchRepository bookSearchRepository;

    @Transactional
    public void updateSearchStats(List<String> isbns) {
        for(String isbn : isbns) {
            bookRepository.findById(isbn).ifPresent(book -> {
                book.incrementSearchCount();
                book.calculatePopularity();
                bookRepository.save(book);
                bookSearchRepository.save(BookSearchDocument.from(book)); //ES 반영
            });
        }
    }
}
