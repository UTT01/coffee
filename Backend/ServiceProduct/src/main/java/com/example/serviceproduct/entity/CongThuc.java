package com.example.serviceproduct.entity;

import com.example.serviceproduct.entity.keys.CongThucId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CongThuc")
@IdClass(CongThucId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CongThuc {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maSanPham") // Tên cột khóa ngoại trong DB
    @JsonIgnore // Cực kỳ quan trọng: Tránh lỗi vòng lặp vô tận (StackOverflow) khi trả về JSON
    private SanPham sanPham;

    @Id
    @Column(name = "maNguyenLieu")
    private String maNguyenLieu;

    @Column(name = "soLuong")
    private Double soLuong;
}