package com.yazeedsabil.ai.search.hanoi;

import com.yazeedsabil.ai.search.AbstractSearchNode;

import java.util.Arrays;


/**
 * Abstract class that represents the generic NPuzzle Problem. Contains methods that will work with any N value.
 */
public abstract class AbstractHanoi extends AbstractSearchNode<byte[]> {
    /**
     * Returns the path length between current node and its parent. Since there will always be a single move difference
     * between a puzzle and its parent, this will always return 1.
     * 
     * However, this will be overridden in within the puzzle class in case we use non-unit costs.
     * 
     *                      *    
     *                     ***       ******
     *                    *****     ********    .
     *                           [0, 0, 0, 1, 1]
     * 
     *                  [1, 2, 3]    [4, 5]     [0]
     *
     * @return distance from parent to node
     */
    @Override
    public double distFromParent() {
        return 1;
    }

    /**
     * Returns the hash code for the puzzle board. Only compares state, so a AbstractNPuzzle's with the same board
     * layout but different f(),h(). and g() scores will still collide.
     *
     * @return hash code for puzzle
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.getState());
    }

    /**
     * Determines if two puzzles are equal to one another. Puzzles are equal if their boards(state) are equal to each
     * other. f(),h(), or g() are not taken into consideration for equality. This must match the hash function.
     *
     * @return true if puzzles(state) are equal and false if not
     */
    @Override
    public boolean equals(Object otherPuzzle) {

        if (otherPuzzle instanceof AbstractHanoi) {
            return Arrays.equals(((AbstractHanoi) otherPuzzle).getState(), this.getState());
        }

        return false;
    }
}
