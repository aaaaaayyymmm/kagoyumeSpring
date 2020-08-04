package com.kagoyume.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.kagoyume.business.CartProductsDTO;
import com.kagoyume.business.UserDataDTO;
import com.kagoyume.dao.UserDataDAO;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{

	//DBからユーザ情報を検索するメソッドを実装したクラス
	@Autowired
	private UserDataDAO userDao;

	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		System.out.println("loadUserByUsername:"+userName+"★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
		//入力したユーザー名から認証を行うユーザー情報を取得する
		UserDataDTO ud = userDao.login(userName);
		//ユーザー情報を取得できなかった場合
		if(ud == null){
			throw new UsernameNotFoundException("User not found for login name: " + userName);
		}
		//権限設定
		//AdminやUserなどが存在するが、今回は利用しないのでUSERのみを仮で設定
		List<GrantedAuthority> grantList = new ArrayList<GrantedAuthority>();
		GrantedAuthority authority = new SimpleGrantedAuthority("USER");
		grantList.add(authority);

		//ユーザー情報が取得できたらSpring Securityで認証できる形で戻す
		//rawDataのパスワードは渡すことができないので、暗号化する
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		//UserDetailsはインタフェースなのでUserクラスのコンストラクタで生成したユーザオブジェクトをキャスト
		UserDetails userDetails = (UserDetails)new User(ud.getName(), encoder.encode(ud.getPassword()),grantList);
		return userDetails;
	}

    public int getLoginUserID(String userName) {
    	return userDao.getUserID(userName);
    }

    public UserDataDTO getLoginUserInfo(int userID) {
    	return userDao.getUserInfo(userID);
    }

    public boolean insertCheck(String name) {
    	return userDao.insertCheck(name);
    }

    public boolean uppdateCheck(int updateUserID, String updateName) {
    	return userDao.uppdateCheck(updateUserID, updateName);
    	}

    public void insertUserInfo(UserDataDTO ud) {
    	try {
			userDao.insert(ud);
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

    public void updateUserInfo(UserDataDTO ud) {
    	try {
			userDao.update(ud);
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

    public void deleteUserInfo(UserDataDTO ud) {
    	try {
			userDao.delete(ud);
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

    public void buyComplete(UserDataDTO add) {
		try {
			userDao.buyComplete(add);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

    public void updateUserTotal(int total, int userID) {
		try {
			userDao.updateUserTotal(total, userID);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

    public ArrayList<CartProductsDTO> getHistory(int userID) {
		try {
			return userDao.getHistory(userID);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}


}
