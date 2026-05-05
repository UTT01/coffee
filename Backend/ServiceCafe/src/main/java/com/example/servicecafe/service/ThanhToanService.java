package com.example.servicecafe.service;

import com.example.servicecafe.client.SanPhamClient;
import com.example.servicecafe.client.StoreClient;
import com.example.servicecafe.dto.PaymentDTO;
import com.example.servicecafe.dto.TruKhoRequest;
import com.example.servicecafe.dto.CongThucDTO;
import com.example.servicecafe.dto.SanPhamResponseDTO;
import com.example.servicecafe.entity.*;
import com.example.servicecafe.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ThanhToanService {

    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private ChiTietHDRepository chiTietRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Inject 2 Feign Client vào đây
    @Autowired
    private SanPhamClient sanPhamClient;

    @Autowired
    private StoreClient storeClient;

    @Modifying
    @Transactional
    public void xuLyThanhToan(PaymentDTO dto) {
        try {
            String inputMaHD = dto.getMaHoaDon();
            System.out.println(">>> [DEBUG] Bắt đầu xử lý HD: " + inputMaHD);

            HoaDon hd = null;

            if (inputMaHD != null && !inputMaHD.trim().isEmpty() && !"undefined".equals(inputMaHD)) {
                hd = hoaDonRepository.findById(inputMaHD).orElse(null);
            }

            if (hd == null) {
                System.out.println(">>> [INFO] Đang tạo hóa đơn mới hoàn toàn...");
                hd = new HoaDon();
                String finalMaHD = (inputMaHD != null && !inputMaHD.trim().isEmpty() && !"undefined".equals(inputMaHD))
                        ? inputMaHD
                        : "HD" + System.currentTimeMillis();
                hd.setMaHoaDon(finalMaHD);
                hd.setThoiGianVao(LocalDateTime.now().minusMinutes(30));
            }

            hd.setMaBan(dto.getMaBan());
            hd.setMaCa(dto.getMaCa());
            hd.setPhuongThucThanhToan(dto.getPhuongThucThanhToan());
            hd.setMaKhuyenMai(dto.getMaKhuyenMai());
            hd.setTongTien(dto.getTongTienSauKM());
            hd.setTrangThaiThanhToan("Paid");
            hd.setThoiGianRa(LocalDateTime.now());

            HoaDon savedHd = hoaDonRepository.save(hd);

            // BẢNG MAP ĐỂ CỘNG DỒN NGUYÊN LIỆU (Tránh gửi lặp nguyên liệu)
            Map<String, Double> mapNguyenLieuCanTru = new HashMap<>();

            if (dto.getItems() != null && !dto.getItems().isEmpty()) {
                chiTietRepository.deleteByMaHoaDon(savedHd);
                chiTietRepository.flush();

                int index = 1;
                List<ChiTietHD> chiTiets = new ArrayList<>();
                for (var itemDto : dto.getItems()) {
                    ChiTietHD ct = new ChiTietHD();
                    ct.setMaChiTietHD(savedHd.getMaHoaDon() + "CTHD" + (index++));
                    ct.setMaHoaDon(savedHd);
                    ct.setMaSanPham(itemDto.getMaSanPham());
                    ct.setSoLuong(itemDto.getSoLuong());
                    ct.setDonGia(itemDto.getGiaBan());
                    // ct.setGhiChu(itemDto.getGhiChu());
                    chiTiets.add(ct);

                    // ==========================================
                    // LOGIC GỌI SANG SERVICE-PRODUCT LẤY CÔNG THỨC
                    // ==========================================
                    try {
                        SanPhamResponseDTO sanPham = sanPhamClient.getSanPhamById(itemDto.getMaSanPham());
                        if (sanPham != null && sanPham.getDanhSachCongThuc() != null) {
                            for (CongThucDTO ctDTO : sanPham.getDanhSachCongThuc()) {
                                String maNL = ctDTO.getMaNguyenLieu();
                                // Số lượng cần trừ = Số lượng NL trong 1 công thức * Số lượng món khách gọi
                                double soLuongTru = ctDTO.getSoLuong() * itemDto.getSoLuong();
                                
                                // Cộng dồn vào Map
                                mapNguyenLieuCanTru.put(maNL, mapNguyenLieuCanTru.getOrDefault(maNL, 0.0) + soLuongTru);
                            }
                        }
                    } catch (Exception e) {
                        System.err.println(">>> [WARNING] Không lấy được công thức cho SP: " + itemDto.getMaSanPham() + ". Lỗi: " + e.getMessage());
                    }
                }
                chiTietRepository.saveAll(chiTiets);
            }

            // ==========================================
            // LOGIC GỌI SANG SERVICE-STORE ĐỂ TRỪ KHO
            // ==========================================
            if (!mapNguyenLieuCanTru.isEmpty()) {
                List<TruKhoRequest> dsTruKho = new ArrayList<>();
                for (Map.Entry<String, Double> entry : mapNguyenLieuCanTru.entrySet()) {
                    TruKhoRequest req = new TruKhoRequest();
                    req.setMaNguyenLieu(entry.getKey());
                    req.setSoLuongTru(entry.getValue());
                    dsTruKho.add(req);
                }

                try {
                    System.out.println(">>> [INFO] Đang gọi ServiceStore trừ kho...");
                    storeClient.truKhoNguyenLieu(dsTruKho);
                    System.out.println(">>> [SUCCESS] Đã trừ kho thành công!");
                } catch (Exception e) {
                    System.err.println(">>> [ERROR] Lỗi khi trừ kho: " + e.getMessage());
                    // Lưu ý: Nếu trừ kho lỗi, Transactional ở đây có thể rollback hóa đơn.
                    // Nếu không muốn hóa đơn bị hủy khi kho lỗi, hãy bọc kỹ try-catch này và không throw.
                    throw new RuntimeException("Thanh toán thất bại do lỗi trừ kho: " + e.getMessage());
                }
            }

            messagingTemplate.convertAndSend("/topic/tables", dto.getMaBan());
            System.out.println(">>> [SUCCESS] Đã thanh toán xong bàn: " + dto.getMaBan());

        } catch (Exception e) {
            System.err.println("❌ LỖI THANH TOÁN: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
