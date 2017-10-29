/**
 * (C) Le Hong Phuong, phuonglh@gmail.com
 */
package vn.hus.nlp.graph.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Le Hong Phuong, phuonglh@gmail.com
 * <p>
 * Oct 20, 2007, 11:29:53 AM
 * <p>
 * An index exception.
 */
public class IndexException extends Exception {

    private static final Logger logger = LogManager.getLogger(IndexException.class);

    private static final long serialVersionUID = 1L;

    public IndexException() {
        super();
    }

    @Override
    public void printStackTrace() {
        super.printStackTrace();
        logger.error("The vertex index is not valid!");
    }
}
