package manager.ui;

import java.awt.BorderLayout;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import manager.Message;
import manager.listener.NodeMessageListener;

public class NodeInfo extends JFrame implements NodeMessageListener {

	private JPanel contentPane;
	private JTextArea console;

	/**
	 * Create the frame.
	 */
	public NodeInfo(String networkAddress, String hash) {
		super(hash + "{" + networkAddress + "}");
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		console = new JTextArea("# ");
		contentPane.add(console);
		this.setVisible(true);
	}

	@Override
	public void OnNodeMessage(Date timeStamp, Message msg) {
		console.append(msg.toString()+"/n");
	}

}
