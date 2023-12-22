package doip.simulation.http.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DoipHttpServerResponse {
	private static Logger logger = LogManager.getLogger(DoipHttpServerResponse.class);
	private int statusCode;
    private Object result;
    private String responseBody;

    public DoipHttpServerResponse(int statusCode, Object result, String responseBody) {
        this.statusCode = statusCode;
        this.result = result;
        this.responseBody = responseBody;
    }

	public int getStatusCode() {
		return statusCode;
	}

	public Object getResult() {
		return result;
	}

	public String getResponseBody() {
		return responseBody;
	}
	
	/**
	 * Gets the result of the HTTP server response.
	 * 
	 * @return The result object. It can be of any type.
	 */
	public <U> U getResultAs(Class<U> expectedClass) {
        if (result != null && expectedClass.isInstance(result)) {
        	logger.info("Received response result is an instance of {}", expectedClass.getSimpleName());
            return expectedClass.cast(result);
        } else {
            //throw new ClassCastException("Cannot cast result to the specified class");
        	logger.warn("Cannot cast result to the specified class : "+ expectedClass.getSimpleName());
        	return null;
        }
    }

}
