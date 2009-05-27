import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;

public class Handler extends Thread {

    private final Socket _client;

    public Handler(Socket client) {
        _client = client;
    }

    @Override
    public void run() {
        try {
            handle();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void handle() throws IOException {

        PrintStream out = null;
        BufferedReader in = null;
        try {
			System.out.println("Received client connection...");
            in = new BufferedReader(new InputStreamReader(_client.getInputStream()));

            out = new PrintStream(_client.getOutputStream());

            String host = _client.getInetAddress().getHostName();

            out.print("HELO " + host + ". Please press ENTER to quit.\r\n");
            out.flush();

            in.readLine();

			out.print("GOODBYE.\r\n");
			out.flush();
        } catch (Exception ex) {
            ex.printStackTrace(out);
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
            if (in != null) {
                in.close();
            }
        }
		System.out.println("Connection closed.");
    }
}
