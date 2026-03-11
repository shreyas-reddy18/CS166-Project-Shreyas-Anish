import java.sql.*;
import java.util.Scanner;

public class MechanicShopClient {
    // Database credentials
    static final String DB_URL = "jdbc:postgresql://localhost:5432/your_database_name";
    static final String USER = "your_username";
    static final String PASS = "your_password";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Scanner scanner = new Scanner(System.in)) {
             
            boolean running = true;
            while (running) {
                System.out.println("\n--- Mechanic Shop Database System ---");
                System.out.println("1. Add Customer");
                System.out.println("2. Add Mechanic");
                System.out.println("3. Add Car");
                System.out.println("4. Initiate a Service Request");
                System.out.println("5. Close a Service Request");
                System.out.println("6. Run Reports");
                System.out.println("0. Exit");
                System.out.print("Select an option: ");
                
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1: addCustomer(conn, scanner); break;
                    // Add other cases here...
                    case 0: running = false; break;
                    default: System.out.println("Invalid choice. Try again.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Connection Error: " + e.getMessage());
        }
    }

    private static void addCustomer(Connection conn, Scanner scanner) {
        System.out.print("Enter First Name: ");
        String fname = scanner.nextLine();
        System.out.print("Enter Last Name: ");
        String lname = scanner.nextLine();
        System.out.print("Enter Phone: ");
        String phone = scanner.nextLine();
        System.out.print("Enter Address: ");
        String address = scanner.nextLine();

        // 10% of grade is Error Handling - Ensure inputs aren't empty!
        if (fname.isEmpty() || lname.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            System.out.println("Error: All fields are required.");
            return;
        }

        String sql = "INSERT INTO Customer (fname, lname, phone, address) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fname);
            pstmt.setString(2, lname);
            pstmt.setString(3, phone);
            pstmt.setString(4, address);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Success! Customer added.");
            }
        } catch (SQLException e) {
            System.out.println("Failed to add customer. Error: " + e.getMessage());
        }
    }
}