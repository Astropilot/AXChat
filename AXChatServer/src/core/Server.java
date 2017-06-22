package core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import modele.Message;

public class Server {
	
	private static final int PORT = 1555;
	private static ServerSocket socket;
	
	private static HashMap<ServerGestion, String> listClients;
	private static List<Thread> listThread;
	
	public static void main(String args[]) {
		
		listClients = new HashMap<ServerGestion, String>();
		listThread = new ArrayList<Thread>();
		
		System.out.println("Demarrage du serveur sur le port " + PORT);
		
        try {
        	socket = new ServerSocket(PORT);
        	Thread t = new Thread(new Accepter_clients(socket, listClients, listThread));
        	t.start();
        	System.out.println("[INFO] Serveur READY !");
        	} catch (IOException e) {
        		System.out.println("[ERROR] Le port " + PORT + " est déjà utilisé !");
        	}
        
        Scanner sc = new Scanner(System.in);
        
        while(true) {
        	String command = sc.nextLine();
        	if(command.equals("exit")) {
        		exitServer();
        	}
        		
        }
    }

	private static void exitServer() {
		System.out.println("Trying to stop the server...");
		for(ServerGestion client : listClients.keySet()) {
			System.out.println("Fermeture du client: "+listClients.get(client));
    		try {
				client.fluxIn.close();
				client.fluxOut.close();
        		client.socket.close();
			} catch (IOException e) {
			}
    	}
    	
    	for(Thread thread : listThread) {
    		if(thread != null && thread.isAlive())
    			thread.stop();
    	}
    	
    	try {
			socket.close();
		} catch (IOException e) {
		}
    	
    	System.out.println("Server stopped !");
    	System.exit(0);
	}
}

class Accepter_clients implements Runnable {

	   private ServerSocket socketserver;
	   private Socket socket;
	   
	   private List<Message> listMessages;
	   private HashMap<ServerGestion, String> listClients;
	   private List<Thread> listThread;
	   
		public Accepter_clients(ServerSocket s, HashMap<ServerGestion, String> listClients, List<Thread> listThread){
			socketserver = s;
			listMessages = new ArrayList<Message>();
			this.listClients = listClients;
			this.listThread = listThread;
		}
		
		public void run() {

	        try {
	        	while(!socketserver.isClosed()){
	        		socket = socketserver.accept();
	                
	                Thread t2 = new Thread(new ServerGestion(socket, listMessages, listClients));
	                listThread.add(t2);
	                t2.start();
	        	}
	        
	        } catch (IOException e) {
				e.printStackTrace();
			}
		}
}
