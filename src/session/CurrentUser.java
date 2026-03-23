
package session;

 import models.User;

 public class CurrentUser {//a class to keep the session of user

     private static User user;

     private CurrentUser() {}

     public static void setUser(User logUser) {
         user = logUser;
     }

     public static User getUser() {
         return user;
     }

     public static void logout() {
         user = null;
     }

     public static boolean isLoggedIn() {
         return user != null;
     }

     public static String getDisplayName() {
         if (user != null) {
             return user.getName() + " " + user.getLastname();
         }
         return "Guest";
     }
 }