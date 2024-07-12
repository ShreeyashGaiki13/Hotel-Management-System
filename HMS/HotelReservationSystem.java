import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class HotelReservationSystem {
    private static final String url = "jdbc:mysql://127.0.0.1:3306/hotel";
    private static final String username = "root";
    private static final String password = "admin@123";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            Scanner sc = new Scanner(System.in);
            while (true) {
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM");
                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservations");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservations");
                System.out.println("5. Delete Reservations");
                System.out.println("0. Exit");
                System.out.print("Choose an option: ");
                int choice = sc.nextInt();
                switch (choice) {
                    case 1:
                        reserveRoom(connection, sc);
                        break;
                    case 2:
                        viewReservations(connection);
                        break;
                    case 3:
                        getRoomNumber(connection, sc);
                        break;
                    case 4:
                        updateReservation(connection, sc);
                        break;
                    case 5:
                        deleteReservation(connection, sc);
                        break;
                    case 0:
                        try{
                             quit();
                        } catch (InterruptedException e){

                        }
                        sc.close();
                        return;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void reserveRoom(Connection connection, Scanner sc) {
        try {
            System.out.println("Guest Name: ");
            String guestName = sc.next();
            sc.nextLine(); // consume newline
            System.out.println("Room Number: ");
            int roomNumber = sc.nextInt();
            System.out.println("Contact Number: ");
            String contactNumber = sc.next();

            String query = "INSERT INTO reservation (cname, room, contact) VALUES ('" + guestName + "', " + roomNumber + ", '" + contactNumber + "')";
            Statement statement = connection.createStatement();
            try {
                int success = statement.executeUpdate(query);
                if (success > 0) {
                    System.out.println("Guest Reserved");
                } else {
                    System.out.println("Reservation Failed");
                }
            } finally {
                statement.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void viewReservations(Connection connection) throws SQLException {
        String query = "SELECT id, cname, room, contact, rtime FROM reservation";
        Statement statement = null;
        ResultSet result = null;
        try {
            statement = connection.createStatement();
            result = statement.executeQuery(query);
            System.out.println("Current Reservations:");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
            System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number       | Reservation Time        |");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");

            while (result.next()) {
                int id = result.getInt("id");
                String guestName = result.getString("cname");
                int room = result.getInt("room");
                String contact = result.getString("contact");
                String rtime = result.getTimestamp("rtime").toString();

                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
                        id, guestName, room, contact, rtime);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if (result != null) try { result.close(); } catch (SQLException e) { /* ignored */ }
            if (statement != null) try { statement.close(); } catch (SQLException e) { /* ignored */ }
        }
        System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
    }

    private static void getRoomNumber(Connection connection, Scanner sc) {
        try {
            System.out.print("Enter Guest Name: ");
            String guestName = sc.next();
            String query = "SELECT room FROM reservation WHERE cname='" + guestName + "'";
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(query);
            if (result.next()) {
                int room = result.getInt("room");
                System.out.println("Room Number: " + room);
            } else {
                System.out.println("No reservation found for " + guestName);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void updateReservation(Connection connection, Scanner sc) {
        try {
            System.out.print("Enter Reservation ID: ");
            int id = sc.nextInt();
            System.out.print("Enter New Room Number: ");
            int newRoom = sc.nextInt();
            String query = "UPDATE reservation SET room=" + newRoom + " WHERE id=" + id;
            Statement statement = connection.createStatement();
            int success = statement.executeUpdate(query);
            if (success > 0) {
                System.out.println("Reservation Updated");
            } else {
                System.out.println("Update Failed");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void deleteReservation(Connection connection, Scanner sc) {
        try {
            System.out.print("Enter Reservation ID: ");
            int id = sc.nextInt();
            String query = "DELETE FROM reservation WHERE id=" + id;
            Statement statement = connection.createStatement();
            int success = statement.executeUpdate(query);
            if (success > 0) {
                System.out.println("Reservation Deleted");
            } else {
                System.out.println("Deletion Failed");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void quit() throws InterruptedException {
        System.out.print("Exiting System");
        int i = 5;
        while(i!=0){
            System.out.print(".");
            Thread.sleep(1000);
            i--;
        }
        System.out.println();
        System.out.println("ThankYou For Using Hotel Reservation System!!!");
    }
}