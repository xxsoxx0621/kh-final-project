package kh.spring.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import kh.spring.dto.BoardDTO;
import kh.spring.dto.CommentDTO;
import kh.spring.service.BoardService;
import kh.spring.service.CommentService;

@Controller
@RequestMapping("/board/")
public class BoardController {
	
	@Autowired
	public BoardService service;
	
	@Autowired
	public CommentService cservice;
	
	@Autowired
	private HttpSession session;
	
	@RequestMapping("boardList")
	public String boardList(Model model) {
		System.out.println("boardList 로 들어온 요청은 이 메서드를 실행합니다.");

		List<BoardDTO> list = service.selectAll();
		model.addAttribute("list", list);
		return "board/boardList";
	}
	
	@RequestMapping("writeForm")
	public String writeForm() {
		System.out.println("writeForm 로 들어온 요청은 이 메서드를 실행합니다.");

		return "board/writeForm";
	}

	@RequestMapping("toDetail")
	public String toDetail(int seq, Model model) {
		System.out.println("toDetail 로 들어온 요청은 이 메서드를 실행합니다.");

		BoardDTO dto = service.selectBySeq(seq);
		model.addAttribute("dto", dto);
		
		List<CommentDTO> list = cservice.selectBySeq(dto.getBoard_seq());
		model.addAttribute("list", list);
		
//		List<FilesDTO> list = fservice.selectBySeq(dto.getSeq());
//		model.addAttribute("list", list);
		
		return "board/boardDetail";
	}

}
