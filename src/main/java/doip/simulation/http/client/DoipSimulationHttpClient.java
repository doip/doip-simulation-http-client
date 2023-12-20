package doip.simulation.http.client;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
		logger.info("Sending GET request to: {}", url);

		HttpResponse<String> response = httpClient.GET(url, String.class);

		return response.body();
	}

	public String getPlatform(String platformName) throws HttpStatusCodeException, HttpInvalidResponseBodyType,
			URISyntaxException, IOException, InterruptedException {
		String url =createGetPlatformUrl(platformName);
		logger.info("Sending GET request to: {}", url);
		
		HttpResponse<String> response = httpClient.GET(url, String.class);
		
		return response.body();
	}

	public String getGateway(String platformName, String gatewayName) throws HttpStatusCodeException,
			HttpInvalidResponseBodyType, URISyntaxException, IOException, InterruptedException {
		String url = createGetGatewayUrl(platformName, gatewayName);
		logger.info("Sending GET request to: {}", url);
		
		HttpResponse<String> response = httpClient.GET(url, String.class);

		return response.body();
	}

	public String executeActionPost(String platformName, Action action) throws HttpStatusCodeException, HttpInvalidRequestBodyType, HttpInvalidResponseBodyType, URISyntaxException, IOException, InterruptedException {
		ActionRequest request = new ActionRequest();
		request.setAction(action);
		String json = JsonUtils.serialize(request);
		
		String url = createGetPlatformUrl(platformName);
		logger.info("Sending POST request to: {}", url);
		
		HttpResponse<String> response = httpClient.POST(url, json, String.class);

		return response.body();
	}

	public String executeActionGet(String platformName, Action action) throws HttpStatusCodeException, HttpInvalidResponseBodyType, URISyntaxException, IOException, InterruptedException {
		
		String url = createGetActionUrl(platformName, action);
		logger.info("Sending GET request to: {}", url);

		HttpResponse<String> response = httpClient.GET(url, String.class);

		// logHeaderResponseInfo(response);

		return response.body();
	}

	@SuppressWarnings("unused")
	private void logHeaderResponseInfo(HttpResponse<?> response) {
		HttpHeaders headers = response.headers();
		headers.map().forEach((k, v) -> logger.info(k + ":" + v));
	}

	public String createGetOverviewUrl(String status) {
		return baseUrl + DOIP_SIMULATION_PATH + (status != null && !status.isEmpty() ? "?status=" + status : "");
	}

	public String createGetPlatformUrl(String platformName) {
		return baseUrl + PLATFORM_PATH + "/" + platformName;
	}

	public String createGetGatewayUrl(String platformName, String gatewayName) {
		return baseUrl + PLATFORM_PATH + "/" + platformName + "/gateway/" + gatewayName;
	}

	public String createGetActionUrl(String platformName, Action action) {
		return baseUrl + PLATFORM_PATH + "/" + platformName + "?action=" + action.toString();
	}

}
