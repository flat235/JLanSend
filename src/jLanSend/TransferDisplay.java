/**
 * 
 */
package jLanSend;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 * @author Moritz Bellach
 *
 */
public class TransferDisplay extends JPanel implements Observer{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JProgressBar progress;
	JLabel fname, from;
	//JButton remove;
	TransferOp op;

	/**
	 * 
	 */
	public TransferDisplay(String fname, String from, TransferOp op) {
		super();
		this.from = new JLabel(from);
		this.fname = new JLabel(fname);
		this.op = op;
		
		progress = new JProgressBar(0, 100);
		progress.setStringPainted(true);
		/*remove = new JButton("remove");
		remove.setEnabled(false);
		remove.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				TransferDisplay toRemove = (TransferDisplay) ((JButton) arg0.getSource()).getParent();
				JComponent par = (JComponent) toRemove.getParent();
				par.remove(toRemove);
				par.revalidate();
			}
		});
		*/
		add(this.fname);
		add(this.from);
		add(progress);
		
		//add(remove);
		
		op.addObserver(this);
	}

	@Override
	public void update(Observable src, Object msg) {
		switch ((ObsMsg) msg) {
		case SENDPROGRESS:
		case RECVPROGRESS:
			progress.setValue(((TransferOp) src).getProgress());
			break;
		case RECVDONE:
		case SENDDONE:
			progress.setValue(100);
			progress.setString("done");
			op = null;
			//remove.setEnabled(true);
			break;
		case FAIL:
			progress.setString("failed");
		case REMOVEME:
			op = null;
			//remove.setEnabled(true);
			break;
		default:
			break;
		}
		
	}
	



}
