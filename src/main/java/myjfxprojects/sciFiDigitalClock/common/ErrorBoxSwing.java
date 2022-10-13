package myjfxprojects.sciFiDigitalClock.common;

import java.awt.Window;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.stream.Stream;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Swing Klasse die eine Error MsgBox erzeugt zum anzeigen eines StackTrace
 * einer Exception
 * 
 * WICHTIG: Diese Klasse ist durch das Swing Framework unabhängig zum JavaFX
 * Framework und kann somit in einer JavaFX Applikation auch verwendet werden,
 * wenn noch kein JavaFX Application Thread existiert!!!
 * 
 * @author christianschmidt
 *
 */
public final class ErrorBoxSwing {

	// root node
	final static JFrame root = new JFrame();

	// Textarea für die MsgBox erzeugen
	final static JTextArea textArea = new JTextArea();

	// boolean wenn die MsgBox beendet wurde
	static boolean msgBoxClosed = false;

	// statischer Initialisierungsblock
	static {
		// standard verhalten bei einem Close Event des Fensters festlegen
		// -> bei scließen der MsgBox wird das root node geschlossen
		root.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	// constructor is not visible
	private ErrorBoxSwing() {};
	
	/**
	 * Methode zum anzeigen des StackTrace der übergeben Exception
	 * 
	 * @param currentEx -> Exception die geworfen wurde
	 * @param titleBar  -> Fenster Title
	 * @param icon		-> falls ein anderes Icon als das Error Icon gewollt ist
	 * 
	 */
	public static boolean showExceptionStacktrace(Exception currentEx, String titleBar, ImageIcon icon) {

		String titleOfMsgBox = "";

		// sicherstellen das keine NullPointerException geworfen wird falls anstatt
		// eine Strings für den fenster Title ein null an die Methode übergeben wird
		if (titleBar != null) {
			titleOfMsgBox = titleBar;
		}

		// nur wenn eine Exception übergeben wurde den stacktrace in die TextArea
		// einfügen
		// damit eine NullPointerException vermieden wird
		if (currentEx != null) {
			// Create expandable Exception.
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			// gives the current Stacktrace from Exception
			currentEx.printStackTrace(pw);
			// formats current Stacktrace to String object
			String exceptionText = sw.toString();
			// Text in die Textarea setzen -> stacktrace von exception
			textArea.setText(exceptionText);
			textArea.setColumns(30);
			textArea.setRows(10);
			textArea.setLineWrap(false);
			textArea.setWrapStyleWord(false);
			// Text Area is not editable
			textArea.setEditable(false);
			textArea.setAlignmentX(JTextArea.LEFT_ALIGNMENT);
			textArea.setSize(textArea.getPreferredSize().width, textArea.getPreferredSize().height);
		}

		// MsgBox erzeugen -> je nach gedrückten Button (OK = 0 und Close Event = -1)
		// wird der Rückgabewert
		// gespeichert
		int returnValue = JOptionPane.showConfirmDialog(root, new JScrollPane(textArea), titleOfMsgBox,
				JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE, icon);

		// da es nur den OK-Button und das X gibt wird hier auf beide Events eingegangen
		if (returnValue == JOptionPane.OK_OPTION || returnValue == JOptionPane.CLOSED_OPTION) {
			// die MsgBox soll nur zum anzeigen dienen und sie muss behandelt werden
			// darum kann der user nur den OK-Button oder das X zum schließen des Fensters
			// drücken
			// in beiden Fällen wird der boolean auf true gesetz
			msgBoxClosed = true;
		}

		// alle Fenster schließen und somit den EventDispatch Thread von Swing beenden
		Stream.of(Window.getWindows()).forEach(Window::dispose);

		return msgBoxClosed;

	}

	/**
	 * Methode zum anzeigen einer "normalen" ErrorMsgBox ohne Stacktrace
	 * 
	 * @param titleBar	->	Fenster Title
	 * @param errorMsg	->	Nachricht für den User (Fehlerbeschreibung)
	 * @param icon		->	falls ein anderes Icon gewollt ist
	 * @return			-> 	true wenn MsgBox geschlossen wird
	 */
	public static boolean showErrorMessage(String titleBar, String errorMsg, ImageIcon icon) {
		
		String titleOfMsgBox = "";
		String messageForUser = "";

		if (titleBar != null) {
			titleOfMsgBox = titleBar;
		}

		if (errorMsg != null) {
			messageForUser = errorMsg;
		}

		// MsgBox erzeugen -> je nach gedrückten Button (OK = 0 und Close Event = -1)
		// wird der Rückgabewert
		// gespeichert
		int returnValue = JOptionPane.showConfirmDialog(root, messageForUser, titleOfMsgBox, 
				JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE, icon);

		// da es nur den OK-Button und das X gibt wird hier auf beide Events eingegangen
		if (returnValue == JOptionPane.OK_OPTION || returnValue == JOptionPane.CLOSED_OPTION) {
			// die MsgBox soll nur zum anzeigen dienen und sie muss behandelt werden
			// darum kann der user nur den OK-Button oder das X zum schließen des Fensters
			// drücken
			// in beiden Fällen wird der boolean auf true gesetz
			msgBoxClosed = true;
		}

		// alle Fenster schließen und somit den EventDispatch Thread von Swing beenden
		Stream.of(Window.getWindows()).forEach(Window::dispose);

		return msgBoxClosed;

	}

}
