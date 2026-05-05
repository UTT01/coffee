package com.example.servicecafe.client;

import com.example.servicecafe.dto.TruKhoRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;

@FeignClient(name = "service-store")
public interface StoreClient {
    // Gọi API trừ kho mà chúng ta vừa tạo ban nãy
    @PostMapping("/nguyen-lieu/tru-kho")
    void truKhoNguyenLieu(@RequestBody List<TruKhoRequest> requests);
}