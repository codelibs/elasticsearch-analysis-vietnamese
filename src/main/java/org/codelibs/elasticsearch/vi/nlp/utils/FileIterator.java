/**
 * Le Hong Phuong, phuonglh@gmail.com
 */
package org.codelibs.elasticsearch.vi.nlp.utils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Le Hong Phuong, phuonglh@gmail.com
 * <p>
 * 29 juin 2009, 00:58:26
 * <p>
 * This utility iterates all the text file (with suffix .txt) in a directory. The files
 * are recursively browsed.
 */
public class FileIterator {

    private static final Logger logger = LogManager.getLogger(FileIterator.class);

    private FileIterator() {
    }

    /**
     * Get all files in a directory which satisfy a given file filter.
     * @param directory a directory to look for files
     * @param fileFilter a file filter
     * @return an array of file
     */
    public static File[] listFiles(final File directory, final FileFilter fileFilter) {
        final List<File> result = new ArrayList<>();
        if (directory.isDirectory()) {
            // get all sub directories and files in this directory
            final File[] files = directory.listFiles();
            for (final File f : files) {
                if (f.isDirectory()) {
                    // recursively get files
                    result.addAll(Arrays.asList(listFiles(f, fileFilter)));
                } else {
                    if (fileFilter.accept(f)) {
                        result.add(f);
                    }
                }
            }
        }
        return result.toArray(new File[result.size()]);
    }

    /**
     * @param args
     */
    public static void main(final String[] args) {
        final FileFilter textFileFilter = new TextFileFilter();
        final File directory = new File("samples");
        final File[] files = FileIterator.listFiles(directory, textFileFilter);
        for (final File file : files) {
            logger.info(file.getAbsolutePath());
        }
    }

}
