import ij.*;
import ij.io.OpenDialog;
import ij.macro.Interpreter;
import ij.plugin.*;
import java.io.*;

import org.apache.commons.io.FileUtils;

public class BIOP_LibInstaller implements PlugIn {
	private final int maxreturn = 3;
	
	
	public static void installLibrary(String libPath) {
		// Get Macros path
		
		// libPath could be just a filename?
		
		File f = new File(libPath);
		String libName = f.getName().substring(0, f.getName().length()-4);

		if (f.exists()) {
			
			try {
				String libFunctions = FileUtils.readFileToString(f);
				// Make sure we append the name of the library somewhere
				String IdStart = "// From: "+libName+" START \n\r";
				String IdEnd = "/n/r// From: "+libName+" END";
				libFunctions = IdStart+libFunctions+IdEnd;				
				   String oldFunctions = Interpreter.getAdditionalFunctions();
				   //IJ.log(oldFunctions);
				   if (oldFunctions != null) {
					   if (oldFunctions.indexOf(IdStart) == -1) {
						   IJ.log("There is another existing Library. Appending "+libName+" Library.");
						   
					   } else {
						   IJ.log(libName+" Library already installed. Reloading...");
						   //cut out the old one
						   int startInd = oldFunctions.indexOf(IdStart);
						   int endInd   = oldFunctions.indexOf(IdEnd)+IdEnd.length();
						   
						   oldFunctions = oldFunctions.substring(startInd, startInd)+oldFunctions.substring(endInd, oldFunctions.length());
						   //IJ.log(oldFunctions);
					   }

					   Interpreter.setAdditionalFunctions(oldFunctions+libFunctions);

				   } else {
					   IJ.log("Empty Library. Adding "+libName+" Library.");

					   Interpreter.setAdditionalFunctions(libFunctions);
				   }
			} catch (Exception e) {
				IJ.error(e.getMessage());
			}
		} else { 
			// Check that the file has the complete path
			IJ.showMessage(libName+" not found. in "+libPath+".\n"+ "Make sure you placed "+libName+" in the right place." );
		}
	}

	private int maxReturns(int ind) {
		return ( (ind-maxreturn-1) > 0 ) ? ind-maxreturn-1 : 0;
	}
	public void run(String what) {
		if (what.equals("Show Functions")) {
			IJ.log("Displaying all available functions from the macro interpreter.");
			// Display all functions available by parsing the string from Interpreter.
			String[] allFun = Interpreter.getAdditionalFunctions().split("\n");
			
			for (int i=0;i<allFun.length; i++) {
				if (allFun[i].startsWith("function")) {
					IJ.log(""+allFun[i].substring(9, allFun[i].indexOf("{")).trim());
					
					// Now go back and grab everything that starts with //
					String comments = "";
					for (int j=i-1; j>maxReturns(i); j--) {
						if(allFun[j].startsWith("//")) {
							comments = "          "+allFun[j].substring(2).trim()+"\n"+ comments;
						}
					}
					IJ.log(comments);
				}
			}
		} else {
				
				OpenDialog od= new OpenDialog("Choose the file with your macros");
				String fileName = od.getFileName();
				String fileDir = od.getDirectory();
				what = fileDir+fileName;
				installLibrary(what);

		}
			
		
	}

}
