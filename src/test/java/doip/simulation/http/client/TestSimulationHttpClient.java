package doip.simulation.http.client;

//import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.starcode88.http.HttpUtils;
import com.starcode88.http.exception.HttpInvalidResponseBodyType;
import com.starcode88.http.exception.HttpStatusCodeException;
import com.starcode88.jutils.properties.EmptyPropertyValue;
import com.starcode88.jutils.properties.MissingProperty;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import doip.junit.InitializationError;
import doip.library.exception.DoipException;
import doip.simulation.http.*;
import doip.simulation.http.lib.ServerInfo;

import static com.starcode88.jtest.Assertions.*;
//import static org.junit.jupiter.api.Assertions.assertEquals;

class TestSimulationHttpClient {
	private static Logger logger = LogManager.getLogger(TestSimulationHttpClient.class);
	private static DoipHttpServer server = null;

	private static final String host = "http://localhost:8080";
	private static DoipSimulationHttpClient httpClient = null;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		String method = "public static void setUpBeforeClass()";
		try {
			logger.trace(">>> {}", method);
			server = DoipHttpServerBuilder.newBuilder().addPlatform("src/test/resources/X2024.properties").build();
			server.start();

			httpClient = new DoipSimulationHttpClient(host);
		} catch (Exception e) {
			logger.error("Error setUpBeforeClass: {}", e.getMessage(), e);
			throw logger.throwing(new InitializationError(e));

		} finally {
			logger.trace("<<< {}", method);
		}
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		String method = "public static void tearDownAfterClass()";
		logger.trace(">>> {}", method);
		server.stop();
		logger.trace("<<< {}", method);
	}

	@Test
	void testGetOverviewExtendedSuccess() throws HttpStatusCodeException, HttpInvalidResponseBodyType,
			URISyntaxException, IOException, InterruptedException {
		logger.info("-------------------------- testGetOverviewExtendedSuccess ------------------------------------");
		try {
			DoipHttpServerResponse response = httpClient.getOverviewExtended("RUNNING");

			// Assert
			assertEquals(200, response.getStatusCode(), "The HTTP status code is not 200");
			assertNotNull(response.getResponseBody(), "The response body from server is null");
			// Additional assertions if needed
			if (response.getResult() != null && response.getResult() instanceof ServerInfo) {

				ServerInfo serverInfo = (ServerInfo) response.getResult();
				logger.info("Received response result is instanceof ServerInfo");
				assertNotNull(serverInfo, "Received ServerInfo is wrong");
			} else {
				fail("ServerInfo is not available");
			}
		} catch (Exception e) {
			fail("Unexpected Exception: " + e.getMessage());
		}
	}

	@Test
	void testGetOverviewExtendedFailure() throws HttpInvalidResponseBodyType,
			URISyntaxException, IOException, InterruptedException {
		logger.info("-------------------------- testGetOverviewExtendedFailure() ------------------------------------");
		
		DoipHttpServerResponse response = httpClient.getOverviewExtended("????");

		assertEquals(400, response.getStatusCode(), "The status code does not match the value 400");
		logger.info("Received response Status code = {} ",response.getStatusCode());
		logger.info("Received response Body = {} ", response.getResponseBody());
	
	}

}
