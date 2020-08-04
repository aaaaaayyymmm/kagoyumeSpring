package com.kagoyume.dao;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.kagoyume.business.CartProductsDTO;
import com.kagoyume.business.JumsHelper;
import com.kagoyume.business.ProductData;
import com.kagoyume.business.UserDataDTO;
import com.kagoyume.business.YahooApi;

@Repository
public class UserDataDAO implements Serializable{

	/*
	@Autowired
	private NamedParameterJdbcTemplate npJdbcTemplate;
	*/
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private JumsHelper jumsHelper;
	@Autowired
	private YahooApi yahooApi;


	//インスタンスオブジェクトを返却させてコードの簡略化
    public static UserDataDAO getInstance(){
        return new UserDataDAO();
    }


    public boolean insertCheck(String name) {
		try {
			List<Map<String, Object>> list = jdbcTemplate.queryForList("SELECT * FROM user_t WHERE name=?", name);
			if(list.size()==0 || list==null) return true;
			return false;
		}catch(Exception e){
			System.out.println(e.getMessage());
			return false;
		}
    }

    public boolean uppdateCheck(int updateUserID, String updateName) {
    	boolean updateFlg = true;
		try {
	    	List<Map<String, Object>> list = jdbcTemplate.queryForList("SELECT userID, name FROM user_t WHERE name=?", updateName);
	    	//名前が使われていなかったら、変更可能
	    	if(list.size()==0 || list==null) return updateFlg;
	    	for(int i=0; i<list.size();  i++) {
	    		int dbUserID = (int) list.get(i).get("userID");
	    		//登録済みの自分の名前と同じ場合、変更可能
	    		if(updateUserID == dbUserID) return updateFlg;
	    	}
		}catch(Exception e){
			System.out.println(e.getMessage());
			return false;
		}
    	updateFlg = false;
    	return updateFlg;
    }

	public void insert(UserDataDTO ud) throws SQLException{
    	try {
    		jdbcTemplate.update("insert into user_t(name,password,mail,address,newDate) VALUES(?,?,?,?,?)",
    							ud.getName(),
    							ud.getPassword(),
    							ud.getMail(),
    							ud.getAddress(),
    							new Timestamp(System.currentTimeMillis())
    							);
    		System.out.println("insert completed");
    	}catch(Exception e){
    		System.out.println(e.getMessage());
    		throw new SQLException(e);
    	}
    }

	public void update(UserDataDTO ud) throws SQLException{
		System.out.println("update:" +ud.getName()+ ":"+ud.getUserID()+"★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
		try {
    		jdbcTemplate.update("UPDATE user_t set name=?, password=?, mail=?, address=?, newDate=? WHERE userID=?",
    							ud.getName(),
    							ud.getPassword(),
    							ud.getMail(),
    							ud.getAddress(),
    							new Timestamp(System.currentTimeMillis()),
    							ud.getUserID()
    							);
    		System.out.println("update completed");
    	}catch(Exception e){
    		System.out.println(e.getMessage());
    		throw new SQLException(e);
    	}
    }

	public void delete(UserDataDTO ud) throws SQLException{
		System.out.println("delete:" +ud.getName()+ ":"+ud.getUserID()+"★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
		try {
    		jdbcTemplate.update("UPDATE user_t set newDate=?, deleteFlg=1 WHERE userID=?",
    							new Timestamp(System.currentTimeMillis()),
    							ud.getUserID()
    							);
    		System.out.println("delete completed");
    	}catch(Exception e){
    		System.out.println(e.getMessage());
    		throw new SQLException(e);
    	}
    }

	public UserDataDTO login(String name) {
		System.out.println("intologinmethod★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
        UserDataDTO ud = new UserDataDTO();
        try {
            List<Map<String, Object>> list = jdbcTemplate.queryForList("select name, password from user_t where name= ? and deleteFlg=0", name);
        	ud.setName((String) list.get(0).get("name"));
            ud.setPassword((String) list.get(0).get("password"));
			return ud;
    	}catch(EmptyResultDataAccessException e){
    		System.out.println(e.getMessage());
    		return ud;
    	}
	}

	public int getUserID(String userName) {
        int userID = 0;
        try {
        	List<Map<String, Object>> list = jdbcTemplate.queryForList("select userID from user_t where name= ?" , userName);
        	if(list.size()==1) {
            	userID = (int) list.get(0).get("userID");
            }
			return userID;
    	}catch(EmptyResultDataAccessException e){
    		System.out.println(e.getMessage());
    		return userID;
    	}
	}

	public UserDataDTO getUserInfo(int userID) {
		UserDataDTO ud = new UserDataDTO();
		//デフォルトでnameのみ「ゲスト」にセット
		ud.setName("ゲスト");
		if(userID==0) return ud;
		try {
			Map<String, Object> map = jdbcTemplate.queryForMap("select * from user_t where userID= ?", userID);
			if(map.size()!=0) {
				ud.setUserID((int) map.get("userID"));
				ud.setName((String) map.get("name"));
				ud.setPassword((String) map.get("password"));
				ud.setMail((String) map.get("mail"));
				ud.setTotal((int) map.get("total"));
				ud.setAddress((String) map.get("address"));
				return ud;
			}
			return ud;
		}catch(EmptyResultDataAccessException e){
			System.out.println(e.getMessage());
			return ud;
		}
	}

	public void buyComplete(UserDataDTO add) throws SQLException{
		System.out.println(add.getUserID());
		try {
			jdbcTemplate.update("INSERT INTO buy_t(userID,itemCode,type,buyDate) VALUES(?,?,?,?)",
								add.getUserID(),
								add.getItemCode(),
								add.getType(),
								new Timestamp(System.currentTimeMillis())
								);
			System.out.println("buy completed");
		}catch(Exception e){
			System.out.println(e.getMessage());
			throw new SQLException(e);
		}
    }

	public void updateUserTotal(int total, int userID) throws SQLException{
		try {
			jdbcTemplate.update("UPDATE user_t set total=? WHERE userID=?",
								total,
								userID
								);
			System.out.println("usertotal updated");
			}catch(Exception e){
			System.out.println(e.getMessage());
			throw new SQLException(e);
		}
	}

	public ArrayList<CartProductsDTO> getHistory(int userID) throws SQLException, IOException{
		//UserDataDTOに詰め替えるために用意
		ArrayList<CartProductsDTO> all = new ArrayList<CartProductsDTO>();
		try {
			//ログインユーザの購入済み商品を取得
			List<Map<String, Object>> list = jdbcTemplate.queryForList("SELECT * FROM buy_t WHERE userID=?", userID);
			for(int i=0; i<list.size(); i++) {
				String code = (String) list.get(i).get("itemCode");
				ProductData pd = yahooApi.searchByItemCode(code);
				String buyDate = jumsHelper.convertDispDate((Timestamp) list.get(i).get("buyDate"));

				CartProductsDTO pro = new CartProductsDTO();
				pro.setProName(pd.getProName());
				pro.setItemCode(pd.getProId());
				pro.setType((int)list.get(i).get("type"));
				pro.setBuyDate(buyDate);
				all.add(pro);
			}
			System.out.println("history completed");
			return all;
		}catch(Exception e){
			System.out.println(e.getMessage());
			return all;
		}
	}

}
