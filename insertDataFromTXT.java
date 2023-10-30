import java.io.*;
import java.util.*;

//credits to Dr. Wagner for general algorithm design
public class phase3 {
	static String tableName;
	static File tableData;
	public static void main(String[] args) {
		try {
			try (Scanner scanner = new Scanner(System.in)) {
				System.out.println("Please enter an existing table name and press enter. \n");
				String fileName = scanner.nextLine();
				tableData = new File(fileName + ".txt");
				System.out.println("Input recieved. Searching for table data file... \n");

				if (tableData.exists() && !tableData.isDirectory()) {
					tableName = tableData.getName();
					tableName = tableName.replace(".txt","");
					System.out.println("File found: " + tableData.getAbsolutePath() + ".\nGenerating SQL insert statements. For table name '"+tableName+"'.");
					
				} else {
					System.out.println("File not found. Exiting...");
				}
			}
        	//Create a file to write results to
			FileWriter outfile = new FileWriter("Results.csv");

			//Read an input file 	
			File infile = tableData;
			Scanner input = new Scanner(infile);

			//Read the file line by line. 
			while (input.hasNextLine()) { //while there's a line to read
				
				String line = input.nextLine(); //store that line in a String variable
				if (line.length() == 0) { //if the line doesn't have any info, skip it.
					outfile.write("\n");
				}
				else {
					outfile.write(processLine(line)); //write our data processing to the outfile

                    outfile.write("\n"); //write the next line else you'll join tail and head of each array
				}
			}
			outfile.close(); // close the outfile
			input.close(); // close the input file
		} catch (FileNotFoundException e) { 
			//Need to catch FNFE for FileWriter and Scanner
			System.out.println(e.getMessage());
		} catch (IOException e) {
			//Need to catch IOE for FileWriter and Scanner
			System.out.println(e.getMessage());			
		}
		//now, generate the SQL statements from the resulting CSV
		//Create a file to write results to
		String filename = "mySQLfile.sql";
        try {
            FileWriter SQLoutput = new FileWriter(filename);
			//Read an input file 	
			File infile = new File("Results.csv");
			Scanner intermediate= new Scanner(infile);
				//Read the file line by line. 
				while (intermediate.hasNextLine()) { //while there's a line to read
				
					String line = intermediate.nextLine(); //store that line in a String variable
					if (line.length() == 0) { //if the line doesn't have any info, skip it.
						SQLoutput.write("\n");
					}
					else {
						SQLoutput.write(genSQL(line)); //write our data processing to the outfile
		
						//SQLoutput.write("\n"); //write the next line else you'll join tail and head of each array
					}
				}
            
            SQLoutput.close();
			intermediate.close();
            System.out.println("SQL file created successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while creating the SQL file.");
            e.printStackTrace();
        }
	}

	public static String processLine(String line) {
		String output; //this will be the "clean" data that's returned
		
		ArrayList<String> cleanValues = new ArrayList<String>(); // I will break the line into a series of values. I'll store these values in a list.
		
		String[] values = line.split("[,]", 0); // Break the line into values that are divided by escape pipe character.
        

		for (String value : values) { // I can iterate through the values, do some additional cleaning/manipulation if I want, and then add to my list.
			
            cleanValues.add(value.trim()); //trim whitespace surrounding strings
                  
		}
        
        ArrayList<String> valueTypes = new ArrayList<String>(); //I will create a corresponding array to the clean values array
        //could just set the values to be different in the exisitng arrays and save some memory and processing, but for this small algorithm, this works fine
        for (String value: cleanValues) {
            
            try {
                Integer.parseInt(value); //if the value is an integer, the control flow will move to the following line
                valueTypes.add(value); //if we get to this line, we must be dealing with a numeric value                                      
            } catch (Exception e) {
                if(value.equals("NULL")) //if the string actually equals "NULL" we don't want to embed single quotes around it
                valueTypes.add(value);
				else{
					valueTypes.add( "'" + value + "'"); //O.W. it's a quoted string
				}
                //exceptions for passing a string type to a function expecting an integer mean we're dealing with text
            }
        }

		output = String.join(",", valueTypes); // Produce my output string by joining my clean values on the comma character.
		return output; 
	} 


	public static String genSQL(String line){
		String output; //this will be the "clean" data that's returned
		output = "INSERT INTO " + tableName + " VALUES (" + line + ");\n";
		
		return output;
	//	
	}
}
