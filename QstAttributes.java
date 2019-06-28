
public class QstAttributes {

	    // CREATE CLASS TO HOLD VALUES FOR EACH QUESTION
	    String qtype;
	    String qname;
	    String qtext;
	    String a_rows;
	    String randomize;
	    String mandatory;
	    String at_least;
	    Boolean hasComment; 
	    String comment;
	    String termText;
	    char aType;
	    String pBreak;

	    
	    public QstAttributes() {
		    qtype = "radio";
		    qname = "";
		    qtext = "";
		    a_rows = "";
		    randomize = "\"\"";
		    mandatory = "";
		    at_least = "\"1\"";
		    hasComment = false; 
		    comment = "";
		    termText="";
		    aType='r';
		    pBreak = "break\n\n";
	    }
	    
	    
	    
	    public void setAType(char newType) {
	    	aType = newType;
	    }
	    
	
	    public void removePageBreak() {
	    	pBreak = "\n\n";
	    }
	    
}
