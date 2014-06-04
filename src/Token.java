import java.util.LinkedList;
import java.util.Queue;
import java.io.Serializable;

public class Token implements Serializable{
	int[] LN ;
	static Queue<Request> queue;
	public Token(int size){
		LN =  new int[size];
		for(int i = 0;i<size;i++){
			LN[i] = -1;
		}
		queue = new LinkedList<Request>();
	}
	
	public int[] getLN() {
		return LN;
	}
	public void setLN(int[] lN) {
		this.LN = lN;
	}
	public static Queue<Request> getQueue() {
		return queue;
	}
	public static void setQueue(Queue<Request> q) {
		queue = q;
	}
	
}	
