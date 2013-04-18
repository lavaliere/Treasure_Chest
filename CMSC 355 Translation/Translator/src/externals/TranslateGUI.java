package externals;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import javax.swing.*;


/*************************************************
 * TranslateGUI class
 * Description of Class ..........................
 * This class is where the meat of the Translator
 * is executed- the GUI is built, and the 
 * Translate button's actions are defined.
 * 
 * The Translate button sets off several events-
 * it sends a request to run the Dictionary.jar
 * and send it the requested translation 
 * parameters and if successful, it calls a
 * TranslationExport object to write the 
 * translation to a file.
 * 
 *************************************************/
public class TranslateGUI implements ActionListener {
	boolean DEBUG = false;
	private JFrame frame = new JFrame();
	ServiceLoad sl = new ServiceLoad();
	
	/* JLabels*/
	private final JLabel jlInstruction = new JLabel("I want to translate ");;
	private final JLabel jlTo = new JLabel("to ");
	private final JLabel jlTranslation = new JLabel("Translation: "); 
	private final JLabel jlBlank = new JLabel("");

	/* Misc GUI components */
	private final String[] words = new String[] {"pig", "dog", "cat", "mouse", "house",
												"runs", "jumps","flies", "eats", "digs",
												"he", "she", "it", "we", "them"}; 
	
	//Load these from the service file
	private final String[] languages = sl.loadLang(); //Loads string of Service stubs Service Names
	private final String[] stubs = sl.loadStubs(); //Loads substring of Service stub names

	private final JComboBox<String> jcWordList = new JComboBox<String>(words);
	private final JComboBox<String> jcLanguageList = new JComboBox<String>(languages);
	private final JTextField jtTranslation = new JTextField("");
	private final JButton jbTranslate = new JButton("Translate!");	
	
	public String language = (String)jcLanguageList.getSelectedItem();
	public String word = (String) jcWordList.getSelectedItem(); 
	
	PrintWriter out = null;
	BufferedReader in = null;
	

	public TranslateGUI(PrintWriter out2, BufferedReader in2) {
		out = out2;
		in = in2;
	}


	/* GUI Methods */
	public void display() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(4,2));
		
		/* First Row*/
		panel.add(jlInstruction);		//add Instruction label
		panel.add(jcWordList); 			//dropdown list of words
			jcWordList.setSelectedIndex(0);
		
		/* Second Row*/
		panel.add(jlTo); 				//JLabel for "to " text
		panel.add(jcLanguageList);		//dropdown list of languages
			jcLanguageList.setSelectedIndex(0);
		
		/* Third Row */
		panel.add(jlTranslation);	//JLabel for "Translation: " text
		panel.add(jtTranslation);	//textbox where translated word appears
			jtTranslation.setEditable(false);	
			
		/* Fourth Row */
		panel.add(jlBlank);			//blank for boxLayout
		panel.add(jbTranslate);		//button that runs the translation
		
		addActionListeners();
		frame.add(panel);
		frame.pack();
		panel.setVisible(true);
		frame.setVisible(true);
	}
	
	private void addActionListeners(){
		jbTranslate.addActionListener(this);
		
		WindowHandler wh = new WindowHandler();
		frame.addWindowListener(wh);
		
	}
	
	public void actionPerformed(ActionEvent e) {
		String sWord = jcWordList.getSelectedItem().toString();
		int index = jcLanguageList.getSelectedIndex();
		String sLang = stubs[index]; //Pull selected stub number
		String result = "";
		
		try{
			//Finding current directory location 
			String path = (new File(".")).getAbsolutePath();
			int end = path.lastIndexOf("\\");
			String decodedPath = path.substring(0, end);

			out.println(sWord); //Send Word to be translated
			out.println(sLang); //Send service stub number
			out.flush();
			
			//JOptionPane.showMessageDialog(new JFrame(), "[Translator]Reading result...");
			while(true){
				result = in.readLine();
				Thread.sleep(400) ;
				if(!result.isEmpty()) break;
			}
			
			//Print out Dictionary's translation, if any
			setTranslationText(result);
			
			//If Dictionary translated the word, create a new TranslationExport object
			//and let it write the translation to a file.
			if(!result.isEmpty()){
				TranslationExport te = new TranslationExport(result, sWord, language);
				JOptionPane.showMessageDialog(new JFrame(), "[Translator]GUI: Exporting translation to " +decodedPath);
				te.write(decodedPath);
			}
			result = "";

		} catch (Exception e1) {
			//If attempt at loading Dictionary fails, alert User with a pop-up
			//and printStackTrace()
			JOptionPane.showMessageDialog(new JFrame(), "[Translator]GUI: Error:\n" + e1.getMessage());
			e1.printStackTrace();
		}
	}//actionPerformed

	private void setTranslationText(String result){
		jtTranslation.setText(result);
	}

}//class translateGUI
