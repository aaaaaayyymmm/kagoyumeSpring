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

import com.kagoyume.business.JumsHelper;
import com.kagoyume.business.ProductData;
import com.kagoyume.business.UserDataDTO;
import com.kagoyume.business.YahooApi;
import com.kagoyume.service.UserDetailsServiceImpl;


@Controller
public class CartController extends BaseController {
	@Autowired
	private UserDetailsServiceImpl userDetailsServiceImpl;
	@Autowired
	private JumsHelper jumsHelper;
	@Autowired
	private YahooApi yahooApi;

	@RequestMapping(value = "/kagoyume/item*", method = RequestMethod.GET)
    public ModelAndView getProductDetail(@RequestParam("id") String itemCode, ModelAndView  mav) {
		ProductData detail;
		try {
			detail = yahooApi.searchByItemCode(itemCode);
			mav.setViewName("kagoyume/item");
			mav.addObject("detail", detail);
			session.setAttribute("detail", detail);
			return mav;
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return mav;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/kagoyume/add", method = RequestMethod.POST)
    public ModelAndView addProduct(@RequestParam("proId") String proId, ModelAndView  mav) {
		ProductData pro = (ProductData)session.getAttribute("detail");

		ProductData product = new ProductData();
		product.setImageURL(pro.getImageURL());
		product.setProId(pro.getProId());
		product.setProName(pro.getProName());
		product.setPrice(pro.getPrice());
		List<ProductData> cart = new ArrayList<ProductData>();
		session = request.getSession(false);
		if(session.getAttribute("cart") != null) {
			cart = (List<ProductData>) session.getAttribute("cart");
		}
		cart.add(product);
		session.setAttribute("cart", cart);
		mav.addObject("cart", cart);
		mav.setViewName("kagoyume/add");
		return mav;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/kagoyume/deleteProduct", method = RequestMethod.GET)
    public ModelAndView deleteProduct(@RequestParam("deleteProduct") String deleteCode, ModelAndView  mav) {
		List<ProductData> cart = (ArrayList<ProductData>)session.getAttribute("cart");
		for (int cartnum =0 ; cartnum<=cart.size(); cartnum++) {
			String proid = cart.get(cartnum).getProId();
			if(deleteCode.equals(proid)) {
				cart.remove(cartnum);
				System.out.println("削除完了");
				break;
			}
		}
		int total = jumsHelper.calcTolal(cart);
		session.setAttribute("total", total);
		session.setAttribute("cart", cart);
		mav.setViewName("kagoyume/cart");
		mav.addObject("cart", cart);
		mav.addObject("total", total);
		return mav;
	}

	@RequestMapping(value = "/kagoyume/buyConfirm", method = RequestMethod.GET)
    public ModelAndView buyConfirm(ModelAndView  mav) {
		@SuppressWarnings("unchecked")
		List<ProductData> cart = (List<ProductData>)session.getAttribute("cart");
		int total = (int)session.getAttribute("total");
		Map<String,Integer> typeMap = jumsHelper.exTypenum();

		mav.addObject("cart", cart);
		mav.addObject("total", total);
		mav.addObject("typeMap", typeMap);
		mav.setViewName("kagoyume/buyConfirm");
		return mav;
	}

	@RequestMapping(value = "/kagoyume/buyComplete", method = RequestMethod.GET)
    public ModelAndView buyComplete(@RequestParam("type") int type, ModelAndView  mav) {
		//ユーザ情報を取得
		UserDataDTO ud = (UserDataDTO)session.getAttribute("userData");
		int userID = ud.getUserID();
		int oldTotal = ud.getTotal();
		int newTotal =  oldTotal + (int) session.getAttribute("total");

		//buy_tの更新
		@SuppressWarnings("unchecked")
		List<ProductData> carts = (List<ProductData>)session.getAttribute("cart");
		for(int i=0; i <carts.size(); i++) {
			ProductData pro = carts.get(i);
			//DTOオブジェクトにマッピング
			UserDataDTO cart = new UserDataDTO();
			cart.setUserID(userID);
			cart.setItemCode(pro.getProId());
			cart.setType(type);
			userDetailsServiceImpl.buyComplete(cart);
		}

		//user_tのtotalを更新
		userDetailsServiceImpl.updateUserTotal(newTotal, userID);
		//セッションの値を削除
		session.removeAttribute("cart");
		session.removeAttribute("total");
		mav.setViewName("kagoyume/buycomplete");
		return mav;
	}

}
