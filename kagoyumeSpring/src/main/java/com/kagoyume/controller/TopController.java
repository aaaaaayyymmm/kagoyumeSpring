package com.kagoyume.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.JsonNode;
import com.kagoyume.business.CartProductsDTO;
import com.kagoyume.business.JumsHelper;
import com.kagoyume.business.ProductData;
import com.kagoyume.business.UserDataDTO;
import com.kagoyume.business.YahooApi;
import com.kagoyume.service.UserDetailsServiceImpl;

@Controller
public class TopController extends BaseController {

	@Autowired
	private UserDetailsServiceImpl userDetailsServiceImpl;
	@Autowired
	private JumsHelper jumsHelper;
	@Autowired
	private YahooApi yahooApi;


	@RequestMapping(value = "/kagoyume/top")
	public ModelAndView top(ModelAndView mav) {
		mav.setViewName("kagoyume/top");
		return mav;
	}

	@RequestMapping(value = "/kagoyume/mydata", method = RequestMethod.GET)
    public ModelAndView myData(ModelAndView  mav) {
		//ログイン中でない場合、ログイン画面へ遷移
		mav.setViewName("kagoyume/login");
		//ログイン中のセッション情報があれば、会員情報ページへ遷移
		session = request.getSession(false);
		if (session.getAttribute("userData") != null) {
		mav.addObject("userData", session.getAttribute("userData"));
		mav.setViewName("kagoyume/mydata");
		}
		return mav;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/kagoyume/cart", method = RequestMethod.GET)
    public ModelAndView showCart(ModelAndView  mav) {
		mav.setViewName("kagoyume/login");
		List<ProductData> cart = new ArrayList<ProductData>();
		session = request.getSession(false);
		if (session.getAttribute("userData") != null) {
			cart = (List<ProductData>)session.getAttribute("cart");
			int total = jumsHelper.calcTolal(cart);
			session.setAttribute("total", total);
			session.setAttribute("cart", cart);
			mav.setViewName("kagoyume/cart");
			mav.addObject("total", total);
			mav.addObject("cart", cart);
			return mav;
		}
		return mav;
	}

	@RequestMapping(value = "/kagoyume/myhistory", method = RequestMethod.GET)
    public ModelAndView getHistory(ModelAndView  mav) {
		UserDataDTO ud = (UserDataDTO)session.getAttribute("userData");
		int userID = ud.getUserID();
		ArrayList<CartProductsDTO> history = userDetailsServiceImpl.getHistory(userID);

		//日本語マッピングした発送先をモデルにセット
		Map<String, String> typeMap = jumsHelper.TypeMappingToDisp();
		mav.addObject("typeMap", typeMap);

		session.setAttribute("history", history);
		mav.addObject("history", history);
		mav.setViewName("kagoyume/myhistory");
		return mav;
	}

	@RequestMapping(value = "/kagoyume/search", method = RequestMethod.GET)
    public ModelAndView searchProduct(@RequestParam("word") String query, ModelAndView  mav) {
		//queryがnullまたはブランクの場合、topへ遷移させる
		if(jumsHelper.isBrank(query)) {
			mav.setViewName("kagoyume/top");
			return mav;
		}
		ArrayList<ProductData> productList = new ArrayList<ProductData>();
		JsonNode json = null;
		int searchResultNum = 0;
		mav.addObject("query", query);
		try {
			json = yahooApi.searchByQuery(query);
			searchResultNum = yahooApi.getSearchCnt(json);
			if(searchResultNum==0) {
				mav.addObject("searchResultNum", searchResultNum);
				return mav;
			}
			productList = yahooApi.getSearchData(json);
			mav.setViewName("kagoyume/search");
			mav.addObject("productList", productList);
			mav.addObject("searchResultNum", searchResultNum);
			return mav;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mav;
	}



}
