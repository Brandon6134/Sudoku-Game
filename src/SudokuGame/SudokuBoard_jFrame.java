/**
 * @author Brandon Kong and Dylan Chang
 * Date: 01/13/2020
 * File Name: SudokuBoard_jFrame
 * Description: This class acts as the JFrame where the solvable sudoku board, 
 * timer, check and clear buttons, and many more
 * components of the game are displayed to user that allows them to play a 
 * sudoku board game. If user successfully wins the game, 
 * they are asked to enter their name to be saved along with their completion 
 * time score.
 */

package SudokuGame;

import SudokuGame.MainMenu_jFrame;
import SudokuGame.MainMenu_jFrame;
import SudokuGame.Rules_jFrame;
import static SudokuGame.Soduko.checker;
import static SudokuGame.Soduko.generateBoard;
import static SudokuGame.Soduko.getBoardKey;
import static SudokuGame.Soduko.getVisibleBoard;
import static SudokuGame.Soduko.nullToZero;
import static SudokuGame.Soduko.setInitialBoard;
import static SudokuGame.Soduko.getDifficulty;
import static SudokuGame.Soduko.isCorrect;
import static SudokuGame.Soduko.setVisibleBoard;
import static SudokuGame.Solverdoku.getNumOfSol;
import static SudokuGame.Solverdoku.setNumOfSol;
import static SudokuGame.Solverdoku.solutionsCounter;
import static SudokuGame.Solverdoku.solveBoard;
import static SudokuGame.highScores.updateHighScores;
import com.sun.xml.internal.ws.util.StringUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Duration;
import java.util.ArrayList;
import java.util.TimerTask;
import javax.swing.DefaultCellEditor;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

public class SudokuBoard_jFrame extends javax.swing.JFrame {
    
    public static JTable t;//the table being displayed on the JFrame
    public static ArrayList<Integer> locationI=new ArrayList<>();
    //x-coordinates of program generated numbers in board
    public static ArrayList<Integer> locationJ=new ArrayList<>();
    //y-coordinates of program generated numbers in board
    public long gameStartTime;
/*
    the time in milliseconds that the user started their
    game of sudoku according to system
    */
    public Timer timer;
/*timer used to display in game timer of how long user 
    takes to complete a game of sudoku
    
    */
    public boolean editTable;
/*if false, entire table is set to not edtiable. if true, 
    other methods determine which cells of table are editable
    */
    private String name;//user's name saved to be displayed in highscores jframe
    private long milliseconds;//time user took to solve correctly solve a sudoku board
    public Duration dur;
/*
    equal to public long runTime, calculates time passed from 
    current time to when game of sudoku was started
    */
    public boolean incorrectDisappear=false;
//boolean determining if the incorrect message is being displayed or not (true if displayed)
    public long runTime;//time passed from current time to when game of sudoku was started

    /**
     * Creates new form createAndDisplayBoard
     */
    public SudokuBoard_jFrame() {
        initComponents();
        showAndHideComponentsPreWin();
        displayTimer();
        setTextDifficulty();
        generateBoard();
        nullToZero();
        setInitialBoard();
        createAndDisplayBoard();
        displayNumOfSol();
    }
    
    /**
     * createAndDisplayBoard is a method that creates a table that holds 
     * the values from a solvable sudoku board and sets specific attributes
     * about the table, e.g. size, which cell is editable or uneditable, 
     * the colour of the font in each cell, etc.
     * Pre: all public variables are declared and methods called up exist
     * Post: a table is created and displayed, containing the values 
     * of a solvable sudoku board
     */
    public void createAndDisplayBoard() {
        String[] invisHeaders={"","","","","","","","",""};
        //headers for the columns of the jtable
        String[][] data = new String[9][9];//holds values for a solvable sudoku board
        
        locationI.clear();
        locationJ.clear();
        
        for (int i = 0; i < getVisibleBoard().length; i++) {
            String[] row= new String[getVisibleBoard()[i].length];
            
            for (int j = 0; j <getVisibleBoard()[i].length ; j++) {
                if (getVisibleBoard()[i][j]!=0)
                {
                    row[j]=String.valueOf(getVisibleBoard()[i][j]);
                    locationI.add(i);//adds x coords
                    locationJ.add(j);//adds y coords
                }
            }
            data[i]=row;
        }
        
        DefaultTableModel model=new DefaultTableModel(data,invisHeaders){
        //makes a model from data and invisHeaders
         @Override
         public Class<?> getColumnClass(int columnIndex) {
    //this built-in method is neccesary for colouring text, the tableFontChanger method
            return String.class;
         }
        };
        
        JTable table = new JTable (model) {
        //makes a table with the values from model
            public boolean isCellEditable(int row, int column){//this is a built in method
                
                for (int i = 0; i < locationI.size(); i++) {
                       
                    /*
                    all of the coordinates (row and column) of each number 
                    that are already generated into sudoku board are saved into locationI 
                    and locationJ arrays; if the coordinates of the box currently 
                    handling match row and column(coordinates of cell in table), 
                    don't allow user to edit that box so returns false, meaning 
                    cell in table is not editable.
                    */
                    if (row==locationI.get(i) && column==locationJ.get(i))
                        return false;
                    else if (editTable==false)
                    /*
                        editTable is set to false only if user wins game, 
                        so if they won game prevents all boxes from being edited
                        */
                        return false;
                    
                }
                return true;
                /*returns true if box currently is handling is empty, 
                so cell in table is editable for user to enter numbers.
                
                */
                    }
        };
        
        table.setGridColor(Color.BLACK);
        table.setShowGrid(true);//table displays a grid
        Font font = new Font("Verdana", Font.PLAIN, 30);
        table.setFont(font);
        table.setRowHeight(40);
        table.setVisible(true);
        table.setTableHeader(null);//header is invisible
        table.setRowSelectionAllowed(false);
        //makes user select the table by individual box, not row
        
        table.setDefaultRenderer(String.class, new tableFontChanger());
        //font colour changing method for table
        
        for (int i = 0; i < 9; i++)//does for all nine all columns 
        {
        JTextField text = new JTextField();
        text.setDocument(new JTextFieldCharacterLimiter(1));
        //sets JTextField "text" to limit of one character allowed to be entered
        table.getColumnModel().getColumn(i).setCellEditor(new DefaultCellEditor(text));
        /*
        above line gets JTable "table" and gets it's column number "i" 
        and puts same character restrictions 
        of JTextField "text" on table column; therefore in table you can
        only enter one character per cell
        */
        }
        
        sudokuBoard.setLayout(new BorderLayout());
        //makes anything added to panel, actually visible
        sudokuBoard.add(new JScrollPane(table));
        t=table;
        /*makes JTable "t" equal to JTable "table" produced, so "table"'s 
        content can be used as the parameter for checker method
        */
        for (int i = 0; i < 9; i++) {
            TableColumn column = t.getColumnModel().getColumn(i);
            column.setPreferredWidth(70);
        }
        sudokuBoard.setPreferredSize(new Dimension(366,366));//sets board size
    }
    
    /**
     * showAndHideComponentsPreWin is a method that sets certain 
     * components in the jFrame to not visible and visible
     * before the user has won the game.
     * Pre: jFrame components exist and public variables are declared
     * Post: certain components in jFrame are set to visible and not visible
     */
    public void showAndHideComponentsPreWin()
    {
        editTable=true;
        checkButton.setVisible(true);
        clearButton.setVisible(true);
        quitButton.setVisible(true);
        ruleButton.setVisible(true);
        newGameButton.setVisible(true);
        giveUpButton.setVisible(true);
        
        youWon.setVisible(false);
        textSavedToScores.setVisible(false);
        enterName.setVisible(false);
        spaceForName.setVisible(false);
        submitName.setVisible(false);
        tenCharLimit.setVisible(false);
        boardIncorrectFill.setVisible(false);
        unsolvableBoard.setVisible(false);
        scoreNotSaved.setVisible(false);
        justSolveButton.setVisible(false);
        solveHereButton.setVisible(false);
    }
    
    /**
     * showAndHideComponents is a method that sets certain 
     * components in the jFrame to not visible and visible
     * after the user has won the game.
     * Pre: jFrame components exist and public variables are declared
     * Post: certain components in jFrame are set to visible and not visible
     */
    public void showAndHideComponentsPostWin()
    {
        editTable=false;
        checkButton.setVisible(false);
        clearButton.setVisible(false);
        quitButton.setVisible(false);
        ruleButton.setVisible(false);
        newGameButton.setVisible(false);
        
        youWon.setVisible(true);
        textSavedToScores.setVisible(true);
        enterName.setVisible(true);
        spaceForName.setVisible(true);
        submitName.setVisible(true);
        tenCharLimit.setVisible(true);
        boardIncorrectFill.setVisible(false);
        unsolvableBoard.setVisible(false);
        giveUpButton.setVisible(false);
        
        spaceForName.setDocument
        (new JTextFieldCharacterLimiter(12));
        //limits user name to 12 characters max for spaceForName jtextfield
    }
    
    /**
     * clearUserInput clears all user inputted numbers into the Sudoku board table.
     * @param table the JTable displaying the sudoku board
     * Pre: table has at least 9 rows and columns
     * Post: table is cleared of all user input, and those boxes are then empty
     */
    public void clearUserInput (JTable table)
    {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        //makes a model of table
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j <9; j++) {
                if (getVisibleBoard()[i][j]==0)
                    table.setValueAt(null,i,j);
                /*
                setValueAt takes in (Object,int,int), so wherever getVisibleBoard 
                has a 0 saved, that means that
                i,j, is the coordinates of a box the user can edit, giving true 
                for the if statement.
                Then sets the value of the box the user can edit, to null, emptying it.
                */ 
            }
        }
    }
    
    /**
     * displayTimer is a method that displays timer as the user 
     * plays the game of sudoku, that keeps track
     * of how long it takes them to complete the game of sudoku.
     * Pre: all public variables are declared
     * Post: timer is started and displayed on jFrame
     */
    public void displayTimer()
    {
        timer = new Timer(100, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    runTime = System.currentTimeMillis() - gameStartTime;
                    dur = Duration.ofMillis(runTime);
                    long hours = dur.toHours();
                    dur = dur.minusHours(hours);
                    long minutes = dur.toMinutes();
                    dur = dur.minusMinutes(minutes);
                    long seconds = dur.toMillis() / 1000;
                    timerDisplay.setText(String.format("%02d:%02d:%02d", 
                            hours, minutes, seconds));
                    //displays timer in hours/mins/secs format
                }
            });
            gameStartTime = System.currentTimeMillis();//game started time is set
            timer.start();
    }
    
    /**
     * readNameAndTime is a method that sets public variables equal to 
     * values that hold the user's name and time in milliseconds to
     * complete the sudoku board.
     * Pre: spaceForName is a declared public jTextField and the 
     * methods called up exist.
     * Post: the public variables are set to the user's name 
     * and time in milliseconds to complete a sudoku board
     */
    public void readNameAndTime ()
    {
        //System.out.println("runTime:"+runTime);
        String n;
        //n=spaceForName.getText().trim();
        n=spaceForName.getText();
        //String p=n.replaceAll("\s+","");
        String p=n.replaceAll(" ","");
        //n= trimAllWhitespace(spaceForName.getText());
        //n.replaceAll("\\s+","");
        //String n = StringUtils.deleteWhitespace(spaceForName.getText());
        System.out.println("Name:"+p);
        setName(p);
        //System.out.println("name:"+getName());
        setMilliseconds(runTime);
        //System.out.println("ms:"+getMilliseconds());
    }
    
    /**
     * setTextDifficulty sets the jLabel "difficultyDisplay" 
     * to certain texts and colours 
     * depending on the difficulty of the board user is currently playing.
     * Pre: jLabel is public and exists, all methods called up exist
     * Post: jLabel is set to a certain text and colour
     */
    public void setTextDifficulty ()
    {
        if (getDifficulty()==1)
        {
            difficultyDisplay.setText("Easy");
            difficultyDisplay.setForeground(Color.GREEN);
        }
        else if (getDifficulty()==2)
        {
            difficultyDisplay.setText("Medium");
            difficultyDisplay.setForeground(Color.YELLOW);
        }
        else
        {
            difficultyDisplay.setText("Hard");
            difficultyDisplay.setForeground(Color.RED);
        }
    }
    
    /**
     * displayIncorrectTemporary method displays a label for 6 seconds, then it disappears.
     * @param label a JLabel displaying some type of "your board is incorrect" message
     * Pre: public variables are declared
     * Post: Jlabel is displayed on JFrame for 6 seconds
     */
    public void displayIncorrectTemporary (JLabel label)
    {
        Timer incorrectTimer = new Timer(6000, new ActionListener() {
        /*has delay of 6000 milliseconds, 
            so when timer is started it is active for 6000 milliseconds    
            */
            @Override
                public void actionPerformed(ActionEvent e) {
                   
                        label.setVisible(false);
                        //makes label be displayed for 6000 milliseconds
                        incorrectDisappear=true;
                        //after 6000 ms, boolean is set to true
                }
            });
        
/*incorrectDisappear is already declared as false as a public variable, 
        when method is called up this boolean statement will first be true
        */
        if(!incorrectTimer.isRunning() && incorrectDisappear==false)
        //if incorrectTimer isn't already running and boolean is false
        {
            incorrectTimer.start();
        }
        
        if (incorrectDisappear==true)
        //boolean is only set to true after message is displayed for 6000 ms
        {
            incorrectTimer.stop();//stops timer
            incorrectDisappear=false;//reset boolean value to false
        }
            
    }
    
    /**
     * displayNumOfSol is a method that takes the current JTable, 
     * including any user input so far, that holds a sudoku board
     * and sets a JLabel numOfSol to display the number of 
     * solutions the current board has.
     * Pre: methods called up exist, public variables exist, the public JTable 
     * t has at least 9 columns and 9 rows, public jLabel exists
     * Post:a jLabel displays how many solutions are possible for current board
     */
    public void displayNumOfSol ()
    {
        setNumOfSol(0);//sets num of solutions to 0
        Integer[][] n=new Integer[9][9];/*
        Integer 2d array that will hold all sudoku board values 
        from VisibleBoard including user's input
        */
        
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                try {
                n[i][j] =Integer.parseInt(t.getValueAt(i,j).toString());
                }
                catch (Exception e)//if value in box was null, would catch
                {
                    n[i][j]=0;
                }
            }
        }
        numOfSol.setText("Number of Board Solutions: "+solutionsCounter(n));
        //sets JLabel "numOfSol" text to display number of solutions
    }
    
    /**
     * solver takes the current JTable,including user input, that holds a 
     * sudoku board and tries to develop a solution to it. 
     * If board is solvable, displays the solution on board and ends the game, 
     * while not allowing user to enter their name 
     * for a highscore time since they gave up. If board is unsolvable, a 
     * JLabel appears for 6 seconds saying that
     * the board was unsolvable based off current user input, and reverts board
     * to what it displayed before this method 
     * was called up.
     * Pre: JTable t is declared and has at least 9 columns and 9 rows, public 
     * JLabels and JButtons are declared, and methods called up exist
     * Post: Fills in board with solution if current including user input board 
     * is solvable, and if unsolvable
     * displays "current board is unsolvable" message .
     */
    public void solver ()
    {
        Integer[][] n=new Integer[9][9];
        /*
        Integer 2d array that will hold all sudoku board values from 
        VisibleBoard including user's input
        */
        Integer[][] copy=new Integer[9][9];
        //copy of n but does not get solved, keeps user's input
        DefaultTableModel model = (DefaultTableModel)t.getModel();
        
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                try {
                n[i][j] =Integer.parseInt(t.getValueAt(i,j).toString());
                copy[i][j]=n[i][j];
                }
                catch (Exception e)//if value in box was null, would catch
                {
                    n[i][j]=0;
                }
            }
        }
        
        solveBoard(n);//with user's inputs put into n, now tries to solve n
        
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                model.setValueAt((Object)n[i][j], i, j);
            }
        }
        checker(t);
        
        if (isCorrect())//if user correctly filled in sudoku board
        {
            timer.stop();
            //stop timer, to record amount of time taken to complete sudoku board
            editTable=false;
            checkButton.setVisible(false);
            clearButton.setVisible(false);
            ruleButton.setVisible(false);
            newGameButton.setVisible(false);
            giveUpButton.setVisible(false);
            scoreNotSaved.setVisible(true);
    //does not allow for user to enter highscore since they did not win themselves
        }
        else
        {
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                        model.setValueAt((Object)copy[i][j], i, j);
        /*reverts table's cell value to what it was before user clicked 
             "Give up and solve" button, still keeping user's previous input
                        */
            }
                displayIncorrectTemporary(unsolvableBoard);
                unsolvableBoard.setVisible(true);
                //displays message saying board was unsolvable
                giveUpButton.setVisible(true);
        }
        }
    }
    
    /**
     * tableFontChanger is a method that changes the font (most importantly 
     * colour and background colour) of certain cells
     * in the table. This is with purpose of being able to visually distinguish
     * user inputted numbers (blue colour) from 
     * code generated numbers (black numbers), and setting certain quadrant's
     * background colours to certain colours to 
     * visually make quadrants easier to recognize.
     * Pre: public variables are declared and initialized
     * Post: c is returned with new colours, changing the font and background 
     * colour of that certain cell
     * return c: returns the component for the cell, with its colour info changed
     */
    public static class tableFontChanger extends DefaultTableCellRenderer {
        @Override
        
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column)
                    //this is a built in method
        {
            Component c = super.getTableCellRendererComponent(table, value, isSelected,
                    hasFocus, row, column);
            Object cellInfo = table.getValueAt(row, column);
            
            inner: for (int i = 0; i < locationI.size(); i++) {
                if (row==locationI.get(i) && column==locationJ.get(i))
                /*checks if coordinates of current box handling exists in the 
                    arraylists, if they do then number was generated by computer 
                    
                    */
                        {
                            c.setForeground(Color.BLACK);//set text colour to black
                            c.setFont(new Font("Courier New", Font.BOLD, 36));
                            //c.setAlignmentX(CENTER_ALIGNMENT);
                            break inner;
                            /*
    if box currently handling has its' cooridinates 
    (row,column) found in locationI and locationJ arraylists,
     means the number in box was generated by code, not user 
     inputted. Means its' colour should be black,
    and breaks inner, the for loop, so it can leave the box as 
    black and start handling the next box.
    if break wasn't there, would keep handling current box 
    and once for loop runs again, would
    set the foreground as red.
                            */
                            
                        }  
                        else
                        {
                            c.setForeground(Color.BLUE);
                            //set text colour to blue if user input
                            c.setFont(new Font("Courier New", Font.BOLD, 36));
                        }
                }

                Color boxColor=Color.PINK;
                
//below if statement checks if the box currently handling exists in certain quadrants
                if ((row<3 && column<3) || (row<3 && column>5) || 
                        (row>=3 && row<=5 && column>=3 && column<=5) || 
                        (row>5 && column<3) || (row>5 && column>5))
                    c.setBackground(boxColor);//sets background of box to pink colour
                else
                    c.setBackground(table.getBackground());
        //sets box background colour to table's default colour, in this case white

            return c;
        }
    }
   /**
    * JTextFieldCharacterLimiter sets a limit for number of 
    * characters that can be entered into a JTextField
    * Pre: none
    * Post: sets a character limit for the JTextField this method is used on
    */ 
    public class JTextFieldCharacterLimiter extends PlainDocument {
  private int lim;

  JTextFieldCharacterLimiter(int limit) {
   super();
   this.lim = limit;
   }

  public void insertString( int offset, String  str, AttributeSet attr ) 
          throws BadLocationException {
    if (str == null) return;
    //basically this return doesn't allow more characters to be inputted

    if ((getLength() + str.length()) <= lim) {
      super.insertString(offset, str, attr);
    }
  }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        checkButton = new javax.swing.JButton();
        quitButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();
        sudokuBoard = new javax.swing.JPanel();
        timerDisplay = new javax.swing.JLabel();
        youWon = new javax.swing.JLabel();
        textSavedToScores = new javax.swing.JLabel();
        enterName = new javax.swing.JLabel();
        spaceForName = new javax.swing.JTextField();
        submitName = new javax.swing.JButton();
        difficultyDisplay = new javax.swing.JLabel();
        tenCharLimit = new javax.swing.JLabel();
        boardIncorrectFill = new javax.swing.JLabel();
        ruleButton = new javax.swing.JButton();
        newGameButton = new javax.swing.JButton();
        numOfSol = new javax.swing.JLabel();
        giveUpButton = new javax.swing.JButton();
        unsolvableBoard = new javax.swing.JLabel();
        scoreNotSaved = new javax.swing.JLabel();
        justSolveButton = new javax.swing.JButton();
        solveHereButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1280, 720));
        setPreferredSize(new java.awt.Dimension(1280, 720));
        setResizable(false);
        getContentPane().setLayout(null);

        checkButton.setBackground(new java.awt.Color(20, 240, 20));
        checkButton.setFont(new java.awt.Font("Tahoma", 0, 48)); // NOI18N
        checkButton.setText("Check");
        checkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkButtonClicked(evt);
            }
        });
        getContentPane().add(checkButton);
        checkButton.setBounds(116, 573, 194, 67);

        quitButton.setBackground(new java.awt.Color(240, 0, 0));
        quitButton.setFont(new java.awt.Font("Tahoma", 0, 48)); // NOI18N
        quitButton.setText("Quit");
        quitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitButtonClicked(evt);
            }
        });
        getContentPane().add(quitButton);
        quitButton.setBounds(895, 573, 135, 67);

        clearButton.setBackground(new java.awt.Color(240, 155, 0));
        clearButton.setFont(new java.awt.Font("Tahoma", 0, 44)); // NOI18N
        clearButton.setText("Clear Entire Board");
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonClicked(evt);
            }
        });
        getContentPane().add(clearButton);
        clearButton.setBounds(406, 577, 385, 63);

        sudokuBoard.setMinimumSize(new java.awt.Dimension(405, 365));
        sudokuBoard.setName(""); // NOI18N
        sudokuBoard.setOpaque(false);
        sudokuBoard.setPreferredSize(new java.awt.Dimension(405, 365));
        sudokuBoard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                sudokuBoardMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout sudokuBoardLayout = new javax.swing.GroupLayout(sudokuBoard);
        sudokuBoard.setLayout(sudokuBoardLayout);
        sudokuBoardLayout.setHorizontalGroup(
            sudokuBoardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 405, Short.MAX_VALUE)
        );
        sudokuBoardLayout.setVerticalGroup(
            sudokuBoardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 365, Short.MAX_VALUE)
        );

        getContentPane().add(sudokuBoard);
        sudokuBoard.setBounds(415, 109, 405, 360);

        timerDisplay.setFont(new java.awt.Font("Tahoma", 0, 48)); // NOI18N
        timerDisplay.setText("00:00:00");
        getContentPane().add(timerDisplay);
        timerDisplay.setBounds(47, 144, 190, 74);

        youWon.setFont(new java.awt.Font("Tahoma", 0, 48)); // NOI18N
        youWon.setForeground(new java.awt.Color(40, 200, 40));
        youWon.setText("You Won!");
        getContentPane().add(youWon);
        youWon.setBounds(878, 137, 224, 81);

        textSavedToScores.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        textSavedToScores.setText("Time Saved to Scores");
        getContentPane().add(textSavedToScores);
        textSavedToScores.setBounds(47, 224, 280, 29);

        enterName.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        enterName.setText("Please Enter Your Name:");
        getContentPane().add(enterName);
        enterName.setBounds(838, 332, 330, 29);

        spaceForName.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        spaceForName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                spaceForNameActionPerformed(evt);
            }
        });
        getContentPane().add(spaceForName);
        spaceForName.setBounds(838, 386, 265, 35);

        submitName.setFont(new java.awt.Font("Bradley Hand ITC", 0, 24)); // NOI18N
        submitName.setText("Submit Name");
        submitName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitNameActionPerformed(evt);
            }
        });
        getContentPane().add(submitName);
        submitName.setBounds(887, 451, 177, 39);

        difficultyDisplay.setFont(new java.awt.Font("Tahoma", 0, 48)); // NOI18N
        difficultyDisplay.setText("Difficulty");
        getContentPane().add(difficultyDisplay);
        difficultyDisplay.setBounds(527, 33, 185, 58);

        tenCharLimit.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        tenCharLimit.setText("(limit of 12 characters)");
        getContentPane().add(tenCharLimit);
        tenCharLimit.setBounds(923, 367, 170, 13);

        boardIncorrectFill.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        boardIncorrectFill.setForeground(new java.awt.Color(255, 0, 0));
        boardIncorrectFill.setText("Board Incorrectly Filled.");
        getContentPane().add(boardIncorrectFill);
        boardIncorrectFill.setBounds(838, 230, 300, 29);

        ruleButton.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        ruleButton.setText("Rules");
        ruleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ruleButtonActionPerformed(evt);
            }
        });
        getContentPane().add(ruleButton);
        ruleButton.setBounds(92, 389, 117, 53);

        newGameButton.setBackground(new java.awt.Color(30, 30, 240));
        newGameButton.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        newGameButton.setText("Generate New Board");
        newGameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newGameButtonActionPerformed(evt);
            }
        });
        getContentPane().add(newGameButton);
        newGameButton.setBounds(35, 311, 255, 37);

        numOfSol.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        numOfSol.setText("Number of Solutions");
        getContentPane().add(numOfSol);
        numOfSol.setBounds(10, 70, 380, 29);

        giveUpButton.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        giveUpButton.setText("Give Up and Solve Board");
        giveUpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                giveUpButtonActionPerformed(evt);
            }
        });
        getContentPane().add(giveUpButton);
        giveUpButton.setBounds(878, 43, 297, 37);

        unsolvableBoard.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        unsolvableBoard.setForeground(new java.awt.Color(240, 0, 0));
        unsolvableBoard.setText("Board Is Unsolvable");
        getContentPane().add(unsolvableBoard);
        unsolvableBoard.setBounds(854, 285, 270, 29);

        scoreNotSaved.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        scoreNotSaved.setForeground(new java.awt.Color(240, 0, 0));
        scoreNotSaved.setText("Score Not Saved");
        getContentPane().add(scoreNotSaved);
        scoreNotSaved.setBounds(47, 109, 300, 29);

        justSolveButton.setText("Just Solve Board");
        justSolveButton.setToolTipText("Solves the board regardless of previous user input. Useful if you don't know what you're doing.");
        justSolveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                justSolveButtonActionPerformed(evt);
            }
        });
        getContentPane().add(justSolveButton);
        justSolveButton.setBounds(865, 100, 149, 29);

        solveHereButton.setText("Solve From Here");
        solveHereButton.setToolTipText("Solves the board with original values and user inputted values. Useful if you know what you're doing but you're stuck.");
        solveHereButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                solveHereButtonActionPerformed(evt);
            }
        });
        getContentPane().add(solveHereButton);
        solveHereButton.setBounds(1012, 100, 151, 29);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SudokuGame/SudokuBASIC (1).png"))); // NOI18N
        getContentPane().add(jLabel1);
        jLabel1.setBounds(0, -50, 1450, 820);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void checkButtonClicked(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkButtonClicked
        // TODO add your handling code here:
        checker(t);
        if (isCorrect())//if user correctly filled in sudoku board
        {
            timer.stop();//stop timer, to record amount of time taken to complete sudoku board
            showAndHideComponentsPostWin();
        }
        else
        {
            boardIncorrectFill.setVisible(true);
            displayIncorrectTemporary(boardIncorrectFill);
        }
            
    }//GEN-LAST:event_checkButtonClicked

    private void quitButtonClicked(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitButtonClicked
        // TODO add your handling code here:
        this.setVisible(false);
        new MainMenu_jFrame().setVisible(true);
    }//GEN-LAST:event_quitButtonClicked

    private void clearButtonClicked(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonClicked
        // TODO add your handling code here:
        clearUserInput(t);
    }//GEN-LAST:event_clearButtonClicked

    private void spaceForNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_spaceForNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_spaceForNameActionPerformed

    private void submitNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitNameActionPerformed
        // TODO add your handling code here:
        readNameAndTime();
        enterName.setVisible(false);
        spaceForName.setVisible(false);
        submitName.setVisible(false);
        quitButton.setVisible(true);
        tenCharLimit.setVisible(false);
        
        if (getDifficulty()==1)
        {
        try {
        updateHighScores(milliseconds, name, "highScores_EASY.txt");
        }
        catch (Exception e)
        {
            System.out.println("Error updating high scores.");
        }
        }
        else if (getDifficulty()==2)
        {
            try {
        updateHighScores(milliseconds, name, "highScores_MEDIUM.txt");
        }
        catch (Exception e)
        {
            System.out.println("Error updating high scores.");
        }
        }
        else
        {
            try {
                updateHighScores(milliseconds, name, "highScores_HARD.txt");
            }
            catch (Exception e)
            {
            System.out.println("Error updating high scores.");
            }
        }
    }//GEN-LAST:event_submitNameActionPerformed

    private void ruleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ruleButtonActionPerformed
        // TODO add your handling code here:
        new Rules_jFrame().setVisible(true);
    }//GEN-LAST:event_ruleButtonActionPerformed

    private void newGameButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newGameButtonActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);
        new SudokuBoard_jFrame().setVisible(true);
    }//GEN-LAST:event_newGameButtonActionPerformed

    private void sudokuBoardMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_sudokuBoardMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_sudokuBoardMouseClicked

    private void giveUpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_giveUpButtonActionPerformed
        // TODO add your handling code here:
        justSolveButton.setVisible(true);
        solveHereButton.setVisible(true);
        giveUpButton.setVisible(false);
        //solver();
        //checker(t);
        
    }//GEN-LAST:event_giveUpButtonActionPerformed

    private void solveHereButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_solveHereButtonActionPerformed
        // TODO add your handling code here:
         solver();
         justSolveButton.setVisible(false);
         solveHereButton.setVisible(false);
    }//GEN-LAST:event_solveHereButtonActionPerformed

    private void justSolveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_justSolveButtonActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel)t.getModel();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                model.setValueAt((Object)getBoardKey()[i][j], i, j);
            }
        }
        checker(t);
        timer.stop();
        //stop timer, to record amount of time taken to complete sudoku board
        editTable=false;
        checkButton.setVisible(false);
        clearButton.setVisible(false);
        ruleButton.setVisible(false);
        newGameButton.setVisible(false);
        giveUpButton.setVisible(false);
        scoreNotSaved.setVisible(true);
        
        justSolveButton.setVisible(false);
        solveHereButton.setVisible(false);
    }//GEN-LAST:event_justSolveButtonActionPerformed

    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SudokuBoard_jFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SudokuBoard_jFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SudokuBoard_jFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SudokuBoard_jFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SudokuBoard_jFrame().setVisible(true);
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel boardIncorrectFill;
    private javax.swing.JButton checkButton;
    private javax.swing.JButton clearButton;
    private javax.swing.JLabel difficultyDisplay;
    private javax.swing.JLabel enterName;
    private javax.swing.JButton giveUpButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton justSolveButton;
    private javax.swing.JButton newGameButton;
    private javax.swing.JLabel numOfSol;
    private javax.swing.JButton quitButton;
    private javax.swing.JButton ruleButton;
    private javax.swing.JLabel scoreNotSaved;
    private javax.swing.JButton solveHereButton;
    private javax.swing.JTextField spaceForName;
    private javax.swing.JButton submitName;
    private javax.swing.JPanel sudokuBoard;
    private javax.swing.JLabel tenCharLimit;
    private javax.swing.JLabel textSavedToScores;
    private javax.swing.JLabel timerDisplay;
    private javax.swing.JLabel unsolvableBoard;
    private javax.swing.JLabel youWon;
    // End of variables declaration//GEN-END:variables

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getMilliseconds() {
        return milliseconds;
    }

    public void setMilliseconds(long milliseconds) {
        this.milliseconds = milliseconds;
    }
}
