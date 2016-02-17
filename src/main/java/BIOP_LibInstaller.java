import ij.*;
import ij.io.OpenDialog;
import ij.macro.Interpreter;
import ij.plugin.*;
import java.io.*;
import java.util.Scanner;

public class BIOP_LibInstaller implements PlugIn {

	
	public void run(String what) {
		if (what.equals("Show Functions")) {

			showAvaialableFunctions();

		} else {
				
				OpenDialog od= new OpenDialog("Choose the file with your macros");
				String fileName = od.getFileName();
				String fileDir = od.getDirectory();
				what = fileDir+fileName;
				installLibrary(what);

		}
			
		
	}

	public static void installLibrary(String libPath) {
		// Get Macros path
		
		// libPath could be just a filename?
		
		File f = new File(libPath);
		String fullPath = f.getAbsolutePath();
		String libName = f.getName().substring(0, f.getName().length()-4);
		
		if (!f.isAbsolute()) {
			IJ.log("Relative Path, adding Path to Fiji");
			// Make path absolute by appending the Fiji/ImageJ root to it.
			String fijiPath = Menus.getPlugInsPath();
			libPath = fijiPath+libPath;
			f = new File(libPath);
			fullPath = f.getAbsolutePath();

		}
			
		IJ.log("Full path to "+libName+" :"+fullPath);

		if (f.exists()) {
			
			try {
				Scanner sc = new Scanner(f);
				 String libFunctions = sc.useDelimiter("\\Z").next();
				 sc.close();
				// Make sure we append the name of the library somewhere
				String IdStart = "// From: "+libName+" START \n\r";
				String IdEnd = "\n\r// From: "+libName+" END";
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
	
	public static void showAvaialableFunctions() {
		
		IJ.log("Displaying all available functions from the macro interpreter.");
		// Display all functions available by parsing the string from Interpreter.
		String[] allFun = Interpreter.getAdditionalFunctions().split("\n");
		
		for (int i=0;i<allFun.length; i++) {
			if (allFun[i].trim().startsWith("// From:")) {
				IJ.log("---------- From "+allFun[i].trim().substring(8,allFun[i].trim().indexOf("START")).trim()+" Library ----------");
			}
			if (allFun[i].trim().startsWith("function")) {
				IJ.log(""+allFun[i].substring(9, allFun[i].indexOf("{")).trim());
				
				// Now go back and grab everything that starts with */
				String comments = "";
				int j=0;
				if(allFun[i-1].trim().startsWith("*/")) {
					// Go back till you get a /*
					j=i-2;
					
					int ind = j;
					while (j>0) {
						if (allFun[j].trim().startsWith("/*")) {
							ind = j;
							j=0;
						} else {
							j--;
						}
					}
					
					for (int k=i-2; k>=ind+1; k--) {
						comments = "        "+allFun[k].substring(2).trim()+"\n"+ comments +"\r";
					}
					
					IJ.log(comments);
				}
			}
		}
		
	}

}
