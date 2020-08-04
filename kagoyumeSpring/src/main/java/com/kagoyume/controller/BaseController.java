package com.kagoyume.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

import com.kagoyume.business.UserDataDTO;
import com.kagoyume.service.UserDetailsServiceImpl;

@Controller
public class BaseController {

	@Autowired
	private UserDetailsServiceImpl userDetailsServiceImpl;
	@Autowired
	HttpSession session;
	@Autowired
	HttpServletRequest request;

	//ログインユーザー情報保持
	@ModelAttribute("userData")
	public UserDataDTO getUserData(ModelAndView mav) {
		session = request.getSession();
		//ログイン情報からユーザIDを取得
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String userName = auth.getName().equals("anonymousUser")? "ゲスト": auth.getName();
		//会員情報変更している場合、新しい名前を取得
		if(session.getAttribute("newUserName") != null) userName = (String)session.getAttribute("newUserName");
		int userID = userDetailsServiceImpl.getLoginUserID(userName);

		//userIDからユーザ情報を取得
		UserDataDTO ud = userDetailsServiceImpl.getLoginUserInfo(userID);
		System.out.println("loginuser is "+ ud.getName());
		System.out.println("loginuser is "+ ud.getPassword());
		boolean loginFlg = ud.getUserID()==0? false: true;

		//ログアウトしてたら、userDataのセッションを消す
		if(userName.equals("ゲスト") && session.getAttribute("userData") !=null) {
			session.removeAttribute("userData");
			session.removeAttribute("newUserName");
		}
		//ログイン中の場合、ユーザ情報をセッションに格納
		if(!userName.equals("ゲスト")) {
			session.setAttribute("userData", ud);
		}
		mav.addObject("userData", ud);
		mav.addObject("userName", userName);
		mav.addObject("loginFlg", loginFlg);
		return ud;
	}

}
