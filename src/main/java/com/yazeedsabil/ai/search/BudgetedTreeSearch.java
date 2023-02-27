package com.yazeedsabil.ai.search;

import com.yazeedsabil.ai.search.interfaces.IHeuristicFunction;

import java.util.List;

/**
 * An implementation of Budgeted Tree Search to find an optimal path in a 
 * state space.
 */
public class BudgetedTreeSearch {

    private AbstractSearchNode initialState;
    private AbstractSearchNode goalState;
    private IHeuristicFunction heuristicFunction;

    /**
     * Creates a BudgetedTreeSearch object with initial state, goal state, 
     * and a heuristic function.
     *
     * @param initialState      the state where the search begins
     * @param goalState         the state where the search ends
     * @param heuristicFunction the heuristic function used to score nodes
     */
    public BudgetedTreeSearch(AbstractSearchNode initialState,
    AbstractSearchNode goalState, IHeuristicFunction heuristicFunction) {
        this.initialState = initialState;
        this.goalState = goalState;
        this.heuristicFunction = heuristicFunction;
    }

    private double solutionCost, fBelow, fAbove, solutionLowerBound;
    private AbstractSearchNode solutionPath;
    private long nodes, expanded, generated;

    /**
     * Begins the Budgeted Tree search. Will return null if the goal node cannot 
     * be found. Returns an AbstractSearchNode that is the last node on the optimal path.
     * You can traverse the optimal path by following each nodes parent until you 
     * arrive back  to the initial node (parent is null).
     *
     * @return null if path does not exist, otherwise the last node on the optimal
     * path
     */
    public Object[] search(int c1, int c2, int gamma, Boolean expGrowth) {

        solutionPath = null;
        solutionCost = Double.MAX_VALUE;
        long budget = 1;

        double[] i = {this.heuristicFunction.calculateHeuristic(this.initialState),
            Double.MAX_VALUE};

        while(solutionCost > i[0]){
            solutionLowerBound = i[0];
            
            double[] s = LimitedSearch(i[0], Long.MAX_VALUE);
            i = new double[]{Math.max(i[0], s[0]), Math.min(i[1], s[1])};
            if(nodes >= c1 * budget){
                budget = nodes;
                i[1] = Double.MAX_VALUE;
                continue;
            }
            
            double delta = 1;

            while(!(almostEqual(i[1], i[0], 0.01) || 
            (c1 * budget <= nodes && nodes < c2 * budget)))
            {
                double nextCost;
                delta *= gamma;
                if(i[1] == Double.MAX_VALUE)
                    nextCost = expGrowth? (i[0] + delta) : (i[0] * gamma);
                else 
                    nextCost = (i[0] + i[1]) / 2.0;
                
                solutionLowerBound = i[0];
                s = LimitedSearch(nextCost, c2 * budget);
                i = new double[]{Math.max(i[0], s[0]), Math.min(i[1], s[1])};
                System.out.println(i[0] + " => " + i[1]);
            }

            budget = Math.max(nodes, c1 * budget);
            i[1] = Double.MAX_VALUE;

            if(solutionCost == i[0]) 
                return new Object[]{solutionPath, expanded, generated};
        }

        return new Object[]{solutionPath, expanded, generated};
    }


    /**
     * Checks whether the values of two doubles are within a certain margin 
     * of error between each other
     * 
     * @param a     first double value
     * @param b     second double value
     * @param eps   margin of error (epsilon)
     * @return      boolean representing whether the two doubles are almost equal
     */
    public static boolean almostEqual(double a, double b, double eps){
        return Math.abs(a-b)<eps;
    }

    private double[] LimitedSearch(double costLimit, long nodeLimit){
        fBelow = 0;
        fAbove = Double.MAX_VALUE;
        nodes = 0;
        LimitedDFS(initialState, 0, costLimit, nodeLimit);
        expanded += nodes;

        if(nodes >= nodeLimit) 
            return new double[]{0, fBelow};
        else if(solutionCost != Double.MAX_VALUE && fBelow >= solutionCost) 
            return new double[] {solutionCost, solutionCost};
        else 
            return new double[]{fAbove, Double.MAX_VALUE};
    }

    private void LimitedDFS(AbstractSearchNode currState, double pathCost,
    double costLimit, long nodeLimit)
    {
        double currF = pathCost + heuristicFunction.calculateHeuristic(currState);
        
        if(almostEqual(solutionCost, solutionLowerBound, 0.01))
        {
            return;
        }
        else if(currF > costLimit)
        {
            fAbove = Math.min(fAbove, currF);
            return;
        }
        else if(currF > solutionCost || almostEqual(currF, solutionCost, 0.01))
        {
            fBelow = solutionCost;
            return;
        }
        else
            fBelow = Math.max(currF, fBelow);

        if(nodes >= nodeLimit)
            return;

        if(currState.equals(goalState)){
            if(currF < solutionCost)
            {
                solutionPath = currState;
                solutionCost = currF;
            }
            return;
        }

        nodes++;

        for(AbstractSearchNode child : 
        (List<AbstractSearchNode>)currState.getSuccessors())
        {
            generated++;
            if (currState.getParent() == null || !currState.getParent().equals(child))
                LimitedDFS(child, pathCost + child.distFromParent(), costLimit, nodeLimit);
        }
    }
}

// fix hur imp: 0:42
// new succ: 0:49
// operators 1:10
// new huersitic 0:51

// GAMMA = 2
// parent 1:17
// path 2:08

// 4,16: 1:15
// 2,16: 1:15
// 2,8: 1:16
// 2,6: 2:06
// 2,4: 3:40
// 2,2: 

// GAMMA = 3
// 2,8: 2:16

// NON exp growth:
// 2,8,2: 6:22
// 2,8,4: 6:24