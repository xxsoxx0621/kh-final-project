package kh.spring.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;

import kh.spring.dao.SearchKeywordDAO;
import kh.spring.dto.FilesDTO;
import kh.spring.dto.ItemsDTO;
import kh.spring.dto.MemberDTO;
import kh.spring.dto.SearchKeywordDTO;
import kh.spring.service.FilesService;
import kh.spring.service.ItemsService;
import kh.spring.service.MemberService;


@Controller
@RequestMapping(value= {"/items/","/"})
public class ItemsController {

	@Autowired
	public ItemsService iservice;

	@Autowired
	public FilesService fservice;

	@Autowired
	public SearchKeywordDAO sdao;

	@Autowired
	HttpSession session;

	@Autowired
	public MemberService mservice;


	@RequestMapping("")	
	public String home(Model model) {


		List<ItemsDTO>ilist =iservice.selectAll();

		model.addAttribute("ilist",ilist);

		List<FilesDTO>flist =fservice.selectAll();
		//		

		model.addAttribute("flist",flist);

	

		return "/items/index";
	}

	//	상품등록

	@RequestMapping("itemsUpload")
	public String itemsUpload(ItemsDTO dto, MultipartFile[] file) throws Exception {



		System.out.println(dto.getIseq());

		String sellerID = (String)session.getAttribute("loginID");

		dto.setSellerID(sellerID);		




		int parentSeq = iservice.insert(dto);



		for(MultipartFile mf : file) {
			if(!mf.isEmpty()) { //  업로드 된 파일 중 첫번째 파일이 비어있지 않다면,
				String realPath = session.getServletContext().getRealPath("");
				File realPathFile = new File(realPath);
				if(!realPathFile.exists()) {realPathFile.mkdir();}


				String oriName = mf.getOriginalFilename(); //클라이언트에게 보여주는 목적 - 사용자가 업로드 한 파일의 원본 이름
				String sysName = "/upload/"+UUID.randomUUID()+"_"+oriName; // 절대 겹치지 않는 무작위의 이름 - 서버쪽에 저장할 파일 이름


				// 서버에 업로드되어 메모리에 적재된 파일의 내용을 어디에 저장할지 결정하는 부분
				mf.transferTo(new File(realPath+"/"+sysName));
				fservice.insert(new FilesDTO(0,oriName,sysName,parentSeq)); //첨부된 파일 정보를 DB에 저장하는 부분
			}

			//			dto.setItems_img1();

		}
		/* service.insert(dto,file); */
		//		마이페이지로 이동예정^^

		return "redirect:/items/";

	}



	// 아이템 판매내역- 수정하기 눌렀을시에 



	// 아이템 등록시  정보를 띄워주기 위한 mapping 

	@RequestMapping("itemsModify")
	public String itemsModify(int iseq,MultipartFile[] file,Model model)throws Exception {

		//		seq 받아와서 상품  찾기
		List<ItemsDTO> ilist =iservice.selectBySeq(iseq);
		//		seq 받아와서 사진  찾기
		List<FilesDTO> flist = fservice.selectBySeq(iseq);


		model.addAttribute("ilist",ilist);
		model.addAttribute("flist",flist);

		String id =(String)session.getAttribute("loginID");
		model.addAttribute("loginID",id);

		return "/items/itemsModify";

	}
	//아이템 수정시 proc

	//	@RequestMapping("itemsModifyProc")
	//	public String itemsModifyProc(int iseq,MultipartFile[] file,Model model)throws Exception {
	//		
	////		seq 받아와서 상품  찾기
	//		List<ItemsDTO> ilist =iservice.selectBySeq(iseq);
	//		
	//		int result = iservice.itemsModifyProc(ilist);
	//		System.out.println(result);
	//		
	////		seq 받아와서 사진  찾기
	//		System.out.println(file.length);
	////		List<FilesDTO> flist = fservice.selectBySeq(iseq);
	//		
	//		
	////		model.addAttribute("ilist",ilist);
	////		model.addAttribute("flist",flist);
	////		
	//		String id =(String)session.getAttribute("loginID");
	//		model.addAttribute("loginID",id);
	//	
	//		return "/";
	//		
	//	}





	@RequestMapping("itemsModifyProc")

	public String itemsModifyProc(ItemsDTO dto,MultipartFile[] file,Model model)throws Exception {


		//		상품번호 추출
		int iseq = dto.getIseq();
		
		 

		//		ilist 는 dto(받아온 값으로) 수정해야 하지만.
		//		flist 는 그대로 쓰면된다.
		// 해당 상품과 이미지들
		System.out.println(dto.getCondition());
		System.out.println(dto.getName());

		int resultI =iservice.itemsModifyProc(dto);
		//사진은 지워야함 업데이트하고 (delete & update) parentSeq 에서
//		List<FilesDTO> flist = fservice.selectBySeq(iseq);

		System.out.println("업데이트 완료 : " + resultI);
		
		String id =(String)session.getAttribute("loginID");

		model.addAttribute("loginID",id);

//		int parentSeq = iservice.insert(dto);
		
		System.out.println("입력파일 길이   : " +file.toString());

		
// file.length 1아니면 지웠다 다시만들기.
		//파일 다시 넣기


		return "redirect:/myPage";		
	}
	 //		for(MultipartFile mf : file) {
	//			if(!mf.isEmpty()) { //  업로드 된 파일 중 첫번째 파일이 비어있지 않다면,
	//				String realPath = session.getServletContext().getRealPath("");
	//				File realPathFile = new File(realPath);
	//				if(!realPathFile.exists()) {realPathFile.mkdir();}
	//
	//
	//				String oriName = mf.getOriginalFilename(); //클라이언트에게 보여주는 목적 - 사용자가 업로드 한 파일의 원본 이름
	//				String sysName = "/upload/"+UUID.randomUUID()+"_"+oriName; // 절대 겹치지 않는 무작위의 이름 - 서버쪽에 저장할 파일 이름
	//
	//
	//				// 서버에 업로드되어 메모리에 적재된 파일의 내용을 어디에 저장할지 결정하는 부분
	//				mf.transferTo(new File(realPath+"/"+sysName));
	//				fservice.insert(new FilesDTO(0,oriName,sysName,parentSeq)); //첨부된 파일 정보를 DB에 저장하는 부분
	//			}
	//		}
	//

	//		return "/items/index";
	//	}



	// 아이템 수정후에 update.do 될 mapping



	//	아이템 상세페이지 하나 클릭했을때
	@RequestMapping("itemsDetail")

	public String itemsDetail(int iseq ,Model model) {

		// 해당 상품과 이미지들
		List<ItemsDTO> ilist =iservice.selectBySeq(iseq);
		List<FilesDTO> flist = fservice.selectBySeq(iseq);

		model.addAttribute("ilist",ilist);
		model.addAttribute("flist",flist);

		//	관련 상품과 이미지들 (rlist = related item list)
		//상품 한개이므로 get (0) 또한 해당상품은 연관상품에 안떠야하니까 iseq 가져간다.
		String targetStr =ilist.get(0).getName(); 
		/* System.out.println(targetStr); */

		String[] str = targetStr.split(" ");



		List<String> targetList = new ArrayList<>();

		// 상품클릭시 이름값으로 띄어쓰기 구분해서 담음	
		for(int i=0; i< str.length; i++) {
			targetList.add(str[i]);
		}

		//관련상품 이름 뽑기
		List<ItemsDTO> rilist = iservice.selectByName(targetList,iseq);

		System.out.println(rilist.size());
		// 관련상품 이미지 뽑기
		List<ItemsDTO> NameToSeq = iservice.selectNameToSeq(rilist);
		/*
		 * for(int i=0 ; i<NameToSeq.size();i++) {
		 * 
		 * System.out.println(NameToSeq.get(i).getName()); }
		 */


		List<FilesDTO> rflist = fservice.selectBySeqR(NameToSeq);

		for(int i=0 ; i<rflist.size();i++) {

			System.out.println(rflist.get(i).getSysName()); }
		System.out.println(rflist.size());

		model.addAttribute("rilist",rilist);
		model.addAttribute("rflist",rflist);

		String id =(String)session.getAttribute("loginID");
		model.addAttribute("loginID",id);
		
		return "/items/itemsDetail";

	}






	//	상품등록시 이동 

	@RequestMapping("itemsSell")

	public String itemsSell(Model model) {

		String id =(String)session.getAttribute("loginID");

		model.addAttribute(id);

		return "items/itemsSell";

	}




	//
	@RequestMapping("itemsList")

	public String itemsList() {

		return "/items/itemsList";
	}






	//최근 검색어 data 넣기

	
	
//최근 검색어 data 넣기

	@RequestMapping("searchByInput")	
	public String input(SearchKeywordDTO dto,Model model) {

		//검색 목록에 값 저장
		sdao.insert(dto);



		return "items/index";
	}


	//최근검색어 삭제
	@ResponseBody
	@RequestMapping(value ="deleteAll", produces="text/html; charset=utf8")
	public String deleteAll() throws Exception{

		int result =sdao.deleteAll();
		return String.valueOf(result);

	}

	//최근검색어 보여주기
	@ResponseBody
	@RequestMapping(value ="listing", produces="text/html; charset=utf8")
	public String listing() throws Exception{

		List<SearchKeywordDTO> slist = sdao.selectAll();

		Gson gson = new Gson();

		String listJson = gson.toJson(slist, List.class).toString();

		/* System.out.println(listJson); */
		return listJson;



	}

	@ResponseBody
	@RequestMapping(value ="deleteByKeyword", produces="text/html; charset=utf8")
	public String deleteByKeyword(String keyword) throws Exception{

		int result = sdao.deleteByKeyword(keyword);


		return String.valueOf(result);


	}


	//메인화면 홈-상품전체 끌고오기



	@RequestMapping("signIn")
	public String login() {

		return "/member/signIn";
	}

	@RequestMapping("join")
	public String join() {

		return "/member/join";
	}

	//	memberController 에서 회원정보(dto) 와 가입일(SignDate) 가져옴
	//마이페이지 이동기능
	@RequestMapping("myPage")
	public String mypage(Model model) {
		String id = (String)session.getAttribute("loginID");
		MemberDTO dto = mservice.select(id);
		//가입한지 몇일 째인지 확인
		int signDate = mservice.signDate(id);
		model.addAttribute("signDate",signDate);
		model.addAttribute("dto", dto);

		//해당 id 의 상품목록들 가져오기
		List<ItemsDTO> ilist = iservice.selectMineById(id);
		//해당 상품목록의 사진들도 가져오기
		List<FilesDTO> flist = fservice.selectMineById(id);

		//판매내역 건수 보내기

		int sellCount = iservice.sellCount(id);

		model.addAttribute("ilist",ilist);
		model.addAttribute("flist",flist);
		model.addAttribute("sellCount",sellCount);
		return "/member/myPage";

	}
	//	상품의 상태 예약중/ 판매완료/판매중
	@ResponseBody
	@RequestMapping(value="updateProc", produces="text/html; charset=utf8")
	public String updateProc(String piseq,String pdeal) {

		int iseq = Integer.parseInt(piseq); 

		String deal = pdeal;
		System.out.println(iseq + ":" + deal);

		int result = iservice.updateProc( deal, iseq);

		return "update complete";


	}


	//		@RequestMapping("itemsDetail")
	//		public String itemsDetail(int iseq ,Model model) {
	//			
	//	// 해당 상품과 이미지들
	//			List<ItemsDTO> ilist =iservice.selectBySeq(iseq);
	//		List<FilesDTO> flist = fservice.selectBySeq(iseq);
	//		
	//		model.addAttribute("ilist",ilist);
	//		model.addAttribute("flist",flist);

	@RequestMapping("findInfo")
	public String findInfo() {

		return "/member/findInfo";
	}
	@RequestMapping("talk")
	public String talk() {

		return "/talk/talk";
	}


	//	상품등록시 이동 
	

	
	@RequestMapping("itemsDetailExam")
	public String itemsDetail2() {
		return "/items/itemsDetailExam";

	}
	
	
	@RequestMapping("itemsOrder")
	public String itemsOrder() {
		return "/items/itemsOrder";

	}



}
