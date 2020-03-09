import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JOptionPane;
import java.io.*;
import java.net.*;

public class A_Chat_Server {
	

	public static ArrayList<Socket> ConnectionArray = new ArrayList<Socket>();
	public static ArrayList<String> CurrentUsers = new ArrayList<String>();
	public static String SERVERSTORAGEDIRECTORY;

	public static void main(String [] args) throws IOException {
		try {
			final int PORT = 12050;
			ServerSocket SERVER = new ServerSocket(PORT);
			System.out.println("Please enter desired storage location for server of received files:\n");
			Scanner scan = new Scanner(System.in);
			SERVERSTORAGEDIRECTORY = scan.nextLine();
			System.out.println("waiting for clients...");

			while(true) {
				Socket SOCK = SERVER.accept(); 

				CONNECT(SOCK);
				System.out.println("Client connection from "+ SOCK.getLocalAddress().getHostName());

				A_Chat_Server_Return CHAT = new A_Chat_Server_Return(SOCK);
				Thread X = new Thread(CHAT);
				X.start();
				
			}
		} catch (Exception e) { e.printStackTrace(); }

	}

	public static void CONNECT(Socket X) throws IOException{
		DataInputStream dis = new DataInputStream(X.getInputStream());
		String UserName = dis.readUTF();

		ConnectionArray.add(X);
		CurrentUsers.add(UserName);

		for (int i = 1; i<= A_Chat_Server.ConnectionArray.size(); i++) {
			Socket TEMP_SOCK = (Socket) A_Chat_Server.ConnectionArray.get(i-1);
			OutputStream TEMP_OS = TEMP_SOCK.getOutputStream();
			DataOutputStream TEMP_DOS = new DataOutputStream(TEMP_OS);
			TEMP_DOS.writeUTF("#?!"+ A_Chat_Server.CurrentUsers);
			TEMP_DOS.flush();
			TEMP_OS.flush();
		}
	}

	public static void DISCONNECT(Socket X) throws IOException {
		String disconnecting_user = "";
		for(int i=1;i<= A_Chat_Server.ConnectionArray.size(); i++){
			if(ConnectionArray.get(i).equals(X)) {
				//ConnectionArray.get(i).close();
				ConnectionArray.remove(i);
				disconnecting_user = CurrentUsers.get(i);
				CurrentUsers.remove(i);
				System.out.println("after disconnecting = "+CurrentUsers);
			}
		}
		for (int i = 1; i<= ConnectionArray.size(); i++) {
			Socket TEMP_SOCK = (Socket) ConnectionArray.get(i-1);
			OutputStream TEMP_OS = TEMP_SOCK.getOutputStream();
			DataOutputStream TEMP_DOS = new DataOutputStream(TEMP_OS);
			TEMP_DOS.writeUTF("#?!"+ CurrentUsers);
			//TEMP_DOS.writeUTF(disconnecting_user + " has disconnected");
			TEMP_DOS.flush();
			TEMP_OS.flush();
		}
	}
}
