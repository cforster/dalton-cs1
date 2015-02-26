package org.dalton;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
/**
 * get stock prices from the internet
 * @author cforster
 *
 */

public class DaltonStock {
	public static void main(String[] args) throws InterruptedException {
//		DaltonStock ds = new DaltonStock("ibmnhjk");
//		System.out.println(ds);
//		Thread.sleep(2000);
//		ds.refresh();

//		System.out.println(DaltonStock.symbolLookup("International Business Machines"));
//		DaltonStock ds = new DaltonStock();
//		System.out.println(ds);
	}

	/**
	 * give the symbol for a company
	 * @param input the search term (company name)
	 * @return the symbol, empty if nothing found
	 */
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
			if(res==null) return "";
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

	public String name = "";
	public String symbol = "";
	public double lastprice;
	public double change;
	public double changepercent;
	public String timestamp;
	public long marketcap;
	public int volume;
	public double changeytd;
	public double changepercentytd;
	public double high;
	public double low;
	public double open;

	public String toString() {
		String ret = "";
		ret+="Name: " + name + "\n";
		ret+="Symbol: " + symbol + "\n";
		ret+="Last Price: " + lastprice + "\n";
		return ret;
	}

	/**
	 * refresh the price.
	 */
	public void refresh() {
		Document doc = fetchQuote(symbol);
		//if(doc==null) return;
		Node quote = doc.getElementsByTagName("StockQuote").item(0);
		if(quote==null) {
			System.err.println("stock not found");
			return;
		}
		
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

	/**
	 * get the stock price for a given symbol
	 * @param symbol the stock symbol
	 */
	public DaltonStock(String symbol) {
		this.symbol = symbol;
		refresh();
	}

	/**
	 * get a random stock
	 */
	public DaltonStock() {
		Random gen = new Random();
		String sym="";
		do{
			//get a random 3 letters:
			for (int i = 0; i < 3; i++) {
				sym+=""+(char)('a'+gen.nextInt(22));
			}
			//search for that:
			sym = DaltonStock.symbolLookup(sym);
		}while(sym.length()==0);
		this.symbol = sym;
		refresh();
	}
}
