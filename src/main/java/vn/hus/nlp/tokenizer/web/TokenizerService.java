package vn.hus.nlp.tokenizer.web;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import vn.hus.nlp.tokenizer.VietTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author LE HONG Phuong, phuonglh@gmail.com
 * <p>
 * Jul 7, 2009, 6:29:34 PM
 * <p>
 */
public class TokenizerService extends Thread {

    private static final Logger logger = LogManager.getLogger(TokenizerService.class);

    private int port = 2929;
    private ServerSocket socket;
    public static Vector<Session> pool;

    private VietTokenizer tokenizer = null;

    public TokenizerService() {
        // do nothing
    }

    public TokenizerService(final int p) {
        this.port = p;

    }

    private void init() {
        try {
            // create the tokenizer
            tokenizer = new VietTokenizer();
            /* start session threads */
            pool = new Vector<>();
            for (int i = 0; i < IConstants.MAX_NUMBER_SESSIONS; ++i) {
                final Session w = new Session(tokenizer);
                w.start(); // start a pool of session threads at start-up time
                           // rather than on demand for efficiency
                pool.add(w);
            }
        } catch (final Exception e) {
            logger.info("Error while initializing service:" + e.getMessage());
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        logger.info("Starting tokenizer service!");
        try {
            this.socket = new ServerSocket(this.port);
        } catch (final IOException ioe) {
            logger.info(ioe);
            System.exit(1);
        }

        init();
        logger.info("Tokenizer service started successfully");
        while (true) {
            Socket incoming = null;
            try {
                incoming = this.socket.accept();
                Session w = null;
                synchronized (pool) {
                    if (pool.isEmpty()) {
                        w = new Session(tokenizer);
                        w.setSocket(incoming); // additional sessions
                        w.start();
                    } else {
                        w = pool.elementAt(0);
                        pool.removeElementAt(0);
                        w.setSocket(incoming);
                    }
                }
            } catch (final IOException e) {
                logger.info(e);
                e.printStackTrace();
            }
        }
    }

}
