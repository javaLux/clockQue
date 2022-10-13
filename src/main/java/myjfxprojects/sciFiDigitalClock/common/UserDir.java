package myjfxprojects.sciFiDigitalClock.common;

import java.nio.file.FileSystems;
import java.nio.file.Path;

public class UserDir {
	
	/**
	 * Konstruktor ist private -> somit nicht sichtbar nach außen
	 */
	private UserDir() {}
	
	/**
	 * statische Methode zum ermitteln des User Verzeichnisses
	 * @return	->	Path Objekt of user dir
	 */
	public static Path getUserDir() {
		String userDir = System.getProperty("user.home");
		Path p = FileSystems.getDefault().getPath(userDir);
		return p;
	}
}
