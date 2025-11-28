/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package gui;
import java.awt.Color;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.table.DefaultTableModel;
import code.StokItem;
import code.StokDAO;
import code.DatabaseConnection;
import code.GradientPanel;
import java.awt.CardLayout;
import java.sql.Connection;
import javax.swing.JOptionPane;

/**
 *
 * @author DHAIFAN
 */
public class User extends javax.swing.JFrame {
    
    private StokDAO stokDao;
    private int currentUserId;
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(User.class.getName());
    
    public void setCurrentUserId(int idUser) {
        this.currentUserId = idUser;
    }
    
    /**
     * Creates new form Admin
     */
    public User() {
        initComponents();
        updateDashboard();

        // Atur mainPanel untuk menu utama
        mainPanel.setLayout(new CardLayout());
        mainPanel.add(panelDashboard, "card2");
        mainPanel.add(panelTransaksi, "card4");

        
        
        // Menu utama
        CardLayout cl = (CardLayout) mainPanel.getLayout();
        
        jMenu1.addMouseListener(new java.awt.event.MouseAdapter() {
    public void mouseClicked(java.awt.event.MouseEvent evt) {
        cl.show(mainPanel, "card2"); // panelDashboard
    }
    });
        jMenu3.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
        cl.show(mainPanel, "card4"); // Langsung ke panelStokMasuk
    }
        });

        
        

        try {
            stokDao = new StokDAO(DatabaseConnection.getConnection());
            loadItemsCombo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setTabel();
        updateDashboard();
        loadAktivitasTerakhir();
        
    }
    
    private void loadItemsCombo() {
        try {
            // koneksi & DAO
            Connection conn = code.DatabaseConnection.getConnection();
            StokDAO dao = new StokDAO(conn);

            // hapus item lama
            cbBarang.removeAllItems();

            // tambahkan placeholder
            cbBarang.addItem(new StokItem(0, "-- Pilih Item --", 0,0,""));

            // ambil semua item dari DB dan tambahkan ke ComboBox
            for (StokItem i : dao.getAllItems()) {
                cbBarang.addItem(i);
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
    
    public int getTransaksiHariIni() {
        int totalTransaksi = 0;
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT COUNT(*) AS total FROM transactions_out " + "WHERE id_user = ? AND DATE(tanggal) = CURDATE()";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, currentUserId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                totalTransaksi = rs.getInt("total");
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return totalTransaksi;
    }

    public int getTotalPenjualan() {
        int totalPenjualan = 0;
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT SUM(t.jumlah * i.harga) AS total_rupiah " + "FROM transactions_out t " + "JOIN items i ON t.id_item = i.id_item " + "WHERE t.id_user = ? AND DATE(t.tanggal) = CURDATE()";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, currentUserId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                totalPenjualan = rs.getInt("total_rupiah");
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return totalPenjualan;
    }
    
    private void updateDashboard() {
        lblTotalTransaksi.setText(String.valueOf(getTransaksiHariIni()));
        lblTotalPenjualan.setText("Rp " + String.format("%,d", getTotalPenjualan()));
    }
    
    private void loadAktivitasTerakhir() {
    try {
        Connection conn = DatabaseConnection.getConnection();
        
        // Hanya transaksi user ini hari ini
        String sql = "SELECT t.tanggal, i.nama_item, t.jumlah, (t.jumlah * i.harga) AS total " +
                     "FROM transactions_out t " +
                     "JOIN items i ON t.id_item = i.id_item " +
                     "WHERE t.id_user = ? AND DATE(t.tanggal) = CURDATE() " + 
                     "ORDER BY t.tanggal DESC";
        
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, currentUserId); 
        ResultSet rs = ps.executeQuery();
        
        DefaultTableModel model = (DefaultTableModel) tblAktivitas.getModel();
        model.setRowCount(0);
        
        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getTimestamp("tanggal"),
                rs.getString("nama_item"),
                rs.getInt("jumlah"),
                "Rp " + String.format("%,d", rs.getInt("total"))
            });
        }
        
    } catch (Exception e) {
        e.printStackTrace();
    }
    }

    private void calculateTotal() {
    try {
        StokItem selected = (StokItem) cbBarang.getSelectedItem();
        
        if(selected == null || selected.getIdItem() == 0) {
            return;
        }
        
        String input = txtJumlah.getText().trim();
        if(input.isEmpty()) {
            lblTotalHarga.setText("Rp 0");
            return;
        }
        
        int jumlah = Integer.parseInt(input);
        
        // Validasi jumlah tidak boleh negatif atau 0
        if(jumlah <= 0) {
            JOptionPane.showMessageDialog(this, "Jumlah harus lebih dari 0!");
            txtJumlah.setText("");
            lblTotalHarga.setText("Rp 0");
            return;
        }
        
        // Validasi stok mencukupi
        if(jumlah > selected.getStok()) {
            JOptionPane.showMessageDialog(this, 
                "Stok tidak mencukupi!\nStok tersedia: " + selected.getStok());
            txtJumlah.setText(String.valueOf(selected.getStok()));
            jumlah = selected.getStok();
        }
        
        // Hitung total
        int total = selected.getHarga() * jumlah;
        lblTotalHarga.setText("Rp " + String.format("%,d", total));
        
    } catch(NumberFormatException e) {
        lblTotalHarga.setText("Rp 0");
    }
}
    
    private void resetFormTransaksi() {
        cbBarang.setSelectedIndex(0);
        txtJumlah.setText("");
        lblHargaSatuan.setText("Rp 0");
        lblStokTersedia.setText("Stok: -");
        lblTotalHarga.setText("Rp 0");
    }
    public void setTabel(){
        tblAktivitas.setBackground(new Color(37, 27, 27, 200)); 
        tblAktivitas.setForeground(Color.WHITE); 
        tblAktivitas.getTableHeader().setBackground(new Color(51, 0, 51)); 
        tblAktivitas.setGridColor(new Color(100, 100, 100)); 
        tblAktivitas.setSelectionBackground(new Color(100, 16, 117)); 
        tblAktivitas.setSelectionForeground(Color.WHITE);
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        panelDashboard = new GradientPanel(new Color(37, 27, 27, 255), new Color(100, 16, 117, 255));
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblAktivitas = new javax.swing.JTable();
        lblTotalTransaksi = new javax.swing.JLabel();
        lblTotalPenjualan = new javax.swing.JLabel();
        refreshButton = new javax.swing.JButton();
        panelTransaksi = new javax.swing.JPanel();
        subPanelStok = new javax.swing.JPanel();
        panelStokMasuk = new GradientPanel(new Color(37, 27, 27, 255), new Color(100, 16, 117, 255));
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        cbBarang = new javax.swing.JComboBox<>();
        jLabel22 = new javax.swing.JLabel();
        txtJumlah = new javax.swing.JTextField();
        btnSimpan1 = new javax.swing.JButton();
        lblTotalHarga = new javax.swing.JLabel();
        lblHargaSatuan = new javax.swing.JLabel();
        lblStokTersedia = new javax.swing.JLabel();
        resetButton = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        mainPanel.setLayout(new java.awt.CardLayout());

        panelDashboard.setBackground(new java.awt.Color(51, 51, 51));
        panelDashboard.setForeground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("DASHBOARD USER");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Total Transaksi  (Hari ini) : ");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Total Penjualan (Hari ini) :");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Aktivitas Terakhir ");

        tblAktivitas.setBackground(new Color(37, 27, 27, 100));
        tblAktivitas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Tanggal", "Nama", "Jumlah", "Total"
            }
        ));
        jScrollPane1.setViewportView(tblAktivitas);

        lblTotalTransaksi.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTotalTransaksi.setForeground(new java.awt.Color(255, 255, 255));
        lblTotalTransaksi.setText("0");

        lblTotalPenjualan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTotalPenjualan.setForeground(new java.awt.Color(255, 255, 255));
        lblTotalPenjualan.setText("0");

        refreshButton.setBackground(new java.awt.Color(204, 0, 0));
        refreshButton.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        refreshButton.setText("Refresh");
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelDashboardLayout = new javax.swing.GroupLayout(panelDashboard);
        panelDashboard.setLayout(panelDashboardLayout);
        panelDashboardLayout.setHorizontalGroup(
            panelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDashboardLayout.createSequentialGroup()
                .addGroup(panelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelDashboardLayout.createSequentialGroup()
                        .addGap(61, 61, 61)
                        .addGroup(panelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(refreshButton)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 461, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelDashboardLayout.createSequentialGroup()
                                .addGroup(panelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(panelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addGap(18, 18, 18)
                                .addGroup(panelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(lblTotalTransaksi, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                                    .addComponent(lblTotalPenjualan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                    .addGroup(panelDashboardLayout.createSequentialGroup()
                        .addGap(145, 145, 145)
                        .addComponent(jLabel1)))
                .addContainerGap(136, Short.MAX_VALUE))
        );
        panelDashboardLayout.setVerticalGroup(
            panelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDashboardLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(panelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(lblTotalTransaksi, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(lblTotalPenjualan))
                .addGap(40, 40, 40)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(refreshButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mainPanel.add(panelDashboard, "card2");

        subPanelStok.setLayout(new java.awt.CardLayout());

        panelStokMasuk.setBackground(new java.awt.Color(51, 51, 51));

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("TRANSAKSI");

        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setText("Pilih Barang");

        cbBarang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbBarangActionPerformed(evt);
            }
        });

        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setText("Jumlah Barang : ");

        txtJumlah.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtJumlahKeyReleased(evt);
            }
        });

        btnSimpan1.setBackground(new java.awt.Color(51, 102, 255));
        btnSimpan1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnSimpan1.setText("Simpan");
        btnSimpan1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpan1ActionPerformed(evt);
            }
        });

        lblTotalHarga.setForeground(new java.awt.Color(255, 255, 255));

        lblHargaSatuan.setForeground(new java.awt.Color(255, 255, 255));

        lblStokTersedia.setForeground(new java.awt.Color(255, 255, 255));

        resetButton.setBackground(new java.awt.Color(51, 102, 255));
        resetButton.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        resetButton.setText("Reset");
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelStokMasukLayout = new javax.swing.GroupLayout(panelStokMasuk);
        panelStokMasuk.setLayout(panelStokMasukLayout);
        panelStokMasukLayout.setHorizontalGroup(
            panelStokMasukLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStokMasukLayout.createSequentialGroup()
                .addGroup(panelStokMasukLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelStokMasukLayout.createSequentialGroup()
                        .addGap(141, 141, 141)
                        .addGroup(panelStokMasukLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelStokMasukLayout.createSequentialGroup()
                                .addComponent(jLabel21)
                                .addGap(37, 37, 37)
                                .addComponent(cbBarang, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelStokMasukLayout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(lblTotalHarga, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelStokMasukLayout.createSequentialGroup()
                                .addComponent(jLabel22)
                                .addGap(18, 18, 18)
                                .addComponent(txtJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lblHargaSatuan, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblStokTersedia, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelStokMasukLayout.createSequentialGroup()
                                .addComponent(btnSimpan1)
                                .addGap(18, 18, 18)
                                .addComponent(resetButton))))
                    .addGroup(panelStokMasukLayout.createSequentialGroup()
                        .addGap(197, 197, 197)
                        .addComponent(jLabel20)))
                .addContainerGap(220, Short.MAX_VALUE))
        );
        panelStokMasukLayout.setVerticalGroup(
            panelStokMasukLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStokMasukLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel20)
                .addGap(20, 20, 20)
                .addGroup(panelStokMasukLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(cbBarang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(lblHargaSatuan, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addComponent(lblStokTersedia, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                .addGap(5, 5, 5)
                .addGroup(panelStokMasukLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(txtJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(lblTotalHarga, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(panelStokMasukLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSimpan1)
                    .addComponent(resetButton))
                .addContainerGap(82, Short.MAX_VALUE))
        );

        subPanelStok.add(panelStokMasuk, "card9");

        javax.swing.GroupLayout panelTransaksiLayout = new javax.swing.GroupLayout(panelTransaksi);
        panelTransaksi.setLayout(panelTransaksiLayout);
        panelTransaksiLayout.setHorizontalGroup(
            panelTransaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(subPanelStok, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelTransaksiLayout.setVerticalGroup(
            panelTransaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTransaksiLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(subPanelStok, javax.swing.GroupLayout.DEFAULT_SIZE, 391, Short.MAX_VALUE))
        );

        mainPanel.add(panelTransaksi, "card4");

        jMenuBar1.setBackground(new java.awt.Color(51, 0, 51));
        jMenuBar1.setForeground(new java.awt.Color(255, 255, 255));

        jMenu1.setText("Dashboard");
        jMenuBar1.add(jMenu1);

        jMenu3.setText("Transaksi");
        jMenuBar1.add(jMenu3);

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

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        loadAktivitasTerakhir();
        updateDashboard();
    }//GEN-LAST:event_refreshButtonActionPerformed

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        // TODO add your handling code here:
        resetFormTransaksi();
    }//GEN-LAST:event_resetButtonActionPerformed

    private void btnSimpan1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpan1ActionPerformed
        try {
            StokItem selected = (StokItem) cbBarang.getSelectedItem();

            if(selected == null || selected.getIdItem() == 0) {
                JOptionPane.showMessageDialog(this, "Pilih barang terlebih dahulu!");
                return;
            }

            String inputJumlah = txtJumlah.getText().trim();
            if(inputJumlah.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Masukkan jumlah barang!");
                txtJumlah.requestFocus();
                return;
            }

            int jumlah = Integer.parseInt(inputJumlah);

            if(jumlah <= 0) {
                JOptionPane.showMessageDialog(this, "Jumlah harus lebih dari 0!");
                return;
            }

            if(jumlah > selected.getStok()) {
                JOptionPane.showMessageDialog(this, "Stok tidak mencukupi!");
                return;
            }

            int total = selected.getHarga() * jumlah;
            String message = "Detail Transaksi:\n" +
            "Barang: " + selected.getNamaItem() + "\n" +
            "Jumlah: " + jumlah + " " + selected.getSatuan() + "\n" +
            "Harga: Rp " + String.format("%,d", selected.getHarga()) + "\n" +
            "Total: Rp " + String.format("%,d", total) + "\n\n" +
            "Proses transaksi ini?";

            int confirm = JOptionPane.showConfirmDialog(this, message,
                "Konfirmasi Transaksi", JOptionPane.YES_NO_OPTION);

            if(confirm != JOptionPane.YES_OPTION) {
                return;
            }

            Connection conn = DatabaseConnection.getConnection();

            //  Insert ke transactions_out
            String sqlInsert = "INSERT INTO transactions_out " +
            "(id_item, jumlah, keterangan, tanggal, id_user) " +
            "VALUES (?, ?, ?, NOW(), ?)";
            PreparedStatement psInsert = conn.prepareStatement(sqlInsert);
            psInsert.setInt(1, selected.getIdItem());
            psInsert.setInt(2, jumlah);
            psInsert.setString(3, "Transaksi");
            psInsert.setInt(4, currentUserId);
            psInsert.executeUpdate();

            //  Update stok di items
            String sqlUpdate = "UPDATE items SET stok = stok - ? WHERE id_item = ?";
            PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate);
            psUpdate.setInt(1, jumlah);
            psUpdate.setInt(2, selected.getIdItem());
            psUpdate.executeUpdate();

            JOptionPane.showMessageDialog(this,
                "Transaksi berhasil!\nTotal: Rp " + String.format("%,d", total));

            resetFormTransaksi();
            loadItemsCombo(); // Refresh combobox
            updateDashboard();  // Update dashboard

        } catch(NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Jumlah tidak valid!");
        } catch(Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memproses transaksi: " + e.getMessage());
        }
    }//GEN-LAST:event_btnSimpan1ActionPerformed

    private void txtJumlahKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtJumlahKeyReleased
        // TODO add your handling code here:
        calculateTotal();
    }//GEN-LAST:event_txtJumlahKeyReleased

    private void cbBarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbBarangActionPerformed
        // TODO add your handling code here:

        StokItem selected = (StokItem) cbBarang.getSelectedItem();

        if(selected != null && selected.getIdItem() != 0) {
            // Tampilkan harga satuan
            lblHargaSatuan.setText("Rp " + String.format("%,d", selected.getHarga()));

            // Tampilkan stok tersedia
            lblStokTersedia.setText("Stok: " + selected.getStok() + " " + selected.getSatuan());

            // Reset jumlah dan total
            txtJumlah.setText("");
            lblTotalHarga.setText("Rp 0");

            // Set focus ke input jumlah
            txtJumlah.requestFocus();
        } else {
            // Reset semua field jika placeholder dipilih
            lblHargaSatuan.setText("Rp 0");
            lblStokTersedia.setText("Stok: -");
            txtJumlah.setText("");
            lblTotalHarga.setText("Rp 0");
        }
    }//GEN-LAST:event_cbBarangActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new User().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSimpan1;
    private javax.swing.JComboBox<StokItem> cbBarang;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblHargaSatuan;
    private javax.swing.JLabel lblStokTersedia;
    private javax.swing.JLabel lblTotalHarga;
    private javax.swing.JLabel lblTotalPenjualan;
    private javax.swing.JLabel lblTotalTransaksi;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel panelDashboard;
    private javax.swing.JPanel panelStokMasuk;
    private javax.swing.JPanel panelTransaksi;
    private javax.swing.JButton refreshButton;
    private javax.swing.JButton resetButton;
    private javax.swing.JPanel subPanelStok;
    private javax.swing.JTable tblAktivitas;
    private javax.swing.JTextField txtJumlah;
    // End of variables declaration//GEN-END:variables
}