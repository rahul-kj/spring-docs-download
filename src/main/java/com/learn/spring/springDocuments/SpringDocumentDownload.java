package com.learn.spring.springDocuments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Hello world!
 * 
 */
public class SpringDocumentDownload {

	private static List<String> documentList;
	private static Properties properties;

	static {
		properties = loadAllProperties();
		documentList = new ArrayList<String>();
		documentList.add(DocumentConstants.SPRING_AMQP);
		documentList.add(DocumentConstants.SPRING_ANDROID);
		documentList.add(DocumentConstants.SPRING_BATCH);
		documentList.add(DocumentConstants.SPRING_BOOT);
		documentList.add(DocumentConstants.SPRING_CLOUD);
		documentList.add(DocumentConstants.SPRING_CLOUD_CLOUDFOUNDRY);
		documentList.add(DocumentConstants.SPRING_CLOUD_CONFIG);
		documentList.add(DocumentConstants.SPRING_CLOUD_CONNECTORS);
		documentList.add(DocumentConstants.SPRING_CLOUD_CONSUL);
		documentList.add(DocumentConstants.SPRING_CLOUD_DATAFLOW);
		documentList.add(DocumentConstants.SPRING_CLOUD_BUS);
		documentList.add(DocumentConstants.SPRING_CLOUD_AWS);
		documentList.add(DocumentConstants.SPRING_CLOUD_NETFLIX);
		documentList.add(DocumentConstants.SPRING_CLOUD_SECURITY);
		documentList.add(DocumentConstants.SPRING_CLOUD_SLEUTH);
		documentList.add(DocumentConstants.SPRING_CLOUD_STREAM);
		documentList.add(DocumentConstants.SPRING_CLOUD_STREAM_MODULES);
		documentList.add(DocumentConstants.SPRING_CLOUD_ZOOKEEPER);
		documentList.add(DocumentConstants.SPRING_DATA);
		documentList.add(DocumentConstants.SPRING_DATA_MONGO);
		documentList.add(DocumentConstants.SPRING_DATA_NEO4J);
		documentList.add(DocumentConstants.SPRING_DATA_GEMFIRE);
		documentList.add(DocumentConstants.SPRING_DATA_REDIS);
		documentList.add(DocumentConstants.SPRING_DATA_JDBC_EXT);
		documentList.add(DocumentConstants.SPRING_DATA_JPA);
		documentList.add(DocumentConstants.SPRING_FRAMEWORK);
		documentList.add(DocumentConstants.SPRING_HATEOAS);
		documentList.add(DocumentConstants.SPRING_INTEGRATION);
		documentList.add(DocumentConstants.SPRING_LDAP);
		documentList.add(DocumentConstants.SPRING_MOBILE);
		documentList.add(DocumentConstants.SPRING_ROO);
		documentList.add(DocumentConstants.SPRING_SECURITY);
		documentList.add(DocumentConstants.SPRING_SECURITY_KERBEROS);
		documentList.add(DocumentConstants.SPRING_SECURITY_SAML);
		documentList.add(DocumentConstants.SPRING_SESSION);
		documentList.add(DocumentConstants.SPRING_SHELL);
		documentList.add(DocumentConstants.SPRING_SOCIAL);
		documentList.add(DocumentConstants.SPRING_SOCIAL_FACEBOOK);
		documentList.add(DocumentConstants.SPRING_SOCIAL_TWITTER);
		documentList.add(DocumentConstants.SPRING_SOCIAL_LINKEDIN);
		documentList.add(DocumentConstants.SPRING_WEBFLOW);
		documentList.add(DocumentConstants.SPRING_WEBSERVICE);
		documentList.add(DocumentConstants.SPRING_XD);
	}

	public static void main(String[] args) {
		if (args == null || args.length != 1) {
			System.out.println("Cannot proceed, please specify the output directory");
			System.exit(0);
		}

		HttpClient httpClient = new DefaultHttpClient();

		for (String document : documentList) {
			String baseURL = properties.getProperty(DocumentConstants.BASE_URL);
			String url = properties.getProperty(document + DocumentConstants.URL);
			String version = properties.getProperty(document + DocumentConstants.VERSION);

			url = MessageFormat.format(url, baseURL, version);

			String fileName = properties.getProperty(document + DocumentConstants.FILE);
			String directory = args[0];
			if (!directory.endsWith("/")) {
				directory = directory + "/";
			}

			File file = null;
			if(fileName.equalsIgnoreCase("index.html")) {
				String newFileName = document.replace(".", "-").concat("-reference.html");
				file = new File(directory + newFileName);
			} else {
				file = new File(directory + fileName);
			}
			
			if (!file.exists()) {
				HttpGet httpGet = null;
				if (url.contains("htmlsingle")) {
					httpGet = new HttpGet(url);
				} else {
					httpGet = new HttpGet(url + fileName);
				}

				try {
					createDirectoryIfItDoesNotExist(directory);
					fetchAndStoreFile(httpClient, file, httpGet);
				} catch (Exception e) {
					System.out.println("Failed to download file : " + fileName + "due to : " + e);
					continue;
				}

				System.out.println("Done with file download for : " + fileName);
			} else {
				System.out.println("File already exists : " + fileName);
			}
			System.out.println("************** SEPERATOR *****************");
		}

	}

	private static void fetchAndStoreFile(HttpClient httpClient, File file, HttpGet httpGet) throws IOException,
			ClientProtocolException, FileNotFoundException {
		HttpResponse response = httpClient.execute(httpGet);
		InputStream pdfStream = response.getEntity().getContent();

		FileOutputStream fos = new FileOutputStream(file);

		int read = 0;
		byte[] bytes = new byte[1024];
		while ((read = pdfStream.read(bytes)) != -1) {
			fos.write(bytes, 0, read);
		}

		fos.close();

		pdfStream.close();
	}

	private static void createDirectoryIfItDoesNotExist(String directory) {
		File dir = new File(directory);
		if (!dir.exists()) {
			dir.mkdir();
		}
	}

	private static Properties loadAllProperties() {
		Properties properties = new Properties();
		loadAllUrls(properties);
		loadAllVersions(properties);
		loadAllFilesToDownload(properties);
		return properties;
	}

	private static void loadAllUrls(Properties properties) {
		ResourceBundle bundle = ResourceBundle.getBundle("META-INF/spring/url");

		Enumeration<String> keys = bundle.getKeys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			properties.put(key, bundle.getString(key));
		}
	}

	private static void loadAllFilesToDownload(Properties properties) {
		ResourceBundle bundle = ResourceBundle.getBundle("META-INF/spring/file");

		Enumeration<String> keys = bundle.getKeys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			properties.put(key, bundle.getString(key));
		}
	}

	private static void loadAllVersions(Properties properties) {
		ResourceBundle bundle = ResourceBundle.getBundle("META-INF/spring/version");

		Enumeration<String> keys = bundle.getKeys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			properties.put(key, bundle.getString(key));
		}
	}
}
