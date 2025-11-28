/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package gui;
import code.Item;
import java.util.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import javax.swing.JPanel;
import javax.swing.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.table.DefaultTableModel;
import code.ItemDAO;
import code.UserItem;
import code.UserDAO;
import code.CategoryItem;
import code.CategoryDAO;
import code.StokItem;
import code.StokDAO;
import code.TransactionsDAO;
import code.TransactionIn;
import code.TransactionOut;
import code.DatabaseConnection;
import code.GradientPanel;
import java.awt.CardLayout;
import java.sql.Connection;
import javax.swing.JOptionPane;
import java.security.MessageDigest;

/**
 *
 * @author DHAIFAN
 */
public class Admin extends javax.swing.JFrame {
    private boolean isAdding = false;
    private CardLayout cl;
    private StokDAO stokDao;
    private StokItem selectedItem;
    private int selectedIdKategori = -1;
    private int selectedData = -1;
    private int currentUserId;
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Admin.class.getName());
    
    public void setCurrentUserId(int idUser) {
        this.currentUserId = idUser;
    }
    
    /**
     * Creates new form Admin
     */
    public Admin() {
        initComponents();
        updateDashboard();

        // Atur mainPanel untuk menu utama
        mainPanel.setLayout(new CardLayout());
        mainPanel.add(panelDashboard, "card2");
        mainPanel.add(panelDataBarang, "card3");
        mainPanel.add(panelStok, "card4");
        mainPanel.add(panelUserManagement, "card5");

        // Buat subPanel di dalam panelDataBarang untuk submenu
        subPanelDataBarang.setLayout(new CardLayout());
        subPanelDataBarang.add(panelLihat, "card6");
        subPanelDataBarang.add(panelTambah, "card7");
        subPanelDataBarang.add(panelKategori, "card8");
        
        subPanelStok.setLayout(new CardLayout());
        subPanelStok.add(panelStokMasuk, "card9");
        subPanelStok.add(panelStokKeluar, "card10");
        subPanelStok.add(panelRiwayatStok, "card11");

        // Menu utama
        CardLayout cl = (CardLayout) mainPanel.getLayout();
        jMenu1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cl.show(mainPanel, "card2"); // panelDashboard
            }
        });
        jMenu2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cl.show(mainPanel, "card3"); // panelDataBarang
            }
        });
        jMenu3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cl.show(mainPanel, "card4"); // panelStok
            }
        });
        jMenu4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cl.show(mainPanel, "card5"); // panelUserManagement
            }
        });

        // Tombol submenu di panelDataBarang
        CardLayout clData = (CardLayout) subPanelDataBarang.getLayout();
        btnLihat.addActionListener(e -> clData.show(subPanelDataBarang, "card6"));
        btnTambah1.addActionListener(e -> clData.show(subPanelDataBarang, "card7"));
        btnKategori.addActionListener(e -> clData.show(subPanelDataBarang, "card8"));
        btnLihat10.addActionListener(e -> clData.show(subPanelDataBarang, "card6"));
        btnTambah20.addActionListener(e -> clData.show(subPanelDataBarang, "card7"));
        btnKategori10.addActionListener(e -> clData.show(subPanelDataBarang, "card8"));
        btnLihat20.addActionListener(e -> clData.show(subPanelDataBarang, "card6"));
        btnTambah30.addActionListener(e -> clData.show(subPanelDataBarang, "card7"));
        btnKategori20.addActionListener(e -> clData.show(subPanelDataBarang, "card8"));
        
        CardLayout clStok = (CardLayout) subPanelStok.getLayout();
        btnStokMasuk.addActionListener(e -> clStok.show(subPanelStok, "card9"));
        btnStokKeluar.addActionListener(e -> clStok.show(subPanelStok, "card10"));
        btnRiwayatStok.addActionListener(e -> clStok.show(subPanelStok, "card11"));
        btnStokMasuk1.addActionListener(e -> clStok.show(subPanelStok, "card9"));
        btnStokKeluar1.addActionListener(e -> clStok.show(subPanelStok, "card10"));
        btnRiwayatStok1.addActionListener(e -> clStok.show(subPanelStok, "card11"));
        btnStokMasuk2.addActionListener(e -> clStok.show(subPanelStok, "card9"));
        btnStokKeluar2.addActionListener(e -> clStok.show(subPanelStok, "card10"));
        btnRiwayatStok2.addActionListener(e -> clStok.show(subPanelStok, "card11"));
        

        try {
            stokDao = new StokDAO(DatabaseConnection.getConnection());
            loadItemsCombo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setTabel();
        loadAktivitasTerakhir();
        loadKategori();
        loadKategoriTable();
        loadHistoryTable();
        loadDataBarang();
        loadUsersCombo();
    }
    
    private void loadItemsCombo() {
        try {
            // koneksi & DAO
            Connection conn = code.DatabaseConnection.getConnection();
            StokDAO dao = new StokDAO(conn);

            // hapus item lama
            cbBarangMasuk.removeAllItems();
            cbBarangMasuk1.removeAllItems();

            // tambahkan placeholder
            cbBarangMasuk.addItem(new StokItem(0, "-- Pilih Item --", 0, 0 ,""));
            cbBarangMasuk1.addItem(new StokItem(0, "-- Pilih Item --", 0, 0 , ""));

            // ambil semua item dari DB dan tambahkan ke ComboBox
            for (StokItem i : dao.getAllItems()) {
                cbBarangMasuk.addItem(i);
                cbBarangMasuk1.addItem(i);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadKategori() {
        try {
            Connection conn = code.DatabaseConnection.getConnection();
            CategoryDAO dao = new CategoryDAO(conn);

            cbKategori1.removeAllItems();

            for (CategoryItem c : dao.getAllCategories()) {
                cbKategori1.addItem(c);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadKategoriTable() {
        try {
            Connection conn = code.DatabaseConnection.getConnection();
            CategoryDAO dao = new CategoryDAO(conn);

            DefaultTableModel model = (DefaultTableModel) jTable3.getModel();
            model.setRowCount(0);

            for (CategoryItem c : dao.getAllCategories()) {
                model.addRow(new Object[]{
                    c.getId(),
                    c.getNama()
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadHistoryTable() {
        try {
            Connection conn = code.DatabaseConnection.getConnection();
            String sql = "SELECT t.tanggal, i.nama_item AS nama, t.jumlah, t.keterangan, u.username " +
                         "FROM transactions_in t " +
                         "JOIN items i ON t.id_item = i.id_item " +
                         "JOIN users u ON t.id_user = u.id_user " +
                         "UNION ALL " +
                         "SELECT t.tanggal, i.nama_item AS nama, -t.jumlah AS jumlah, t.keterangan, u.username " +
                         "FROM transactions_out t " +
                         "JOIN items i ON t.id_item = i.id_item " +
                         "JOIN users u ON t.id_user = u.id_user " +
                         "ORDER BY tanggal DESC";

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            DefaultTableModel model = (DefaultTableModel) jTable4.getModel();
            model.setRowCount(0);

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getTimestamp("tanggal"),
                    rs.getString("nama"),
                    rs.getInt("jumlah"),
                    rs.getString("keterangan"),
                    rs.getString("username")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
        private void loadHistory() {
            try {
                Connection conn = code.DatabaseConnection.getConnection();
                DefaultTableModel model = (DefaultTableModel) jTable4.getModel();
                model.setRowCount(0); // kosongkan tabel dulu

                String sql = "SELECT tanggal, nama_item AS nama, jumlah, keterangan, username " +
                             "FROM (" +
                             "  SELECT t.tanggal, i.nama_item, t.jumlah, t.keterangan, u.username " +
                             "  FROM transactions_in t " +
                             "  JOIN items i ON t.id_item = i.id_item " +
                             "  JOIN users u ON t.id_user = u.id_user " +
                             "  UNION ALL " +
                             "  SELECT t.tanggal, i.nama_item, -t.jumlah, t.keterangan, u.username " +
                             "  FROM transactions_out t " +
                             "  JOIN items i ON t.id_item = i.id_item " +
                             "  JOIN users u ON t.id_user = u.id_user " +
                             ") AS history " +
                             "ORDER BY tanggal DESC";

                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getDate("tanggal"), // tanggal saja tanpa jam
                        rs.getString("nama"),
                        rs.getInt("jumlah"),
                        rs.getString("keterangan"),
                        rs.getString("username")
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    
    private void loadDataBarang() {
        try {
            Connection conn = code.DatabaseConnection.getConnection();
            String sql = "SELECT i.id_item as id, i.nama_item AS nama, c.nama_kategori as kategori, i.stok, i.harga " +
                         "FROM items i " +
                         "JOIN categories c ON i.id_kategori = c.id_kategori "
                         ;

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
            model.setRowCount(0);

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nama"),
                    rs.getString("kategori"),
                    rs.getInt("stok"),
                    rs.getInt("harga")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public int getTotalProduk() {
        int total = 0;
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT COUNT(*) AS total FROM items";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                total = rs.getInt("total");
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return total;
    }
    
    public int getStokMasukHariIni() {
        int totalMasuk = 0;
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT SUM(jumlah) AS total_masuk FROM transactions_in WHERE DATE(tanggal) = CURDATE()";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                totalMasuk = rs.getInt("total_masuk");
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return totalMasuk;
    }

    public int getStokKeluarHariIni() {
        int totalKeluar = 0;
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT SUM(jumlah) AS total_keluar FROM transactions_out WHERE DATE(tanggal) = CURDATE()";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                totalKeluar = rs.getInt("total_keluar");
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return totalKeluar;
    }
    
    private void updateDashboard() {
        lblTotalProduk.setText(String.valueOf(getTotalProduk()));
        lblStokMasuk.setText(String.valueOf(getStokMasukHariIni()));
        lblStokKeluar.setText(String.valueOf(getStokKeluarHariIni()));
    }
    
    private void loadAktivitasTerakhir() {
        try {
            Connection conn = code.DatabaseConnection.getConnection();
            String sql = "SELECT t.tanggal, i.nama_item AS nama, t.jumlah, t.keterangan, u.username " +
                         "FROM transactions_in t " +
                         "JOIN items i ON t.id_item = i.id_item " +
                         "JOIN users u ON t.id_user = u.id_user " + 
                         "WHERE DATE(t.tanggal) = CURDATE() " +
                         "UNION ALL " +
                         "SELECT t.tanggal, i.nama_item AS nama, -t.jumlah AS jumlah, t.keterangan, u.username " +
                         "FROM transactions_out t " +
                         "JOIN items i ON t.id_item = i.id_item " +
                         "JOIN users u ON t.id_user = u.id_user " +
                         "WHERE DATE(t.tanggal) = CURDATE() " +
                         "ORDER BY tanggal DESC";

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0);

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getTimestamp("tanggal"),
                    rs.getString("nama"),
                    rs.getInt("jumlah"),
                    rs.getString("keterangan"),
                    rs.getString("username")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadUsersCombo() {
        try {
            Connection conn = code.DatabaseConnection.getConnection();
            UserDAO dao = new UserDAO(conn);
            cbUser.removeAllItems();
            for (UserItem u : dao.getAllUsers()) {
                cbUser.addItem(u);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            // ubah byte array jadi hex string
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
    public void setTabel(){
        jTable1.setBackground(new Color(37, 27, 27, 200)); 
        jTable1.setForeground(Color.WHITE); 
        jTable1.getTableHeader().setBackground(new Color(51, 0, 51)); 
        jTable1.setGridColor(new Color(100, 100, 100)); 
        jTable1.setSelectionBackground(new Color(100, 16, 117)); 
        jTable1.setSelectionForeground(Color.WHITE);
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jRadioButton1 = new javax.swing.JRadioButton();
        mainPanel = new javax.swing.JPanel();
        panelDashboard = new GradientPanel(new Color(37, 27, 27, 255), new Color(100, 16, 117, 255));
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        lblTotalProduk = new javax.swing.JLabel();
        lblStokMasuk = new javax.swing.JLabel();
        lblStokKeluar = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        panelDataBarang = new javax.swing.JPanel();
        subPanelDataBarang = new javax.swing.JPanel();
        panelLihat = new GradientPanel(new Color(37, 27, 27, 255), new Color(100, 16, 117, 255));
        jLabel3 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        btnRefresh = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnTambah1 = new javax.swing.JButton();
        btnKategori = new javax.swing.JButton();
        btnLihat = new javax.swing.JButton();
        panelTambah = new GradientPanel(new Color(37, 27, 27, 255), new Color(100, 16, 117, 255));
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txtNama = new javax.swing.JTextField();
        txtStok = new javax.swing.JTextField();
        txtHarga = new javax.swing.JTextField();
        txtSatuan = new javax.swing.JTextField();
        cbKategori1 = new javax.swing.JComboBox<>();
        btnSimpan = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();
        btnLihat10 = new javax.swing.JButton();
        btnTambah20 = new javax.swing.JButton();
        btnKategori10 = new javax.swing.JButton();
        panelKategori = new GradientPanel(new Color(37, 27, 27, 255), new Color(100, 16, 117, 255));
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        txtNamaKategori = new javax.swing.JTextField();
        btnTambah = new javax.swing.JButton();
        btnEdit1 = new javax.swing.JButton();
        btnHapus1 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        btnLihat20 = new javax.swing.JButton();
        btnTambah30 = new javax.swing.JButton();
        btnKategori20 = new javax.swing.JButton();
        panelStok = new javax.swing.JPanel();
        subPanelStok = new javax.swing.JPanel();
        panelStokMasuk = new GradientPanel(new Color(37, 27, 27, 255), new Color(100, 16, 117, 255));
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        cbBarangMasuk = new javax.swing.JComboBox<>();
        jLabel22 = new javax.swing.JLabel();
        txtJumlahMasuk = new javax.swing.JTextField();
        btnSimpan1 = new javax.swing.JButton();
        btnStokMasuk2 = new javax.swing.JButton();
        btnStokKeluar2 = new javax.swing.JButton();
        btnRiwayatStok2 = new javax.swing.JButton();
        panelStokKeluar = new GradientPanel(new Color(37, 27, 27, 255), new Color(100, 16, 117, 255));
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        cbBarangMasuk1 = new javax.swing.JComboBox<>();
        jLabel25 = new javax.swing.JLabel();
        txtJumlahMasuk1 = new javax.swing.JTextField();
        btnSimpan2 = new javax.swing.JButton();
        btnStokMasuk1 = new javax.swing.JButton();
        btnStokKeluar1 = new javax.swing.JButton();
        btnRiwayatStok1 = new javax.swing.JButton();
        panelRiwayatStok = new GradientPanel(new Color(37, 27, 27, 255), new Color(100, 16, 117, 255));
        jLabel26 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable4 = new javax.swing.JTable();
        btnRiwayatStok = new javax.swing.JButton();
        btnStokKeluar = new javax.swing.JButton();
        btnStokMasuk = new javax.swing.JButton();
        panelUserManagement = new GradientPanel(new Color(37, 27, 27, 255), new Color(100, 16, 117, 255));
        jPanel1 = new GradientPanel(new Color(37, 27, 27, 255), new Color(100, 16, 117, 255));
        jLabel9 = new javax.swing.JLabel();
        cbUser = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtPassword = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        cbRole = new javax.swing.JComboBox<>();
        btnAdd = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jMenu4 = new javax.swing.JMenu();

        jRadioButton1.setText("jRadioButton1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        mainPanel.setLayout(new java.awt.CardLayout());

        panelDashboard.setBackground(new java.awt.Color(51, 51, 51));
        panelDashboard.setForeground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("DASHBOARD ADMIN");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Total Produk : ");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Total Stok Masuk : ");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Total Stok Keluar : ");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Aktivitas Terakhir ");

        jTable1.setBackground(new Color(37, 27, 27, 100));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Tanggal", "Nama", "Jumlah", "Keterangan", "User"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        lblTotalProduk.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTotalProduk.setForeground(new java.awt.Color(255, 255, 255));
        lblTotalProduk.setText("0");

        lblStokMasuk.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblStokMasuk.setForeground(new java.awt.Color(255, 255, 255));
        lblStokMasuk.setText("0");

        lblStokKeluar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblStokKeluar.setForeground(new java.awt.Color(255, 255, 255));
        lblStokKeluar.setText("0");

        jButton1.setBackground(new java.awt.Color(204, 0, 0));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton1.setText("Refresh");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelDashboardLayout = new javax.swing.GroupLayout(panelDashboard);
        panelDashboard.setLayout(panelDashboardLayout);
        panelDashboardLayout.setHorizontalGroup(
            panelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelDashboardLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(110, 110, 110))
            .addGroup(panelDashboardLayout.createSequentialGroup()
                .addGap(61, 61, 61)
                .addGroup(panelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1)
                    .addGroup(panelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(panelDashboardLayout.createSequentialGroup()
                            .addGroup(panelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE)
                                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(panelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(lblStokMasuk, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                                .addComponent(lblStokKeluar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(panelDashboardLayout.createSequentialGroup()
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(30, 30, 30)
                            .addComponent(lblTotalProduk, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 461, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(44, Short.MAX_VALUE))
        );
        panelDashboardLayout.setVerticalGroup(
            panelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDashboardLayout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(lblTotalProduk))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(lblStokMasuk))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(lblStokKeluar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mainPanel.add(panelDashboard, "card2");

        subPanelDataBarang.setLayout(new java.awt.CardLayout());

        panelLihat.setBackground(new java.awt.Color(51, 51, 51));

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Search Barang : ");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("LIHAT BARANG");

        txtSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchActionPerformed(evt);
            }
        });

        btnSearch.setBackground(new java.awt.Color(51, 102, 255));
        btnSearch.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnSearch.setText("Search");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Id", "Nama", "Kategori", "Stok", "Harga"
            }
        ));
        jTable2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable2MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTable2);

        btnRefresh.setBackground(new java.awt.Color(255, 153, 0));
        btnRefresh.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnRefresh.setText("Refresh");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        btnHapus.setBackground(new java.awt.Color(255, 0, 0));
        btnHapus.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnHapus.setText("Hapus");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });

        btnTambah1.setBackground(new java.awt.Color(51, 0, 51));
        btnTambah1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnTambah1.setForeground(new java.awt.Color(255, 255, 255));
        btnTambah1.setText("Tambah Barang");
        btnTambah1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnTambah1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnTambah1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambah1ActionPerformed(evt);
            }
        });

        btnKategori.setBackground(new java.awt.Color(51, 0, 51));
        btnKategori.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnKategori.setForeground(new java.awt.Color(255, 255, 255));
        btnKategori.setText("Kategori Barang");
        btnKategori.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnKategori.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnKategori.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKategoriActionPerformed(evt);
            }
        });

        btnLihat.setBackground(new java.awt.Color(51, 0, 51));
        btnLihat.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLihat.setForeground(new java.awt.Color(255, 255, 255));
        btnLihat.setText("Lihat Data Barang");
        btnLihat.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnLihat.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnLihat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLihatActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelLihatLayout = new javax.swing.GroupLayout(panelLihat);
        panelLihat.setLayout(panelLihatLayout);
        panelLihatLayout.setHorizontalGroup(
            panelLihatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLihatLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelLihatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLihatLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(panelLihatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLihatLayout.createSequentialGroup()
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(158, 158, 158))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLihatLayout.createSequentialGroup()
                                .addGroup(panelLihatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panelLihatLayout.createSequentialGroup()
                                        .addComponent(btnRefresh)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnHapus))
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 530, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(panelLihatLayout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnSearch)))
                                .addGap(30, 30, 30))))
                    .addGroup(panelLihatLayout.createSequentialGroup()
                        .addComponent(btnLihat)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnTambah1)
                        .addGap(16, 16, 16)
                        .addComponent(btnKategori)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        panelLihatLayout.setVerticalGroup(
            panelLihatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLihatLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelLihatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnLihat)
                    .addComponent(btnTambah1)
                    .addComponent(btnKategori))
                .addGap(45, 45, 45)
                .addComponent(jLabel8)
                .addGap(18, 18, 18)
                .addGroup(panelLihatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearch))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelLihatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRefresh)
                    .addComponent(btnHapus))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        subPanelDataBarang.add(panelLihat, "card6");

        panelTambah.setBackground(new java.awt.Color(51, 51, 51));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("TAMBAH BARANG");

        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Nama Barang : ");

        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Kategori : ");

        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("Stok Awal : ");

        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("Harga  : ");

        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("Satuan : ");

        btnSimpan.setBackground(new java.awt.Color(0, 102, 255));
        btnSimpan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnSimpan.setText("Simpan");
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });

        btnReset.setBackground(new java.awt.Color(255, 51, 51));
        btnReset.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnReset.setText("Reset");

        btnLihat10.setBackground(new java.awt.Color(51, 0, 51));
        btnLihat10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLihat10.setForeground(new java.awt.Color(255, 255, 255));
        btnLihat10.setText("Lihat Data Barang");
        btnLihat10.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnLihat10.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnLihat10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLihat10ActionPerformed(evt);
            }
        });

        btnTambah20.setBackground(new java.awt.Color(51, 0, 51));
        btnTambah20.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnTambah20.setForeground(new java.awt.Color(255, 255, 255));
        btnTambah20.setText("Tambah Barang");
        btnTambah20.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnTambah20.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnTambah20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambah20ActionPerformed(evt);
            }
        });

        btnKategori10.setBackground(new java.awt.Color(51, 0, 51));
        btnKategori10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnKategori10.setForeground(new java.awt.Color(255, 255, 255));
        btnKategori10.setText("Kategori Barang");
        btnKategori10.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnKategori10.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnKategori10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKategori10ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelTambahLayout = new javax.swing.GroupLayout(panelTambah);
        panelTambah.setLayout(panelTambahLayout);
        panelTambahLayout.setHorizontalGroup(
            panelTambahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTambahLayout.createSequentialGroup()
                .addGroup(panelTambahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelTambahLayout.createSequentialGroup()
                        .addGap(134, 134, 134)
                        .addGroup(panelTambahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelTambahLayout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(panelTambahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel14)
                                    .addComponent(jLabel15)
                                    .addGroup(panelTambahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.LEADING)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelTambahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtNama)
                                    .addComponent(txtStok)
                                    .addComponent(txtHarga)
                                    .addComponent(txtSatuan, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cbKategori1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(panelTambahLayout.createSequentialGroup()
                                .addGap(66, 66, 66)
                                .addComponent(btnSimpan)
                                .addGap(18, 18, 18)
                                .addComponent(btnReset))
                            .addComponent(jLabel12)))
                    .addGroup(panelTambahLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnLihat10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnTambah20)
                        .addGap(16, 16, 16)
                        .addComponent(btnKategori10)))
                .addContainerGap(114, Short.MAX_VALUE))
        );
        panelTambahLayout.setVerticalGroup(
            panelTambahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTambahLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelTambahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnLihat10)
                    .addComponent(btnTambah20)
                    .addComponent(btnKategori10))
                .addGap(45, 45, 45)
                .addComponent(jLabel12)
                .addGap(18, 18, 18)
                .addGroup(panelTambahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(txtNama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(panelTambahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(cbKategori1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addGroup(panelTambahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(txtStok, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(panelTambahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(txtHarga, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(panelTambahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(txtSatuan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(panelTambahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSimpan)
                    .addComponent(btnReset))
                .addContainerGap(31, Short.MAX_VALUE))
        );

        subPanelDataBarang.add(panelTambah, "card7");

        panelKategori.setBackground(new java.awt.Color(51, 51, 51));

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText("KATEGORI BARANG");

        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setText("Nama Kategori : ");

        btnTambah.setBackground(new java.awt.Color(51, 102, 255));
        btnTambah.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnTambah.setText("Tambah");
        btnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahActionPerformed(evt);
            }
        });

        btnEdit1.setBackground(new java.awt.Color(255, 153, 51));
        btnEdit1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnEdit1.setText("Edit");
        btnEdit1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEdit1ActionPerformed(evt);
            }
        });

        btnHapus1.setBackground(new java.awt.Color(255, 51, 0));
        btnHapus1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnHapus1.setText("Hapus");
        btnHapus1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapus1ActionPerformed(evt);
            }
        });

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Id", "Kategori"
            }
        ));
        jTable3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable3MouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jTable3);

        btnLihat20.setBackground(new java.awt.Color(51, 0, 51));
        btnLihat20.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLihat20.setForeground(new java.awt.Color(255, 255, 255));
        btnLihat20.setText("Lihat Data Barang");
        btnLihat20.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnLihat20.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnLihat20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLihat20ActionPerformed(evt);
            }
        });

        btnTambah30.setBackground(new java.awt.Color(51, 0, 51));
        btnTambah30.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnTambah30.setForeground(new java.awt.Color(255, 255, 255));
        btnTambah30.setText("Tambah Barang");
        btnTambah30.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnTambah30.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnTambah30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambah30ActionPerformed(evt);
            }
        });

        btnKategori20.setBackground(new java.awt.Color(51, 0, 51));
        btnKategori20.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnKategori20.setForeground(new java.awt.Color(255, 255, 255));
        btnKategori20.setText("Kategori Barang");
        btnKategori20.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnKategori20.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnKategori20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKategori20ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelKategoriLayout = new javax.swing.GroupLayout(panelKategori);
        panelKategori.setLayout(panelKategoriLayout);
        panelKategoriLayout.setHorizontalGroup(
            panelKategoriLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelKategoriLayout.createSequentialGroup()
                .addGroup(panelKategoriLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelKategoriLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnLihat20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnTambah30)
                        .addGap(16, 16, 16)
                        .addComponent(btnKategori20))
                    .addGroup(panelKategoriLayout.createSequentialGroup()
                        .addGap(103, 103, 103)
                        .addGroup(panelKategoriLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelKategoriLayout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addComponent(jLabel18))
                            .addGroup(panelKategoriLayout.createSequentialGroup()
                                .addComponent(btnEdit1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnHapus1))
                            .addGroup(panelKategoriLayout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtNamaKategori, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnTambah))
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(82, Short.MAX_VALUE))
        );
        panelKategoriLayout.setVerticalGroup(
            panelKategoriLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelKategoriLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelKategoriLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnLihat20)
                    .addComponent(btnTambah30)
                    .addComponent(btnKategori20))
                .addGap(44, 44, 44)
                .addComponent(jLabel18)
                .addGap(18, 18, 18)
                .addGroup(panelKategoriLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(txtNamaKategori, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTambah))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelKategoriLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnEdit1)
                    .addComponent(btnHapus1))
                .addContainerGap(29, Short.MAX_VALUE))
        );

        subPanelDataBarang.add(panelKategori, "card8");

        javax.swing.GroupLayout panelDataBarangLayout = new javax.swing.GroupLayout(panelDataBarang);
        panelDataBarang.setLayout(panelDataBarangLayout);
        panelDataBarangLayout.setHorizontalGroup(
            panelDataBarangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(subPanelDataBarang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelDataBarangLayout.setVerticalGroup(
            panelDataBarangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(subPanelDataBarang, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        mainPanel.add(panelDataBarang, "card3");

        subPanelStok.setLayout(new java.awt.CardLayout());

        panelStokMasuk.setBackground(new java.awt.Color(51, 51, 51));

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("TAMBAH STOK");

        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setText("Nama Barang : ");

        cbBarangMasuk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbBarangMasukActionPerformed(evt);
            }
        });

        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setText("Jumlah Barang : ");

        btnSimpan1.setBackground(new java.awt.Color(51, 102, 255));
        btnSimpan1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnSimpan1.setText("Simpan");
        btnSimpan1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpan1ActionPerformed(evt);
            }
        });

        btnStokMasuk2.setBackground(new java.awt.Color(51, 0, 51));
        btnStokMasuk2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnStokMasuk2.setForeground(new java.awt.Color(255, 255, 255));
        btnStokMasuk2.setText("Stok Masuk");
        btnStokMasuk2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnStokMasuk2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        btnStokKeluar2.setBackground(new java.awt.Color(51, 0, 51));
        btnStokKeluar2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnStokKeluar2.setForeground(new java.awt.Color(255, 255, 255));
        btnStokKeluar2.setText("Stok Keluar");
        btnStokKeluar2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnStokKeluar2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        btnRiwayatStok2.setBackground(new java.awt.Color(51, 0, 51));
        btnRiwayatStok2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnRiwayatStok2.setForeground(new java.awt.Color(255, 255, 255));
        btnRiwayatStok2.setText("Riwayat Stok");
        btnRiwayatStok2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRiwayatStok2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRiwayatStok2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRiwayatStok2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelStokMasukLayout = new javax.swing.GroupLayout(panelStokMasuk);
        panelStokMasuk.setLayout(panelStokMasukLayout);
        panelStokMasukLayout.setHorizontalGroup(
            panelStokMasukLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStokMasukLayout.createSequentialGroup()
                .addGroup(panelStokMasukLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelStokMasukLayout.createSequentialGroup()
                        .addGap(135, 135, 135)
                        .addGroup(panelStokMasukLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelStokMasukLayout.createSequentialGroup()
                                .addGroup(panelStokMasukLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel21)
                                    .addComponent(jLabel22))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelStokMasukLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(cbBarangMasuk, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtJumlahMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(panelStokMasukLayout.createSequentialGroup()
                                .addGap(101, 101, 101)
                                .addComponent(btnSimpan1))
                            .addGroup(panelStokMasukLayout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(jLabel20))))
                    .addGroup(panelStokMasukLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnStokMasuk2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnStokKeluar2)
                        .addGap(14, 14, 14)
                        .addComponent(btnRiwayatStok2)))
                .addContainerGap(146, Short.MAX_VALUE))
        );
        panelStokMasukLayout.setVerticalGroup(
            panelStokMasukLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStokMasukLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelStokMasukLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnStokMasuk2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnStokKeluar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnRiwayatStok2))
                .addGap(65, 65, 65)
                .addComponent(jLabel20)
                .addGap(30, 30, 30)
                .addGroup(panelStokMasukLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(cbBarangMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelStokMasukLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(txtJumlahMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addComponent(btnSimpan1)
                .addContainerGap(116, Short.MAX_VALUE))
        );

        subPanelStok.add(panelStokMasuk, "card9");

        panelStokKeluar.setBackground(new java.awt.Color(51, 51, 51));

        jLabel23.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setText("HAPUS STOK");

        jLabel24.setForeground(new java.awt.Color(255, 255, 255));
        jLabel24.setText("Nama Barang : ");

        jLabel25.setForeground(new java.awt.Color(255, 255, 255));
        jLabel25.setText("Jumlah Barang : ");

        btnSimpan2.setBackground(new java.awt.Color(51, 102, 255));
        btnSimpan2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnSimpan2.setText("Simpan");
        btnSimpan2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpan2ActionPerformed(evt);
            }
        });

        btnStokMasuk1.setBackground(new java.awt.Color(51, 0, 51));
        btnStokMasuk1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnStokMasuk1.setForeground(new java.awt.Color(255, 255, 255));
        btnStokMasuk1.setText("Stok Masuk");
        btnStokMasuk1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnStokMasuk1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        btnStokKeluar1.setBackground(new java.awt.Color(51, 0, 51));
        btnStokKeluar1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnStokKeluar1.setForeground(new java.awt.Color(255, 255, 255));
        btnStokKeluar1.setText("Stok Keluar");
        btnStokKeluar1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnStokKeluar1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        btnRiwayatStok1.setBackground(new java.awt.Color(51, 0, 51));
        btnRiwayatStok1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnRiwayatStok1.setForeground(new java.awt.Color(255, 255, 255));
        btnRiwayatStok1.setText("Riwayat Stok");
        btnRiwayatStok1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRiwayatStok1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRiwayatStok1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRiwayatStok1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelStokKeluarLayout = new javax.swing.GroupLayout(panelStokKeluar);
        panelStokKeluar.setLayout(panelStokKeluarLayout);
        panelStokKeluarLayout.setHorizontalGroup(
            panelStokKeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStokKeluarLayout.createSequentialGroup()
                .addGroup(panelStokKeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelStokKeluarLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnStokMasuk1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnStokKeluar1)
                        .addGap(14, 14, 14)
                        .addComponent(btnRiwayatStok1))
                    .addGroup(panelStokKeluarLayout.createSequentialGroup()
                        .addGap(138, 138, 138)
                        .addGroup(panelStokKeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelStokKeluarLayout.createSequentialGroup()
                                .addGroup(panelStokKeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel24)
                                    .addComponent(jLabel25))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelStokKeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(cbBarangMasuk1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtJumlahMasuk1, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(panelStokKeluarLayout.createSequentialGroup()
                                .addGap(101, 101, 101)
                                .addComponent(btnSimpan2))
                            .addGroup(panelStokKeluarLayout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addComponent(jLabel23)))))
                .addContainerGap(143, Short.MAX_VALUE))
        );
        panelStokKeluarLayout.setVerticalGroup(
            panelStokKeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStokKeluarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelStokKeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnStokMasuk1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnStokKeluar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnRiwayatStok1))
                .addGap(67, 67, 67)
                .addComponent(jLabel23)
                .addGap(30, 30, 30)
                .addGroup(panelStokKeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(cbBarangMasuk1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelStokKeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(txtJumlahMasuk1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addComponent(btnSimpan2)
                .addContainerGap(114, Short.MAX_VALUE))
        );

        subPanelStok.add(panelStokKeluar, "card10");

        panelRiwayatStok.setBackground(new java.awt.Color(51, 51, 51));

        jLabel26.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(255, 255, 255));
        jLabel26.setText("RIWAYAT STOK");

        jTable4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Tanggal", "Nama", "Jumlah", "Tipe", "User"
            }
        ));
        jScrollPane4.setViewportView(jTable4);
        if (jTable4.getColumnModel().getColumnCount() > 0) {
            jTable4.getColumnModel().getColumn(0).setHeaderValue("Tanggal");
            jTable4.getColumnModel().getColumn(1).setHeaderValue("Nama");
            jTable4.getColumnModel().getColumn(2).setHeaderValue("Jumlah");
            jTable4.getColumnModel().getColumn(3).setHeaderValue("Tipe");
            jTable4.getColumnModel().getColumn(4).setHeaderValue("User");
        }

        btnRiwayatStok.setBackground(new java.awt.Color(51, 0, 51));
        btnRiwayatStok.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnRiwayatStok.setForeground(new java.awt.Color(255, 255, 255));
        btnRiwayatStok.setText("Riwayat Stok");
        btnRiwayatStok.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRiwayatStok.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRiwayatStok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRiwayatStokActionPerformed(evt);
            }
        });

        btnStokKeluar.setBackground(new java.awt.Color(51, 0, 51));
        btnStokKeluar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnStokKeluar.setForeground(new java.awt.Color(255, 255, 255));
        btnStokKeluar.setText("Stok Keluar");
        btnStokKeluar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnStokKeluar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        btnStokMasuk.setBackground(new java.awt.Color(51, 0, 51));
        btnStokMasuk.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnStokMasuk.setForeground(new java.awt.Color(255, 255, 255));
        btnStokMasuk.setText("Stok Masuk");
        btnStokMasuk.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnStokMasuk.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        javax.swing.GroupLayout panelRiwayatStokLayout = new javax.swing.GroupLayout(panelRiwayatStok);
        panelRiwayatStok.setLayout(panelRiwayatStokLayout);
        panelRiwayatStokLayout.setHorizontalGroup(
            panelRiwayatStokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRiwayatStokLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel26)
                .addGap(161, 161, 161))
            .addGroup(panelRiwayatStokLayout.createSequentialGroup()
                .addGroup(panelRiwayatStokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelRiwayatStokLayout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 508, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelRiwayatStokLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnStokMasuk)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnStokKeluar)
                        .addGap(14, 14, 14)
                        .addComponent(btnRiwayatStok)))
                .addContainerGap(35, Short.MAX_VALUE))
        );
        panelRiwayatStokLayout.setVerticalGroup(
            panelRiwayatStokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRiwayatStokLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelRiwayatStokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnStokMasuk, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnStokKeluar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnRiwayatStok))
                .addGap(10, 10, 10)
                .addComponent(jLabel26)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(36, Short.MAX_VALUE))
        );

        subPanelStok.add(panelRiwayatStok, "card11");

        javax.swing.GroupLayout panelStokLayout = new javax.swing.GroupLayout(panelStok);
        panelStok.setLayout(panelStokLayout);
        panelStokLayout.setHorizontalGroup(
            panelStokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(subPanelStok, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelStokLayout.setVerticalGroup(
            panelStokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStokLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(subPanelStok, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mainPanel.add(panelStok, "card4");

        panelUserManagement.setBackground(new java.awt.Color(51, 51, 51));

        jPanel1.setBackground(new java.awt.Color(102, 102, 102));
        jPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 2, true));

        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("User");

        cbUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbUserActionPerformed(evt);
            }
        });

        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Username : ");

        txtUsername.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUsernameActionPerformed(evt);
            }
        });

        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Password : ");

        jLabel27.setForeground(new java.awt.Color(255, 255, 255));
        jLabel27.setText("Role : ");

        cbRole.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "admin", "user" }));
        cbRole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbRoleActionPerformed(evt);
            }
        });

        btnAdd.setBackground(new java.awt.Color(51, 255, 255));
        btnAdd.setText("Add");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(255, 153, 0));
        jButton3.setText("Edit");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(204, 0, 0));
        jButton4.setText("Delete");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("USER MANAGEMENT");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel9)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbRole, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(151, 151, 151))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 369, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(btnAdd)
                        .addGap(47, 47, 47)
                        .addComponent(jButton3)
                        .addGap(50, 50, 50)
                        .addComponent(jButton4)))
                .addContainerGap(35, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(cbUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(cbRole, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAdd)
                    .addComponent(jButton3)
                    .addComponent(jButton4))
                .addContainerGap(28, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelUserManagementLayout = new javax.swing.GroupLayout(panelUserManagement);
        panelUserManagement.setLayout(panelUserManagementLayout);
        panelUserManagementLayout.setHorizontalGroup(
            panelUserManagementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelUserManagementLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(55, Short.MAX_VALUE))
        );
        panelUserManagementLayout.setVerticalGroup(
            panelUserManagementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelUserManagementLayout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(42, Short.MAX_VALUE))
        );

        mainPanel.add(panelUserManagement, "card5");

        jMenuBar1.setBackground(new java.awt.Color(51, 0, 51));
        jMenuBar1.setForeground(new java.awt.Color(255, 255, 255));

        jMenu1.setText("Dashboard");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Data Barang");
        jMenuBar1.add(jMenu2);

        jMenu3.setText("Stok");
        jMenuBar1.add(jMenu3);

        jMenu4.setText("User Management");
        jMenuBar1.add(jMenu4);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        loadAktivitasTerakhir();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        UserItem selected = (UserItem) cbUser.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Pilih user terlebih dahulu!");
            return;
        }

        String newUsername = txtUsername.getText().trim();
        String newPassword = txtPassword.getText().trim();
        String newRole = cbRole.getSelectedItem().toString();

        // Jika password tidak diisi, tetap gunakan password lama
        String passwordToSave = newPassword.isEmpty() ? selected.getPassword() : md5(newPassword);

        try {
            Connection conn = code.DatabaseConnection.getConnection();
            UserDAO dao = new UserDAO(conn);

            // Validasi username unik kecuali user yang sedang diedit
            if (!newUsername.equals(selected.getUsername()) && dao.isUsernameExists(newUsername)) {
                JOptionPane.showMessageDialog(this, "Username sudah ada!");
                return;
            }

            UserItem updatedUser = new UserItem(selected.getIdUser(), newUsername, passwordToSave, newRole);

            if (dao.updateUser(updatedUser)) {
                JOptionPane.showMessageDialog(this, "User berhasil diupdate!");
                loadUsersCombo(); // refresh combobox
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal update user!");
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void cbRoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbRoleActionPerformed
    
    }//GEN-LAST:event_cbRoleActionPerformed

    private void cbUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbUserActionPerformed
//        if (!isAdding) {
//            UserItem selected = (UserItem) cbUser.getSelectedItem();
//            if (selected != null) {
//                txtUsername.setText(selected.getUsername());
//                txtPassword.setText(""); // password tidak ditampilkan
//                cbRole.setSelectedItem(selected.getRole());
//            }
//        }
    }//GEN-LAST:event_cbUserActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        String username = txtUsername.getText().trim();
        String password = md5(txtPassword.getText().trim());
        String role = cbRole.getSelectedItem().toString();

        UserItem u = new UserItem(0, username, password, role);
        try {
            Connection conn = code.DatabaseConnection.getConnection();
            UserDAO dao = new UserDAO(conn);
            if (dao.addUser(u)) {
                JOptionPane.showMessageDialog(this, "User berhasil ditambahkan");
                loadUsersCombo();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }//GEN-LAST:event_btnAddActionPerformed

    private void txtUsernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsernameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUsernameActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        UserItem selected = (UserItem) cbUser.getSelectedItem();
        if (selected != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Apakah Anda yakin ingin menghapus user " + selected.getUsername() + "?",
                    "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Connection conn = code.DatabaseConnection.getConnection();
                    UserDAO dao = new UserDAO(conn);
                    if (dao.deleteUser(selected.getIdUser())) {
                        JOptionPane.showMessageDialog(this, "User berhasil dihapus");
                        loadUsersCombo();
                        txtUsername.setText("");
                        txtPassword.setText("");
                        cbRole.setSelectedIndex(0);
                    } else {
                        JOptionPane.showMessageDialog(this, "Gagal menghapus user");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Tidak ada user yang dipilih");
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void btnKategori20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKategori20ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnKategori20ActionPerformed

    private void btnTambah30ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambah30ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambah30ActionPerformed

    private void btnLihat20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLihat20ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnLihat20ActionPerformed

    private void jTable3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable3MouseClicked
        int row = jTable3.getSelectedRow();
        if (row >= 0) {
            // Ambil ID dari kolom pertama
            selectedIdKategori = (int) jTable3.getValueAt(row, 0);
            // Tampilkan nama kategori di textfield
            txtNamaKategori.setText(jTable3.getValueAt(row, 1).toString());
        }
    }//GEN-LAST:event_jTable3MouseClicked

    private void btnHapus1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapus1ActionPerformed
        try {
            Connection conn = code.DatabaseConnection.getConnection();
            CategoryDAO dao = new CategoryDAO(conn);

            if(selectedIdKategori == -1) {
                JOptionPane.showMessageDialog(this, "Pilih kategori dulu!");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus kategori ini?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION);

            if(confirm == JOptionPane.YES_OPTION) {
                if(dao.delete(selectedIdKategori)) {
                    loadKategori();
                    loadKategoriTable();
                    JOptionPane.showMessageDialog(this, "Kategori berhasil dihapus");
                    selectedIdKategori = -1; // reset ID
                    txtNamaKategori.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus kategori");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnHapus1ActionPerformed

    private void btnEdit1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEdit1ActionPerformed
        try {
            Connection conn = code.DatabaseConnection.getConnection();
            CategoryDAO dao = new CategoryDAO(conn);

            if(selectedIdKategori == -1) {
                JOptionPane.showMessageDialog(this, "Pilih kategori dulu!");
                return;
            }

            String nama = txtNamaKategori.getText();
            if(nama.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nama kategori tidak boleh kosong!");
                return;
            }

            if(dao.update(selectedIdKategori, nama)) {
                loadKategori();
                loadKategoriTable();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengupdate kategori");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnEdit1ActionPerformed

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
        // TODO add your handling code here:
        try {
            String nama = txtNamaKategori.getText();

            Connection conn = code.DatabaseConnection.getConnection();
            CategoryDAO dao = new CategoryDAO(conn);

            dao.insert(nama);
            loadKategori();        // update ComboBox
            loadKategoriTable();   // update JTable
            JOptionPane.showMessageDialog(this, "Kategori ditambahkan");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnTambahActionPerformed

    private void btnKategori10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKategori10ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnKategori10ActionPerformed

    private void btnTambah20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambah20ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambah20ActionPerformed

    private void btnLihat10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLihat10ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnLihat10ActionPerformed

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        try {
            String nama = txtNama.getText();
            int harga = Integer.parseInt(txtHarga.getText());
            int stok = Integer.parseInt(txtStok.getText());
            String satuan = txtSatuan.getText();

            // kategori (boleh null kalau tidak dipilih)
            CategoryItem selected = (CategoryItem) cbKategori1.getSelectedItem();
            Integer idKategori = (selected.getId() == 0) ? null : selected.getId();

            // Buat object Item
            Item item = new Item();
            item.setNamaItem(nama);
            item.setHarga(harga);
            item.setStok(stok);
            item.setSatuan(satuan);
            item.setIdKategori(idKategori);

            // Panggil DAO
            Connection conn = code.DatabaseConnection.getConnection();
            ItemDAO dao = new ItemDAO(conn);

            if (dao.insertItem(item)) {
                JOptionPane.showMessageDialog(this, "Data berhasil disimpan!");
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan data!");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Input tidak valid: " + e.getMessage());
        }
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void btnKategoriActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKategoriActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnKategoriActionPerformed

    private void btnTambah1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambah1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambah1ActionPerformed

    private void btnLihatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLihatActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnLihatActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        try {
            Connection conn = code.DatabaseConnection.getConnection();
            CategoryDAO dao = new CategoryDAO(conn);

            if(selectedData == -1) {
                JOptionPane.showMessageDialog(this, "Pilih baris dulu!");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus kategori ini?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION);

            if(confirm == JOptionPane.YES_OPTION) {
                if(dao.deleteData(selectedData)) {
                    loadDataBarang();
                    JOptionPane.showMessageDialog(this, "Kategori berhasil dihapus");
                    selectedData = -1; // reset ID
                    txtNamaKategori.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus kategori");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        // TODO add your handling code here:
        loadDataBarang();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void jTable2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MouseClicked
        int row = jTable2.getSelectedRow();
        if (row >= 0) {
            // Ambil ID dari kolom pertama
            selectedData = (int) jTable2.getValueAt(row, 0);
        }
    }//GEN-LAST:event_jTable2MouseClicked

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        // TODO add your handling code here:
        try {
            String keyword = txtSearch.getText().trim();

            if (keyword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Masukkan nama barang untuk dicari!");
                return;
            }

            Connection conn = code.DatabaseConnection.getConnection();
            ItemDAO dao = new ItemDAO(conn);

            // Ambil hasil search dari DAO
            List<Object[]> results = dao.searchItems(keyword);

            // Model tabel
            DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
            model.setRowCount(0); // hapus isi lama

            // Tampilkan hasil ke tabel
            for (Object[] row : results) {
                model.addRow(row);
            }

            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Data tidak ditemukan!");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }//GEN-LAST:event_btnSearchActionPerformed

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchActionPerformed

    private void btnSimpan2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpan2ActionPerformed
        System.out.println("currentUserId: " + currentUserId);
        try {
            StokItem selected = (StokItem) cbBarangMasuk1.getSelectedItem();

            if (selected == null || selected.getIdItem() == 0) {
                JOptionPane.showMessageDialog(this, "Pilih item terlebih dahulu!");
                return;
            }

            String input = txtJumlahMasuk1.getText().trim();
            if (input.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Jumlah stok harus diisi!");
                return;
            }
            int hapus = Integer.parseInt(input);

            if (hapus <= 0) {
                JOptionPane.showMessageDialog(this, "Jumlah harus lebih dari 0!");
                return;
            }

            // Hitung stok baru
            int stokBaru = selected.getStok() - hapus;
            if (stokBaru < 0) {
                JOptionPane.showMessageDialog(this, "Stok tidak boleh negatif!");
                return;
            }

            // Update stok di database
            if (stokDao.updateStok(selected.getIdItem(), stokBaru)) {

                // Catat transaksi keluar
                TransactionOut tOut = new TransactionOut();
                tOut.setIdItem(selected.getIdItem());
                tOut.setJumlah(hapus);
                tOut.setKeterangan("Keluar");
                tOut.setTanggal(new java.sql.Timestamp(System.currentTimeMillis()));
                tOut.setIdUser(currentUserId); // pastikan ini sudah diset saat login

                TransactionsDAO tDao = new TransactionsDAO(code.DatabaseConnection.getConnection());
                tDao.insertTransactionOut(tOut);

                JOptionPane.showMessageDialog(this, "Stok berhasil dikurangi!");
                loadItemsCombo();
                loadHistory();

            } else {
                JOptionPane.showMessageDialog(this, "Gagal update stok!");
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Jumlah stok tidak valid!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnSimpan2ActionPerformed

    private void btnSimpan1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpan1ActionPerformed
        try {
            StokItem selected = (StokItem) cbBarangMasuk.getSelectedItem();

            if (selected == null || selected.getIdItem() == 0) {
                JOptionPane.showMessageDialog(this, "Pilih item terlebih dahulu!");
                return;
            }

            // Ambil jumlah stok yang ingin ditambahkan
            String input = txtJumlahMasuk.getText().trim();
            if (input.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Jumlah stok harus diisi!");
                return;
            }
            int tambahan = Integer.parseInt(input);

            // Hitung stok baru
            int stokBaru = selected.getStok() + tambahan;

            // Update stok di database
            if (stokDao.updateStok(selected.getIdItem(), stokBaru)) {

                // Catat transaksi masuk
                TransactionIn tIn = new TransactionIn();
                tIn.setIdItem(selected.getIdItem());
                tIn.setJumlah(tambahan);
                tIn.setKeterangan("Masuk");
                tIn.setTanggal(new java.sql.Timestamp(System.currentTimeMillis()));
                tIn.setIdUser(currentUserId); // ganti dengan id user yang sedang login

                TransactionsDAO tDao = new TransactionsDAO(code.DatabaseConnection.getConnection());
                tDao.insertTransactionIn(tIn);

                JOptionPane.showMessageDialog(this, "Stok berhasil ditambahkan!");
                loadItemsCombo();
                loadHistory();

            } else {
                JOptionPane.showMessageDialog(this, "Gagal update stok!");
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Jumlah stok tidak valid!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnSimpan1ActionPerformed

    private void cbBarangMasukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbBarangMasukActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbBarangMasukActionPerformed

    private void btnRiwayatStokActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRiwayatStokActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRiwayatStokActionPerformed

    private void btnRiwayatStok1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRiwayatStok1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRiwayatStok1ActionPerformed

    private void btnRiwayatStok2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRiwayatStok2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRiwayatStok2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new Admin().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnEdit1;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnHapus1;
    private javax.swing.JButton btnKategori;
    private javax.swing.JButton btnKategori10;
    private javax.swing.JButton btnKategori20;
    private javax.swing.JButton btnLihat;
    private javax.swing.JButton btnLihat10;
    private javax.swing.JButton btnLihat20;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnRiwayatStok;
    private javax.swing.JButton btnRiwayatStok1;
    private javax.swing.JButton btnRiwayatStok2;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JButton btnSimpan1;
    private javax.swing.JButton btnSimpan2;
    private javax.swing.JButton btnStokKeluar;
    private javax.swing.JButton btnStokKeluar1;
    private javax.swing.JButton btnStokKeluar2;
    private javax.swing.JButton btnStokMasuk;
    private javax.swing.JButton btnStokMasuk1;
    private javax.swing.JButton btnStokMasuk2;
    private javax.swing.JButton btnTambah;
    private javax.swing.JButton btnTambah1;
    private javax.swing.JButton btnTambah20;
    private javax.swing.JButton btnTambah30;
    private javax.swing.JComboBox<StokItem> cbBarangMasuk;
    private javax.swing.JComboBox<StokItem> cbBarangMasuk1;
    private javax.swing.JComboBox<code.CategoryItem> cbKategori1;
    private javax.swing.JComboBox<String> cbRole;
    private javax.swing.JComboBox<UserItem> cbUser;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTable jTable4;
    private javax.swing.JLabel lblStokKeluar;
    private javax.swing.JLabel lblStokMasuk;
    private javax.swing.JLabel lblTotalProduk;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel panelDashboard;
    private javax.swing.JPanel panelDataBarang;
    private javax.swing.JPanel panelKategori;
    private javax.swing.JPanel panelLihat;
    private javax.swing.JPanel panelRiwayatStok;
    private javax.swing.JPanel panelStok;
    private javax.swing.JPanel panelStokKeluar;
    private javax.swing.JPanel panelStokMasuk;
    private javax.swing.JPanel panelTambah;
    private javax.swing.JPanel panelUserManagement;
    private javax.swing.JPanel subPanelDataBarang;
    private javax.swing.JPanel subPanelStok;
    private javax.swing.JTextField txtHarga;
    private javax.swing.JTextField txtJumlahMasuk;
    private javax.swing.JTextField txtJumlahMasuk1;
    private javax.swing.JTextField txtNama;
    private javax.swing.JTextField txtNamaKategori;
    private javax.swing.JTextField txtPassword;
    private javax.swing.JTextField txtSatuan;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtStok;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}