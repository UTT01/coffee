package com.example.serviceproduct.repository;

import com.example.serviceproduct.entity.CongThuc;
import com.example.serviceproduct.entity.keys.CongThucId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CongThucRepository extends JpaRepository<CongThuc, CongThucId> {
}