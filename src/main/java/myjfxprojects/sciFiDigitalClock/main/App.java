package myjfxprojects.sciFiDigitalClock.main;

import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import myjfxprojects.sciFiDigitalClock.business.*;
import myjfxprojects.sciFiDigitalClock.common.*;
import myjfxprojects.sciFiDigitalClock.customTooltip.TimeZoneTooltip;
import myjfxprojects.sciFiDigitalClock.database.DbDirectory;
import myjfxprojects.sciFiDigitalClock.database.DbHandling;
import myjfxprojects.sciFiDigitalClock.location.CustomComboBox;
import myjfxprojects.sciFiDigitalClock.location.LocationListViewApp;
import org.slf4j.Logger;

import javax.swing.*;

/**
 * JavaFX APP
 */
public class App extends Application {

	// initialize data bean singleton
	private final DataBean dataBean = DataBean.getInstance();

	private final String pathToWheelChairIcon = "resources/images/wheelChair.png";

	// root layout manager
	private Parent root = null;

	@Override
	public void init() {

		// get a Thread safe instance of application logger
		Logger LOGGER = ApplicationLogger.getAppLogger();

		// create (if not exists) the database directory
		if (! DbDirectory.createDbDirectory()) {
			// if failed to create database dir -> show error msg box and shutdown
			// application
			ErrorBoxSwing.showErrorMessage(DataBean.APP_NAME + " - Launch failed",
					"Failed to create the database directory\n'" + DbDirectory.DB_DIR.getAbsolutePath() + "'"
							+ "\nApplication will closed. For more information's, see the log file.",
					new ImageIcon(pathToWheelChairIcon));

			System.exit(1);
		}

		// initialize a new API instance to speak with the SQLite database and store
		// them in data bean
		this.dataBean.setJooqDbApi(new DbHandling());

		// if the connection to the database failed -> show error msg box and shut down
		// application
		if (! this.dataBean.getJooqDbApi().establishDbConnection()) {
			// if failed to create database dir -> show error msg box and shutdown
			// application
			ErrorBoxSwing.showErrorMessage(DataBean.APP_NAME + " - Launch failed",
					"Failed to established a connection to the database\n"
							+ "Application will closed. For more information's, see the log file.",
					new ImageIcon(pathToWheelChairIcon));

			System.exit(1);

		} else {
			// load all available locations from database into the data bean
			this.dataBean.getJooqDbApi().getAvailableLocations();

			// load user settings from database
			this.dataBean.getJooqDbApi().getApplicationSettings();
		}

		// initialize and start ScheduledService for check Internet connection at
		// runtime
		// every 10 seconds
		InternetConn.SCHEDULED_SERVICE_INTERNET_CONN.setPeriod(Duration.seconds(10));

		try {

			InternetConn.SCHEDULED_SERVICE_INTERNET_CONN.start();

		} catch (Exception ex) {
			// handle exception from executing this service
			LOGGER.error("Failed to execute the scheduled service 'SCHEDULED_SERVICE_INTERNET_CONN' ", ex);

			ErrorBoxSwing.showErrorMessage(DataBean.APP_NAME + " - Launch failed",
					"Failed to execute the scheduled service to check the Internet connectivity\n"
							+ "Application will closed. For more information's, see the log file.",
					new ImageIcon(pathToWheelChairIcon));

			System.exit(1);
		}

		// Loader for config file
		LoadToken loader = new LoadToken();

		if(loader.isConfigFileExists()) {
			// get the API key and store them in data bean
			this.dataBean.setApiKey(loader.getApiKey());

		} else {

			try {
				// create new config file in resource folder
				loader.createConfigFile();
			} catch (Exception ex) {
				LOGGER.error("Failed to create config.yml file in resources folder. ", ex);

				ErrorBoxSwing.showErrorMessage(DataBean.APP_NAME + " - Launch failed",
						"Can not create config file.\n"
								+ "Application will closed. For more information's, see the log file.",
						new ImageIcon(pathToWheelChairIcon));
				System.exit(1);
			}
		}
	}

	@Override
	public void start(Stage primaryStage) {

		// FXML Util class object
		FxmlUtil fxmlUtil = new FxmlUtil();

		// load FXML file to root layout container with self-made
		// FxmlUtil class
		this.root = fxmlUtil.loadFxmlFile("/fxml/ClockView.fxml");

		// if FXML file successfully loaded -> set in scene
		if (this.root != null) {

			// if it was possible to get locations from database than run the APP
			// e.g. the database has no location entries than give user info and stop the APP
			if (! this.dataBean.getUserLocationsSet().isEmpty()) {

				// initialize and save application class for the location list view in data bean
				this.dataBean.setLocationListViewApp(new LocationListViewApp());

				// initialize and save application class for the settings view in data bean
				this.dataBean.setSettingsViewApp(new SettingsViewApp());

				// save primary stage in model
				this.dataBean.setPrimaryStage(primaryStage);

				// get digital clock instance
				ControlDigitalClock digitalClock = new ControlDigitalClock();

				// get hour hand instance
				ControlHourHand hourHand = new ControlHourHand();

				// get minute hand instance
				ControlMinuteHand minuteHand = new ControlMinuteHand();

				Scene scene = new Scene(root);

				// load specific Style sheet file
				scene.getStylesheets().add(this.getClass().getResource("/css/mainView.css").toExternalForm());

				primaryStage.setScene(scene);

				// set the undecorated primary stage -> this means without frame
				primaryStage.initStyle(StageStyle.TRANSPARENT);
				scene.setFill(Color.TRANSPARENT);
				// set icon for dock and main window
				primaryStage.getIcons().add(new Image("images/clock.png"));
				// set window name
				primaryStage.setTitle(DataBean.APP_NAME);

				// IMPORTANT: replace the main window to the last x and y values from database
				primaryStage.setX(DataBean.current_X_Pos_MainWindow);
				primaryStage.setY(DataBean.current_Y_Pos_MainWindow);

				// dependent on database value -> maximize or minimize VBox middle (with Combo
				// Box etc.)
				if (DataBean.isVBoxMiddleMinimize) {
					ManageVBoxMiddle.minimizeVBoxMiddle();
				} else {
					ManageVBoxMiddle.maximizeVBoxMiddle();
				}

				primaryStage.show();
				primaryStage.setResizable(false);

				// Add InvalidationListener to primary stage -> listens to x and y position on
				// screen
				primaryStage.xProperty().addListener(new primaryStagePosListener());
				primaryStage.yProperty().addListener(new primaryStagePosListener());

				// set once current date information
				this.dataBean.getDigitalClockFXMLcontroller()
						.setLblWeekDay(ControlDateInfos.getInstance().getCurrentDayOfWeek());
				this.dataBean.getDigitalClockFXMLcontroller()
						.setLblMonth(ControlDateInfos.getInstance().getCurrentMonth());
				this.dataBean.getDigitalClockFXMLcontroller()
						.setLblMonthDay(ControlDateInfos.getInstance().getCurrentDayOfMonth());

				// first after showing set digital clock on current time
				digitalClock.setDigitalClockOnStartUp();

				// set once the hour hand on current hour
				hourHand.setHourHandOnStartUp();

				// set once the minute hand on current minute
				minuteHand.setMinuteHandOnStartUp();

				// allow mouse drag&drop on the main window without frame
				UndecoratedWindow undecoratedWindow = new UndecoratedWindow();
				undecoratedWindow.allowDragAndDrop(root, primaryStage);

				// initialize the custom time zone tool tip for the digital clock
				// the constructor of this tool tip will be stored the instance in the data bean
				new TimeZoneTooltip();

				// start animation of digital clock, hour and minute hand
				digitalClock.startDigitalClockAnimation();
				hourHand.startHourHandAnimation();
				minuteHand.startMinuteHandAnimation();

				// initialize thread safe instance of weather object and start fetching weather
				// data
				Weather.getInstance().startPeriodicallyFetchWeatherData();

				// initialize custom combo-box for locations objects
				//
				// save instance in data bean
				this.dataBean.setCustomComboBox(new CustomComboBox());

				// Add event filter to handle close event for the primary stage
				// IMPORTANT: if main window will be closed from task bar -> than close the
				// complete application with all opened windows
				primaryStage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, event -> {

					
				});
			}
			// show error message box if no locations couldn't read from database
			else {
				ErrorBoxSwing.showErrorMessage(DataBean.APP_NAME + " - Launch failed",
						"No data could be read from the database. The database may be damaged or corrupt.\n"
						+ "Please manually delete the database file (" + DbDirectory.DB_DIR.getAbsolutePath() + ")"
								+ "\nand start the application again.",
						new ImageIcon(pathToWheelChairIcon));

				System.exit(1);
			}
		}
	}

	/**
	 * Method writes the current application settings in the SQLite database.
	 */
	@Override
	public void stop() {

		this.dataBean.getJooqDbApi().setApplicationSettings();
		this.dataBean.getJooqDbApi().closeDbConnection();
	}

	public static void main(String[] args) {
		launch(args);
	}

	// this InvalidationListener listens to the x and y position of a stage on
	// screen
	// if stage is moved (drag&drop) on desktop -> you can get the changed x and y
	// position
	class primaryStagePosListener implements InvalidationListener {

		@Override
		public void invalidated(Observable observable) {

			// save current position of main window (stage) in model if this is changing
			DataBean.current_X_Pos_MainWindow = dataBean.getPrimaryStage().getX();
			DataBean.current_Y_Pos_MainWindow = dataBean.getPrimaryStage().getY();

			// replace the list view stage if primary stage will be moved on desktop
			if ((dataBean.getListViewStage() != null) && (dataBean.getListViewStage().isShowing())) {

				dataBean.getListViewStage().getScene().getWindow().setX(DataBean.current_X_Pos_MainWindow);
				dataBean.getListViewStage().getScene().getWindow()
						.setY(DataBean.current_Y_Pos_MainWindow + dataBean.getPrimaryStage().getHeight());
			}

			// replace the settings view stage if primary stage will move on desktop
			if ((dataBean.getSettingsViewStage() != null) && (dataBean.getSettingsViewStage().isShowing())) {

				dataBean.getSettingsViewStage().getScene().getWindow().setX(DataBean.current_X_Pos_MainWindow);
				dataBean.getSettingsViewStage().getScene().getWindow()
						.setY(DataBean.current_Y_Pos_MainWindow + dataBean.getPrimaryStage().getHeight());
			}
		}
	}

}