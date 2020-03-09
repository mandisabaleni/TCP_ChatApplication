import java.net.*;
import java.io.*;
import java.util.Scanner;
import javax.swing.JOptionPane;

/**
* The A_Chat_Client is a class that provides attributes and functionality for a client. It creates a socket for the client
* and sends as well as receives messages to and from the server.
* @author Sihle Mkaza Bongani Motholo Mandisa Baleni
* @since 2019-02-23
*/
public class A_Chat_Client implements Runnable
{
	Socket SOCK;
	InputStream is;	
	DataInputStream dis;
	Boolean sendingSOCK = false;
	String sfile ="";
	OutputStream os;
	DataOutputStream dos;
			
		
	public A_Chat_Client(Socket X)
	{
		this.SOCK = X;
	}

	public void run()
	{
		try
		{
			try
			{
				is = SOCK.getInputStream();
				dis = new DataInputStream(is);
				os = SOCK.getOutputStream();
				dos = new DataOutputStream(os);
				
				dos.flush();
				os.flush();
				CheckStream();
			}
			finally
			{
				//SOCK.close();
			}
		}
		catch(Exception e){System.out.println(e);}
	}
	
	/**
	* This method disconnects a client from the server. It is called when a user logs out and removes them from
	* the socket connection array and online users array then closes its socket as well as informing other users 
	* that the user has disconnected.
	* @return nothing.
	* @throws IOException
	*/
	
	public void DISCONNECT() throws IOException
	{
		dos.writeUTF("DISCONNECTING$%^");
		dos.flush();
		os.flush();
		JOptionPane.showMessageDialog(null, "You disconnected!");
		A_Chat_Client_GUI.MainWindow.dispose();
	}
	

	/**
	* This method continuously checks for incoming messages or new connections and disconnections from clients.
	* @return nothing.
	*/
	public void CheckStream()
	{
		while(true)
		{
				RECEIVE();
		}
	}

	/**
	* This method is responsible for receiving messages sent by other clients and displaying it in the receiving
	* clients conversation text area box, TA_CONVERSATION.
	* @return nothing.
	*/
	public void RECEIVE()
	{
		try{
			
			String header = dis.readUTF();
			String MESSAGE = "";
			if(!header.equals("A_FILE"))
			{
				MESSAGE = header;
				
				if(MESSAGE.contains("#?!")||header.equals("#?!"))//adding or removing a user
				{
					String TEMP1 = MESSAGE.substring(3);
						   TEMP1 = TEMP1.replace("[","");
						   TEMP1 = TEMP1.replace("]","");
						   System.out.println("this is Temp1: "+TEMP1);
					String[] CurrentUsers = TEMP1.split(", ");
					A_Chat_Client_GUI.JL_ONLINE.removeAll();
					A_Chat_Client_GUI.JL_ONLINE.setListData(CurrentUsers);
					//A_Chat_Client_GUI.JL_ONLINE.updateUI();
				}
				//if(MESSAGE.contains("DISCONNECTING$%^")){}
				if(header.equals("A_MSG"))
				{
					MESSAGE = dis.readUTF();
					A_Chat_Client_GUI.TA_CONVERSATION.append(MESSAGE + "\n");
					
				}	
			}else{
				if(sendingSOCK==false){
					int choice = A_Chat_Client_GUI.BuildReceiveWindow();
					if(choice ==0){
						RECEIVEFILE();
					}
				}
			}
		}catch(Exception e){
			System.out.println(e);
		}
	}

	/**
	* This method takes a message and displays it in the text area conversation box of all other online users.//fix
	* @param X is the message typed in by a client destined to be sent to other clients.
	* @return nothing.
	*/
	public void SEND(String X)
	{
		try{
			os = SOCK.getOutputStream();
			dos = new DataOutputStream(os);
			dos.writeUTF("A_MSG");
			dos.writeUTF(A_Chat_Client_GUI.UserName + ": " + X);
			
			dos.flush();
			os.flush();
			
			//os.close();
			//dos.close();		

			A_Chat_Client_GUI.TF_UserMessage.setText("");
		} catch(Exception e)
		{System.out.println(e);}
		
	}
	
	public void SENDFILE(String X)
	{
		try 
		{
		
			File myFile = new File(X);
			byte[] mybytearray = new byte[(int) myFile.length()];
			
			FileInputStream fis = new FileInputStream(myFile);
			BufferedInputStream bis = new BufferedInputStream(fis);
			
			DataInputStream dis = new DataInputStream(bis);
			dis.readFully(mybytearray, 0, mybytearray.length);
			
			os = SOCK.getOutputStream();
			dos = new DataOutputStream(os);
			
			dos.writeUTF("A_FILE");
			dos.writeUTF(myFile.getName());
			sfile = myFile.getName();
			dos.writeLong(mybytearray.length);
			dos.write(mybytearray, 0, mybytearray.length);
			
			dos.flush();		
			os.flush();
			fis.close();
			bis.close();
			//try closing all input streams here
			//os.close();
			//dos.close();
			A_Chat_Client_GUI.TF_UserFilePath.setText("");

		} 
		catch(Exception e) 
		{ System.out.println(e);	}
		
		
	}
	
	public void RECEIVEFILE(){
		try{
			int bytesRead;
			int current =0;
			
			String fileName = dis.readUTF();
			if(sfile == fileName){sendingSOCK=true;}
		    long size = dis.readLong();					
			byte[] bytesArray = new byte[1024];
			boolean received = false;
		
			OutputStream FILEOUT = new FileOutputStream( fileName);
			
			while((dis.available() > 0)&& size>0 &&(bytesRead = dis.read(bytesArray,0,(int)Math.min(bytesArray.length,size)))!=-1)
			{
				//if(dis.available() < 0){ }//had return statement
				FILEOUT.write(bytesArray, 0, bytesRead);
				size-=bytesRead;
				received = true;
			}
			//is.close();
			//dis.close();
			if(received==true)
			{System.out.println("Received file");}
			else{System.out.println("File Not Received");}
			FILEOUT.flush();
			FILEOUT.close();
		} catch(Exception e){System.out.println(e);}	
	}
}
