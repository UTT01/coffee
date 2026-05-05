package com.example.servicecafe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

// BillResponseDTO.java
@Data
@AllArgsConstructor
public class ThanhToanDTO {
    private String maHoaDon;
    private String maBan;
    private Double tongTien;
    private List<BillItemDTO> items;

    @Data
    @AllArgsConstructor
    public static class BillItemDTO {
        private String maSanPham;
        private String tenSanPham;
        private Integer soLuong;
        private Double giaBan;
        private Double thanhTien;
    }
}
