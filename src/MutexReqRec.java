
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;


//Accepts connections for requests from different nodes

public class MutexReqRec extends Thread {
	private ServerSocket LocServersock;
	private boolean close = false;

	public MutexReqRec(int port) throws IOException {
		LocServersock = new ServerSocket(port);
		LocServersock.setSoTimeout(10000);
	}

	public void run() {
		while (!close) {
			try {
				Socket server = LocServersock.accept();
				/*DataInputStream in = new DataInputStream(
						server.getInputStream());*/
				InputStream is = server.getInputStream();  
				ObjectInputStream ois = new ObjectInputStream(is); 
				Object obj = ois.readObject();
				System.out.println("\nrecieved a request message");
				// Listen for requests and decide..
				if(obj instanceof Token){
					//System.out.println("We got a token");
					DistMutEx.token = (Token)obj;
					DistMutEx.hastoken = true;
					DistMutEx.tokenInUse = true;
					DistMutEx.tokenRecvd = true;
					 
					//DistMutEx.hastoken = true;           
				}
				else if(obj instanceof Request){
					System.out.println("Recieved a Token Request");
					//System.out.println(DistMutEx.hastoken);
					Request request = (Request)obj;
					DistMutEx.RN[request.getId()] = (DistMutEx.RN[request.getId()]>request.getReqNo()? 
															DistMutEx.RN[request.getId()]:
																request.getReqNo());
					if(DistMutEx.hastoken){
						//Adding the Request from other node to Queue of Token
						if(request.getReqNo() == DistMutEx.token.LN[request.getId()]+1 && !DistMutEx.token.getQueue().contains(request));
						DistMutEx.token.getQueue().add((Request)obj);
						
						System.out.println("Request added successfully");
						
						/*if(!DistMutEx.tokenInUse){
							MutexReqRec.sendToken();
						}*/
					}
					
				}
				//If the Queue of Token is not empty.
				
			} catch (IOException e) {
				System.out.println("No inbound connections, application terminating");
				/*if (e.getCause() instanceof SocketTimeoutException){
					System.out.println("No inbound connections, application terminating");
				}
				else{
					e.printStackTrace();
				}*/
				
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			

		}
		System.out.println("Application shutting down......."
				+ "\nall connections are terminated ");
		return;
	}
	
	public static synchronized void sendToken(){
		if (DistMutEx.hastoken && !DistMutEx.token.getQueue().isEmpty() && !DistMutEx.tokenInUse) {
			Request requestServing = DistMutEx.token.getQueue().poll();
			//Sending the Token to the next Request in the queue
			try {
				DistMutEx.hastoken = false;
				DistMutEx.requestMade = false;
				DistMutEx.tokenRecvd = false;
				Node node = DistMutEx.nodes.get(Integer.toString(requestServing.getId()));
				System.out.println("Sending token to " +node.getHostname());
				Socket client = new Socket(node.getHostname(), Integer.parseInt(node.getPort()));
				OutputStream os = client.getOutputStream();  
				ObjectOutputStream oos = new ObjectOutputStream(os);  
				oos.writeObject(DistMutEx.token);  
				oos.close();  
				os.close();  
				client.close();

			}
			catch (IOException e) {
				//DistMutEx.exit = true;
				MutexReqRec.retry();
				e.printStackTrace();
			}
		}
	}
	static void retry(){
		MutexReqRec.sendToken();
	}
	public void closeThread(){
		//Thread.currentThread().interrupt();
		MutexReqRec.sendToken();
		this.close = true;
		return;
		
	}
}
