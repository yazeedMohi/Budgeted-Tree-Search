package com.yazeedsabil.ai.search.hanoi;

import java.io.IOException;
import java.util.List;

import com.yazeedsabil.ai.search.AStarSearch;
import com.yazeedsabil.ai.search.AbstractSearchNode;
import com.yazeedsabil.ai.search.interfaces.IHeuristicFunction;

/**
 * Manhattan distance heuristic function for the NPuzzle problem.
 */
public class HanoiHeuristic implements IHeuristicFunction {

    /**
     * The goal node used in every heuristic value calculation.
     */
    private byte pegs;

    /**
     * Creates the heuristic function for n puzzles.
     *
     * @param goalNode goal node used in every heuristic value calculation
     */
    public HanoiHeuristic(byte pegs) {
        this.pegs = pegs;
    }

    
    @Override
    public double calculateHeuristic(AbstractSearchNode searchNode) {
        byte[] state = (byte[])searchNode.getState();
        double result = 0;
        for(int plate = 0; plate < ((byte[])searchNode.getState()).length; plate ++)
        {
            if(state[plate] == -1) continue;

            if(state[plate] != (pegs - 1))
            {
                result++;
                continue;
            }

            if(plate == state.length - 1) continue;

            for(byte prevPlate = (byte)(state.length - 1); prevPlate > plate; prevPlate--)
            {
                if(state[prevPlate] != (pegs - 1) && state[prevPlate] != -1) {result += 2; break;}
            }
        }

        return result;
    }
}