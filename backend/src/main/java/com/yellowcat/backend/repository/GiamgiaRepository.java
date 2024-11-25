package com.yellowcat.backend.repository;

import com.yellowcat.backend.model.Giamgia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface GiamgiaRepository extends JpaRepository<Giamgia, Integer> {
    List<Giamgia> findByNgaybatdauLessThanEqualAndNgayketthucGreaterThanEqualAndTrangthai(
            LocalDate ngaybatdau, LocalDate ngayketthuc, Boolean trangthai
    );
}