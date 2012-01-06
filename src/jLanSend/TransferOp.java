/**
 * 
 */
package jLanSend;

//import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Observable;

/**
 * @author Moritz Bellach
 *
 */
public abstract class TransferOp extends Observable {

	protected Socket s;
	protected BufferedReader in;
	protected PrintWriter out;
	protected DataInputStream ins;
	protected DataOutputStream outs;
	protected Thread t;
	protected String rHostName;
	protected String rnick;
	protected String fname;
	protected Long fsize, bytesDone;
	protected int progress;
	protected File f;
	
	public void notifyObservers(Object o) {
		setChanged();
		super.notifyObservers(o);
	}
	
	/**
	 * 
	 * @return hostname or ip of the remote host
	 */
	public String getRHostName() {
		return rHostName;
	}
	
	/**
	 * 
	 * @return filename
	 */
	public String getFName() {
		return fname;
	}
	
	/**
	 * 
	 * @return file size in bytes
	 */
	public Long getSize() {
		return fsize;
	}
	
	public String getRNick() {
		return rnick;
	}
	
	/**
	 * 
	 * @return progress in percent
	 */
	public int getProgress() {
		return progress;
	}
	
	public void startThread() {
		t.start();
	}
	
	protected void closeNet() {
		try {
			if(ins != null) {
				ins.close();
			}
			if(outs != null) {
				outs.close();
			}
			if(in != null) {
				in.close();
			}
			if(out != null) {
				out.close();
			}
			if(s != null) {
				s.close();
			}
		} catch (IOException e) {
			System.out.println("error on close oO");
		}
		
	}
	
	protected void updateProgress(int bytes) {
		bytesDone += bytes;
		progress = (int) ((bytesDone * 100L)/fsize);
	}
}
