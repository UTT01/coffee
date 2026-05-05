package com.example.servicecafe.dto;

import lombok.Data;
import java.util.List;

@Data
public class SanPhamResponseDTO {
    private String maSanPham;
    private String tenSanPham;
    private Double donGia;
    private String trangThai;
    
    // Biến này đặc biệt quan trọng để chứa danh sách công thức
    private List<CongThucDTO> danhSachCongThuc; 
}