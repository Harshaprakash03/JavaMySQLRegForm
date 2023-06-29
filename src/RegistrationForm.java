import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class RegistrationForm extends JDialog {
    private JTextField tfName;
    private JTextField tfEmail;
    private JTextField tfPhone;
    private JTextField tfAddress;
    private JPasswordField pfPassword;
    private JPasswordField pfConfirmPassword;
    private JButton btnRegister;
    private JButton btnCancel;
    private JPanel RegisterPanel;

    public RegistrationForm(JFrame parent) {
        super(parent);
        setTitle("Create a new Account");
        setContentPane(RegisterPanel);
        setMinimumSize(new Dimension(450,474));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });

        setVisible(true);
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void registerUser() {

        String name = tfName.getText();
        String email = tfEmail.getText();
        String phone = tfPhone.getText();
        String address = tfAddress.getText();
        String password = String.valueOf(pfPassword.getPassword());
        String confirmpassword = String.valueOf(pfConfirmPassword.getPassword());

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,"Please Enter All Fields",
                    "Try again",JOptionPane.ERROR_MESSAGE);

            return;
        }

        if (!password.equals(confirmpassword)) {
            JOptionPane.showMessageDialog(this,"Confirmed Password does not match","Try agin",
                    JOptionPane.ERROR_MESSAGE);

            return;
        }

        user = adduserToDatabase(name,email,phone,address,password);
        if (user !=null){
            dispose();
        }
        else {
            JOptionPane.showMessageDialog(this,"Failed to register new user","Try again",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public User user;
    private User adduserToDatabase(String name, String email, String phone, String address, String password) {

        User user = null;
        final String DB_URL = "jdbc:mysql://localhost/MyStore?serverTimezone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try{
            Connection conn = DriverManager.getConnection(DB_URL,USERNAME,PASSWORD);
            //connected to database successfully

            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO users (name,email,phone,address,password)" +
                    "VALUES (?,?,?,?,?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1,name);
            preparedStatement.setString(2,email);
            preparedStatement.setString(3,phone);
            preparedStatement.setString(4,address);
            preparedStatement.setString(5,password);

            //insert rwo in table
            int addedRows = preparedStatement.executeUpdate();
            if (addedRows > 0){
                user = new User();
                user.name= name;
                user.email = email;
                user.phone=phone;
                user.address=address;
                user.password=password;
            }

            stmt.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return user;

    }

    public static void main(String[] args) {
        RegistrationForm MyForm = new RegistrationForm(null);

        User user = MyForm.user;
        if (user != null){
            System.out.println("Successfully Registered : "+ user.name);
        }
        else{
            System.out.println("Registration Cancelled");
        }
    }
}
