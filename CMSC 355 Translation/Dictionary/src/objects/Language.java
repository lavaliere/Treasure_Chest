package objects;

import java.util.ArrayList;
import java.util.HashMap;

/*************************************************
 *		 			Language class
 * Description of Class ..........................
 * This class is to defines Language objects so 
 * that new languages may be added to the 
 * Dictionary dynamically. 
 * 
 * It  outlines how a Language object stores and 
 * searches for its translations, and contains a
 * method to run a translation.
 *************************************************
 */

public class Language {
	private HashMap<String, String> hmMap = new HashMap<String,String>();
	
	/*********************************************************
	/*No other constructors necessary because a Language object
	/*will never be declared if both parameters or only
	/*one parameter is given.
	******************************************************** */
	public Language(ArrayList<String> alEnglish, ArrayList<String> alLanguage){
		for(int i = 0; i < alEnglish.size(); i++){
			hmMap.put(alEnglish.get(i), alLanguage.get(i));
		}
	}
	
	/*********************************************************
	/*translate()
	 * This method allows the Dictionary app to translate
	 * a given word into the Language object's language.
	******************************************************** */
	public String translate(String searched){
		 return (hmMap.containsKey(searched) ? hmMap.get(searched) : "Translation not found!");
	}
}
