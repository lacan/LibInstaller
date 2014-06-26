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
		String homePath = Prefs.getHomeDir();
//		String libPath = homePath+File.separator+"macros"+File.separator;
		
		//IJ.log(libPath+libName);
		File f = new File(libPath);
		String libName = f.getName().substring(0, f.getName().length()-4);

		if (!f.exists()) {
			//Need to download from Update Site
			IJ.showMessage(libName+" not found. in "+libPath+".\n"+ "Make sure you placed "+libName+" in the right place." );
		} else {
			try {
				String libFunctions = FileUtils.readFileToString(f);
				// Make sure we append the name of the library somewhere
				libFunctions = "// From file: "+libName+"\n\r"+libFunctions;
				   String oldFunctions = Interpreter.getAdditionalFunctions();
				   //IJ.log(oldFunctions);
				   if (oldFunctions != null) {
					   if (oldFunctions.indexOf(libName) == -1) {
						   IJ.log("Existing Library. Appending "+libName+" Library.");
						   Interpreter.setAdditionalFunctions(oldFunctions+libFunctions);
						   
					   } else {
						   IJ.log(libName+" Library already installed.");
					   }
				   } else {
					   IJ.log("Empty Library. Adding "+libName+" Library.");

					   Interpreter.setAdditionalFunctions(libFunctions);
				   }
			} catch (Exception e) {
				IJ.error(e.getMessage());
			}
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
