package main;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.prefs.Preferences;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;

/** Generates an adjective-noun pair from lists. */
public class RandomWords {
    
    static final String WIDTH_PREF = "guiwidth";
    static final String ADJS_PREF = "adjsfile";
    static final String NOUNS_PREF = "nounsfile";
    static final String NO_FILE = "No file chosen";
    static final String TITLE = "Random Words";
    
    /** Random instance. */
    static final Random RAND = new Random();
    /** File chooser instance. */
    static final JFileChooser FILE_CHOOSER = new JFileChooser(); 
    /** Preferences instance. */
    static final Preferences PREFS = Preferences.userNodeForPackage(RandomWords.class);
    
    /** List of adjectives. */
    static final List<String> ADJS_LIST = new ArrayList<String>();
    /** List of nouns. */
    static final List<String> NOUNS_LIST = new ArrayList<String>();
    
    /** Adjectives file location. */
    static String adjsFile = PREFS.get(ADJS_PREF, NO_FILE);
    /** Nouns file location. */
    static String nounsFile = PREFS.get(NOUNS_PREF, NO_FILE);
    
    /** Main frame of the GUI. */
    static JFrame frame;
    /** Button to choose adjectives list file. */
    static JButton adjsButton = new JButton("Adjectives list");
    /** Label to show selected adjectives file. */
    static JLabel adjsLabel = new JLabel(adjsFile);
    /** Button to choose nouns list file. */
    static JButton nounsButton = new JButton("Nouns list");
    /** Label to show selected nouns file. */
    static JLabel nounsLabel = new JLabel(nounsFile);
    /** Button to get a new prompt. */
    static JButton promptButton = new JButton("New prompt");
    /** Label containing the current prompt. */
    static JLabel promptLabel = new JLabel(" ");
    
    public static void main(String[] args) {
        
        readAdjs();
        readNouns();
        setupGui();
    }
    
    /** Sets up the GUI window. */
    static void setupGui() {
        
        // Setup main frame
        frame = new JFrame();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setResizable(true);
        frame.setTitle(TITLE);
        frame.setLayout(new GridBagLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                
                PREFS.putInt(WIDTH_PREF, frame.getWidth());
            }
        });
        
        // Setup adjs button and label
        GridBagConstraints conAdjsButton = new GridBagConstraints();
        conAdjsButton.gridy = 0;
        conAdjsButton.insets = new Insets(30, 30, 0, 30);
        adjsButton.addActionListener((e) -> getAdjs());
        frame.add(adjsButton, conAdjsButton);

        GridBagConstraints conAdjsLabel = new GridBagConstraints();
        conAdjsLabel.gridy = 1;
        conAdjsLabel.insets = new Insets(0, 30, 5, 30);
        frame.add(adjsLabel, conAdjsLabel);
        
        // Setup nouns button and label
        GridBagConstraints conNounsButton = new GridBagConstraints();
        conNounsButton.gridy = 2;
        conNounsButton.insets = new Insets(15, 30, 0, 30);
        nounsButton.addActionListener((e) -> getNouns());
        frame.add(nounsButton, conNounsButton);
        
        GridBagConstraints conNounsLabel = new GridBagConstraints();
        conNounsLabel.gridy = 3;
        conNounsLabel.insets = new Insets(0, 30, 5, 30);
        frame.add(nounsLabel, conNounsLabel);
        
        // Setup prompt button and label
        GridBagConstraints conPromptButton = new GridBagConstraints();
        conPromptButton.gridy = 4;
        conPromptButton.insets = new Insets(15, 30, 0, 30);
        promptButton.addActionListener((e) -> getPrompt());
        frame.add(promptButton, conPromptButton);

        GridBagConstraints conPromptLabel = new GridBagConstraints();
        conPromptLabel.gridy = 5;
        conPromptLabel.insets = new Insets(15, 30, 30, 30);
        promptLabel.setFont(new Font(promptLabel.getFont().getFontName(), Font.BOLD, promptLabel.getFont().getSize()));
        frame.add(promptLabel, conPromptLabel);
        
        // Display
        frame.pack();
        frame.setSize(PREFS.getInt(WIDTH_PREF, 0), frame.getHeight());
        int xPos = (int) ((screen.getWidth() - frame.getSize().width) / 2);
        int yPos = (int) ((screen.getHeight() - frame.getSize().height) / 2);
        frame.setLocation(xPos, yPos);
        frame.setVisible(true);
    }
    
    /** Gets adjectives file from user. */
    static void getAdjs() {
        
        FILE_CHOOSER.showOpenDialog(new JFrame());
        File file = FILE_CHOOSER.getSelectedFile();
        
        if (file == null) {
            
            return;
        }
        
        adjsFile = file.getAbsolutePath();
        adjsLabel.setText(adjsFile);
        PREFS.put(ADJS_PREF, adjsFile);
        readAdjs();
    }
    
    /** Gets nouns file from user. */
    static void getNouns() {
        
        FILE_CHOOSER.showOpenDialog(new JFrame());
        File file = FILE_CHOOSER.getSelectedFile();
        
        if (file == null) {
            
            return;
        }
        
        nounsFile = file.getAbsolutePath();
        nounsLabel.setText(nounsFile);
        PREFS.put(NOUNS_PREF, nounsFile);
        readNouns();
    }
    
    /** Reads adjectives file to the list. */
    static void readAdjs() {
        
        if (!NO_FILE.equals(adjsFile)) {
                
            try {
                
                BufferedReader reader = new BufferedReader(new FileReader(new File(adjsFile)));
                ADJS_LIST.clear();
                
                while (reader.ready()) {
                    
                    ADJS_LIST.add(reader.readLine().trim());
                }
                
                reader.close();
                            
            } catch (Exception e) {
    
                System.err.println("Problem reading file " + adjsFile);
                e.printStackTrace();
            }   
        }
    }
    
    /** Reads nouns file to the list. */
    static void readNouns() {
        
        if (!NO_FILE.equals(nounsFile)) {
            
            try {
                
                BufferedReader reader = new BufferedReader(new FileReader(new File(nounsFile)));
                NOUNS_LIST.clear();
                
                while (reader.ready()) {
                    
                    NOUNS_LIST.add(reader.readLine().trim());
                }
                
                reader.close();
                            
            } catch (Exception e) {
    
                System.err.println("Problem reading file " + nounsFile);
                e.printStackTrace();
            }   
        }
    }
    
    /** Sets the prompt text to a new random pair. */
    static void getPrompt() {
        
        String newLabel;
        
        if (ADJS_LIST.isEmpty()) {
            
            newLabel = "Invalid adjectives list";
            
        } else if (NOUNS_LIST.isEmpty()) {
            
            newLabel = "Invalid nouns list";
            
        } else {
       
            String adj = ADJS_LIST.get(RAND.nextInt(ADJS_LIST.size()));
            String noun = NOUNS_LIST.get(RAND.nextInt(NOUNS_LIST.size()));
            newLabel = adj + " " + noun;
        }
        
        promptLabel.setText(newLabel);
    }
}
