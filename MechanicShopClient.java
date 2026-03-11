import java.sql.*;
import java.util.Scanner;

public class MechanicShopClient {
    // Database credentials
    static final String DB_URL = "jdbc:postgresql://localhost:35749/snall008_DB_Project";
    static final String USER = "snall008";
    static final String PASS = ""; 

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
                
                try {
                    int choice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    switch (choice) {
                        case 1: 
                            addCustomer(conn, scanner); 
                            break;
                        case 2: 
                            addMechanic(conn, scanner); 
                            break;
                        case 3: 
                            addCar(conn, scanner); 
                            break;
                        case 4: 
                            initiateServiceRequest(conn, scanner); 
                            break;
                        case 5: 
                            closeServiceRequest(conn, scanner); 
                            break;
                        case 6: 
                            runReports(conn, scanner); 
                            break;
                        case 0: 
                            running = false; 
                            System.out.println("Exiting system. Goodbye!");
                            break;
                        default: 
                            System.out.println("Invalid choice. Please select a valid option.");
                    }
                } catch (java.util.InputMismatchException e) {
                    System.out.println("Error: Invalid input. Please enter a number.");
                    scanner.nextLine(); // Clear the bad input from the scanner buffer
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

    private static void addMechanic(Connection conn, Scanner scanner) {
        System.out.print("Enter First Name: ");
        String fname = scanner.nextLine();
        System.out.print("Enter Last Name: ");
        String lname = scanner.nextLine();
        System.out.print("Enter Years of Experience: ");
        int experience;

        try {
            experience = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Error: Experience must be a valid number.");
            return;
        }

        if (fname.isEmpty() || lname.isEmpty() || experience < 0) {
            System.out.println("Error: Invalid input. Names cannot be empty and experience must be positive.");
            return;
        }

        String sql = "INSERT INTO Mechanic (fname, lname, experience) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fname);
            pstmt.setString(2, lname);
            pstmt.setInt(3, experience);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Success! Mechanic added.");
            }
        } catch (SQLException e) {
            System.out.println("Failed to add mechanic. Error: " + e.getMessage());
        }
    }

    private static void addCar(Connection conn, Scanner scanner) {
        System.out.print("Enter VIN (max 17 chars): ");
        String vin = scanner.nextLine();
        System.out.print("Enter Make: ");
        String make = scanner.nextLine();
        System.out.print("Enter Model: ");
        String model = scanner.nextLine();
        System.out.print("Enter Year: ");
        int year;
        System.out.print("Enter Customer ID who owns this car: ");
        int customerId;

        try {
            year = Integer.parseInt(scanner.nextLine());
            customerId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Error: Year and Customer ID must be valid numbers.");
            return;
        }

        if (vin.isEmpty() || make.isEmpty() || model.isEmpty() || vin.length() > 17) {
            System.out.println("Error: Invalid input. Ensure fields are not empty and VIN is 17 characters or less.");
            return;
        }

        String sql = "INSERT INTO Car (vin, make, model, year, customer_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, vin);
            pstmt.setString(2, make);
            pstmt.setString(3, model);
            pstmt.setInt(4, year);
            pstmt.setInt(5, customerId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Success! Car added.");
            }
        } catch (SQLException e) {
            System.out.println("Failed to add car. Check if the Customer ID exists. Error: " + e.getMessage());
        }
    }

    private static void initiateServiceRequest(Connection conn, Scanner scanner) {
        try {
            System.out.print("Enter Customer Last Name: ");
            String lname = scanner.nextLine();

            // 1. Search for Customer
            String custQuery = "SELECT id, fname, lname FROM Customer WHERE lname = ?";
            PreparedStatement custStmt = conn.prepareStatement(custQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            custStmt.setString(1, lname);
            ResultSet custRs = custStmt.executeQuery();

            int customerId = -1;

            // Check if we have results
            if (!custRs.isBeforeFirst()) {
                System.out.println("No customers found with last name: " + lname);
                System.out.println("Let's add a new customer.");
                System.out.print("Enter First Name: ");
                String newFname = scanner.nextLine();
                System.out.print("Enter Phone: ");
                String phone = scanner.nextLine();
                System.out.print("Enter Address: ");
                String address = scanner.nextLine();

                String insertCust = "INSERT INTO Customer (fname, lname, phone, address) VALUES (?, ?, ?, ?)";
                PreparedStatement insertCustStmt = conn.prepareStatement(insertCust, Statement.RETURN_GENERATED_KEYS);
                insertCustStmt.setString(1, newFname);
                insertCustStmt.setString(2, lname);
                insertCustStmt.setString(3, phone);
                insertCustStmt.setString(4, address);
                insertCustStmt.executeUpdate();

                ResultSet keys = insertCustStmt.getGeneratedKeys();
                if (keys.next()) {
                    customerId = keys.getInt(1);
                }
            } else {
                // Display matching customers
                System.out.println("\nMatching Customers:");
                int count = 1;
                while (custRs.next()) {
                    System.out.println(count + ". " + custRs.getString("fname") + " " + custRs.getString("lname") + " (ID: " + custRs.getInt("id") + ")");
                    count++;
                }
                System.out.print("Select a customer by number (1-" + (count - 1) + "): ");
                int choice = Integer.parseInt(scanner.nextLine());
                
                custRs.absolute(choice); // Move to the selected row
                customerId = custRs.getInt("id");
            }

            // 2. List Cars for the Customer
            String carQuery = "SELECT vin, make, model FROM Car WHERE customer_id = ?";
            PreparedStatement carStmt = conn.prepareStatement(carQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            carStmt.setInt(1, customerId);
            ResultSet carRs = carStmt.executeQuery();

            String selectedVin = "";

            if (!carRs.isBeforeFirst()) {
                System.out.println("No cars found for this customer. Let's add one.");
                selectedVin = promptForNewCar(conn, scanner, customerId);
            } else {
                System.out.println("\nCustomer's Cars:");
                int count = 1;
                while (carRs.next()) {
                    System.out.println(count + ". " + carRs.getString("make") + " " + carRs.getString("model") + " (VIN: " + carRs.getString("vin") + ")");
                    count++;
                }
                System.out.println(count + ". Add a NEW car instead");
                System.out.print("Select an option (1-" + count + "): ");
                int carChoice = Integer.parseInt(scanner.nextLine());

                if (carChoice == count) {
                    selectedVin = promptForNewCar(conn, scanner, customerId);
                } else {
                    carRs.absolute(carChoice);
                    selectedVin = carRs.getString("vin");
                }
            }

            // 3. Create the Service Request
            System.out.println("\n--- Entering Service Request Details ---");
            System.out.print("Enter Date (YYYY-MM-DD): ");
            String date = scanner.nextLine();
            System.out.print("Enter Odometer reading: ");
            int odometer = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter Complaint/Issue: ");
            String complain = scanner.nextLine();
            System.out.print("Enter Mechanic ID assigning the request: ");
            int mechanicId = Integer.parseInt(scanner.nextLine());

            String srQuery = "INSERT INTO Service_Request (date, odometer, complain, vin, mechanic_id) VALUES (?::DATE, ?, ?, ?, ?)";
            PreparedStatement srStmt = conn.prepareStatement(srQuery);
            srStmt.setString(1, date);
            srStmt.setInt(2, odometer);
            srStmt.setString(3, complain);
            srStmt.setString(4, selectedVin);
            srStmt.setInt(5, mechanicId);

            int rows = srStmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Success! Service Request initiated.");
            }

        } catch (SQLException e) {
            System.out.println("Database error during service request: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number entered. Aborting request.");
        }
    }

    // Helper method to keep the main method clean
    private static String promptForNewCar(Connection conn, Scanner scanner, int customerId) throws SQLException {
        System.out.print("Enter VIN (max 17 chars): ");
        String vin = scanner.nextLine();
        System.out.print("Enter Make: ");
        String make = scanner.nextLine();
        System.out.print("Enter Model: ");
        String model = scanner.nextLine();
        System.out.print("Enter Year: ");
        int year = Integer.parseInt(scanner.nextLine());

        String sql = "INSERT INTO Car (vin, make, model, year, customer_id) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, vin);
        pstmt.setString(2, make);
        pstmt.setString(3, model);
        pstmt.setInt(4, year);
        pstmt.setInt(5, customerId);
        pstmt.executeUpdate();
        
        return vin;
    }

    private static void closeServiceRequest(Connection conn, Scanner scanner) {
        try {
            System.out.println("\n--- Close a Service Request ---");
            System.out.print("Enter Service Request ID (rid): ");
            int rid = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter Mechanic ID (Employee ID): ");
            int mechanicId = Integer.parseInt(scanner.nextLine());

            // 1. Verify Mechanic Exists
            String mechQuery = "SELECT id FROM Mechanic WHERE id = ?";
            try (PreparedStatement mechStmt = conn.prepareStatement(mechQuery)) {
                mechStmt.setInt(1, mechanicId);
                ResultSet mechRs = mechStmt.executeQuery();
                if (!mechRs.next()) {
                    System.out.println("Error: Mechanic with ID " + mechanicId + " does not exist.");
                    return; // Abort the process
                }
            }

            // 2. Verify Service Request Exists AND fetch its start date
            String srQuery = "SELECT date FROM Service_Request WHERE rid = ?";
            java.sql.Date requestDate = null;
            try (PreparedStatement srStmt = conn.prepareStatement(srQuery)) {
                srStmt.setInt(1, rid);
                ResultSet srRs = srStmt.executeQuery();
                if (!srRs.next()) {
                    System.out.println("Error: Open Service Request with ID " + rid + " does not exist.");
                    return; // Abort the process
                }
                requestDate = srRs.getDate("date"); // Save the date to compare later
            }

            // 3. Check if the request is already closed
            String checkClosedQuery = "SELECT wid FROM Closed_Request WHERE rid = ?";
            try (PreparedStatement checkClosedStmt = conn.prepareStatement(checkClosedQuery)) {
                checkClosedStmt.setInt(1, rid);
                ResultSet checkRs = checkClosedStmt.executeQuery();
                if (checkRs.next()) {
                    System.out.println("Error: Service Request " + rid + " has already been closed.");
                    return;
                }
            }

            // 4. Get closing details and validate the date
            System.out.print("Enter Closing Date (YYYY-MM-DD): ");
            String closingDateStr = scanner.nextLine();
            java.sql.Date closingDate = java.sql.Date.valueOf(closingDateStr); // Parses string to SQL Date

            if (closingDate.before(requestDate)) {
                System.out.println("Error: Closing date (" + closingDateStr + ") cannot be before the original request date (" + requestDate.toString() + ").");
                return;
            }

            System.out.print("Enter Comments/Notes: ");
            String comment = scanner.nextLine();

            System.out.print("Enter Final Bill Amount: ");
            double bill = Double.parseDouble(scanner.nextLine());
            if (bill < 0) {
                System.out.println("Error: Bill amount cannot be negative.");
                return;
            }

            // 5. Insert the final closing record
            String insertSql = "INSERT INTO Closed_Request (rid, date, comment, bill, mechanic_id) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, rid);
                insertStmt.setDate(2, closingDate);
                insertStmt.setString(3, comment);
                insertStmt.setDouble(4, bill);
                insertStmt.setInt(5, mechanicId);

                int rows = insertStmt.executeUpdate();
                if (rows > 0) {
                    System.out.println("Success! Service request " + rid + " is now closed.");
                }
            }

        } catch (NumberFormatException e) {
            System.out.println("Error: Please enter valid numbers for IDs and Bill Amount.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: Invalid date format. Please ensure you use YYYY-MM-DD.");
        } catch (SQLException e) {
            System.out.println("Database Error during closing: " + e.getMessage());
        }
    }

    private static void runReports(Connection conn, Scanner scanner) {
        boolean reporting = true;
        while (reporting) {
            System.out.println("\n--- Analytical Reports ---");
            System.out.println("1. Closed requests with bill < $100");
            System.out.println("2. Customers with > 20 cars");
            System.out.println("3. Cars built before 1995 with < 50,000 miles");
            System.out.println("4. Top 'k' cars with the highest number of open requests");
            System.out.println("5. Customers sorted descending by total bill");
            System.out.println("0. Return to Main Menu");
            System.out.print("Select a report: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        reportClosedRequestsUnder100(conn);
                        break;
                    case 2:
                        reportCustomersWithManyCars(conn);
                        break;
                    case 3:
                        reportOldCarsLowMiles(conn);
                        break;
                    case 4:
                        System.out.print("Enter the number of cars (k) to display: ");
                        int k = Integer.parseInt(scanner.nextLine());
                        if (k <= 0) {
                            System.out.println("Error: 'k' must be greater than 0.");
                        } else {
                            reportTopKCarsOpenRequests(conn, k);
                        }
                        break;
                    case 5:
                        reportCustomersByTotalBill(conn);
                        break;
                    case 0:
                        reporting = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number.");
            }
        }
    }

    // --- Report Helper Methods ---

    private static void reportClosedRequestsUnder100(Connection conn) {
        String sql = "SELECT cu.fname, cu.lname, cr.date, cr.comment, cr.bill " +
                    "FROM Customer cu " +
                    "JOIN Car c ON cu.id = c.customer_id " +
                    "JOIN Service_Request sr ON c.vin = sr.vin " +
                    "JOIN Closed_Request cr ON sr.rid = cr.rid " +
                    "WHERE cr.bill < 100";
                    
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\n--- Closed Requests < $100 ---");
            System.out.printf("%-15s %-15s %-12s %-10s %s\n", "First Name", "Last Name", "Date", "Bill", "Comment");
            System.out.println("----------------------------------------------------------------------");
            while (rs.next()) {
                System.out.printf("%-15s %-15s %-12s $%-9.2f %s\n", 
                    rs.getString("fname"), rs.getString("lname"), 
                    rs.getDate("date").toString(), rs.getDouble("bill"), 
                    rs.getString("comment"));
            }
        } catch (SQLException e) {
            System.out.println("Error executing report: " + e.getMessage());
        }
    }

    private static void reportCustomersWithManyCars(Connection conn) {
        String sql = "SELECT cu.fname, cu.lname, COUNT(c.vin) as car_count " +
                    "FROM Customer cu " +
                    "JOIN Car c ON cu.id = c.customer_id " +
                    "GROUP BY cu.id, cu.fname, cu.lname " +
                    "HAVING COUNT(c.vin) > 20";

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\n--- Customers with > 20 Cars ---");
            System.out.printf("%-15s %-15s %s\n", "First Name", "Last Name", "Total Cars");
            System.out.println("----------------------------------------");
            while (rs.next()) {
                System.out.printf("%-15s %-15s %d\n", 
                    rs.getString("fname"), rs.getString("lname"), rs.getInt("car_count"));
            }
        } catch (SQLException e) {
            System.out.println("Error executing report: " + e.getMessage());
        }
    }

    private static void reportOldCarsLowMiles(Connection conn) {
        String sql = "SELECT DISTINCT c.make, c.model, c.year, sr.odometer " +
                    "FROM Car c " +
                    "JOIN Service_Request sr ON c.vin = sr.vin " +
                    "WHERE c.year < 1995 AND sr.odometer < 50000";

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\n--- Cars < 1995 and < 50,000 miles ---");
            System.out.printf("%-15s %-15s %-6s %s\n", "Make", "Model", "Year", "Odometer");
            System.out.println("------------------------------------------------");
            while (rs.next()) {
                System.out.printf("%-15s %-15s %-6d %d\n", 
                    rs.getString("make"), rs.getString("model"), 
                    rs.getInt("year"), rs.getInt("odometer"));
            }
        } catch (SQLException e) {
            System.out.println("Error executing report: " + e.getMessage());
        }
    }

    private static void reportTopKCarsOpenRequests(Connection conn, int k) {
        String sql = "SELECT c.make, c.model, COUNT(sr.rid) as request_count " +
                    "FROM Car c " +
                    "JOIN Service_Request sr ON c.vin = sr.vin " +
                    "LEFT JOIN Closed_Request cr ON sr.rid = cr.rid " +
                    "WHERE cr.rid IS NULL " +
                    "GROUP BY c.vin, c.make, c.model " +
                    "ORDER BY request_count DESC " +
                    "LIMIT ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, k);
            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("\n--- Top " + k + " Cars with Highest Open Requests ---");
                System.out.printf("%-15s %-15s %s\n", "Make", "Model", "Open Requests");
                System.out.println("--------------------------------------------------");
                while (rs.next()) {
                    System.out.printf("%-15s %-15s %d\n", 
                        rs.getString("make"), rs.getString("model"), rs.getInt("request_count"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error executing report: " + e.getMessage());
        }
    }

    private static void reportCustomersByTotalBill(Connection conn) {
        String sql = "SELECT cu.fname, cu.lname, SUM(cr.bill) as total_bill " +
                    "FROM Customer cu " +
                    "JOIN Car c ON cu.id = c.customer_id " +
                    "JOIN Service_Request sr ON c.vin = sr.vin " +
                    "JOIN Closed_Request cr ON sr.rid = cr.rid " +
                    "GROUP BY cu.id, cu.fname, cu.lname " +
                    "ORDER BY total_bill DESC";

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\n--- Customers Ranked by Total Bill ---");
            System.out.printf("%-15s %-15s %s\n", "First Name", "Last Name", "Total Bill");
            System.out.println("---------------------------------------------");
            while (rs.next()) {
                System.out.printf("%-15s %-15s $%.2f\n", 
                    rs.getString("fname"), rs.getString("lname"), rs.getDouble("total_bill"));
            }
        } catch (SQLException e) {
            System.out.println("Error executing report: " + e.getMessage());
        }
    }
}