package com.kagoyume.business;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

//画面系の処理や表示を簡略化するためのヘルパークラス。定数なども保存されます
@Service
public class JumsHelper {

	public static JumsHelper getInstance(){
		return new JumsHelper();
	}

	//配送先の種別は数値で管理している。画面用とDB用を相互にマッピングする
    public Map<String,Integer> exTypenum(){
    	Map<String,Integer> typeMap = new LinkedHashMap<>();	//LinkedHashMap→putした順序を保持してくれる
    	typeMap.put("自宅", 1);
    	typeMap.put("郵便局", 2);
    	typeMap.put("コンビニ", 3);
   		return typeMap;
    }

	//配送先の種別は数値で管理している。画面用とDB用を相互にマッピングする
    public Map<String,String> TypeMappingToDisp(){
    	Map<String,String> typeMap = new LinkedHashMap<>();	//LinkedHashMap→putした順序を保持してくれる
    	typeMap.put("1", "自宅");
    	typeMap.put("2", "郵便局");
    	typeMap.put("3", "コンビニ");
   		return typeMap;
    }

    public String convertDispDate(Timestamp date) {
    	String dispDate = "";
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    	dispDate = sdf.format(date);
    	return dispDate;
    }

    public int calcTolal(List<ProductData> cart) {
    	//総額計算
		int total = 0;
		if (cart != null) {
			for (int i=0; i<cart.size(); i++){
	        	 int price = cart.get(i).getPrice();
	        	 total += price;
			}
		}
		return total;
    }
    public boolean isBrank(String query) {
    	query = query.replaceAll("　", "");
		query = query.replaceAll(" ", "");
		if(query==null || query.isEmpty()) return true;
		return false;
    }
}