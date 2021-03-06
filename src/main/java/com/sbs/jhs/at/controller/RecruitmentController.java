package com.sbs.jhs.at.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sbs.jhs.at.dto.Recruitment;
import com.sbs.jhs.at.dto.Job;
import com.sbs.jhs.at.dto.Member;
import com.sbs.jhs.at.dto.ResultData;
import com.sbs.jhs.at.service.RecruitmentService;
import com.sbs.jhs.at.util.Util;

@Controller
public class RecruitmentController {
	@Autowired
	private RecruitmentService recruitmentService;

	@RequestMapping("/usr/recruitment/{jobCode}-list")
	public String showList(Model model, @PathVariable("jobCode") String jobCode) {
		Job job = recruitmentService.getJobByCode(jobCode);
		model.addAttribute("job", job);

		List<Recruitment> recruitments = recruitmentService.getForPrintRecruitments();

		model.addAttribute("recruitments", recruitments);

		return "recruitment/list";
	}

	@RequestMapping("/usr/recruitment/{jobCode}-detail")
	public String showDetail(Model model, @RequestParam Map<String, Object> param, HttpServletRequest req, @PathVariable("jobCode") String jobCode, String listUrl) {
		if ( listUrl == null ) {
			listUrl = "./" + jobCode + "-list";
		}
		model.addAttribute("listUrl", listUrl);

		Job job = recruitmentService.getJobByCode(jobCode);
		model.addAttribute("job", job);

		int id = Integer.parseInt((String) param.get("id"));

		Member loginedMember = (Member)req.getAttribute("loginedMember");

		Recruitment recruitment = recruitmentService.getForPrintRecruitmentById(loginedMember, id);

		model.addAttribute("recruitment", recruitment);

		return "recruitment/detail";
	}

	@RequestMapping("/usr/recruitment/{jobCode}-modify")
	public String showModify(Model model, @RequestParam Map<String, Object> param, HttpServletRequest req, @PathVariable("jobCode") String jobCode, String listUrl) {
		model.addAttribute("listUrl", listUrl);

		Job job = recruitmentService.getJobByCode(jobCode);
		model.addAttribute("job", job);

		int id = Integer.parseInt((String) param.get("id"));

		Member loginedMember = (Member)req.getAttribute("loginedMember");
		Recruitment recruitment = recruitmentService.getForPrintRecruitmentById(loginedMember, id);

		model.addAttribute("recruitment", recruitment);

		return "recruitment/modify";
	}

	@RequestMapping("/usr/recruitment/{jobCode}-write")
	public String showWrite(@PathVariable("jobCode") String jobCode, Model model, String listUrl) {
		if ( listUrl == null ) {
			listUrl = "./" + jobCode + "-list";
		}
		model.addAttribute("listUrl", listUrl);
		Job job = recruitmentService.getJobByCode(jobCode);
		model.addAttribute("job", job);

		return "recruitment/write";
	}

	@RequestMapping("/usr/recruitment/{jobCode}-doModify")
	public String doModify(@RequestParam Map<String, Object> param, HttpServletRequest req, int id, @PathVariable("jobCode") String jobCode, Model model) {
		Job job = recruitmentService.getJobByCode(jobCode);
		model.addAttribute("job", job);
		Map<String, Object> newParam = Util.getNewMapOf(param, "title", "body", "fileIdsStr", "recruitmentId", "id");
		Member loginedMember = (Member)req.getAttribute("loginedMember");

		ResultData checkActorCanModifyResultData = recruitmentService.checkActorCanModify(loginedMember, id);

		if (checkActorCanModifyResultData.isFail() ) {
			model.addAttribute("historyBack", true);
			model.addAttribute("msg", checkActorCanModifyResultData.getMsg());

			return "common/redirect";
		}

		recruitmentService.modify(newParam);

		String redirectUri = (String) param.get("redirectUri");

		return "redirect:" + redirectUri;
	}

	@RequestMapping("/usr/recruitment/{jobCode}-doWrite")
	public String doWrite(@RequestParam Map<String, Object> param, HttpServletRequest req, @PathVariable("jobCode") String jobCode, Model model) {
		Job job = recruitmentService.getJobByCode(jobCode);
		model.addAttribute("job", job);

		Map<String, Object> newParam = Util.getNewMapOf(param, "title", "body", "fileIdsStr");
		int loginedMemberId = (int)req.getAttribute("loginedMemberId");
		newParam.put("jobId", job.getId());
		newParam.put("memberId", loginedMemberId);
		int newRecruitmentId = recruitmentService.write(newParam);

		String redirectUri = (String) param.get("redirectUri");
		redirectUri = redirectUri.replace("#id", newRecruitmentId + "");

		return "redirect:" + redirectUri;
	}
}