package com.example.serviceproduct.service;

import com.example.serviceproduct.dto.response.SanPhamResponse;
import com.example.serviceproduct.entity.CongThuc;
import com.example.serviceproduct.entity.LoaiSanPham;
import com.example.serviceproduct.entity.SanPham;
import com.example.serviceproduct.repository.CongThucRepository;
import com.example.serviceproduct.repository.LoaiSanPhamRepository;
import com.example.serviceproduct.repository.SanPhamRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SanPhamService {
    @Autowired
    private final SanPhamRepository sanPhamRepository;
    @Autowired
    private CongThucRepository congThucRepository;
    @Autowired
    private LoaiSanPhamRepository loaiSanPhamRepository;
    public List<SanPhamResponse> getAllSanPham() {
    List<SanPham> danhSach = sanPhamRepository.findAll();

    return danhSach.stream().map(sp -> {
        SanPhamResponse dto = new SanPhamResponse();
        dto.setMaSanPham(sp.getMaSanPham());
        dto.setTenSanPham(sp.getTenSanPham());
        dto.setDonGia(sp.getDonGia());
        dto.setDuongDanHinh(sp.getDuongDanHinh());
        dto.setTrangThai(sp.getTrangThai());
        if (sp.getLoaiSanPham() != null) {
            dto.setMaLoaiSanPham(sp.getLoaiSanPham().getMaLoaiSanPham());
            dto.setTenLoaiSanPham(sp.getLoaiSanPham().getTenLoaiSanPham());
        }
        // Thêm dòng này:
        dto.setDanhSachCongThuc(sp.getDanhSachCongThuc()); 
        return dto;
    }).collect(Collectors.toList());
}

    @Transactional
    public SanPham saveSanPham(SanPham sanPham) {

        if (sanPham.getLoaiSanPham() != null && sanPham.getLoaiSanPham().getMaLoaiSanPham() != null) {
            String maLoai = sanPham.getLoaiSanPham().getMaLoaiSanPham();
            // Tìm Loại SP thật trong DB
            LoaiSanPham loaiCoSan = loaiSanPhamRepository.findById(maLoai)
                    .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy Loại Sản Phẩm có mã " + maLoai));
            // Gắn cục Object thật vào
            sanPham.setLoaiSanPham(loaiCoSan);
        }

        // --- 2. XỬ LÝ CÔNG THỨC (Đoạn code siêu chuẩn của bạn) ---
        if (sanPham.getDanhSachCongThuc() != null) {
            for (CongThuc ct : sanPham.getDanhSachCongThuc()) {
                ct.setSanPham(sanPham);
            }
        }

        // --- 3. LƯU XUỐNG DATABASE ---
        return sanPhamRepository.save(sanPham);
    }

    @Transactional
    public void deleteSanPham(String maSanPham) {
        sanPhamRepository.deleteById(maSanPham);
    }
    public SanPhamResponse getSanPhamById(String maSanPham) {
        SanPham sp = sanPhamRepository.findById(maSanPham)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm có mã: " + maSanPham));

        SanPhamResponse dto = new SanPhamResponse();
        dto.setMaSanPham(sp.getMaSanPham());
        dto.setTenSanPham(sp.getTenSanPham());
        dto.setDonGia(sp.getDonGia());
        dto.setDuongDanHinh(sp.getDuongDanHinh());
        dto.setTrangThai(sp.getTrangThai());
        if (sp.getLoaiSanPham() != null) {
            dto.setMaLoaiSanPham(sp.getLoaiSanPham().getMaLoaiSanPham());
            dto.setTenLoaiSanPham(sp.getLoaiSanPham().getTenLoaiSanPham());
        }
        dto.setDanhSachCongThuc(sp.getDanhSachCongThuc()); 
        return dto;
    }
}
