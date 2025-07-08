package com.sajotuna.books.book.service;

import com.sajotuna.books.book.OrderStockClient;
import com.sajotuna.books.book.controller.request.StockRequest;
import com.sajotuna.books.book.controller.response.AladinBookResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AladinStockService {

    private final OrderStockClient orderStockClient;

    public void syncStockWithOrderApi(List<AladinBookResponse> books) {

        List<StockRequest> stockRequests = books.stream()
                .filter(book -> book.getStock() != null && book.getStock() > 0)
                .map(book -> new StockRequest(book.getIsbn(), book.getStock()))
                .toList();

        if (!stockRequests.isEmpty()) {
            orderStockClient.createStocks(stockRequests);
        }
    }
}
