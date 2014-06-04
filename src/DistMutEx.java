import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class DistMutEx {
	static HashMap<String, Node> nodes = new HashMap<String, Node>();
	static int my_id;
	static int counter = 0;
	// RN, LN and Queue as required for the algo
	static int[] RN;
	static Token token;
	static boolean hastoken = false;
	static boolean tokenRecvd = false;
	static boolean requestMade = false;
	static boolean tokenInUse = false;
	static boolean exit = false;
	MutexReqRec mutexRecvReqThread ;
	Scanner rfile;
	int num_nodes = 0;
	String rline;
	int valcn = 0, p;

	public DistMutEx(int my_id) {
		this.my_id = my_id;
		// Reading Config file
		try {
			rfile = new Scanner(new File("Config.txt"));
			while (rfile.hasNextLine()) {
				rline = rfile.nextLine();
				if (rline.charAt(0) != '#') {
					// Skipping the first line of input, Which has only the
					// count of the nodes
					if (valcn == 0) {
						num_nodes = Character.getNumericValue(rline.charAt(0));
						valcn = 1;
						rline = rfile.nextLine();
					}
					String[] params = rline.split(" ");
					Node temp = new Node(params[0], params[1], params[2]);
					temp.print();
					nodes.put(params[0], temp);
				}
			}
			// Initializing the Token object
			token = new Token(num_nodes);
			// Initializing the RN array
			RN = new int[DistMutEx.nodes.size()];
			//Initializing RN
			for (int i = 0; i < DistMutEx.nodes.size(); i++) {
				RN[i] = -1;
			}
			// IF the node id is 0, Hold the token for initial
			if (my_id == 0) {
				hastoken = true;
			}
			// Starting MutEx thread to accept requests
			p = Integer.parseInt(nodes.get(Integer.toString(my_id)).port);
			mutexRecvReqThread = new MutexReqRec(p);
			mutexRecvReqThread.start();
			rfile.close();
		} catch (IOException e) {
			rfile.close();
			e.printStackTrace();
		}
	}

	public boolean csEnter() {
		
		if (hastoken && token.queue.isEmpty()) {
			tokenInUse = true;
			return hastoken;
		}
		
		else {
			if (hastoken && !token.queue.isEmpty()){
				MutexReqRec.sendToken();
			}
			if(!exit)
			{
				// Generate CS request
				requestMade = true;
				counter++;
				// Increment RN of my_id********
				DistMutEx.RN[my_id]++;
				//Generating a new request
				Request request = new Request(my_id, counter);
				// Broadcast request message to all nodes
				for (String node : nodes.keySet()) {
					try {
						if (!nodes.get(node).id.equals(Integer.toString(my_id))) {
							String hostname = nodes.get(node).hostname;
							int port = Integer.parseInt(nodes.get(node).port);
							System.out.println("request to " + hostname + " port: "
									+ port);
							Thread.sleep(100);
							Socket client = new Socket(hostname, port);
							OutputStream os = client.getOutputStream();
							ObjectOutputStream oos = new ObjectOutputStream(os);
							oos.writeObject(request);
							oos.close();
							os.close();
							client.close();
						}
					} catch (IOException e) {
						//e.printStackTrace();
					} catch (InterruptedException e) {
						//e.printStackTrace();
					}
				}
				int counter = 0;
				while (!tokenRecvd && counter<25) {
					try {
						counter++;
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("waiting for token...");
				}
				if(tokenRecvd){
					System.out.println("Token recieved");
					tokenInUse = true;
					return tokenRecvd;
				}
				else{
					System.out.println("Request operation timed out: retrying with new cs request");
					DistMutEx.RN[my_id]--;
					return false;
				}
			}
			else{
				System.out.println("Nodes requested for termination");
				return false;
			}
		}
		// wait until Token is recvd
		
	}
	public void csLeave() {
		tokenInUse = false;
		if(requestMade){
			token.LN[my_id] = RN[my_id];
			requestMade = false;
		}
		
		//Checking if the queue is not empty
		if (hastoken && !token.getQueue().isEmpty()) {
			MutexReqRec.sendToken();
						tokenRecvd = false;
						hastoken = false;
						Request request = token.getQueue().poll();
						//Updating the LN, ie., LN[id] = RN[id]
						
						//Sending the Token to the next Request in the queue
						try {
							
							Node node = nodes.get(Integer.toString(request.getId()));
							System.out.println("Sending token from cs to " +node.getHostname());
							Socket client = new Socket(node.getHostname(), Integer.parseInt(node.getPort()));
							OutputStream os = client.getOutputStream();  
							ObjectOutputStream oos = new ObjectOutputStream(os);  
							oos.writeObject(token);  
							oos.close();  
							os.close();  
							client.close();
							//token = null;
							
							//requestMade = false;
							//tokenInUse = false;
						}
						catch (IOException e) {
							e.printStackTrace();
						}
		//tokenInUse = false;
		}
	}
		
	
	
	public void close(){
		if(mutexRecvReqThread!=null)
			System.out.println("Reached maximum number of executions");
			mutexRecvReqThread.closeThread();
			//System.out.println("Closing algo thread");
		return;
	}
}
