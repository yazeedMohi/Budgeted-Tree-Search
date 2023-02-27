package com.yazeedsabil.ai.search.interfaces;

import com.yazeedsabil.ai.search.AbstractSearchNode;

/**
 * Represents a heuristic function to be used with AStarSearch, IDAStarSearch, or BudgetedTreeSearch. This function MUST be admissible.
 */
public interface IHeuristicFunction {

    /**
     * Calculates the heuristic score for the current search node when compared to the goal node. The goal node should
     * be contained in the implemented IHeuristicFunction class.
     *
     * @param searchNode the node being compared to the goal
     * @return the heuristic score for the search node
     */
    public double calculateHeuristic(AbstractSearchNode searchNode);
}
