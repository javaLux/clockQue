package myjfxprojects.sciFiDigitalClock.common;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import myjfxprojects.sciFiDigitalClock.customTooltip.NoResultTooltip;

public class UndecoratedWindow {
	/**
	 * Instanzvariablen Koordinaten der stage / des Mauszeigers: mouseDragDeltaX
	 * mouseDragDeltaY
	 */
	private double mouseDragDeltaX = 0;
	private double mouseDragDeltaY = 0;
	
	// instance of noResultTooltip to hide them if primary stage is moving on desktop
//	private NoResultTooltip noResult = NoResultTooltip.getInstance();

	/**
	 * Der Methode wird das node (Layoutmanager) und die stage übergeben die per
	 * Mausklick bewegt werden soll.
	 *
	 * @param node  -> z.B. ein Layoutmanager vom Typ HBox
	 * @param stage -> die stage (Hauptbühne)
	 */
	public void allowDragAndDrop(Node node, Stage stage) {
		
		// get current stage position when mouse pressed on given node
		node.setOnMousePressed(event -> {
			// check if tool tip is visible
			if(NoResultTooltip.getInstance().isShowing()) {
				// hide them
				NoResultTooltip.getInstance().hide();
			}
			// get new stage coordinates
			mouseDragDeltaX = node.getLayoutX() - event.getSceneX();
			mouseDragDeltaY = node.getLayoutY() - event.getSceneY();
		});
		
		// set the new position of the stage
		node.setOnMouseDragged(event -> {
			stage.setX(event.getScreenX() + mouseDragDeltaX);
			stage.setY(event.getScreenY() + mouseDragDeltaY);
		});
	}

	/**
	 * Methode verschiebt die stage auf dem desktop über einen Button. Dabei wird
	 * das MousePressedEvent auf dem übergeben Button aufgerufen, die X und Y
	 * koordinaten werden gespeichert und beim los lassen der Maustaste werden die
	 * jeweilgen Koordinaten an die stage übergeben.
	 * 
	 * @param stage -> Hauptfenster / Bühne
	 * @param btn   -> Button der zum verschieben der Stage genutzt werden soll
	 */
	public void allowDragOnButton(Button btn, Stage stage) {
		btn.setOnMousePressed(e -> {
			mouseDragDeltaX = btn.getLayoutX() - e.getSceneX();
			mouseDragDeltaY = btn.getLayoutY() - e.getSceneY();
		});

		btn.setOnMouseDragged(e -> {
			stage.setX(e.getScreenX() + mouseDragDeltaX);
			stage.setY(e.getScreenY() + mouseDragDeltaY);
		});
	}
}

