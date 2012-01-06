/**
 * 
 */
package jLanSend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Moritz Bellach
 *
 */
public class Detector implements Runnable {
	
	private Thread t;
	private Socket s;
	private BufferedReader in;
	private PrintWriter out;
	private InetAddress myip;
	private String myipS;
	private String [] myipA;
	private int port;
	private int threadNo;
	
	public Detector(int threadNo) {
		this.port = JLanSend.getJLanSend().getPort();
		this.threadNo = threadNo;
		t = new Thread(this);
		
		try {
			myip = InetAddress.getLocalHost();
			myipS = myip.getHostAddress();
			//System.out.println("myIP = " + myipS);
			myipA = myipS.split("\\.");
			//for (String part : myipA) {
			//	System.out.println(part);
			//}
			
		} catch (UnknownHostException e) {
			System.out.println("Could not get my own ip");
		}
		t.start();
	}
	

	@Override
	public void run() {
		
		while(true) {
			for(int i = (threadNo*32)+1; (i < (threadNo*32 + 33)) && (i < 255); i++) {
				//System.out.println("trying ." + Integer.valueOf(i));
				try {
					s = new Socket();
					s.connect(new InetSocketAddress(myipA[0] + "." + myipA[1] + "." + myipA[2] + "." + String.valueOf(i), port), 250);
					//s = new Socket(myipA[0] + "." + myipA[1] + "." + myipA[2] + "." + String.valueOf(i), port);
					
					in = new BufferedReader(new InputStreamReader(s.getInputStream()));
					out = new PrintWriter(s.getOutputStream(), true);
					out.println("JLanSend");
					out.println(Integer.valueOf(JLanSend.getJLanSend().getLProtoV()));
					String rproto = in.readLine();
					if(rproto.equals("ok")) {
						//my proto version
						out.println("detect");
						String rnick = in.readLine();
						JLanSend.getJLanSend().addRHost(rnick + "@" + s.getInetAddress().getHostAddress());
						//System.out.println("detected " + rnick + "@" + s.getInetAddress().getHostAddress());
						//rHosts.add(s.getInetAddress().getHostAddress());
					}
					else {
						//switch to compatibility later
					}
					in .close();
					out.close();
					s.close();
					
				} catch (UnknownHostException e) {
					System.out.println("." + Integer.valueOf(i) + " is unknownHost");
				} catch (IOException e) {
					//System.out.println("." + Integer.valueOf(i) + " is IOE");
					JLanSend.getJLanSend().delRHost(myipA[0] + "." + myipA[1] + "." + myipA[2] + "." + String.valueOf(i));
				}
			}
			
			try {
				Thread.sleep(1000*30);
				//wait(1000 * 30);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		
	}

}
