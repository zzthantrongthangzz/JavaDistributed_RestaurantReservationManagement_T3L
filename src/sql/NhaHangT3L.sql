USE master;
GO

-- Tạo database mới
CREATE DATABASE NhaHangT3L;
GO

USE NhaHangT3L;
GO


-- 1. Bảng Chức Vụ
CREATE TABLE ChucVu
(
	maChucVu nvarchar(8) primary key not null, 
	tenChucVu nvarchar(40) not null
);
GO

-- 2. Bảng Nhân Viên
CREATE TABLE NhanVien
(
	maNhanVien nvarchar(8) primary key not null,
	hoTen nvarchar(40) not null,
	gioiTinh bit not null check (gioiTinh IN (0,1)),
	soDienThoai nvarchar(20) unique not null,
	email nvarchar(40) unique not null, 
	ngaySinh Date not null, 
	diaChi nvarchar(50) not null,
	maChucVu nvarchar(8) not null, 
	trangThai bit default 1

	CONSTRAINT FK_NhanVien_ChucVu FOREIGN KEY (maChucVu) REFERENCES ChucVu(maChucVu) 
);
GO

-- 3. Bảng Tài Khoản
CREATE TABLE TaiKhoan
(
	maNhanVien nvarchar(8) not null,
	taiKhoan nvarchar(20) primary key not null,
	matKhau nvarchar(100) not null, 
	ngayTaoTK Date not null,

	CONSTRAINT FK_TaiKhoan_NhanVien FOREIGN KEY (maNhanVien) REFERENCES NhanVien(maNhanVien)
);
GO

-- 4. Bảng Khách Hàng
CREATE TABLE KhachHang
(
    maKhachHang nvarchar(8) primary key,
    hoTen nvarchar(30) not null,
    soDienThoai nvarchar(15) unique not null,
    gioiTinh bit not null check (gioiTinh IN (0,1)),
    email nvarchar(50), 
    diaChi nvarchar(100),
    ngaySinh Date,
    tichDiem int not null default 0,
    trangThai bit default 1
);
GO

-- 5. Bảng Khuyến Mãi
CREATE TABLE KhuyenMai
(
	maKhuyenMai nvarchar(8) primary key,
	tenKhuyenMai nvarchar(30) not null, 
	loaiKhuyenMai nvarchar(20) not null, 
	ngayBatDau Datetime not null, 
	ngayKetThuc Datetime not null, 
	giaTriGiam Decimal (10,2) not null,
	hienThi bit default 1
);
GO

-- 6. Bảng Loại Món
CREATE TABLE LoaiMon
(
	maLoai nvarchar(8) primary key,
	tenLoai nvarchar(20) not null
);
GO

-- 7. Bảng Món (Đã thêm trường gia)
CREATE TABLE Mon
(
	maMon nvarchar(8) primary key,
	tenMon nvarchar(40) not null,
    gia decimal(18, 2) not null check (gia >= 0), -- Đã thêm trường giá
	duongDanAnh nvarchar(255) not null,
	tinhTrang nvarchar(30) not null CHECK (tinhTrang IN (N'Đang kinh doanh', N'Ngừng kinh doanh')),
	moTa nvarchar(255),
	donVi nvarchar(20) not null, 
	maLoai nvarchar(8) not null,

	CONSTRAINT FK_Mon_LoaiMon FOREIGN KEY (maLoai) REFERENCES LoaiMon(maLoai)
);
GO

-- 8. Bảng Lịch Sử Giá (Cập nhật logic tham chiếu, giữ nguyên cấu trúc để lưu log)
CREATE TABLE LichSuGia (
    maLog int IDENTITY(1,1) PRIMARY KEY,
    maMon nvarchar(8) NOT NULL,
    giaCu decimal(18, 2),             
    giaMoi decimal(18, 2) NOT NULL,      
    ngayThayDoi datetime DEFAULT GETDATE(),
    maNhanVien nvarchar(8),          
    
    CONSTRAINT FK_LichSuGia_Mon FOREIGN KEY (maMon) REFERENCES Mon(maMon),
    CONSTRAINT FK_LichSuGia_NhanVien FOREIGN KEY (maNhanVien) REFERENCES NhanVien(maNhanVien)
);
GO

-- 9. Bảng Tầng
CREATE TABLE Tang (
    maTang NVARCHAR(8) PRIMARY KEY NOT NULL,
    tenTang NVARCHAR(30) NOT NULL
);
GO

-- 10. Bảng Khu
CREATE TABLE Khu (
    maKhu NVARCHAR(8) PRIMARY KEY NOT NULL,
    tenKhu NVARCHAR(50) NOT NULL,
    maTang NVARCHAR(8) NOT NULL,
    
    CONSTRAINT FK_Khu_Tang FOREIGN KEY (maTang) REFERENCES Tang(maTang)
);
GO

-- 11. Bảng Bàn
CREATE TABLE Ban
(
	maBan nvarchar(8) primary key,
	tenBan nvarchar(30) not null,
	loaiBan nvarchar(20) not null,
	sucChua int not null,
	trangThai nvarchar(30) not null,
	maKhu nvarchar(8) NOT NULL
	
	CONSTRAINT FK_Ban_Khu FOREIGN KEY (maKhu) REFERENCES Khu(maKhu)
);
GO

-- 12. Bảng Phiếu Đặt Bàn 
CREATE TABLE PhieuDatBan
(
	maPhieuDatBan nvarchar(8) primary key,
	thoiGianDat Datetime not null,
	trangThai nvarchar(20) not null,
	maKhachHang nvarchar(8) not null,
	maNhanVien nvarchar(8) not null,
    tienDatCoc decimal(18, 2) default 0, 
    ghiChu nvarchar(40),

    CONSTRAINT FK_PhieuDatBan_KhachHang FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKhachHang),
    CONSTRAINT FK_PhieuDatBan_NhanVien FOREIGN KEY (maNhanVien) REFERENCES NhanVien(maNhanVien)
);
GO

-- 13. Bảng Phiếu Đặt Bàn - Bàn (Trung gian)
CREATE TABLE PhieuDatBan_Ban 
(
	maPhieuDatBan nvarchar(8) not null,
	maBan nvarchar(8) not null,
	
	CONSTRAINT PK_PhieuDatBan_Ban PRIMARY KEY (maPhieuDatBan, maBan),

    CONSTRAINT FK_PhieuDatBan_Ban_PhieuDatBan FOREIGN KEY (maPhieuDatBan) REFERENCES PhieuDatBan(maPhieuDatBan),
    CONSTRAINT FK_PhieuDatBan_Ban_Ban FOREIGN KEY (maBan) REFERENCES Ban(maBan)
);
GO

-- 14. Bảng Hóa Đơn
CREATE TABLE HoaDon
(
	maHoaDon nvarchar(8) primary key,
	trangThai nvarchar(20) not null,
	ngayLapHoaDon datetime not null,
	thue float not null ,
	maNhanVien nvarchar(8) not null,
	maPhieuDatBan nvarchar(8),
	maKhachHang nvarchar(8) not null,
	maKhuyenMai nvarchar(8),
	diaChi nvarchar(50),
	tienDatCoc decimal(18, 2) default 0 not null,
    soTienKhachTra decimal(18, 2) default 0 not null,
	soTienThoi decimal(18, 2) default 0 not null,

    CONSTRAINT FK_HoaDon_NhanVien FOREIGN KEY (maNhanVien) REFERENCES NhanVien(maNhanVien),
    CONSTRAINT FK_HoaDon_PhieuDatBan FOREIGN KEY (maPhieuDatBan) REFERENCES PhieuDatBan(maPhieuDatBan),
    CONSTRAINT FK_HoaDon_KhachHang FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKhachHang),
    CONSTRAINT FK_HoaDon_KhuyenMai FOREIGN KEY (maKhuyenMai) REFERENCES KhuyenMai(maKhuyenMai)
);
GO

-- 15. Bảng Hóa Đơn - Bàn (Trung gian)
CREATE TABLE HoaDon_Ban
(
	maHoaDon nvarchar(8) not null,
	maBan nvarchar(8) not null,
	
	CONSTRAINT PK_HoaDon_Ban PRIMARY KEY (maHoaDon, maBan),
    
    CONSTRAINT FK_HoaDon_Ban_HoaDon FOREIGN KEY (maHoaDon) REFERENCES HoaDon(maHoaDon),
    CONSTRAINT FK_HoaDon_Ban_Ban FOREIGN KEY (maBan) REFERENCES Ban(maBan)
);
GO

-- 16. Bảng Chi Tiết Hóa Đơn
CREATE TABLE ChiTietHoaDon
(
	maHoaDon nvarchar(8) not null,
	maMon nvarchar(8) not null,
	soLuong int not null CHECK (soLuong > 0),
	donGia decimal(18, 2) not null,

	CONSTRAINT PK_ChiTietHoaDon PRIMARY KEY (maHoaDon, maMon),

    CONSTRAINT FK_ChiTietHoaDon_HoaDon FOREIGN KEY (maHoaDon) REFERENCES HoaDon(maHoaDon),
    CONSTRAINT FK_ChiTietHoaDon_MonAn FOREIGN KEY (maMon) REFERENCES Mon(maMon)
);
GO

-- 17. Bảng Chi Tiết Phiếu Đặt Bàn
CREATE TABLE ChiTietPhieuDatBan
(
	maPhieuDatBan nvarchar(8) not null,
	maMon nvarchar(8) not null,
	soLuong int not null CHECK (soLuong > 0),
    donGia decimal(18, 2) not null,

	CONSTRAINT PK_ChiTietPhieuDatBan PRIMARY KEY (maPhieuDatBan, maMon),

    CONSTRAINT FK_ChiTietPhieuDatBan_PhieuDatBan FOREIGN KEY (maPhieuDatBan) REFERENCES PhieuDatBan(maPhieuDatBan),
    CONSTRAINT FK_ChiTietPhieuDatBan_Mon FOREIGN KEY (maMon) REFERENCES Mon(maMon)
);
GO

-- 18. Bảng lịch sử log huỷ bàn chờ
CREATE TABLE LichSuHuyDatBan (
    maLog int IDENTITY(1,1) PRIMARY KEY,
    maPhieuDatBan nvarchar(8) NOT NULL,   
    tenBan nvarchar(50),              
    tenKhachHang nvarchar(50),        
    sdtKhachHang nvarchar(15),        
    maNhanVien nvarchar(8) NOT NULL,  
    tenNhanVien nvarchar(50),           
    thoiGianHuy DATETIME DEFAULT GETDATE(),
    lyDoHuy nvarchar(255),
    
    CONSTRAINT FK_LichSuHuy_NhanVien FOREIGN KEY (maNhanVien) REFERENCES NhanVien(maNhanVien)

);
GO

--  INSERT DỮ LIỆU (INSERT DATA) 

INSERT INTO Tang (maTang, tenTang) VALUES
('T01', N'Tầng 1'),
('T02', N'Tầng 2');
GO

INSERT INTO Khu (maKhu, tenKhu, maTang) VALUES
('K01', N'Khu A', 'T01'),
('K02', N'Khu B', 'T01'),
('K03', N'Khu C', 'T02')
GO

INSERT INTO LoaiMon (maLoai, tenLoai) VALUES
('LM000001', N'Món chính'),
('LM000002', N'Món khai vị'),
('LM000003', N'Món tráng miệng'),
('LM000004', N'Đồ uống');
GO

-- Cập nhật INSERT INTO Mon (Thêm giá tiền)
INSERT INTO Mon (maMon, tenMon, gia, tinhTrang, duongDanAnh, moTa, donVi, maLoai) VALUES
('MM000001', N'Bánh cuốn tôm thịt', 99000.00, N'Đang kinh doanh', '/IMG/mon_banhcuontomthit.png', N'Bánh cuốn tôm thịt truyền thống mềm mịn, nhân tôm tươi và thịt heo xay, ăn kèm nước mắm chua ngọt đậm vị.', N'Phần', 'LM000001'),
('MM000002', N'Cơm chiên', 69000.00, N'Đang kinh doanh', '/IMG/mon_comchien.png', N'Cơm chiên hải sản thơm ngon với trứng, tôm, mực và rau củ, hạt cơm vàng giòn, hấp dẫn khó cưỡng.', N'Đĩa', 'LM000001'),
('MM000003', N'Bò bít tết', 299000.00, N'Đang kinh doanh', '/IMG/mon_bobittet.png', N'Thịt bò thăn ngoại áp chảo chín vừa, mềm mọng nước, phủ sốt tiêu đen đặc trưng, dùng kèm khoai tây và salad tươi.', N'Phần', 'LM000001'),
('MM000004', N'Mì xào', 85000.00, N'Đang kinh doanh', '/IMG/mon_mixao.png', N'Mì xào thập cẩm với tôm, thịt, trứng và rau củ, sợi mì vàng giòn hòa quyện hương vị đậm đà.', N'Đĩa', 'LM000001'),
('MM000005', N'Bò lúc lắc', 129000.00, N'Đang kinh doanh', '/IMG/mon_boluclac.png', N'Thịt bò cắt khối vuông xào lăn sốt tiêu đen, thơm mềm, dùng kèm khoai tây chiên và salad tươi mát.', N'Phần', 'LM000001'),
('MM000006', N'Gan ngỗng', 180000.00, N'Ngừng kinh doanh', '/IMG/mon_ganngong.png', N'Gan ngỗng áp chảo chuẩn vị Âu, béo ngậy, tan chảy trong miệng, kết hợp cùng nước sốt balsamic tinh tế.', N'Phần', 'LM000001'),
('MM000007', N'Cừu hầm rượu vang', 189000.00, N'Đang kinh doanh', '/IMG/mon_cuuhamruouvang.png', N'Thịt cừu hầm rượu vang đỏ, mềm tan, kết hợp rau củ và thảo mộc.', N'Phần', 'LM000001'),
('MM000008', N'Mì Ramen', 99000.00, N'Đang kinh doanh', '/IMG/mon_miramen.png', N'Mì Ramen kiểu Nhật với nước dùng hầm xương đậm đà và trứng lòng đào.', N'Phần', 'LM000001'),
('MM000009', N'Mì ống Penne', 109000.00, N'Đang kinh doanh', '/IMG/mon_miongpenne.png', N'Mì ống Penne Ý dai ngon, sốt kem tươi hoặc sốt cà chua thanh vị.', N'Phần', 'LM000001'),
('MM000010', N'Gà hầm sâm', 159000.00, N'Đang kinh doanh', '/IMG/mon_gahamsam.png', N'Gà ta hầm cùng nhân sâm, táo tàu và hạt sen, bổ dưỡng và thơm ngát.', N'Phần', 'LM000001'),
('MM000011', N'Cá hồi rau củ', 149000.00, N'Đang kinh doanh', '/IMG/mon_cahoiraucu.png', N'Cá hồi áp chảo cùng rau củ tươi, vị béo ngậy và bổ dưỡng.', N'Phần', 'LM000001'),
('MM000012', N'Nấm xào hương thảo', 89000.00, N'Đang kinh doanh', '/IMG/mon_namxaohuongthao.png', N'Nấm tươi xào cùng hương thảo, vị ngọt tự nhiên và thơm dịu.', N'Phần', 'LM000001'),
('MM000013', N'Gỏi bò bóp thấu', 109000.00, N'Đang kinh doanh', '/IMG/mon_goibobopthau.png', N'Thịt bò thái mỏng trộn chua cay cùng hành tây, rau thơm tươi mát.', N'Phần', 'LM000001'),
('MM000014', N'Lagu bò', 129000.00, N'Đang kinh doanh', '/IMG/mon_lagubo.png', N'Thịt bò hầm cùng khoai tây và cà rốt, nước sốt sánh đậm vị truyền thống.', N'Phần', 'LM000001'),
('MM000015', N'Bò nướng tảng', 179000.00, N'Đang kinh doanh', '/IMG/mon_bonuongtang.png', N'Thịt bò tảng nướng than hoa, mềm ngọt, thơm lừng và đậm vị.', N'Phần', 'LM000001'),
('MM000016', N'Mì Ý Spaghetty', 95000.00, N'Đang kinh doanh', '/IMG/mon_miyspageti.png', N'Mì Ý sốt bò bằm đặc trưng, sợi mì dai mềm hòa quyện cùng nước sốt cà chua và thịt bò đậm đà chuẩn vị châu Âu.', N'Đĩa', 'LM000001'),
('MM000017', N'Gà nướng mật ong', 220000.00, N'Đang kinh doanh', '/IMG/mon_ganuongmatong.png', N'Nửa con gà nướng mật ong vàng óng, da giòn thịt mềm, hương vị ngọt thanh và đậm đà khó quên.', N'Phần', 'LM000001'),
('MM000018', N'Chả ram', 70000.00, N'Đang kinh doanh', '/IMG/mon_charam.png', N'Chả ram giòn tan, nhân thịt và tôm thơm béo, đặc sản miền Trung được cuốn và chiên vàng hấp dẫn.', N'Phần', 'LM000002'),
('MM000019', N'Gỏi bò ngũ sắc', 85000.00, N'Đang kinh doanh', '/IMG/mon_goibongusac.png', N'Thịt bò mềm trộn rau củ đủ màu, đậm đà, đẹp mắt và ngon miệng.', N'Phần', 'LM000001'),
('MM000020', N'Gỏi ngó sen', 69000.00, N'Đang kinh doanh', '/IMG/mon_goingosen.png', N'Ngó sen giòn cùng tôm thịt và rau thơm, hòa quyện vị chua ngọt thanh nhẹ.', N'Phần', 'LM000002'),
('MM000021', N'Bò cuốn lá lốt', 79000.00, N'Đang kinh doanh', '/IMG/mon_bocuonlalot.png', N'Thịt bò xay ướp gia vị, cuốn lá lốt nướng thơm, dậy mùi đặc trưng.', N'Phần', 'LM000002'),
('MM000022', N'Tôm chiên xù', 79000.00, N'Đang kinh doanh', '/IMG/mon_tomchienxu.png', N'Tôm tươi được tẩm bột chiên vàng giòn rụm, giữ trọn vị ngọt tự nhiên.', N'Phần', 'LM000002'),
('MM000023', N'Súp cua tổ yến', 119000.00, N'Đang kinh doanh', '/IMG/mon_supcuatoyen.png', N'Súp cua bổ dưỡng với tổ yến, thịt cua tươi và nấm hương thơm dịu nhẹ.', N'Phần', 'LM000002'),
('MM000024', N'Chả mực Hạ Long', 89000.00, N'Đang kinh doanh', '/IMG/mon_chamuchalong.png', N'Mực Hạ Long tươi giã tay, chiên vàng thơm phức, vị dai giòn đặc trưng.', N'Phần', 'LM000002'),
('MM000025', N'Súp cua thịt bắp', 69000.00, N'Đang kinh doanh', '/IMG/mon_supcuathitbap.png', N'Súp cua nóng hổi, kết hợp thịt cua và hạt bắp ngọt tự nhiên.', N'Phần', 'LM000002'),
('MM000026', N'Bò cuốn rau củ', 85000.00, N'Đang kinh doanh', '/IMG/mon_bocuonraucu.png', N'Thịt bò mềm cuốn rau củ tươi, nướng vừa lửa, thơm ngon hấp dẫn.', N'Phần', 'LM000002'),
('MM000027', N'Chạo tôm', 79000.00, N'Đang kinh doanh', '/IMG/mon_chaotom.png', N'Tôm quết nhuyễn bọc mía, nướng thơm béo, mang hương vị truyền thống.', N'Phần', 'LM000002'),
('MM000028', N'Súp cua tuyết nhĩ', 79000.00, N'Đang kinh doanh', '/IMG/mon_supcuatuyetnhi.png', N'Súp cua nấu cùng tuyết nhĩ và trứng gà, vị ngọt thanh, tốt cho sức khỏe.', N'Phần', 'LM000002'),
('MM000029', N'Khoai lang chiên', 59000.00, N'Đang kinh doanh', '/IMG/mon_khoailangchien.png', N'Khoai lang tươi chiên vàng giòn, vị ngọt bùi tự nhiên, ăn kèm tương ớt.', N'Phần', 'LM000002'),
('MM000030', N'Kem Flan', 45000.00, N'Đang kinh doanh', '/IMG/mon_kemplan.png', N'Kem caramel mềm mịn, vị ngọt thanh béo nhẹ, tan ngay khi thưởng thức – món tráng miệng cổ điển được yêu thích.', N'Cái', 'LM000003'),
('MM000031', N'Panacota', 55000.00, N'Đang kinh doanh', '/IMG/mon_Panacota.png', N'Panna Cotta dâu tây kiểu Ý, lớp kem sữa mịn màng kết hợp nước sốt dâu tươi tạo cảm giác ngọt ngào khó quên.', N'Cái', 'LM000003'),
('MM000032', N'Bánh pancake', 59000.00, N'Đang kinh doanh', '/IMG/mon_banhpancake.png', N'Bánh pancake mềm mịn, ăn kèm mật ong hoặc siro trái cây thơm ngọt.', N'Phần', 'LM000003'),
('MM000033', N'Cupcake mâm xôi', 69000.00, N'Đang kinh doanh', '/IMG/mon_cupcakemamxoi.png', N'Cupcake mềm thơm với nhân mâm xôi chua ngọt hòa quyện tinh tế.', N'Phần', 'LM000003'),
('MM000034', N'Chè long nhãn', 49000.00, N'Đang kinh doanh', '/IMG/mon_chelongnhan.png', N'Chè long nhãn thanh mát, ngọt dịu, giải nhiệt tuyệt vời trong ngày hè.', N'Phần', 'LM000003'),
('MM000035', N'Kem trái cây', 59000.00, N'Đang kinh doanh', '/IMG/mon_kemtraicay.png', N'Kem mát lạnh kết hợp hương vị trái cây tươi ngọt ngào và hấp dẫn.', N'Phần', 'LM000003'),
('MM000036', N'Chè khúc bạch', 55000.00, N'Đang kinh doanh', '/IMG/mon_chekhucbach.png', N'Chè khúc bạch béo ngậy, thơm mùi hạnh nhân, ăn kèm trái cây tươi.', N'Phần', 'LM000003'),
('MM000037', N'Bánh Tiramisu', 79000.00, N'Đang kinh doanh', '/IMG/mon_banhtirramisu.png', N'Bánh Tiramisu Ý thơm cà phê và kem mascarpone béo ngậy.', N'Phần', 'LM000003'),
('MM000038', N'Bánh quy socola', 49000.00, N'Đang kinh doanh', '/IMG/mon_banhquysocola.png', N'Bánh quy giòn tan, thơm bơ và vị socola đậm đà khó cưỡng.', N'Phần', 'LM000003'),
('MM000039', N'Bánh mousse chanh dây', 69000.00, N'Đang kinh doanh', '/IMG/mon_banhmoussechanhday.png', N'Bánh mousse chanh dây chua nhẹ, mát lạnh, vị kem béo ngậy.', N'Phần', 'LM000003'),
('MM000040', N'Nước lọc Satori', 19000.00, N'Đang kinh doanh', '/IMG/nuoc_locsatori.png', N'Nước tinh khiết Satori đóng chai, thanh khiết và dễ uống.', N'Chai', 'LM000004'),
('MM000041', N'Nước lọc Aquafina', 19000.00, N'Đang kinh doanh', '/IMG/nuoc_locaquafina.png', N'Nước tinh khiết Aquafina, vị nhẹ, đảm bảo an toàn sức khỏe.', N'Chai', 'LM000004'),
('MM000042', N'Nước CocaCola', 25000.00, N'Đang kinh doanh', '/IMG/nuoc_coca.png', N'Nước ngọt có gas CocaCola sảng khoái, vị ngọt nhẹ và mát lạnh, giải khát tức thì.', N'Lon', 'LM000004'),
('MM000043', N'Nước Pepsi', 25000.00, N'Đang kinh doanh', '/IMG/nuoc_pepsi.png', N'Nước ngọt có gas Pepsi với vị đậm đà, mang lại cảm giác tươi mới và tỉnh táo.', N'Lon', 'LM000004'),
('MM000044', N'Nước Mirinda Cam', 25000.00, N'Đang kinh doanh', '/IMG/nuoc_mirindacam.png', N'Nước ngọt Mirinda hương cam thơm lừng, vị ngọt dịu nhẹ, mang lại cảm giác tươi mát.', N'Lon', 'LM000004'),
('MM000045', N'Nước 7Up', 25000.00, N'Đang kinh doanh', '/IMG/nuoc_7up.png', N'Nước ngọt 7Up vị chanh tự nhiên, không caffeine, mang lại cảm giác nhẹ nhàng và sảng khoái.', N'Lon', 'LM000004'),
('MM000046', N'Nước Sprite', 25000.00, N'Đang kinh doanh', '/IMG/nuoc_sprite.png', N'Nước ngọt có gas Sprite vị chanh tươi, hương vị the mát giúp giải khát tức thì.', N'Lon', 'LM000004'),
('MM000047', N'Nước Redbull', 35000.00, N'Đang kinh doanh', '/IMG/nuoc_redbull.png', N'Nước tăng lực Redbull giúp tỉnh táo, bổ sung năng lượng tức thì cho cơ thể.', N'Lon', 'LM000004'),
('MM000048', N'Nước Sting', 35000.00, N'Đang kinh doanh', '/IMG/nuoc_sting.png', N'Nước tăng lực Sting vị dâu ngọt dịu, giúp sảng khoái và tràn đầy năng lượng.', N'Lon', 'LM000004'),
('MM000049', N'Nước Warrior Dâu', 35000.00, N'Đang kinh doanh', '/IMG/nuoc_warriordau.png', N'Nước tăng lực Warrior hương dâu, bổ sung vitamin và năng lượng, vị ngọt nhẹ dễ uống.', N'Lon', 'LM000004'),
('MM000050', N'Nước Warrior Nho', 35000.00, N'Đang kinh doanh', '/IMG/nuoc_warriornho.png', N'Nước tăng lực Warrior vị nho thơm ngon, mang lại cảm giác tỉnh táo và sảng khoái.', N'Lon', 'LM000004'),
('MM000051', N'Bia tiger bạc', 39000.00, N'Đang kinh doanh', '/IMG/nuoc_biatigerbac.png', N'Bia Tiger bạc mát lạnh, vị đậm đà, phù hợp cho mọi bữa tiệc.', N'Lon', 'LM000004'),
('MM000052', N'Bia tiger nâu', 39000.00, N'Đang kinh doanh', '/IMG/nuoc_biatigernau.png', N'Bia Tiger nâu vị đậm hơn, hương thơm lúa mạch đặc trưng.', N'Lon', 'LM000004'),
('MM000053', N'Bia heneken', 42000.00, N'Đang kinh doanh', '/IMG/nuoc_biaheneken.png', N'Bia Heineken cao cấp với hương vị tươi mát và phong cách châu Âu.', N'Lon', 'LM000004');
GO

INSERT INTO ChucVu (maChucVu, tenChucVu) VALUES
('CV000001', N'Quản lý'),
('CV000002', N'Nhân viên');
GO

INSERT INTO NhanVien (maNhanVien, hoTen, gioiTinh, soDienThoai, email, ngaySinh, diaChi, maChucVu,trangThai) VALUES
('NV000001', N'Admin T3L Team', 1, '0938383838', 'admint3lteam@gmail.com', '2005-10-10', N'TP Hồ Chí Minh', 'CV000001',1),
('NV000002', N'Thân Trọng Thắng', 1, '0901010101', 'thantrongthang@gmail.com', '2005-01-01', N'TP Hồ Chí Minh', 'CV000001',1),
('NV000003', N'Hoàng Thành Long', 1, '0902020202', 'hoangthanhlong@gmail.com', '2005-02-02', N'TP Hồ Chí Minh', 'CV000001',1),
('NV000004', N'Lê Nhật Tân', 1, '0903030303', 'lenhattan@gmail.com', '2005-03-03', N'TP Hồ Chí Minh', 'CV000001',1),
('NV000005', N'Nguyễn Văn Tân', 1, '0904040404', 'nguyenvantan@gmail.com', '2005-04-04', N'TP Hồ Chí Minh', 'CV000001',1),
('NV000006', N'Trần Ngọc Anh', 0, '0901234567', 'ngocanh.t@gmail.com', '2006-03-15', N'Gò Vấp', 'CV000002',1),
('NV000007', N'Nguyễn Văn Bình', 1, '0912345678', 'vanbinh.n@gmail.com', '1999-07-22', N'Tân Bình', 'CV000002',1),
('NV000008', N'Hoàng Thị Lan', 0, '0923456789', 'thilan.h@gmail.com', '2000-11-05', N'Bình Thạnh', 'CV000002',1);
GO


INSERT INTO TaiKhoan (maNhanVien, taiKhoan, matKhau, ngayTaoTK) VALUES
('NV000001', 'admint3lteam', 'eQUBr5fpT1NTwXT6Jch/b3DP6broVlZhRTw7xDToqRauXhivTGClq90Vrvss3PoJ', GETDATE()),
('NV000002', 'tthang', '9ybx9bo74kBB1p/LxmKA7V13yFDC123qxdiOR23Nqkl6WeqTtbtUeCT4D94kMhcw', GETDATE()),
('NV000003', 'tlong', 'j3eSnPeXdMig5w4UmwAb8rByFA56foyDxgaM7gBAHD0HUGOk5CXSNO74uFWdWjPG', GETDATE()),
('NV000004', 'ntan', 'F8UIGGKvY0mKiSZUenfpMTM8SrVrST7SU7y9R1WrAZCezPadISucEXR1hwQ9fLhG', GETDATE()),
('NV000005', 'vtan', 'DNr10ZjjLvgc6rZoCAxEMWi4je905J1ePoHv33ladAPpnxoQJcJf8FSJYRX5pT0y', GETDATE()),
('NV000006', 'nanh', 'ULgWDX3gfdVOyT1j7GkpEpjk4gLyXbv5oQVyGpvmfdMr42dqQHBOvH2XnXjnLY/s', GETDATE()),
('NV000007', 'vbinh', 'vgtlLBfJ+BCiB8fB7QB4ZqBdptY7XwPwMjEJxaVT59ohlbIT3RPWhvO7E6BtjzVj', GETDATE()),
('NV000008', 'tlan', 'yz4Oa/tg0bGbVIyZ9LIdTLtMsqi8wGjmcDHkP0RIBNbg/Jp2z5kf43jK2xj3VdFK', GETDATE());
GO

INSERT INTO KhuyenMai(maKhuyenMai, tenKhuyenMai, loaiKhuyenMai, ngayBatDau, ngayKetThuc, giaTriGiam, hienThi)
VALUES
('KM000001', N'Giảm giá khai trương', N'Giảm %', '2025-10-10', '2025-10-15', 10, 1),
('KM000002', N'Giảm sốc cuối tuần', N'Giảm tiền', '2025-10-18', '2025-10-20', 50000, 1),
('KM000003', N'Ưu đãi khách hàng mới', N'Giảm %', '2025-10-15', '2025-10-25', 15, 1),
('KM000004', N'Black Friday', N'Giảm %', '2025-11-25', '2025-11-30', 50, 1),
('KM000005', N'Mua nhiều giảm nhiều', N'Giảm tiền', '2025-10-20', '2025-10-31', 30000, 1),
('KM000006', N'Sinh nhật cửa hàng', N'Giảm %', '2025-12-01', '2025-12-07', 20, 1),
('KM000007', N'Tết Dương lịch', N'Giảm tiền', '2025-12-25', '2026-01-05', 70000, 1),
('KM000008', N'Flash Sale giữa tháng', N'Giảm %', '2025-10-14', '2025-10-16', 25, 1),
('KM000009', N'Combo ưu đãi', N'Giảm tiền', '2025-10-12', '2025-10-22', 40000, 1),
('KM000010', N'Giảm giá HSSV', N'Giảm %', '2025-09-01', '2025-12-31', 10, 1),
('KM000011', N'Ngày vàng giảm sốc', N'Giảm tiền', '2025-11-10', '2025-11-11', 111111, 1),
('KM000012', N'Cyber Monday', N'Giảm %', '2025-12-02', '2025-12-04', 30, 1),
('KM000013', N'Ưu đãi giờ vàng', N'Giảm tiền', '2025-10-19', '2025-10-19', 20000, 1),
('KM000014', N'Giảm cho đơn đầu tiên', N'Giảm %', '2025-10-01', '2025-12-31', 12, 1),
('KM000015', N'Trung thu vui vẻ', N'Giảm tiền', '2025-09-05', '2025-09-15', 35000, 0),
('KM000016', N'Khuyến mãi hàng tồn kho', N'Giảm %', '2025-10-05', '2025-10-20', 18, 1),
('KM000017', N'Giáng sinh an lành', N'Giảm %', '2025-12-20', '2025-12-26', 40, 1),
('KM000018', N'Năm mới tiết kiệm', N'Giảm tiền', '2025-12-30', '2026-01-10', 80000, 1),
('KM000019', N'Ưu đãi VIP Member', N'Giảm %', '2025-10-01', '2026-03-31', 25, 1),
('KM000020', N'Xả hàng cuối năm', N'Giảm tiền', '2025-12-28', '2026-01-15', 100000, 1);
GO

INSERT INTO KhachHang (maKhachHang, hoTen, soDienThoai, gioiTinh, email, diaChi, ngaySinh, tichDiem, trangThai) VALUES
('KH000001', N'Nguyễn Văn An', '0905123456', 1, 'nvan.an@gmail.com', N'Quận 1, TP.HCM', '1990-05-15', 120, 1),
('KH000002', N'Trần Thị Bình', '0912345678', 0, 'binh.tran@yahoo.com', N'Gò Vấp, TP.HCM', '1995-10-20', 80, 1),
('KH000003', N'Lê Quốc Cường', '0987654321', 1, NULL, N'Tân Bình, TP.HCM', '1988-12-12', 200, 1), -- Thiếu Email
('KH000004', N'Phạm Thị Dung', '0938123456', 0, 'dung.pham@outlook.com', NULL, '1992-08-08', 150, 1), -- Thiếu Địa chỉ
('KH000005', N'Hoàng Văn Đạt', '0977123456', 1, 'hoangdat99@gmail.com', N'Bình Thạnh, TP.HCM', NULL, 300, 1), -- Thiếu Ngày sinh
('KH000006', N'Võ Thị Hồng', '0968123456', 0, 'hong.vo@gmail.com', N'Quận 12, TP.HCM', '1998-02-14', 50, 1),
('KH000007', N'Bùi Minh Tâm', '0908456123', 1, NULL, NULL, NULL, 0, 1), -- Thiếu hết (khách vãng lai)
('KH000008', N'Đỗ Thị Ngọc', '0945236789', 0, 'ngoc.do@company.com', N'Thủ Đức, TP.HCM', '1993-11-25', 90, 1),
('KH000009', N'Trương Văn Lâm', '0939123456', 1, 'lam.truong@gmail.com', N'Quận 7, TP.HCM', '1985-06-30', 45, 1),
('KH000010', N'Ngô Thị Hạnh', '0987123987', 0, NULL, N'Quận 3, TP.HCM', '1996-09-09', 180, 1),
('KH000011', N'Phan Văn Quý', '0977123987', 1, 'quy.phan@gmail.com', NULL, '2000-01-01', 75, 1),
('KH000012', N'Lưu Thị Mai', '0956123987', 0, 'mai.luu@yahoo.com', N'Quận 5, TP.HCM', '1991-03-08', 210, 1),
('KH000013', N'Tô Văn Tuấn', '0909321987', 1, 'tuan.to@gmail.com', N'Tân Phú, TP.HCM', NULL, 30, 1),
('KH000014', N'Cao Thị Lan', '0919321987', 0, NULL, NULL, '1997-07-27', 95, 1),
('KH000015', N'Nguyễn Văn Khôi', '0921321987', 1, 'khoi.nguyen@gmail.com', N'Bình Tân, TP.HCM', '1989-04-30', 260, 1),
('KH000016', N'Đinh Thị Hương', '0932321987', 0, 'huong.dinh@outlook.com', N'Quận 10, TP.HCM', '1994-12-24', 130, 1),
('KH000017', N'Lê Văn Trí', '0944321987', 1, 'tri.le@gmail.com', NULL, NULL, 10, 1),
('KH000018', N'Trần Thị Thu', '0955321987', 0, NULL, N'Phú Nhuận, TP.HCM', '1999-05-01', 170, 1),
('KH000019', N'Phùng Văn Nam', '0966321987', 1, 'nam.phung@gmail.com', N'Quận 4, TP.HCM', '1992-10-10', 220, 1),
('KH000020', N'Vũ Thị My', '0977321987', 0, 'my.vu@gmail.com', N'Quận 8, TP.HCM', '1998-11-20', 140, 1);
GO


-- SỬA LẠI: TẤT CẢ CÁC BÀN ĐỀU TRONG TRẠNG THÁI 'Bàn đang trống'
INSERT INTO Ban (maBan, tenBan, loaiBan, sucChua, trangThai, maKhu) VALUES
('MB000001', N'Phòng VIP 01', N'Phòng VIP', 30, N'Bàn đang trống', 'K01'),
('MB000002', N'Phòng VIP 02', N'Phòng VIP', 30, N'Bàn đang trống', 'K01'),
('MB000003', N'Phòng VIP 03', N'Phòng VIP', 30, N'Bàn đang trống', 'K02'),
('MB000004', N'Phòng VIP 04', N'Phòng VIP', 30, N'Bàn đang trống', 'K03'),
('MB000005', N'Bàn 005', N'Bàn nhỏ', 4, N'Bàn đang trống', 'K01'),
('MB000006', N'Bàn 006', N'Bàn nhỏ', 4, N'Bàn đang trống', 'K01'),
('MB000007', N'Bàn 007', N'Bàn nhỏ', 4, N'Bàn đang trống', 'K01'),
('MB000008', N'Bàn 008', N'Bàn nhỏ', 4, N'Bàn đang trống', 'K01'),
('MB000009', N'Bàn 009', N'Bàn nhỏ', 4, N'Bàn đang trống', 'K01'),
('MB000010', N'Bàn 010', N'Bàn nhỏ', 4, N'Bàn đang trống', 'K01'),
('MB000011', N'Bàn 011', N'Bàn nhỏ', 4, N'Bàn đang trống', 'K01'),
('MB000012', N'Bàn 012', N'Bàn nhỏ', 4, N'Bàn đang trống', 'K01'),
('MB000013', N'Bàn 013', N'Bàn vừa', 8, N'Bàn đang trống', 'K02'),
('MB000014', N'Bàn 014', N'Bàn vừa', 8, N'Bàn đang trống', 'K02'),
('MB000015', N'Bàn 015', N'Bàn vừa', 8, N'Bàn đang trống', 'K02'),
('MB000016', N'Bàn 016', N'Bàn vừa', 8, N'Bàn đang trống', 'K02'),
('MB000017', N'Bàn 017', N'Bàn vừa', 8, N'Bàn đang trống', 'K02'),
('MB000018', N'Bàn 018', N'Bàn vừa', 8, N'Bàn đang trống', 'K02'),
('MB000019', N'Bàn 019', N'Bàn vừa', 8, N'Bàn đang trống', 'K02'),
('MB000020', N'Bàn 020', N'Bàn vừa', 8, N'Bàn đang trống', 'K02'),
('MB000021', N'Bàn 021', N'Bàn vừa', 8, N'Bàn đang trống', 'K02'),
('MB000022', N'Bàn 022', N'Bàn vừa', 8, N'Bàn đang trống', 'K02'),
('MB000023', N'Bàn 023', N'Bàn lớn', 15, N'Bàn đang trống', 'K03'),
('MB000024', N'Bàn 024', N'Bàn lớn', 15, N'Bàn đang trống', 'K03'),
('MB000025', N'Bàn 025', N'Bàn lớn', 15, N'Bàn đang trống', 'K03'),
('MB000026', N'Bàn 026', N'Bàn lớn', 15, N'Bàn đang trống', 'K03'),
('MB000027', N'Bàn 027', N'Bàn lớn', 15, N'Bàn đang trống', 'K03'),
('MB000028', N'Bàn 028', N'Bàn lớn', 15, N'Bàn đang trống', 'K03'),
('MB000029', N'Bàn 029', N'Bàn lớn', 15, N'Bàn đang trống', 'K03'),
('MB000030', N'Bàn 030', N'Bàn lớn', 15, N'Bàn đang trống', 'K03');
GO


-- TẤT CẢ HÓA ĐƠN TRƯỚC ĐÓ 'Chưa thanh toán' -> 'Đã thanh toán'
-- Đã thêm số tiền khách trả (giả định > tổng tiền) để hợp logic.

INSERT INTO HoaDon (maHoaDon, trangThai, ngayLapHoaDon, thue, maNhanVien, maKhachHang, soTienKhachTra, soTienThoi)
VALUES ('HD000001', N'Đã thanh toán', DATEADD(hour, -2, GETDATE()), 0.0, 'NV000006', 'KH000001', 1000000, 250000);
INSERT INTO HoaDon_Ban (maHoaDon, maBan) VALUES ('HD000001', 'MB000002');
INSERT INTO ChiTietHoaDon (maHoaDon, maMon, soLuong, donGia)
VALUES
('HD000001', 'MM000003', 2, 299000.00),
('HD000001', 'MM000007', 1, 189000.00),
('HD000001', 'MM000053', 4, 42000.00);

INSERT INTO HoaDon (maHoaDon, trangThai, ngayLapHoaDon, thue, maNhanVien, maKhachHang, soTienKhachTra, soTienThoi)
VALUES ('HD000002', N'Đã thanh toán', DATEADD(minute, -45, GETDATE()), 0.0, 'NV000007', 'KH000003', 200000, 35000);
INSERT INTO HoaDon_Ban (maHoaDon, maBan) VALUES ('HD000002', 'MB000007');
INSERT INTO ChiTietHoaDon (maHoaDon, maMon, soLuong, donGia)
VALUES
('HD000002', 'MM000016', 1, 95000.00),
('HD000002', 'MM000018', 1, 70000.00);

INSERT INTO HoaDon (maHoaDon, trangThai, ngayLapHoaDon, thue, maNhanVien, maKhachHang, soTienKhachTra, soTienThoi)
VALUES ('HD000003', N'Đã thanh toán', DATEADD(minute, -90, GETDATE()), 0.0, 'NV000008', 'KH000005', 120000, 1000);
INSERT INTO HoaDon_Ban (maHoaDon, maBan) VALUES ('HD000003', 'MB000010');
INSERT INTO ChiTietHoaDon (maHoaDon, maMon, soLuong, donGia)
VALUES
('HD000003', 'MM000002', 1, 69000.00),
('HD000003', 'MM000043', 2, 25000.00);

INSERT INTO HoaDon (maHoaDon, trangThai, ngayLapHoaDon, thue, maNhanVien, maKhachHang, soTienKhachTra, soTienThoi)
VALUES ('HD000004', N'Đã thanh toán', DATEADD(hour, -1, GETDATE()), 0.0, 'NV000006', 'KH000007', 350000, 25000);
INSERT INTO HoaDon_Ban (maHoaDon, maBan) VALUES ('HD000004', 'MB000013');
INSERT INTO ChiTietHoaDon (maHoaDon, maMon, soLuong, donGia)
VALUES
('HD000004', 'MM000005', 1, 129000.00),
('HD000004', 'MM000021', 1, 79000.00),
('HD000004', 'MM000052', 3, 39000.00);

INSERT INTO HoaDon (maHoaDon, trangThai, ngayLapHoaDon, thue, maNhanVien, maKhachHang, soTienKhachTra, soTienThoi)
VALUES ('HD000005', N'Đã thanh toán', DATEADD(minute, -20, GETDATE()), 0.0, 'NV000007', 'KH000009', 350000, 40000);
INSERT INTO HoaDon_Ban (maHoaDon, maBan) VALUES ('HD000005', 'MB000018');
INSERT INTO ChiTietHoaDon (maHoaDon, maMon, soLuong, donGia)
VALUES
('HD000005', 'MM000017', 1, 220000.00),
('HD000005', 'MM000030', 2, 45000.00);

INSERT INTO HoaDon (maHoaDon, trangThai, ngayLapHoaDon, thue, maNhanVien, maKhachHang, soTienKhachTra, soTienThoi)
VALUES ('HD000006', N'Đã thanh toán', DATEADD(minute, -75, GETDATE()), 0.0, 'NV000008', 'KH000011', 200000, 33000);
INSERT INTO HoaDon_Ban (maHoaDon, maBan) VALUES ('HD000006', 'MB000019');
INSERT INTO ChiTietHoaDon (maHoaDon, maMon, soLuong, donGia)
VALUES
('HD000006', 'MM000014', 1, 129000.00),
('HD000006', 'MM000040', 2, 19000.00);

INSERT INTO HoaDon (maHoaDon, trangThai, ngayLapHoaDon, thue, maNhanVien, maKhachHang, soTienKhachTra, soTienThoi)
VALUES ('HD000007', N'Đã thanh toán', DATEADD(hour, -3, GETDATE()), 0.0, 'NV000006', 'KH000013', 700000, 47000);
INSERT INTO HoaDon_Ban (maHoaDon, maBan) VALUES ('HD000007', 'MB000024');
INSERT INTO ChiTietHoaDon (maHoaDon, maMon, soLuong, donGia)
VALUES
('HD000007', 'MM000015', 2, 179000.00),
('HD000007', 'MM000026', 2, 85000.00),
('HD000007', 'MM000045', 5, 25000.00);

INSERT INTO HoaDon (maHoaDon, trangThai, ngayLapHoaDon, thue, maNhanVien, maKhachHang, soTienKhachTra, soTienThoi)
VALUES ('HD000008', N'Đã thanh toán', DATEADD(minute, -50, GETDATE()), 0.0, 'NV000007', 'KH000015', 150000, 1000);
INSERT INTO HoaDon_Ban (maHoaDon, maBan) VALUES ('HD000008', 'MB000029');
INSERT INTO ChiTietHoaDon (maHoaDon, maMon, soLuong, donGia)
VALUES
('HD000008', 'MM000011', 1, 149000.00);

-- Dữ liệu Phiếu Đặt Bàn và Hóa Đơn liên quan 
-- Chuyển hết sang 'Đã thanh toán'

INSERT INTO PhieuDatBan (maPhieuDatBan, thoiGianDat, trangThai, maKhachHang, maNhanVien, ghiChu)
VALUES ('PDB00001', GETDATE(), N'Đang chờ', 'KH000002', 'NV000008', N'Khách VIP, chuẩn bị rượu');
INSERT INTO PhieuDatBan_Ban (maPhieuDatBan, maBan) VALUES ('PDB00001', 'MB000004');
INSERT INTO ChiTietPhieuDatBan (maPhieuDatBan, maMon, soLuong, donGia)
VALUES
('PDB00001', 'MM000007', 2, 189000.00);

INSERT INTO HoaDon (maHoaDon, trangThai, ngayLapHoaDon, thue, maNhanVien, maPhieuDatBan, maKhachHang, soTienKhachTra, soTienThoi)
VALUES ('HD000009', N'Đã thanh toán', GETDATE(), 0.0, 'NV000008', 'PDB00001', 'KH000002', 400000, 22000);
INSERT INTO HoaDon_Ban (maHoaDon, maBan) VALUES ('HD000009', 'MB000004');
INSERT INTO ChiTietHoaDon (maHoaDon, maMon, soLuong, donGia)
VALUES
('HD000009', 'MM000007', 2, 189000.00);


INSERT INTO PhieuDatBan (maPhieuDatBan, thoiGianDat, trangThai, maKhachHang, maNhanVien, ghiChu)
VALUES ('PDB00002', GETDATE(), N'Đang chờ', 'KH000004', 'NV000006', NULL);
INSERT INTO PhieuDatBan_Ban (maPhieuDatBan, maBan) VALUES ('PDB00002', 'MB000009');

INSERT INTO HoaDon (maHoaDon, trangThai, ngayLapHoaDon, thue, maNhanVien, maPhieuDatBan, maKhachHang, soTienKhachTra, soTienThoi)
VALUES ('HD000010', N'Đã thanh toán', GETDATE(), 0.0, 'NV000006', 'PDB00002', 'KH000004', 0, 0); -- Không có món nên coi như huỷ hoặc thanh toán 0đ
INSERT INTO HoaDon_Ban (maHoaDon, maBan) VALUES ('HD000010', 'MB000009');


INSERT INTO PhieuDatBan (maPhieuDatBan, thoiGianDat, trangThai, maKhachHang, maNhanVien, ghiChu)
VALUES ('PDB00003', GETDATE(), N'Đang chờ', 'KH000006', 'NV000007', NULL);
INSERT INTO PhieuDatBan_Ban (maPhieuDatBan, maBan) VALUES ('PDB00003', 'MB000015');
INSERT INTO ChiTietPhieuDatBan (maPhieuDatBan, maMon, soLuong, donGia)
VALUES
('PDB00003', 'MM000020', 2, 69000.00);

INSERT INTO HoaDon (maHoaDon, trangThai, ngayLapHoaDon, thue, maNhanVien, maPhieuDatBan, maKhachHang, soTienKhachTra, soTienThoi)
VALUES ('HD000011', N'Đã thanh toán', GETDATE(), 0.0, 'NV000007', 'PDB00003', 'KH000006', 150000, 12000);
INSERT INTO HoaDon_Ban (maHoaDon, maBan) VALUES ('HD000011', 'MB000015');
INSERT INTO ChiTietHoaDon (maHoaDon, maMon, soLuong, donGia)
VALUES
('HD000011', 'MM000020', 2, 69000.00);


INSERT INTO PhieuDatBan (maPhieuDatBan, thoiGianDat, trangThai, maKhachHang, maNhanVien, ghiChu)
VALUES ('PDB00004', GETDATE(), N'Đang chờ', 'KH000008', 'NV000008', NULL);
INSERT INTO PhieuDatBan_Ban (maPhieuDatBan, maBan) VALUES ('PDB00004', 'MB000022');

INSERT INTO HoaDon (maHoaDon, trangThai, ngayLapHoaDon, thue, maNhanVien, maPhieuDatBan, maKhachHang, soTienKhachTra, soTienThoi)
VALUES ('HD000012', N'Đã thanh toán', GETDATE(), 0.0, 'NV000008', 'PDB00004', 'KH000008', 0, 0);
INSERT INTO HoaDon_Ban (maHoaDon, maBan) VALUES ('HD000012', 'MB000022');


INSERT INTO PhieuDatBan (maPhieuDatBan, thoiGianDat, trangThai, maKhachHang, maNhanVien, ghiChu)
VALUES ('PDB00005', GETDATE(), N'Đang chờ', 'KH000010', 'NV000006', NULL);
INSERT INTO PhieuDatBan_Ban (maPhieuDatBan, maBan) VALUES ('PDB00005', 'MB000027');
INSERT INTO ChiTietPhieuDatBan (maPhieuDatBan, maMon, soLuong, donGia)
VALUES
('PDB00005', 'MM000025', 5, 69000.00),
('PDB00005', 'MM000042', 10, 25000.00);

INSERT INTO HoaDon (maHoaDon, trangThai, ngayLapHoaDon, thue, maNhanVien, maPhieuDatBan, maKhachHang, soTienKhachTra, soTienThoi)
VALUES ('HD000013', N'Đã thanh toán', GETDATE(), 0.0, 'NV000006', 'PDB00005', 'KH000010', 600000, 5000);
INSERT INTO HoaDon_Ban (maHoaDon, maBan) VALUES ('HD000013', 'MB000027');
INSERT INTO ChiTietHoaDon (maHoaDon, maMon, soLuong, donGia)
VALUES
('HD000013', 'MM000025', 5, 69000.00),
('HD000013', 'MM000042', 10, 25000.00);

GO

-- Dữ liệu hóa đơn cũ (vốn dĩ đã thanh toán)
DECLARE @DauThang DATETIME = DATEADD(day, 1, EOMONTH(GETDATE(), -1));

INSERT INTO HoaDon (maHoaDon, trangThai, ngayLapHoaDon, thue, maNhanVien, maPhieuDatBan, maKhachHang, maKhuyenMai, diaChi, soTienKhachTra, soTienThoi)
VALUES 
('HD000014', N'Đã thanh toán', DATEADD(day, -1, GETDATE()), 0.1, 'NV000006', NULL, 'KH000005', 'KM000002', NULL, 300000.00, 6300.00);
INSERT INTO HoaDon_Ban (maHoaDon, maBan) VALUES ('HD000014', 'MB000001');
INSERT INTO ChiTietHoaDon (maHoaDon, maMon, soLuong, donGia)
VALUES
('HD000014', 'MM000001', 2, 99000.00),
('HD000014', 'MM000020', 1, 69000.00),
('HD000014', 'MM000043', 2, 25000.00);

INSERT INTO HoaDon (maHoaDon, trangThai, ngayLapHoaDon, thue, maNhanVien, maPhieuDatBan, maKhachHang, maKhuyenMai, diaChi, soTienKhachTra, soTienThoi)
VALUES 
('HD000015', N'Đã thanh toán', DATEADD(day, -3, GETDATE()), 0.1, 'NV000007', NULL, 'KH000007', NULL, NULL, 350000.00, 21100.00);
INSERT INTO HoaDon_Ban (maHoaDon, maBan) VALUES ('HD000015', 'MB000003');
INSERT INTO ChiTietHoaDon (maHoaDon, maMon, soLuong, donGia)
VALUES
('HD000015', 'MM000003', 1, 299000.00);

INSERT INTO HoaDon (maHoaDon, trangThai, ngayLapHoaDon, thue, maNhanVien, maPhieuDatBan, maKhachHang, maKhuyenMai, diaChi, soTienKhachTra, soTienThoi)
VALUES 
('HD000016', N'Đã thanh toán', DATEADD(day, -5, GETDATE()), 0.1, 'NV000008', NULL, 'KH000010', 'KM000001', NULL, 450000.00, 18360.00);
INSERT INTO HoaDon_Ban (maHoaDon, maBan) VALUES ('HD000016', 'MB000005');
INSERT INTO ChiTietHoaDon (maHoaDon, maMon, soLuong, donGia)
VALUES
('HD000016', 'MM000016', 2, 95000.00),
('HD000016', 'MM000030', 2, 45000.00),
('HD000016', 'MM000051', 4, 39000.00);

INSERT INTO HoaDon (maHoaDon, trangThai, ngayLapHoaDon, thue, maNhanVien, maPhieuDatBan, maKhachHang, maKhuyenMai, diaChi, soTienKhachTra, soTienThoi)
VALUES 
('HD000017', N'Đã thanh toán', CASE WHEN DATEDIFF(day, @DauThang, GETDATE()) >= 10 THEN DATEADD(day, -10, GETDATE()) ELSE @DauThang END, 0.1, 'NV000006', NULL, 'KH000011', NULL, NULL, 300000.00, 6300.00);
INSERT INTO HoaDon_Ban (maHoaDon, maBan) VALUES ('HD000017', 'MB000006');
INSERT INTO ChiTietHoaDon (maHoaDon, maMon, soLuong, donGia)
VALUES
('HD000017', 'MM000005', 1, 129000.00),
('HD000017', 'MM000025', 2, 69000.00);

INSERT INTO HoaDon (maHoaDon, trangThai, ngayLapHoaDon, thue, maNhanVien, maPhieuDatBan, maKhachHang, maKhuyenMai, diaChi, soTienKhachTra, soTienThoi)
VALUES 
('HD000018', N'Đã thanh toán', @DauThang, 0.1, 'NV000007', NULL, 'KH000015', 'KM000003', NULL, 550000.00, 42295.00);
INSERT INTO HoaDon_Ban (maHoaDon, maBan) VALUES ('HD000018', 'MB000008');
INSERT INTO ChiTietHoaDon (maHoaDon, maMon, soLuong, donGia)
VALUES
('HD000018', 'MM000017', 1, 220000.00),
('HD000018', 'MM000022', 1, 79000.00),
('HD000018', 'MM000035', 2, 59000.00),
('HD000018', 'MM000053', 3, 42000.00);

INSERT INTO HoaDon (maHoaDon, trangThai, ngayLapHoaDon, thue, maNhanVien, maPhieuDatBan, maKhachHang, maKhuyenMai, diaChi, soTienKhachTra, soTienThoi)
VALUES 
('HD000019', N'Đã thanh toán', DATEADD(day, -2, GETDATE()), 0.1, 'NV000008', NULL, 'KH000002', NULL, NULL, 200000.00, 41600.00);
INSERT INTO HoaDon_Ban (maHoaDon, maBan) VALUES ('HD000019', 'MB000011');
INSERT INTO ChiTietHoaDon (maHoaDon, maMon, soLuong, donGia)
VALUES
('HD000019', 'MM000019', 1, 85000.00),
('HD000019', 'MM000032', 1, 59000.00);

INSERT INTO HoaDon (maHoaDon, trangThai, ngayLapHoaDon, thue, maNhanVien, maPhieuDatBan, maKhachHang, maKhuyenMai, diaChi, soTienKhachTra, soTienThoi)
VALUES 
('HD000020', N'Đã thanh toán', GETDATE(), 0.1, 'NV000007', NULL, 'KH000004', 'KM000005', NULL, 120000.00, 4500.00);
INSERT INTO HoaDon_Ban (maHoaDon, maBan) VALUES ('HD000020', 'MB000012');
INSERT INTO ChiTietHoaDon (maHoaDon, maMon, soLuong, donGia)
VALUES
('HD000020', 'MM000004', 1, 85000.00),
('HD000020', 'MM000044', 2, 25000.00);

INSERT INTO HoaDon (maHoaDon, trangThai, ngayLapHoaDon, thue, maNhanVien, maPhieuDatBan, maKhachHang, maKhuyenMai, diaChi, soTienKhachTra, soTienThoi)
VALUES 
('HD000021', N'Đã thanh toán', GETDATE(), 0.1, 'NV000008', NULL, 'KH000012', 'KM000010', NULL, 200000.00, 8930.00);
INSERT INTO HoaDon_Ban (maHoaDon, maBan) VALUES ('HD000021', 'MB000014');
INSERT INTO ChiTietHoaDon (maHoaDon, maMon, soLuong, donGia)
VALUES
('HD000021', 'MM000023', 1, 119000.00),
('HD000021', 'MM000031', 1, 55000.00),
('HD000021', 'MM000041', 1, 19000.00);

INSERT INTO HoaDon (maHoaDon, trangThai, ngayLapHoaDon, thue, maNhanVien, maPhieuDatBan, maKhachHang, maKhuyenMai, diaChi, soTienKhachTra, soTienThoi)
VALUES 
('HD000022', N'Đã thanh toán', DATEADD(minute, -30, GETDATE()), 0.1, 'NV000006', NULL, 'KH000014', 'KM000019', NULL, 150000.00, 10575.00);
INSERT INTO HoaDon_Ban (maHoaDon, maBan) VALUES ('HD000022', 'MB000016');
INSERT INTO ChiTietHoaDon (maHoaDon, maMon, soLuong, donGia)
VALUES
('HD000022', 'MM000008', 1, 99000.00),
('HD000022', 'MM000050', 2, 35000.00);
GO



-- --------------------------------------------------------------------------------------
-- A. TẠO BÀN ĐANG PHỤC VỤ (KHÁCH ĐANG ĂN)
-- Tiếp nối hóa đơn cũ (HD000022) -> Bắt đầu từ HD000023
-- --------------------------------------------------------------------------------------

-- 1. Bàn MB000002 (VIP 02) - Đang ăn, đã gọi món cách đây 1 tiếng
INSERT INTO HoaDon (maHoaDon, trangThai, ngayLapHoaDon, thue, maNhanVien, maKhachHang, soTienKhachTra, soTienThoi)
VALUES ('HD000023', N'Chưa thanh toán', DATEADD(hour, -1, GETDATE()), 0.0, 'NV000006', 'KH000001', 0, 0);

INSERT INTO HoaDon_Ban (maHoaDon, maBan) VALUES ('HD000023', 'MB000002');

INSERT INTO ChiTietHoaDon (maHoaDon, maMon, soLuong, donGia) VALUES
('HD000023', 'MM000003', 2, 299000.00), -- Bò bít tết
('HD000023', 'MM000007', 1, 189000.00), -- Cừu hầm
('HD000023', 'MM000053', 4, 42000.00);   -- Bia Heineken

UPDATE Ban SET trangThai = N'Bàn đang phục vụ' WHERE maBan = 'MB000002';


-- 2. Bàn MB000018 (Bàn vừa) - Đang ăn, mới vào 20 phút
INSERT INTO HoaDon (maHoaDon, trangThai, ngayLapHoaDon, thue, maNhanVien, maKhachHang, soTienKhachTra, soTienThoi)
VALUES ('HD000024', N'Chưa thanh toán', DATEADD(minute, -20, GETDATE()), 0.0, 'NV000007', 'KH000009', 0, 0);

INSERT INTO HoaDon_Ban (maHoaDon, maBan) VALUES ('HD000024', 'MB000018');

INSERT INTO ChiTietHoaDon (maHoaDon, maMon, soLuong, donGia) VALUES
('HD000024', 'MM000017', 1, 220000.00), -- Gà nướng mật ong
('HD000024', 'MM000030', 2, 45000.00);  -- Kem Flan

UPDATE Ban SET trangThai = N'Bàn đang phục vụ' WHERE maBan = 'MB000018';


-- 3. Bàn MB000007 (Bàn nhỏ) - Đang ăn
INSERT INTO HoaDon (maHoaDon, trangThai, ngayLapHoaDon, thue, maNhanVien, maKhachHang, soTienKhachTra, soTienThoi)
VALUES ('HD000025', N'Chưa thanh toán', DATEADD(minute, -45, GETDATE()), 0.0, 'NV000008', 'KH000003', 0, 0);

INSERT INTO HoaDon_Ban (maHoaDon, maBan) VALUES ('HD000025', 'MB000007');

INSERT INTO ChiTietHoaDon (maHoaDon, maMon, soLuong, donGia) VALUES
('HD000025', 'MM000016', 1, 95000.00), -- Mì Ý
('HD000025', 'MM000042', 2, 25000.00); -- Coca

UPDATE Ban SET trangThai = N'Bàn đang phục vụ' WHERE maBan = 'MB000007';


-- --------------------------------------------------------------------------------------
-- B. TẠO BÀN ĐANG CHỜ (KHÁCH ĐẶT TRƯỚC CHO TỐI NAY)
-- Tiếp nối phiếu đặt cũ (PDB00005) -> Bắt đầu từ PDB00006
-- --------------------------------------------------------------------------------------

-- Khai báo biến thời gian (Tự động tính toán theo giờ máy tính)
DECLARE @ToiNay22h DATETIME = DATEADD(hour, 22, CAST(CAST(GETDATE() AS DATE) AS DATETIME));
DECLARE @ToiNay23h DATETIME = DATEADD(hour, 23, CAST(CAST(GETDATE() AS DATE) AS DATETIME));

-- 1. Bàn MB000004 (Phòng VIP 04) - Hẹn 22:00 Tối nay
INSERT INTO PhieuDatBan (maPhieuDatBan, thoiGianDat, trangThai, maKhachHang, maNhanVien, tienDatCoc, ghiChu)
VALUES ('PDB00006', @ToiNay22h, N'Đang chờ', 'KH000002', 'NV000008', 500000, N'Khách VIP, chuẩn bị rượu');

INSERT INTO PhieuDatBan_Ban (maPhieuDatBan, maBan) VALUES ('PDB00006', 'MB000004');

INSERT INTO ChiTietPhieuDatBan (maPhieuDatBan, maMon, soLuong, donGia) VALUES
('PDB00006', 'MM000007', 2, 189000.00); -- Cừu hầm

UPDATE Ban SET trangThai = N'Bàn đang chờ' WHERE maBan = 'MB000004';


-- 2. Bàn MB000009 (Bàn nhỏ) - Hẹn 22:00 Tối nay
INSERT INTO PhieuDatBan (maPhieuDatBan, thoiGianDat, trangThai, maKhachHang, maNhanVien, tienDatCoc, ghiChu)
VALUES ('PDB00007', @ToiNay22h, N'Đang chờ', 'KH000004', 'NV000006', 0, NULL);

INSERT INTO PhieuDatBan_Ban (maPhieuDatBan, maBan) VALUES ('PDB00007', 'MB000009');

UPDATE Ban SET trangThai = N'Bàn đang chờ' WHERE maBan = 'MB000009';


-- 3. Bàn MB000015 (Bàn vừa) - Hẹn 23:00 Tối nay
INSERT INTO PhieuDatBan (maPhieuDatBan, thoiGianDat, trangThai, maKhachHang, maNhanVien, tienDatCoc, ghiChu)
VALUES ('PDB00008', @ToiNay23h, N'Đang chờ', 'KH000006', 'NV000007', 200000, NULL);

INSERT INTO PhieuDatBan_Ban (maPhieuDatBan, maBan) VALUES ('PDB00008', 'MB000015');

INSERT INTO ChiTietPhieuDatBan (maPhieuDatBan, maMon, soLuong, donGia) VALUES 
('PDB00008', 'MM000020', 2, 69000.00); -- Gỏi ngó sen

UPDATE Ban SET trangThai = N'Bàn đang chờ' WHERE maBan = 'MB000015';


-- 4. Bàn MB000027 (Bàn lớn) - Hẹn 22:00 Tối nay
INSERT INTO PhieuDatBan (maPhieuDatBan, thoiGianDat, trangThai, maKhachHang, maNhanVien, tienDatCoc, ghiChu)
VALUES ('PDB00009', @ToiNay22h, N'Đang chờ', 'KH000010', 'NV000006', 1000000, NULL);

INSERT INTO PhieuDatBan_Ban (maPhieuDatBan, maBan) VALUES ('PDB00009', 'MB000027');

INSERT INTO ChiTietPhieuDatBan (maPhieuDatBan, maMon, soLuong, donGia) VALUES
('PDB00009', 'MM000025', 5, 69000.00), -- Súp cua
('PDB00009', 'MM000042', 10, 25000.00); -- Coca

UPDATE Ban SET trangThai = N'Bàn đang chờ' WHERE maBan = 'MB000027';


-- --------------------------------------------------------------------------------------
-- C. TẠO DỮ LIỆU ĐẶT BÀN CHO CÁC NGÀY TIẾP THEO (TƯƠNG LAI)
-- Tiếp tục mã PDB00010, PDB00011
-- --------------------------------------------------------------------------------------

DECLARE @NgayMai DATE = DATEADD(day, 1, GETDATE());
DECLARE @NgayKia DATE = DATEADD(day, 2, GETDATE());

-- NGÀY MAI: Bàn VIP 01
INSERT INTO PhieuDatBan (maPhieuDatBan, thoiGianDat, trangThai, maKhachHang, maNhanVien, tienDatCoc, ghiChu)
VALUES ('PDB00010', DATEADD(hour, 18, CAST(@NgayMai AS DATETIME)), N'Đang chờ', 'KH000001', 'NV000002', 500000, N'Tiệc sinh nhật sếp');
INSERT INTO PhieuDatBan_Ban (maPhieuDatBan, maBan) VALUES ('PDB00010', 'MB000001');
INSERT INTO ChiTietPhieuDatBan (maPhieuDatBan, maMon, soLuong, donGia) VALUES 
('PDB00010', 'MM000003', 5, 299000);

-- NGÀY KIA: Bàn 013
INSERT INTO PhieuDatBan (maPhieuDatBan, thoiGianDat, trangThai, maKhachHang, maNhanVien, tienDatCoc, ghiChu)
VALUES ('PDB00011', DATEADD(hour, 11, CAST(@NgayKia AS DATETIME)), N'Đang chờ', 'KH000003', 'NV000003', 0, N'Ăn trưa');
INSERT INTO PhieuDatBan_Ban (maPhieuDatBan, maBan) VALUES ('PDB00011', 'MB000013');


-- --------------------------------------------------------------------------------------
-- D. TẠO DỮ LIỆU LỊCH SỬ HỦY BÀN
-- Giả lập các phiếu PDB00012 -> PDB00017 đã bị hủy và lưu vào log
-- --------------------------------------------------------------------------------------

INSERT INTO LichSuHuyDatBan (maPhieuDatBan, tenBan, tenKhachHang, sdtKhachHang, maNhanVien, tenNhanVien, thoiGianHuy, lyDoHuy)
VALUES 
('PDB00012', N'Bàn 005', N'Nguyễn Văn An', '0905123456', 'NV000006', N'Trần Ngọc Anh', DATEADD(hour, -2, GETDATE()), N'Khách bận đột xuất không đến được'),
('PDB00013', N'Bàn 010', N'Trần Thị Bình', '0912345678', 'NV000002', N'Thân Trọng Thắng', DATEADD(day, -1, GETDATE()), N'Trời mưa to, khách hủy lịch'),
('PDB00014', N'Bàn 013, Bàn 014', N'Lê Quốc Cường', '0987654321', 'NV000007', N'Nguyễn Văn Bình', DATEADD(day, -2, GETDATE()), N'Nhóm bạn dời lịch sang tuần sau'),
('PDB00015', N'Phòng VIP 01', N'Phạm Thị Dung', '0938123456', 'NV000006', N'Trần Ngọc Anh', DATEADD(day, -3, GETDATE()), N'Khách đổi ý'),
('PDB00016', N'Bàn 020 (Rút bớt)', N'Hoàng Văn Đạt', '0977123456', 'NV000002', N'Thân Trọng Thắng', DATEADD(hour, -5, GETDATE()), N'Số lượng người giảm, dư 1 bàn nên hủy bớt'),
('PDB00017', N'Bàn 025', N'Đỗ Thị Ngọc', '0945236789', 'NV000001', N'Admin T3L Team', DATEADD(day, -10, GETDATE()), N'Bàn bị hỏng chân, cần bảo trì gấp');
GO