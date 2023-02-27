package com.yazeedsabil.ai.search;

import com.yazeedsabil.ai.search.interfaces.IHeuristicFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;


/**
 * An implementation of A* search to find an optimal path in a state space.
 */
public class AStarSearch {

    private AbstractSearchNode initialState;
    private AbstractSearchNode goalState;
    private IHeuristicFunction heuristicFunction;

    /**
     * Creates an AStarSearch object with initial state, goal state, and a heuristic function.
     *
     * @param initialState      the state where the search begins
     * @param goalState         the state where the search ends
     * @param heuristicFunction the heuristic function used to score nodes
     */
    public AStarSearch(AbstractSearchNode initialState, AbstractSearchNode goalState,
                       IHeuristicFunction heuristicFunction) {
        this.initialState = initialState;
        this.goalState = goalState;
        this.heuristicFunction = heuristicFunction;
    }

    /**
     * Begins the A* search. Will return null if the goal node cannot be found. Returns a AbstractSearchNode that
     * is the last node on the optimal path. You can traverse the optimal path by following each nodes parent
     * until you arrive back to the initial node (parent is null).
     *
     * @return null if path does not exist, otherwise the last node on the optimal path
     */
    public AbstractSearchNode search() {

        PriorityQueue<AbstractSearchNode> openSet = new PriorityQueue<AbstractSearchNode>(1, goalState);
        // Used to quickly determine if node with same state is in the open set and to retrieve that states best f
        HashMap<AbstractSearchNode, Double> openSetHash = new HashMap<>();
        HashSet<AbstractSearchNode> closedSet = new HashSet<>();

        // Set Initial Nodes F To Heuristic Score
        this.initialState.setF(this.heuristicFunction.calculateHeuristic(this.initialState));

        // Add Initial Node Into Queue/HashMap
        openSet.add(this.initialState);
        openSetHash.put(this.initialState, this.initialState.getF());

        while (!openSet.isEmpty()) {
            AbstractSearchNode currentState = openSet.poll();

            // Check If Goal Is Found
            if (currentState.equals(this.goalState))
                return currentState;

            // Add To Closed Set Since We Have Generated The Nodes Successors
            closedSet.add(currentState);
            List<AbstractSearchNode> childrenStates = currentState.getSuccessors();

            for (AbstractSearchNode childState : childrenStates) {

                // If Child Node's Children Has Already Been Generated Then Skip Child
                if (closedSet.contains(childState))
                    continue;

                childState.setG(childState.getParent().getG() + childState.distFromParent());
                childState.setH(this.heuristicFunction.calculateHeuristic(childState));
                childState.setF(childState.getG() + childState.getH());

                // If Child Node Is In Queue And The Version In Queue Has A Smaller F() Then Do Not Add The Child To Queue
                // Note the short circuit logic
                if (openSetHash.containsKey(childState) && openSetHash.get(childState) < childState.getF())
                    continue;

                openSet.add(childState);
                openSetHash.put(childState, childState.getF());
            }
        }

        return null;
    }
}
