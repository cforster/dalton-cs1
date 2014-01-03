package org.dalton;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class DaltonStock {
	public static void main(String[] args) throws InterruptedException {
		DaltonStock ds = new DaltonStock("ibm");
		System.out.println(ds);
		Thread.sleep(2000);
		ds.refresh();

		System.out.println(DaltonStock.symbolLookup("International Business Machines"));
	}

	public static String symbolLookup(String input) {
		String url= "";
		try {
			url = "http://dev.markitondemand.com/Api/v2/Lookup?input=" + URLEncoder.encode(input, "ISO-8859-1");
			URL markit;
			Document doc;
			markit = new URL(url);
			InputStream is = markit.openStream();

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(is);
			doc.getDocumentElement().normalize();
			Node res = doc.getElementsByTagName("LookupResult").item(0);
			if (res.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) res;
				String sym = eElement.getElementsByTagName("Symbol").item(0).getTextContent();
				return sym;
			}
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return "";
	}


	private Document fetchQuote(String symbol) {
		String url = "http://dev.markitondemand.com/Api/v2/Quote?symbol=" + symbol;
		URL markit;
		Document doc;
		try {
			markit = new URL(url);
			InputStream is = markit.openStream();

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(is);
			doc.getDocumentElement().normalize();
			return doc;

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	String name;
	String symbol;
	double lastprice;
	double change;
	double changepercent;
	String timestamp;
	long marketcap;
	int volume;
	double changeytd;
	double changepercentytd;
	double high;
	double low;
	double open;

	public String toString() {
		String ret = "";
		ret+="Name: " + name + "\n";
		ret+="Symbol: " + symbol + "\n";
		ret+="Last Price: " + lastprice + "\n";
		return ret;
	}

	public void refresh() {
		Document doc = fetchQuote(symbol);
		Node quote = doc.getElementsByTagName("StockQuote").item(0);
		if (quote.getNodeType() == Node.ELEMENT_NODE) {

			Element eElement = (Element) quote;

			name = eElement.getElementsByTagName("Name").item(0).getTextContent();
			lastprice = Double.parseDouble(eElement.getElementsByTagName("LastPrice").item(0).getTextContent());
			change = Double.parseDouble(eElement.getElementsByTagName("Change").item(0).getTextContent());
			changepercent = Double.parseDouble(eElement.getElementsByTagName("ChangePercent").item(0).getTextContent());
			timestamp = eElement.getElementsByTagName("Timestamp").item(0).getTextContent();
			marketcap = Long.parseLong(eElement.getElementsByTagName("MarketCap").item(0).getTextContent());
			volume = Integer.parseInt(eElement.getElementsByTagName("Volume").item(0).getTextContent());
			changeytd = Double.parseDouble(eElement.getElementsByTagName("ChangeYTD").item(0).getTextContent());
			changepercentytd = Double.parseDouble(eElement.getElementsByTagName("ChangePercentYTD").item(0).getTextContent());
			high = Double.parseDouble(eElement.getElementsByTagName("High").item(0).getTextContent());
			open = Double.parseDouble(eElement.getElementsByTagName("Open").item(0).getTextContent());
		}
	}

	public DaltonStock(String symbol) {
		this.symbol = symbol;
		refresh();
	}
}
