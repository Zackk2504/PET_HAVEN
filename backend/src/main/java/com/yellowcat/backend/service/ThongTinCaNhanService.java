package com.yellowcat.backend.service;

import com.yellowcat.backend.model.Thongtincanhan;
import com.yellowcat.backend.repository.ThongtincanhanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ThongTinCaNhanService {
    @Autowired
    ThongtincanhanRepository thongtincanhanRepository;

    public void addOrUpdate(Thongtincanhan thongtincanhan) {
        thongtincanhanRepository.save(thongtincanhan);
    }

    public Optional<Thongtincanhan> getThongtincanhanByIdTaiKhoan(String id) {
        return thongtincanhanRepository.findByIdtaikhoan(id);
    }
}
