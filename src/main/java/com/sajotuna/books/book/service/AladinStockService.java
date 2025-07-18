package com.sajotuna.books.book.service;

import com.sajotuna.books.book.OrderStockClient;
import com.sajotuna.books.book.controller.request.StockRequest;
import com.sajotuna.books.book.domain.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AladinStockService {

    private final OrderStockClient orderStockClient;

    public void syncStockWithOrderApi(List<Book> books) {

        List<StockRequest> stockRequests = books.stream()
                .map(book -> new StockRequest(book.getIsbn(), 100))
                .toList();

        if (!stockRequests.isEmpty()) {
            orderStockClient.createStocks(stockRequests);
        }
    }
}
