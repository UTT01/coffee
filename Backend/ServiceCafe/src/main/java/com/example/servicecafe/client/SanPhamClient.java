package com.example.servicecafe.client;

import com.example.servicecafe.dto.SanPhamResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@FeignClient(name = "product-service", url = "http://localhost:8083")
public interface SanPhamClient {
    @GetMapping("/{id}")
    SanPhamResponseDTO getSanPhamById(@PathVariable("id") String id);
    // Thêm method này vào interface SanPhamClient
}

