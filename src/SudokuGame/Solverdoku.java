/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SudokuGame;

/**
 * @author Dylan Chang and Brandon Kong
 * Date Finished: January 12, 2020
 * Solverdoku
 * Solverdoku solves the generated Sudoku board and it counts the number 
 * of possible solutions to the board generated.
 */
public class Solverdoku {

    public static int getNumOfSol() {
        return numOfSol;
    }

    public static void setNumOfSol(int aNumOfSol) {
        numOfSol = aNumOfSol;
    }
    private static final Integer EMPTY_CELL = 0;//the value for an empty space in the board
    private static int numOfSol = 0;//number of solutions the current board has
    static Integer [][] mirrorBoard = new Integer [9][9];//copy of board trying to solve
    /****
     * This method will solve a 2D array according to the guidelines of sudoku.
     * @param laBoard the 2D array containing the values
     * Pre: this method requires a 9x9 2D integer array that contains values that
     * follow the guidelines of sudoku and make the board solvable
     * Post: will print out a statement saying that the board is unsolvable or 
     * it will print out the solved 2D array
     */
    public static void solveBoard(Integer[][]laBoard){
        if(Solverdoku.solutionsCounter(laBoard)!= 0){
            System.out.println("Solvable");
            validBoardFromCell(laBoard);
            System.out.println("Number of Solutions: " + getNumOfSol());
            for (int i = 0; i < 9; i++) {
                System.out.println("");
                for (int j = 0; j < 9; j++) {
                    System.out.print(laBoard[j][i] + " ");  
                }
            }
        }else{
            System.out.println("This board is unsolvable!");
        }
    }
    /****
     * This method will return true or false when a potential board is entered
     * @param laBoard the 2D array containing the potential board being tested
     * @return will return true if the potential board follows the guidelines of
     * sudoku, else will return false
     * Pre: requires a 2D integer array with values
     * Post: will return a true or false statement back to the solveBoard method
     * determining whether a board is valid or not
     */
    public static boolean validBoardFromCell(Integer[][]laBoard){
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (laBoard[row][col] == EMPTY_CELL) {//if that space is empty
                    for (int placement = 1; placement < 10; placement++) {//tries all values from 1 to 9
                        if(validPlacement(row,col,placement,laBoard)){//if this placement is valid, enter it
                            laBoard[row][col] = placement;           
                            if(validBoardFromCell(laBoard)){//continue on recursing
                                return true;
                            }else{ //if the board doesnt work down the road, backtrack and try another value
                                laBoard[row][col] = EMPTY_CELL; 
                            }
                        }
                    }
                    return false;//false if all placements of 1 - 9 do not work
                }
            }
        }
        return true;//if you reach the end of the board without errors
    }
    /****
     * This method is used to count the number of solutions a potential board has,
     * similar to the bubble sort method
     * @param laBoard the 2D array being tested
     * @return will return the number of solutions laBoard has
     * Pre: must receive a 2D integer array
     * Post: will output the number of solutions to the main method
     */
    public static int solutionsCounter(Integer [][] laBoard){
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                mirrorBoard[j][i] = laBoard[j][i];
                //makes a mirror board so not as to destroy the original board
            }
        }
        if(solutionsCounter(mirrorBoard,1)){
            return getNumOfSol();
        }else{
            return 0;
        }
    }
    /****
     * This method is an overloaded method that will record the number of solutions
     * in a board without changing the solved board's contents
     * @param laBoard the 2D array being tested
     * @param place the possible values left off at
     * @return true if solutions > 0, else false
     * Pre: requires the main solutionsCounter to be called
     * Post: will be recursed a multitude of times until returning to the main method
     */
    public static boolean solutionsCounter(Integer[][]laBoard, int place){
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (laBoard[row][col] == EMPTY_CELL) {
                    for (int placement = place; placement < 10; placement++) {
                        if(validPlacement(row,col,placement,laBoard)){
                            solutionsCounter(laBoard,placement+1);
                            laBoard[row][col] = placement;           
                            if(solutionsCounter(laBoard,1)){ //this is the change
                                return true;
                            }else{ //if the board doesnt work down the road, backtrack
                                laBoard[row][col] = EMPTY_CELL; 
                            }
                        }
                    }
                    return false;
                }
            }
        }
        setNumOfSol(getNumOfSol() + 1);
        return true;
    }
    /****
     * This method will return true or false to whether a value follows the
     * guidelines to sudoku: in its row, its column, its quadrant
     * @param row the row number of the tested value in terms of its index (first row = 0)
     * @param col the column number of the tested value in terms of its index
     * @param placement the value being tested
     * @param laBoard the 2D array being tested
     * @return will return true or false whether the value being placed is valid
     * Pre: requires the value being placed, its index co-ordinates, and the 2D array
     * Post: will return a statement to the validBoardFromCell method determining
     * whether to continue placing or to remove this value
     */
    public static boolean validPlacement(int row, int col, int placement, Integer[][]laBoard){
        //check row & column
        for (int i = 0; i < 9; i++) {
            if(placement == laBoard[row][i]){
                return false;
            }
            if(placement == laBoard[i][col]){
                return false;
            }
        }
        //check quadrant
        int upperLeftX = (col < 3) ? 0 : (col < 6) ? 3 : 6;//finds upper leftmost box of quadrant
        int upperLeftY = (row < 3) ? 0 : (row < 6) ? 3 : 6;
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                if(placement == laBoard[upperLeftY + y][upperLeftX + x]){
                    return false;//checks every box in quadrant
                }
            }
        }
        return true;
    }
}

