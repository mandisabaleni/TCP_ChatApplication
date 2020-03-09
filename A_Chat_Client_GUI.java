import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.*;
import java.net.*;
import java.util.*;

/**
* This class manages the interface for a user in the chat application. It provides basic componets for user 
* functionality including containers needed to type in user names and messages and files as well as buttons 
* for logging in and out. The class is also responsible for providing dialog boxes to assist user functionality 
* for loggin in and out and sending file.
* @author Sihle Mkaza Bongani Motholo Mandisa Baleni
* @version 1.0
* @since 2019-02-23
*/

public class A_Chat_Client_GUI {
	
	private static A_Chat_Client ChatClient;
	public static String UserName;

	//Main Student Chat Window
	public static JFrame MainWindow = new JFrame();
	private static JButton B_LOGIN = new JButton();
	private static JButton B_DISCONNECT = new JButton();
	private static JButton B_SEND = new JButton();
	private static JButton B_SENDFILE = new JButton();
	private static JLabel L_Message = new JLabel("Type");
	public static JTextField TF_Username = new JTextField(20);
	public static JTextField TF_UserMessage = new JTextField(20);
	public static JTextField TF_UserFilePath = new JTextField(20);
	private static JLabel L_Conversation = new JLabel();
	public static JTextArea TA_CONVERSATION = new JTextArea();
	private static JScrollPane SP_CONVERSATION = new JScrollPane();
	private static JLabel L_ONLINE = new JLabel();
	public static  JList JL_ONLINE = new JList();
	private static JScrollPane SP_ONLINE = new JScrollPane();
	
	//file sending GUI window
	public static JFrame fileReceiveWindow = new JFrame();
	public static JButton B_AcceptFile = new JButton();
	public static JButton B_DeclineFile = new JButton();
	public static JLabel L_fileLabel = new JLabel();
	public static JButton B_CHOOSE = new JButton();//new
		//NEW
	public static JFileChooser fileChooser = new JFileChooser();

	
	/**
	* This method starts up the main interface window for the client.
	* @return nothing
	*/
	public static void main(String [] args) {
		BuildMainWindow();
		Initialize();
	}

	/**
	* This method establishes a connection with the server by creating a new socket with the server's IP address 
	* and port number. It creates a new ChatClient object and creates a new thread for it and their entered user
	* name is added to the Users list of the application.
	* @return nothing1
	* @throws Exception on server not responding
	*/
	public static void Connect() {
		try {
			final int PORT = 12050;
			System.out.println("Enter IP: \n");
			Scanner in = new Scanner(System.in);
		    final String HOST = in.nextLine();
			Socket SOCK = new Socket(HOST, PORT);
			System.out.println("You connected to: "+HOST);

			ChatClient = new A_Chat_Client(SOCK);
			//send name to add to online list
			OutputStream os = SOCK.getOutputStream();
			DataOutputStream dos = new DataOutputStream(os);
			dos.writeUTF(UserName);
			String [] temp_array = new String[A_Chat_Server.CurrentUsers.size()];
			JL_ONLINE.setListData(A_Chat_Server.CurrentUsers.toArray(temp_array));
			//remove
			System.out.println("username entered by user has been written to its output stream");

			dos.flush();
			os.flush();
			
			//os.close();
			//dos.close();

			Thread X = new Thread(ChatClient);
			X.start();
			
		} catch(Exception e) { 
			System.out.print(e);
		 	System.out.println("Server not responding");
			System.exit(0);	
		}
	}

	/**
	* This method initializes the login button to be enabled but initializes the logout send and send file buttons
	* to be disenabled.
	* @return nothing
	*/
	public static void Initialize() {
		B_SEND.setEnabled(true);
		B_SENDFILE.setEnabled(true);
		B_DISCONNECT.setEnabled(true);
		B_LOGIN.setEnabled(true);
	}	

	/**
	 * BuildMainWindow method sets the GUI structure and appearance
	 * @return nothing
	 */ 
	public static void BuildMainWindow() {
		MainWindow.setTitle("Student Chat");
		MainWindow.setSize(450,500);
		MainWindow.setLocation(220, 180);
		MainWindow.setResizable(false);
		ConfigureMainWindow();
		MainWindow_Action();
		MainWindow.setVisible(true);

	}
	
	//NEW
	public static int BuildReceiveWindow()
	{
		
		Object[] options = {"Accept",
                    "Decline"};
		int n = JOptionPane.showOptionDialog(MainWindow,
		"User wants to send you a file " + "4" + "KB",
		"Incoming File",
		JOptionPane.YES_NO_OPTION,
		JOptionPane.QUESTION_MESSAGE,
		null,     //do not use a custom Icon
		options,  //the titles of buttons
		options[0]); //default button title
		
		return n;
	}

	public static void ConfigureMainWindow() {
		MainWindow.getContentPane().add(B_SEND);
		MainWindow.getContentPane().add(B_SENDFILE);
		MainWindow.getContentPane().add(B_CHOOSE);
		MainWindow.getContentPane().add(B_DISCONNECT);
		MainWindow.getContentPane().add(B_LOGIN);
		MainWindow.getContentPane().add(TF_Username);
		MainWindow.getContentPane().add(TF_UserMessage);
		MainWindow.getContentPane().add(TF_UserFilePath);
		MainWindow.getContentPane().add(L_Conversation);
		MainWindow.getContentPane().add(SP_CONVERSATION);
		MainWindow.getContentPane().add(L_ONLINE);
		MainWindow.getContentPane().add(SP_ONLINE);

		MainWindow.setBackground(new java.awt.Color(255,255,255));
		MainWindow.setSize(500,400);
		MainWindow.getContentPane().setLayout(null);

		//message send button
		//B_SEND.setBackground(new java.awt.Color(64,64,64));
		//B_SEND.setForeground(new java.awt.Color(255,255,255));
		B_SEND.setText("send");
		B_SEND.setBounds(259,250,81,25);
		B_SEND.setEnabled(true);
		
		//file send button

		B_SENDFILE.setText("send file");
		B_SENDFILE.setBounds(362,300,100,25);
		B_SENDFILE.setEnabled(true);


		B_CHOOSE.setText("select");
		B_CHOOSE.setBounds(259,300,100,25);

		//logout button

		B_DISCONNECT.setText("logout");
		B_DISCONNECT.setBounds(360,4,80,25);

		//login button

		B_LOGIN.setText("login");
		B_LOGIN.setBounds(280,4,70,25);

		//Username textfield 
		TF_Username.setForeground(new java.awt.Color(0,0,0));
		TF_Username.requestFocus();
		TF_Username.setVisible(true);

		TF_Username.setBounds(130,4,140,30);
		
		//user/client message textfield
		TF_UserMessage.setForeground(new java.awt.Color(0,0,0));
		TF_UserMessage.requestFocus();
		TF_UserMessage.setVisible(true);

		TF_UserMessage.setBounds(10,250,249,26);
		
		//filepath textfield
	    	TF_UserFilePath.setForeground(new java.awt.Color(0,0,0));
		TF_UserFilePath.requestFocus();
		TF_UserFilePath.setVisible(true);

		TF_UserFilePath.setBounds(10,300,249,26);

		//conversation label
		L_Conversation.setHorizontalAlignment(SwingConstants.CENTER);
		L_Conversation.setText("Chat Box");

		L_Conversation.setBounds(100,50,140,16);

		//conversation text area
		TA_CONVERSATION.setColumns(20);
		TA_CONVERSATION.setFont(new java.awt.Font("Tshoma", 0, 12));
		TA_CONVERSATION.setForeground(new java.awt.Color(0,0,0));
		TA_CONVERSATION.setLineWrap(true);
		TA_CONVERSATION.setRows(5);
		TA_CONVERSATION.setEditable(false);

		//conversation scrollpane holds the conversation text area
		SP_CONVERSATION.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		SP_CONVERSATION.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		SP_CONVERSATION.setViewportView(TA_CONVERSATION);

		SP_CONVERSATION.setBounds(10, 70, 330, 180);

		//online users label
		L_ONLINE.setHorizontalAlignment(SwingConstants.CENTER);
		L_ONLINE.setText("Currently Online");
		L_ONLINE.setToolTipText("");

		L_ONLINE.setBounds(350, 50, 130, 16);

		//list of online users
		String[] TestNames = new String[20];
		JL_ONLINE.setForeground(new java.awt.Color(0,0,255));
		JL_ONLINE.setListData(TestNames);

		//online users scrollpane holds the list of users
		SP_ONLINE.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		SP_ONLINE.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		SP_ONLINE.setViewportView(JL_ONLINE);

		SP_ONLINE.setBounds(350, 70, 130, 180);
		
	
	}

	/**
	*Login Action sets the action listener for the login button
	*@return nothing
	*/
	public static void Login_Action() {
		B_LOGIN.addActionListener( new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{ ACTION_LOGIN();}
		});
	}
	
	/**
	*Action login adds the user to the list of current users and turns on the sending messages and files buttons
	*@return nothing
	*/
	public static void ACTION_LOGIN() {
		if (!TF_Username.getText().equals("")) {
			UserName = TF_Username.getText().trim();
			A_Chat_Server.CurrentUsers.add(UserName);
			//remove
			System.out.println("user name added to server...");
			B_SEND.setEnabled(true);
			B_SENDFILE.setEnabled(true);
			B_DISCONNECT.setEnabled(true);
			B_LOGIN.setEnabled(false);
			TF_Username.setDisabledTextColor(Color.lightGray);
			TF_Username.setEditable(false);
			Connect();
			
		}
		else {
			JOptionPane.showMessageDialog(null, "Please enter a name!");
		}
	}
	
	/**
	 * MainWindow action sets the functionalites of what happens when the buttons in the GUI are pressed
	 * @return nothing
	 */ 

	public static void MainWindow_Action() {
		B_SEND.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{ ACTION_B_SEND(); }
		});
		
		B_SENDFILE.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{ ACTION_B_SENDFILE(); }
		});

		B_DISCONNECT.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt){
				ACTION_B_DISCONNECT();

			}
		});

		B_LOGIN.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt){
				ACTION_LOGIN();
			}
		});
		
		//NEW
		B_CHOOSE.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt){
				choose();
			}
		});


	}

	/**
	*Action_B_SEND gets the message from the textfield and clears the textfield for a new message to be written
	*@return nothing
	*/
	public static void ACTION_B_SEND(){
		if (!TF_Username.getText().equals("")) {
			ChatClient.SEND(TF_UserMessage.getText());
			TF_UserMessage.requestFocus();
		}	
	}
	
	public static void ACTION_B_SENDFILE(){
		if (!TF_UserFilePath.getText().equals("")) {
			ChatClient.SENDFILE(TF_UserFilePath.getText());
			TF_UserFilePath.requestFocus();
		}	
	}

	/**
	*Action_B_DISCONNECT removes the clients socket by calling on the DISCONNECT method found in A_Chat_Client.java
	*@return nothing
	*/
	public static void ACTION_B_DISCONNECT() {
		try {
			ChatClient.DISCONNECT();
		} catch (Exception Y){ Y.printStackTrace();}

	}
	
	//new
	public static void choose() {
		if (fileChooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION)
		{
			File selectedFile = fileChooser.getSelectedFile();
			String path = selectedFile.getAbsolutePath();
			TF_UserFilePath.setText(path);
		}
	}


	
}
