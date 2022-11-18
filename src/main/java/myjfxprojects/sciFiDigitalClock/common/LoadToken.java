package myjfxprojects.sciFiDigitalClock.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

public class LoadToken {
	// get a Thread safe instance of application logger
	Logger LOGGER = ApplicationLogger.getAppLogger();

	//Path to the config file, dependent on the build artifact
	private final String configFilePath = Paths.get("resources", "config.yml").toAbsolutePath().toString();

	private static File configFile = null;

	public boolean isConfigFileExists() {

		try {
			configFile = new File(configFilePath);
			System.out.println(configFilePath);
		} catch (NullPointerException ex) {
			return false;
		}

		return configFile.exists();
	}
	public void createConfigFile() throws Exception {

		// create new config.yml file
		FileWriter fw = new FileWriter(configFilePath);

		// write the three necessary lines in the config file
		for (int i = 0; i < 3; i++) {
			switch (i) {
				case 0 -> fw.write("# set behind the colon your API key from the https://home.openweathermap.org/\n");
				case 1 -> fw.write("# e.g. apikey: <Token>\n");
				case 2 -> fw.write("apikey: write your api key here\n");
				default -> {
				}
			}
		}
		fw.close();
	}

	public String getApiKey() {

		// token object
		AccessToken token = null;
		// Instantiating new YAML object mapper
		ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

		try {
			token = yamlMapper.readValue(configFile, AccessToken.class);
		} catch (IOException ex) {
			LOGGER.error("Failed to read YAML config file.", ex);
		}

		if(token != null) {
			return token.apikey;
		}
		return "";
	}
}
