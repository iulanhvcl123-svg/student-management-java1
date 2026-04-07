import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import com.formdev.flatlaf.FlatLightLaf;

// ===== MODEL =====
class Student implements Serializable {
    String id, name, dob, classId;

    Student(String id, String name, String dob, String classId) {
        this.id = id;
        this.name = name;
        this.dob = dob;
        this.classId = classId;
    }
}

// ===== MAIN =====
public class StudentGUI extends JFrame {
    ArrayList<Student> list = new ArrayList<>();

    JTextField txtId, txtName, txtClass;
    JSpinner txtDob;

    DefaultTableModel model;
    JTable table;

    CardLayout cardLayout;
    JPanel contentPanel;

    JButton btnHome, btnManage;

    // 🔥 label dashboard
    JLabel totalStudentLabel;

    final String FILE_NAME = "students.dat";

    // ===== AUTO ID =====
    String generateID() {
        int max = 0;
        for (Student s : list) {
            try {
                int num = Integer.parseInt(s.id.replace("SV", ""));
                if (num > max) max = num;
            } catch (Exception e) {}
        }
        return String.format("SV%03d", max + 1);
    }

    // ===== UPDATE DASHBOARD =====
    void updateDashboard() {
        if (totalStudentLabel != null) {
            totalStudentLabel.setText(String.valueOf(list.size()));
        }
    }

    // ===== SIDEBAR ACTIVE =====
    void setActive(JButton btn) {
        btnHome.setBackground(Color.WHITE);
        btnManage.setBackground(Color.WHITE);
        btn.setBackground(new Color(200, 230, 255));
    }

    public StudentGUI() {
        setTitle("Quan Ly Sinh Vien");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        loadFromFile();

        setLayout(new BorderLayout());

        // ===== SIDEBAR =====
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(33, 150, 243));
        sidebar.setPreferredSize(new Dimension(200, getHeight()));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(" MENU");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(title);
        sidebar.add(Box.createVerticalStrut(30));

        btnHome = new JButton("🏠 Trang chu");
        btnManage = new JButton("👨‍🎓 Sinh vien");

        for (JButton b : new JButton[]{btnHome, btnManage}) {
            b.setMaximumSize(new Dimension(180, 45));
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            b.setFocusPainted(false);
            b.setBackground(Color.WHITE);
            sidebar.add(b);
            sidebar.add(Box.createVerticalStrut(10));
        }

        add(sidebar, BorderLayout.WEST);

        // ===== CARD =====
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // ===== DASHBOARD =====
        JPanel homePanel = new JPanel(new GridLayout(2, 2, 20, 20));
        homePanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        homePanel.setBackground(new Color(245,247,250));

        homePanel.add(createCard("👨‍🎓 Tong SV", "" + list.size(), new Color(76,175,80)));
        homePanel.add(createCard("📘 Lop", "TKPTG", new Color(33,150,243)));
        homePanel.add(createCard("🎂 Trung binh tuoi", "20", new Color(255,152,0)));
        homePanel.add(createCard("⭐ Trang thai", "Hoat dong", new Color(156,39,176)));

        // ===== FORM =====
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Thong tin sinh vien"));
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx=0; gbc.gridy=0;
        panel.add(new JLabel("Ma SV:"), gbc);

        gbc.gridx=1;
        txtId = new JTextField(15);
        txtId.setEditable(false);
        txtId.setText(generateID());
        panel.add(txtId, gbc);

        gbc.gridx=0; gbc.gridy=1;
        panel.add(new JLabel("Ho & Ten:"), gbc);

        gbc.gridx=1;
        txtName = new JTextField(15);
        panel.add(txtName, gbc);

        gbc.gridx=0; gbc.gridy=2;
        panel.add(new JLabel("Ngay sinh:"), gbc);

        gbc.gridx=1;
        txtDob = new JSpinner(new SpinnerDateModel());
        txtDob.setEditor(new JSpinner.DateEditor(txtDob, "dd/MM/yyyy"));
        panel.add(txtDob, gbc);

        gbc.gridx=0; gbc.gridy=3;
        panel.add(new JLabel("Ma lop:"), gbc);

        gbc.gridx=1;
        txtClass = new JTextField(15);
        panel.add(txtClass, gbc);

        gbc.gridx=0; gbc.gridy=4;
        JButton btnAdd = new JButton("Them");
        panel.add(btnAdd, gbc);

        gbc.gridx=1;
        JButton btnUpdate = new JButton("Sua");
        panel.add(btnUpdate, gbc);

        // ===== TABLE =====
        model = new DefaultTableModel(new String[]{"Ma SV","Ten","Ngay sinh","Lop"},0);
        table = new JTable(model);
        table.setRowHeight(28);

        JScrollPane scroll = new JScrollPane(table);

        JPanel bottom = new JPanel();
        JButton btnDelete = new JButton("Xoa");
        JButton btnSearch = new JButton("Tim");
        JButton btnReset = new JButton("Reset");

        bottom.add(btnDelete);
        bottom.add(btnSearch);
        bottom.add(btnReset);

        JPanel studentPanel = new JPanel(new BorderLayout());
        studentPanel.add(panel, BorderLayout.NORTH);
        studentPanel.add(scroll, BorderLayout.CENTER);
        studentPanel.add(bottom, BorderLayout.SOUTH);

        contentPanel.add(homePanel, "HOME");
        contentPanel.add(studentPanel, "STUDENT");

        add(contentPanel, BorderLayout.CENTER);

        showData();
        updateDashboard(); // 🔥 init

        // ===== SIDEBAR =====
        btnHome.addActionListener(e -> {
            cardLayout.show(contentPanel, "HOME");
            setActive(btnHome);
        });

        btnManage.addActionListener(e -> {
            cardLayout.show(contentPanel, "STUDENT");
            setActive(btnManage);
        });

        setActive(btnHome);

        // ===== ADD =====
        btnAdd.addActionListener(e -> {
            String id = generateID();
            String name = txtName.getText().trim();
            String classId = txtClass.getText().trim();

            Date date = (Date) txtDob.getValue();
            String dob = new SimpleDateFormat("dd/MM/yyyy").format(date);

            if(name.isEmpty() || classId.isEmpty()){
                JOptionPane.showMessageDialog(this,"Khong duoc de trong!");
                return;
            }

            list.add(new Student(id,name,dob,classId));
            saveToFile();
            showData();
            updateDashboard(); // 🔥 realtime
            clear();
        });

        // DELETE
        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row>=0){
                list.remove(row);
                saveToFile();
                showData();
                updateDashboard(); // 🔥 realtime
                clear();
            }
        });

        // SEARCH
        btnSearch.addActionListener(e -> {
            String k = JOptionPane.showInputDialog(this,"Nhap:");
            if(k==null) return;

            k=k.toLowerCase();
            model.setRowCount(0);

            for(Student s:list){
                if(s.name.toLowerCase().contains(k) || s.id.toLowerCase().contains(k)){
                    model.addRow(new Object[]{s.id,s.name,s.dob,s.classId});
                }
            }
        });

        btnReset.addActionListener(e->showData());
    }

    // ===== CARD =====
    JPanel createCard(String title,String value,Color color){
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(color);
        p.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JLabel t = new JLabel(title);
        t.setForeground(Color.WHITE);

        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI",Font.BOLD,24));
        v.setForeground(Color.WHITE);

        if(title.contains("Tong SV")){
            totalStudentLabel = v; // 🔥 bind label
        }

        p.add(t,BorderLayout.NORTH);
        p.add(v,BorderLayout.CENTER);

        return p;
    }

    void showData(){
        model.setRowCount(0);
        for(Student s:list){
            model.addRow(new Object[]{s.id,s.name,s.dob,s.classId});
        }
    }

    void saveToFile(){
        try{
            ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(FILE_NAME));
            oos.writeObject(list);
            oos.close();
        }catch(Exception e){e.printStackTrace();}
    }

    void loadFromFile(){
        try{
            ObjectInputStream ois=new ObjectInputStream(new FileInputStream(FILE_NAME));
            list=(ArrayList<Student>)ois.readObject();
            ois.close();
        }catch(Exception e){
            list=new ArrayList<>();
        }
    }

    void clear(){
        txtName.setText("");
        txtClass.setText("");
        txtDob.setValue(new Date());
        txtId.setText(generateID());
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {}

        new StudentGUI().setVisible(true);
    }
}