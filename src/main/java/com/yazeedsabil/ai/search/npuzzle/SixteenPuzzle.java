package com.yazeedsabil.ai.search.npuzzle;

import com.yazeedsabil.ai.search.AStarSearch;
import com.yazeedsabil.ai.search.AbstractSearchNode;
import com.yazeedsabil.ai.search.BudgetedTreeSearch;
import com.yazeedsabil.ai.search.IDAStarSearch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;


public class SixteenPuzzle extends AbstractNPuzzle {

    /**
     * Creates an SixteenPuzzle object with a puzzle board for its state.
     *
     * @param puzzleBoard       the state of the node
     */
    public SixteenPuzzle(byte[] puzzleBoard) {
        // NOTE: Validation checks commented-out to improve performance - they seldom help anyways while testing.

        // if (puzzleBoard == null) throw new IllegalArgumentException("16Puzzle Board's Cannot Be Null");

        // if (puzzleBoard.length != 16) throw new IllegalArgumentException("16Puzzle Board's Must Have 16 Spaces(Array Length of 16)");

        this.setState(puzzleBoard);

        for (int currentSpace = 0; currentSpace < puzzleBoard.length; currentSpace++) if (puzzleBoard[currentSpace] == 0) { this.setEmptySpaceLocation(currentSpace); break; }

        // if (this.getEmptySpaceLocation() == -1) throw new IllegalArgumentException("All NPuzzle Boards Most Contain an Empty Space(0 Must Be In The Array)");
    }

    /**
     * Another constructor for creating an SixteenPuzzle object with a puzzle board for its state.
     * This one includes a parent node.
     *
     * @param puzzleBoard       the state of the node
     * @param parentPuzzle      reference to the parent of this node
     */
    public SixteenPuzzle(byte[] puzzleBoard, SixteenPuzzle parentPuzzle) {
        this(puzzleBoard);
        this.setParent(parentPuzzle);
    }

    /**
     * A third constructor for creating an SixteenPuzzle object with a puzzle board for its state.
     * This one includes an integer representing the action that took us from the parent node to this node.
     *
     * @param puzzleBoard       the state of the node
     * @param parentPuzzle      reference to the parent of this node
     */
    public SixteenPuzzle(byte[] puzzleBoard, int operator) {
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

        if(unitCost) return 1;
        double t = this.getState()[((SixteenPuzzle)this.getParent()).getEmptySpaceLocation()];
        return (t+2)/(t+1);

        // double t = this.getState()[((SixteenPuzzle)this.getParent()).getEmptySpaceLocation()];

        // if(t == 20) return 0;
        // if(unitCost) return 1;
        // return (t+2)/(t+1);
    }

    public double distFromParentPattern() {
        if(this.getParent() == null) return 0;
        double t = this.getState()[((SixteenPuzzle)this.getParent()).getEmptySpaceLocation()];

        if(t == 20) return 0;
        if(unitCost) return 1;
        return (t+2)/(t+1);
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

        if (((this.getEmptySpaceLocation() - 1) >= 0) && (this.getEmptySpaceLocation() % 4 != 0))
            successors.add(getSucc(-1));

        if (((this.getEmptySpaceLocation() + 1) < 16) && (this.getEmptySpaceLocation() % 4 != 3))
            successors.add(getSucc(1));

        if (((this.getEmptySpaceLocation() - 4) >= 0) && (this.getEmptySpaceLocation() / 4 != 0))
            successors.add(getSucc(-4));

        if (((this.getEmptySpaceLocation() + 4) < 16) && (this.getEmptySpaceLocation() / 4 != 3))
            successors.add(getSucc(4));

        return successors;
    }

    /**
     * Generates a child node by applying an action to the current node.
     *
     * @param operator  an integer representing the action to be applied
     * @return  the new child node
     */
    private AbstractSearchNode getSucc(int operator){
        byte[] succ = Arrays.copyOf(this.getState(), this.getState().length);
        succ[this.getEmptySpaceLocation()] = this.getState()[this.getEmptySpaceLocation() + operator];
        succ[this.getEmptySpaceLocation() + operator] = 0;

        return new SixteenPuzzle(succ, this);
    }

    /**
     * Reverses an action to generate a parent node from a child.
     *
     * @param operator  an integer representing the action to be reversed
     * @return  the new parent node
     */
    @Override
    public AbstractSearchNode reverseOperation(){
        if(this.getOperator() == Integer.MAX_VALUE) return null;

        byte[] parent = Arrays.copyOf(this.getState(), this.getState().length);
        parent[this.getEmptySpaceLocation()] = this.getState()[this.getEmptySpaceLocation() - this.getOperator()];
        parent[this.getEmptySpaceLocation() - this.getOperator()] = 0;

        return new SixteenPuzzle(parent);
    }

    /**
     * Static variable indicating whether we will be using unit costs or not.
     */
    static boolean unitCost = false;

    /**
     * Main entry point of the program.  All experiments are performed here.
     */

    //{0, 1, 1, 1, 
    // 2, 1, 1, 1, 
    // 2, 2, 3, 3, 
    // 3, 3, 3, 3}

    public static void main(String[] args) throws IOException
    {
        // new Thread(new SolverWorker(0, 9)).start();
        // Thread t1 = new Thread(new SolverWorker(87, 90));
        // Thread t2 = new Thread(new SolverWorker(65, 69));
        // Thread t3 = new Thread(new SolverWorker(48, 54));
        // Thread t4 = new Thread(new SolverWorker(55, 59));
        // Thread t5 = new Thread(new SolverWorker(79, 79));
        // Thread t6 = new Thread(new SolverWorker(16, 16));
        
        // t1.start();
        // t2.start();
        // t3.start();
        // t4.start();
        // t5.start();
        // t6.start();
    
        // int threadCount = 16;
        // for(int i = 0; i < threadCount; i ++)
        // {
        //     (new Thread(new SolverWorker((100 / threadCount) * i, (i == threadCount-1) ? (99) : ((100 / threadCount) * (i + 1) - 1)))).start();
        // }
        
        // Thread t1 = new Thread(new PatternWorker("0 1 2 3 20 20 6 7 20 20 20 20 20 20 20 20", "5-5-5|1"));
        //     t1.start();
        
        // System.out.println(Threads.nThreads);
        // RunnableTask task1 = new RunnableTask("First", 36, 59);
        // RunnableTask task2 = new RunnableTask("Second", 62, 69);
        // RunnableTask task3 = new RunnableTask("Third", 71, 89);
        // RunnableTask task4 = new RunnableTask("Fourth", 90, 99);
        

        // task1.run();task2.run();task3.run();task4.run();
        // writePattern(generatePattern("0 1 2 3 20 20 6 7 20 20 20 20 20 20 20 20"), "5-5-5|1");
        // writePattern(generatePattern("0 20 20 20 4 5 20 20 8 9 20 20 12 20 20 20"), "5-5-5|2");
        // writePattern(generatePattern("0 20 20 20 20 20 20 20 20 20 10 11 20 13 14 15"), "5-5-5|3");
            //Time: 225.015s elapsed | 233152903 expanded | 691145485 generated | solution length: 66.06262348762353
            double totalTime = 0, totalExpanded = 0, totalGenerated = 0;
            double j = 0;
            for(int i = 0; i < 100; i++)
            {
                if(!new File("./results/BTSVarExact/"+i+".txt").exists() ||new File("./results/BTSVarExact/"+i+".txt").length() == 0) continue;
                j++;
                try (BufferedReader br = new BufferedReader(new FileReader("./results/BTSVarExact/"+i+".txt"))) {
                    for(String line; (line = br.readLine()) != null; ) {
                        if(!line.contains("Time")) continue;

                        totalTime += Double.parseDouble(line.substring(line.indexOf("Time: ", 0) + 6, line.indexOf("s elapsed", 0)));
                        totalExpanded += Double.parseDouble(line.substring(line.indexOf("elapsed | ", 0) + 10, line.indexOf(" expanded", 0)));
                        totalGenerated += Double.parseDouble(line.substring(line.indexOf("expanded | ", 0) + 11, line.indexOf(" generated", 0)));
                    }
                }
            }

            System.out.println("Average time "+ (totalTime/j) + " | Average expanded: " + (totalExpanded/j) + " | Average generated: " + (totalGenerated/j));
        //     Input files

        // Path output = Paths.get("./results/BTSVarExact/concat.txt");

        // Charset charset = StandardCharsets.UTF_8;

        // for(int i = 0; i < 100; i++)
        // {
        //     if(!new File("./results/BTSVarExact/"+i+".txt").exists() ||new File("./results/BTSVarExact/"+i+".txt").length() == 0) continue;

        //     Path path = Paths.get("./results/BTSVarExact/"+i+".txt");
        //     List<String> lines = Files.readAllLines(path, charset);
        //     lines.add("\n");
        //     Files.write(output, lines, charset, StandardOpenOption.CREATE,
        //             StandardOpenOption.APPEND);
        // }
    }

    public static void SolveBoardRange(int from, int to) throws IOException
    {
        try (BufferedReader br = new BufferedReader(new FileReader("korf.txt"))) {
            int i = -1;
            for(String line; (line = br.readLine()) != null; ) {
                i++;
                if(i < from || i > to || (new File("./results/IDAStarVar/"+i+".txt")).exists()) continue;
                solveBoard(line);
                // return;
            }
        }
    }

    public static HashMap<Integer, Double> generatePattern(String last) throws IOException
    {
        System.out.println("Generating patterns for: {" + last + "}");
        AbstractSearchNode end = new SixteenPuzzle(stringToBoard(last));
        HashMap<Integer, Double> result = new HashMap<Integer, Double>();
        List<AbstractSearchNode> currentPatterns = new ArrayList<>();
        currentPatterns.add(end);

        int i = 0;
        while(currentPatterns.size() > 0){
            List<AbstractSearchNode> newPatterns = new ArrayList<>();
            for(AbstractSearchNode pattern : currentPatterns){
                double cost = solvePattern(pattern, end);
                result.put(pattern.hashCode(), cost);
                for(AbstractSearchNode child :(List<AbstractSearchNode>) pattern.getSuccessors()) if(!result.containsKey(child.hashCode())) newPatterns.add(child);
            }
            currentPatterns = newPatterns;
            System.out.println("Level " + i + " frontier "+ currentPatterns.size() + " collected " + result.size());
            i++;
        }

        return result;
    }

    private static String goal = "0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15";

    private static void solveBoard(String problem) throws IOException{
        String problemNumber = (Integer.parseInt(problem.substring(0,3).replace(" ", "")) - 1)+"";
        String problemBoard =  problem.substring(5);
        
        File fout = new File("./results/IDAStarVar/"+problemNumber+".txt");
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        String str = "IDA* on non-unit STP problem "+problemNumber;
        System.out.println(str);
        bw.write(str);
        bw.newLine();

        System.out.println("(4x4)"+ problemBoard);
        bw.write("(4x4)"+ problemBoard);
        bw.newLine();

        System.out.println("(4x4)"+goal);
        bw.write("(4x4)"+goal);
        bw.newLine();

        byte[] initStateArray = stringToBoard( problemBoard);//"11 1 7 4 10 13 3 8 9 14 0 15 6 5 2 12");//"14 13 15 7 11 12 9 5 6 0 2 1 4 8 10 3");//14 7 1 9 12 3 6 15 8 11 2 5 10 0 4 13
        byte[] goalStateArray = stringToBoard(goal);// {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};

        SixteenPuzzle initialState = new SixteenPuzzle(initStateArray);
        SixteenPuzzle goalState = new SixteenPuzzle(goalStateArray);

        NPuzzleHueristic heuristicFunction = new NPuzzleHueristic(goalState, unitCost);
        
        long startTime = System.currentTimeMillis();
        IDAStarSearch searcher = new IDAStarSearch(initialState, goalState, heuristicFunction);
        Object[] result = searcher.search(bw);
        if(result == null)return;
        // BudgetedTreeSearch searcher = new BudgetedTreeSearch(initialState, goalState, heuristicFunction);
        // Object[] result = searcher.search(2, 8, 2, true);
        AbstractSearchNode finalSearchNode = (AbstractSearchNode)result[0];
        long expanded = (long)result[1];
        long generated = (long)result[2];
        // System.out.println("Initial State");
        // System.out.println(initialState);

        // System.out.println("Final State");
        // System.out.println(finalSearchNode);


        List<AbstractSearchNode> path = AbstractSearchNode.getPath(finalSearchNode);
        double cost = 0;
        for(AbstractSearchNode c : path) cost+=c.distFromParent();

        str = "Time: " + ((System.currentTimeMillis() - startTime) / (1000.0)) + "s elapsed | " + expanded + " expanded | " + generated + " generated | " + "solution length: " + cost;
        System.out.println(str);
        bw.write(str);

        bw.close();

        // int step = 1;
        // for (AbstractSearchNode node : path) {
        //     System.out.println("Step " + step);
        //     System.out.println(node);
        //     step++;
        // }
    }

    // private static void solvePattern(String pattern, String patternGoal) throws IOException
    // {
    //     byte[] initStateArray = stringToBoard(pattern);//"11 1 7 4 10 13 3 8 9 14 0 15 6 5 2 12");//"14 13 15 7 11 12 9 5 6 0 2 1 4 8 10 3");//14 7 1 9 12 3 6 15 8 11 2 5 10 0 4 13
    //     byte[] goalStateArray = stringToBoard(patternGoal);// {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};

    //     SixteenPuzzle initialState = new SixteenPuzzle(initStateArray);
    //     SixteenPuzzle goalState = new SixteenPuzzle(goalStateArray);

    //     NPuzzleHueristic heuristicFunction = new NPuzzleHueristic(goalState, unitCost);
        
    //     long startTime = System.currentTimeMillis();
    //     // AStarSearch searcher = new AStarSearch(initialState, goalState, heuristicFunction);
    //     // AbstractSearchNode finalSearchNode = searcher.search();
    //     IDAStarSearch searcher = new IDAStarSearch(initialState, goalState, heuristicFunction);
    //     AbstractSearchNode finalSearchNode = (AbstractSearchNode)searcher.search(null)[0];

    //     List<AbstractSearchNode> path = AbstractSearchNode.getPath(finalSearchNode);
    //     double cost = 0;
    //     for(AbstractSearchNode c : path) cost+=((SixteenPuzzle)c).distFromParentPattern();

    //     System.out.println("solved in " + ((System.currentTimeMillis() - startTime) / 1000.0)  + "s solution length: " + cost + " moves");
    // }

    private static double solvePattern(AbstractSearchNode start, AbstractSearchNode end) throws IOException{
        NPuzzleHueristic heuristicFunction = new NPuzzleHueristic(end, unitCost);
        BudgetedTreeSearch searcher = new BudgetedTreeSearch(start, end, heuristicFunction);
        AbstractSearchNode finalSearchNode = (AbstractSearchNode)searcher.search(2, 8, 2, true)[0];
        double cost = 0;
        for(AbstractSearchNode c : AbstractSearchNode.getPath(finalSearchNode)) cost+=((SixteenPuzzle)c).distFromParentPattern();
        return cost;
    }

    public static void writePattern(HashMap<Integer, Double> map, String file) {
        try {
            File fileOne=new File(file);
            FileOutputStream fos=new FileOutputStream(fileOne);
            ObjectOutputStream oos=new ObjectOutputStream(fos);
    
            oos.writeObject(map);
            oos.flush();
            oos.close();
            fos.close();
        } catch(Exception e) {}
    
        //read from file 
    }

    private static HashMap<Integer, Double> readPattern(String file){
        try {
            File toRead=new File("fileone");
            FileInputStream fis=new FileInputStream(toRead);
            ObjectInputStream ois=new ObjectInputStream(fis);
    
            HashMap<Integer, Double> mapInFile=(HashMap<Integer, Double>)ois.readObject();
    
            ois.close();
            fis.close();

            return mapInFile;
        } catch(Exception e) { return null;}
    }

    
    /**
     * Coverts a string representation of a puzzle board to an integer array.
     *
     * @param str   space-separated string representation of the board
     * @return  integer array representation of the board
     */
    private static byte[] stringToBoard(String str){
        byte[] result = new byte[16];
        int i =0;
        for (String num : str.split("\\s+"))
        {
            result[i] = Byte.parseByte(num);
            i++;
        }

        return result;
    }

    
}

class SolverWorker implements Runnable{

    private int from, to;

    SolverWorker(int from, int to){
        this.from = from;
        this.to = to;
    }

	@Override
	public void run() {
        try {
                System.out.println("Running " +  Thread.currentThread().getName() );
                try {
                    SixteenPuzzle.SolveBoardRange(from, to);
                } catch (Exception e) {
                    System.out.println("Thread " +  Thread.currentThread().getName() + " interrupted.");
                }
                System.out.println("Thread " +  Thread.currentThread().getName() + " exiting.");
        } catch (Exception e) {
            System.out.println("Thread " +  Thread.currentThread().getName() + " interrupted.");
        }
	}
}

class PatternWorker implements Runnable{

    private String pattern, file;

    PatternWorker(String pattern, String file){
        this.pattern = pattern;
        this.file = file;
    }

	@Override
	public void run() {
        try {
                System.out.println("Running " +  Thread.currentThread().getName() );
                try {
                    SixteenPuzzle.writePattern(SixteenPuzzle.generatePattern(pattern), file);
                } catch (Exception e) {
                    System.out.println("Thread " +  Thread.currentThread().getName() + " interrupted.");
                }
                System.out.println("Thread " +  Thread.currentThread().getName() + " exiting.");
        } catch (Exception e) {
            System.out.println("Thread " +  Thread.currentThread().getName() + " interrupted.");
        }
	}
}
// HARD
// conf x 1: 230 (optimal) -> 220
// conf x 2: 96 (optimal) -> 94
// conf x 3: 120 (sub optimal)
// conf x 4: 84 (sub optimal)

// BTS UNIT COST
// path: 
// parent: 1 min 33 secs
// operators: 2 mins

// BTS VAR COST
// path: 
// parent: 
// operators: 


// IDA* all unit cost: 100
// IDA* one with var cost: 1 (10-30 mins)
// BTS all unit: 100
// BTS all var c1!=c2: 100
// BTS all var c1==c2: 100