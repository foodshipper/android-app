package de.foodshippers.foodship.api;

/**
 * Created by soenke on 21.11.16.
 */
public class ServerErrorThrowable extends Throwable {
    private int responseCode = 0;

    public ServerErrorThrowable(int respCode, String msg) {
        super(msg);
        responseCode = respCode;
    }

    public int getResponseCode() {
        return responseCode;
    }
}
