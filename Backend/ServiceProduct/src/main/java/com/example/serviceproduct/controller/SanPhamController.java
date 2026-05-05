package com.example.serviceproduct.controller;

import com.example.serviceproduct.dto.response.SanPhamResponse;
import com.example.serviceproduct.entity.CongThuc;
import com.example.serviceproduct.entity.SanPham;
import com.example.serviceproduct.service.SanPhamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/san-pham")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:5173") // Cho phép Frontend (React/Vue/Swing call API)
public class SanPhamController {

    private final SanPhamService sanPhamService;

    // GET: /api/v1/san-pham
    @GetMapping
    public ResponseEntity<List<SanPhamResponse>> getAll() {
        return ResponseEntity.ok(sanPhamService.getAllSanPham());
    }
    @GetMapping("/{id}")
    public ResponseEntity<SanPhamResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(sanPhamService.getSanPhamById(id));
    }

    // POST: /api/v1/san-pham
    @PostMapping
    public ResponseEntity<SanPham> create(@RequestBody SanPham sanPham) {
        return ResponseEntity.ok(sanPhamService.saveSanPham(sanPham));
    }

    // DELETE: /api/v1/san-pham/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        sanPhamService.deleteSanPham(id);
        return ResponseEntity.noContent().build();
    }
}