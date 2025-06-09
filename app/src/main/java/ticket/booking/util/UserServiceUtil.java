package ticket.booking.util;

import org.mindrot.jbcrypt.BCrypt;

public class UserServiceUtil {

     public static String hashPassword(String plainPassword){
         return BCrypt.hashpw(plainPassword, BCrypt.gensalt()); // No one can understand this password as we doing hashing on the main password.
     }

     public static boolean checkPassword(String plainPassword, String hashedPassword){
          return BCrypt.checkpw(plainPassword, hashedPassword);
     }

     public static String generateTicketId() {
          return "" + System.currentTimeMillis();
     }
}
