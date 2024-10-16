package com.yellowcat.backend.controller;

import com.yellowcat.backend.model.Lichhen;
import com.yellowcat.backend.service.LichHenService;
import org.hibernate.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lich-hen")
public class LichHenController {
    @Autowired
    public LichHenService lichHenService;

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/all")
    public Page<Lichhen> getAllLichHen(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10); // 10 items per page
        return lichHenService.getAllPageLichHen(pageable);
    }

    @PreAuthorize("hasRole('user')")
    @GetMapping("/findByIdUser")
    public Page<Lichhen> findByIdUser(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10); // 10 items per page

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String idUser = authentication.getName(); // Đây là idUser

        return lichHenService.findByIdUser(pageable,idUser);
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/findByUserEmail")
    public Page<Lichhen> findByUserEmail(@RequestParam(defaultValue = "0") int page,@RequestParam("email") String Email) {
        Pageable pageable = PageRequest.of(page, 10);

        return lichHenService.findByEmailNguoiDat(pageable, Email);
    }

    @PostMapping("/add")
    public Lichhen addOne(@RequestBody Lichhen lichhen){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();

        String idUser = authentication.getName(); // Đây là idUser
        String email = jwt.getClaimAsString("email");

        lichhen.setIdkhachhang(idUser);
        lichhen.setEmailNguoiDat(email);
        lichhen.setTrangthai(4);

         Lichhen createLich = lichHenService.addOrUpdate(lichhen);
        return new ResponseEntity<Lichhen>(createLich, HttpStatus.CREATED).getBody();
    }

    @PutMapping("/update/{id}")
    public Lichhen updateOne(@RequestBody Lichhen lichhen,@RequestParam int id){

        Lichhen datLaiLich = lichHenService.findById(id);

        datLaiLich.setTrangthai(3);
        datLaiLich.setDate(lichhen.getDate());

        Lichhen updateLich = lichHenService.addOrUpdate(datLaiLich);
        return new ResponseEntity<Lichhen>(updateLich, HttpStatus.CREATED).getBody();
    }

    @PutMapping("/updateTrangThai/{id}/{idTT}")
    public Lichhen updateMore(@RequestBody Lichhen lichhen,@RequestParam int id,@RequestParam int idTT){

        Lichhen datLaiLich = lichHenService.findById(id);

        datLaiLich.setTrangthai(idTT);

        Lichhen updateLich = lichHenService.addOrUpdate(datLaiLich);
        return new ResponseEntity<Lichhen>(updateLich, HttpStatus.CREATED).getBody();
    }
}
