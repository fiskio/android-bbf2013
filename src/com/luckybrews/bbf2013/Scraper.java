package com.luckybrews.bbf2013;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;


public class Scraper {

	private final static String TAG = "Scraper";
	
	// private static final String DATABASE_NAME="beers.db";
	// private static final int SCHEMA=1; 

	static final String BREWERY="brewery";
	static final String BEERNAME="beername";
	static final String TYPE="type";
	static final String COLOR="color";
	static final String ABV="abv";
	static final String DESCRIPTION="description";
	static final String ICON="icon";
	static final String WISHLIST="wishlist";
	static final String DRUNK="drunk";
	static final String RATING="rating";
	static final String COMMENT="comment";
	
	static final String TABLE="beerlist";
	
	private byte[] getLogoImage(String strurl)
	{
		URL url = null;
		try {
			url = new URL(strurl);
		} catch (MalformedURLException e1) {
			Log.e(TAG, "URL is not valid!");
			e1.printStackTrace();
		}
		ByteArrayOutputStream bais = new ByteArrayOutputStream();
		InputStream is = null;
		try {
		  is = url.openStream();
		  byte[] byteChunk = new byte[4096]; 
		  int n;

		  while ( (n = is.read(byteChunk)) > 0 ) {
		    bais.write(byteChunk, 0, n);
		  }
		
		  if (is != null) { is.close(); }
		  
		}
		catch (IOException e) {
		  Log.e(TAG, "Failed while reading bytes from " + url.toExternalForm() + " : " + e.getMessage());
		  e.printStackTrace ();
		}
		
		return bais.toByteArray();
	}
	
	public List<Beer> scrape() {
		
		List<Beer> ret = new ArrayList<Beer>();
		
		Document doc = null;
		try {
			// final String url = "http://www.yourround.co.uk/Mobile/Festival/Battersea/Battersea_Beer_Festival_2013/SW11_5TN.aspx"; // Battersea
			final String url = "http://www.yourround.co.uk/Mobile/Brewer/London/Beavertown_Brewery/N1_5AA.aspx"; // BeaverTown
			
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			Log.e(TAG, "Error connecting JSOUP");
			e.printStackTrace();
		}
		
		// String title = doc.title();
	
		// final String tid = "ctl00_MainContentPlaceHolder_FestivalInventoryListView_itemPlaceholderContainer";
		final String tid = "ctl00_MainContentPlaceHolder_BrewerInventoryListViewCurrent_itemPlaceholderContainer";
		Element table = doc.getElementById(tid);
		
		//Element logo = table.getElementById("ctl00_MainContentPlaceHolder_FestivalInventoryListView_ctrl0_imgDrinkLogoHyperLink");
		//System.out.println("LOGO: " + logo.html());
		
		//Element brewery = table.getElementById("ctl00_MainContentPlaceHolder_FestivalInventoryListView_ctrl0_DrinkNameLabel");
		//System.out.println("BREWERY: " + brewery.text());

		Elements trs = table.select("table[width=100%]");
		int idx = 0;
		for (Element one : trs) {
			idx++;
			if (idx%2 == 0) 
			//if (idx > 1)
				continue;
			//System.out.println(one + "\n");
			Beer item = new Beer();
			
			Elements tds = one.select("td");
			//System.out.println("TDS: " + tds.size());
			Iterator<Element> iter = tds.iterator();
			Element color = iter.next();
			item.color = color.attr("bgcolor");

			Element logo = iter.next();
			String url = logo.select("a").first().select("img").first().absUrl("src");
			// System.out.println(url);
			
			//fetch image 
			byte[] img = getLogoImage(url);
			item.img = img;
			//String str_logo = new String(img, )
			//System.out.println(img);

			Element beer = iter.next();
			beer = iter.next();//
			StringTokenizer s = new StringTokenizer(beer.text(), "-");
			//System.out.println(beer.text());
			//s.useDelimiter("-");
			
			if (s.countTokens() == 4) {
				String bprod = s.nextToken().trim();
				item.brewery = bprod;
				//System.out.println(bprod);
			}
		
			//item.brewery = title;
			
			String bname = s.nextToken().trim();
			item.beerName = bname;
			
			String btype = s.nextToken().trim();
			item.type = btype;
			
			String babv = s.nextToken().trim();
			item.abv = babv;
			
			//System.out.println(bname);
			//System.out.println(btype);
			//System.out.println(babv);

			Element desc = iter.next();
			item.desc = desc.text();
			//System.out.println(desc.text() +"\n");
			
			Log.d(TAG, "Scrpaped beer # " + idx/2);
			
			ret.add(item);	
		}
		
		return ret;
	}
	
	
}