package com.yazeedsabil.ai.search;

import com.yazeedsabil.ai.search.interfaces.IHeuristicFunction;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of IDA* search to find an optimal path in a state space.
 */
public class IDAStarSearch {

    private AbstractSearchNode initialState;
    private AbstractSearchNode goalState;
    private IHeuristicFunction heuristicFunction;

    /**
     * Creates an IDAStarSearch object with initial state, goal state, and
     * a heuristic function.
     *
     * @param initialState      the state where the search begins
     * @param goalState         the state where the search ends
     * @param heuristicFunction the heuristic function used to score nodes
     */
    public IDAStarSearch(AbstractSearchNode initialState, AbstractSearchNode 
    goalState, IHeuristicFunction heuristicFunction) {
        this.initialState = initialState;
        this.goalState = goalState;
        this.heuristicFunction = heuristicFunction;
    }

    /**
     * Begins the IDA* search. Will return null if the goal node cannot be found.
     * Returns a AbstractSearchNode that is the last node on the optimal path.
     * You can traverse the optimal path by following each nodes parent
     * until you arrive back to the initial node (parent is null).
     *
     * @return null if path does not exist, otherwise the last node on the optimal
     * path
     */

    private long expanded, generated;
    private AbstractSearchNode solutionPath;

    long lines = 0;

    public Object[] search(BufferedWriter bw) throws IOException {

        // Find Initial F Bound
        double currentFBound = this.heuristicFunction.calculateHeuristic(this.initialState);
        expanded = 0;
        generated = 0;
        // Set Root of Path To Initial Node
        // ArrayList<AbstractSearchNode> path = new ArrayList<>();
        // path.add(0, this.initialState);

        // Keep Retrying With Larger F Bound Until One Of The Follow:
        // 0 Is Returned     - The Goal Node Is Found So Path Contains Optimal Path
        // Integer.MAX_Value - No Node Was Found With A F Higher Than F Boundary 
        // So Goal Node Does Not Exist
        double smallestNewFBound;
        do {
            // Start Search
            if(bw != null)
            {
                String str = "F bound current: "+currentFBound + " | " 
                + expanded + " expanded | " +generated +" generated";
                System.out.println(str);
                bw.write(str);
                bw.newLine();
                lines++;
                if(lines > 100){
                    bw.write("Interrupted");
                    bw.close();
                    return null;
                }
            }

            smallestNewFBound = recur_search(this.initialState, 0,
            currentFBound);

            // Check If Goal Node Was Found
            if (smallestNewFBound == 0.0)
            {
                return new Object[]{solutionPath, expanded, generated};
            }

            // Set New F Boundary
            currentFBound = smallestNewFBound;

        } while (currentFBound != Double.MAX_VALUE);

        return null;
    }

    /**
     * Recursively searches down the children of nodes. Will prevent itself from
     * search down path with higher f than current f boundary. If paths with higher
     * f boundary are found then it will return the smallest f over the boundary 
     * found. This smallest f over f boundary is a potential new f boundary during 
     * the next iteration. Will return 0 if goal node is found and Integer.MAX_VALUE
     * if there is not a single path with a f greater than the
     * f boundary, meaning the goal node cannot be found.
     *
     * @param path          list of nodes ordered by the order they were visited
     * @param graphCost     current graph cost to get to the current node
     * @param currentFBound the max f boundary for current iteration
     * @return the smallest f value in the iteration that was greater than the 
     * fBoundary for the iteration
     */
    private double recur_search(AbstractSearchNode currState, double graphCost,
    double currentFBound) {

        // Set G, H, and F of Current Node
        // AbstractSearchNode currentNode = path.get(path.size() - 1);
        currState.setH(this.heuristicFunction.calculateHeuristic(currState));
        // System.out.println(currentNode.getH());
        currState.setG(graphCost);
        currState.setF(graphCost + currState.getH());


        
        // Current Node Has F Larger Than Current Bound
        if (currState.getF() > currentFBound)
            return currState.getF();

        // Found The Goal Node -> Send Signal To End Recursion
        if (currState.equals(this.goalState))
        {
            solutionPath = currState;
            return 0.0;
        }
        // else System.out.println(currentNode.getH());

        // If This Stays Integer.MAX_VALUE Then All Paths Explored Were Smaller 
        // Than F Bound
        double minFFound = Double.MAX_VALUE;

        expanded++;
        
        // Expand Search To Each Child Node
        for (AbstractSearchNode child :
        (List<AbstractSearchNode>)currState.getSuccessors()) {
            generated++;
            // Verify Child Node Is Not Already On The Current Search Path
            if (currState.getParent() == null ||
            !currState.getParent().equals(child)){

                // Add Child Tp Path And Then Continue Search Down The Path
                // path.add(child);
                double minFOverBound = recur_search(child, currState.getG() +
                child.distFromParent(), currentFBound);

                // Signals To End Recursion When Goal Is Found
                if (minFOverBound == 0.0) return 0.0;

                // Keep Track Of The Smallest F Found Over Bound Generated By 
                // Each Child's Search Path
                if (minFOverBound < minFFound) minFFound = minFOverBound;

                // Remove Child From Search Path Before Exploring Next Child
                // path.remove(path.size() - 1);
            }
        }

        return minFFound;
    }
}
