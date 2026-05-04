// ===========================================================================
// PHẦN 1: DỌN DẸP DB & TẠO CONSTRAINTS (KHÓA CHÍNH)
// ===========================================================================
MATCH (n) DETACH DELETE n;

CREATE CONSTRAINT IF NOT EXISTS FOR (c:ChucVu) REQUIRE c.maChucVu IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS FOR (n:NhanVien) REQUIRE n.maNhanVien IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS FOR (t:TaiKhoan) REQUIRE t.taiKhoan IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS FOR (k:KhachHang) REQUIRE k.maKhachHang IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS FOR (km:KhuyenMai) REQUIRE km.maKhuyenMai IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS FOR (l:LoaiMon) REQUIRE l.maLoai IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS FOR (m:Mon) REQUIRE m.maMon IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS FOR (t:Tang) REQUIRE t.maTang IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS FOR (k:Khu) REQUIRE k.maKhu IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS FOR (b:BanAn) REQUIRE b.maBan IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS FOR (p:PhieuDatBan) REQUIRE p.maPhieuDatBan IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS FOR (h:HoaDon) REQUIRE h.maHoaDon IS UNIQUE;


// ===========================================================================
// PHẦN 2: TẠO DỮ LIỆU GỐC (MASTER DATA)
// ===========================================================================

// 1. TẦNG & KHU
MERGE (t1:Tang {maTang: 'T01', tenTang: 'Tầng 1'})
MERGE (t2:Tang {maTang: 'T02', tenTang: 'Tầng 2'})

WITH t1, t2
MERGE (k1:Khu {maKhu: 'K01', tenKhu: 'Khu A'}) MERGE (k1)-[:THUOC_TANG]->(t1)
MERGE (k2:Khu {maKhu: 'K02', tenKhu: 'Khu B'}) MERGE (k2)-[:THUOC_TANG]->(t1)
MERGE (k3:Khu {maKhu: 'K03', tenKhu: 'Khu C'}) MERGE (k3)-[:THUOC_TANG]->(t2);

// 2. LOẠI MÓN
CREATE (:LoaiMon {maLoai: 'LM000001', tenLoai: 'Món chính'})
CREATE (:LoaiMon {maLoai: 'LM000002', tenLoai: 'Món khai vị'})
CREATE (:LoaiMon {maLoai: 'LM000003', tenLoai: 'Món tráng miệng'})
CREATE (:LoaiMon {maLoai: 'LM000004', tenLoai: 'Đồ uống'});

// 3. MÓN ĂN (Đã sửa đường dẫn thành /img/)
CREATE (:Mon {maMon: 'MM000001', tenMon: 'Bánh cuốn tôm thịt', gia: 99000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/mon_banhcuontomthit.png', moTa: 'Bánh cuốn tôm thịt truyền thống mềm mịn...', donVi: 'Phần', _maLoai: 'LM000001'})
CREATE (:Mon {maMon: 'MM000002', tenMon: 'Cơm chiên', gia: 69000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/mon_comchien.png', moTa: 'Cơm chiên hải sản thơm ngon...', donVi: 'Đĩa', _maLoai: 'LM000001'})
CREATE (:Mon {maMon: 'MM000003', tenMon: 'Bò bít tết', gia: 299000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/mon_bobittet.png', moTa: 'Thịt bò thăn ngoại áp chảo...', donVi: 'Phần', _maLoai: 'LM000001'})
CREATE (:Mon {maMon: 'MM000004', tenMon: 'Mì xào', gia: 85000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/mon_mixao.png', moTa: 'Mì xào thập cẩm...', donVi: 'Đĩa', _maLoai: 'LM000001'})
CREATE (:Mon {maMon: 'MM000005', tenMon: 'Bò lúc lắc', gia: 129000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/mon_boluclac.png', moTa: 'Thịt bò cắt khối vuông xào lăn...', donVi: 'Phần', _maLoai: 'LM000001'})
CREATE (:Mon {maMon: 'MM000006', tenMon: 'Gan ngỗng', gia: 180000.0, tinhTrang: 'Ngừng kinh doanh', duongDanAnh: '/img/mon_ganngong.png', moTa: 'Gan ngỗng áp chảo chuẩn vị Âu...', donVi: 'Phần', _maLoai: 'LM000001'})
CREATE (:Mon {maMon: 'MM000007', tenMon: 'Cừu hầm rượu vang', gia: 189000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/mon_cuuhamruouvang.png', moTa: 'Thịt cừu hầm rượu vang đỏ...', donVi: 'Phần', _maLoai: 'LM000001'})
CREATE (:Mon {maMon: 'MM000008', tenMon: 'Mì Ramen', gia: 99000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/mon_miramen.png', moTa: 'Mì Ramen kiểu Nhật...', donVi: 'Phần', _maLoai: 'LM000001'})
CREATE (:Mon {maMon: 'MM000009', tenMon: 'Mì ống Penne', gia: 109000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/mon_miongpenne.png', moTa: 'Mì ống Penne Ý dai ngon...', donVi: 'Phần', _maLoai: 'LM000001'})
CREATE (:Mon {maMon: 'MM000010', tenMon: 'Gà hầm sâm', gia: 159000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/mon_gahamsam.png', moTa: 'Gà ta hầm cùng nhân sâm...', donVi: 'Phần', _maLoai: 'LM000001'})
CREATE (:Mon {maMon: 'MM000011', tenMon: 'Cá hồi rau củ', gia: 149000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/mon_cahoiraucu.png', moTa: 'Cá hồi áp chảo cùng rau củ tươi...', donVi: 'Phần', _maLoai: 'LM000001'})
CREATE (:Mon {maMon: 'MM000012', tenMon: 'Nấm xào hương thảo', gia: 89000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/mon_namxaohuongthao.png', moTa: 'Nấm tươi xào cùng hương thảo...', donVi: 'Phần', _maLoai: 'LM000001'})
CREATE (:Mon {maMon: 'MM000013', tenMon: 'Gỏi bò bóp thấu', gia: 109000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/mon_goibobopthau.png', moTa: 'Thịt bò thái mỏng trộn chua cay...', donVi: 'Phần', _maLoai: 'LM000001'})
CREATE (:Mon {maMon: 'MM000014', tenMon: 'Lagu bò', gia: 129000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/mon_lagubo.png', moTa: 'Thịt bò hầm cùng khoai tây...', donVi: 'Phần', _maLoai: 'LM000001'})
CREATE (:Mon {maMon: 'MM000015', tenMon: 'Bò nướng tảng', gia: 179000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/mon_bonuongtang.png', moTa: 'Thịt bò tảng nướng than hoa...', donVi: 'Phần', _maLoai: 'LM000001'})
CREATE (:Mon {maMon: 'MM000016', tenMon: 'Mì Ý Spaghetty', gia: 95000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/mon_miyspageti.png', moTa: 'Mì Ý sốt bò bằm...', donVi: 'Đĩa', _maLoai: 'LM000001'})
CREATE (:Mon {maMon: 'MM000017', tenMon: 'Gà nướng mật ong', gia: 220000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/mon_ganuongmatong.png', moTa: 'Nửa con gà nướng mật ong...', donVi: 'Phần', _maLoai: 'LM000001'})
CREATE (:Mon {maMon: 'MM000018', tenMon: 'Chả ram', gia: 70000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/mon_charam.png', moTa: 'Chả ram giòn tan...', donVi: 'Phần', _maLoai: 'LM000002'})
CREATE (:Mon {maMon: 'MM000019', tenMon: 'Gỏi bò ngũ sắc', gia: 85000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/mon_goibongusac.png', moTa: 'Thịt bò mềm trộn rau củ...', donVi: 'Phần', _maLoai: 'LM000001'})
CREATE (:Mon {maMon: 'MM000020', tenMon: 'Gỏi ngó sen', gia: 69000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/mon_goingosen.png', moTa: 'Ngó sen giòn cùng tôm thịt...', donVi: 'Phần', _maLoai: 'LM000002'})
CREATE (:Mon {maMon: 'MM000021', tenMon: 'Bò cuốn lá lốt', gia: 79000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/mon_bocuonlalot.png', moTa: 'Thịt bò xay ướp gia vị...', donVi: 'Phần', _maLoai: 'LM000002'})
CREATE (:Mon {maMon: 'MM000022', tenMon: 'Tôm chiên xù', gia: 79000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/mon_tomchienxu.png', moTa: 'Tôm tươi được tẩm bột...', donVi: 'Phần', _maLoai: 'LM000002'})
CREATE (:Mon {maMon: 'MM000023', tenMon: 'Súp cua tổ yến', gia: 119000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/mon_supcuatoyen.png', moTa: 'Súp cua bổ dưỡng với tổ yến...', donVi: 'Phần', _maLoai: 'LM000002'})
CREATE (:Mon {maMon: 'MM000024', tenMon: 'Chả mực Hạ Long', gia: 89000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/mon_chamuchalong.png', moTa: 'Mực Hạ Long tươi giã tay...', donVi: 'Phần', _maLoai: 'LM000002'})
CREATE (:Mon {maMon: 'MM000025', tenMon: 'Súp cua thịt bắp', gia: 69000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/mon_supcuathitbap.png', moTa: 'Súp cua nóng hổi...', donVi: 'Phần', _maLoai: 'LM000002'})
CREATE (:Mon {maMon: 'MM000026', tenMon: 'Bò cuốn rau củ', gia: 85000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/mon_bocuonraucu.png', moTa: 'Thịt bò mềm cuốn rau củ...', donVi: 'Phần', _maLoai: 'LM000002'})
CREATE (:Mon {maMon: 'MM000027', tenMon: 'Chạo tôm', gia: 79000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/mon_chaotom.png', moTa: 'Tôm quết nhuyễn bọc mía...', donVi: 'Phần', _maLoai: 'LM000002'})
CREATE (:Mon {maMon: 'MM000028', tenMon: 'Súp cua tuyết nhĩ', gia: 79000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/mon_supcuatuyetnhi.png', moTa: 'Súp cua nấu cùng tuyết nhĩ...', donVi: 'Phần', _maLoai: 'LM000002'})
CREATE (:Mon {maMon: 'MM000029', tenMon: 'Khoai lang chiên', gia: 59000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/mon_khoailangchien.png', moTa: 'Khoai lang tươi chiên vàng...', donVi: 'Phần', _maLoai: 'LM000002'})
CREATE (:Mon {maMon: 'MM000030', tenMon: 'Kem Flan', gia: 45000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/mon_kemplan.png', moTa: 'Kem caramel mềm mịn...', donVi: 'Cái', _maLoai: 'LM000003'})
CREATE (:Mon {maMon: 'MM000031', tenMon: 'Panacota', gia: 55000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/mon_Panacota.png', moTa: 'Panna Cotta dâu tây kiểu Ý...', donVi: 'Cái', _maLoai: 'LM000003'})
CREATE (:Mon {maMon: 'MM000032', tenMon: 'Bánh pancake', gia: 59000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/mon_banhpancake.png', moTa: 'Bánh pancake mềm mịn...', donVi: 'Phần', _maLoai: 'LM000003'})
CREATE (:Mon {maMon: 'MM000033', tenMon: 'Cupcake mâm xôi', gia: 69000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/mon_cupcakemamxoi.png', moTa: 'Cupcake mềm thơm...', donVi: 'Phần', _maLoai: 'LM000003'})
CREATE (:Mon {maMon: 'MM000034', tenMon: 'Chè long nhãn', gia: 49000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/mon_chelongnhan.png', moTa: 'Chè long nhãn thanh mát...', donVi: 'Phần', _maLoai: 'LM000003'})
CREATE (:Mon {maMon: 'MM000035', tenMon: 'Kem trái cây', gia: 59000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/mon_kemtraicay.png', moTa: 'Kem mát lạnh kết hợp...', donVi: 'Phần', _maLoai: 'LM000003'})
CREATE (:Mon {maMon: 'MM000036', tenMon: 'Chè khúc bạch', gia: 55000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/mon_chekhucbach.png', moTa: 'Chè khúc bạch béo ngậy...', donVi: 'Phần', _maLoai: 'LM000003'})
CREATE (:Mon {maMon: 'MM000037', tenMon: 'Bánh Tiramisu', gia: 79000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/mon_banhtirramisu.png', moTa: 'Bánh Tiramisu Ý...', donVi: 'Phần', _maLoai: 'LM000003'})
CREATE (:Mon {maMon: 'MM000038', tenMon: 'Bánh quy socola', gia: 49000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/mon_banhquysocola.png', moTa: 'Bánh quy giòn tan...', donVi: 'Phần', _maLoai: 'LM000003'})
CREATE (:Mon {maMon: 'MM000039', tenMon: 'Bánh mousse chanh dây', gia: 69000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/mon_banhmoussechanhday.png', moTa: 'Bánh mousse chanh dây...', donVi: 'Phần', _maLoai: 'LM000003'})
CREATE (:Mon {maMon: 'MM000040', tenMon: 'Nước lọc Satori', gia: 19000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/nuoc_locsatori.png', moTa: 'Nước tinh khiết Satori...', donVi: 'Chai', _maLoai: 'LM000004'})
CREATE (:Mon {maMon: 'MM000041', tenMon: 'Nước lọc Aquafina', gia: 19000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/nuoc_locaquafina.png', moTa: 'Nước tinh khiết Aquafina...', donVi: 'Chai', _maLoai: 'LM000004'})
CREATE (:Mon {maMon: 'MM000042', tenMon: 'Nước CocaCola', gia: 25000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/nuoc_coca.png', moTa: 'Nước ngọt có gas CocaCola...', donVi: 'Lon', _maLoai: 'LM000004'})
CREATE (:Mon {maMon: 'MM000043', tenMon: 'Nước Pepsi', gia: 25000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/nuoc_pepsi.png', moTa: 'Nước ngọt có gas Pepsi...', donVi: 'Lon', _maLoai: 'LM000004'})
CREATE (:Mon {maMon: 'MM000044', tenMon: 'Nước Mirinda Cam', gia: 25000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/nuoc_mirindacam.png', moTa: 'Nước ngọt Mirinda hương cam...', donVi: 'Lon', _maLoai: 'LM000004'})
CREATE (:Mon {maMon: 'MM000045', tenMon: 'Nước 7Up', gia: 25000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/nuoc_7up.png', moTa: 'Nước ngọt 7Up...', donVi: 'Lon', _maLoai: 'LM000004'})
CREATE (:Mon {maMon: 'MM000046', tenMon: 'Nước Sprite', gia: 25000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/nuoc_sprite.png', moTa: 'Nước ngọt có gas Sprite...', donVi: 'Lon', _maLoai: 'LM000004'})
CREATE (:Mon {maMon: 'MM000047', tenMon: 'Nước Redbull', gia: 35000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/nuoc_redbull.png', moTa: 'Nước tăng lực Redbull...', donVi: 'Lon', _maLoai: 'LM000004'})
CREATE (:Mon {maMon: 'MM000048', tenMon: 'Nước Sting', gia: 35000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/nuoc_sting.png', moTa: 'Nước tăng lực Sting...', donVi: 'Lon', _maLoai: 'LM000004'})
CREATE (:Mon {maMon: 'MM000049', tenMon: 'Nước Warrior Dâu', gia: 35000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/nuoc_warriordau.png', moTa: 'Nước tăng lực Warrior dâu...', donVi: 'Lon', _maLoai: 'LM000004'})
CREATE (:Mon {maMon: 'MM000050', tenMon: 'Nước Warrior Nho', gia: 35000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/nuoc_warriornho.png', moTa: 'Nước tăng lực Warrior nho...', donVi: 'Lon', _maLoai: 'LM000004'})
CREATE (:Mon {maMon: 'MM000051', tenMon: 'Bia tiger bạc', gia: 39000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/nuoc_biatigerbac.png', moTa: 'Bia Tiger bạc...', donVi: 'Lon', _maLoai: 'LM000004'})
CREATE (:Mon {maMon: 'MM000052', tenMon: 'Bia tiger nâu', gia: 39000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/nuoc_biatigernau.png', moTa: 'Bia Tiger nâu...', donVi: 'Lon', _maLoai: 'LM000004'})
CREATE (:Mon {maMon: 'MM000053', tenMon: 'Bia heneken', gia: 42000.0, tinhTrang: 'Đang kinh doanh', duongDanAnh: '/img/nuoc_biaheneken.png', moTa: 'Bia Heineken...', donVi: 'Lon', _maLoai: 'LM000004'});

// Nối Món vào Loại Món
WITH 1 as dummy
MATCH (m:Mon), (l:LoaiMon) WHERE m._maLoai = l.maLoai MERGE (m)-[:THUOC_LOAI]->(l);

// 4. CHỨC VỤ & NHÂN VIÊN & TÀI KHOẢN
CREATE (:ChucVu {maChucVu: 'CV000001', tenChucVu: 'Quản lý'})
CREATE (:ChucVu {maChucVu: 'CV000002', tenChucVu: 'Nhân viên'});

CREATE (:NhanVien {maNhanVien: 'NV000001', hoTen: 'Admin T3L Team', gioiTinh: true, soDienThoai: '0938383838', email: 'admint3lteam@gmail.com', ngaySinh: date('2005-10-10'), diaChi: 'TP Hồ Chí Minh', trangThai: 1, _maChucVu: 'CV000001'})
CREATE (:NhanVien {maNhanVien: 'NV000002', hoTen: 'Thân Trọng Thắng', gioiTinh: true, soDienThoai: '0901010101', email: 'thantrongthang@gmail.com', ngaySinh: date('2005-01-01'), diaChi: 'TP Hồ Chí Minh', trangThai: 1, _maChucVu: 'CV000001'})
CREATE (:NhanVien {maNhanVien: 'NV000003', hoTen: 'Hoàng Thành Long', gioiTinh: true, soDienThoai: '0902020202', email: 'hoangthanhlong@gmail.com', ngaySinh: date('2005-02-02'), diaChi: 'TP Hồ Chí Minh', trangThai: 1, _maChucVu: 'CV000001'})
CREATE (:NhanVien {maNhanVien: 'NV000004', hoTen: 'Lê Nhật Tân', gioiTinh: true, soDienThoai: '0903030303', email: 'lenhattan@gmail.com', ngaySinh: date('2005-03-03'), diaChi: 'TP Hồ Chí Minh', trangThai: 1, _maChucVu: 'CV000001'})
CREATE (:NhanVien {maNhanVien: 'NV000005', hoTen: 'Nguyễn Văn Tân', gioiTinh: true, soDienThoai: '0904040404', email: 'nguyenvantan@gmail.com', ngaySinh: date('2005-04-04'), diaChi: 'TP Hồ Chí Minh', trangThai: 1, _maChucVu: 'CV000001'})
CREATE (:NhanVien {maNhanVien: 'NV000006', hoTen: 'Trần Ngọc Anh', gioiTinh: false, soDienThoai: '0901234567', email: 'ngocanh.t@gmail.com', ngaySinh: date('2006-03-15'), diaChi: 'Gò Vấp', trangThai: 1, _maChucVu: 'CV000002'})
CREATE (:NhanVien {maNhanVien: 'NV000007', hoTen: 'Nguyễn Văn Bình', gioiTinh: true, soDienThoai: '0912345678', email: 'vanbinh.n@gmail.com', ngaySinh: date('1999-07-22'), diaChi: 'Tân Bình', trangThai: 1, _maChucVu: 'CV000002'})
CREATE (:NhanVien {maNhanVien: 'NV000008', hoTen: 'Hoàng Thị Lan', gioiTinh: false, soDienThoai: '0923456789', email: 'thilan.h@gmail.com', ngaySinh: date('2000-11-05'), diaChi: 'Bình Thạnh', trangThai: 1, _maChucVu: 'CV000002'});

CREATE (:TaiKhoan {taiKhoan: 'admint3lteam', matKhau: 'eQUBr5fpT1NTwXT6Jch/b3DP6broVlZhRTw7xDToqRauXhivTGClq90Vrvss3PoJ', ngayTaoTK: date(), _maNhanVien: 'NV000001'})
CREATE (:TaiKhoan {taiKhoan: 'tthang', matKhau: '9ybx9bo74kBB1p/LxmKA7V13yFDC123qxdiOR23Nqkl6WeqTtbtUeCT4D94kMhcw', ngayTaoTK: date(), _maNhanVien: 'NV000002'})
CREATE (:TaiKhoan {taiKhoan: 'tlong', matKhau: 'j3eSnPeXdMig5w4UmwAb8rByFA56foyDxgaM7gBAHD0HUGOk5CXSNO74uFWdWjPG', ngayTaoTK: date(), _maNhanVien: 'NV000003'})
CREATE (:TaiKhoan {taiKhoan: 'ntan', matKhau: 'F8UIGGKvY0mKiSZUenfpMTM8SrVrST7SU7y9R1WrAZCezPadISucEXR1hwQ9fLhG', ngayTaoTK: date(), _maNhanVien: 'NV000004'})
CREATE (:TaiKhoan {taiKhoan: 'vtan', matKhau: 'DNr10ZjjLvgc6rZoCAxEMWi4je905J1ePoHv33ladAPpnxoQJcJf8FSJYRX5pT0y', ngayTaoTK: date(), _maNhanVien: 'NV000005'})
CREATE (:TaiKhoan {taiKhoan: 'nanh', matKhau: 'ULgWDX3gfdVOyT1j7GkpEpjk4gLyXbv5oQVyGpvmfdMr42dqQHBOvH2XnXjnLY/s', ngayTaoTK: date(), _maNhanVien: 'NV000006'})
CREATE (:TaiKhoan {taiKhoan: 'vbinh', matKhau: 'vgtlLBfJ+BCiB8fB7QB4ZqBdptY7XwPwMjEJxaVT59ohlbIT3RPWhvO7E6BtjzVj', ngayTaoTK: date(), _maNhanVien: 'NV000007'})
CREATE (:TaiKhoan {taiKhoan: 'tlan', matKhau: 'yz4Oa/tg0bGbVIyZ9LIdTLtMsqi8wGjmcDHkP0RIBNbg/Jp2z5kf43jK2xj3VdFK', ngayTaoTK: date(), _maNhanVien: 'NV000008'});

WITH 1 as dummy
MATCH (nv:NhanVien), (cv:ChucVu) WHERE nv._maChucVu = cv.maChucVu MERGE (nv)-[:GIU_CHUC_VU]->(cv);
WITH 1 as dummy
MATCH (tk:TaiKhoan), (nv:NhanVien) WHERE tk._maNhanVien = nv.maNhanVien MERGE (nv)-[:CO_TAI_KHOAN]->(tk);


// 5. KHUYẾN MÃI
CREATE (:KhuyenMai {maKhuyenMai: 'KM000001', tenKhuyenMai: 'Giảm giá khai trương', loaiKhuyenMai: 'Giảm %', ngayBatDau: date('2025-10-10'), ngayKetThuc: date('2025-10-15'), giaTriGiam: 10.0, hienThi: 1})
CREATE (:KhuyenMai {maKhuyenMai: 'KM000002', tenKhuyenMai: 'Giảm sốc cuối tuần', loaiKhuyenMai: 'Giảm tiền', ngayBatDau: date('2025-10-18'), ngayKetThuc: date('2025-10-20'), giaTriGiam: 50000.0, hienThi: 1})
CREATE (:KhuyenMai {maKhuyenMai: 'KM000003', tenKhuyenMai: 'Ưu đãi khách hàng mới', loaiKhuyenMai: 'Giảm %', ngayBatDau: date('2025-10-15'), ngayKetThuc: date('2025-10-25'), giaTriGiam: 15.0, hienThi: 1})
CREATE (:KhuyenMai {maKhuyenMai: 'KM000004', tenKhuyenMai: 'Black Friday', loaiKhuyenMai: 'Giảm %', ngayBatDau: date('2025-11-25'), ngayKetThuc: date('2025-11-30'), giaTriGiam: 50.0, hienThi: 1})
CREATE (:KhuyenMai {maKhuyenMai: 'KM000005', tenKhuyenMai: 'Mua nhiều giảm nhiều', loaiKhuyenMai: 'Giảm tiền', ngayBatDau: date('2025-10-20'), ngayKetThuc: date('2025-10-31'), giaTriGiam: 30000.0, hienThi: 1})
CREATE (:KhuyenMai {maKhuyenMai: 'KM000010', tenKhuyenMai: 'Giảm giá HSSV', loaiKhuyenMai: 'Giảm %', ngayBatDau: date('2025-09-01'), ngayKetThuc: date('2025-12-31'), giaTriGiam: 10.0, hienThi: 1})
CREATE (:KhuyenMai {maKhuyenMai: 'KM000019', tenKhuyenMai: 'Ưu đãi VIP Member', loaiKhuyenMai: 'Giảm %', ngayBatDau: date('2025-10-01'), ngayKetThuc: date('2026-03-31'), giaTriGiam: 25.0, hienThi: 1});


// 6. KHÁCH HÀNG (Bỏ qua các trường NULL)
CREATE (:KhachHang {maKhachHang: 'KH000001', hoTen: 'Nguyễn Văn An', soDienThoai: '0905123456', gioiTinh: true, email: 'nvan.an@gmail.com', diaChi: 'Quận 1, TP.HCM', ngaySinh: date('1990-05-15'), tichDiem: 120, trangThai: 1})
CREATE (:KhachHang {maKhachHang: 'KH000002', hoTen: 'Trần Thị Bình', soDienThoai: '0912345678', gioiTinh: false, email: 'binh.tran@yahoo.com', diaChi: 'Gò Vấp, TP.HCM', ngaySinh: date('1995-10-20'), tichDiem: 80, trangThai: 1})
CREATE (:KhachHang {maKhachHang: 'KH000003', hoTen: 'Lê Quốc Cường', soDienThoai: '0987654321', gioiTinh: true, diaChi: 'Tân Bình, TP.HCM', ngaySinh: date('1988-12-12'), tichDiem: 200, trangThai: 1})
CREATE (:KhachHang {maKhachHang: 'KH000004', hoTen: 'Phạm Thị Dung', soDienThoai: '0938123456', gioiTinh: false, email: 'dung.pham@outlook.com', ngaySinh: date('1992-08-08'), tichDiem: 150, trangThai: 1})
CREATE (:KhachHang {maKhachHang: 'KH000005', hoTen: 'Hoàng Văn Đạt', soDienThoai: '0977123456', gioiTinh: true, email: 'hoangdat99@gmail.com', diaChi: 'Bình Thạnh, TP.HCM', tichDiem: 300, trangThai: 1})
CREATE (:KhachHang {maKhachHang: 'KH000006', hoTen: 'Võ Thị Hồng', soDienThoai: '0968123456', gioiTinh: false, email: 'hong.vo@gmail.com', diaChi: 'Quận 12, TP.HCM', ngaySinh: date('1998-02-14'), tichDiem: 50, trangThai: 1})
CREATE (:KhachHang {maKhachHang: 'KH000007', hoTen: 'Bùi Minh Tâm', soDienThoai: '0908456123', gioiTinh: true, tichDiem: 0, trangThai: 1})
CREATE (:KhachHang {maKhachHang: 'KH000008', hoTen: 'Đỗ Thị Ngọc', soDienThoai: '0945236789', gioiTinh: false, email: 'ngoc.do@company.com', diaChi: 'Thủ Đức, TP.HCM', ngaySinh: date('1993-11-25'), tichDiem: 90, trangThai: 1})
CREATE (:KhachHang {maKhachHang: 'KH000009', hoTen: 'Trương Văn Lâm', soDienThoai: '0939123456', gioiTinh: true, email: 'lam.truong@gmail.com', diaChi: 'Quận 7, TP.HCM', ngaySinh: date('1985-06-30'), tichDiem: 45, trangThai: 1})
CREATE (:KhachHang {maKhachHang: 'KH000010', hoTen: 'Ngô Thị Hạnh', soDienThoai: '0987123987', gioiTinh: false, diaChi: 'Quận 3, TP.HCM', ngaySinh: date('1996-09-09'), tichDiem: 180, trangThai: 1})
CREATE (:KhachHang {maKhachHang: 'KH000011', hoTen: 'Phan Văn Quý', soDienThoai: '0977123987', gioiTinh: true, email: 'quy.phan@gmail.com', ngaySinh: date('2000-01-01'), tichDiem: 75, trangThai: 1})
CREATE (:KhachHang {maKhachHang: 'KH000012', hoTen: 'Lưu Thị Mai', soDienThoai: '0956123987', gioiTinh: false, email: 'mai.luu@yahoo.com', diaChi: 'Quận 5, TP.HCM', ngaySinh: date('1991-03-08'), tichDiem: 210, trangThai: 1})
CREATE (:KhachHang {maKhachHang: 'KH000013', hoTen: 'Tô Văn Tuấn', soDienThoai: '0909321987', gioiTinh: true, email: 'tuan.to@gmail.com', diaChi: 'Tân Phú, TP.HCM', tichDiem: 30, trangThai: 1})
CREATE (:KhachHang {maKhachHang: 'KH000014', hoTen: 'Cao Thị Lan', soDienThoai: '0919321987', gioiTinh: false, ngaySinh: date('1997-07-27'), tichDiem: 95, trangThai: 1})
CREATE (:KhachHang {maKhachHang: 'KH000015', hoTen: 'Nguyễn Văn Khôi', soDienThoai: '0921321987', gioiTinh: true, email: 'khoi.nguyen@gmail.com', diaChi: 'Bình Tân, TP.HCM', ngaySinh: date('1989-04-30'), tichDiem: 260, trangThai: 1});

// 7. BÀN
CREATE (:BanAn {maBan: 'MB000001', tenBan: 'Phòng VIP 01', loaiBan: 'Phòng VIP', sucChua: 30, trangThai: 'Bàn đang trống', _maKhu: 'K01'})
CREATE (:BanAn {maBan: 'MB000002', tenBan: 'Phòng VIP 02', loaiBan: 'Phòng VIP', sucChua: 30, trangThai: 'Bàn đang trống', _maKhu: 'K01'})
CREATE (:BanAn {maBan: 'MB000003', tenBan: 'Phòng VIP 03', loaiBan: 'Phòng VIP', sucChua: 30, trangThai: 'Bàn đang trống', _maKhu: 'K02'})
CREATE (:BanAn {maBan: 'MB000004', tenBan: 'Phòng VIP 04', loaiBan: 'Phòng VIP', sucChua: 30, trangThai: 'Bàn đang trống', _maKhu: 'K03'})
CREATE (:BanAn {maBan: 'MB000005', tenBan: 'Bàn 005', loaiBan: 'Bàn nhỏ', sucChua: 4, trangThai: 'Bàn đang trống', _maKhu: 'K01'})
CREATE (:BanAn {maBan: 'MB000006', tenBan: 'Bàn 006', loaiBan: 'Bàn nhỏ', sucChua: 4, trangThai: 'Bàn đang trống', _maKhu: 'K01'})
CREATE (:BanAn {maBan: 'MB000007', tenBan: 'Bàn 007', loaiBan: 'Bàn nhỏ', sucChua: 4, trangThai: 'Bàn đang trống', _maKhu: 'K01'})
CREATE (:BanAn {maBan: 'MB000008', tenBan: 'Bàn 008', loaiBan: 'Bàn nhỏ', sucChua: 4, trangThai: 'Bàn đang trống', _maKhu: 'K01'})
CREATE (:BanAn {maBan: 'MB000009', tenBan: 'Bàn 009', loaiBan: 'Bàn nhỏ', sucChua: 4, trangThai: 'Bàn đang trống', _maKhu: 'K01'})
CREATE (:BanAn {maBan: 'MB000010', tenBan: 'Bàn 010', loaiBan: 'Bàn nhỏ', sucChua: 4, trangThai: 'Bàn đang trống', _maKhu: 'K01'})
CREATE (:BanAn {maBan: 'MB000011', tenBan: 'Bàn 011', loaiBan: 'Bàn nhỏ', sucChua: 4, trangThai: 'Bàn đang trống', _maKhu: 'K01'})
CREATE (:BanAn {maBan: 'MB000012', tenBan: 'Bàn 012', loaiBan: 'Bàn nhỏ', sucChua: 4, trangThai: 'Bàn đang trống', _maKhu: 'K01'})
CREATE (:BanAn {maBan: 'MB000013', tenBan: 'Bàn 013', loaiBan: 'Bàn vừa', sucChua: 8, trangThai: 'Bàn đang trống', _maKhu: 'K02'})
CREATE (:BanAn {maBan: 'MB000014', tenBan: 'Bàn 014', loaiBan: 'Bàn vừa', sucChua: 8, trangThai: 'Bàn đang trống', _maKhu: 'K02'})
CREATE (:BanAn {maBan: 'MB000015', tenBan: 'Bàn 015', loaiBan: 'Bàn vừa', sucChua: 8, trangThai: 'Bàn đang trống', _maKhu: 'K02'})
CREATE (:BanAn {maBan: 'MB000016', tenBan: 'Bàn 016', loaiBan: 'Bàn vừa', sucChua: 8, trangThai: 'Bàn đang trống', _maKhu: 'K02'})
CREATE (:BanAn {maBan: 'MB000017', tenBan: 'Bàn 017', loaiBan: 'Bàn vừa', sucChua: 8, trangThai: 'Bàn đang trống', _maKhu: 'K02'})
CREATE (:BanAn {maBan: 'MB000018', tenBan: 'Bàn 018', loaiBan: 'Bàn vừa', sucChua: 8, trangThai: 'Bàn đang trống', _maKhu: 'K02'})
CREATE (:BanAn {maBan: 'MB000019', tenBan: 'Bàn 019', loaiBan: 'Bàn vừa', sucChua: 8, trangThai: 'Bàn đang trống', _maKhu: 'K02'})
CREATE (:BanAn {maBan: 'MB000020', tenBan: 'Bàn 020', loaiBan: 'Bàn vừa', sucChua: 8, trangThai: 'Bàn đang trống', _maKhu: 'K02'})
CREATE (:BanAn {maBan: 'MB000021', tenBan: 'Bàn 021', loaiBan: 'Bàn vừa', sucChua: 8, trangThai: 'Bàn đang trống', _maKhu: 'K02'})
CREATE (:BanAn {maBan: 'MB000022', tenBan: 'Bàn 022', loaiBan: 'Bàn vừa', sucChua: 8, trangThai: 'Bàn đang trống', _maKhu: 'K02'})
CREATE (:BanAn {maBan: 'MB000023', tenBan: 'Bàn 023', loaiBan: 'Bàn lớn', sucChua: 15, trangThai: 'Bàn đang trống', _maKhu: 'K03'})
CREATE (:BanAn {maBan: 'MB000024', tenBan: 'Bàn 024', loaiBan: 'Bàn lớn', sucChua: 15, trangThai: 'Bàn đang trống', _maKhu: 'K03'})
CREATE (:BanAn {maBan: 'MB000025', tenBan: 'Bàn 025', loaiBan: 'Bàn lớn', sucChua: 15, trangThai: 'Bàn đang trống', _maKhu: 'K03'})
CREATE (:BanAn {maBan: 'MB000026', tenBan: 'Bàn 026', loaiBan: 'Bàn lớn', sucChua: 15, trangThai: 'Bàn đang trống', _maKhu: 'K03'})
CREATE (:BanAn {maBan: 'MB000027', tenBan: 'Bàn 027', loaiBan: 'Bàn lớn', sucChua: 15, trangThai: 'Bàn đang trống', _maKhu: 'K03'})
CREATE (:BanAn {maBan: 'MB000028', tenBan: 'Bàn 028', loaiBan: 'Bàn lớn', sucChua: 15, trangThai: 'Bàn đang trống', _maKhu: 'K03'})
CREATE (:BanAn {maBan: 'MB000029', tenBan: 'Bàn 029', loaiBan: 'Bàn lớn', sucChua: 15, trangThai: 'Bàn đang trống', _maKhu: 'K03'})
CREATE (:BanAn {maBan: 'MB000030', tenBan: 'Bàn 030', loaiBan: 'Bàn lớn', sucChua: 15, trangThai: 'Bàn đang trống', _maKhu: 'K03'});

WITH 1 as dummy
MATCH (b:BanAn), (k:Khu) WHERE b._maKhu = k.maKhu MERGE (b)-[:THUOC_KHU]->(k);


// ===========================================================================
// PHẦN 3: GIAO DỊCH (HÓA ĐƠN VÀ PHIẾU ĐẶT BÀN)
// ===========================================================================

// 1. TẠO HÓA ĐƠN
CREATE (:HoaDon {maHoaDon: 'HD000001', trangThai: 'Đã thanh toán', ngayLapHoaDon: localdatetime() - duration('PT2H'), thue: 0.0, soTienKhachTra: 1000000.0, soTienThoi: 250000.0, _maNhanVien: 'NV000006', _maKhachHang: 'KH000001'})
CREATE (:HoaDon {maHoaDon: 'HD000002', trangThai: 'Đã thanh toán', ngayLapHoaDon: localdatetime() - duration('PT45M'), thue: 0.0, soTienKhachTra: 200000.0, soTienThoi: 35000.0, _maNhanVien: 'NV000007', _maKhachHang: 'KH000003'})
CREATE (:HoaDon {maHoaDon: 'HD000003', trangThai: 'Đã thanh toán', ngayLapHoaDon: localdatetime() - duration('PT90M'), thue: 0.0, soTienKhachTra: 120000.0, soTienThoi: 1000.0, _maNhanVien: 'NV000008', _maKhachHang: 'KH000005'})
CREATE (:HoaDon {maHoaDon: 'HD000004', trangThai: 'Đã thanh toán', ngayLapHoaDon: localdatetime() - duration('PT1H'), thue: 0.0, soTienKhachTra: 350000.0, soTienThoi: 25000.0, _maNhanVien: 'NV000006', _maKhachHang: 'KH000007'})
CREATE (:HoaDon {maHoaDon: 'HD000005', trangThai: 'Đã thanh toán', ngayLapHoaDon: localdatetime() - duration('PT20M'), thue: 0.0, soTienKhachTra: 350000.0, soTienThoi: 40000.0, _maNhanVien: 'NV000007', _maKhachHang: 'KH000009'})
CREATE (:HoaDon {maHoaDon: 'HD000006', trangThai: 'Đã thanh toán', ngayLapHoaDon: localdatetime() - duration('PT75M'), thue: 0.0, soTienKhachTra: 200000.0, soTienThoi: 33000.0, _maNhanVien: 'NV000008', _maKhachHang: 'KH000011'})
CREATE (:HoaDon {maHoaDon: 'HD000007', trangThai: 'Đã thanh toán', ngayLapHoaDon: localdatetime() - duration('PT3H'), thue: 0.0, soTienKhachTra: 700000.0, soTienThoi: 47000.0, _maNhanVien: 'NV000006', _maKhachHang: 'KH000013'})
CREATE (:HoaDon {maHoaDon: 'HD000008', trangThai: 'Đã thanh toán', ngayLapHoaDon: localdatetime() - duration('PT50M'), thue: 0.0, soTienKhachTra: 150000.0, soTienThoi: 1000.0, _maNhanVien: 'NV000007', _maKhachHang: 'KH000015'})
CREATE (:HoaDon {maHoaDon: 'HD000014', trangThai: 'Đã thanh toán', ngayLapHoaDon: localdatetime() - duration('P1D'), thue: 0.1, soTienKhachTra: 300000.0, soTienThoi: 6300.0, _maNhanVien: 'NV000006', _maKhachHang: 'KH000005', _maKhuyenMai: 'KM000002'})
CREATE (:HoaDon {maHoaDon: 'HD000015', trangThai: 'Đã thanh toán', ngayLapHoaDon: localdatetime() - duration('P3D'), thue: 0.1, soTienKhachTra: 350000.0, soTienThoi: 21100.0, _maNhanVien: 'NV000007', _maKhachHang: 'KH000007'})
CREATE (:HoaDon {maHoaDon: 'HD000016', trangThai: 'Đã thanh toán', ngayLapHoaDon: localdatetime() - duration('P5D'), thue: 0.1, soTienKhachTra: 450000.0, soTienThoi: 18360.0, _maNhanVien: 'NV000008', _maKhachHang: 'KH000010', _maKhuyenMai: 'KM000001'})
CREATE (:HoaDon {maHoaDon: 'HD000023', trangThai: 'Chưa thanh toán', ngayLapHoaDon: localdatetime() - duration('PT1H'), thue: 0.0, soTienKhachTra: 0.0, soTienThoi: 0.0, _maNhanVien: 'NV000006', _maKhachHang: 'KH000001'})
CREATE (:HoaDon {maHoaDon: 'HD000024', trangThai: 'Chưa thanh toán', ngayLapHoaDon: localdatetime() - duration('PT20M'), thue: 0.0, soTienKhachTra: 0.0, soTienThoi: 0.0, _maNhanVien: 'NV000007', _maKhachHang: 'KH000009'})
CREATE (:HoaDon {maHoaDon: 'HD000025', trangThai: 'Chưa thanh toán', ngayLapHoaDon: localdatetime() - duration('PT45M'), thue: 0.0, soTienKhachTra: 0.0, soTienThoi: 0.0, _maNhanVien: 'NV000008', _maKhachHang: 'KH000003'});

// Nối Hóa đơn với Khách & Nhân viên
WITH 1 as dummy
MATCH (hd:HoaDon), (nv:NhanVien) WHERE hd._maNhanVien = nv.maNhanVien MERGE (hd)-[:LAP_BOI]->(nv);
WITH 1 as dummy
MATCH (hd:HoaDon), (kh:KhachHang) WHERE hd._maKhachHang = kh.maKhachHang MERGE (hd)-[:CUA_KHACH]->(kh);
WITH 1 as dummy
MATCH (hd:HoaDon), (km:KhuyenMai) WHERE hd._maKhuyenMai = km.maKhuyenMai MERGE (hd)-[:AP_DUNG]->(km);

// Nối Hóa đơn với Bàn (HoaDon_Ban)
WITH 1 as dummy MATCH (hd:HoaDon {maHoaDon: 'HD000001'}), (b:BanAn {maBan: 'MB000002'}) MERGE (hd)-[:SU_DUNG_BAN]->(b);
WITH 1 as dummy MATCH (hd:HoaDon {maHoaDon: 'HD000002'}), (b:BanAn {maBan: 'MB000007'}) MERGE (hd)-[:SU_DUNG_BAN]->(b);
WITH 1 as dummy MATCH (hd:HoaDon {maHoaDon: 'HD000003'}), (b:BanAn {maBan: 'MB000010'}) MERGE (hd)-[:SU_DUNG_BAN]->(b);
WITH 1 as dummy MATCH (hd:HoaDon {maHoaDon: 'HD000004'}), (b:BanAn {maBan: 'MB000013'}) MERGE (hd)-[:SU_DUNG_BAN]->(b);
WITH 1 as dummy MATCH (hd:HoaDon {maHoaDon: 'HD000005'}), (b:BanAn {maBan: 'MB000018'}) MERGE (hd)-[:SU_DUNG_BAN]->(b);
WITH 1 as dummy MATCH (hd:HoaDon {maHoaDon: 'HD000006'}), (b:BanAn {maBan: 'MB000019'}) MERGE (hd)-[:SU_DUNG_BAN]->(b);
WITH 1 as dummy MATCH (hd:HoaDon {maHoaDon: 'HD000007'}), (b:BanAn {maBan: 'MB000024'}) MERGE (hd)-[:SU_DUNG_BAN]->(b);
WITH 1 as dummy MATCH (hd:HoaDon {maHoaDon: 'HD000008'}), (b:BanAn {maBan: 'MB000029'}) MERGE (hd)-[:SU_DUNG_BAN]->(b);
WITH 1 as dummy MATCH (hd:HoaDon {maHoaDon: 'HD000014'}), (b:BanAn {maBan: 'MB000001'}) MERGE (hd)-[:SU_DUNG_BAN]->(b);
WITH 1 as dummy MATCH (hd:HoaDon {maHoaDon: 'HD000015'}), (b:BanAn {maBan: 'MB000003'}) MERGE (hd)-[:SU_DUNG_BAN]->(b);
WITH 1 as dummy MATCH (hd:HoaDon {maHoaDon: 'HD000016'}), (b:BanAn {maBan: 'MB000005'}) MERGE (hd)-[:SU_DUNG_BAN]->(b);
WITH 1 as dummy MATCH (hd:HoaDon {maHoaDon: 'HD000023'}), (b:BanAn {maBan: 'MB000002'}) MERGE (hd)-[:SU_DUNG_BAN]->(b);
WITH 1 as dummy MATCH (hd:HoaDon {maHoaDon: 'HD000024'}), (b:BanAn {maBan: 'MB000018'}) MERGE (hd)-[:SU_DUNG_BAN]->(b);
WITH 1 as dummy MATCH (hd:HoaDon {maHoaDon: 'HD000025'}), (b:BanAn {maBan: 'MB000007'}) MERGE (hd)-[:SU_DUNG_BAN]->(b);

// Thêm Chi Tiết Hóa Đơn (Món)
WITH 1 as dummy MATCH (hd:HoaDon {maHoaDon: 'HD000001'}), (m:Mon {maMon: 'MM000003'}) MERGE (hd)-[:GOM_MON {soLuong: 2, donGia: 299000.0}]->(m);
WITH 1 as dummy MATCH (hd:HoaDon {maHoaDon: 'HD000001'}), (m:Mon {maMon: 'MM000007'}) MERGE (hd)-[:GOM_MON {soLuong: 1, donGia: 189000.0}]->(m);
WITH 1 as dummy MATCH (hd:HoaDon {maHoaDon: 'HD000001'}), (m:Mon {maMon: 'MM000053'}) MERGE (hd)-[:GOM_MON {soLuong: 4, donGia: 42000.0}]->(m);
WITH 1 as dummy MATCH (hd:HoaDon {maHoaDon: 'HD000014'}), (m:Mon {maMon: 'MM000001'}) MERGE (hd)-[:GOM_MON {soLuong: 2, donGia: 99000.0}]->(m);
WITH 1 as dummy MATCH (hd:HoaDon {maHoaDon: 'HD000023'}), (m:Mon {maMon: 'MM000003'}) MERGE (hd)-[:GOM_MON {soLuong: 2, donGia: 299000.0}]->(m);


// 2. TẠO PHIẾU ĐẶT BÀN (ĐÃ SỬA LỖI DATETIME BẰNG CÁCH CHỈ ĐỊNH RÕ NĂM-THÁNG-NGÀY-GIỜ)
CREATE (:PhieuDatBan {maPhieuDatBan: 'PDB00001', thoiGianDat: localdatetime({year: date().year, month: date().month, day: date().day, hour: 22, minute: 0, second: 0}), trangThai: 'Đã hoàn thành', tienDatCoc: 0.0, ghiChu: 'Khách VIP, chuẩn bị rượu', _maKhachHang: 'KH000002', _maNhanVien: 'NV000008'})
CREATE (:PhieuDatBan {maPhieuDatBan: 'PDB00006', thoiGianDat: localdatetime({year: date().year, month: date().month, day: date().day, hour: 22, minute: 0, second: 0}), trangThai: 'Đang chờ', tienDatCoc: 500000.0, ghiChu: 'Khách VIP, chuẩn bị rượu', _maKhachHang: 'KH000002', _maNhanVien: 'NV000008'})
CREATE (:PhieuDatBan {maPhieuDatBan: 'PDB00007', thoiGianDat: localdatetime({year: date().year, month: date().month, day: date().day, hour: 22, minute: 0, second: 0}), trangThai: 'Đang chờ', tienDatCoc: 0.0, _maKhachHang: 'KH000004', _maNhanVien: 'NV000006'})
CREATE (:PhieuDatBan {maPhieuDatBan: 'PDB00008', thoiGianDat: localdatetime({year: date().year, month: date().month, day: date().day, hour: 23, minute: 0, second: 0}), trangThai: 'Đang chờ', tienDatCoc: 200000.0, _maKhachHang: 'KH000006', _maNhanVien: 'NV000007'})
CREATE (:PhieuDatBan {maPhieuDatBan: 'PDB00010', thoiGianDat: localdatetime({year: date().year, month: date().month, day: date().day, hour: 18, minute: 0, second: 0}) + duration('P1D'), trangThai: 'Đang chờ', tienDatCoc: 500000.0, ghiChu: 'Tiệc sinh nhật sếp', _maKhachHang: 'KH000001', _maNhanVien: 'NV000002'});

// Nối Phiếu đặt bàn
WITH 1 as dummy
MATCH (p:PhieuDatBan), (nv:NhanVien) WHERE p._maNhanVien = nv.maNhanVien MERGE (p)-[:LAP_BOI]->(nv);
WITH 1 as dummy
MATCH (p:PhieuDatBan), (kh:KhachHang) WHERE p._maKhachHang = kh.maKhachHang MERGE (p)-[:DAT_BOI]->(kh);

// Nối Phiếu đặt bàn với Bàn
WITH 1 as dummy MATCH (p:PhieuDatBan {maPhieuDatBan: 'PDB00001'}), (b:BanAn {maBan: 'MB000004'}) MERGE (p)-[:GOM_BAN]->(b);
WITH 1 as dummy MATCH (p:PhieuDatBan {maPhieuDatBan: 'PDB00006'}), (b:BanAn {maBan: 'MB000004'}) MERGE (p)-[:GOM_BAN]->(b);
WITH 1 as dummy MATCH (p:PhieuDatBan {maPhieuDatBan: 'PDB00007'}), (b:BanAn {maBan: 'MB000009'}) MERGE (p)-[:GOM_BAN]->(b);
WITH 1 as dummy MATCH (p:PhieuDatBan {maPhieuDatBan: 'PDB00008'}), (b:BanAn {maBan: 'MB000015'}) MERGE (p)-[:GOM_BAN]->(b);
WITH 1 as dummy MATCH (p:PhieuDatBan {maPhieuDatBan: 'PDB00010'}), (b:BanAn {maBan: 'MB000001'}) MERGE (p)-[:GOM_BAN]->(b);

// Chi tiết phiếu đặt bàn
WITH 1 as dummy MATCH (p:PhieuDatBan {maPhieuDatBan: 'PDB00001'}), (m:Mon {maMon: 'MM000007'}) MERGE (p)-[:GOM_MON {soLuong: 2, donGia: 189000.0}]->(m);
WITH 1 as dummy MATCH (p:PhieuDatBan {maPhieuDatBan: 'PDB00006'}), (m:Mon {maMon: 'MM000007'}) MERGE (p)-[:GOM_MON {soLuong: 2, donGia: 189000.0}]->(m);
WITH 1 as dummy MATCH (p:PhieuDatBan {maPhieuDatBan: 'PDB00008'}), (m:Mon {maMon: 'MM000020'}) MERGE (p)-[:GOM_MON {soLuong: 2, donGia: 69000.0}]->(m);


// 3. TẠO HÓA ĐƠN TỪ PHIẾU ĐẶT BÀN (HD000009)
CREATE (:HoaDon {maHoaDon: 'HD000009', trangThai: 'Đã thanh toán', ngayLapHoaDon: localdatetime(), thue: 0.0, soTienKhachTra: 400000.0, soTienThoi: 22000.0, _maNhanVien: 'NV000008', _maKhachHang: 'KH000002', _maPhieuDatBan: 'PDB00001'});

WITH 1 as dummy MATCH (hd:HoaDon {maHoaDon: 'HD000009'}), (nv:NhanVien {maNhanVien: 'NV000008'}) MERGE (hd)-[:LAP_BOI]->(nv);
WITH 1 as dummy MATCH (hd:HoaDon {maHoaDon: 'HD000009'}), (kh:KhachHang {maKhachHang: 'KH000002'}) MERGE (hd)-[:CUA_KHACH]->(kh);
WITH 1 as dummy MATCH (hd:HoaDon {maHoaDon: 'HD000009'}), (pdb:PhieuDatBan {maPhieuDatBan: 'PDB00001'}) MERGE (hd)-[:TU_PHIEU]->(pdb);
WITH 1 as dummy MATCH (hd:HoaDon {maHoaDon: 'HD000009'}), (b:BanAn {maBan: 'MB000004'}) MERGE (hd)-[:SU_DUNG_BAN]->(b);
WITH 1 as dummy MATCH (hd:HoaDon {maHoaDon: 'HD000009'}), (m:Mon {maMon: 'MM000007'}) MERGE (hd)-[:GOM_MON {soLuong: 2, donGia: 189000.0}]->(m);


// 4. LỊCH SỬ HỦY ĐẶT BÀN
CREATE (:LichSuHuyDatBan {maLog: 1, maPhieuDatBan: 'PDB00012', tenBan: 'Bàn 005', tenKhachHang: 'Nguyễn Văn An', sdtKhachHang: '0905123456', tenNhanVien: 'Trần Ngọc Anh', thoiGianHuy: localdatetime() - duration('PT2H'), lyDoHuy: 'Khách bận đột xuất không đến được', _maNhanVien: 'NV000006'})
CREATE (:LichSuHuyDatBan {maLog: 2, maPhieuDatBan: 'PDB00013', tenBan: 'Bàn 010', tenKhachHang: 'Trần Thị Bình', sdtKhachHang: '0912345678', tenNhanVien: 'Thân Trọng Thắng', thoiGianHuy: localdatetime() - duration('P1D'), lyDoHuy: 'Trời mưa to, khách hủy lịch', _maNhanVien: 'NV000002'})
CREATE (:LichSuHuyDatBan {maLog: 3, maPhieuDatBan: 'PDB00014', tenBan: 'Bàn 013, Bàn 014', tenKhachHang: 'Lê Quốc Cường', sdtKhachHang: '0987654321', tenNhanVien: 'Nguyễn Văn Bình', thoiGianHuy: localdatetime() - duration('P2D'), lyDoHuy: 'Nhóm bạn dời lịch sang tuần sau', _maNhanVien: 'NV000007'});

WITH 1 as dummy
MATCH (l:LichSuHuyDatBan), (nv:NhanVien) WHERE l._maNhanVien = nv.maNhanVien MERGE (l)-[:HUY_BOI]->(nv);

// ===========================================================================
// PHẦN 4: CẬP NHẬT TRẠNG THÁI & DỌN DẸP TEMPORARY PROPERTIES
// ===========================================================================

// Cập nhật trạng thái Bàn theo nghiệp vụ
MATCH (b:BanAn) WHERE b.maBan IN ['MB000002', 'MB000018', 'MB000007'] SET b.trangThai = 'Bàn đang phục vụ';
MATCH (b:BanAn) WHERE b.maBan IN ['MB000004', 'MB000009', 'MB000015', 'MB000027', 'MB000001'] SET b.trangThai = 'Bàn đang chờ';

// Xóa các thuộc tính tạm thời (_maKhu, _maLoai, v.v.)
MATCH (n)
REMOVE n._maTang, n._maKhu, n._maLoai, n._maChucVu, n._maNhanVien, n._maKhachHang, n._maKhuyenMai, n._maPhieuDatBan;