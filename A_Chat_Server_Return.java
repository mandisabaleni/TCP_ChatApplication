import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
* @author Sihle Mkaza Bongani Motholo Mandisa Baleni
* @since 2019-02-23
*/

public class A_Chat_Server_Return implements Runnable {
	
	Socket SOCK;
	Socket sendingSOCK;
	InputStream is;//
	DataInputStream dis;
	OutputStream os;
	DataOutputStream dos;
	String MESSAGE = "";

	public A_Chat_Server_Return(Socket X) {
		this.SOCK = X;
	}

	public void run() {
		try {

				is = SOCK.getInputStream();
			    dis = new DataInputStream(is);
				os = SOCK.getOutputStream();
				dos = new DataOutputStream(os);

				while (true)
				{
					String header = dis.readUTF();

					if (header.equals("")){
						return;
					}
					if(header.equals("DISCONNECTING$%^")){//
						A_Chat_Server.DISCONNECT(SOCK);
					}
					if(header.equals("A_MSG")){
						MESSAGE = dis.readUTF();
						SEND(MESSAGE);
					}
					if(header.equals("A_FILE"))
						SENDFILE();
			    }
		}catch (Exception e) { System.out.println(e); }
	}

	public void SEND(String MESSAGE) throws IOException{
		System.out.println("Client said: "+MESSAGE);

		for(int i=1; i<=A_Chat_Server.ConnectionArray.size(); i++){
			Socket TEMP_SOCK = (Socket) A_Chat_Server.ConnectionArray.get(i-1);
			if (TEMP_SOCK.isClosed()==false)
			{
				OutputStream TEMP_OS = TEMP_SOCK.getOutputStream();
				DataOutputStream TEMP_DOS = new DataOutputStream(TEMP_OS);
				TEMP_DOS.writeUTF("A_MSG");
				TEMP_DOS.writeUTF(MESSAGE);

				TEMP_DOS.flush();
				TEMP_OS.flush();

				System.out.println("Sent to: "+TEMP_SOCK.getLocalAddress().getHostName());
			} else {continue;}
		}
	}
	public void SENDFILE(){
		try {
			//RECEIVE
			int bytesRead;
			int current = 0;

			String fileName = dis.readUTF();
			long size = dis.readLong();
			byte[] bytesArray = new byte[1024];
			boolean received = false;

			OutputStream FILEOUT = new FileOutputStream(A_Chat_Server.SERVERSTORAGEDIRECTORY+fileName);

			while ((dis.available() > 0) && size > 0 && (bytesRead = dis.read(bytesArray, 0, (int) Math.min(bytesArray.length, size))) != -1) {
				/*if (dis.available() < 0) { //inside loop was simple return statement;
					FILEOUT.write(bytesArray, 0, bytesRead);
					size -= bytesRead;
					received = true;
					FILEOUT.flush();
					FILEOUT.close();
					break;
				}*/

				 FILEOUT.write(bytesArray, 0, bytesRead);
				 size -= bytesRead;
				 received = true;

			}
			//is.close();
			//dis.close();
			if (received)
				System.out.println("Received file");
			else
				System.out.println("File Not Received");
			FILEOUT.flush();
			FILEOUT.close();

			//SENDOUT
			File myFile = new File(A_Chat_Server.SERVERSTORAGEDIRECTORY+fileName);//must change
			byte[] mybytearray = new byte[(int) myFile.length()];
			FileInputStream fis = new FileInputStream(myFile);
			BufferedInputStream bis = new BufferedInputStream(fis);

			dis = new DataInputStream(bis);
			dis.readFully(mybytearray, 0, mybytearray.length);

			for (int i = 1; i <= A_Chat_Server.ConnectionArray.size(); i++) {
				Socket TEMP_SOCK = (Socket) A_Chat_Server.ConnectionArray.get(i - 1);
				System.out.println("sendingSOCK "+TEMP_SOCK.equals(this.SOCK));
				if (TEMP_SOCK.isClosed() == false || (!TEMP_SOCK.equals(this.SOCK))) {

					OutputStream TEMP_OS = TEMP_SOCK.getOutputStream();
					DataOutputStream TEMP_DOS = new DataOutputStream(TEMP_OS);

					TEMP_DOS.writeUTF("A_FILE");
					TEMP_DOS.writeUTF(myFile.getName());
					TEMP_DOS.writeLong(mybytearray.length);
					TEMP_DOS.write(mybytearray, 0, mybytearray.length);

					TEMP_DOS.flush();
					TEMP_OS.flush();

					System.out.println("Sent file to: " + TEMP_SOCK.getLocalAddress().getHostName());
				} else {
					continue;
				}
			}
		}catch (Exception e) { System.out.println(e); }
	}
}
