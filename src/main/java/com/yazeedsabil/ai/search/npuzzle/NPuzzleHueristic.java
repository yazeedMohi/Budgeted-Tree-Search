package com.yazeedsabil.ai.search.npuzzle;

import java.util.ArrayList;

import com.yazeedsabil.ai.search.AbstractSearchNode;
import com.yazeedsabil.ai.search.interfaces.IHeuristicFunction;

/**
 * Manhattan distance heuristic function for the NPuzzle problem.
 */
public class NPuzzleHueristic implements IHeuristicFunction {

    /**
     * The goal node used in every heuristic value calculation.
     */
    private byte[] goalState;
    
    /**
     * Indicates whether we will be using unit or non-unit costs.
     */
    private boolean unitCost;

    /**
     * The length of the row or the column in an n puzzle.
     */
    private int rowLength;

    /**
     * Creates the heuristic function for n puzzles.
     *
     * @param goalNode goal node used in every heuristic value calculation
     */
    public NPuzzleHueristic(AbstractSearchNode goalNode, boolean unitCost) {
        this.goalState = (byte[])goalNode.getState();
        this.unitCost = unitCost;
        this.rowLength = (int)Math.sqrt(goalState.length);
    }

    
    @Override
    public double calculateHeuristic(AbstractSearchNode searchNode) {
        return getManhattanDistance(searchNode) + getLinearConflicts(searchNode);
    }

    /**
     * Calculates the manhattan distance between two NPuzzles. For each position 
     * of search board we find how far away that position is compared to the goal
     * node. We calculate the distance of each position by finding the sum of
     * how far the positions differ in horizontal spaces and vertical spaces. For
     * instance, in a 3x3 board the distance between the top left position to the
     * bottom right position would be 4: from the top left the bottom right is 2
     * rows down and 2 columns right. We ignore the empty space as part of this
     * calculation.
     *
     * @param searchNode the search node whose heuristic value is being calculated
     * @return returns the score
     */
    private double getManhattanDistance(AbstractSearchNode searchNode){
        byte[] currentBoard = (byte[]) searchNode.getState();
        int flatBoardLength = goalState.length;

        int score = 0;
        for (int goalBoardPosition = 0; goalBoardPosition < flatBoardLength;
        goalBoardPosition++) {

            // Empty Space Not Part Of Heuristic Calculation
            if (goalState[goalBoardPosition] == 0 ||
            goalState[goalBoardPosition] == currentBoard[goalBoardPosition])
                continue;

            // Find Position In Search Board That Matches The Current Goal Board
            // Position
            int matchingSpaceOnCurrentNode = 0;
            for (int currentNodePosition = 0; currentNodePosition < flatBoardLength;
             currentNodePosition++)
                if (goalState[goalBoardPosition] == currentBoard[currentNodePosition])
                { matchingSpaceOnCurrentNode = currentNodePosition; break; }

            // Convert One Dimensional Index To Two Dimensional Indices
            byte[] currentIndices = oneDimIndToTwoInd(matchingSpaceOnCurrentNode,
            rowLength);
            byte[] goalIndices = oneDimIndToTwoInd(goalBoardPosition, rowLength);

            // Sum All Differences Between X Positions And Y Positions
            score += getCost(currentBoard[matchingSpaceOnCurrentNode]) *
            (Math.abs(currentIndices[1] - goalIndices[1]) +
            Math.abs(currentIndices[0] - goalIndices[0]));
        }

        return score;
    }

    private double getCost(double t){
        return /*t == 20 ? 0 :*/ (this.unitCost? 1 : ((t+2)/(t+1)));
    }

    /**
     * Converts a 1-dimensional index to a 2-dimensional index in a rowLength x
     * rowLength grid.
     *
     * @param flatIndex the index in one dimension
     * @param rowLength the length of one row/column in the NPuzzle board.
     * @return array representing the two dimensional index
     */
    private byte[] oneDimIndToTwoInd(int flatIndex, int rowLength) {
        return new byte[]{ (byte)(flatIndex % rowLength),
            (byte)(flatIndex / rowLength)};
    }

    /**
     * Calculates the number of linear conflicts between two NPuzzles.  Two tiles ‘a’
     * and ‘b’ are in a linear conflict if they are in the same row or column, also
     * their goal positions are in the same row or column and the goal position o
     * one of the tiles is blocked by the other tile in that row.
     *
     * @param searchNode the search node whose heuristic value is being calculated
     * @return returns the calculated score
     */

    private int getLinearConflicts(AbstractSearchNode searchNode)
    {
        byte[] state = (byte[])searchNode.getState();
        int result = 0;
        ArrayList<Integer> tilesRelated = new ArrayList<>();

        // Loop foreach pair of elements in the row/column
        for(int dimension = 0; dimension < 2; dimension ++)
        {
            for (int i = 0; i < rowLength; i++)
            {
                tilesRelated.clear();
                for (int h = 0; h < rowLength - 1 && !tilesRelated.contains(h); h++)
                {
                    for (int k = h + 1; k < rowLength && !tilesRelated.contains(h);
                    k++)
                    {
                        // Avoid the empty tile
                        if (dimension == 1 && (state[i*rowLength + h] == 0 ||
                         state[i*rowLength + k] == 0)) continue;
                        if (dimension == 0 && (state[h*rowLength + i] == 0 ||
                         state[k*rowLength + i] == 0)) continue;

                        Boolean conflicts = dimension == 1 
                            ? InConflict(i, state[i*rowLength + h],
                             state[i*rowLength + k], h, k, dimension, rowLength) 
                            : InConflict(i, state[h*rowLength + i],
                             state[k*rowLength + i], h, k, dimension, rowLength);
                        
                        // If no conflicts continue
                        if (!conflicts) continue;

                        // Add at least two moves to the result, with the value of 
                        // cost depending on the puzzle's cost function.
                        result += dimension == 1 
                        ? (getCost(state[i*rowLength + h]) +
                         getCost(state[i*rowLength + k]))
                        : (getCost(state[h*rowLength + i]) +
                         getCost(state[k*rowLength + i]));
                        tilesRelated.add(h);
                        tilesRelated.add(k);
                        break;
                    }
                }
            }
        }

        return result;
    }

    /**
     * Checks whether two positions are in conflict.
     *
     * @param index the index in one dimension.
     * @param a value inside the first location.
     * @param b value inside the second location.
     * @param indexA index of the first location.
     * @param indexB index of the second location.
     * @param dimension whether we're checking rows or columns.
     * @param rowLength the length of one row/column in the NPuzzle board.
     * @return boolean representing whether a conflict is present. 
     */

    private boolean InConflict(int index, int a, int b, int indexA, int indexB,
    int dimension, int rowLength)
    {
        int indexGoalA = -1;
        int indexGoalB = -1;

        // Loop through the row/column
        for (int c = 0; c < rowLength; c++)
        {
            if ((dimension == 1 && goalState[index*rowLength + c] == a) ||
            (dimension == 0 && goalState[c*rowLength + index] == a))
                indexGoalA = c;
            else if ((dimension == 1 && goalState[index*rowLength +c] == b) ||
            (dimension == 0 && goalState[c*rowLength + index]== b))
                indexGoalB = c;
        }

        // Check if one of the tiles must move away for the other to replace it.
        return (indexGoalA >= 0 && indexGoalB >= 0) &&
        ((indexA < indexB && indexGoalA > indexGoalB) ||
         (indexA > indexB && indexGoalA < indexGoalB));
    }
}
