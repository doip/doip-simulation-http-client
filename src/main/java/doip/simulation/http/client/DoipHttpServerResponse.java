package doip.simulation.http.client;

public class DoipHttpServerResponse {
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

}
