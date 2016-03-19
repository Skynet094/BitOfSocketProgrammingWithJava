import java.awt.List;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

class UserStatus implements java.io.Serializable {

 /**
  * 
  */
 private static final long serialVersionUID = 1L;
 String userName;
 Boolean status;

 UserStatus(String userName, Boolean status) {

  this.userName = userName;
  this.status = status;

 }

}

public class TestServer {

 //public static Vector<UserStatus> Friends=new Vector<UserStatus>(); 

 public static volatile HashMap < String, OutputStream > OutputStreamArray = new HashMap < String, OutputStream > ();
 public static volatile HashMap < String, String > UserPassword = new HashMap < String, String > ();
 public static volatile HashMap < String, Vector < UserStatus >> FriendList = new HashMap < String, Vector < UserStatus > > ();
 private static volatile Map < String, Vector < UserStatus >> FriendRequestList = new HashMap < String, Vector < UserStatus >> ();

 public static volatile HashMap < Integer, UserStatus > OnlineFolks = new HashMap < Integer, UserStatus > ();


 public static int workerThreadCount = 0;

 public static void main(String args[]) {
  int id = 1;

  try {
   ServerSocket ss = new ServerSocket(5555);
   System.out.println("Server has been started successfully.");

   while (true) {
    Socket s = ss.accept(); //TCP Connection
    WorkerThread wt = new WorkerThread(s, id, UserPassword, FriendList, FriendRequestList, OnlineFolks, OutputStreamArray);
    Thread t = new Thread(wt);
    t.start();
    workerThreadCount++;
    System.out.println("Client [" + id + "] is now connected. No. of worker threads = " + workerThreadCount);
    id++;
   }
  } catch (Exception e) {
   System.err.println("Problem in ServerSocket operation. Exiting main.");
  }
 }

}

class WorkerThread implements Runnable {

 private Socket socket;
 private InputStream is;
 private OutputStream os;

 private OutputStream friend_os;

 private ObjectOutputStream o_Stream; //for sending objects to client

 private static volatile Vector < UserStatus > temporaryVector;
 private static volatile Map < String, String > UserPassword;
 private static volatile Map < String, Vector < UserStatus >> FriendList;
 private static volatile Map < String, Vector < UserStatus >> FriendRequestList;
 private static volatile Map < String, OutputStream > OutputStreamArray;
 private static volatile Map < Integer, UserStatus > OnlineFolks;
 private static PrintWriter friend_pr;
 private int id = 0;
 private Boolean User_on_flag = true;

 String userID = null;

 public WorkerThread(Socket s, int id, Map < String, String > UserPassword, HashMap < String, Vector < UserStatus >> FriendList, Map < String, Vector < UserStatus >> FriendRequestList, Map < Integer, UserStatus > OnlineFolks, HashMap < String, OutputStream > OutputStreamArray) {
  this.socket = s;

  try {

   this.is = this.socket.getInputStream();
   this.os = this.socket.getOutputStream();
   this.o_Stream = new ObjectOutputStream(s.getOutputStream());

   this.UserPassword = UserPassword;

   this.FriendRequestList = FriendRequestList;
   this.FriendList = FriendList;
   this.OnlineFolks = OnlineFolks;
   this.OnlineFolks.put(id, new UserStatus("Guest" + Integer.toString(id), true));
   this.OutputStreamArray = OutputStreamArray;

  } catch (Exception e) {
   System.err.println("Sorry. Cannot manage client [" + id + "] properly.");
   OnlineFolks.remove(id);


  }

  this.id = id;
 }

 public void run() {
  BufferedReader br = new BufferedReader(new InputStreamReader(this.is));
  PrintWriter pr = new PrintWriter(this.os);

  pr.println("Press i)# to login ii)$ to sign up");
  pr.flush();

  String str;

  while (true) {
   try {


    if ((str = br.readLine()) != null) {


     if (str.equals("#")) {




      pr.println("ENTER_USERID");
      pr.flush();


      if ((str = br.readLine()) != null) {
       userID = str;
       Boolean value = UserPassword.containsKey(str);
       if (value != true) {
        pr.println("USER_NOT_FOUND");
        pr.flush();
       } else {

        pr.println("USER_FOUND");
        pr.flush();


        //System.out.println("WHAT THE HELL IS GOING ON? " + UserList.get(str));



        //System.out.println("I sent this: ENTER PASSWORD");     


        if ((str = br.readLine()) != null) {


         //	System.out.println("I got this: "+ str);  

         String password = str;
         String realPassword = UserPassword.get(userID);

         if (!password.equals(realPassword)) {

          pr.println("PASSWORD_MISMATCH");
          pr.flush();

         } else {
          pr.println("LOGGED_IN");
          pr.flush();
          OnlineFolks.put(id, new UserStatus(userID, true));
          OutputStreamArray.put(userID, this.socket.getOutputStream());

          //friendList container initialized
          if ((str = br.readLine()) != null) {

           if (str.equals("LOGGED_IN_ACK")) {


            System.out.println("User: " + userID + " has logged in.\n\n");

            // here that particular user will be served with various services

            while (true) {


             try {



              if ((str = br.readLine()) != null) {

               if (str.equals("chat")) {

            	   pr.println("Welcome"+userID);
            	   pr.flush();
            	   
            	   System.out.println("I am in the chat branch " );
            	   
                if ((str = br.readLine()) != null) { //expecting friend's ID

                	
                	System.out.println("I am in the chat branch, got this: " + str+ " "+ OutputStreamArray.containsKey(str));
             	   
                 if (OutputStreamArray.containsKey(str)) {
                  friend_os = OutputStreamArray.get(str);
                  friend_pr = new PrintWriter(friend_os);
                  System.out.println("Yes. it contains!");
                  
                  
                 }
                 
                }

                  while (true) {

                   if ((str = br.readLine()) != null) {

                    if (str.equals("BYE")) {
                     
                     break;
                    }
                    
                    else {
                    System.out.println("Trying to send "+ userID+" this: "+str);
                     friend_pr.println(userID + ": " + str);
                     friend_pr.flush();

                   }

                   }

                  }

}

               if (str.equals("file_transfer")) {


               } else if (str.equals("##")) {


                try {

                 System.out.println("I am here!!");
                 //System.out.println("Sent this: "+FriendRequestList.get(userID).elementAt(0).userName);

                 o_Stream.writeUnshared(FriendList.get(userID));
                 o_Stream.flush();


                 System.out.println("I wrote it bruh!!");

                } catch (Exception e) {
                 e.printStackTrace();
                 System.out.println("I CAUGHT IT!!");
                 System.out.println(e);

                }



               } else if (str.equals("@@")) { //ADD reject

                while (true) {


                 //		System.out.println("#$ reached with this value "+ str);

                 if ((str = br.readLine()) != null) {

                  //		System.out.println("Got this from client: "+ str);

                  if (str.equals("X"))
                   break;

                  else {


                   String split_string[] = str.split(",");

                   Boolean test = UserPassword.containsKey(split_string[0].trim());
                   if (test) {


                    FriendRequestList.remove(split_string[0]);

                    if (split_string[1].trim().equals("A")) {

                     //	System.out.println("Got this from client: "+ str);

                     FriendList.get(userID).add(new UserStatus(split_string[0], false));
                     FriendList.get(split_string[0]).add(new UserStatus(userID, false));
                    }



                   }

                  }


                 } else {

                  System.out.println("[" + userID + "] closed the client.");

                  OnlineFolks.remove(id);
                  break;



                 }


                }


               } else if (str.equals("#$")) { //send friend request

                //System.out.println("#$ reached with this value "+ str);


                while (true) {


                 //		System.out.println("#$ reached with this value "+ str);

                 if ((str = br.readLine()) != null) {

                  //		System.out.println("Got this from client: "+ str);

                  if (str.equals("X"))
                   break;

                  else {

                   Boolean test = UserPassword.containsKey(str); //user exists
                   if (test) {

                    //	System.out.println("Got this from client: "+ str);
                    if (FriendRequestList.containsKey(str) != false) {
                     FriendRequestList.get(str).add(new UserStatus(userID, true));

                    }

                   }


                  }

                 } else {

                  System.out.println("[" + userID + "] closed the client.");

                  OnlineFolks.remove(id);
                  break;



                 }


                }



               } else if (str.equals("@#")) { // view friend request queue

                try {

                 System.out.println("I am here!!");
                 //System.out.println("Sent this: "+FriendRequestList.get(userID).elementAt(0).userName);

                 o_Stream.writeUnshared(FriendRequestList.get(userID));

                 o_Stream.flush();

                 System.out.println("I wrote it bruh!!");

                } catch (Exception e) {
                 e.printStackTrace();
                 System.out.println("I CAUGHT IT!!");
                 System.out.println(e);

                }

               } else if (str.equals("@")) {



                try {
                 o_Stream.writeUnshared(OnlineFolks);
                 o_Stream.flush();

                } catch (Exception e) {

                }

               } else if (str.equals("Q")) {
                System.out.println("User: " + userID + " has logged out\n\n");
                OnlineFolks.remove(id);
               // SocketsArray.get(userID).close();
                break;
               }


              } else

              {
               System.out.println("[" + userID + "] closed the client.");
             
               OnlineFolks.remove(id);
               break;

              }




             } catch (Exception e) {


              System.err.println("Problem in communicating with the client [" + userID + "]. Terminating worker thread.");

              OnlineFolks.remove(id);

              break;


             }







            }






           }




          }


         }

        }

       }
      }
     } else if (str.equals("$")) {

      //System.out.println("WHAT THE HELL IS GOING ON?");

      String userID = null;

      pr.println("ENTER_USERID");
      pr.flush();


      while (true) {
       if ((str = br.readLine()) != null) {

        Boolean value = UserPassword.containsKey(str);

        if (value == true) {
         //		System.out.println("Inside true");
         pr.println("USERID_TAKEN");
         pr.flush();
        } else {
         userID = str;

         pr.println("GIVE_PASSCODE");
         pr.flush();

         // 	System.out.println("Inside false");

         if ((str = br.readLine()) != null) {
          UserPassword.put(userID, str);
          FriendList.put(userID, new Vector < UserStatus > ());
          FriendRequestList.put(userID, new Vector < UserStatus > ());




         }

         //	System.out.println("Breaaaaaak");			
         break;


        }
       }

      }




     } else if (str.equals("BYE")) {
      System.out.println("[" + id + "] says: BYE. Worker thread will terminate now.");

      OnlineFolks.remove(id);

      break; // terminate the loop; it will terminate the thread also
     } else if (str.equals("DL")) {
      try {
       File file = new File("cat and chicken for server.jpg");
       FileInputStream fis = new FileInputStream(file);
       BufferedInputStream bis = new BufferedInputStream(fis);
       OutputStream os = socket.getOutputStream();
       byte[] contents;
       long fileLength = file.length();
       long current = 0;

       long start = System.nanoTime();
       delay(100000);

       while (current != fileLength) {
        int size = 10000;
        byte mark;

        if (fileLength - current > size) {
         current += size;
         mark = 1;
        } else {
         size = (int)(fileLength - current);
         current = fileLength;
         mark = 0;
        }
        contents = new byte[size + 1];
        bis.read(contents, 0, size);
        contents[size] = mark;
        os.write(contents);
        System.out.println("Sending file ... " + (current * 100) / fileLength + "% complete!");
       }

       os.flush();
       System.out.println("File sent successfully!");
       delay(100000);
      } catch (Exception e) {
       e.printStackTrace();
       System.err.println("Could not transfer file.");
      }
      pr.println("Downloaded.");
      pr.flush();

     } else {
      System.out.println("[" + id + "] says: " + str);
      pr.println("Got it. You sent \"" + str + "\"");
      pr.flush();
     }
    } else {
     System.out.println("[" + id + "] terminated connection. Worker thread will terminate now.");

     OnlineFolks.remove(id);
     break;
    }
   } catch (Exception e) {
    System.err.println("Problem in communicating with the client [" + id + "]. Terminating worker thread.");

    OnlineFolks.remove(id);

    break;
   }
  }

  try {
   this.is.close();
   this.os.close();
   this.socket.close();
   this.o_Stream.close();
  } catch (Exception e) {

  }

  TestServer.workerThreadCount--;
  System.out.println("Client [" + id + "] is now terminating. No. of worker threads = " + TestServer.workerThreadCount);

  OnlineFolks.remove(id);
 }

 private void delay(int p) {
  int k = 0;
  while (k < p)
   k++;
 }
}