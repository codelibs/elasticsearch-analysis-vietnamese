/**
 * Le Hong Phuong, phuonglh@gmail.com
 */
package org.codelibs.elasticsearch.vi.nlp.utils;

import java.io.File;
import java.io.FileFilter;

/**
 * @author Le Hong Phuong, phuonglh@gmail.com
 * <p>
 * 29 juin 2009, 00:50:31
 * <p>
 * Text file filter.
 */
public class TextFileFilter implements FileFilter {

    private String extension = ".txt";

    /**
     * Default constructor.
     */
    public TextFileFilter() {
        // do nothing, use the default extension
    }

    /**
     * Constructs a text file filter given an extension.
     * @param extension
     */
    public TextFileFilter(final String extension) {
        this.extension = extension;
    }

    /* (non-Javadoc)
     * @see java.io.FileFilter#accept(java.io.File)
     */
    @Override
    public boolean accept(final File pathname) {
        if (!pathname.isFile()) {
            return false;
        }
        if (pathname.getName().endsWith(extension)) {
            return true;
        }
        return false;
    }

}
