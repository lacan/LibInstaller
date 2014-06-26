// Installing functions
// Here it has a dialog and you can point it to the BIOPLib.txt file

run("Install Functions");

// viewing the functions
run("Show Functions");


print("///////////////////");

//Using these functions
// In our case we need to define a function that gives the name for a parameter window first
function toolName() {
	return "My new tool";
}


// These will only run if you relaunch the macro... Hopefully this can be solved
// Otherwise just make sure that Install Functions is run on startup, for example.
//setting a variable
setData("Some key", 12);

setData("Thr Method", "Otsu");

// Recovering data

method = getData("Thr Method");

showMessage("My method is: "+method);