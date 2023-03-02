package com.yazeedsabil.ai.search.hanoi;

import java.util.ArrayList;
import java.util.Arrays;

import java.io.IOException;
import java.util.List;

import com.yazeedsabil.ai.search.AStarSearch;
import com.yazeedsabil.ai.search.AbstractSearchNode;
import com.yazeedsabil.ai.search.BudgetedTreeSearch;
import com.yazeedsabil.ai.search.IDAStarSearch;

public class FivePegHanoi extends AbstractHanoi {

    /**
     * Creates an FivePegHanoi object with a puzzle board for its state.
     *
     * @param puzzleBoard       the state of the node
     */
    public FivePegHanoi(byte[] puzzleBoard) {
        this.setState(puzzleBoard);
    }

    /**
     * Another constructor for creating an FivePegHanoi object with a puzzle board for its state.
     * This one includes a parent node.
     *
     * @param puzzleBoard       the state of the node
     * @param parentPuzzle      reference to the parent of this node
     */
    public FivePegHanoi(byte[] puzzleBoard, FivePegHanoi parentPuzzle) {
        this(puzzleBoard);
        this.setParent(parentPuzzle);
    }

    /**
     * A third constructor for creating an FivePegHanoi object with a puzzle board for its state.
     * This one includes an integer representing the action that took us from the parent node to this node.
     *
     * @param puzzleBoard       the state of the node
     * @param parentPuzzle      reference to the parent of this node
     */
    public FivePegHanoi(byte[] puzzleBoard, byte operator) {
        this(puzzleBoard);
        this.setOperator(operator);
    }

    /**
     * The cost function for the puzzle.  Either unit cost or (t+2)/(t+1) variable cost.
     *
     * @return  the cost for going from the parent node to this node
     */
    @Override
    public double distFromParent() {
        if(this.getParent() == null) return 0;
        return 1;
    }

    /**
     * The successor function for the puzzle.  Returns a list of children nodes by applying legal
     * actions to the current node.
     *
     * @return  successors list
     */
    @Override
    public ArrayList<AbstractSearchNode> getSuccessors() {
        ArrayList<AbstractSearchNode> successors = new ArrayList<>();
        for(int plate = 0; plate < getState().length; plate ++)
        {
            if(getState()[plate] == -1) continue;

            for(byte peg = 0; peg < PEGS; peg ++)
            {
                if(peg == getState()[plate]) continue;

                boolean filled = false;

                if(plate != 0)
                {
                    for(byte prevPlate = 0; prevPlate < plate; prevPlate ++)
                    {
                        if(getState()[prevPlate] == peg || getState()[prevPlate] == getState()[plate]) {filled = true; break;}
                    }
                }

                if(!filled)
                {
                    byte[] succState = Arrays.copyOf(getState(), getState().length);
                    succState[plate] = peg;
                    FivePegHanoi successor = new FivePegHanoi(succState, this);
                    successors.add(successor);
                }
            }
        }

        return successors;
    }
    
    private static void solveBoard(byte[] initStateArray) throws IOException
    {
        // byte[] initStateArray = {0, 0, 0, 0, 0, 0, 0};
        // byte[] goalStateArray = {4, 4, 4, 4, 4, 4, 4};
        byte[] goalStateArray = new byte[initStateArray.length];
        Arrays.fill(goalStateArray, (byte)(PEGS-1));
        FivePegHanoi initialState = new FivePegHanoi(initStateArray);
        FivePegHanoi goalState = new FivePegHanoi(goalStateArray);

        HanoiHeuristic heuristicFunction = new HanoiHeuristic((byte)PEGS);
        
        long startTime = System.currentTimeMillis();

        AStarSearch searcher = new AStarSearch(initialState, goalState, heuristicFunction);
        AbstractSearchNode finalSearchNode = searcher.search();

        // System.out.println("A* Time: " + ((System.currentTimeMillis() - startTime) / (1000.0)));

        // startTime = System.currentTimeMillis();

        // IDAStarSearch searcher2 = new IDAStarSearch(initialState, goalState, heuristicFunction);
        // finalSearchNode = (AbstractSearchNode)searcher2.search(null)[0];

        // System.out.println("IDA* Time: " + ((System.currentTimeMillis() - startTime) / (1000.0)));

        // startTime = System.currentTimeMillis();

        // BudgetedTreeSearch searcher3 = new BudgetedTreeSearch(initialState, goalState, heuristicFunction);
        // finalSearchNode = (AbstractSearchNode)searcher3.search(8, 8, 2, true)[0];

        // System.out.println("BTS Time: " + ((System.currentTimeMillis() - startTime) / (1000.0)));

        List<AbstractSearchNode> path = AbstractSearchNode.getPath(finalSearchNode);
        double cost = 0;
        for(AbstractSearchNode c : path) cost+=c.distFromParent();

        System.out.println("Pegs: "+ PEGS + " | Plates: "+goalStateArray.length + " | Time: " + ((System.currentTimeMillis() - startTime) / (1000.0)) + "s | Solution length: " + cost);

        // int step = 1;
        // for (AbstractSearchNode node : path) {
        //     System.out.println("Step " + step);
        //     System.out.println(node);
        //     step++;
        // }
    }

    private final static int PEGS = 5;
    
    public static void main(String[] args) throws IOException
    {
        for(int plates = 1; plates < 13; plates ++)
            solveBoard(new byte[plates]);
    }

    /**
     * Converts the puzzle into its string representation as a simple diagram of PEGS and plates. A | is used
     * represent the empty space, and *s represent the plates.
     *
     * @return a string representation of the puzzle
     */

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        byte[][] printFriendly = new byte[PEGS][getState().length];
        byte[] indices = new byte[PEGS];

        for(byte plate = (byte)(getState().length - 1); plate >= 0; plate--)
        {
            if(getState()[plate] == -1) continue;
            printFriendly[getState()[plate]][indices[getState()[plate]]] = (byte)(plate + 1);
            indices[getState()[plate]]++;
        }

        for(int i = printFriendly[0].length - 1; i >= 0 ; i--)
        {
            for(int j = 0; j < printFriendly.length; j++)
            {
                stringBuilder.append(" ".repeat(2 + (printFriendly[0].length - Math.max(printFriendly[j][i], 1))));
                stringBuilder.append(printFriendly[j][i] == 0 ? "|" :"*".repeat(2 * printFriendly[j][i] - 1));
                stringBuilder.append(" ".repeat(2 + (printFriendly[0].length - Math.max(printFriendly[j][i], 1))));
            }
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }
}