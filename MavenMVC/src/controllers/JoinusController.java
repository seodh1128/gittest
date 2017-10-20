package controllers;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import dao.MemberDao;
import vo.Member;

@Controller
@RequestMapping("/joinus/*")
public class JoinusController {
	
private MemberDao memberDao;
	
	@Autowired
	public void setMemberDao(MemberDao memberDao){
		this.memberDao = memberDao;
	}
	
	@RequestMapping(value={"join.htm"}, method=RequestMethod.GET)
	public String join(){
		
		return "joinus.join";
		
	}
	@RequestMapping(value={"join.htm"}, method=RequestMethod.POST)
	public String join(Member member) throws ClassNotFoundException, SQLException{
		
		memberDao.insert(member);
		
		return "redirect:../index.jsp";
	}
	
	@RequestMapping(value={"login.htm"}, method=RequestMethod.GET)
	public String login(){
		
		return "joinus.login";
		
	}
	@RequestMapping(value={"login.htm"}, method=RequestMethod.POST)
	public String login(Member member) throws ClassNotFoundException, SQLException{
		
		memberDao.insert(member);
		
		return "redirect:../index.jsp";
	}
}
