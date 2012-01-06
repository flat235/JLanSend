/**
 * 
 */
package jLanSend;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.ImageIcon;

/**
 * @author Moritz Bellach
 *
 */
public class JLanSend extends Observable implements Observer {
	
	private static JLanSend jls;
	private TrayIcon trayicon = null;
	private PopupMenu popup;
	private MenuItem sendStat, recvStat;
	private MainWindow mw;
	private Receiver receiver = null;
	private Vector<ReceiveOp> recvOps;
	private Vector<SendOp> sendOps;
	private Vector<String> rHosts;
	private final int lprotov = 1;
	private String nick;
	private int port;
	private Detector [] detector;
	
	/**
	 * 
	 * @return the application object
	 */
	public static JLanSend getJLanSend(){
		return jls;
	}

	/**
	 * 
	 */
	public JLanSend() {
		// TODO read config (?)
		recvOps = new Vector<ReceiveOp>();
		sendOps = new Vector<SendOp>();
		rHosts = new Vector<String>();
		port = 9999;
		nick = "no nick yet";
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		jls = new JLanSend();
		jls.initTray();
		jls.initMW();
		jls.initDetector();
		jls.startReceiver(jls.getPort());
	}
	
	/**
	 * starts receiver is it isn't started already
	 * @param port
	 */
	public void startReceiver(int port) {
		if(receiver == null) {
			try {
				receiver = new Receiver(port);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// TODO what if thread still running?
	}
	
	/**
	 * 
	 * @param op
	 */
	public void addReceiveOp(ReceiveOp op) {
		recvOps.add(op);
		op.addObserver(this);
		op.startThread();
	}
	
	
	/**
	 * 
	 * @param op
	 */
	public void addSendOp(SendOp op) {
		sendOps.add(op);
		op.addObserver(this);
		op.startThread();
	}
	
	
	/**
	 * stops receiver if it isn't stopped already
	 */
	public void stopReceiver() {
		if(receiver != null) {
			receiver.stop();
			receiver = null;
		}
	}
	
	public int getLProtoV() {
		return lprotov;
	}
	
	public int getPort() {
		return port;
	}

	/**
	 * initializes the systray icon
	 * could be used again later if turning on and off the icon is allowed via settings
	 */
	private void initTray(){
		if(SystemTray.isSupported()){
			popup = new PopupMenu("JLanSend");
			MenuItem send = new MenuItem("Send File");
			sendStat = new MenuItem("Sending: 0");
			recvStat = new MenuItem("Receiving: 0");
			
			MenuItem exit = new MenuItem("Exit");
			exit.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO do this better
					System.exit(0);
					
				}
			});
			
			CheckboxMenuItem recv = new CheckboxMenuItem("Receive Files", true);
			
			popup.add(send);
			popup.addSeparator();
			popup.add(sendStat);
			popup.add(recvStat);
			popup.addSeparator();
			popup.add(recv);
			popup.addSeparator();
			popup.add(exit);
			
			URL imageURL = JLanSend.class.getResource("icon.png");
			trayicon = new TrayIcon(new ImageIcon(imageURL).getImage(), "JLanSend", popup);
			trayicon.setImageAutoSize(true);
			trayicon.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					mw.toggleVisibility();
				}
			});
			SystemTray systray = SystemTray.getSystemTray();
			try {
				systray.add(trayicon);
			} catch (AWTException e){
				// free up some minimal resources
				sendStat = null;
				recvStat = null;
				popup = null;
				trayicon = null;
			}
			
			
			
		}
	}
	
	private void initMW(){
		mw = new MainWindow();
	}
	
	private void initDetector() {
		detector = new Detector[8];
		for(int i = 0; i < 8; i++){
			detector[i] = new Detector(i);
		}
		
	}

	/**
	 * @param nick the nick to set
	 */
	public void setNick(String nick) {
		this.nick = nick;
	}

	/**
	 * @return the nick
	 */
	public String getNick() {
		return nick;
	}
	
	
	public String niceBytes(Long bytes) {
		String si;
		double b = 0;
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMinimumFractionDigits(1);
		nf.setMaximumFractionDigits(1);
		
		if(bytes > (1024*1024*1024)) {
			si = "GB";
			b = ((double) bytes) / (1024.0*1024.0*1024.0);
		}
		else if(bytes > (1024*1024)) {
			si = "MB";
			b = ((double) bytes) / (1024.0*1024.0);
		}
		else if(bytes > 1024) {
			si = "KB";
			b = ((double) bytes) / 1024.0;
		}
		else {
			si = "B";
		}
		if(si == "B") {
			return Long.toString(bytes) + si;
		}
		else {
			return nf.format(b) + si;
		}
	}
	
	public void addRHost(String rHost) {
		if(mw != null){
			mw.changeRHost(true, rHost);
			// TODO add cli here later?
		}
	}
	
	public void delRHost(String rHost) {
		if(mw != null){
			mw.changeRHost(false, rHost);
			//TODO add cli here later?
		}
	}
	
	
	public void notifyObservers(Object o) {
		setChanged();
		super.notifyObservers(o);
	}

	@Override
	public void update(Observable src, Object msg) {
		switch ((ObsMsg) msg) {
		case RECVSTART:
		case SENDSTART:
			notifyObservers(src);
			break;
		case REMOVEME:
			if(src instanceof ReceiveOp) {
				recvOps.remove(src);
			}
			else if(src instanceof SendOp) {
				sendOps.remove(src);
			}
			else {
				System.out.println("oO");
			}
			break;

		default:
			break;
		}
		
	}
}
