import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the server class for UDP. This class saves the messages sent from the
 * client and if requested sends the content back to the client.
 * 
 * @author Yogesh Nagarur
 */
public class server_java_udp {

	private final static int PACKETSIZE = 1024;
	public static String userMessage = "";
	public static String currentUserName;
	static Map<String, String> messageHistory;
	static ArrayList<String> messageOrderHistory;

	/**
	 * This is the main method which initialises the UDP server and also where
	 * data is sent to from the client. Depending on if the request from client
	 * is to "send" a message, it will be saved into message history. If the
	 * request is "print", data will be sent from here to the client for output.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String args[]) throws Exception {

		messageHistory = new HashMap<String, String>();
		messageOrderHistory = new ArrayList<String>();
		if (args.length != 1) {
			System.err.println("Invalid number of args. Terminating.");
			System.exit(0);
		}

		int port = Integer.parseInt(args[0]);

		if (port <= 1024 || port > 49151) {
			System.err.println("Invalid port. Terminating.");
			System.exit(0);
		} else {
			try {

				DatagramSocket socket = new DatagramSocket(port);

				for (;;) {
					DatagramPacket packet = new DatagramPacket(
							new byte[PACKETSIZE], PACKETSIZE);
					socket.receive(packet);
					if (new String(packet.getData()).trim().split(":")[0]
							.equals("LOGINNAME")) {
						currentUserName = new String(packet.getData()).trim()
								.split(":")[1].trim();
						if (!messageHistory.containsKey(currentUserName)) {
							messageHistory.put(currentUserName, "");
						}
					} else if (new String(packet.getData()).trim().equals(
							"print")) {
						// For Single Client sending
						// byte[] dataToClient =
						// messageHistory.get(currentUserName).getBytes();

						// For Complete transaction sending
						String completeMessage = "";
						for (int i = 0; i < messageOrderHistory.size(); i++) {
							completeMessage = completeMessage
									+ messageOrderHistory.get(i) + "\n";
						}
						byte[] dataToClient = completeMessage.getBytes();
						DatagramPacket sendToClient = new DatagramPacket(
								dataToClient, dataToClient.length,
								packet.getAddress(), packet.getPort());
						socket.send(sendToClient);
					} else {
						messageOrderHistory.add(currentUserName + " : "
								+ new String(packet.getData()).trim());

						// System.out.println("Message From " + currentUserName
						// + " : " + new String(packet.getData()).trim());
						if (messageHistory.containsKey(currentUserName)) {
							String oldMsg = messageHistory.get(currentUserName) == null ? ""
									: messageHistory.get(currentUserName);
							messageHistory.put(currentUserName, oldMsg + " "
									+ new String(packet.getData()).trim());
						}

					}

				}
			} catch (Exception e) {
				System.err.println("Could not bind port. Terminating.");
			}
		}
	}
}