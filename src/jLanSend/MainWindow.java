/**
 * 
 */
package jLanSend;


import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

/**
 * @author Moritz Bellach
 *
 */
public class MainWindow extends JFrame implements Observer {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	private JTabbedPane tabGroup;
	private JPanel recvTab, sendTab, settingsTab, sendBtnGrp, sendOpList, receiveOpList;
	private JTextField nick, port;
	private JCheckBox startTray, startAutodetection, startReceiver;
	private JButton fchooser, sendbtn, downloaddir;
	private JComboBox hostchooser;
	private ComboBoxModel cbm;
	private File f;
	private Vector<String> rHosts;

	/**
	 * @throws HeadlessException
	 */
	public MainWindow() throws HeadlessException {
		super("JLanSend");
		
		JLanSend.getJLanSend().addObserver(this);
		
		setLayout(new BorderLayout());
		tabGroup = new JTabbedPane();
		recvTab = new JPanel(new BorderLayout());
		sendTab = new JPanel(new BorderLayout());
		settingsTab = new JPanel(new GridLayout(0, 2));
		
		sendBtnGrp = new JPanel();
		sendTab.add(sendBtnGrp, BorderLayout.NORTH);
		fchooser = new JButton("choose file");
		fchooser.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser jfs = new JFileChooser();
				if(JFileChooser.APPROVE_OPTION == jfs.showOpenDialog(rootPane)) {
					f = jfs.getSelectedFile();
					fchooser.setText(f.getName());
				}
				
			}
		});
		sendbtn = new JButton("send");
		sendbtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//SendOp op = new SendOp(f, (String) hostchooser.getSelectedItem(), 9999);
				//TransferDisplay disp = new TransferDisplay(op.getFName(), op.rHostName, op);
				//sendOpList.add(disp);
				//sendOpList.revalidate();
				JLanSend.getJLanSend().addSendOp(new SendOp(f, (String) hostchooser.getSelectedItem(), 9999));
			}
		});
		cbm = new DefaultComboBoxModel();
		rHosts = new Vector<String>();
		hostchooser = new JComboBox(rHosts);
		hostchooser.setEditable(true);
		sendBtnGrp.add(new JLabel("Send"));
		sendBtnGrp.add(fchooser);
		sendBtnGrp.add(new JLabel("to"));
		sendBtnGrp.add(hostchooser);
		sendBtnGrp.add(sendbtn);
		
		sendOpList = new JPanel(new GridLayout(0, 1));
		sendTab.add(new JScrollPane(sendOpList), BorderLayout.CENTER);
		
		receiveOpList = new JPanel(new GridLayout(0, 1));
		recvTab.add(new JScrollPane(receiveOpList), BorderLayout.CENTER);
		
		port = new JTextField(String.valueOf(JLanSend.getJLanSend().getPort()));
		downloaddir = new JButton(JLanSend.getJLanSend().getDownloaddir());
		nick = new JTextField(JLanSend.getJLanSend().getNick());
		startReceiver = new JCheckBox();
		startReceiver.setSelected(JLanSend.getJLanSend().isStartReceiver());
		startAutodetection = new JCheckBox();
		startAutodetection.setSelected(JLanSend.getJLanSend().isStartAutodetection());
		startTray = new JCheckBox();
		startTray.setSelected(JLanSend.getJLanSend().isStartTray());
		
		settingsTab.add(new JLabel("Nick"));
		settingsTab.add(nick);
		settingsTab.add(new JLabel("Download Directory"));
		settingsTab.add(downloaddir);
		settingsTab.add(new JLabel("Port"));
		settingsTab.add(port);
		settingsTab.add(new JLabel("start receiving files on launch"));
		settingsTab.add(startReceiver);
		settingsTab.add(new JLabel("create tray icon on launch"));
		settingsTab.add(startTray);
		settingsTab.add(new JLabel("start detecting other JLanSends on launch"));
		settingsTab.add(startAutodetection);
		
		tabGroup.addTab("Send", sendTab);
		tabGroup.addTab("Receive", recvTab);
		tabGroup.addTab("Settings", settingsTab);
		add(tabGroup);
		pack();
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setVisible(true);
		
	}
	
	/**
	 * hides the main window
	 */
	public void pubhide(){
		setVisible(false);
	}
	
	/**
	 * unhides the main window
	 */
	public void pubunhide(){
		setVisible(true);
	}
	
	/**
	 * toggles the visibility of the main window
	 */
	public void toggleVisibility(){
		if(isVisible()){
			pubhide();
		}
		else{
			pubunhide();
		}
	}
	
	public synchronized void changeRHost(boolean add, String rHost){
		if(add){
			if(!rHosts.contains(rHost)){
				rHosts.add(rHost);
			}
		}
		else {
			String toRemove = "";
			for(String savedHost : rHosts){
				if(savedHost.endsWith(rHost)){
					toRemove = savedHost;
					break;
				}
			}
			if(!toRemove.equals("")){
				rHosts.remove(toRemove);
			}
			hostchooser.revalidate();
		}
		
	}
	

	@Override
	public void update(Observable src, Object msg) {
		if(src instanceof JLanSend) {
			if(msg instanceof ReceiveOp) {
				((ReceiveOp) msg).addObserver(this);
				receiveOpList.add(new TransferDisplay(((ReceiveOp) msg).getFName(),
						((ReceiveOp) msg).getRNick() + "@" + ((ReceiveOp) msg).getRHostName(),
						(ReceiveOp) msg)
				);
			}
			else if(msg instanceof SendOp) {
				((SendOp) msg).addObserver(this);
				sendOpList.add(new TransferDisplay(((SendOp) msg).getFName(),
						((SendOp) msg).getRNick() + "@" + ((SendOp) msg).getRHostName(),
						(SendOp) msg)
				);
				sendOpList.revalidate();
			}

			else {
				System.out.println("oO");
			}
		}
		else {
			switch ((ObsMsg) msg) {
			/*case RECVPROGRESS:
				// TODO get progress
				break;
			case RECVDONE:
				// TODO show its done
				break;
			case SENDPROGRESS:
				// TODO get progress
				break;
			case SENDDONE:
				// TODO show its done
				break;*/
			case REMOVEME:
				// TODO remove from gui ???
				src.deleteObserver(this);
			/*case NEWRHOSTS:
				hostchooser.removeAllItems();
				rHosts = JLanSend.getJLanSend().getRHosts();
				for (String rHost : rHosts) {
					hostchooser.addItem(rHost);
				}
			*/
			default:
				break;
			}
		}
		
	}
		
	
}
