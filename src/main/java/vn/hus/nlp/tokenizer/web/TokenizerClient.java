package vn.hus.nlp.tokenizer.web;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author LE HONG Phuong, phuonglh@gmail.com
 * <p>
 * Jul 8, 2009, 11:37:55 AM
 * <p>
 */
public class TokenizerClient {

    private static final Logger logger = LogManager.getLogger(TokenizerClient.class);

    String host;
    int port;

    private BufferedReader in;
    private BufferedWriter out;
    private Socket sock;

    /**
     * Creates a tokenizer client
     * @param host
     * @param port
     */
    public TokenizerClient(final String host, final int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * @return
     */
    public boolean connect() {
        try {
            sock = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(sock.getInputStream(), "UTF-8"));
            out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream(), "UTF-8"));
            return true;
        } catch (final Exception e) {
            logger.info(e.getMessage());
            return false;
        }
    }

    /**
     * @param data
     * @return
     */
    public String process(final String data) {
        try {
            out.write(data);
            out.write((char) 0);
            out.flush();

            //Get data from server
            String result = "";
            while (true) {
                final int ch = in.read();

                if (ch == 0) {
                    break;
                }
                result += (char) ch;
            }
            return result;
        } catch (final Exception e) {
            logger.info(e.getMessage());
            return "";
        }

    }

    /**
     * Closes the socket.
     */
    public void close() {
        try {
            this.sock.close();
        } catch (final Exception e) {
            logger.info(e.getMessage());
        }
    }

    public static void main(final String[] args) {
        if (args.length != 2) {
            logger.info("TokenizerClient [inputfile] [outputfile]");
            return;
        }

        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), "UTF-8"));
             final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]), "UTF-8"))) {
            final TokenizerClient client = new TokenizerClient("localhost", 2929);
            client.connect();
            String line;
            String input = "";
            while ((line = reader.readLine()) != null) {
                input += line + "\n";
            }

            final String result = client.process(input);
            writer.write(result + "\n");
            client.close();
        } catch (final Exception e) {
            logger.info(e.getMessage());
            logger.warn(e);
        }
    }

}
