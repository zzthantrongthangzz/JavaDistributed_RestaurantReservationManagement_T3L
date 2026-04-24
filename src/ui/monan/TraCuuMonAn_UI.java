package ui.monan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;

import entity.MonAn;
import ui.TrangChu_UI;
import entity.LoaiMon;
import dao.MonAn_DAO;
import dao.LoaiMon_DAO;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TraCuuMonAn_UI extends JPanel {

    private final Color MAU_NEN_INPUT = new Color(45, 49, 56);
    private final Color MAU_NEN_TAB = new Color(48, 52, 56);
    private final Color MAU_NEN_ITEM = new Color(31, 32, 34);
    private final Color MAU_CHU_CHUNG = Color.WHITE;
    private final Color MAU_THANH_TIM_KIEM = new Color(60, 64, 68);
    private final Color MAU_CAM_GIA = new Color(255, 180, 0); 
    private final int KICH_THUOC_ITEM_RONG = 220;
    private final int KICH_THUOC_ITEM_CAO = 300; 
    
    private MonAn_DAO monAn_DAO;
    private LoaiMon_DAO loaiMonDAO;
    private JPanel panelLuoiMonAn;
    private JLabel lblSoMon;
    private JTextField txtTimKiem;
    private JTextField txtTimKiem2;
    private JComboBox<Object> cmbBoLoc;
    private JPanel panelChinh;
    
    private List<MonAn> danhSachMonAnHienThi; 

    // Khởi tạo giao diện tra cứu món ăn
    public TraCuuMonAn_UI() {
    	monAn_DAO = new MonAn_DAO();
    	loaiMonDAO = new LoaiMon_DAO();
    	danhSachMonAnHienThi = monAn_DAO.docDanhSachMon(); 
    	
        setLayout(new BorderLayout());
        setBackground(MAU_NEN_TAB); 
        
        this.panelChinh = new JPanel(new BorderLayout(0, 15));
        panelChinh.setBackground(MAU_NEN_TAB);
        panelChinh.setBorder(new EmptyBorder(3, 25, 20, 25));

        JPanel panelDieuKhien = new JPanel(new BorderLayout(15, 0));
        panelDieuKhien.setBackground(MAU_NEN_TAB);
        panelDieuKhien.setPreferredSize(new Dimension(0, 50));

        JPanel panelTimKiemLoc = new JPanel();
        panelTimKiemLoc.setLayout(new BoxLayout(panelTimKiemLoc, BoxLayout.X_AXIS));
        panelTimKiemLoc.setBackground(MAU_NEN_TAB);

        this.txtTimKiem = taoThanhTimKiem("Nhập mã món. . .", "ma");
        txtTimKiem.setPreferredSize(new Dimension(270, 40));
        txtTimKiem.setMaximumSize(new Dimension(400, 40));

        JLabel lblMaMon = new JLabel("Mã món: ");
        lblMaMon.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblMaMon.setForeground(Color.WHITE);
        lblMaMon.setBackground(new Color(124, 124, 124));
        lblMaMon.setOpaque(true); 
        lblMaMon.setHorizontalAlignment(SwingConstants.CENTER); 
        lblMaMon.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        
        JPanel searchWrapper = new JPanel(new BorderLayout());
        searchWrapper.setBackground(MAU_NEN_TAB);
        searchWrapper.add(txtTimKiem, BorderLayout.CENTER);
        searchWrapper.add(lblMaMon, BorderLayout.WEST);
        searchWrapper.setMaximumSize(new Dimension(450, 40));
        
        this.txtTimKiem2 = taoThanhTimKiem("Nhập tên món. . .","ten"); 
        txtTimKiem2.setPreferredSize(new Dimension(270, 40));
        txtTimKiem2.setMaximumSize(new Dimension(400, 40));

        JLabel lblTenMon = new JLabel("Tên món:");
        lblTenMon.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTenMon.setForeground(Color.WHITE);
        lblTenMon.setBackground(new Color(124, 124, 124));
        lblTenMon.setOpaque(true);
        lblTenMon.setHorizontalAlignment(SwingConstants.CENTER);
        lblTenMon.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        JPanel searchWrapper2 = new JPanel(new BorderLayout());
        searchWrapper2.setBackground(MAU_NEN_TAB);
        searchWrapper2.add(txtTimKiem2, BorderLayout.CENTER);
        searchWrapper2.add(lblTenMon, BorderLayout.WEST);
        searchWrapper2.setMaximumSize(new Dimension(450, 40));
        
        JLabel lblLoai = new JLabel("Loại");
        lblLoai.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblLoai.setForeground(Color.WHITE);
        lblLoai.setBackground(new Color(124, 124, 124));
        lblLoai.setOpaque(true);
        lblLoai.setHorizontalAlignment(SwingConstants.CENTER);
        lblLoai.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        
        JPanel comboWrapper = new JPanel(new BorderLayout());
        comboWrapper.setBackground(MAU_NEN_TAB);
        
        this.cmbBoLoc = new JComboBox<Object>();
        taiDuLieuLoaiMon();
        cmbBoLoc.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cmbBoLoc.setBackground(MAU_THANH_TIM_KIEM);
        cmbBoLoc.setForeground(MAU_CHU_CHUNG);
        
        cmbBoLoc.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        cmbBoLoc.addActionListener(e -> {
            thucHienLoc(); 
        });
        cmbBoLoc.setFocusable(false);
        
	        cmbBoLoc.setUI(new BasicComboBoxUI() {
	          @Override
	          protected JButton createArrowButton() {
	              
	          	ImageIcon icon = new ImageIcon(getClass().getResource("/IMG/muitenxuong_32px.png"));
	              Image img = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
	              JButton button = new JButton(new ImageIcon(img));
	              
	              button.setBackground(MAU_THANH_TIM_KIEM);
	              button.setOpaque(true);
	              button.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
	              return button;
	          }
	      });
      
	        cmbBoLoc.setBorder(BorderFactory.createCompoundBorder(
	          BorderFactory.createLineBorder(new Color(70, 72, 87), 1),
	          new EmptyBorder(0, 15, 0, 5) 
	      ));

        cmbBoLoc.setPreferredSize(new Dimension(150, 40));
        cmbBoLoc.setMaximumSize(new Dimension(150, 40));
        
        comboWrapper.add(lblLoai, BorderLayout.WEST);   
        comboWrapper.add(cmbBoLoc, BorderLayout.CENTER); 
        
        int chuanChieuCao = searchWrapper.getPreferredSize().height; 
        Dimension maxSize = new Dimension(Integer.MAX_VALUE, chuanChieuCao);
        searchWrapper.setMaximumSize(maxSize);
     	searchWrapper2.setMaximumSize(maxSize);
     	comboWrapper.setMaximumSize(maxSize); 
     
        panelTimKiemLoc.add(searchWrapper); 
        panelTimKiemLoc.add(Box.createRigidArea(new Dimension(10, 0)));
        panelTimKiemLoc.add(searchWrapper2); 
        panelTimKiemLoc.add(Box.createRigidArea(new Dimension(10, 0)));
        panelTimKiemLoc.add(comboWrapper);

        JPanel panelNutChucNang = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelNutChucNang.setBackground(MAU_NEN_TAB);
        JButton btnLamMoi = taoNutChucNang("Làm mới", new Color(30, 144, 255));
        
        Dimension kichThuocNut = new Dimension(115, 40);
        btnLamMoi.setPreferredSize(kichThuocNut);

        panelNutChucNang.add(btnLamMoi);
        
        panelDieuKhien.add(panelTimKiemLoc, BorderLayout.WEST);
        panelDieuKhien.add(panelNutChucNang, BorderLayout.EAST);
               
        JPanel panelTopWrapper = new JPanel(new BorderLayout());
        panelTopWrapper.setBackground(MAU_NEN_TAB);        
        panelTopWrapper.add(taoPanelTieuDe(), BorderLayout.NORTH);      
        panelTopWrapper.add(panelDieuKhien, BorderLayout.CENTER);       
        panelChinh.add(panelTopWrapper, BorderLayout.NORTH);
        
        JPanel panelNoiDung = new JPanel(new BorderLayout(0, 15));
        panelNoiDung.setBackground(MAU_NEN_TAB);
        
        this.lblSoMon = new JLabel("Số món trong nhà hàng: " + danhSachMonAnHienThi.size());
        lblSoMon.setForeground(new Color(180, 180, 180));
        lblSoMon.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSoMon.setBorder(new EmptyBorder(0, 0, 5, 0)); 
        panelNoiDung.add(lblSoMon, BorderLayout.NORTH);

        this.panelLuoiMonAn = new WrapFlowPanel();

        panelLuoiMonAn.setLayout(new FlowLayout(FlowLayout.LEFT, 40, 35));
        panelLuoiMonAn.setBackground(MAU_NEN_TAB);
        
        capNhatLuoiMonAn(danhSachMonAnHienThi);
        
        JScrollPane scrollPane = new JScrollPane(panelLuoiMonAn); 
        tuyChinhScrollBar(scrollPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(MAU_NEN_TAB); 
        
        panelChinh.setFocusable(true);	
        panelChinh.requestFocusInWindow();
        
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);

        panelNoiDung.add(scrollPane, BorderLayout.CENTER);
        panelChinh.add(panelNoiDung, BorderLayout.CENTER);
        add(panelChinh, BorderLayout.CENTER);
    }
    
    // Tạo panel tiêu đề
    private JPanel taoPanelTieuDe() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MAU_NEN_TAB);
        panel.setPreferredSize(new Dimension(0, 80)); 
        panel.setBorder(new EmptyBorder(2, 0, 10, 0)); 

        JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        contentPanel.setBackground(MAU_NEN_TAB);

        JLabel lblTieuDe = new JLabel("TRA CỨU MÓN ĂN");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTieuDe.setForeground(Color.WHITE);
        
        lblTieuDe.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 3, 0, new Color(100, 104, 124)),
            new EmptyBorder(10, 30, 10, 30)
        ));

        contentPanel.add(lblTieuDe);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    // Lớp nội bộ để xử lý Layout tự động xuống dòng
    private class WrapFlowPanel extends JPanel implements Scrollable {

        public WrapFlowPanel() {
        }

        @Override
        public Dimension getPreferredSize() {	
            int parentWidth = getParent().getWidth();

            if (parentWidth == 0) {
                return super.getPreferredSize();
            }

            int hgap = ((FlowLayout) getLayout()).getHgap();
            int vgap = ((FlowLayout) getLayout()).getVgap();
            
            int availableWidth = parentWidth - hgap;

            int itemsPerRow = Math.max(1, availableWidth / (KICH_THUOC_ITEM_RONG + hgap));
            
            int componentCount = getComponentCount();
            if (componentCount == 0) {
                return new Dimension(parentWidth, 0);
            }

            int rowCount = (int) Math.ceil((double) componentCount / itemsPerRow);
            int totalHeight = (rowCount * KICH_THUOC_ITEM_CAO) + ((rowCount - 1) * vgap) + vgap * 2; 

            return new Dimension(parentWidth, totalHeight);
        }
        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }
        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 16;
        }
        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 16;
        }
        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }
        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    // Tạo item hiển thị món ăn
    private JPanel taoItemMonAn(MonAn monAn) {
        
        JPanel panelItem = new JPanel(new BorderLayout());
        panelItem.setPreferredSize(new Dimension(KICH_THUOC_ITEM_RONG, KICH_THUOC_ITEM_CAO)); 
        panelItem.setBackground(MAU_NEN_TAB); 
        panelItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JPanel panelHinhAnh = new JPanel(new BorderLayout()) {
             private final int ARC_SIZE = 15; 
             
             @Override
             protected void paintComponent(Graphics g) {
                 Graphics2D g2d = (Graphics2D) g.create();
                 g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                 g2d.setColor(MAU_NEN_ITEM);
                 
                 g2d.fillRoundRect(0, 0, getWidth(), getHeight(), ARC_SIZE, ARC_SIZE);
                 
                 g2d.dispose();
             }
        };
        panelHinhAnh.setOpaque(false); 
        panelHinhAnh.setPreferredSize(new Dimension(KICH_THUOC_ITEM_RONG, KICH_THUOC_ITEM_CAO));
        
        JPanel panelChuaAnh = new JPanel(new BorderLayout());
        panelChuaAnh.setOpaque(false); 
        panelChuaAnh.setBorder(new EmptyBorder(10, 10, 10, 10)); 
        
        JLabel lblHinhAnh = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(monAn.getDuongDanAnh()));
            Image img = icon.getImage().getScaledInstance(KICH_THUOC_ITEM_RONG - 40, KICH_THUOC_ITEM_RONG - 40, Image.SCALE_SMOOTH);
            lblHinhAnh.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            lblHinhAnh.setText("Ảnh Lỗi");
            lblHinhAnh.setForeground(MAU_CHU_CHUNG);
        }
        lblHinhAnh.setHorizontalAlignment(SwingConstants.CENTER);
        lblHinhAnh.setVerticalAlignment(SwingConstants.CENTER);
        
        panelChuaAnh.add(lblHinhAnh, BorderLayout.CENTER);
        panelHinhAnh.add(panelChuaAnh, BorderLayout.NORTH);

        JPanel panelThongTin = new JPanel();
        panelThongTin.setLayout(new BoxLayout(panelThongTin, BoxLayout.Y_AXIS));
        panelThongTin.setOpaque(false); 
        panelThongTin.setBorder(new EmptyBorder(0, 0, 10, 0)); 

        JLabel lblTenMon = new JLabel(monAn.getTenMon());
        lblTenMon.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTenMon.setForeground(MAU_CHU_CHUNG);
        lblTenMon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblGia = new JLabel(String.format("%,.0f VNĐ", monAn.getGia()));
        lblGia.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblGia.setForeground(MAU_CAM_GIA); 
        lblGia.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelThongTin.add(lblTenMon);
        panelThongTin.add(Box.createRigidArea(new Dimension(0, 5))); 
        panelThongTin.add(lblGia);
        
        
        panelHinhAnh.add(panelThongTin, BorderLayout.CENTER); 
        panelItem.add(panelHinhAnh, BorderLayout.CENTER);       

        panelItem.addMouseListener(new MouseAdapter() {
          
        	@Override
            public void mouseClicked(MouseEvent e) {
                Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(TraCuuMonAn_UI.this);
                
                ChiTietMonAn_UI chiTietDialog = new ChiTietMonAn_UI(parentFrame, monAn);
                
                chiTietDialog.setVisible(true);
            }
        });

        return panelItem;
    }
    
    // Tạo thanh tìm kiếm
    private JTextField taoThanhTimKiem(String placeholder, final String loaiTimKiem) {
        JTextField textField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon icon = new ImageIcon(getClass().getResource("/IMG/search.png"));
                Image img = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                Icon searchIcon = new ImageIcon(img);
                
                int y = (getHeight() - searchIcon.getIconHeight()) / 2;
                
                int x = getWidth() - searchIcon.getIconWidth() - 10; 
                searchIcon.paintIcon(this, g, x, y); 
            }
        };
        
        textField.addActionListener(e -> {
            if (loaiTimKiem.equals("ma")) {
                thucHienTimKiemTheoMa();
            } else if (loaiTimKiem.equals("ten")) {
                thucHienTimKiemTheoTen();
            }
        });
        
        textField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int iconWidth = 24;
                int iconMargin = 10;
                int iconX = textField.getWidth() - iconWidth - iconMargin;
                Rectangle iconBounds = new Rectangle(iconX, 0, iconWidth + iconMargin, textField.getHeight());

                if (iconBounds.contains(e.getPoint())) {
                    if (loaiTimKiem.equals("ma")) {
                        thucHienTimKiemTheoMa();
                    } else if (loaiTimKiem.equals("ten")) {
                        thucHienTimKiemTheoTen();
                    }
                }
            }
        });

        textField.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int iconWidth = 24;
                int iconMargin = 10;
                int iconX = textField.getWidth() - iconWidth - iconMargin;
                Rectangle iconBounds = new Rectangle(iconX, 0, iconWidth + iconMargin, textField.getHeight());
                
                if (iconBounds.contains(e.getPoint())) {
                    textField.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    textField.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
                }
            }
        });
        
        textField.setText(placeholder);
        textField.setForeground(new Color(150, 150, 160));
        textField.setBackground(MAU_THANH_TIM_KIEM);
        textField.setCaretColor(MAU_CHU_CHUNG);
        	
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 72, 87), 1),
            new EmptyBorder(8, 15, 8, 40) 
        ));
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(MAU_CHU_CHUNG);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(new Color(150, 150, 160));
                }
            }
        });
        
        return textField;
    }
   
    // Tạo nút chức năng
    private JButton taoNutChucNang(String text, Color mauNen) {		
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(mauNen);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(mauNen.brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(mauNen);
            }
        });
        
        button.addActionListener(e -> {
            if (text.equals("Làm mới")) {
                List<MonAn> toanBoMonAn = monAn_DAO.docDanhSachMon();
                capNhatLuoiMonAn(toanBoMonAn);
                txtTimKiem.setForeground(new Color(150, 150, 160));
                txtTimKiem.setText("Nhập mã món. . ."); 
                txtTimKiem2.setForeground(new Color(150, 150, 160));
                txtTimKiem2.setText("Nhập tên món. . .");
                cmbBoLoc.setSelectedIndex(0);
                
                panelChinh.requestFocusInWindow();  
            } 
            else {
                System.out.println("Nút: " + text + " vừa được chọn.");
            }
        });
       
        return button;
    }
    
    // Cập nhật hiển thị lưới món ăn
    private void capNhatLuoiMonAn(List<MonAn> danhSach) {
        danhSachMonAnHienThi = danhSach;    
        panelLuoiMonAn.removeAll();
        for (MonAn monAn : danhSachMonAnHienThi) {
            panelLuoiMonAn.add(taoItemMonAn(monAn));
        }      
        lblSoMon.setText("Số món kinh doanh: " + danhSachMonAnHienThi.size());
        panelLuoiMonAn.revalidate();
        panelLuoiMonAn.repaint();
    }
    
    // Tìm kiếm món ăn theo mã
    private void thucHienTimKiemTheoMa() {
        String tuKhoa = txtTimKiem.getText().trim();
        String placeholder = "Nhập mã món. . .";
        List<MonAn> ketQua;

        if (tuKhoa.isEmpty() || tuKhoa.equals(placeholder)) {
            ketQua = monAn_DAO.docDanhSachMon();
        } else {
            ketQua = monAn_DAO.timKiemTheoMa(tuKhoa);
            if (ketQua.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy món ăn nào với mã: \"" + tuKhoa + "\"", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        capNhatLuoiMonAn(ketQua);
    }

    // Tìm kiếm món ăn theo tên
    private void thucHienTimKiemTheoTen() {
        String tuKhoa = txtTimKiem2.getText().trim();
        String placeholder = "Nhập tên món. . ."; 
        List<MonAn> ketQua;

        if (tuKhoa.isEmpty() || tuKhoa.equals(placeholder)) {
            ketQua = monAn_DAO.docDanhSachMon();
        } else {
            ketQua = monAn_DAO.timKiemTheoTen(tuKhoa);
            if (ketQua.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy món ăn nào với tên: \"" + tuKhoa + "\"", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        capNhatLuoiMonAn(ketQua);
    }
    
    // Tải danh sách loại món vào ComboBox
    private void taiDuLieuLoaiMon() {
        try {
            if (this.cmbBoLoc == null) {
                this.cmbBoLoc = new JComboBox<Object>();
            }
            
            cmbBoLoc.removeAllItems();
            
            cmbBoLoc.addItem("Tất cả");
            
            List<LoaiMon> dsLoai = loaiMonDAO.docDanhSachLoaiMon();
            
            for (LoaiMon loai : dsLoai) {
                cmbBoLoc.addItem(loai); 
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách loại món!");
        }
    }
    
    // Lọc món ăn theo loại
    private void thucHienLoc() {
        Object itemDuocChon = cmbBoLoc.getSelectedItem();
        
        if (itemDuocChon == null) {
            return; 
        }
        
        List<MonAn> ketQuaLoc;

        if (itemDuocChon instanceof String && itemDuocChon.equals("Tất cả")) {
            ketQuaLoc = monAn_DAO.docDanhSachMon();
        } else if (itemDuocChon instanceof LoaiMon) {
            String tenLoai = ((LoaiMon) itemDuocChon).getTenLoai();
            ketQuaLoc = monAn_DAO.locMonAnTheoLoai(tenLoai);
        } else {
            ketQuaLoc = monAn_DAO.docDanhSachMon(); 
        }
        
        capNhatLuoiMonAn(ketQuaLoc);
        
        txtTimKiem.setForeground(new Color(150, 150, 160));
        txtTimKiem.setText("Nhập mã món. . ."); 

        txtTimKiem2.setForeground(new Color(150, 150, 160));
        txtTimKiem2.setText("Nhập tên món. . .");
        
        panelChinh.requestFocusInWindow(); 
    }
    
    // Tùy chỉnh thanh cuộn
    private void tuyChinhScrollBar(JScrollPane scrollPane) {
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setPreferredSize(new Dimension(8, 0)); 
        verticalScrollBar.setBackground(MAU_NEN_INPUT);
        verticalScrollBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(100, 105, 120); 
                this.trackColor = MAU_NEN_INPUT; 
                this.thumbDarkShadowColor = new Color(80, 85, 100);
                this.thumbHighlightColor = new Color(120, 125, 140);
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0)); 
                return button;
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0)); 
                return button;
            }

            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                if (thumbBounds.isEmpty() || !verticalScrollBar.isEnabled()) {
                    return;
                }
                g.setColor(new Color(100, 105, 120));
                g.fillRoundRect(thumbBounds.x + 2, thumbBounds.y, thumbBounds.width - 4, thumbBounds.height, 4, 4);
            }

            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                g.setColor(MAU_NEN_INPUT);
                g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
            }
        });

        JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
        horizontalScrollBar.setPreferredSize(new Dimension(0, 8)); 
        horizontalScrollBar.setBackground(MAU_NEN_INPUT);
        horizontalScrollBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(100, 105, 120);
                this.trackColor = MAU_NEN_INPUT;
                this.thumbDarkShadowColor = new Color(80, 85, 100);
                this.thumbHighlightColor = new Color(120, 125, 140);
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }

            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                if (thumbBounds.isEmpty() || !horizontalScrollBar.isEnabled()) {
                    return;
                }
                g.setColor(new Color(100, 105, 120));
                g.fillRoundRect(thumbBounds.x, thumbBounds.y + 2, thumbBounds.width, thumbBounds.height - 4, 4, 4);
            }

            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                g.setColor(MAU_NEN_INPUT);
                g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
            }
        });
    }
    
}