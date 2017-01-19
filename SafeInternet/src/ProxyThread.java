import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

class ProxyThread extends Thread
{
	private Socket pSocket;
	private String forbidenWord = "";
	private String hostFullAdres;
	private String ipAddress;
	private String date;
	private BufferedOutputStream ClientOutput;
	private BufferedInputStream ClientInput;
	private boolean serverconnected=true;
	private ReplyHdr replyHdr;

	public ProxyThread(Socket s){
		pSocket = s;
	}

	public void run(){

		try{
			replyHdr = new ReplyHdr();
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");	//This part sets date with current date and print it to command window.
			Date date2 = new Date();												//Date line will be kept in the logfile.txt.
			date = dateFormat.format(date2);

			InetAddress IP=InetAddress.getLocalHost();								//It gets the IP number of client. This line will be kept in the logfile.txt
			ipAddress = (IP.getHostAddress());

			long timetostart = System.currentTimeMillis();

			ClientInput = new BufferedInputStream(pSocket.getInputStream());
			ClientOutput = new BufferedOutputStream(pSocket.getOutputStream());
			StringBuffer host = new StringBuffer("");

			byte[] request = getHttp(ClientInput, host, "request");   				//This line defines request as a byte.
			int Length_of_request = Array.getLength(request);          				//This line gets legth of request

			hostFullAdres = host.toString();						    			//get hostFullAdres as host + port number (such as www.google.com:443)	
			int posport = hostFullAdres.indexOf(":");								//it returns the position of ":" in host name	
			int hostPort = 80;														//default port number for http is 80.

			if (posport > 0){														//If there is a :, then apply the procedure in if.
				hostPort = Integer.parseInt(hostFullAdres.substring(posport + 1)); 	//Change hostport to port number written in the hostFullAdres after :
				hostFullAdres = hostFullAdres.substring(0, posport);               	//Remove port number from hostFullAdres.
			}

			Socket server = null;
			try {
				server = new Socket(hostFullAdres, hostPort);						// Socket is tried to be created.
			}catch(Exception e){

				String errMsg = replyHdr.formServerNotFound();
				ClientOutput.write(errMsg.getBytes(), 0, errMsg.length());

				serverconnected=false;
			}

			if(filter (hostFullAdres) == false){									// If input contains any banned words, do the statement in this if.
				String errMsg = replyHdr.formForbidden();
				ClientOutput.write(errMsg.getBytes(), 0, errMsg.length());
			}

			byte[] response = null; 												//This line defines responce as a byte since it may contains images. 	
			int Length_of_Response = 0;    											//Since response can only be null in this location, Length_of_response can only be 0.

			if (serverconnected){

				server.setSoTimeout(10000000);				
				BufferedInputStream servin = new BufferedInputStream(server.getInputStream());
				BufferedOutputStream servout = new BufferedOutputStream(server.getOutputStream());

				servout.write(request, 0, Length_of_request);						// BufferedOutputStream is used to send request to Internet server.
				servout.flush();

				StringBuffer sb = new StringBuffer(""); 				
				response = getHttp(servin,sb, "response");							// BufferedInputStream is used to get response.
				Length_of_Response = Array.getLength(response);

				servin.close();
				servout.close();
			}

			if(filter(hostFullAdres)){
				ClientOutput.write(response, 0, Length_of_Response);
			}

			long endTime = System.currentTimeMillis();

			System.out.println(".....................................................");				//It prints some data to command prompt.
			System.out.flush();
			System.out.println("IP ADRESS OF USER:       " + ipAddress);
			System.out.flush();
			System.out.println("HOST NAME:               " + hostFullAdres + ":" + hostPort );
			System.out.flush();

			if(filter(hostFullAdres)){
				System.out.println("DELAY:                   " + Long.toString(endTime - timetostart));
			}else{
				System.out.println("BANNED WORDS:            " + forbidenWord);
			}			
			System.out.flush();

			File file = new File("logFile.txt");
			BufferedWriter writer = new BufferedWriter(new FileWriter(file,true));						
			//It writes necessary data to logFile.txt

			writer.write(".....................................................");
			writer.newLine();
			writer.write("IP ADDRESS OF USER: " + ipAddress +  "---- Date: " + date);
			writer.newLine();

			writer.write(new String(request));
			writer.newLine();

			if(filter(hostFullAdres)){
				writer.write("Delay: " + Long.toString(endTime - timetostart));
				writer.newLine();
			}else{
				writer.write("Banned Word: " + forbidenWord);
				writer.newLine();
			}

			writer.write(".....................................................");
			writer.flush();

			writer.close();

			ClientOutput.close();
			ClientInput.close();
			pSocket.close();
		}  catch (Exception e)  {}

	}

	private byte[] getHttp (InputStream i, StringBuffer host, String type){
		ByteArrayOutputStream bs = new ByteArrayOutputStream();				//It creates a ByteArrayOutputStream and calls streamHttp.
		forwardHttp(i, bs, host, type);
		return bs.toByteArray();
	}

	private String readLine (InputStream ClientInputt)						
	//In this part, readLine method was defined to read input come from the client.
	{																		//This method is used in getHttp method to read Client Input	
		StringBuffer stringbuffer = new StringBuffer("");					// First, a string buffer was defined to write data read in inputstream.
		int k=0;
		try{
			ClientInputt.mark(1);											//Here, if the first character was checked. 
			if (ClientInputt.read() == -1){									//If ClientInputt is null, ClientInputt.read() returns -1.
				return null;				
			}					
			else{
				ClientInputt.reset();										//It reset the ClientInputt to remove mark.
			}																


			while ((k = ClientInputt.read()) >= 0){  			  			// After being sure that ClientInputt is not null, Each characters are checked.
				if ((k == 10) || (k == 13))									// If there is /n (10 as a char) or /r (13 as a char), it stops writing. 
					break;	
				else
					stringbuffer.append((char)k);							// it append every character to stringbuffer.
			}

			if (k == 13){													// In windows, to jump to new line, "\r\n" can be used.
				ClientInputt.mark(1);										// This if statement check if the last character read in while is /r or not. 
				if (ClientInputt.read() != 10){								// If it is /r, it checks if next character is /n or not.
					ClientInputt.reset();							
				}
			}
		}  catch (Exception e)  {}

		return stringbuffer.toString();										// It transforms its String Buffer to string and returns it 
	}

	private int forwardHttp (InputStream input, OutputStream output,StringBuffer host, String type){

		StringBuffer stbf = new StringBuffer("");
		String string = "";
		int cLength=0,portpos=0,byteCount = 0;

		try{
			while ((string = readLine(input)) != null){						//This while loop gets the new line untill the end of file. 
				// This while loop look for host name and length of the content in each lines. If it finds, it saves it to an integer.
				// Also, this loop stores all lines read to a string buffer.
				if (string.length() == 0)									//If the length of the line is 0, it breaks.
					break;												
				stbf.append(string + "\r\n");								//else it writes line to stringbuffer and add /r/n to get newline.

				portpos = string.toLowerCase().indexOf("content-length:");				//	Then, the program looks for content-length:
				if (portpos >= 0){														//If there is, it appends the string after portpos+15 (Since first 14 character is "content-length:") to cLength
					cLength = Integer.parseInt(string.substring(portpos + 15).trim());	
				}
				portpos = string.toLowerCase().indexOf("host:");			//It finds the place "host:" if there exist. If it doesn't exist, it return -1. 
				if (portpos >= 0){											//If there is, it gets host data from it.
					host.setLength(0);										//To do this, it set the length of the hos is zero.
					host.append(string.substring(portpos + 5).trim());		//Then it appends the strign after portpos+5 (Since first 4 character is "Host:") to Host
				}
			}
			stbf.append("\r\n");													
			output.write(stbf.toString().getBytes(), 0, stbf.length());				
			// It prints the data to ByteArrayOutputStream namet as output
			// Since its unit is byte, string buffer was trasnformed to byte[] form before printing.

			if ((cLength > 0)){											// It transfer response to outputstream if there is a content if there is a clength ana wait is true.
				try {
					byte[] buf = new byte[1000];
					int bytesIn = 0;
					while ( ((byteCount < cLength) && ((bytesIn = input.read(buf)) >= 0))) { 
						//If I can still get data from input and all character in content wasn't written yet, procedure in while loop goes on to work. 
						output.write(buf, 0, bytesIn);
						byteCount += bytesIn;
					}

				}catch (Exception e) {

					String errMsg = replyHdr.formNotFound();
					ClientOutput.write(errMsg.getBytes(), 0, errMsg.length());
					}
				}
			if ((cLength < 0) && type.equals("response")){
				byte[] buf = new byte[1000];
				int bytesIn = 0;
				while ( ((byteCount < cLength) && ((bytesIn = input.read(buf)) >= 0))) { 
					//If I can still get data from input and all character in content wasn't written yet, procedure in while loop goes on to work. 
					output.write(buf, 0, bytesIn);
					byteCount += bytesIn;
				}
			}
		}catch (Exception e){}

		try  {output.flush();}  catch (Exception e)  {}
		return (stbf.length() + byteCount);
	}

	public boolean filter(String s){
		boolean a = true;
		try {			
			BufferedReader br1 = new BufferedReader(new FileReader(("database.txt")));			//It opens the database.txt file
			String line = null;
			String Printer= "";
			int counter = 0;
			try {
				s=s.toLowerCase();
				while((line=br1.readLine())!=null){												// It search if input contains the word in the line read.
					if(s.contains(line)){														// It repeats this statement till it search for each line.
						a= false;
						Printer = Printer + line;											// It stores all banned words input contains in a string to be able to print later.
						if (counter != 0){
							Printer = Printer + ", ";}
						counter=counter + 1;
					} 
				}
				if(counter!=0){
					forbidenWord = Printer;
				} 

			} catch (IOException e) {
				e.printStackTrace();
			}



		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		return a;
	}

}