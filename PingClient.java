import java.io.*;
import java.net.*;
import java.util.*;

public class PingClient {


    public static void main(String[] args)throws Exception {

        long totalrtt = 0;
        long maxrtt = -9999;
        long minrtt = 9999;
        int drops = 0;
        long successes = 0;
        int retPacket;

        if (args.length != 2) 
        {  
            System.out.println("Required arguments: host port");
            return;
        }
        String server = args[0];   // Read first argument from user
        String serport = args[1]; // Read second argument from user
        int serverPort = Integer.parseInt(serport);
        DatagramSocket socket = new DatagramSocket(); // Create new datagram socket
        socket.setSoTimeout(3000); // Set socket timeout value. Read API for DatagramSocket to do this
        InetAddress serverAddress = InetAddress.getByName(server); //Convert server to InetAddress format; Check InetAddress API for this
        byte[] sendData = new byte[1024];
        byte[] receiveData = new byte[1024];

        for(int i = 0; i < 10; i++) {
            String payload = "Ping " + i + " " + System.currentTimeMillis() + "\r\n"; // Construct data payload for PING as per the instructions
            sendData = payload.getBytes(); // Convert payload into bytes
            DatagramPacket packet = new DatagramPacket(sendData, payload.length(), serverAddress, serverPort);    // Create new datagram packet
            socket.send(packet); // send packet
            DatagramPacket reply = new DatagramPacket(new byte[1024], 1024); // Create datagram packet for reply

            try {
                socket.receive(reply); //wait for incoming packet reply
               // Thread.sleep(1000);
                byte[] buf = reply.getData();
                ByteArrayInputStream bais = new ByteArrayInputStream(buf);
                InputStreamReader isr = new InputStreamReader(bais);
                BufferedReader br = new BufferedReader(isr);
                String line = br.readLine();
                String[] split = line.split(" ");
                // Parse incoming string "line"
                // extract packet sequence number into the variable retPacket
                retPacket = Integer.parseInt(split[1]);
                if (retPacket != i) {
                    System.out.print("Received out of order packet");
                    System.out.println();
                }
                else {
                    System.out.println("Received from " + packet.getAddress().getHostAddress() + " ," + new String(line));
                    System.out.println();
                    
                    long pktTime = Long.parseLong(split[2]);
                    long rtt = System.currentTimeMillis() - pktTime;
                    //calculate average
                    totalrtt += rtt;
                    // calculate total, max and min rtt
                    if(rtt>maxrtt){
                        maxrtt = rtt;
                    }
                    if(rtt<minrtt){
                        minrtt = rtt;
                    }
                    successes++;

                }
            }
            catch(SocketTimeoutException e) {
                System.out.println("Error: Request timed out");
                drops++;
            }
        }
        // print and store average, max, min rtt and drops
        System.out.println("Average rtt: " + totalrtt/successes+ "\nMax rtt: " + maxrtt+ "\nMin rtt: " + minrtt+ "\nNum drops: "+ drops);
    }
}   