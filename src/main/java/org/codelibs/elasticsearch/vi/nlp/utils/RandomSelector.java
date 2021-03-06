/**
 * Phuong LE HONG, phuonglh@gmail.com
 */
package org.codelibs.elasticsearch.vi.nlp.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author LE HONG Phuong, phuonglh@gmail.com
 * <p>
 * Oct 8, 2009, 2:20:12 PM
 * <p>
 * A selector which selects randomly some elements from a list of elements. This
 * is useful for constructing training and test sets from a corpus.
 */
public class RandomSelector<T> {

    private static final Logger logger = LogManager.getLogger(RandomSelector.class);

    private final List<T> selectedElements = new ArrayList<>();
    private List<T> unselectedElements;;

    /**
     * Constructor of a selector for selecting <code>n</code> elements
     * from a list of elements.
     * @param elements a list of elements
     * @param n an integer
     */
    public RandomSelector(final List<T> elements, final int n) {
        select(elements, n);
    }

    private void select(final List<T> elements, final int n) {
        if (n > elements.size()) {
            logger.error("Error. The size of dataset is less than {}\n", n);
            return;
        }

        unselectedElements = new ArrayList<>(elements);

        // create a set of integers
        final Set<Integer> pool = new HashSet<>();
        for (int i = 0; i < elements.size(); i++) {
            pool.add(i);
        }
        final Random generator = new Random();
        // randomly select n integers from this set
        // each time an integer is selected, it is removed from
        // the set so that the next time we have a different int
        int k;
        for (int j = 0; j < n; j++) {
            boolean nextRound = false;
            do {
                k = generator.nextInt(elements.size());
                if (pool.contains(k)) {
                    selectedElements.add(elements.get(k));
                    unselectedElements.remove(elements.get(k));
                    pool.remove(k);
                    nextRound = true;
                }
            } while (!nextRound);
        }
    }

    /**
     * Returns the list of selected elements.
     * @return a list of selected elements
     */
    public List<T> getSelectedElements() {
        return selectedElements;
    }

    /**
     * Returns the list of unselected elements.
     * @return a list of unselected elements.
     */
    public List<T> getUnselectedElements() {
        return unselectedElements;
    }

    /**
     * For internal test only.
     * @param args
     */
    public static void main(final String[] args) {
        final List<Integer> elements = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            elements.add(i);
        }
        final RandomSelector<Integer> randomSelector = new RandomSelector<>(elements, 5);
        logger.info("Selected elements = ");
        logger.info(randomSelector.getSelectedElements());
        logger.info("Unselected elements = ");
        logger.info(randomSelector.getUnselectedElements());
    }

}
