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

public class DoipSimulationHttpClient {
	private static Logger logger = LogManager.getLogger(DoipSimulationHttpClient.class);

	private final HttpClient httpClient;
	private final String baseUrl;

	private static final String PLATFORM_PATH = "/doip-simulation/platform";
	private static final String DOIP_SIMULATION_PATH = "/doip-simulation";

	public DoipSimulationHttpClient(String baseUrl) {
		this.baseUrl = baseUrl;
		// this.httpClient = new HttpClient("http://localhost:8080");
		this.httpClient = new HttpClient(baseUrl);
	}

	public String getOverview(String status) throws HttpStatusCodeException, HttpInvalidResponseBodyType,
			URISyntaxException, IOException, InterruptedException {
		String url = createGetOverviewUrl(status);
		HttpResponse<String> response = sendGetRequest(url);

		return response.body();
	}

	public DoipHttpServerResponse getOverviewExtended(String status) throws HttpStatusCodeException,
			HttpInvalidResponseBodyType, URISyntaxException, IOException, InterruptedException {
		String url = createGetOverviewUrl(status);
		HttpResponse<String> response = sendGetRequest(url);

		return createExtendedResponse(response, ServerInfo.class);
	}

	public String getPlatform(String platformName) throws HttpStatusCodeException, HttpInvalidResponseBodyType,
			URISyntaxException, IOException, InterruptedException {
		String url = createGetPlatformUrl(platformName);
		HttpResponse<String> response = sendGetRequest(url);

		return response.body();
	}

	public DoipHttpServerResponse getPlatformExtended(String platformName) throws HttpStatusCodeException,
			HttpInvalidResponseBodyType, URISyntaxException, IOException, InterruptedException {
		String url = createGetPlatformUrl(platformName);
		HttpResponse<String> response = sendGetRequest(url);

		return createExtendedResponse(response, Platform.class);
	}

	public String getGateway(String platformName, String gatewayName) throws HttpStatusCodeException,
			HttpInvalidResponseBodyType, URISyntaxException, IOException, InterruptedException {
		String url = createGetGatewayUrl(platformName, gatewayName);
		HttpResponse<String> response = sendGetRequest(url);

		return response.body();
	}

	public DoipHttpServerResponse getGatewayExtended(String platformName, String gatewayName)
			throws HttpStatusCodeException, HttpInvalidResponseBodyType, URISyntaxException, IOException,
			InterruptedException {
		String url = createGetGatewayUrl(platformName, gatewayName);
		HttpResponse<String> response = sendGetRequest(url);

		return createExtendedResponse(response, Gateway.class);
	}

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

	public DoipHttpServerResponse executeActionPostExtended(String platformName, Action action)
			throws HttpStatusCodeException, HttpInvalidRequestBodyType, HttpInvalidResponseBodyType, URISyntaxException,
			IOException, InterruptedException {
		ActionRequest request = new ActionRequest();
		request.setAction(action);
		String json = JsonUtils.serialize(request);

		String url = createGetPlatformUrl(platformName);
		HttpResponse<String> response = sendPostRequest(json, url);

		return createExtendedResponse(response, Platform.class);
	}

	public String executeActionGet(String platformName, Action action) throws HttpStatusCodeException,
			HttpInvalidResponseBodyType, URISyntaxException, IOException, InterruptedException {

		String url = createGetActionUrl(platformName, action);
		HttpResponse<String> response = sendGetRequest(url);

		// logHeaderResponseInfo(response);

		return response.body();
	}

	public DoipHttpServerResponse executeActionGetExtended(String platformName, Action action)
			throws HttpStatusCodeException, HttpInvalidResponseBodyType, URISyntaxException, IOException,
			InterruptedException {

		String url = createGetActionUrl(platformName, action);
		HttpResponse<String> response = sendGetRequest(url);

		return createExtendedResponse(response, Platform.class);
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
