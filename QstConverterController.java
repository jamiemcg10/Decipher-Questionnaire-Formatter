import java.io.*;
import java.util.Scanner;
import java.util.regex.*;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Pos;

public class QstConverterController {

	
		// GUI controls defined in FXML and used by the controller's code
		@FXML
		private TextField qstName;
		
		@FXML
		private TextField exportFileName;
		
		

		
		@FXML 
		private void retrieveFileName(ActionEvent event) {
			String fileName = "";
			
			JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("TXT files", "txt");
			chooser.setFileFilter(filter);
			int open = chooser.showOpenDialog(null);
			if (open == JFileChooser.APPROVE_OPTION) {
				fileName = chooser.getSelectedFile().getAbsolutePath().replace("\\", "\\\\");
				System.out.println(fileName);
			}			
			
			qstName.setText(fileName);
			
		}
		
		
		
		private void write_question(QstAttributes qst, FileWriter exp){  // WRITE CONTENTS OF QUESTION OBJECT TO FILE
	    
			try {
			    
			        exp.write(qst.qtype + "," + qst.qname + "," + qst.qtext);
		
			    if (qst.hasComment) {
			        exp.write("sub,,\"" + qst.comment + "\"\n");
			    }
			    exp.write(qst.a_rows);
			    exp.write(qst.termText);
			    //exp.write("break\n\n");
			    exp.write(qst.pBreak);
			    
			} catch (IOException e) {
				;
			}  // end try/catch
			
		}  // end write_question(QstAttributes qst, FileWriter exp)
		
		
		@FXML
		private void doStuff(ActionEvent event) throws IOException {

			
			FileWriter export;
			File file = new File (qstName.getText());
			try {
				String filePath = qstName.getText();
				int filePathInd = filePath.lastIndexOf("\\");
				filePath = filePath.substring(0, filePathInd+1);
				filePath = filePath + exportFileName.getText() + ".csv";
				export = new FileWriter(filePath);

			
			QstAttributes question = null;

			Pattern qst = Pattern.compile("[A-Z]+[a-z]*\\d+\\.");  // QUESTION PATTERN - MATCHES UPPERCASE LETTER, FOLLOWED BY OPTIONAL LOWERCASE LETTER, NUMBER, AND PERIOD
			Pattern ans = Pattern.compile("^\\d+\\.");             // ANSWER PATTERN - MATCHES NUMBER AND PERIOD


		Scanner questionnaire = new Scanner(file);
		// INITIALIZE FILE
		export.write("#defaultBlanks=True version=2.0\n");
		export.write("type,label,text\n");
		export.write("survey,,Survey Name\n\n\n");
		
		
		// LOOP THROUGH LINES IN QUESTIONNAIRE
		while (questionnaire.hasNextLine()) {
		    String line = questionnaire.nextLine().trim();
		    System.out.println(line);
		    
		    	// TEST FOR TERMINATE
		    	if (line.toLowerCase().indexOf("terminate if") >= 0 & line.toLowerCase().indexOf("terminate if") < 3) {
		    		question.termText = "term,,\"" + line + "\"\n";
		    		continue;
		    	}
		    
		        // TEST FOR QUESTION
		    	Matcher qstMatch = qst.matcher(line);
		    	Matcher ansMatch = ans.matcher(line);
		        if (qstMatch.find() | line.toLowerCase().indexOf("txt.") > 0) {
		        	int commentEnd = line.toLowerCase().indexOf("txt.");
		            // NEW QUESTION
		            // WRITE PREVIOUS QUESTION TO FILE IN ONE EXISTS
		            if (question != null) {
		                write_question(question, export); }
		            
		            question = new QstAttributes();   // CREATE NEW/REFRESH QUESTION OBJECT    
		     
		            if (commentEnd > -1) {
		            	question.qtext = line.substring(commentEnd+4).trim() + "\n";
		            	question.qname = line.substring(0,commentEnd+3).trim();
		            	question.qtype = "comment";
		            	

		            } else {
		            	int qend_index = qstMatch.end();   // INDEX OF WHERE VARIABLE NAME ENDS
		            	question.qtext = "\"" + line.substring(qend_index).trim() + "\"\n";  // EXTRACT QUESTION TEXT
		            	question.qname = line.substring(0, qend_index-1).trim();    // EXTRACT VARIABLE BAME
		            }
		        }
		       
			           
		        
		        
		        // TEST FOR ROW/COLUMN ANSWER
		        if (line.toLowerCase().indexOf("grid columns") >= 0) {
		        	question.setAType('c');
		        }
		        
		        if (line.toLowerCase().indexOf("grid rows") >= 0) {
		        	question.setAType('r');
		        }
		        
		        if (ansMatch.find()) {
		            int aend_index = ansMatch.end();
		            question.a_rows += question.aType + ",," + "\"" + line.substring(aend_index).trim() + "\"\n";
		            continue;
		        }
		        
		        
		        // TEST FOR QUESTION TYPE
		        if (line.toLowerCase().indexOf("check one") >= 0) {  // SINGLE SELECT
		            question.qtype = "radio";}
		        if (line.toLowerCase().indexOf("check all") >= 0) {  // MULTIPLE SELECT
		            question.qtype = "checkbox";}
		        if (line.toLowerCase().indexOf("verbatim") >= 0) {  // TEXT ENTRY]	
		            question.qtype = "essay";}    
		        if (line.toLowerCase().indexOf("enter number") >= 0) {  // TEXT ENTRY
		            question.qtype = "number";}    
		            
		        	
		  
		        
		        // CHECK FOR SECONDARY LINE
		        if (line.length() > 0 && (line.substring(0,1).equals("(") & line.substring(line.length()-1,line.length()).equals(")"))) {
		        	question.hasComment = true;
		            question.comment = line;
		        }

		        
		        
		        // CHECK FOR REMOVAL OF PAGE BREAK
		        if (line.toLowerCase().indexOf("no page break") >= 0) {
		        	question.removePageBreak();
		        }
		
		}
		  
		questionnaire.close();
		export.close();

		Label completeMsg = new Label("Conversion complete!");
		Button okButton = new Button("OK");
		Stage popStage = new Stage();

		VBox vbox1 = new VBox(20);
		vbox1.getChildren().addAll(completeMsg, okButton);
		vbox1.setAlignment(Pos.CENTER);
		
		EventHandler<ActionEvent> butEvent = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				popStage.hide();
				
			}
			
		};
		
		okButton.setOnAction(butEvent);
		
		Scene completeScreen = new Scene (vbox1, 200, 100);
		
		popStage.setScene(completeScreen);
		popStage.show();
		
			} catch (FileNotFoundException e) {
				System.out.println("The file could not be created. Please make sure the file is not open.");
			}
		
		}
		
		

	}


