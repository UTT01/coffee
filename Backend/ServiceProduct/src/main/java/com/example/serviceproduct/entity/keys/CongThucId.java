package com.example.serviceproduct.entity.keys;

import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;

@Embeddable
@Data @NoArgsConstructor @AllArgsConstructor
public class CongThucId implements Serializable {
    private String sanPham;
    private String maNguyenLieu;
}