import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
public class Server {
    private ServerSocket _listenSocket;
    private boolean _running = true;

    public static void usage() {
        System.out.println("Usage: Server <interface> <port> <mode>");
        System.out.println("\tinterface: ip address to listen on");
        System.out.println("\tport:      port to bind to");
        System.out.println("\tmode:      0=simple socket, 1=X.509");
        System.out.println("");
        System.out.println("Example:");
        System.out.println("\tServer 127.0.0.1 9999 1");
        System.exit(1);
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            usage();
        }

        new Server(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));

    }

    public Server(String host, int port, int mode) throws Exception {

        String hostToLog = (host == null) ? "[NOT_SPECIFIED]" : host;
        System.out.println("Server listening on " + hostToLog + ":" + port);

        if (mode == 1) {
            System.setProperty("javax.net.ssl.keyStore", "./server.jks");
            System.setProperty("javax.net.ssl.keyStorePassword", "password");

            System.setProperty("javax.net.ssl.trustStore", "./trusted_clients.jks");
            System.setProperty("javax.net.ssl.trustStorePassword", "password");

            SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

            if (host != null) {
                InetAddress netAddr = InetAddress.getByName(host);
                _listenSocket = factory.createServerSocket(port, 0, netAddr);
            } else {
                _listenSocket = factory.createServerSocket(port);
            }

            ((SSLServerSocket) _listenSocket).setNeedClientAuth(true);

        } else {
            if (host != null) {
                InetAddress netAddr = InetAddress.getByName(host);
                _listenSocket = new ServerSocket(port, 0, netAddr);
            } else {
                _listenSocket = new ServerSocket(port);
            }
        }

        // give the socket a non-zero timeout so accept() can be interrupted
        _listenSocket.setSoTimeout(6000);

        while (_running) {
            try {
                Socket client = _listenSocket.accept();

                Thread t = new Handler(client);
                t.setDaemon(true);
                t.start();
            } catch (SocketTimeoutException ex) {
                // Ignore
            }
        }

        _listenSocket.close();
        _listenSocket = null;

    }

    public void stopMe() {
        _running = false;
    }

}