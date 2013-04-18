package externals;

import java.io.*;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/*************************************************
 *		 	TranslationExport class
 * Description of Class ..........................
 * This class is where a TranslationExport object
 * is defined.
 * 
 * A TE object stores a string taken from its 
 * only constructor's parameter, and if its
 * write() method is called, writes that
 * string to a "translation.text" file in a 
 * Temp folder (for Write privilege safety). 
 * 
 *************************************************
 */

public class TranslationExport {
	String translation = ""; //store the translation from the constructor
	String from = "";
	String lang = "";
	

/*************************************************
 *	Only one constructor is needed as this object
 * will never be created if there is no result
 * to print. 
 *************************************************/
	public TranslationExport(String result, String source, String language) {
		translation = result;
		from = source;
		lang = language;
	}
	

/*************************************************
 * write()
 *	This method attempts to write the translation
 * into a simple text file in a "temp" directory
 * made just in case the user is running Win7. 
 * 
 * If successful, the user will get a pop-up to
 * confirm it. If not, the user will also get
 * a pop-up outlining why the export failed.
 * 
 * This method returns false if the export fails,
 * true otherwise.
 *************************************************/
	public boolean write(String directory){
		PrintWriter out = null;
		
		//Attempt to create the translation.txt file
		try {
			String file = directory + "\\temp\\translation.txt\\";
			File export = new File(file);
			if(!export.exists()){
				try{
					export.getParentFile().mkdirs();
					export.createNewFile();
				}catch(Exception e){
					JOptionPane.showMessageDialog(new JFrame(), "[Translator] TE: ERROR: Could not create directory! " + directory + "\n" + e.getMessage());
					e.printStackTrace();
					return false;
				}
			}
			out = new PrintWriter(new BufferedWriter(new FileWriter(export, true)));
			out.println(from + " ----> " + translation);
			out.close();
			JOptionPane.showMessageDialog(new JFrame(), "[Translator] TE: Translation successful! \r\n Translation exported to:\r\n '" + directory + "\\temp\\translation.txt'");
		
		} catch (IOException e) {
			//If translation.txt cannot be created/written to
			//alert the user to the problem, printStackTrace(),
			//and return false.
			JOptionPane.showMessageDialog(new JFrame(), "[Translator] TE: ERROR: Translation could not be exported. \n" + e.getMessage() + "Path: " + directory);
			e.printStackTrace();
			return false;
		}		
		return true;
	}//write()

}//class TranslationExport
