import java.io.*;
import java.util.Scanner;
import java.util.regex.*;

public class Script {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Script script = new Script();
		script.doStuff();
	}
	
	
	
	public void write_question(QstAttributes qst, FileWriter exp){  // WRITE CONTENTS OF QUESTION OBJECT TO FILE
    
		try {
		    if (qst.qtype == "checkbox") {
		        exp.write("<" + qst.qtype + "\n\tlabel=\"" + qst.qname + "\"\n\tat least=" + qst.at_least + "\n\tshuffle=" + qst.randomize + ">\n");
		    } else {
		        exp.write("<" + qst.qtype + "\n\tlabel=\"" + qst.qname + "\">\n");
		    }
		    
		    exp.write("\t" + qst.qtext);
		    if (qst.hasComment) {
		        exp.write("\t<comment><em>" + qst.comment + "</em></comment>");
		    }
		    exp.write(qst.a_rows);
		    exp.write("</" + qst.qtype + ">\n");
		    exp.write("<suspend/>\n\n");
		} catch (IOException e) {
			;
		}  // end try/catch
		
	}  // end write_question(QstAttributes qst, FileWriter exp)
	
	
	public void doStuff() throws IOException {

		System.out.println("Welcome to the survey converter!");
		
		FileWriter export;
		File file = new File ("C:\\Users\\Jamie\\Documents\\Python projects\\Survey converter\\testfile.txt");
		export = new FileWriter("C:\\Users\\Jamie\\Documents\\Python projects\\Survey converter\\export.txt");
		
		QstAttributes question = null;

		Pattern qst = Pattern.compile("[A-Z]+[a-z]*\\d+\\.");  // QUESTION PATTERN - MATCHES UPPERCASE LETTER, FOLLOWED BY OPTIONAL LOWERCASE LETTER, NUMBER, AND PERIOD
		Pattern ans = Pattern.compile("^\\d+\\.");             // ANSWER PATTERN - MATCHES NUMBER AND PERIOD


	Scanner questionnaire = new Scanner(file);
	while (questionnaire.hasNextLine()) {
	    String line = questionnaire.nextLine().trim();
	    System.out.println(line);
	        // TEST FOR QUESTION
	    	Matcher qstMatch = qst.matcher(line);
	    	Matcher ansMatch = ans.matcher(line);
	        if (qstMatch.find()) {
	            // NEW QUESTION
	            // WRITE PREVIOUS QUESTION TO FILE IN ONE EXISTS
	            if (question != null) {
	                write_question(question, export); }
	            question = new QstAttributes();   // CREATE NEW/REFRESH QUESTION OBJECT
	            
	     
	            int qend_index = qstMatch.end();   // INDEX OF WHERE VARIABLE NAME ENDS
	            question.qtext = "<title>" + line.substring(qend_index).trim() + "</title>" + "\n";  // EXTRACT QUESTION TEXT
	            question.qname = line.substring(0, qend_index-1).trim();    // EXTRACT VARIABLE BAME
	            //System.out.println(question.qtext);
	        } 
	        
	        // TEST FOR QUESTION TYPE
	        if (line.toLowerCase().indexOf("check one") >= 0) {  // SINGLE SELECT
	            question.qtype = "radio";}
	        if (line.toLowerCase().indexOf("check all") >= 0) {  // MULTIPLE SELECT
	            question.qtype = "checkbox";}
	        if (line.toLowerCase().indexOf("enter verbatim") >= 0) {  // TEXT ENTRY
	            question.qtype = "essay";}    
	            
	        // TEST FOR ANSWER
	        if (ansMatch.find()) {
	            int aend_index = ansMatch.end();
	            System.out.println("answer length: " + line.length());
	            System.out.println("answer end index: " + aend_index);
	            System.out.println("match: " + ansMatch.group());
	            question.a_rows += "\t<row label=\"r" + ansMatch.group().substring(0,aend_index-1) + "\">";
	            question.a_rows += line.substring(aend_index).trim();
	            question.a_rows += "</row>\n";
	        }
	  
	        
	        // CHECK FOR SECONDARY LINE
	        if (line.length() > 1 && (line.substring(0,1) == "(" & line.substring(line.length()-2,line.length()-1) == ")")) {
	            question.hasComment = true;
	            question.comment = line;
	        }
	        
	        //System.out.println(line);
	
	}
	  
	questionnaire.close();
	export.close();
	
	}
	
	

}
