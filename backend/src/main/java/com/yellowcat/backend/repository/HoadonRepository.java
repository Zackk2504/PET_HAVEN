package com.yellowcat.backend.repository;

import com.yellowcat.backend.model.Hoadon;
import com.yellowcat.backend.model.Lichhen;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HoadonRepository extends JpaRepository<Hoadon, Integer> {

    List<Hoadon> findByIdlichhen_Date(LocalDate date);

    @Query("SELECT h FROM Hoadon h WHERE h.nguoithanhtoan = :email " +
            "AND h.phuongthucthanhtoan = 'Offline' " +
            "AND h.date = :date ")
    List<Hoadon> findByNguoithanhtoanAndPhuongthucthanhtoan(@Param("email") String email
    , @Param("date") LocalDate date);
    Optional<Hoadon> findByIdlichhen_Id(Integer idlichhen);

    Optional<Hoadon> findByMagiaodich(String idPayMent);


//    ________________Thống kê______________________
// Thống kê theo ngày
@Query("SELECT FUNCTION('DATE', h.date) AS ngay, SUM(h.sotien) AS doanhthu " +
        "FROM Hoadon h " +
        "WHERE h.trangthai = 2 " +
        "GROUP BY FUNCTION('DATE', h.date) " +
        "ORDER BY FUNCTION('DATE', h.date)")
List<Object[]> thongKeTheoNgay();

    // Thống kê theo tháng
    @Query("SELECT FUNCTION('DATE_TRUNC', 'month', h.date) AS thang, SUM(h.sotien) AS doanhthu " +
            "FROM Hoadon h " +
            "WHERE h.trangthai = 2 " +
            "GROUP BY FUNCTION('DATE_TRUNC', 'month', h.date) " +
            "ORDER BY FUNCTION('DATE_TRUNC', 'month', h.date)")
    List<Object[]> thongKeTheoThang();

    // Thống kê theo năm
    @Query("SELECT FUNCTION('DATE_TRUNC', 'year', h.date) AS nam, SUM(h.sotien) AS doanhthu " +
            "FROM Hoadon h " +
            "WHERE h.trangthai = 2 " +
            "GROUP BY FUNCTION('DATE_TRUNC', 'year', h.date) " +
            "ORDER BY FUNCTION('DATE_TRUNC', 'year', h.date)")
    List<Object[]> thongKeTheoNam();
}