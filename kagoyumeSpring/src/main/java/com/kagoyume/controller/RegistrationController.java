package com.kagoyume.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.kagoyume.business.UserDataDTO;
import com.kagoyume.business.UserInfoForm;
import com.kagoyume.service.UserDetailsServiceImpl;


@Controller
public class RegistrationController extends BaseController {
	@Autowired
	private UserDetailsServiceImpl userDetailsServiceImpl;
	@Autowired
	HttpSession session;
	@Autowired
	HttpServletRequest request;

	@RequestMapping(value = "/kagoyume/registration", method = RequestMethod.POST)
    public ModelAndView registration(UserInfoForm userInfoForm, ModelAndView  mav) {
		//viewで使うモデルの為に、初期値を設定
		mav.addObject("nameCheck", true);
		mav.setViewName("kagoyume/registration");
		return mav;
	}

	@RequestMapping(value = "/kagoyume/registrationConfirm", method = RequestMethod.POST)
    public ModelAndView registrationConfirm(@Validated UserInfoForm userInfoForm, BindingResult result, ModelAndView  mav) {
		//遷移先URLを先に定義
		mav.setViewName("kagoyume/registration");
		//おなじユーザ名は登録させない
		boolean ck = true;
		ck = userDetailsServiceImpl.insertCheck(userInfoForm.getName());
		mav.addObject("nameCheck", ck);
		//バリデーションチェックに引っかかった場合、登録画面へ戻る
		if(result.hasErrors() || !ck) return mav;

		System.out.println(userInfoForm.getName());
		//バリデーションチェックをクリアしたら、確認画面へ遷移
		mav.setViewName("kagoyume/registrationConfirm");
		return mav;
	}

	@RequestMapping(value = "/kagoyume/registrationComplete", method = RequestMethod.POST)
    public String registrationComplete(UserInfoForm userInfoForm, ModelAndView mav) {
		UserDataDTO ud = new UserDataDTO();
		System.out.println(userInfoForm.getName());
		ud.setName(userInfoForm.getName());
		ud.setPassword(userInfoForm.getPassword());
		ud.setMail(userInfoForm.getMail());
		ud.setAddress(userInfoForm.getAddress());
		userDetailsServiceImpl.insertUserInfo(ud);

		//自動ログインする
		try {
			request.login(ud.getName(), ud.getPassword());
		} catch (ServletException e) {
			//内部ログインできなかった場合、topからユーザ自身でログインしてもらうからなにもしない
			e.printStackTrace();
		}
		return "kagoyume/registrationComplete";
	}

	@RequestMapping(value = "/kagoyume/mydelete", method = RequestMethod.GET)
    public ModelAndView myDelete(ModelAndView  mav) {
		mav.setViewName("kagoyume/mydelete");
		return mav;
	}

	@RequestMapping(value = "/kagoyume/myDeleteResult", method = RequestMethod.POST)
    public ModelAndView myDeleteResult(@ModelAttribute("userData") UserDataDTO baseud, ModelAndView  mav) {
		mav.setViewName("kagoyume/myDeleteResult");
		userDetailsServiceImpl.deleteUserInfo(baseud);
		session.removeAttribute("userData");
		session.invalidate();
		//新しいセッションを作成
	    session = request.getSession();
		return mav;
	}

	@RequestMapping(value = "/kagoyume/myupdate", method = RequestMethod.GET)
    public ModelAndView myUpdate(UserInfoForm userInfoForm,
    								ModelAndView  mav) {
		//viewで使うモデルの為に、初期値を設定
		mav.addObject("nameCheck", true);
		mav.setViewName("kagoyume/myupdate");
		return mav;
	}

	@RequestMapping(value = "/kagoyume/myUpdateResult", method = RequestMethod.POST)
    public ModelAndView myUpdateResult(@ModelAttribute("userData") UserDataDTO baseud,
    									@Validated UserInfoForm userInfoForm,
        								BindingResult result,
    									ModelAndView mav) {
		//アップデートチェックのために、ユーザーIDを取得
		session = request.getSession();
		UserDataDTO tmp = (UserDataDTO) session.getAttribute("userData");
		int userID = tmp.getUserID();

		//遷移先URLを先に定義
		mav.setViewName("kagoyume/myupdate");
		//おなじユーザ名は登録させないためのチェックをする
		boolean ck = true;
		ck = userDetailsServiceImpl.uppdateCheck(userID, userInfoForm.getName());
		mav.addObject("nameCheck", ck);

		UserDataDTO ud = new UserDataDTO();
		ud.setUserID(baseud.getUserID());
		ud.setName(userInfoForm.getName());
		ud.setPassword(userInfoForm.getPassword());
		ud.setMail(userInfoForm.getMail());
		ud.setAddress(userInfoForm.getAddress());
		mav.addObject("userData", ud);
		//バリデーションチェックに引っかかった場合、登録画面へ戻る
		if(result.hasErrors() || !ck) return mav;

		//バリデーションチェックにクリアしたら、updateし、完了画面へ遷移
		userDetailsServiceImpl.updateUserInfo(ud);
		//ログイン情報が違くなってしまうと、会員情報を正しく表示できなくなるので、
		//画面表示用と、BaseControllerのgetUserData用に新しい名前を格納する
		mav.addObject("userName", ud.getName());
		session.setAttribute("newUserName", ud.getName());
		mav.setViewName("kagoyume/myUpdateResult");
		return mav;
	}


}
