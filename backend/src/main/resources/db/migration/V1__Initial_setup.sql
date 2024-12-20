
CREATE TABLE GiamGia
(
    ID           SERIAL PRIMARY KEY,
    PhanTramGiam INT not null ,
    NgayBatDau   TIMESTAMP not null ,
    NgayKetThuc  TIMESTAMP not null ,
    MoTa         TEXT,
    TrangThai    BOOLEAN
);

CREATE TABLE ThongTinCaNhan
(
    ID         SERIAL PRIMARY KEY,
    HoTen      VARCHAR(100),
    Tuoi       INT,
    GioiTinh   VARCHAR(10),
    idTaiKhoan varchar(255) not null ,
    SDT        VARCHAR(20),
    Image      VARCHAR(100),
    Email      varchar(255),
    Role       varchar(100)
);

CREATE TABLE DichVu
(
    ID        SERIAL PRIMARY KEY,
    TenDichVu VARCHAR(100) NOT NULL,
    MoTa      TEXT         NOT NULL,
    Anh       TEXT,
    GiaTien   INT          NOT NULL,
    TrangThai   BOOLEAN NOT NULL,
    Hien        BOOLEAN NOT NULL
);

CREATE TABLE ThuCung (
                         ID             SERIAL PRIMARY KEY,
                         Ten            VARCHAR(100) NOT NULL,
                         CanNang        FLOAT CHECK (CanNang >= 0),
                         Tuoi           INT CHECK (Tuoi >= 0),
                         Giong          VARCHAR(100),
                         IDTaiKhoan VARCHAR(255) not null ,
                         Image          TEXT,
                         GioiTinh       BOOLEAN NOT NULL,
                         CoPhaiMeoKhong BOOLEAN NOT NULL,
                         TinhTrangSucKhoe TEXT,
                         MoTa TEXT
);


CREATE TABLE CaLichHen
(
    ID          SERIAL PRIMARY KEY,
    ThoiGianCa TIME NOT NULL,
    TrangThai   BOOLEAN
);

CREATE TABLE LichHen
(
    ID          SERIAL PRIMARY KEY,
    idKhachHang VARCHAR(255) NOT NULL ,
    idThuCung   INT REFERENCES ThuCung (ID),
    idDichVu    INT REFERENCES DichVu (ID),
    Date        DATE NOT NULL,
    TrangThai   INT   NOT NULL,
    EmailNguoiDat VARCHAR(255) NOT NULL default '123@gmail.com',
    idCaLichHen INT REFERENCES CaLichHen(ID),
    TrangThaiCa BOOLEAN,
    ThoiGianHuy TIMESTAMP,
    ThoiGianThayDoi TIMESTAMP,
    SoLanThayDoi INT not null default 0,
    SoLanNhacNho INT
);
-- quy ước trang thai:
-- 0 : thành công
-- 1 : đã đổi
-- 2 : đã hủy
-- 3 : chờ thanh toán
-- 4 : chờ xác nhận
-- 5 : Rỗng
-- 6 : Thanh toán thành công
-- 7 : Đã hoàn tiền
-- 8 : Chờ sử dụng

CREATE TABLE HoaDon
(
    ID                  SERIAL PRIMARY KEY,
    MaGiaoDich          VARCHAR(255) Not null UNIQUE ,
    idLichHen           INT REFERENCES LichHen (ID),
    Date                TIMESTAMP,
    SoTienBanDau    DECIMAL(10, 2) NOT NULL,
    SoTien              DECIMAL(10, 2) NOT NULL,
    NgayThanhToan       TIMESTAMP,
    PhuongThucThanhToan VARCHAR(50),
    TrangThai           INT not null,
    NguoiThanhToan      VARCHAR(255),
    idGiamGia   INT REFERENCES GiamGia (ID)
);
-- Quy ước:
-- 1: Chờ thanh toán
-- 2: Thành công
-- 3: Đã hoàn tiền


CREATE TABLE DanhGia
(
    ID         SERIAL PRIMARY KEY,
    idTaiKhoan VARCHAR(255) not null ,
    SoSao      VARCHAR(5) not null ,
    MoTa       TEXT,
    Date       TIMESTAMP,
    TrangThai  BOOLEAN,
    idHoaDon INT REFERENCES HoaDon(ID)
);

CREATE TABLE NgayNghi
(
    ID          SERIAL PRIMARY KEY,
    NgayNghi    TIMESTAMP not null ,
    TrangThai   BOOLEAN
);


-- Thêm dữ liệu vào bảng DichVu
INSERT INTO public.dichvu (id, tendichvu, mota, anh, giatien, trangthai, hien) VALUES (5, 'Spa cho mèo < 3kg (full)', 'Bao gồm cắt tỉa - tắm - massage', null, 18, true, true);
INSERT INTO public.dichvu (id, tendichvu, mota, anh, giatien, trangthai, hien) VALUES (6, 'Spa cho mèo >= 3kg (full)', 'Bao gồm cắt tỉa - tắm - massage', null, 23, true, true);
INSERT INTO public.dichvu (id, tendichvu, mota, anh, giatien, trangthai, hien) VALUES (7, 'Spa cho mèo >= 3kg (cắt tỉa)', 'Cắt tỉa bộ móng cho chú mèo của bạn', null, 10, true, true);


-- -- Thêm dữ liệu vào bảng GiamGia
-- INSERT INTO GiamGia (PhanTramGiam, NgayBatDau, NgayKetThuc, MoTa, TrangThai)
-- VALUES
--        (20, '2024-11-01 00:00:00', '2024-11-30 23:59:59', 'Giảm giá tháng 11', TRUE),
--        (15, '2024-12-01 00:00:00', '2024-12-31 23:59:59', 'Giảm giá tháng 12', TRUE);



-- -- Thêm dữ liệu vào bảng ThuCung
-- INSERT INTO ThuCung (Ten, CanNang, Tuoi, Giong, IDTaiKhoan, Image)
-- VALUES ('Dog 1', 10.5, 2, 'Golden', 1, NULL),
--        ('Dog 2', 8.3, 3, 'Bulldog', 2, NULL),
--        ('Dog 3', 12.7, 4, 'Labrador', 3, NULL);

INSERT INTO CaLichHen ( ThoiGianCa, TrangThai)
VALUES
    ( '07:00:00', TRUE),
    ( '08:00:00', TRUE),
    ( '09:00:00', TRUE),
    ( '10:00:00', TRUE),
    ('11:00:00', TRUE),
    ('12:00:00', TRUE),
    ('13:00:00', TRUE),
    ('14:00:00', TRUE);

-- -- Thêm dữ liệu vào bảng LichHen
-- INSERT INTO LichHen (idKhachHang, idThuCung, idDichVu, Date, TrangThai , idCaLichHen,TrangThaiCa)
-- VALUES (1, 1, 1, '2024-10-02 ', 1 , 1,TRUE),
--        (2, 2, 2, '2024-10-03 ', 2, 2,TRUE),
--        (3, 3, 3, '2024-10-04 ', 3 , 3,TRUE);

-- -- Thêm dữ liệu vào bảng HoaDon
-- INSERT INTO HoaDon (idLichHen, Date,SoTienBanDau, SoTien, NgayThanhToan, PhuongThucThanhToan, TrangThai,MaGiaoDich,idGiamGia)
-- VALUES (1, '2024-10-02 10:00:00',20, 20, '2024-10-02 11:00:00', 'Offline', 1,'1',2),
--        (2, '2024-10-03 14:00:00',20, 20, '2024-10-03 15:00:00', 'Online', 2,'2',2),
--        (3, '2024-10-04 09:00:00',20, 20, '2024-10-04 10:00:00', 'Online', 3,'3',2);

-- -- Thêm dữ liệu vào bảng DanhGia
-- INSERT INTO DanhGia (idTaiKhoan, SoSao, MoTa, Date, TrangThai,idHoaDon)
-- VALUES (1, 5, 'Rất tốt', '2024-10-01 12:00:00', TRUE,'1'),
--        (2, 4, 'Tốt', '2024-09-30 11:00:00', TRUE,'1'),
--        (3, 3, 'Bình thường', '2024-09-29 10:00:00', FALSE,'1');

