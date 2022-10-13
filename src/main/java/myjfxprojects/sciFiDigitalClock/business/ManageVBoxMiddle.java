/**
 * 
 */
package myjfxprojects.sciFiDigitalClock.business;

import myjfxprojects.sciFiDigitalClock.common.DataBean;

/**
 * @author Christian
 *
 *         Class managed the size of the VBox(middle) with Combo Box and date
 *         info's.
 */
public class ManageVBoxMiddle {

	// private constructor
	private ManageVBoxMiddle() {}

	
	/**
	 * Minimize the VBox middle with Combo Box and date info's
	 */
	public static void minimizeVBoxMiddle() {
		
		// set state of VBox Middle in data bean
		DataBean.isVBoxMiddleMinimize = true;
		
		DataBean.getInstance().getDigitalClockFXMLcontroller().getBtnResizeVboxMiddle().setGraphic(DataBean.getInstance().getImgViewArrowBtnRight());

		DataBean.getInstance().getDigitalClockFXMLcontroller().getVBoxMiddle().setVisible(false);
		DataBean.getInstance().getDigitalClockFXMLcontroller().getVBoxMiddle().setPrefWidth(DataBean.minimizeWidthVboxMiddle);
		DataBean.getInstance().getDigitalClockFXMLcontroller().getRootHBox().setPrefWidth(DataBean.minimizeWidthMainWindow);
		DataBean.getInstance().getPrimaryStage().setWidth(DataBean.minimizeWidthMainWindow);	
	}
	
	/**
	 * Maximize the VBox middle with Combo Box and date info's
	 */
	public static void maximizeVBoxMiddle() {
		
		// set state of VBox Middle in data bean
		DataBean.isVBoxMiddleMinimize = false;
		
		DataBean.getInstance().getDigitalClockFXMLcontroller().getBtnResizeVboxMiddle().setGraphic(DataBean.getInstance().getImgViewArrowBtnLeft());
		
		DataBean.getInstance().getPrimaryStage().setWidth(DataBean.defaultWidthMainWindow);
		DataBean.getInstance().getDigitalClockFXMLcontroller().getRootHBox().setPrefWidth(DataBean.defaultWidthMainWindow);
		DataBean.getInstance().getDigitalClockFXMLcontroller().getVBoxMiddle().setPrefWidth(DataBean.defaultWidthVboxMiddle);
		DataBean.getInstance().getDigitalClockFXMLcontroller().getVBoxMiddle().setVisible(true);		
	}
}
