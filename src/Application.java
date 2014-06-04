import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.Random;

/* Application Module */

public class Application {
	static int my_id;

	/*
	 * Thread that generates CS request after a random wait If token is with
	 * this node and the Q is empty no request for CS is generated else Request
	 * for CS is generated
	 */
	public static void main(String args[]) {
		my_id = Integer.parseInt(args[0]);
		int counter = 0;
		DistMutEx distributedMutex = new DistMutEx(my_id);
		Random ran = new Random();
		while (true){
			counter++;
			while (distributedMutex.csEnter()) {
				counter++;
				//boolean gotToken = distributedMutex.csEnter();
			//	if (gotToken) {
					try {
						
						File file = new File("output.txt");
						
						// if file doesnt exists, then create it
						if (!file.exists()) {
							file.createNewFile();
							FileChannel channel = new RandomAccessFile(file, "rw").getChannel(); //ad now
							//ad now
							
							String content = "cs-enter pid" + my_id;
							try {
								
								FileLock lock = channel.tryLock();
					        } catch (OverlappingFileLockException e) {
					        	content = content + " locked";
					            // File is already locked in this thread or virtual machine
					        }
							channel.close();
							
							
							//String content = "cs-enter pid" + my_id;
							FileWriter fw = new FileWriter(file.getAbsoluteFile());
							BufferedWriter bw = new BufferedWriter(fw);
							bw.write(content);
							bw.close();
						}
						else{
							FileChannel channel = new RandomAccessFile(file, "rw").getChannel(); //ad now
							//FileLock lock = channel.lock(); //ad now
							String content = "\ncs-enter pid" + my_id;
							try {
								FileLock lock = channel.tryLock();
					        } catch (OverlappingFileLockException e) {
					        	content = content + " locked";
					            // File is already locked in this thread or virtual machine
					        }
							//lock.release();
							channel.close();
							FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
							//String content = "\ncs-enter pid" + my_id;
							BufferedWriter bw = new BufferedWriter(fw);
							bw.write(content);
							bw.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
						File file = new File("output.txt");
						FileChannel channel = new RandomAccessFile(file, "rw").getChannel(); //ad now
						//FileLock lock = channel.lock(); //ad now
						String content = "\ncs-exit pid" + my_id;
						try {
							FileLock lock = channel.tryLock();
				        } catch (OverlappingFileLockException e) {
				        	content = content + " locked";
				            // File is already locked in this thread or virtual machine
				        }
						//lock.release();
						channel.close();
						// if file doesnt exists, then create it
						if (!file.exists()) {
							file.createNewFile();
						}
						FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
						BufferedWriter bw = new BufferedWriter(fw);
						bw.write(content);
						bw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			//	}
				distributedMutex.csLeave();
				
				
				try {
					int sleep = ran.nextInt(1000);
					Thread.sleep(sleep);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(counter>15)
					break;
			}
			if(counter>15)
				break;
	}
		distributedMutex.close();
		//System.out.println("Exiting from main method");
	}
}
