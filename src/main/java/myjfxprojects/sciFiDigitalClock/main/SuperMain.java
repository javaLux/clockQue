package myjfxprojects.sciFiDigitalClock.main;

public class SuperMain {
	
	public static void main(String[] args) {
		// IMPORTANT:	set JVM option to display the
		//				location names in correct UTF-8 encoding
		//				without this it can be possible that some chars
		//				not correctly encoded
		// 				Option is for execute the JAR file, not the EXE file.
		System.setProperty("file.encoding", "UTF-8");
		App.main(args);
	}
}
