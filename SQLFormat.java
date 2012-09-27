import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;
import java.util.StringTokenizer;

public class SQLFormat {
    public static void main(String[] args) throws FileNotFoundException{
        ArrayList alSecNum = new ArrayList();
        String sLine = "";
        String sUseNum;
        int count = 0;
        int stopFlag = 0;

        Scanner keyboard = new Scanner(System.in);

            do{
            System.out.println("Please enter sequence numbers for new sections. Type 'N' to stop.");
                sUseNum = keyboard.next();
                System.out.println("Added Section # " + sUseNum + ".");

                if(sUseNum.equalsIgnoreCase("N")){
                    stopFlag = -1;
                    keyboard.close();
                    break;
                } else{
                    alSecNum.add(sUseNum);
                    count++;
                }
            }while(stopFlag != -1); //while

        String sSubNum = "";

        for(int i = 0; i < count; i++){
            sSubNum = sSubNum + "'DESCRIPTION', " + "'" + (String)alSecNum.get(i)+ "//";
            System.out.println("SubNum is now " + sSubNum);
            System.out.println("Building string for for formatting... +\n" + sSubNum);
        }//for i < count

        System.out.println("Starting to import the file...");
    //    fileRead(alSecNum);
    //    formatSections(pullingSections(fileRead(alSecNum), sSubNum));
 fileWriter(formatSections(pullingSections(fileRead(alSecNum), sSubNum)));
    }//main

    //pulls all "INSERT INTO" into an Arraylist
    //WORKS!!
    public static ArrayList fileRead(ArrayList alSectionNumber) throws FileNotFoundException{
        Scanner fileRead = new Scanner(new FileReader("C://Documents and Settings/Tracy/Desktop/iwcontent.sql"));
        String sFileLine = "";
        ArrayList alFileInserts = new ArrayList();
        int iArrayListIndex = 0;

        while(fileRead.hasNext()){
            sFileLine = fileRead.nextLine();
            if(sFileLine.startsWith("INSERT INTO 'iwcontent'") == true){
                alFileInserts.add(iArrayListIndex, sFileLine);
                if(sFileLine.contains(");")){
                    iArrayListIndex++;
                }
            } else if(sFileLine.contains(");")){
                    alFileInserts.add(iArrayListIndex, sFileLine);
                    iArrayListIndex++;
            } else {
                alFileInserts.add(iArrayListIndex, sFileLine);
            }
        }//while
        for(int i = 0; i < alFileInserts.size(); i++){
            System.out.println("ArrayList of grabbed sections: " + alFileInserts.get(i).toString());
        }
        return alFileInserts;
    }//fileread method

    //breaking the 'string of fileRead's contents and searching number the user entered
    //after splitting it into a string array using the delimiter "//"
    //WORKS!!
    public static ArrayList pullingSections(ArrayList alSectionNumber, String sSubNum){
        ArrayList alOutput = new ArrayList();
        String[] saSubNumSplit = sSubNum.split("//");
        String[] saIWContent = new String[alSectionNumber.size()];
        String sDot = ".";
        String sApo = "'";

        //ArrayList to String array
        for(int i = 0; i < saIWContent.length; i ++){
            saIWContent[i] = (String) alSectionNumber.get(i);
        }
        //looping i through the numbers to be searched for
        for(int i = 0; i < saSubNumSplit.length; i++){
        //looping k through the imported string array's contents
            for(int k = 0; k < saIWContent.length; k++){
                if(saIWContent[k].contains(saSubNumSplit[i])){
                    //if the section number has a dot after
                    if(saIWContent[k].contains(saSubNumSplit[i] + sDot)){
                        System.out.println("Match found! Prints out as: " + saIWContent[k]);
                        alOutput.add(saIWContent[k]);
                        //if the section number has an apostrophe after
                    } else if(saIWContent[k].contains(saSubNumSplit[i] + sApo)){
                        System.out.println("Match found! Prints out as: " + saIWContent[k]);
                        alOutput.add(saIWContent[k]);
                    }
                }//if SecNum contains
            }//for arrayList length
        }//for loop
        return alOutput;
    }//ArrayList pullingSections;

    public static ArrayList formatSections(ArrayList alPulledSections){
        ArrayList alFormattedSec = new ArrayList();
        String[] saPulledSections = new String[alPulledSections.size()];
        String[] saSplitSection = new String[21];
        String sName = "";
        String sSequence = "";
        String sContent = "";
        String sBuilt = "";

        Object[] oaSeq = new Object[alPulledSections.size()];

        for(int i = 0; i < saPulledSections.length; i++){
            saPulledSections[i] = (String) alPulledSections.get(i);
            saSplitSection = saPulledSections[i].toString().split("', '|null, ");
            Sequence oSeq = new Sequence();
            sName = saSplitSection[1];
            sSequence = saSplitSection[10];
            sContent = saSplitSection[2];

            sBuilt = "\r\n INSERT INTO `iwcontent` \r\n\t (`Name`, `Type`, `Description`, `Sequence`, `KBAreaID`, `ParentID`, `ParentTableID`, `ModifiedBy`, `ModifiedDate`, `CreatedBy`, `CreatedDate`, `IsActive`, `Phase`, `ApprovedBy`, `DateApproved`, `Content`) \r\n\t VALUES ('" + sName + "', 'Help Documentation', 'DESCRIPTION', '"+ sSequence +"', 0, 0, 0, 1, NOW(), 1, NOW(), 1, 'Approved', 1, NOW(), \r\n\t '" + sContent + "'); \r\n\r\n";

            oSeq.setSequence(sSequence);
            oSeq.setBuilt(sBuilt);
            oaSeq[i] = oSeq;
            System.out.println("Sequence object to Object array...." + i + " Contents: " + ((Sequence) oaSeq[i]).getSequence());
        }//for

            System.out.println("After for loop...." + ((Sequence) oaSeq[0]).getSequence());

            Object[] oaSort = new Object[oaSeq.length];
            String sCast = "";

            for(int j = 0; j < oaSeq.length; j++) {
                for(int i = j + 1; i < oaSeq.length; i++) {
                    if(((Sequence) oaSeq[i]).getSequence().compareTo(((Sequence) oaSeq[j]).getSequence()) < 0) {
                        Sequence t = (Sequence) oaSeq[j];
                        oaSeq[j] = oaSeq[i];
                        oaSeq[i] = t;
                    }
                }//for i
            } //for j

            for(int i = 0; i < oaSeq.length ; i ++){
                Sequence secPull = (Sequence) oaSeq[i];
                String sPulled = secPull.getBuilt();
                alFormattedSec.add(sPulled);
            }
        return alFormattedSec;
    }//formatSections

    public static void fileWriter(ArrayList alFormattedSec){
        try {
            FileOutputStream out = new FileOutputStream(new File("C:/Documents and Settings/Tracy/Desktop/formattedSQL.txt"));
            String[] saFinal = new String[alFormattedSec.size()];

            for(int i = 0 ; i < alFormattedSec.size(); i++){
                System.out.println("Writing file line... " + (String) alFormattedSec.get(i));
                saFinal[i] = (String) alFormattedSec.get(i);
            }

            for(int i = 0; i < alFormattedSec.size(); i++){
                out.write(saFinal[i].getBytes());
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }//formatedSec method
}//SQLFormat class

class Sequence{
    public String sSequence = "";
    public String sBuilt = "";

    public Sequence(String sSetSeq, String sSetBuilt){
        setSequence(sSetSeq);
        setBuilt(sSetBuilt);
    }

    public Sequence(){

    }

    public void setSequence(String sSet){
        this.sSequence = sSet;
    }//set Sequence

    public String getSequence(){
        String sGet = "";
        sGet = this.sSequence;
        return sGet;
    }

    public void setBuilt(String sSet){
        this.sBuilt = sSet;
    }

    public String getBuilt(){
        String sReturn = "";
        sReturn = this.sBuilt;
        return sReturn;
    }
}//sequence

