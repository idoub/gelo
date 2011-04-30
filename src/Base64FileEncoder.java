import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.codec.binary.Base64;

public class Base64FileEncoder {
	
	private boolean singleFile = false;
	private boolean insertIntoHTML = false;
	private boolean fileCompare = false;
	private boolean lessThan = false;
	private long lessThanSize = 0;
	public String eol = System.getProperty("line.separator");
	public String fs = System.getProperty("file.separator");

	public static void main (String args[]) {
		Base64FileEncoder program = new Base64FileEncoder();
		try {
			program.run(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//*******************************MAIN RUN FUNCTION***************************************//
	private void run(String args[]) throws IOException{
		if (args.length < 1) {
			printHelp();
			System.exit(9);
		}
	if (args.length > 1) {
		for(int i=1;i<args.length;i++) {
			String option = args[i];
			if(option.equals("-h"))
				insertIntoHTML = true;
			else if(option.equals("-s"))
				singleFile = true;
			else if(option.equals("-c"))
				fileCompare = true;
			else if(option.equals("-lt")) {
				lessThan = true;
				lessThanSize = Long.parseLong(args[i+1]) * 1024;
				i++;
			}
			else {
				System.out.println("ERROR:Unrecognized option." + eol);
				printHelp();
				System.exit(9);
			}
		}
	}
	String base64file = null;
	File[] file = new File(args[0]).listFiles();
	base64file = getFiles(file);
	
	if(singleFile)
		writeBase64ToFile(base64file, "Base 64 Images");
	}

//****************************OPEN EACH FILE INDIVIDUALLY********************************//
	private String getFiles(File[] files) {
		String base64string = "";
		for (File file : files) {
	        if (file.isDirectory()) {
	            base64string += getFiles(file.listFiles());
	        } else {
	        	String tmpBase64 = encodeFile(file.toString());
	            if(!lessThan && !insertIntoHTML && !singleFile) {
	            	base64string = tmpBase64;
		            writeBase64ToFile(base64string, file.getName());
	            }

	        	if(fileCompare) {
        			File outFile = new File("base64" + fs + file.getName() + ".txt");
	        		if(outFile.length() > file.length()*1.35) {
	        			outFile.delete();
	        			continue;
	        		}
	        	}
	            
	        	if(lessThan && file.length() > lessThanSize)
	        		continue;
	        	
	            if(insertIntoHTML) {
	            	tmpBase64 = "data:image/png;base64," + tmpBase64;
	            	//insertHTMLstring(base64string,file.toString());
	            }
	            if(singleFile) {
		            base64string += file.getPath() + eol + "**********" + eol + tmpBase64 + eol + eol;
	            }
	        }
	    }
		if(singleFile)
			return base64string;
		else
			return null;
	}

//************************************ENCODE EACH FILE***********************************//
	private String encodeFile(String infile) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(infile);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        try {
            for (int readNum; (readNum = fis.read(buf)) != -1;) {
                bos.write(buf, 0, readNum);
            }
        } catch (IOException e2) {
        	e2.printStackTrace();
        }
        byte[] bytes = bos.toByteArray();
        
        byte[] base = Base64.encodeBase64(bytes, true);
        String base64string = new String(base);
        return base64string;
	}

//*****************************WRITE BASE 64 TO FILE*************************************//
	private void writeBase64ToFile(String base64string, String filename) {
		File dir = new File("base64");
		if(!dir.exists())
			dir.mkdir();
		
        FileWriter outFileWriter = null;
		try {
			outFileWriter = new FileWriter("base64" + fs + filename + ".txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
        PrintWriter out = new PrintWriter(outFileWriter);
        out.write(base64string);
		
		out.close();
	}

//************************************PRINT HELP*****************************************//
	private void printHelp() {
	      System.out.println ("Command line parameters: input [options]");
	      System.out.println ("Options are:");
	      System.out.println ("\t-h\t\tinsert images found into html");
	      System.out.println ("\t-s\t\twrite all base 64 conversions to a single file");
	      System.out.println ("\t-lt <filesize>\tset a maximum filesize to convert in KB");
	}
}