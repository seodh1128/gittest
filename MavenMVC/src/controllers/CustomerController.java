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


// 6~8�� - ������̼��� Ȱ���� ��Ʈ�ѷ�
// view�� Return ������ ���� ���� : Customer. + @RequestMapping�� ���õ� ��������

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
		// // : �����Ϸ��� ��ġ�� �ڵ忡�� �����Ǵ� �ּ�
		// @ : �����Ϸ��� ���ĵ� �ڵ忡 �����ִ� �ּ�
		// ��Ʈ�ѷ��� ��Ȱ�� �� �� �����鼭 view������ ������ ������ �޼ҵ�
		// �������� �� ������ ��Ƽ� �����ִ� �޼ҵ�
	
		
		// ������ ����
		int pg = 1;
		// �����͸� ���� ���� ����
		String f = "TITLE";
		String q = "%%";
				
		// ������,�ʵ�,�˻� ������ ����ְų� �������� üũ�Ѵ�.
		if(page != null && !page.equals(""))
			pg = Integer.parseInt(page);
		if(field != null && !field.equals(""))
			f = field;		
		if(query != null && !query.equals(""))
			q = query;
		
				
		//NoticeDao dao = new NoticeDao();
		// ������� �䱸������ ���޹���
		//List<Notice> list = dao.getNotices(1, field , query);
		// ȭ�鿡 �ѷ���� �� ��
		List<Notice> list = noticeDao.getNotices(pg, f , q);
		
		// ���ڵ� ���� �ľ��Ͽ� �� �Լ��� �߰����� �ش�.
		model.addAttribute("count", noticeDao.getCount(f, q));
				
		model.addAttribute("list", list);
				
		
		//��ȯ�� �ּҸ� ��ȯ���ָ� �ȴ�.
		//7�忡�� ����� - jsp -> tile-def���� �����ص� tile���� �Է��� �ش�.
		return "customer.notice";
	}
	
	@RequestMapping("noticeDetail.htm")
	public String noticeDetail(String seq, Model model) throws ClassNotFoundException, SQLException{
		
		//�������� seq�� �����ͼ� Notice�� �Է����ش�.
		Notice notice = noticeDao.getNotice(seq);
		
		//return�� Model�����Ϳ� View����Ÿ ����
		model.addAttribute("notice",notice);
		//View�� �� Model(notice)�� ������ �� ����

		return "customer.noticeDetail";
	}
	
	//�Խñ� ���
	// �Խñ� ��� ȭ������ �̵��ϴ� �޼ҵ� - get���
	@RequestMapping(value={"noticeReg.htm"}, method=RequestMethod.GET)
	public String noticeReg(){
		return "customer.noticeReg";
	}
	// �Խñ� ��� �� Ȯ���ϴ� �޼ҵ� - post���
	@RequestMapping(value={"noticeReg.htm"}, method=RequestMethod.POST)
	public String noticeReg(Notice n, HttpServletRequest request) throws ClassNotFoundException, SQLException, IOException{


			// ������ ������� �ʾ��� ��� �������� �ʵ��� if������ ���� ���� Ȯ��
			if(!n.getFile().isEmpty())
			{
			//1. ���ε��� ���ϸ� �� ���ε��� ��θ� ���
			String path = request.getServletContext().getRealPath("/customer/upload");
			String filename = n.getFile().getOriginalFilename();
			String filepath = path+"\\"+filename;
			
			//2. ���ε��� ������ �ش� ��ο� ����
			FileOutputStream fileStream = new FileOutputStream(filepath);
			fileStream.write(n.getFile().getBytes());
			fileStream.close();
			
			//3. ���ε��� ���ϸ��� DB�� �����ؾ� �Ѵ�.
			
			//Notice �ϳ��� Notice�� �������� �Խñ��� ���� ���
			/*NoticeFile nf = new NoticeFile();
			nf.setNoticeSeq(n.getSeq());
			nf.setFileSrc(filename);
			noticeFileDao.insert(nf);*/
			
			n.setFileSrc(filename);
			}
		//noticeDao.insert(n);
		noticeDao.insertAndPointUpOfMember(n, "newlec");
		//Ʈ������ : �Խñ� �ۼ��� Memeber�� Point ���� - ������ ���� �޼ҵ带 �߰�������
		
		return "redirect:notice.htm";
	}
	
	// �Խñ� ����
	// �Խñ� ���� ȭ������ �̵��ϴ� �޼ҵ� - get���
	// �Խñ� �������� �ش�Ǵ� �Խñ� ��ȣ(Ű��-seq)�� �ʿ��ϹǷ�, seq�� �޾ƾ� �Ѵ�.
	@RequestMapping(value={"noticeEdit.htm"}, method=RequestMethod.GET)
	public String noticeEdit(String seq, Model model) throws ClassNotFoundException, SQLException{
		
		Notice notice = noticeDao.getNotice(seq);
		model.addAttribute("notice", notice);
		return "customer.noticeEdit";
	}
	// �Խñ� ������Ʈ���� Notice(request ���� ������)
	@RequestMapping(value={"noticeEdit.htm"}, method=RequestMethod.POST)
	public String noticeEdit(Notice n,HttpServletRequest request) throws ClassNotFoundException, SQLException, IOException{
		
		/*Notice n = new Notice();
		n.setTitle(title);
		n.setContent(content);*/
		String path = request.getServletContext().getRealPath("/customer/upload");
		String filename = n.getFile().getOriginalFilename();
		String filepath = path+"\\"+filename;
		
		//2. ���ε��� ������ �ش� ��ο� ����
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
		// Get����̱� ������ POST��Ŀ� ������ ���Ͱ� ������Ǿ� �ѱ����ϸ��� ������.
		
		// ���ϸ��� UTF-8�� �ٲ��ش�.
		/*String fileName = new String(file.getBytes(), "utf-8");
		System.out.println(fileName);*/
		
		// Header���� UTF-8�� ���� �ȵǹǷ�, �ٽ� ISO8859_1�� �纯ȯ�Ѵ�.
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
