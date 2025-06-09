package ticket.booking.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ticket.booking.entities.Ticket;
import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import ticket.booking.util.UserServiceUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class UserBookingService {

    private User user;

    private List<User> userList;
    private ObjectMapper objectMapper = new ObjectMapper();

    private static final String USERS_PATH = "C:\\Users\\Om357\\Desktop\\CODING\\IRCTC\\app\\src\\main\\java\\ticket\\booking\\localDB\\users.json";


    public UserBookingService(User user1) throws IOException { // CONSTRUCTOR
        this.user = user1; //STORED user at global level.

        loadUsers();
        // File users = new File(USERS_PATH); // 1) We able JAVA to read user file.
        // userList = objectMapper.readValue(users, new TypeReference<List<User>>() {}); // 2) Now we used objectmapper to map variables with the data present int json.
        // TYPE_REFERENCE is very important thing in java as List return generic types of value ,TypeReference makes sure at RUNTIME it resolve the type of variables which are going to mapped
        // ,and then do DESERIALIZE> and work accordingly.
        // JSON -->>> OBJECT (USER) (De-serialization).
        // OBJECT -->> JSON  (Serialization).
    }

    public UserBookingService() throws IOException {
        loadUsers();
    }

    public List<User> loadUsers() throws IOException {

        File users = new File(USERS_PATH);

        return objectMapper.readValue(users, new TypeReference<List<User>>() {
        });

    }

    public Boolean loginUser() {
        Optional<User> foundUser = userList.stream().filter(user1 -> {
            return user1.getName().equalsIgnoreCase(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword());
        }).findFirst();
        return foundUser.isPresent();
    }

    public Boolean signUp(User user1) {
        try {
            userList.add(user1);
            saveUserListToFile();
            return Boolean.TRUE;
        } catch (IOException ex) {
            return Boolean.FALSE;
        }
    }

    private void saveUserListToFile() throws IOException {
        File usersFile = new File(USERS_PATH);
        objectMapper.writeValue(usersFile, userList); //json -- >> OBJECT(USER) ;; SERIALIZATION
    }

    public void fetchBooking() {
        user.printTickets();
    }

    public Optional<User> getUserByUsername(String username){
        return userList.stream().filter(user -> user.getName().equals(username)).findFirst();
    }

    public void setUser(User user){
        this.user = user;
    }

    // todo: Complete this function
    public Boolean cancelBooking(String ticketId) {

        Scanner s = new Scanner(System.in);
        System.out.println("Enter the ticket id to cancel");
        ticketId = s.next();

        if (ticketId == null || ticketId.isEmpty()) {
            System.out.println("Ticket ID cannot be null or empty.");
            return Boolean.FALSE;
        }

        String finalTicketId1 = ticketId;  //Because strings are immutable
        boolean removed = user.getTicketsBooked().removeIf(ticket -> ticket.getTicketId().equals(finalTicketId1));

        String finalTicketId = ticketId;
        user.getTicketsBooked().removeIf(Ticket -> Ticket.getTicketId().equals(finalTicketId));
        if (removed) {
            System.out.println("Ticket with ID " + ticketId + " has been canceled.");
            return Boolean.TRUE;
        } else {
            System.out.println("No ticket found with ID " + ticketId);
            return Boolean.FALSE;
        }
    }

    public List<Train> getTrains(String source, String destination) {
        try {
            TrainService trainService = new TrainService();
            return trainService.searchTrains(source, destination);
        } catch (IOException ex) {
            System.out.println("There is something wrong");
            return Collections.emptyList();
        }
    }

    public List<List<Integer>> fetchSeats(Train train) {
        return train.getSeats();
    }

    public Boolean bookTrainSeat(Train train, int row, int seat) {
        try{
            TrainService trainService = new TrainService();
            List<List<Integer>> seats = train.getSeats();
            if (row >= 0 && row < seats.size() && seat >= 0 && seat < seats.get(row).size()) {
                if (seats.get(row).get(seat) == 0) {
                    seats.get(row).set(seat, 1);

                    train.setSeats(seats);
                    trainService.addTrain(train);

                    Ticket ticket = new Ticket();

                    ticket.setSource(train.getStations().getFirst());
                    ticket.setDestination(train.getStations().getLast());
                    ticket.setTrain(train);
                    ticket.setUserId(user.getUserId());
                    ticket.setDateOfTravel("2021-09-01");
                    ticket.setTicketId(UserServiceUtil.generateTicketId());

                    user.getTicketsBooked().add(ticket);

                    System.out.println("Seat booked successfully  !  ");

                    System.out.println(ticket.getTicketInfo());

                    saveUserListToFile();
                    return true; // Booking successful
                } else {
                    return false; // Execute when Seat is already booked
                }
            } else {
                return false; // Execute when Invalid row or seat index
            }
        }catch (IOException ex){
            return Boolean.FALSE;
        }
    }
}
