import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class SafeInternet {

	static public int port = 8080; // default port
	static public String userInput;
	static boolean questioner=true;
	public static void main (String args[]){
		System.out.println("Welcome to Safe Internet");
		Scanner scan = new Scanner(System.in);
		
		if (args.length == 0){
		
			
			System.out.println("You can use java SafeInternet [Port Number]");
			System.out.println("Or you can enter directly port number now");
			System.out.print("Enter Port Number:");
			
			port = scan.nextInt(); 
			
			while(port < 2000){
				
				System.out.println("Port Number Should be more than 2000");
				System.out.print("Enter Port Number:");
				port = scan.nextInt(); 
				
			}
		}else{
			port = Integer.parseInt(args[0]);
		}
		
		while(port < 2000){
			
			System.out.println("Port Number Should be more than 2000");
			System.out.print("Enter Port Number:");
			port = scan.nextInt(); 
			
		}
		
		while (questioner=true){
			questioner=false;
			Scanner reader = new Scanner(System.in);  
			System.out.println("Do You Want to Start Safe Internet Proxy Server Select [Y/N] or ");
			System.out.print("Add Safe Key Words for Database Select [A]: ");
			userInput = reader.next(); 

			if(userInput.toUpperCase().equals("A")){
				File file = new File("src/database.txt");
				
				BufferedReader br2 = null;
				try {
					br2 = new BufferedReader(new FileReader(file));
				} catch (FileNotFoundException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				String line = null;
				int counter = 0;
				try {
					
					System.out.println("The list of banned words: ");
					while((line=br2.readLine())!=null){
						System.out.print(line);
						for (int i=0; i<(15 - line.length()); i++){
						System.out.print(" ");
						}
						counter = counter + 1;
						if (counter%3==0)
							System.out.println();}	
						
						
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				System.out.println();
				while(!userInput.toUpperCase().equals("D")){
					System.out.println("Enter safe word to disable or Exit D: ");
					userInput = reader.next(); 

					try {

						@SuppressWarnings("resource")
						BufferedWriter writer = new BufferedWriter(new FileWriter(file,true));
						if (!userInput.toUpperCase().equals("D")){
						writer.write(userInput.toLowerCase());
						writer.newLine();
						writer.flush();
						}
					} catch (IOException e) {e.printStackTrace();}
				}

			}else if(userInput.toUpperCase().equals("Y")){

				
				System.err.println("\n \n==> SafeInternet established on port " + port + ". Press CTRL-C to end.  \n\n");
				ProxyServer jp = new ProxyServer(port);
				jp.start();

				while (true)
				{
					try { Thread.sleep(3000); } catch (Exception e) {}
				}

			}else if(userInput.toUpperCase().equals("N")){
				System.out.println("Program is closed");
				break;
			}else{

				System.out.println("Invalid Input. Please Try Again.");
				questioner=true;

			}

		}
	}
}
