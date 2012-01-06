/**
 * 
 */
package jLanSend;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Moritz Bellach
 *
 */
public class Receiver implements Runnable {
	
	private ServerSocket ss = null;
	private Socket s = null;
	private boolean done = false;
	private Thread t;
	
	/**
	 * 
	 * @param port
	 * @throws IOException probably port is already taken
	 */
	public Receiver(int port) throws IOException {
		ss = new ServerSocket(port);
		t = new Thread(this);
		t.start();
	}
	
	/**
	 * stop receiving new connections
	 */
	public void stop() {
		done = true;
	}
	

	@Override
	public void run() {
		while(!done) {
			try {
				s = ss.accept();
				System.out.println("accepted new connection");
				if(!done) {
					JLanSend.getJLanSend().addReceiveOp(new ReceiveOp(s));
				}
				else {
					s.close();
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			ss.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
