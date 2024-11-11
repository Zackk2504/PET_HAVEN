package com.yellowcat.backend.controller;

import com.yellowcat.backend.DTO.DatLichDTO;
import com.yellowcat.backend.DTO.DoiLichDTO;
import com.yellowcat.backend.model.Calichhen;
import com.yellowcat.backend.model.Dichvu;
import com.yellowcat.backend.model.Lichhen;
import com.yellowcat.backend.model.Thucung;
import com.yellowcat.backend.service.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequestMapping("/api/dat-lich")
public class DatLichController {
    @Autowired
    public LichHenService lichHenService;

    @Autowired
    private CaLichHenService caLichHenService;

    @Autowired
    private DichVuService dichVuService;

    @Autowired
    private ThuCungService thuCungService;

    @GetMapping("/dat-lich-info")
    public ResponseEntity<Map<String, Object>> getDatLichInfo(@RequestParam("ngay") LocalDate ngay) {
        Map<String, Object> response = new HashMap<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String idUser = auth.getName();

        // Lấy danh sách dịch vụ
        List<Dichvu> danhSachDichVu = dichVuService.getListTrangThaiTrue();
        response.put("dichVu", danhSachDichVu);

        // Lấy các ca có thể đặt
        List<Calichhen> CaLichHen = caLichHenService.getAllByDate(ngay);
        response.put("CaLichHen", CaLichHen);

        List<Thucung> listThuCung = thuCungService.findListThuCungByid(idUser);
        response.put("ListThuCung", listThuCung);

        return ResponseEntity.ok(response);
    }

    // API tạo lịch hẹn khi khách hàng ấn nút xác nhận
    @PutMapping("/xac-nhan-dat")
    public ResponseEntity<Lichhen> createLichhen(
           @Valid @RequestBody DatLichDTO datLichDTO ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();

        String idUser = authentication.getName(); // Đây là idUser
        String email = jwt.getClaimAsString("email");
        Optional<Lichhen> lichhenOptional = lichHenService.getLichHenByDateandCa(datLichDTO.getDate(),datLichDTO.getIdcalichhen());

        Thucung thucung = datLichDTO.getIdThuCung();
        thucung.setIdtaikhoan(idUser);
        thuCungService.saveOrUpdate(thucung);

        Optional<Dichvu> dichvuOptional = dichVuService.findById(datLichDTO.getIdDichVu());
        Dichvu dichvu = dichvuOptional.get();

        if (!lichhenOptional.isPresent()) {
            System.out.println("lịch không tồn tại");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
//        Check xem có lịch đã được đặt chưa
        if (lichhenOptional.get().getTrangthai()!=5){
            System.out.println("Lịch đã được đặt trước rồi , vui lòng chọn thời gian khác");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
//        Check không cho đặt ca trong quá khứ
        if (!caLichHenService.isCaAvailable(datLichDTO.getIdcalichhen(),datLichDTO.getDate())){
            System.out.println("Không được đặt ca trong quá khứ");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }


        Lichhen lichhen = lichhenOptional.get();
        System.out.println(lichhen);
        lichhen.setIdkhachhang(idUser);
        lichhen.setEmailNguoiDat(email);
        lichhen.setTrangthai(4);
        lichhen.setThucung(thucung);
        lichhen.setDate(datLichDTO.getDate());
        lichhen.setDichvu(dichvu);
        if(!lichhen.getTrangthaica()){
            lichhen.setTrangthaica(true);
        }else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Lichhen createLich = lichHenService.addOrUpdate(lichhen);

        lichHenService.sendEmailWithActions(createLich);

        scheduleTrangThaiChange(createLich.getId());

        return new ResponseEntity<>(createLich, HttpStatus.CREATED);
    }

    private AtomicBoolean isCancelled = new AtomicBoolean(false);  // Biến flag để kiểm tra

    public void cancelScheduleChange() {
        isCancelled.set(true);  // Đặt flag hủy thành true
    }

    // Sau 20p tự động đổi trạng thái thành chờ thanh toán
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void scheduleTrangThaiChange(Integer lichhenId) {
        try {
            if (isCancelled.get()) {
                System.out.println("Tiến trình bị hủy.");
                return;  // Nếu tiến trình bị hủy thì kết thúc
            }
            Thread.sleep( 60 * 1000); // Đợi
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return; // Ngừng xử lý nếu bị gián đoạn
        }

        // Xóa cache Hibernate để chắc chắn lấy dữ liệu từ DB
        entityManager.clear();

        // Kiểm tra và cập nhật trạng thái sau khi đợi 30 giây
        Lichhen lichhen = lichHenService.findById(lichhenId);
        if (lichhen == null) {
            System.out.println("Lịch hẹn không tồn tại hoặc đã bị xóa.");
            return;
        }

        if (lichhen.getTrangthai() == 4) {
            lichhen.setTrangthai(3); // Cập nhật sang trạng thái 3 (Chờ thanh toán)
            lichHenService.addOrUpdate(lichhen);
            System.out.println("Đã cập nhật trạng thái của lịch hẹn ID: " + lichhenId + " thành 3 (Chờ thanh toán)");
        } else {
            System.out.println("Lịch hẹn"+ lichhenId +  " đã bị hủy hoặc thay đổi trạng thái, không cập nhật nữa.");
        }
    }

    @Transactional
    @PutMapping("/huy-lich/{id}")
    public ResponseEntity<?> huyLichHen(@PathVariable Integer id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String idUser = authentication.getName();

        Lichhen lichhen = lichHenService.findById(id);
        if (lichhen == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lịch hẹn không tồn tại.");
        }

        // Check xem có phải chủ lịch không
        if (!lichhen.getIdkhachhang().equalsIgnoreCase(idUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không phải là chủ lịch hẹn này.");
        }

        if (lichhen.getTrangthai() == 4) {
            // Tạo đối tượng lịch hẹn mới với trạng thái hủy
            lichhen.setSolanthaydoi(lichhen.getSolanthaydoi()+1);

            Lichhen lichhenNew = new Lichhen();
            lichhenNew.setSolanthaydoi(lichhen.getSolanthaydoi());
            lichhenNew.setEmailNguoiDat(lichhen.getEmailNguoiDat());
            lichhenNew.setIdkhachhang(lichhen.getIdkhachhang());
            lichhenNew.setTrangthai(2); // Đặt trạng thái là "Đã hủy"
            lichhenNew.setIdcalichhen(lichhen.getIdcalichhen());
            lichhenNew.setThoigianhuy(LocalDateTime.now());
            lichhenNew.setThucung(lichhen.getThucung());
            lichhenNew.setDichvu(lichhen.getDichvu());
            lichhenNew.setDate(lichhen.getDate());
            lichhenNew.setTrangthaica(true);
            lichHenService.addOrUpdate(lichhenNew);

            // Cập nhật lịch gốc với trạng thái đã hủy
            lichhen.setTrangthai(5);
            lichhen.setEmailNguoiDat("default-email@example.com");
            if (lichhen.getTrangthaica()) {
                lichhen.setTrangthaica(false);
            } else {
                return ResponseEntity.ok("Lỗi ca");
            }
            lichHenService.addOrUpdate(lichhen);
            cancelScheduleChange();
            return ResponseEntity.ok("Lịch hẹn đã được hủy thành công.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lịch hẹn không thể hủy vì trạng thái không hợp lệ.");
    }

    @Transactional
    @PutMapping("/thay-doi-thoi-gian/{id}")
    public ResponseEntity<?> thayDoiThoiGian(@PathVariable Integer id,@Valid @RequestBody DoiLichDTO doiLichDTO) {
        Lichhen lichhen = lichHenService.findById(id);
        Lichhen lichhenNew = new Lichhen();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String idUser = authentication.getName();

        if (lichhen == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lịch hẹn không tồn tại.");
        }

        // Check xem có phải chủ lịch không
        if (!lichhen.getIdkhachhang().equalsIgnoreCase(idUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không phải là chủ lịch hẹn này.");
        }

        if (lichhen != null && lichhen.getTrangthai() == 4) {
            // Kiểm tra số lần thay đổi
            if (lichhen.getSolanthaydoi() >= 1) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lịch chỉ được phép thay đổi một lần.");
            }
//          Thay đổi thời gian và ca lịch
            Optional<Lichhen> lichhenDoiOptional = lichHenService.getLichHenByDateandCa(doiLichDTO.getDate(),doiLichDTO.getIdcalichhen());
            if (!lichhenDoiOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lịch lỗi.");
            }

            Lichhen lichDoi = lichhenDoiOptional.get();
            lichDoi.setEmailNguoiDat(lichhen.getEmailNguoiDat());
            lichDoi.setIdkhachhang(lichhen.getIdkhachhang());
            lichDoi.setTrangthai(3); // Đặt trạng thái là "Chờ thanh toán"
            lichDoi.setThoigianthaydoi(LocalDateTime.now());
            lichDoi.setThucung(lichhen.getThucung());
            lichDoi.setDichvu(lichhen.getDichvu());
            lichDoi.setTrangthaica(true);
            lichDoi.setSolanthaydoi(lichhen.getSolanthaydoi());
            lichHenService.addOrUpdate(lichDoi);

//            Cập nhập số lần thay đổi
            lichhen.setSolanthaydoi(lichhen.getSolanthaydoi()+1);

//            Tạo bản ghi lưu trừ lịch đổi
            lichhenNew.setEmailNguoiDat(lichhen.getEmailNguoiDat());
            lichhenNew.setIdkhachhang(lichhen.getIdkhachhang());
            lichhenNew.setTrangthai(1); // Đặt trạng thái là "Thất bại"
            lichhenNew.setIdcalichhen(lichhen.getIdcalichhen());
            lichhenNew.setThoigianthaydoi(LocalDateTime.now());
            lichhenNew.setThucung(lichhen.getThucung());
            lichhenNew.setDichvu(lichhen.getDichvu());
            lichhenNew.setDate(lichhen.getDate());
            lichhenNew.setTrangthaica(true);
            lichhenNew.setSolanthaydoi(lichhen.getSolanthaydoi());
            lichHenService.addOrUpdate(lichhenNew);

            lichhen.setTrangthai(5);
            lichhen.setEmailNguoiDat("default-email@example.com");
            if (lichhen.getTrangthaica()){
                lichhen.setTrangthaica(false);
            }else {
                return ResponseEntity.ok("Lỗi ca");
            }
            cancelScheduleChange();
            lichHenService.addOrUpdate(lichhen);
            return ResponseEntity.ok("Thời gian của lịch hẹn đã được cập nhật.");
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


}
