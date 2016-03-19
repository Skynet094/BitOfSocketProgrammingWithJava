	import java.io.*;
import java.net.*;
import java.util.*;


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

	public class SimpleClient {

	 private static Vector < UserStatus > FriendRequestList = new Vector < UserStatus > ();
	 private static HashMap < Integer, UserStatus > OnlineFolks = new HashMap < Integer, UserStatus > ();
	 public static volatile Vector < UserStatus > FriendList = new Vector < UserStatus > ();
	 private static Socket s = null;
	 private static BufferedReader br = null;
	 private static PrintWriter pr = null;
	 private static InputStream is = null;

	 private static ObjectInputStream i_Stream = null;

	 public static void main(String args[]) throws IOException {
	  try {
	   s = new Socket("localhost", 5555);

	   br = new BufferedReader(new InputStreamReader(s.getInputStream()));
	   pr = new PrintWriter(s.getOutputStream());
	   is = s.getInputStream();
	   i_Stream = new ObjectInputStream(is); //for sharing objects

	  } catch (Exception e) {
	   System.out.println(e);
	   System.err.println("Problem in connecting with the server. Exiting main.");
	   System.exit(1);
	  }

	  Scanner input = new Scanner(System.in);
	  String strSend = null, strRecv = null;

	  try {
	   strRecv = br.readLine();
	   if (strRecv != null) {
	    System.out.println("Server says: " + strRecv);
	   } else {
	    System.err.println("Error in reading from the socket. Exiting main.");
	    cleanUp();
	    System.exit(0);
	   }
	  } catch (Exception e) {
	   System.err.println("Error in reading from the socket. Exiting main.");
	   cleanUp();
	   System.exit(0);
	  }

	  while (true) {
	   System.out.print("Enter a choice: ");
	   try {
	    strSend = input.nextLine();
	   } catch (Exception e) {
	    continue;
	   }

	   pr.println(strSend);
	   pr.flush();


	   if (strSend.equals("#")) {
	    /* LOGIN */

	    ServerClientCommunication("ENTER_USERID", "Enter UserID:", true);
	    if (ServerClientCommunication("USER_NOT_FOUND", "User not found, start from the top.", false) == false) { //user found

	     //	System.out.println("I am here now, before the password.");

	     System.out.println("Enter Password: ");
	     strSend = input.nextLine();
	     pr.println(strSend);
	     pr.flush();

	     if (ServerClientCommunication("LOGGED_IN", "You are logged in! Woo!", false) == false) //false means, password mismatch

	      System.out.println("Password mismatch happened, start from the top.");

	     else {

	      pr.println("LOGGED_IN_ACK");
	      pr.flush();

	      //here starts a new chronicle , of the logged in user 

	      System.out.println("\n\n----------------------------------Main Menu------------------------------------\n\n");
	      System.out.println("Choose an option: \n");
	      System.out.println("i) @ to view online users \n");

	      System.out.println("ii)#$ to send Friend Requests \n");
	      System.out.println("iii)@# to View Friend Requests \n");

	      System.out.println("iv)@@ to Add/Reject from Friend Requests \n");

	      System.out.println("v)## to View Friend list \n");
	      System.out.println("vi)chat to Chat with a friend\n");

	      System.out.println("vii) Q to logout\n");
	      //System.out.println("Main Menu: ");
	      //System.out.println("Main Menu: ");

	      while (true) {


	       System.out.print("Enter a choice: ");
	       try {
	        strSend = input.nextLine();
	       } catch (Exception e) {
	        continue;
	       }

	       pr.println(strSend);
	       pr.flush();

	       System.out.println("I was here with this value: " + strSend);

	       String str = null;
	       
	       
	       if(strSend.equals("file_transfer")){
	    	   
	    	   //do you wanna receive or share? 
	    	   
		    	   System.out.print("Press S to send, Press R to receive.");
			       try {
			        strSend = input.nextLine();
			       } catch (Exception e) {
			        continue;
			       }
			       
			       pr.println(strSend);
			       pr.flush();              //S or R jacce 
			       String test[]=null;
			     
			       if(strSend.equals("S")){
			    	   
			    	   System.out.println("Press userID,fileID.");
				          
			    	   try {
			    		   
					        strSend = input.nextLine(); //send userID and file ID
					       } catch (Exception e) {
					        continue;
					       }

			    	   	System.out.println("Before split:" +strSend);
					       test= strSend.split(",");
					       System.out.println(test[0] + " " + test[1]);
					       pr.println(strSend);
					       pr.flush();
					       
			    	   
			       }
			       
			       //System.out.println("I am: "+ test[0] + "");
			       
			       if(((str=br.readLine())!=null)){ //Get S_OK here
			    	
			    	   System.out.println("I am here with: " + str);
			    	   
			        if(strSend.equals("R")){
			        	

				    	   System.out.println("This should work");
				    	   
				    
	    		    try {
	    		     byte[] contents = new byte[10001];
	    		    
	    		     File myFile = new File("File.txt");
	    		     if(!myFile.exists()) {
	    		    	 
	    		         myFile.createNewFile();
	    		     } 
	    		     
	    		     if(!myFile.exists()){
	    		    	 
	    		    	 System.out.println("It did not work");
	    		     }
	    		     
	    		     else {
	    		    	 System.out.println("It did  work");
	    		     }
	    		     pr.println("R_OK");
	    		     pr.flush();

	    		     FileOutputStream fos = new FileOutputStream(myFile, false);
	    		     BufferedOutputStream bos = new BufferedOutputStream(fos);
	    		     InputStream is = s.getInputStream();

	    		     int bytesRead = 0;

	    		     while ((bytesRead = is.read(contents)) != -1) {
	    		      bos.write(contents, 0, bytesRead - 1); //write n bytes, 0 to n-1 	// source of data, source offset, length 
	    		      byte mark = contents[bytesRead - 1]; //nth byte has 0 or 1 , will use 0 if I want to terminate 
	    		      if (mark == 0) //This is a protocol baby , lame but still a protocol. 
	    		       break;
	    		      //System.out.println("bos is busy , mark = "+mark);
	    		     }

	    		     bos.flush();
	    		     //System.out.println("bos flushed");
	    		    } catch (Exception e) {
	    		     e.printStackTrace();
	    		     System.err.println("Could not transfer file.");
	    		    }

	    		   
			       }
			        
			       }
	    	   
	       }

	       else if (strSend.equals("chat")) {
	        
	    	   
	    	   System.out.println("First Enter a user then write messages, type BYE to end chat ");
	        
	        if ((str = br.readLine()) != null) {

		          System.out.println(str);
		         }
	        
	        
	    	//send friend's ID
	        
	         try {
	          strSend = input.nextLine();
	         } catch (Exception e) {
	          continue;
	         }
	         
	       pr.println(strSend);
	       pr.flush();

	       

	        
	        while (true) {
	        	
	         
	          try {
		          strSend = input.nextLine();
		          System.out.println("Send this to server "+ strSend);
		         } catch (Exception e) {
		          continue;
		         }

		       pr.println(strSend);
		       pr.flush();
		       
		       if ((str = br.readLine()) != null) {
		    	   System.out.println("is this ever reached?");
		          System.out.println(str);	          		        
		         }


}
	        
	       }

	        if (strSend.equals("##")) {


	        System.out.println("I am in the friend list");

	        try {

	         FriendList = (Vector < UserStatus > ) i_Stream.readUnshared();

		        System.out.println("I read Friend list!!!");
		        
	        } catch (ClassNotFoundException e) {
	         // TODO Auto-generated catch block
	         System.out.println("I WAS CAUGHT");

	         e.printStackTrace();
	        }


	        System.out.println("\n\n-----------------Friend List------------------\n\n");

	        if (FriendList == null) {
	         System.out.println("No friends.");

	        } else {
	         for (int i = 0; i < FriendList.size(); i++) {

	          System.out.println(FriendList.elementAt(i).userName);

	         }
	        }

	        System.out.println("\n\n------------------------------------------------------\n\n");





	       } else if (strSend.equals("@@")) { //Accept or reject 

	        System.out.println("\nEnter i) UserID, A to accept \nii)UserID, R to reject. Press X to quit from this.\n");


	        while (true) { //SEND FRIEND REQUESTS!!


	         pr.println(strSend);
	         pr.flush();

	         try {
	          strSend = input.nextLine();
	         } catch (Exception e) {
	          continue;
	         }

	         if (strSend.equals("X")) {
	          break;
	         }

	        }



	       } else if (strSend.equals("@#")) { //View Friend Request List



	        try {
	         System.out.println("I AM HERE");
	         FriendRequestList = (Vector < UserStatus > ) i_Stream.readUnshared();
	        } catch (ClassNotFoundException e) {
	         // TODO Auto-generated catch block
	         //System.out.println("I WAS CAUGHT");

	         e.printStackTrace();
	        }


	        System.out.println("\n\n-----------------Friend Requests------------------\n\n");

	        if (FriendRequestList == null) {
	         System.out.println("No friend request");

	        } else {
	         for (int i = 0; i < FriendRequestList.size(); i++) {

	          System.out.println(FriendRequestList.elementAt(i).userName);

	         }
	        }

	        System.out.println("\n\n------------------------------------------------------\n\n");



	       } else if (strSend.equals("#$")) {


	        System.out.println("To Send Friend requests, press user name, press X to stop.\n\n");

	        while (true) { //SEND FRIEND REQUESTS!!


	         pr.println(strSend);
	         pr.flush();

	         try {
	          strSend = input.nextLine();
	         } catch (Exception e) {
	          continue;
	         }

	         if (strSend.equals("X")) {
	          break;
	         }



	        }

	       } else if (strSend.equals("@")) { //show online users!




	        try {
	         OnlineFolks = (HashMap < Integer, UserStatus > ) i_Stream.readUnshared();
	        } catch (ClassNotFoundException e) {
	         // TODO Auto-generated catch block
	         e.printStackTrace();
	        }




	        Iterator < Integer > keySetIterator = OnlineFolks.keySet().iterator();

	        System.out.println("Number of people online: " + OnlineFolks.size());

	        if (OnlineFolks.size() > 0) {

	         System.out.println("Online people:-");

	        }

	        while (keySetIterator.hasNext())

	        {
	         Integer key = keySetIterator.next();
	         UserStatus obj = OnlineFolks.get(key);

	         if (obj.status == true) {
	          System.out.println("User: " + obj.userName);
	         }

	        }



	       } else if (strSend.equals("Q")) {
	        System.out.println("\n\n----------------------------------Logged out------------------------------------\n\n");
	        break;
	       }
	      }
	     }
	    }



	   } else if (strSend.equals("$")) {


	    String str = null;
	    ServerClientCommunication("ENTER_USERID", "Enter UserID: ", true);

	    while (true) {
	     if ((str = br.readLine()) != null) {

	      if (str.equals("USERID_TAKEN")) {

	       System.out.println("User id taken, please try another userID");
	       strSend = input.nextLine();
	       pr.println(strSend);
	       pr.flush();
	      } else if (str.equals("GIVE_PASSCODE")) {
	       System.out.println("Enter password: ");
	       strSend = input.nextLine();
	       pr.println(strSend);
	       pr.flush();
	       break;
	      }
	     }

	    }
	    System.out.println("Sign up completed.");

	   } else if (strSend.equals("BYE")) {
	    System.out.println("Client wishes to terminate the connection. Exiting main.");
	    break;
	   }


	  }

	  cleanUp();
	 }


	 private static Boolean ServerClientCommunication(String from_server, String to_Client, Boolean Flag) {


	  //System.out.println("I got inside the funky function");
	  Scanner input = new Scanner(System.in);
	  String strSend = null, strRecv = null;
	  Boolean flag_A = false;

	  try {
	   strRecv = br.readLine();
	   //System.out.println(" I read this-> strRecv: " + strRecv);
	   if (strRecv != null) {

	    if (strRecv.equals(from_server)) {
	     System.out.println(to_Client);
	     flag_A = true;
	     if (Flag) {
	      try {
	       strSend = input.nextLine(); //give userID
	       pr.println(strSend);
	       pr.flush();

	      } catch (Exception e) {}
	     }

	    }


	   } else {
	    System.err.println("Error in reading from the socket. Exiting main.");
	    cleanUp();
	    System.exit(0);
	   }
	  } catch (Exception e) {
	   System.err.println("Error in reading from the socket. Exiting main.");
	   cleanUp();
	   System.exit(0);
	  }

	  //    	System.out.println("Returning this value: " + flag_A);
	  return flag_A; //this means, whether server sent what was expected

	 }

	 private static void cleanUp() {
	  try {
	   br.close();
	   pr.close();
	   s.close();
	   i_Stream.close();
	  } catch (Exception e) {

	  }
	 }
	}