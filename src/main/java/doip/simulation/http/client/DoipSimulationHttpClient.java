package doip.simulation.http.client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.starcode88.http.HttpClient;
import com.starcode88.http.exception.HttpInvalidRequestBodyType;
import com.starcode88.http.exception.HttpInvalidResponseBodyType;
import com.starcode88.http.exception.HttpStatusCodeException;
import doip.simulation.http.lib.*;

import doip.simulation.http.lib.Action;
import doip.simulation.http.lib.utils.JsonUtils;

/**
 * HTTP client for interacting with the DoipSimulation server.
 */
public class DoipSimulationHttpClient {
	private static Logger logger = LogManager.getLogger(DoipSimulationHttpClient.class);

	private final HttpClient httpClient;
	private final String baseUrl;

	private static final String PLATFORM_PATH = "/doip-simulation/platform";
	private static final String DOIP_SIMULATION_PATH = "/doip-simulation";

	/**
	 * Constructor for initializing the HTTP client with a base URL.
	 *
	 * @param baseUrl The base URL of the DoipSimulation server.
	 */
	public DoipSimulationHttpClient(String baseUrl) {
		this.baseUrl = baseUrl;
		// this.httpClient = new HttpClient("http://localhost:8080");
		this.httpClient = new HttpClient(baseUrl);
	}

	/**
	 * Retrieves the overview from the server based on the specified status.
	 *
	 * @param status The status parameter for filtering the overview.
	 * @return The response body as a String.
	 * @throws HttpStatusCodeException     If the HTTP status code indicates an
	 *                                     error.
	 * @throws HttpInvalidResponseBodyType If the HTTP response body type is
	 *                                     invalid.
	 * @throws URISyntaxException          If there is an issue with the URI syntax.
	 * @throws IOException                 If an I/O error occurs.
	 * @throws InterruptedException        If the operation is interrupted.
	 */
	public String getOverview(String status) throws HttpStatusCodeException, HttpInvalidResponseBodyType,
			URISyntaxException, IOException, InterruptedException {
		String url = createGetOverviewUrl(status);
		HttpResponse<String> response = sendGetRequest(url);

		return response.body();
	}

	/**
	 * Retrieves the extended overview from the server based on the specified
	 * status.
	 *
	 * @param status The status parameter for filtering the overview.
	 * @return The extended server response.
	 * @throws HttpInvalidResponseBodyType If the HTTP response body type is
	 *                                     invalid.
	 * @throws URISyntaxException          If there is an issue with the URI syntax.
	 * @throws IOException                 If an I/O error occurs.
	 * @throws InterruptedException        If the operation is interrupted.
	 */
	public DoipHttpServerResponse getOverviewExtended(String status)
			throws HttpInvalidResponseBodyType, URISyntaxException, IOException, InterruptedException {
		String url = createGetOverviewUrl(status);

		try {
			HttpResponse<String> response = sendGetRequest(url);
			return createExtendedResponse(response, ServerInfo.class);
		} catch (HttpStatusCodeException e) {
			return new DoipHttpServerResponse(e.getResponse().statusCode(), null,
					String.valueOf(e.getResponse().body()));
		}

	}

	/**
	 * Retrieves a platform from the server by name.
	 *
	 * @param platformName The name of the platform.
	 * @return The response body as a String.
	 */
	public String getPlatform(String platformName) throws HttpStatusCodeException, HttpInvalidResponseBodyType,
			URISyntaxException, IOException, InterruptedException {
		String url = createGetPlatformUrl(platformName);
		HttpResponse<String> response = sendGetRequest(url);

		return response.body();
	}

	/**
	 * Retrieves the extended overview of platform from the server by name.
	 *
	 * @param platformName The name of the platform.
	 * @return The extended server response.
	 */
	public DoipHttpServerResponse getPlatformExtended(String platformName)
			throws HttpInvalidResponseBodyType, URISyntaxException, IOException, InterruptedException {
		String url = createGetPlatformUrl(platformName);

		try {
			HttpResponse<String> response = sendGetRequest(url);
			return createExtendedResponse(response, Platform.class);
		} catch (HttpStatusCodeException e) {
			return new DoipHttpServerResponse(e.getResponse().statusCode(), null,
					String.valueOf(e.getResponse().body()));
		}

	}

	/**
	 * Retrieve a gateway by name for a specific platform.
	 *
	 * @param platformName The name of the platform.
	 * @param gatewayName  The name of the gateway.
	 * @return The response body as a String.
	 */
	public String getGateway(String platformName, String gatewayName) throws HttpStatusCodeException,
			HttpInvalidResponseBodyType, URISyntaxException, IOException, InterruptedException {
		String url = createGetGatewayUrl(platformName, gatewayName);
		HttpResponse<String> response = sendGetRequest(url);

		return response.body();
	}

	/**
	 * Retrieve the extended overview of gateway by name for a specific platform.
	 *
	 * @param platformName The name of the platform.
	 * @param gatewayName  The name of the gateway.
	 * @return The extended server response.
	 */
	public DoipHttpServerResponse getGatewayExtended(String platformName, String gatewayName)
			throws HttpInvalidResponseBodyType, URISyntaxException, IOException, InterruptedException {
		String url = createGetGatewayUrl(platformName, gatewayName);

		try {
			HttpResponse<String> response = sendGetRequest(url);
			return createExtendedResponse(response, Gateway.class);
		} catch (HttpStatusCodeException e) {
			return new DoipHttpServerResponse(e.getResponse().statusCode(), null,
					String.valueOf(e.getResponse().body()));
		}

	}

	/**
	 * Perform the specified action on the given platform.
	 *
	 * @param platform The platform on which the action needs to be performed.
	 * @param action   The action to be performed (start or stop ...).
	 * @return The response body as a String.
	 */
	public String executeActionPost(String platformName, Action action)
			throws HttpStatusCodeException, HttpInvalidRequestBodyType, HttpInvalidResponseBodyType, URISyntaxException,
			IOException, InterruptedException {
		ActionRequest request = new ActionRequest();
		request.setAction(action);
		String json = JsonUtils.serialize(request);

		String url = createGetPlatformUrl(platformName);
		HttpResponse<String> response = sendPostRequest(json, url);

		return response.body();
	}

	/**
	 * Perform the specified action on the given platform.
	 *
	 * @param platform The platform on which the action needs to be performed.
	 * @param action   The action to be performed (start or stop ...).
	 * @return The extended server response.
	 */
	public DoipHttpServerResponse executeActionPostExtended(String platformName, Action action)
			throws HttpInvalidRequestBodyType, HttpInvalidResponseBodyType, URISyntaxException, IOException,
			InterruptedException {
		ActionRequest request = new ActionRequest();
		request.setAction(action);
		String json = JsonUtils.serialize(request);

		String url = createGetPlatformUrl(platformName);

		try {
			HttpResponse<String> response = sendPostRequest(url, json);
			return createExtendedResponse(response, Platform.class);
		} catch (HttpStatusCodeException e) {
			return new DoipHttpServerResponse(e.getResponse().statusCode(), null,
					String.valueOf(e.getResponse().body()));
		}

	}

	/**
	 * Perform the specified action on the given platform.
	 *
	 * @param platform The platform on which the action needs to be performed.
	 * @param action   The action to be performed (start or stop ...).
	 * @return The response body as a String.
	 */
	public String executeActionGet(String platformName, Action action) throws HttpStatusCodeException,
			HttpInvalidResponseBodyType, URISyntaxException, IOException, InterruptedException {

		String url = createGetActionUrl(platformName, action);
		HttpResponse<String> response = sendGetRequest(url);

		// logHeaderResponseInfo(response);

		return response.body();
	}
	
	/**
	 * Perform the specified action on the given platform.
	 *
	 * @param platform The platform on which the action needs to be performed.
	 * @param action   The action to be performed (start or stop ...).
	 * @return The extended server response.
	 */
	public DoipHttpServerResponse executeActionGetExtended(String platformName, Action action)
			throws HttpInvalidResponseBodyType, URISyntaxException, IOException, InterruptedException {

		String url = createGetActionUrl(platformName, action);

		try {
			HttpResponse<String> response = sendGetRequest(url);
			return createExtendedResponse(response, Platform.class);
		} catch (HttpStatusCodeException e) {
			return new DoipHttpServerResponse(e.getResponse().statusCode(), null,
					String.valueOf(e.getResponse().body()));
		}

	}

	private HttpResponse<String> sendGetRequest(String url) throws URISyntaxException, IOException,
			InterruptedException, HttpStatusCodeException, HttpInvalidResponseBodyType {
		logger.info("Sending GET request to: {}", url);
		return httpClient.GET(url, String.class);
	}

	private HttpResponse<String> sendPostRequest(String json, String url) throws URISyntaxException, IOException,
			InterruptedException, HttpStatusCodeException, HttpInvalidRequestBodyType, HttpInvalidResponseBodyType {
		logger.info("Sending POST request to: {}", url);
		return httpClient.POST(url, json, String.class);
	}

	@SuppressWarnings("unused")
	private void logHeaderResponseInfo(HttpResponse<?> response) {
		HttpHeaders headers = response.headers();
		headers.map().forEach((k, v) -> logger.info(k + ":" + v));
	}

	private DoipHttpServerResponse createExtendedResponse(HttpResponse<String> response, Class<?> clazz)
			throws JsonProcessingException {
		Object operationResult = null;
		if (response.statusCode() == HttpURLConnection.HTTP_OK) {
			operationResult = deserializeResponse(response.body(), clazz);
		}
		return new DoipHttpServerResponse(response.statusCode(), operationResult, response.body());
	}

	private <T> T deserializeResponse(String responseBody, Class<T> clazz) throws JsonProcessingException {
		return JsonUtils.deserialize(responseBody, clazz);
	}

	public String createGetOverviewUrl(String status) {
		return DOIP_SIMULATION_PATH + (status != null && !status.isEmpty() ? "?status=" + status : "");
	}

	public String createGetPlatformUrl(String platformName) {
		return PLATFORM_PATH + "/" + platformName;
	}

	public String createGetGatewayUrl(String platformName, String gatewayName) {
		return PLATFORM_PATH + "/" + platformName + "/gateway/" + gatewayName;
	}

	public String createGetActionUrl(String platformName, Action action) {
		return PLATFORM_PATH + "/" + platformName + "?action=" + action.toString();
	}

}
