package com.yazeedsabil.ai.search.hanoi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

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
            // if(getState()[plate] == -1) continue;

            for(byte peg = 0; peg < PEGS; peg ++)
            {
                if(peg == getState()[plate]) continue;

                boolean filled = false;

                // if(plate != 0)
                // {
                    for(byte prevPlate = 0; prevPlate < plate; prevPlate ++)
                    {
                        if(getState()[prevPlate] == peg || getState()[prevPlate] == getState()[plate]) {filled = true; break;}
                    }
                // }

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
    
    private static void solveBoard(byte[] initStateArray, int[] PDB) throws IOException
    {
        // byte[] initStateArray = {0, 0, 0, 0, 0, 0, 0};
        // byte[] goalStateArray = {4, 4, 4, 4, 4, 4, 4};
        byte[] goalStateArray = new byte[initStateArray.length];
        Arrays.fill(goalStateArray, (byte)(PEGS-1));
        FivePegHanoi initialState = new FivePegHanoi(initStateArray);
        FivePegHanoi goalState = new FivePegHanoi(goalStateArray);

        HanoiHeuristic heuristicFunction = new HanoiHeuristic((byte)PEGS, PDB);

        System.out.println("H: "+ heuristicFunction.calculateHeuristic(initialState));
        
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

        int step = 1;
        for (AbstractSearchNode node : path) {
            System.out.println("Step " + step);
            System.out.println(node);
            step++;
        }

        System.out.println("Pegs: "+ PEGS + " | Plates: "+goalStateArray.length + " | Time: " + ((System.currentTimeMillis() - startTime) / (1000.0)) + "s | Solution length: " + cost);
    }

    private final static int PEGS = 5;
    // create one additive PDB for 6 dishes
    public static void main(String[] args) throws IOException
    {
        // for(int plates = 1; plates < 13; plates ++)
        //     solveBoard(new byte[plates]);
        
        // for(int plates = 3; plates < 13; plates ++)
        // {
        //     long startTime = System.currentTimeMillis();

        //     int[] PDB = buildPDB(plates);

        //     writePattern(PDB, "Pattern-"+plates);

        //     System.out.println("Pegs: "+ PEGS + " | Plates: " + plates + " | Time: " + ((System.currentTimeMillis() - startTime) / (1000.0)) + "s | DB entries: " + PDB.length);
        // }

        // writePattern(buildPDB(8), "Pattern-8");
        
        int dishes = 13;
        int pdbDishes = 12;

        solveBoard(new byte[dishes], readPattern("Pattern-"+pdbDishes));
    }

    /**
     * Converts the puzzle into its string representation as a simple diagram of PEGS and plates. A | is used
     * represent the empty space, and *s represent the plates.
     *
     * @return a string representation of the puzzle
     */


    private static int[] buildPDB(int dishes)
    {
        byte[] goalStateArray = new byte[dishes];
        Arrays.fill(goalStateArray, (byte)(PEGS-1));
        int[] PDB = new int[(int)Math.pow(PEGS, dishes)];
        Arrays.fill(PDB, -1);
        PDB[rank(goalStateArray)] = 0;

        for(int solutionLength = 0; solutionLength < (dishes - 2) * (PEGS - 1) * 2; solutionLength ++)
        {
            for(int i = 0; i < PDB.length; i++)
            {
                if(PDB[i] != solutionLength) continue;

                byte[] state = unrank(i, dishes);
                for(AbstractSearchNode succ : (new FivePegHanoi(state)).getSuccessors())
                {
                    int rank = rank((byte[])succ.getState());
                    if(PDB[rank] == -1) PDB[rank] = solutionLength + 1;
                }
            }
        }

        System.out.println("Done building PDB");
        return PDB;
    }

    public static int rank(byte[] state)
    {
        int res = 0;

        for(int i = 0; i < state.length; i ++) res += (int)(state[i] * Math.pow(PEGS, i));

        return res;
    }

    public static byte[] unrank(int rank, int dishes)
    {
        byte[] state = new byte[dishes];

        for(int i = 0; i < state.length; i ++)
        {
            state[i] = (byte)(rank % PEGS);
            rank /= PEGS;
        }

        return state;
    }

    public static void writePattern(int[] map, String file) {
        try {
            File fileOne=new File("src\\main\\java\\com\\yazeedsabil\\ai\\search\\hanoi\\PDBs\\" + file);
            FileOutputStream fos=new FileOutputStream(fileOne);
            ObjectOutputStream oos=new ObjectOutputStream(fos);
    
            oos.writeObject(map);
            oos.flush();
            oos.close();
            fos.close();
        } catch(Exception e) {}
    
        //read from file 
    }

    private static int[] readPattern(String file){
        try {
            File toRead=new File("src\\main\\java\\com\\yazeedsabil\\ai\\search\\hanoi\\PDBs\\" + file);
            FileInputStream fis=new FileInputStream(toRead);
            ObjectInputStream ois=new ObjectInputStream(fis);
    
            int[] mapInFile=(int[])ois.readObject();
    
            ois.close();
            fis.close();

            return mapInFile;
        } catch(Exception e) { return null;}
    }

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