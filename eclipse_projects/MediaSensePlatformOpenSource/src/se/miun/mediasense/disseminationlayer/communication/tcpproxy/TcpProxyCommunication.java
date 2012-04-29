package se.miun.mediasense.disseminationlayer.communication.tcpproxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import se.miun.mediasense.addinlayer.AddInManager;
import se.miun.mediasense.disseminationlayer.communication.CommunicationInterface;
import se.miun.mediasense.disseminationlayer.communication.GetMessage;
import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.communication.NotifyMessage;
import se.miun.mediasense.disseminationlayer.communication.SetMessage;
import se.miun.mediasense.disseminationlayer.communication.serializer.EnterSeparatedMessageSerializer;
import se.miun.mediasense.disseminationlayer.disseminationcore.DisseminationCore;

public class TcpProxyCommunication implements CommunicationInterface, Runnable {

	private String serverAddress;
	private Socket socket;
	private int serverPort;
	private String localIp;
	private Thread th;

	private EnterSeparatedMessageSerializer messageSerializer = new EnterSeparatedMessageSerializer();
	private DisseminationCore core;
	private boolean runCommunication;

	public TcpProxyCommunication(DisseminationCore core, String proxyServerAddress,
			int proxyPort) throws UnknownHostException, IOException {
		this.core = core;
		serverAddress = proxyServerAddress;
		serverPort = proxyPort;
		startup();
	}

	private void startup() throws UnknownHostException, IOException {
		if (runCommunication == false) {
			socket = new Socket(serverAddress, serverPort);
			getIdentity();
			runCommunication = true;
			th = new Thread(this, "ProxyThread");
			th.start();
		} else {
			socket = new Socket(serverAddress, serverPort);
			reconnect();
		}
	}

	private void getIdentity() throws IOException {
		// Generate request to server
		String request = "IDREQUEST|" + socket.getInetAddress().toString();
		OutputStream out = socket.getOutputStream();
		PrintWriter pr = new PrintWriter(out);
		pr.println(encode(request));
		pr.flush();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
		String response = br.readLine();
		localIp = decode(response);

	}

	private void reconnect() throws IOException {
		// Generate request to server
		String request = getLocalIp();
		OutputStream out = socket.getOutputStream();
		PrintWriter pr = new PrintWriter(out);
		pr.println(encode(request));

	}

	@Override
	public void sendMessage(Message message) {
		synchronized (socket) {
			// TODO Auto-generated method stub
			String SendString = "ROUTE|" + message.getToIp() + "|"
					+ encode(new String(messageSerializer.serializeMessage(message)));
			OutputStream out;
			try {
				out = socket.getOutputStream();
				PrintWriter pr = new PrintWriter(out);
				pr.println(encode(SendString));
				pr.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public String getLocalIp() {
		// TODO Auto-generated method stub
		return localIp;
	}

	@Override
	public void shutdown() {
		try {
			runCommunication = false;
			th = null;
			socket.close();			
		} catch (Exception e) {
			
		}

	}

	private String decode(String response) {
		try {
			byte[] temp = Base64.decode(response);
			return new String(temp);
		} catch (IOException ex) {
			Logger.getLogger(TcpProxyCommunication.class.getName()).log(Level.SEVERE, null,
					ex);
		}
		return "ERROR";
	}

	private String encode(String value) {
		return Base64.encodeBytes(value.getBytes());

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));

			while (runCommunication) {
				try {
					String rawMessage = br.readLine();
					final String message = decode(rawMessage);

					Thread t = new Thread(new Runnable() {

						@Override
						public void run() {
							handleCommand(message);
						}

					});
					t.start();

				} catch (Exception e) {
					// e.printStackTrace();
				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private void handleCommand(String line) {
		String temp = line.split("\\|")[0];
		if (temp.equalsIgnoreCase("MESSAGE")) {
			handleMessage(decode(line.split("\\|", 2)[1]));
		} else if (temp.equalsIgnoreCase("SHUTDOWN")) {
			try {
				reconnect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// This should be done better... With some managers and stuff. Victor will
	// fix it...
	private void handleMessage(String stringRepresentation) {
		try {

			Message message = messageSerializer
					.deserializeMessage(stringRepresentation.getBytes());

			switch (message.getType()) {

			case Message.GET:
				// Fire off the getEvent!
				GetMessage getMessage = (GetMessage) message;
				core.callGetEventListener(getMessage.getFromIp(), getMessage.uci);
				break;

			case Message.SET:
				// Fire off the SetEvent!
				SetMessage setMessage = (SetMessage) message;
				core.callSetEventListener(setMessage.uci, setMessage.value);
				break;

			case Message.NOTIFY:
				// Fire off the getResponseEvent!
				NotifyMessage notifyMessage = (NotifyMessage) message;
				core.callGetResponseListener(notifyMessage.uci,
						notifyMessage.value);
				break;

			default:

				//This forwards any unknown messages to the lookupService
				core.getLookupServiceInterface().handleMessage(message);
				
				// This forwards any unknown messages to the AddInManager and
				// the addIns
				AddInManager addInManager = core.getMediaSensePlatform()
						.getAddInManager();
				addInManager.forwardMessageToAddIns(message);
				break;

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
