package vn.hus.nlp.tokenizer.web;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Vector;

import vn.hus.nlp.tokenizer.VietTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author LE HONG Phuong, phuonglh@gmail.com
 * <p>
 * Jul 8, 2009, 11:41:32 AM
 * <p>
 * A tokenizer session.
 */
public class Session extends Thread {

    private static final Logger logger = LogManager.getLogger(Session.class);

    private final VietTokenizer tokenizer;
    private Socket incoming;

    /**
     * @param tokenizer
     */
    public Session(final VietTokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    /**
     * @param s
     */
    public synchronized void setSocket(final Socket s) {
        this.incoming = s;
        notify();
    }

    /* (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    @Override
    public synchronized void run() {
        while (true) {
            try {
                if (incoming == null) {
                    wait();
                }

                logger.info("Socket opening ...");
                final BufferedReader in = new BufferedReader(new InputStreamReader(incoming.getInputStream(), "UTF-8"));
                //PrintStream out = (PrintStream) incoming.getOutputStream();
                final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(incoming.getOutputStream(), "UTF-8"));

                String content = "";

                while (true) {
                    final int ch = in.read();
                    if (ch == 0) {
                        break;
                    }

                    content += (char) ch;
                }
                // tokenize the content
                //
                final StringBuffer result = new StringBuffer(1024);
                final String[] sentences = tokenizer.tokenize(content);
                for (final String s : sentences) {
                    result.append(s);
                }

                // write out the result
                //
                out.write(result.toString().trim());
                out.write((char) 0);
                out.flush();
            } catch (final InterruptedIOException e) {
                logger.info("The connection is interrupted");
            } catch (final Exception e) {
                logger.info(e);
                e.printStackTrace();
            }

            //update pool
            //go back in wait queue if there is fewer than max
            this.setSocket(null);
            final Vector<Session> pool = TokenizerService.pool;
            synchronized (pool) {
                if (pool.size() >= IConstants.MAX_NUMBER_SESSIONS) {
                    /* too many threads, exit this one*/
                    return;
                } else {
                    pool.addElement(this);
                }
            }
        }
    }
}
