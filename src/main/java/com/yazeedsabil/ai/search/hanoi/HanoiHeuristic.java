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


    private int[] PDB;

    /**
     * Creates the heuristic function for n puzzles.
     *
     * @param goalNode goal node used in every heuristic value calculation
     */
    public HanoiHeuristic(byte pegs, int[] PDB) {
        this.pegs = pegs;
        this.PDB = PDB;
    }

    
    @Override
    public double calculateHeuristic(AbstractSearchNode searchNode) {
        // return naiiveHeurstic(searchNode);
        return additivePDB(searchNode, pegs);
    }

    private double naiiveHeurstic(AbstractSearchNode searchNode)
    {
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


    // random three lookups
    // or max two

    int[] perms1 = {0, 2, 4, 6, 8, 10};
    int[] perms2 = {1, 3, 5, 7, 9, 11};

    private double additivePDB(AbstractSearchNode searchNode, int pegs)
    {
        byte[] state = (byte[])searchNode.getState();
        byte[] p1 = new byte[state.length / 2];
        byte[] p2 = new byte[state.length / 2];
        
        int j = 0;
        for(int i : perms1)
        {
            p1[j] = state[i];
            j++;
        }

        j = 0;
        for(int i : perms2)
        {
            p2[j] = state[i];
            j++;
        }

        // System.out.println(PDB[FivePegHanoi.rank(p1)] + " " + PDB[FivePegHanoi.rank(p2)]);
        // System.exit(1);
        return PDB[FivePegHanoi.rank(p1)] + PDB[FivePegHanoi.rank(p2)];
    }
}