/**
 * Name: Dylan Chang & Brandon Kong
 * Date: 01/12/2020
 * File Name: highScores
 * Description: This class will solely be used for the updateHighScores() method.
 * Basically, this class is used to take in a time and a name, decide whether it
 * is worthy of being on the leader board (based on time) and will sort it and enter
 * it into a specific text file based on the difficulty completed.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SudokuGame;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author dylan
 */
public class highScores {

    static ArrayList<Long> highTimes = new ArrayList<>();
    static ArrayList<String> highNames = new ArrayList<>();

    /**
     * **
     * This method will receive a player's name and completion time once they
     * complete the game. Then, it will enter said player's score into an array
     * list and sort the array. It will then upload the sorted times into a
     * file.
     *
     * @param theTime player's time
     * @param theName player's username
     * @param textFileName the difficulty completed (ex: highScores_EASY.txt)
     * @throws IOException Pre: The user must correctly complete the sudoku
     * board. Post: The user's score will be entered into an array of high
     * scores and will be sorted from shortest to highest completion time. If
     * their time made the cut, it will be uploaded into the respective
     * highScore text file.
     */
    public static void updateHighScores(long theTime, String theName, String textFileName) throws IOException {
        fileToArray(textFileName);
        alreadyPlayed(theName, theTime);
        insertSort();
        arrayToFile(textFileName);
    }

    /**
     * **
     * This will sort the highTimes arraylist from lowest to highest completion
     * time by insertion sort method. The highNames arraylist will move
     * accordingly. 
     * Pre: The values in the highTimes list must be integers.
     * Post: The highTimes arraylist will be sorted from lowest to highest time.
     * The highNames arraylist will move whenever the highTimes moves.
     */
    public static void insertSort() {
        for (int top = 1; top < highTimes.size(); top++) {
            long item = highTimes.get(top); // temporary storage of item
            String itemName = highNames.get(top);
            int i = top;
            while (i > 0 && item < highTimes.get(i - 1)) {
                highTimes.set(i, highTimes.get(i - 1)); // shift larger items to the right by one
                highNames.set(i, highNames.get(i - 1));
                i--;
            } // prepare to check the next item to the left	
            highTimes.set(i, item); // put sorted item in open location 
            highNames.set(i, itemName);
        }
    }

    /**
     * **
     * This method will transfer the values in highNames and highTimes into a
     * text file in a format, allowing it to be easily read from later.
     *
     * @param textFileName the difficulty (easy,medium,hard)
     * @throws FileNotFoundException Pre: the array must contain valid values
     * Post: the array values will be uploaded to the text file in a format
     */
    public static void arrayToFile(String textFileName) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(textFileName);
        int size = (highNames.size() > 10) ? 10 : highNames.size();
        for (int i = 0; i < size; i++) {
            writer.print(highNames.get(i).toUpperCase() + " ");
            writer.print(highTimes.get(i));
            if(i+1 != size){
                writer.println("");
            }
        }
        writer.close();
    }

    /**
     * **
     * This method will read the values in a specified text file and will copy
     * it into two arraylists.
     *
     * @param textFileName the difficulty (easy,medium,hard)
     * @throws FileNotFoundException
     * @throws IOException Pre: the file must be correctly formatted Post: the
     * file values will be read into respective arrays with specific data types
     */
    public static void fileToArray(String textFileName) throws FileNotFoundException, IOException {
        LineNumberReader reader = null;
        reader = new LineNumberReader(new FileReader(textFileName));
        while (reader.readLine() != null); //goes to end of file
        int arrNum = reader.getLineNumber();
        FileReader f = new FileReader(textFileName);
        Scanner s = new Scanner(f);
        for (int i = 0; i < arrNum; i++) {
            highNames.add(s.next());
            System.out.print("Name: " + highNames.get(i));
            highTimes.add(s.nextLong());
            System.out.println("Time: " + highNames.get(i));
        }
    }
    /****
     * This method will determine whether the player has already played(based off
     * username) and will only update time if their new time is better, otherwise
     * it will add the new name and new time.  If they've already played and their
     * time is worse, nothing will be updated.
     * @param theName the username
     * @param theTime the new time
     * Pre: all arraylists must contain valid values, theName and theTime must
     * not be empty
     * Post: the highTimes and highNames arraylists will be updated according to
     * the new time and username inputted
     */
    public static void alreadyPlayed(String theName, long theTime) {
        boolean alreadyPlayd = false;
        int nameIndex = 0;
        for (int i = 0; i < highNames.size(); i++) {
            if (highNames.get(i).equalsIgnoreCase(theName)) { //if user played already
                alreadyPlayd = true;
                nameIndex = i; //saves index of name
            }
        }
        if (alreadyPlayd) { //if user played already
            if (theTime < highTimes.get(nameIndex)) { //if time is better than previous
                highTimes.set(nameIndex, theTime);
            }
        } else { //else new username
            highNames.add(theName);
            highTimes.add(theTime);
        }
    }
    public static void main(String[] args) throws IOException {
        updateHighScores(900, "obama", "highScores_HARD.txt");
    }

}
