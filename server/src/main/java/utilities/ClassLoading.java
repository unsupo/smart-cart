package utilities;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class ClassLoading {
	public static void main(String[] args) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		
		String path = "C:\\Users\\jarndt\\Documents\\qa-automation\\qa-automation-dist\\target\\qa-automation-dist-0.0.3-SNAPSHOT\\qa-automation\\config";
		String name = "InternetSearch.WolframAlphaTag";
//		try{
//			new ClassLoading().load(path, name).execute(new Element("A").setText("1+2+x"), null);
//		}catch(NoClassDefFoundError ncdfe){
//			new ClassLoading().load(path, ncdfe.getMessage().replace("/", ".")).execute(new Element("A").setText("1+2+x"), null);
//			new ClassLoading().load(path, name).execute(new Element("A").setText("1+2+x"), null);
//		}
	}

	
	
	public Object load(String path, String className)
			throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, MalformedURLException, ClassNotFoundException {
		// Create a File object on the root of the directory containing the
		// class file
		// String path = "C:\\Users\\jarndt\\Documents\\classTesting";

		File file = new File(path);
		Class cls = null;
		// Convert File to a URL
		URL url = file.toURL(); // file:/c:/myclasses/
		URL[] urls = new URL[] { url };

		// Create a new class loader with the directory
		ClassLoader cl = new URLClassLoader(urls);

		// Load in the class; MyClass.class should be located in
		// // the directory file:/c:/myclasses/com/mycompany
		// Class cls = cl.loadClass("com.mycompany.MyClass");

		cls = cl.loadClass(className);
		return  cls.getConstructor().newInstance();
	}
	
	public Object getClassInstance(String className)
			throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException{
		// Create a File object on the root of the directory containing the
		// class file
		// String path = "C:\\Users\\jarndt\\Documents\\classTesting";

		Class cls = null;
		try {
			cls = Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("No Class: "+className);
		}
		return cls.getConstructor().newInstance();
	}
}
