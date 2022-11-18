package myjfxprojects.sciFiDigitalClock.common;

public class AccessToken {
    public String apikey;

    public AccessToken(String key) {
        this.apikey = key;
    }

    public AccessToken() {}

    @Override
    public String toString() {
        return "AccessToken{" +
                "apikey='" + apikey + '\'' +
                '}';
    }
}
