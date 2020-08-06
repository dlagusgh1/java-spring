package com.sbs.jhs.at.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sbs.jhs.at.dto.Article;
import com.sbs.jhs.at.dto.ArticleReply;
import com.sbs.jhs.at.service.ArticleService;

@Controller
public class ArticleController {
	@Autowired
	private ArticleService articleService;
	
	// 게시물 리스트	
	@RequestMapping("/article/list")
	public String showList(Model model, String page, String searchKeywordType, String searchKeyword) {
		
		if (page == null) {
			page = "1";
		} 
		
		int Spage = Integer.parseInt(page);
		
		int itemsInAPage = 5;
		int limitFrom = (Spage-1) * itemsInAPage;
		
		if (searchKeywordType != null && searchKeyword != null) {
			List<Article> articles = articleService.getForPrintSearchArticle(limitFrom, itemsInAPage, searchKeywordType, searchKeyword);
			
			int totalCount = articleService.getForPrintListSearchArticlesCount(searchKeywordType, searchKeyword);
			int totalPage = (int) Math.ceil(totalCount / (double) itemsInAPage);
			
			model.addAttribute("totalCount", totalCount);
			model.addAttribute("totalPage", totalPage);
			model.addAttribute("page", page);
			
			model.addAttribute("searchKeywordType", searchKeywordType);
			model.addAttribute("searchKeyword", searchKeyword);
			
			model.addAttribute("articles", articles);
			
			int pageCount = 5;
			int startPage = ((Spage - 1) / pageCount) * pageCount + 1;
			int endPage = startPage + pageCount - 1;
			
			if( totalPage < Spage) {
				Spage = totalPage;
			}
			if ( endPage > totalPage) {
				endPage = totalPage;
			}
			
			model.addAttribute("pageCount", pageCount);
			model.addAttribute("startPage", startPage);
			model.addAttribute("endPage", endPage);
			
			return "article/list";
		}
		
		int totalCount = articleService.getForPrintListArticlesCount();
		int totalPage = (int) Math.ceil(totalCount / (double) itemsInAPage);
		
		List<Article> articles = articleService.getForPrintArticles(limitFrom, itemsInAPage);
		
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("totalPage", totalPage);
		model.addAttribute("page", page);
		
		model.addAttribute("articles", articles);
		
		int pageCount = 5;
		int startPage = ((Spage - 1) / pageCount) * pageCount + 1;
		int endPage = startPage + pageCount - 1;
		
		if( totalPage < Spage) {
			Spage = totalPage;
		}
		if ( endPage > totalPage) {
			endPage = totalPage;
		}
		
		model.addAttribute("pageCount", pageCount);
		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);
		

		return "article/list";
	}
	
	// 게시물 상세보기
	@RequestMapping("/article/detail")
	public String showDetail(Model model, String page, int id) {
		
		if (page == null) {
			page = "1";
		} 
		
		int Spage = Integer.parseInt(page);
		
		int itemsInAPage = 5;
		int limitFrom = (Spage-1) * itemsInAPage;
		
		int totalCount = articleService.getForPrintListArticleRepliesCount(id);
		int totalPage = (int) Math.ceil(totalCount / (double) itemsInAPage);
		
		Article article = articleService.getForPrintArticle(id);
		
		List<ArticleReply> articleReplies = articleService.getForPrintArticleReplies(id, limitFrom, itemsInAPage);
		
		model.addAttribute("article", article);
		model.addAttribute("articleReplies", articleReplies);
		
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("totalPage", totalPage);
		model.addAttribute("page", page);
		
		int pageCount = 5;
		int startPage = ((Spage - 1) / pageCount) * pageCount + 1;
		int endPage = startPage + pageCount - 1;
		
		if( totalPage < Spage) {
			Spage = totalPage;
		}
		if ( endPage > totalPage) {
			endPage = totalPage;
		}
		
		model.addAttribute("pageCount", pageCount);
		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);
		
		return "article/detail";
	}
	
	@RequestMapping("article/getForPrintArticleRepliesRs")
	@ResponseBody
	public Map<String, Object> getForPrintArticleRepliesRs(String page, int id) {
		
		if (page == null) {
			page = "1";
		} 
		
		int Spage = Integer.parseInt(page);
		
		int itemsInAPage = 5;
		int limitFrom = (Spage-1) * itemsInAPage;
		
		int totalCount = articleService.getForPrintListArticleRepliesCount(id);
		int totalPage = (int) Math.ceil(totalCount / (double) itemsInAPage);
		
		List<ArticleReply> articleReplies = articleService.getForPrintArticleReplies(id, limitFrom, itemsInAPage);

		Map<String, Object> rs = new HashMap<>();
		
		rs.put("resultCode", "S-1");
		rs.put("msg", String.format("총 %d개의 댓글이 있습니다.", articleReplies.size()));
		rs.put("articleReplies", articleReplies);
		
		rs.put("totalCount", totalCount);
		rs.put("totalPage", totalPage);
		rs.put("page", page);
		
		int pageCount = 5;
		int startPage = ((Spage - 1) / pageCount) * pageCount + 1;
		int endPage = startPage + pageCount - 1;
		
		if( totalPage < Spage) {
			Spage = totalPage;
		}
		if ( endPage > totalPage) {
			endPage = totalPage;
		}
		
		rs.put("pageCount", pageCount);
		rs.put("startPage", startPage);
		rs.put("endPage", endPage);

		return rs;
	}
	
	// 게시물 작성 폼
	@RequestMapping("/article/write")
	public String write(Model model) {
		
		return "article/write";
	}
	
	// 게시물 작성
	@RequestMapping("/article/doWrite")
	public String doWrite(Model model, String title, String body) {
		
		articleService.write(title, body);
		
		String redirectUrl = "/article/list?page=1";

		model.addAttribute("locationReplace", redirectUrl);

		return "common/redirect";
	}
	
	// 게시물 삭제
	@RequestMapping("/article/delete")
	public String delete(Model model, int id) {
		
		articleService.delete(id);
		
		String redirectUrl = "/article/list?page=1";

		model.addAttribute("locationReplace", redirectUrl);

		return "common/redirect";
	}
	
	// 게시물 수정 폼
	@RequestMapping("/article/modify")
	public String modify(Model model, int id) {
		
		Article article = articleService.getForPrintArticle(id);
		
		model.addAttribute("article", article);
		
		return "article/modify";
	}
	
	// 게시물 수정 기능
	@RequestMapping("/article/doModify")
	public String doModify(Model model, int id, String title, String body) {
		
		articleService.modify(id, title, body);
		
		String redirectUrl = "/article/detail?id=" + id;

		model.addAttribute("locationReplace", redirectUrl);

		return "common/redirect";
	}
	
	// 댓글 작성 기능
	@RequestMapping("/article/doWriteReply")
	public String doWriteReply(Model model, @RequestParam Map<String, Object> param, HttpServletRequest request) {
		
		Map<String, Object> rs = articleService.writeReply(param);
		
		String msg = (String) rs.get("msg");
		String redirectUrl = (String) param.get("redirectUrl");

		model.addAttribute("alertMsg", msg);
		model.addAttribute("locationReplace", redirectUrl);

		return "common/redirect";
	}
	
	@RequestMapping("article/doWriteReplyAjax")
	@ResponseBody
	public Map<String, Object> doWriteReplyAjax(@RequestParam Map<String, Object> param, HttpServletRequest request) {
		
		Map<String, Object> rs = articleService.writeReply(param);
		
		return rs;
	}
	
	// 댓글 삭제
	@RequestMapping("/article/replyDelete")
	public String replyDelete(Model model, int articleId, int articleReplyId) {
		
		articleService.replyDelete(articleId, articleReplyId);
		
		String redirectUrl = "/article/detail?id=" + articleId;

		model.addAttribute("locationReplace", redirectUrl);

		return "common/redirect";
	}
	
	// 게시물 수정 폼
	@RequestMapping("/article/replyModify")
	public String replyModify(Model model, int articleId, int articleReplyId) {
		
		ArticleReply articleReply = articleService.getForPrintArticleReply(articleId, articleReplyId);	
		
		model.addAttribute("articleId", articleId);
		model.addAttribute("articleReplyId", articleReplyId);
		model.addAttribute("articleReply", articleReply);
		
		return "article/replyModify";
	}
	
	// 게시물 수정 기능
	@RequestMapping("/article/doReplyModify")
	public String doReplyModify(Model model, int articleId, int articleReplyId, String body) {
		
		articleService.replyModify(articleId, articleReplyId, body);
		
		String redirectUrl = "/article/detail?id=" + articleId;

		model.addAttribute("locationReplace", redirectUrl);

		return "common/redirect";
	}
}
