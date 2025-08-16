import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Room implements Serializable {
    private static final long serialVersionUID = 1L;
    int roomNumber;
    String category; // Standard, Deluxe, Suite
    boolean isAvailable;

    public Room(int roomNumber, String category) {
        this.roomNumber = roomNumber;
        this.category = category;
        this.isAvailable = true;
    }

    @Override
    public String toString() {
        return "Room " + roomNumber + " (" + category + ") - " + (isAvailable ? "Available" : "Booked");
    }
}

class Booking implements Serializable {
    private static final long serialVersionUID = 1L;
    String guestName;
    int roomNumber;
    String category;
    String paymentStatus;

    public Booking(String guestName, int roomNumber, String category) {
        this.guestName = guestName;
        this.roomNumber = roomNumber;
        this.category = category;
        this.paymentStatus = "Paid";
    }

    @Override
    public String toString() {
        return "Guest: " + guestName + ", Room: " + roomNumber + " (" + category + "), Payment: " + paymentStatus;
    }
}

public class HotelReservationSystem {
    static List<Room> rooms = new ArrayList<>();
    static List<Booking> bookings = new ArrayList<>();
    static final String ROOMS_FILE = "rooms.dat";
    static final String BOOKINGS_FILE = "bookings.dat";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        loadRooms();
        loadBookings();

        while (true) {
            System.out.println("\n=== Hotel Reservation System ===");
            System.out.println("1. View Available Rooms");
            System.out.println("2. Book Room");
            System.out.println("3. Cancel Reservation");
            System.out.println("4. View Bookings");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> viewAvailableRooms(scanner);
                case "2" -> bookRoom(scanner);
                case "3" -> cancelReservation(scanner);
                case "4" -> viewBookings();
                case "5" -> {
                    saveRooms();
                    saveBookings();
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    static void initializeRooms() {
        rooms.clear();
        for (int i = 101; i <= 105; i++) rooms.add(new Room(i, "Standard"));
        for (int i = 201; i <= 203; i++) rooms.add(new Room(i, "Deluxe"));
        for (int i = 301; i <= 302; i++) rooms.add(new Room(i, "Suite"));
    }

    static void viewAvailableRooms(Scanner scanner) {
        System.out.print("Enter room category (Standard/Deluxe/Suite or All): ");
        String category = scanner.nextLine();

        System.out.println("\n--- Available Rooms ---");
        boolean found = false;
        for (Room room : rooms) {
            if (room.isAvailable && (category.equalsIgnoreCase("All") || room.category.equalsIgnoreCase(category))) {
                System.out.println(room);
                found = true;
            }
        }
        if (!found) System.out.println("No available rooms for that category.");
    }

    static void bookRoom(Scanner scanner) {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        System.out.print("Enter room category (Standard/Deluxe/Suite): ");
        String category = scanner.nextLine();

        for (Room room : rooms) {
            if (room.isAvailable && room.category.equalsIgnoreCase(category)) {
                room.isAvailable = false;
                Booking booking = new Booking(name, room.roomNumber, category);
                bookings.add(booking);
                System.out.println("Booking successful!");
                System.out.println(booking);
                return;
            }
        }
        System.out.println("No available rooms in " + category + " category.");
    }

    static void cancelReservation(Scanner scanner) {
        System.out.print("Enter your name to cancel reservation: ");
        String name = scanner.nextLine();

        Booking toCancel = null;
        for (Booking booking : bookings) {
            if (booking.guestName.equalsIgnoreCase(name)) {
                toCancel = booking;
                break;
            }
        }

        if (toCancel != null) {
            for (Room room : rooms) {
                if (room.roomNumber == toCancel.roomNumber) {
                    room.isAvailable = true;
                    break;
                }
            }
            bookings.remove(toCancel);
            System.out.println("Reservation canceled for " + name);
        } else {
            System.out.println("No booking found for that name.");
        }
    }

    static void viewBookings() {
        System.out.println("\n--- All Bookings ---");
        for (Booking booking : bookings) {
            System.out.println(booking);
        }
        if (bookings.isEmpty()) {
            System.out.println("No current bookings.");
        }
    }

    // ===== File I/O =====
    @SuppressWarnings("unchecked")
    static void loadRooms() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ROOMS_FILE))) {
            rooms = (List<Room>) ois.readObject();
        } catch (Exception e) {
            System.out.println("Initializing new room data...");
            initializeRooms();
            saveRooms();
        }
    }

    @SuppressWarnings("unchecked")
    static void loadBookings() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(BOOKINGS_FILE))) {
            bookings = (List<Booking>) ois.readObject();
        } catch (Exception e) {
            bookings = new ArrayList<>();
        }
    }

    static void saveRooms() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ROOMS_FILE))) {
            oos.writeObject(rooms);
        } catch (IOException e) {
            System.out.println("Error saving room data.");
        }
    }

    static void saveBookings() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BOOKINGS_FILE))) {
            oos.writeObject(bookings);
        } catch (IOException e) {
            System.out.println("Error saving booking data.");
        }
    }
}


