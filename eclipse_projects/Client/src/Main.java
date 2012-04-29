import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

import se.miun.mediasense.addinlayer.extensions.publishsubscribe.SubscriptionEventListener;
import se.miun.mediasense.disseminationlayer.communication.CommunicationInterface;
import se.miun.mediasense.disseminationlayer.disseminationcore.DisseminationCore;
import se.miun.mediasense.disseminationlayer.disseminationcore.GetEventListener;
import se.miun.mediasense.disseminationlayer.disseminationcore.GetResponseListener;
import se.miun.mediasense.disseminationlayer.disseminationcore.ResolveResponseListener;
import se.miun.mediasense.disseminationlayer.disseminationcore.SetEventListener;
import se.miun.mediasense.disseminationlayer.lookupservice.LookupServiceInterface;
import se.miun.mediasense.interfacelayer.MediaSensePlatform;

public class Main implements GetResponseListener, SetEventListener, ResolveResponseListener, GetEventListener {

	private JFrame frame;
	private JTextField textField;
	
	//MediaSense Platform Application Interfaces
    MediaSensePlatform platform;
    DisseminationCore core;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		initialize();
		
		 //Create the platform itself
        platform = new MediaSensePlatform();
        	       
        //Initialize the platform with chosen LookupService type and chosen Communication type. 
        platform.initalize(LookupServiceInterface.DISTRIBUTED, CommunicationInterface.TCP); //For DHT Lookup and TCP P2P communication
	
      //Extract the core for accessing the primitive functions
        core = platform.getDisseminationCore();
        
        //Set the response listeners
        core.setGetResponseListener(this);
        core.setResolveResponseListener(this);
        
        //Set the event listeners
        core.setGetEventListener(this);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(30, 46, 272, 28);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		JButton btnResolve = new JButton("resolve");
		btnResolve.setBounds(40, 86, 117, 29);
		btnResolve.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String uci = textField.getText();
				if(!uci.equals("")) {
					core.resolve(uci);
					System.out.println("Started to resolve: " + uci);
				}
			}
		});
		frame.getContentPane().add(btnResolve);
		
		JButton btnRegister = new JButton("register");
		btnRegister.setBounds(185, 86, 117, 29);
		btnRegister.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String uci = textField.getText();
				if(!uci.equals("")) {
					core.register(uci);
					System.out.println("Registered: " + uci);
				}
			}
		});
		frame.getContentPane().add(btnRegister);
	}

	@Override
	public void getEvent(String source, String uci) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resolveResponse(String uci, String ip) {
		if (ip == null) {
			System.out.println("Resolve failed for " + uci);
		}
		else {
			System.out.println("Resolved " + uci + " @ " + ip);
		}
		
	}

	@Override
	public void setEvent(String uci, String value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getResponse(String uci, String value) {
		// TODO Auto-generated method stub
		
	}
}
