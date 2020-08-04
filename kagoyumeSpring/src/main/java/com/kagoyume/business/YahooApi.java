package com.kagoyume.business;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class YahooApi {

	//yahooAPIのアプリケーションID
	String appid = "dj00aiZpPWZSNUhRdVpENktGNyZzPWNvbnN1bWVyc2VjcmV0Jng9MDQ-";
	String productBaseUrl = "http://localhost:8080/kagoyume/Item?id=";

    public JsonNode searchByQuery(String query) throws IOException{
   		String encodedQuery = URLEncoder.encode(query, "UTF-8");
		//リクエストURL
		String url = "https://shopping.yahooapis.jp/ShoppingWebService/V3/itemSearch?appid="+appid+"&query="+encodedQuery;
		//API接続用のURL生成
		URL url4conn = new URL(url);
		//API接続用のURLへのコネクションを取得
		HttpURLConnection conn = (HttpURLConnection)url4conn.openConnection();
		//HTTPメソッドをGETに指定
		conn.setRequestMethod("GET");
		//リクエストのボディ送信を許可しない
		conn.setDoOutput(false);
		//レスポンスのボディ送信を許可する
		conn.setDoInput(true);
		//接続
		conn.connect();

		// 検索果読み取り
		InputStream stream = null;
		try {
			stream = conn.getErrorStream();
			if (stream == null) {
				stream = conn.getInputStream();
			}
		} catch (IOException e) {
			System.err.println(e);
		}

		//読み取った検索結果をjsonに変換
		String result = "";
		JsonNode json = null;
		try {
			result = convertToString(stream);
			System.out.println(result);

			//JSON文字列を読み込み、JsonNodeオブジェクトに変換
			JsonFactory jfactory = new JsonFactory();
			JsonParser parser = jfactory.createParser(result);
			ObjectMapper mapper = new ObjectMapper();
			json = mapper.readTree(parser);

		} catch (IOException e) {
			System.err.println(e);
			return json;
		} finally {
			conn.disconnect();
		}
		return json;
    }

	public int getSearchCnt(JsonNode json) {
		int searchCnt = 0;
		//検索結果件数を取得
		searchCnt = json.get("totalResultsAvailable").asInt();
		return searchCnt;
	}

	public ArrayList<ProductData> getSearchData(JsonNode json) {
		//商品検索結果の入ったBeansを格納するArrayListを生成
		ArrayList<ProductData> list = new ArrayList<ProductData>();
		//Jsonから20件分の要素を取りだし、String型に格納（とりあえず20件）
		for(int i= 0; i <20; i++) {
			String imageURL = json.get("hits").get(i).get("image").get("small").textValue();
			String proName = json.get("hits").get(i).get("name").asText();
			int price = json.get("hits").get(i).get("price").asInt();
			String proId = json.get("hits").get(i).get("code").textValue();
			String review = json.get("hits").get(i).get("review").get("rate").textValue();

			ProductData pd = new ProductData();
			pd.setHitNum(String.valueOf(i));
			pd.setImageURL(imageURL);
			pd.setProName(proName);
			pd.setPrice(price);
			pd.setProId(proId);
			pd.setReview(review);
			pd.setProURL(productBaseUrl + proId);
			list.add(pd);
		}
		return list;
	}


	public ProductData searchByItemCode(String itemCode) throws IOException {
//		リクエストURL
		String url = "https://shopping.yahooapis.jp/ShoppingWebService/V1/json/itemLookup?appid="+appid+"&itemcode="+itemCode+"&responsegroup=medium";
//		API接続用のURL生成
		URL url4conn = new URL(url);
//		API接続用のURLへのコネクションを取得
		HttpURLConnection conn = (HttpURLConnection)url4conn.openConnection();
//		HTTPメソッドをGETに指定
		conn.setRequestMethod("GET");
//		リクエストのボディ送信を許可しない
		conn.setDoOutput(false);
//		レスポンスのボディ送信を許可する
		conn.setDoInput(true);
//		接続
		conn.connect();

		String result = "";
		result = convertToString(conn.getInputStream());
		System.out.println(result);

		//JSON文字列を読み込み、JsonNodeオブジェクトに変換
		JsonFactory jfactory = new JsonFactory();
		JsonParser parser = jfactory.createParser(result);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(parser);

		String imageURL = root.get("ResultSet").get("0").get("Result").get("0").get("Image").get("Small").textValue();
		String proName = root.get("ResultSet").get("0").get("Result").get("0").get("Name").textValue();
		int price = root.get("ResultSet").get("0").get("Result").get("0").get("Price").get("_value").asInt();
		String proId = root.get("ResultSet").get("0").get("Result").get("0").get("Code").textValue();
		String caption = root.get("ResultSet").get("0").get("Result").get("0").get("Description").textValue();
		String review = root.get("ResultSet").get("0").get("Result").get("0").get("Review").get("Rate").textValue();

		ProductData pd = new ProductData();
		pd.setImageURL(imageURL);
		pd.setProName(proName);
		pd.setPrice(price);
		pd.setProId(proId);
		pd.setCaption(caption);
		pd.setReview(review);
		pd.setProURL(productBaseUrl + proId);

		conn.disconnect();
		return pd;
		}

	private String convertToString(InputStream stream) throws IOException {
		StringBuffer sb = new StringBuffer();
		String line = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		try {
			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

}
