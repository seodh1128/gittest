package controllers;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import dao.NoticeDao;
import vo.Notice;


// 6~8강 - 어노테이션을 활용한 컨트롤러
// view단 Return 페이지 기준 정의 : Customer. + @RequestMapping에 세팅된 페이지명

@Controller
@RequestMapping("/customer/*")
public class CustomerController {
	
	private NoticeDao noticeDao;
	
	@Autowired
	public void setNoticeDao(NoticeDao noticeDao) {
		this.noticeDao = noticeDao;
	}
	
	
	
	@RequestMapping("notice.htm")
	public String notices(String page, String field, String query, Model model) throws ClassNotFoundException, SQLException{
		// // : 컴파일러를 거치면 코드에서 삭제되는 주석
		// @ : 컴파일러를 거쳐도 코드에 남아있는 주석
		// 컨트롤러의 역활을 할 수 있으면서 view단으로 전달이 가능한 메소드
		// 모델정보와 뷰 정보를 담아서 보내주는 메소드
	
		
		// 페이지 설정
		int pg = 1;
		// 데이터를 보낼 변수 선언
		String f = "TITLE";
		String q = "%%";
				
		// 페이지,필드,검색 내용이 비어있거나 공백인지 체크한다.
		if(page != null && !page.equals(""))
			pg = Integer.parseInt(page);
		if(field != null && !field.equals(""))
			f = field;		
		if(query != null && !query.equals(""))
			q = query;
		
				
		//NoticeDao dao = new NoticeDao();
		// 사용자의 요구사항을 전달받음
		//List<Notice> list = dao.getNotices(1, field , query);
		// 화면에 뿌려줘야 할 모델
		List<Notice> list = noticeDao.getNotices(pg, f , q);
		
		// 레코드 개수 파악하여 모델 함수에 추가시켜 준다.
		model.addAttribute("count", noticeDao.getCount(f, q));
				
		model.addAttribute("list", list);
				
		
		//반환은 주소만 반환해주면 된다.
		//7장에서 변경됨 - jsp -> tile-def에서 정의해둔 tile명을 입력해 준다.
		return "customer.notice";
	}
	
	@RequestMapping("noticeDetail.htm")
	public String noticeDetail(String seq, Model model) throws ClassNotFoundException, SQLException{
		
		//제공받은 seq를 가져와서 Notice에 입력해준다.
		Notice notice = noticeDao.getNotice(seq);
		
		//return할 Model데이터와 View데이타 선언
		model.addAttribute("notice",notice);
		//View값 및 Model(notice)에 주입할 값 주입

		return "customer.noticeDetail";
	}
	
	//게시글 등록
	// 게시글 등록 화면으로 이동하는 메소드 - get방식
	@RequestMapping(value={"noticeReg.htm"}, method=RequestMethod.GET)
	public String noticeReg(){
		return "customer.noticeReg";
	}
	// 게시글 등록 후 확정하는 메소드 - post방식
	@RequestMapping(value={"noticeReg.htm"}, method=RequestMethod.POST)
	public String noticeReg(Notice n, HttpServletRequest request) throws ClassNotFoundException, SQLException, IOException{


			// 파일을 등록하지 않았을 경우 실행하지 않도록 if문으로 공백 여부 확인
			if(!n.getFile().isEmpty())
			{
			//1. 업로드한 파일명 및 업로드할 경로를 기록
			String path = request.getServletContext().getRealPath("/customer/upload");
			String filename = n.getFile().getOriginalFilename();
			String filepath = path+"\\"+filename;
			
			//2. 업로드할 파일을 해당 경로에 전송
			FileOutputStream fileStream = new FileOutputStream(filepath);
			fileStream.write(n.getFile().getBytes());
			fileStream.close();
			
			//3. 업로드한 파일명을 DB에 저장해야 한다.
			
			//Notice 하나의 Notice에 여러개의 게시글을 담을 경우
			/*NoticeFile nf = new NoticeFile();
			nf.setNoticeSeq(n.getSeq());
			nf.setFileSrc(filename);
			noticeFileDao.insert(nf);*/
			
			n.setFileSrc(filename);
			}
		//noticeDao.insert(n);
		noticeDao.insertAndPointUpOfMember(n, "newlec");
		//트랜젝션 : 게시글 작성시 Memeber의 Point 증가 - 구현을 위한 메소드를 추가시켜줌
		
		return "redirect:notice.htm";
	}
	
	// 게시글 수정
	// 게시글 수정 화면으로 이동하는 메소드 - get방식
	// 게시글 수정에는 해당되는 게시글 번호(키값-seq)가 필요하므로, seq를 받아야 한다.
	@RequestMapping(value={"noticeEdit.htm"}, method=RequestMethod.GET)
	public String noticeEdit(String seq, Model model) throws ClassNotFoundException, SQLException{
		
		Notice notice = noticeDao.getNotice(seq);
		model.addAttribute("notice", notice);
		return "customer.noticeEdit";
	}
	// 게시글 업데이트에는 Notice(request 받은 데이터)
	@RequestMapping(value={"noticeEdit.htm"}, method=RequestMethod.POST)
	public String noticeEdit(Notice n,HttpServletRequest request) throws ClassNotFoundException, SQLException, IOException{
		
		/*Notice n = new Notice();
		n.setTitle(title);
		n.setContent(content);*/
		String path = request.getServletContext().getRealPath("/customer/upload");
		String filename = n.getFile().getOriginalFilename();
		String filepath = path+"\\"+filename;
		
		//2. 업로드할 파일을 해당 경로에 전송
		FileOutputStream fileStream = new FileOutputStream(filepath);
		fileStream.write(n.getFile().getBytes());
		fileStream.close();
		noticeDao.update(n);
		return "redirect:notice.htm?seq="+n.getSeq();
	}
	
	@RequestMapping("noticeDel.htm")
	public String noticeDel(String seq) throws ClassNotFoundException, SQLException{
		noticeDao.delete(seq);
		
		return "redirect:notice.htm";
	}

	@RequestMapping("download.htm")
	public void download(String path, String file,HttpServletRequest request ,HttpServletResponse response) throws IOException{
		System.out.println(file);
		// Get방식이기 때문에 POST방식에 적용한 필터가 미적용되어 한글파일명이 깨진다.
		
		// 파일명을 UTF-8로 바꿔준다.
		/*String fileName = new String(file.getBytes(), "utf-8");
		System.out.println(fileName);*/
		
		// Header에는 UTF-8로 들어가면 안되므로, 다시 ISO8859_1로 재변환한다.
		response.setHeader("Content-Disposition", 
				"attachent;filename="+new String(file.getBytes(), "ISO8859_1"));
		
		String fullPath = request.getServletContext().getRealPath( path + "/" + file);
	
		FileInputStream fileInput = new FileInputStream(fullPath);
		ServletOutputStream sout = response.getOutputStream();
		
		byte[] buf = new byte[1024];
		int size = 0;
		
		while((size = fileInput.read(buf,0,1024)) != -1){
			sout.write(buf, 0, size);
		}
		fileInput.close();
		sout.close();
		
	}
	
}
