/**
 * Name: Dylan Chang & Brandon Kong
 * Date: 01/13/2020
 * File Name: Soduko
 * Description: Contains all of the key methods required to create a fully solved 
 * sudoku board(answer board), create a solvable sudoku board (with missing numbers
 * in certain areas) based off the fully solved sudoku board, and to check if 
 * a board is correctly filled in.
 */
package SudokuGame;

import java.util.ArrayList;
import java.util.Random;
import javax.swing.JTable;


public class Soduko {

    public static ArrayList<Object> available = new ArrayList<Object>() {
        //holds numbers 1 through 9 and is used in seeing which numbers are available to place in board
        {
            for (int i = 1; i < 10; i++) {
                add(i);
            }
        }
    };
    public static ArrayList<Object> temporary = new ArrayList<>();
    /*
    temporary holds numbers in the current row, column, and quadrant 
    with respect to current box currently filling a number with
    */
    public static Integer[][] boardKey = new Integer[9][9];//holds a fully solved sudoku board
    public static Integer[][] visibleBoard = new Integer[9][9];
    /* visibleBoard holds a solvable sudoku board with some empty values (meant for user to
    enter numbers into these empty values to solve the visibleBoard)
    */
    public static int count = 0;
    //total number of numbers added into boardKey
    private static boolean correct=true;
    //variable is if user correctly filled in sudoku board (false if wrong and true if right)
    private static int difficulty=0;
    //this int is set to different int values according to the difficulty chosen

    public static void main(String[] args) {
    }

    /****
* This method will use a valid sudoku board/2D array and it will transfer a 
* select number of values into the display array that the user will see
* Pre: requires a valid and complete 2D array (the generated board)
* Post: the 2D array that the user sees (visibleBoard) will be filled with 
* numbers and 0’s to be displayed to the jFrame
*/
    public static void setInitialBoard() {
        int numOfInitialValues = 0, count = 18, theQuad;
        int[] quadrantCount = {2, 2, 2, 2, 2, 2, 2, 2, 2};
        boolean technicality = true;//just a bypass to a problem in the code

        switch (difficulty) {
            case 1: //easy
                numOfInitialValues = 60;// the number of given values in the sudoku board "visibleBoard"
               break;
            case 2: //medium
                numOfInitialValues = 42;
                break;
            case 3: //hard
                numOfInitialValues = 35;
                break;
        }
        for (int i = 1; i < 10; i++) { //initially place 2 values in each quadrant
            placeInQuadrant(i);
            placeInQuadrant(i);
        }
        while (count != numOfInitialValues) {
            //this will run until the number of values on the visible board equals numOfInitalValues
            technicality = true;
            do {
                theQuad = randomNumber(0, 8);//random quadrant index
                if (quadrantCount[theQuad] != 7) {
                   //this limits the number of values in each quadrant to 7
                    quadrantCount[theQuad]++;
                    placeInQuadrant(theQuad + 1);
                    technicality = false;
                }
            } while (technicality);//break out of dowhile once a quadrant is entered
            count++; // another value was added to visibleBoard
        }
    }

      /****
      * This method will set a value from the boardKey to the visibleBoard 
      * in a random empty box in a specified quadrant
      * @param quadrant the specified quadrant
      * Pre: requires the quadrant to be from 1-9, and the visibleBoard must have vacant boxes
      * Post: will have transferred a value from boardKey to visibleBoard
      */
    public static void placeInQuadrant(int quadrant) {
        int x = 0, y = 0;
        while (true) {
            switch (quadrant) {
            // contains the range of coordinates for each quadrant    
                case 1:
                    x = randomNumber(0, 2);
                    y = randomNumber(0, 2);
                    break;
                case 2:
                    x = randomNumber(3, 5);
                    y = randomNumber(0, 2);
                    break;
                case 3:
                    x = randomNumber(6, 8);
                    y = randomNumber(0, 2);
                    break;
                case 4:
                    x = randomNumber(0, 2);
                    y = randomNumber(3, 5);
                    break;
                case 5:
                    x = randomNumber(3, 5);
                    y = randomNumber(3, 5);
                    break;
                case 6:
                    x = randomNumber(6, 8);
                    y = randomNumber(3, 5);
                    break;
                case 7:
                    x = randomNumber(0, 2);
                    y = randomNumber(6, 8);
                    break;
                case 8:
                    x = randomNumber(3, 5);
                    y = randomNumber(6, 8);
                    break;
                case 9:
                    x = randomNumber(6, 8);
                    y = randomNumber(6, 8);
                    break;
            }
            if (getVisibleBoard()[x][y].equals(0)) {
                // if that spot is empty, fill it and break out of while loop, else generate another
                getVisibleBoard()[x][y] = getBoardKey()[x][y];
                break;
            }
        }
    }

    /****
* This method will generate a 9x9 array of single integer values that
* follow the rules and restrictions of the traditional game sudoku. It 
* will traverse each cell and will place a calculated valid value until
* there are no valid values for a cell or the array is filled out. If it runs
* out of valid values, it will reset all the variables and will run itself 
* again until it completes the board. (RECURSIVE)
* Pre: BoardKey must be a 9x9 integer array
* Post: the BoardKey will be filled out with a valid and complete 
* array of values that follow the rules and restrictions of sudoku
*/
    public static void generateBoard() {
        int x = 0, y = 0;
        Integer value = 0;
        outer:
        for (int i = 1; i < 82; i++) {
            if (i == 28 || i == 55) {
                //at the end of each rightmost quadrant on the right side of the board
                x = x - 9;
                y++;
                revertValues(true);//reset available values
            } else if (i % 9 == 1 && i != 1) {
                //at the end of each quadrant
                y = y - 2;
                revertValues(true);
            } else if (i % 3 == 1 && i != 1) {
                 //end of each row of one quadrant
                x = x - 3;
                y++;
            }
            removeValues(x, y); //remove invalid values from available
            if (available.isEmpty()) {
                //if there are no more possible values for a cell, recurse
                revertValues(true);
                for (int j = 0; j < 9; j++) {
                    for (int k = 0; k < 9; k++) {
                        getBoardKey()[j][k] = 0;
                    }
                }
                generateBoard();
                count++;
                break outer;//exit out
            }
            do {
                value = randomNumber(1, 9);
            } while (!contains(value));
            getBoardKey()[x][y] = value;
            for (int j = 0; j < available.size(); j++) { //find this value and remove it
                if (available.get(j).equals(value)) {
                    available.remove(j);
                    j--;
                }
            }
            revertValues(false);//put temporary back into available
            x++;//go to the next square
        }
    } //generateBoard

    /****
* This method will check if the available arraylist contains
* the value val
* @param val the value being checked
* return will return true if available contains val
* Pre: will run everytime generateBoard generates a 
* a random number from (1-9)
* Post: will determine whether to generate another 
* number or use the one generated (val)
*/
    public static boolean contains(int val) {
        for (int i = 0; i < available.size(); i++) {
            if (available.get(i) == (Object)val) {
                return true;
            }
        }
        return false;
    }

    /****
* This method will remove invalid values from the available arraylist
* based on a cell’s row and column. It will first remove all the values 
* that are in its row and then it will do the same with the column. When
* it removes a value from the available, it will temporary store that value
* in the temporary arraylist.
* @param x the x-coordinate index of the cell
* @param y the y-coordinate index of the cell
* Pre: x and y must be the coordinate of an empty cell and they must be
* within the range of 0 - 8
*/
    public static void removeValues(int x, int y) {
        Integer theNum;
        if (x != 0) { //this will not check the leftmost cell of the row
            for (int i = x; i > -1; i--) {// traverse the all values left of cell
                theNum = getBoardKey()[i][y];
                for (int j = available.size(); j > 0; j--) {
                    if (available.get(j - 1).equals(theNum)) {//remove from the available arraylist 
                        temporary.add(theNum);
                        available.remove(j - 1);
                        j--;
                    }
                }
            }
        }
        if (y != 0) {//same but with column as above
            for (int i = y; i > -1; i--) {
                if (available.contains(getBoardKey()[x][i])) {
                    theNum = getBoardKey()[x][i];
                    for (int j = available.size(); j > 0; j--) {
                        if (available.get(j - 1).equals(theNum)) {
                            temporary.add(theNum);
                            available.remove(j - 1);
                            j--;
                        }
                    }
                }
            }
        }
    }//remove values

    /****
* This method will transfer values around between the available
* and temporary arraylists. If total is false, all the stored values 
* in temporary array will be transferred back to the available 
* array. If total is true, both arrays will be cleared and the 
* available array will be reset with all values 1 - 9
* @param total indicator to full reset or reset only temporary 
* Pre: occurs in a sequence in generateBoard
* Post: available and/or temporary arraylist will have their values 
* reset
*/
    public static void revertValues(boolean total) {

        if (!total) { // if false
            for (Integer i = 0; i < temporary.size(); i++) {
                available.add(temporary.get(i));//puts all of temporary back
            }
            temporary.clear();//clear temporary arraylist
        } else { //if true
            available.clear(); //clear both arraylists 
            temporary.clear();
            for (Integer i = 1; i < 10; i++) {
                available.add(i); //add the range of numbers from 1-9
            }
        }
    }

    /****
* random number generator
*/
    public static int randomNumber(int min, int max) {
        return (int) (Math.random() * ((max - min) + 1)) + min;
    }
    
    /**
     * nullToZero method changes any null values found
     * when traversing the 2d array visibleBoard, to zero values
     * Pre: none
     * Post: all values in visibleBoard 2d array are set to equal 0
     */
    public static void nullToZero()
    {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                getVisibleBoard()[i][j]=0;
            }
        }
    }
    
    /**
     * checker method checks if the JTable t is correctly filled in, where each 
     * column, row, and quadrant contains the numbers one through nine with no repeats. 
     * If it is incorrectly filled, a public boolean correct is set to false, but if 
     * correctly filled the boolean will be set to true.
     * @param t the jTable being used to display the unsolved Sudoku board and ask 
     * for users input to solve board
     * Pre: t has at least nine rows and columns, and all methods called up exist. 
     * Post: sets public boolean "correct" to false if table filled incorrectly, set
     * to true if table filled correctly.
     */
    public static void checker(JTable t)
    {    
        int x = 0, y = 0;
        Object cellData=0;//holds the current cell's (one box of the table) data
        setCorrect(true);
        revertValues(true);//fills the available arraylist with all numbers 1 to 9
        
        /*
        below for loop starts from uppermost left corner box of table, then after 
        done with that box moves diagonally down and right one box.
        this allows loop to check the 9 boxes going diagonally down and right and 
        ends up at bottom most right corner box,so it checks all columns and rows in table.
        */
        inner:
        for (int i = 0; i < 9; i++) {
            revertValues(true);
            
//checks if the current row of box being handled has all numbers one through nine, with no repeats
            for (int j = 0; j < 9; j++) {
                cellData=t.getModel().getValueAt(y,j);
                //gets value from current box in table, and sets cellData to this value
                checkComparing(cellData);
                if (isCorrect()==false)//if they incorrectly filled in sudoku board
                    break inner;
                /*
                exit loop since we already know user's board is wrong, 
                no need to further check if they're correct
                */
            }
            checkIfCorrect();
            
            revertValues(true);
               
  //checks if the current column of box being handled has all numbers one through nine, with no repeats
            for (int j = 0; j < 9; j++) {
                cellData=t.getModel().getValueAt(j,x);
                checkComparing(cellData);
                if (isCorrect()==false)
                    break inner;
            }
            checkIfCorrect();
            x++;
            y++;
   //adding x and y coordinates by one means goes diagonally down and right one box in table
        }

        revertValues(true);
        x = 0;
        y = 0;

        /*
        below for loop checks if all 3 by 3 quadrants correctly contain all 
        numbers one through nine with no repeats
        starts with top left quadrant, then moves right until reaches last quadrant in first row. 
        then moves down a row of quadrants
        and starts at left quadrant again; repeats until all quadrants are checked.
        */
        if (isCorrect() == true) {
            inner:
            for (int i = 1; i < 82; i++) {//checks 81 boxes
                if (i == 28 || i == 55) {
                    x = x - 9;
                    y++;
                    revertValues(true);
                } else if (i % 9 == 1 && i != 1) {
                    y = y - 2;
                    checkIfCorrect();
                    if (isCorrect()==false)//if they incorrectly filled in sudoku board
                        break inner;//stop checking since they incorrectly filled board
                    revertValues(true);
                } else if (i % 3 == 1 && i != 1) {
                    x = x - 3;
                    y++;
                }

                cellData=t.getModel().getValueAt(y,x);
                    
                checkComparing(cellData);
                x++;
            }
        }
    }
    
    /**
     * checkComparing is a method that checks if an arraylist "available" already 
     * contains cellData, and attempts to remove cellData from the arraylist.
     * The array also checks if cellData contains a value that user inputted into a 
     * JTable by checking if cellData can be converted into a string.
     * @param cellData value from a single box in a JTable
     * Pre: arrayList "available" must be a declared public variable
     * Post: cellData is removed from the 2d arraylist "available"
     */
    public static void checkComparing (Object cellData)
    {
        String userInput="";
//variable for holding user's string input for a number
        boolean isUserInput=false;
//true if is user inputted the number in box, false if box has number generated from code
        String number="";
//a string used to temporaily hold strings of numbers "1" through "9" for comparison purposes
        try {
                    userInput=(String)cellData;
                    if (!"".equals(userInput))
                        /*
                       if cellData was succesfully saved as a string into userInput, userInput 
                       will no longer hold "" so this boolean will be true
                        */
                    {
                        isUserInput=true;//box currently handling has user inputted data
                    }
                }    
                 catch (Exception e) {
                }
        
                if (available.contains(cellData))
                //if available arraylist contains the number from box
                {
                    available.remove(cellData);
                } 
                else if (isUserInput==true)
              //means the number it's handling in the box was user inputted, and is a string
                        {
                            for (Integer i = 1; i < 10; i++) {
                                number=Integer.toString(i);
                            //number is a string, set to hold values "1" through "9"
                                
                                if (number.equals(cellData))
                            //if number is equal to user input in box
                                    available.remove((Object)i);
                            //removes that user inputted number from available array
                            }
                        }
                else {//board must be filled incorrectly
                    setCorrect(false);
                }
                
    }

    /**
     * checkIfCorrect method checks if the row, column, 
     * quadrant just checked is correctly filled in.
     * If it was correctly filled in (with no repeating numbers from 1 to 9), 
     * the available arraylist should be empty.
     * If it is not empty, then there are repeating numbers in the row, column, 
     * or quadrant, and therefore user incorrectly filled out sudoku board,
     * thus the boolean for if user is correct is set as false.
     * Pre: "available" arraylist is a public variable and is declared
     * Post: none
     */
    public static void checkIfCorrect ()
    {
        if (available.isEmpty()==false)
                setCorrect(false);
    }

    public static Integer[][] getBoardKey() {
        return boardKey;
    }

    public static void setBoardKey(Integer[][] aBoardKey) {
        boardKey = aBoardKey;
    }

    public static Integer[][] getVisibleBoard() {
        return visibleBoard;
    }

    public static void setVisibleBoard(Integer[][] aVisibleBoard) {
        visibleBoard = aVisibleBoard;
    }

    public static boolean isCorrect() {
        return correct;
    }

    public static void setCorrect(boolean aCorrect) {
        correct = aCorrect;
    }

    public static int getDifficulty() {
        return difficulty;
    }

    public static void setDifficulty(int aDifficulty) {
        difficulty = aDifficulty;
    }
}
