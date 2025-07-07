package com.sajotuna.books.book;

import com.sajotuna.books.book.controller.request.AladinStockRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "order-api")
public interface OrderStockClient {

    @PostMapping("/api/stocks/batch")
    void updateStock(@RequestBody List<AladinStockRequest> stockRequests);
}
