package com.sharps.Network;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.os.AsyncTask;

import com.sharps.main.ViewContent;

public class GamesDownloader
		extends
		AsyncTask<String, Integer, HashMap<String, ArrayList<Hashtable<String, String>>>> {
	private NetworkMediator mediator = NetworkMediator.getSingletonObject();

	public GamesDownloader(String URL) {
		execute(URL);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected HashMap<String, ArrayList<Hashtable<String, String>>> doInBackground(
			String... params) {
		// TODO Auto-generated method stub
		String str = "";
		try {
			HttpParams httpParameters = new BasicHttpParams();
			int timeoutConnection = 3000;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			// Set the default socket timeout (SO_TIMEOUT) 
			// in milliseconds which is the timeout for waiting for data.
			int timeoutSocket = 3000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			HttpClient hc = new DefaultHttpClient(httpParameters);
			HttpGet post = new HttpGet(params[0]);
			// Create local HTTP context
			HttpContext localContext = new BasicHttpContext();
			// Bind custom cookie store to the local context
			localContext.setAttribute(ClientContext.COOKIE_STORE,
					mediator.getCockies());
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			str = hc.execute(post, responseHandler, localContext);
			System.out.println("Inkommande: " + str);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return parseContent(str);
	}

	@Override
	protected void onPostExecute(
			HashMap<String, ArrayList<Hashtable<String, String>>> result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		mediator.notifyContentContainers(ViewContent.GAMES);
	}

	private HashMap<String, ArrayList<Hashtable<String, String>>> parseContent(
			String str) {
		HashMap<String, ArrayList<Hashtable<String, String>>> map = mediator
				.getLibrary().getGames();
		// TODO Auto-generated method stub
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(str));
			org.w3c.dom.Document doc = docBuilder.parse(is);
			// normalize text representation
			doc.getDocumentElement().normalize();
			//System.out.println("Root element of the doc is "
			//		+ doc.getDocumentElement().getNodeName());
			NodeList listOfGames = doc.getElementsByTagName("game");
			int totalGames = listOfGames.getLength();
			//System.out.println("Total no of games : " + totalGames);

			for (int s = 0; s < listOfGames.getLength(); s++) {
				Hashtable<String, String> table = new Hashtable<String, String>();
				String id = null;
				Node eventNode = listOfGames.item(s);
				if (eventNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eventElement = (Element) eventNode;
					for (int i = 0; i < eventElement.getChildNodes()
							.getLength(); i++) {
						Node e = eventElement.getChildNodes().item(i)
								.getChildNodes().item(0);
						if (e != null && e.getNodeValue() != null) {
							if (eventNode.getChildNodes().item(i).getNodeName()
									.equals("sheetid")) {
								id = e.getNodeValue();
								if (map.get(id)==null) {
									map.put(id,
											new ArrayList<Hashtable<String, String>>());
								}
							}
							table.put(eventNode.getChildNodes().item(i)
									.getNodeName(), e.getNodeValue());
						}
					}
					map.get(id).add(table);
				}// end of if clause
				
			}// end of for loop with s var
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

}
