package com.example.servicestore.service;

import com.example.servicestore.dto.TruKhoRequest;
import com.example.servicestore.entity.NguyenLieu;
import com.example.servicestore.repository.NguyenLieuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NguyenLieuService {
    
    private final NguyenLieuRepository repository;

    // Lấy tất cả
    public List<NguyenLieu> getAllNguyenLieu() {
        return repository.findAll();
    }

    // Thêm mới hoặc Cập nhật
    public NguyenLieu saveNguyenLieu(NguyenLieu nguyenLieu) {
        return repository.save(nguyenLieu);
    }

    // Xóa
    public void deleteNguyenLieu(String maNguyenLieu) {
        repository.deleteById(maNguyenLieu);
    }

    // Chức năng trừ kho khi bán hàng
    @Transactional
    public void truKho(List<TruKhoRequest> requests) {
        for (TruKhoRequest req : requests) {
            // Tìm nguyên liệu trong kho
            NguyenLieu nl = repository.findById(req.getMaNguyenLieu())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy nguyên liệu mã: " + req.getMaNguyenLieu()));

            // Kiểm tra số lượng tồn kho
            if (nl.getSoLuong() < req.getSoLuongTru()) {
                throw new RuntimeException("Kho không đủ số lượng cho nguyên liệu: " + nl.getTenNguyenLieu() + 
                                           " (Tồn: " + nl.getSoLuong() + ", Cần trừ: " + req.getSoLuongTru() + ")");
            }

            // Trừ số lượng và lưu lại
            nl.setSoLuong(nl.getSoLuong() - req.getSoLuongTru());
            repository.save(nl);
        }
    }
}