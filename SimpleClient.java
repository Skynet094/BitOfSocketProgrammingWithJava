
import java.io.*;
import java.net.*;
import java.util.*;

public class SimpleClient {

    private static Socket s = null;
    private static BufferedReader br = null;
    private static PrintWriter pr = null;

    public static void main(String args[]) {
        try {
            s = new Socket("localhost", 5555);

            br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            pr = new PrintWriter(s.getOutputStream());
        } catch (Exception e) {
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
            System.out.print("Enter a string: ");
            try {
                strSend = input.nextLine();
            } catch (Exception e) {
                continue;
            }

            pr.println(strSend);
            pr.flush();
            if (strSend.equals("BYE")) {
                System.out.println("Client wishes to terminate the connection. Exiting main.");
                break;
            }
            if (strSend.equals("DL")) {
                try {
                    byte[] contents = new byte[10001];

                    FileOutputStream fos = new FileOutputStream("cat and chicken for client.jpg");
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    InputStream is = s.getInputStream();

                    int bytesRead = 0;

                    while ((bytesRead = is.read(contents)) != -1) {
                        bos.write(contents, 0, bytesRead-1); //write n bytes, 0 to n-1 	// source of data, source offset, length 
                        byte mark=contents[bytesRead-1];  //nth byte has 0 or 1 , will use 0 if I want to terminate 
                        if(mark==0) //This is a protocol baby , lame but still a protocol. 
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

        cleanUp();
    }

    private static void cleanUp() {
        try {
            br.close();
            pr.close();
            s.close();
        } catch (Exception e) {

        }
    }
}
