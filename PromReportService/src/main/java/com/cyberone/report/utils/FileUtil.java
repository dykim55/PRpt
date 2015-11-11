package com.cyberone.report.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.cyberone.report.exception.BizException;

public class FileUtil {

	private static Logger logger = LoggerFactory.getLogger(FileUtil.class);
	
	/**
	 * [설명] 파일업로드
	 * 
	 * @param file - MultipartFile 객체를 넘겨받음
	 * @param uploadPath - upload Path
	 * @param over - 업로드 파일명과기존파일명 중복시 덮어쓸지 여부(덮어쓸시 true)
	 * @return
	 * @throws IOException 
	 */
	public static String writeFile(MultipartFile multiFile, String uploadPath, boolean over) throws  IOException {
		int i = 0;
		
		String orgFileNeme	= multiFile.getOriginalFilename();	// 원본파일명
		String saveFileNeme 	= "";	// 저장파일명 - 파일명 변화(현재날짜시간 + 확장자 )
		
//		String[] arrFileNeme = orgFileNeme.split("\\.");	// 파일명
		String[] arrFileNeme = new String[2];
		
		arrFileNeme[0] = orgFileNeme.substring(0, orgFileNeme.lastIndexOf("."));
		arrFileNeme[1] = orgFileNeme.substring(orgFileNeme.lastIndexOf(".")+1, orgFileNeme.length());
		
		FileOutputStream fos = null;
		
		// upload 디렉토리존재확인
		File file = new File(uploadPath);
		if ( !file.exists() ) file.mkdirs();	// 미존재시 디렉토리 생성

		// 확장자 체크 = > file.getOriginalFilename()
		if (!FileUtil.isFileExtCheck(arrFileNeme[1].toLowerCase())) throw new BizException("일시적인 시스템장애로 접근하실 수 없습니다.");
		
		// 특수문자 or '%00', '%zz' 체크 => file.getOriginalFilename()
		if (isSpecial(orgFileNeme)) throw new BizException("일시적인 시스템장애로 접근하실 수 없습니다.");

		try {
			arrFileNeme[0]	= DateUtil.getTime("yyyyMMddHHmmss");
			saveFileNeme 	= arrFileNeme[0] + "." + arrFileNeme[1];	// 저장파일명 - 파일명 변화(현재날짜시간 + 확장자 )
			
			i = 1;
			// 같은 파일명 존재시 덮어쓸지 여부에 따라 파일명 변경
			if (!over) {
				while (existsFile(uploadPath, saveFileNeme)) {
					saveFileNeme = arrFileNeme[0] + "_"  + (i++) + "." + arrFileNeme[1];
				}
			}

			// fos = new FileOutputStream(uploadPath + "\\" + n_file);
			fos = new FileOutputStream(uploadPath + saveFileNeme);
			fos.write(multiFile.getBytes());
		} catch (Exception e) {
			throw new BizException("일시적인 시스템장애로 접근하실 수 없습니다.", e);
		} finally {
			if (fos != null) fos.close();
		}
		
		return saveFileNeme;
	}
	
	/**
	 * [설명] 파일업로드(확장자가 없을 경우.)
	 * 
	 * @param file - MultipartFile 객체를 넘겨받음
	 * @param uploadPath - upload Path
	 * @param over - 업로드 파일명과기존파일명 중복시 덮어쓸지 여부(덮어쓸시 true)
	 * @return
	 * @throws IOException 
	 */
	public static String writeFile2(MultipartFile multiFile, String uploadPath, boolean over) throws  IOException {
		int i = 0;
		
		String orgFileNeme	= multiFile.getOriginalFilename();	// 원본파일명
		String saveFileNeme 	= "";	// 저장파일명 - 파일명 변화(현재날짜시간 + 확장자 )
		
		String arrFileNeme = "";	// 파일명
		
		FileOutputStream fos = null;
		
		// upload 디렉토리존재확인
		File file = new File(uploadPath);
		if ( !file.exists() ) file.mkdirs();	// 미존재시 디렉토리 생성

		// 특수문자 or '%00', '%zz' 체크 => file.getOriginalFilename()
		if (isSpecial(orgFileNeme)) throw new BizException("일시적인 시스템장애로 접근하실 수 없습니다.");

		try {
			arrFileNeme	= DateUtil.getTime("yyyyMMddHHmmss");
			saveFileNeme 	= arrFileNeme + ".txt";	// 저장파일명 - 파일명 변화(현재날짜시간 + 확장자 )
			
			i = 1;
			// 같은 파일명 존재시 덮어쓸지 여부에 따라 파일명 변경
			if (!over) {
				while (existsFile(uploadPath, saveFileNeme)) {
					saveFileNeme = arrFileNeme + "_"  + (i++) + ".txt";
				}
			}

			// fos = new FileOutputStream(uploadPath + "\\" + n_file);
			fos = new FileOutputStream(uploadPath + saveFileNeme);
			fos.write(multiFile.getBytes());
		} catch (Exception e) {
			throw new BizException("일시적인 시스템장애로 접근하실 수 없습니다.", e);
		} finally {
			if (fos != null) fos.close();
		}
		
		return saveFileNeme;
	}
	
	/**
	 * [설명] 파일업로드(확장자가 없을 경우.)
	 * 
	 * @param file - MultipartFile 객체를 넘겨받음
	 * @param uploadPath - upload Path
	 * @param over - 업로드 파일명과기존파일명 중복시 덮어쓸지 여부(덮어쓸시 true)
	 * @return
	 * @throws IOException 
	 */
	public static String writeFile3(String path, String fileName, String uploadPath, boolean isDelete) throws  IOException {
		FileOutputStream fos = null;
		FileInputStream fis = null;
		
		File file = new File(path + fileName);
		System.out.println(file.exists());
		System.out.println(path + fileName);
		
		if (file.isFile()) {
			// upload 디렉토리존재확인
			System.out.println(file.length());
			File writeFile = new File(uploadPath+fileName);
			File fParent = new File ( writeFile.getParent());
			if ( !fParent.exists()) {
				fParent.mkdir();
				writeFile.createNewFile();
			}
			
			try {
				fis = new FileInputStream(file);
				fos = new FileOutputStream(writeFile);
				
				FileChannel fcin =  fis.getChannel();
				FileChannel fcout = fos.getChannel();
				 
				long size = fcin.size();
				fcin.transferTo(0, size, fcout);
				
				fcout.close();
				fcin.close();
				
				if(isDelete){
					file.delete();
				}
			/*	int i = 0;
				byte[] bBuffer = new byte[1024];
				while((i = fis.read()) != -1){
					fos.write(bBuffer, 0, i);
				}
				fos.flush();
				
				if(isDelete){
					file.delete();
				}
				System.out.println(writeFile.length());*/
			} catch (Exception e) {
				throw new BizException("일시적인 시스템장애로 접근하실 수 없습니다.", e);
			} finally {
				if (fos != null) fos.close();
				if (fis != null) fis.close();
			}
		}
		
		
		return uploadPath+fileName;
	}

	/**
	 * [설명] 파일생성
	 * 
	 * @param sFilePath - 파일경로
	 * @param sFileName - 파일명
	 * @param sFileContent - 파일내용
	 * @param isDelete - 업로드 파일명과기존파일명 중복시 덮어쓸지 여부(덮어쓸시 true)
	 * @return
	 * @throws IOException 
	 */
	public static void writeFile4(String sFilePath, String sFileName, String sFileContent, String sCharSet, boolean isDelete) throws  IOException {
		
		File uploadSub = new File(sFilePath);
		if (!uploadSub.exists()) uploadSub.mkdir();
		
		File writeFile = new File(uploadSub + "/" + sFileName);
    	FileOutputStream fos = null;

    	try {
            fos = new FileOutputStream(writeFile, isDelete);
            fos.write(sFileContent.getBytes(sCharSet));
            fos.flush();
    	} catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
        	fos.close();
        }

	}
	
	/**
	 * [설명] 파일 확장자 체크
	 * @param val
	 * @return
	 */
	public static boolean isFileExtCheck(String val){
		boolean b = false;
		//gif|jpg|jpeg|bmp|png
		String div = ".gif|.jpg|.jpeg|.bmp|.png|.pdf|.mp3|.mp4|";
	
		if ( -1 < div.indexOf(val) ) {
			b = true;
		}
		return b;
	}

	public static boolean isImageFileExtCheck(String val){
		boolean b = false;
		String div = ".gif|.jpg|.jpeg|.bmp|.png|";
	
		if ( -1 < div.indexOf(val) ) {
			b = true;
		}
		return b;
	}
	
	/**
	 * [설명] 파일명에 특수문자가 있는지 확인함
	 * @param val
	 * @return
	 */
	public static boolean isSpecial (String val) {
		boolean b = false;
//		String[] filter_word = {"?","/",">","<",",",";",":","[","]","{","}","~","!","@","#","$","%","^","&","*","(",")","=","+","|","\\","`","%00","%zz"};
		String[] filter_word = {"?","/",">","<",",",";",":","{","}","~","!","@","#","$","%","^","*","=","+","|","\\","`","%00","%zz"};
		
		for(int i=0 ; i < filter_word.length ; i++){
			if(val.indexOf(filter_word[i]) > -1){
				b = true;
			}
		}
		return b;
	}

	/**
	 * [설명] Text파일을 읽어 List로 반환
	 * @param filePath
	 * @param fileName
	 * @return
	 * @throws Exception 
	 */
	public static List<String> getTextFileRead(String fileFullPath) throws Exception {
		String val = "";
		List<String> list = null;
		
		File file = new File(fileFullPath);
		if (file.isFile()) {
			// RandomAccessFile 사용
			// RandomAccessFile raf = new RandomAccessFile(file, "rw");	// read/write 랜덤 엑세스 파일로 open
			// raf.seek(50);	// 커서를 50byte 뒤로 이동.
			
			// FileReader를 이용한 파일 읽기
			// BufferedReader br = new BufferedReader(new FileReader(file));
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			list = new ArrayList<String>();
			while((val = br.readLine()) != null){
				list.add(val);
			}
			br.close();
		}
		return list;
	}

	/**
	 * [설명] 파일이 존재여부
	 * 
	 * @param filePath
	 * @param fileName
	 * @return boolean
	 */
	public static String existsFileName(String filePathNm) {
		File file = new File(filePathNm);
		boolean result = file.isFile();
		if(result){
			return filePathNm;
		} else {
			return "";
		}
	}

	/**
	 * [설명] 파일이 존재여부
	 * 
	 * @param filePath
	 * @param fileName
	 * @return boolean
	 */
	public static boolean existsFile(String filePath, String fileName) {
		File file = null;
		if("".equals(fileName)){
			file = new File(filePath);
		} else {
			file = new File(filePath, fileName);
		}
		boolean result = file.isFile();
		return result;
	}

	/**
	 * [설명] 파일 삭제
	 * 
	 * @param filePath
	 * @param fileName
	 * @return boolean
	 */
	public static boolean deleteFile(String filePath, String fileName) {
		File file = new File(filePath, fileName);
		boolean result = file.delete();
		return result;
	}

	public static void main(String[] args) {
		System.out.println(FileUtil.existsFile("d:/cmpdata/application/batch/seedVerify/allApplicationRequest/2012", "allApplicationRequest.txt.2012-11-06"));
	}
	
	/**
	 * filter_word변수에 등록된 Mime Type을 체크하여 있으면 true 를 반환
	 * 
	 * @param str
	 * @return
	 */
	public static boolean getMimeTypeCheck(String str) {
		String[] filter_word = {"application/octet-stream", "application/x-msdownload", "application/x-sh"};
		boolean b = false;

		for (int i = 0; i < filter_word.length; i++) {
			if (str.equalsIgnoreCase(filter_word[i])) {
				b = true;
			}
		}
		return b;
	}
	
	/**
	 * 파일 사이즈
	 * 
	 * @param str
	 * @return
	 */
	public static long getFileSize(String pathnm) {
		long fileByteSize = 0;
		File file = new File(pathnm);
		
		if (file.exists()) {
			fileByteSize = file.length();
		}
		
		return fileByteSize;
	}
	
	public static File rename(File f) {	//File f는 원본 파일
	    
		String name = f.getName();
	    String body = null;
	    String ext = null;

	    int dot = name.lastIndexOf(".");
	    if (dot != -1) {//확장자가 있을때
	    	//body = name.substring(0, dot);
	    	body = String.valueOf((new Date()).getTime());
	    	ext = name.substring(dot);
	    } else {//확장자가 없을때
	    	//body = name;
	    	body = String.valueOf((new Date()).getTime());
	    	ext = "";
	    }

	    f = new File(f.getParent(), body + ext);
	    
	    int count = 0;
	    //중복된 파일이 있을때
	    //파일이름뒤에 a숫자.확장자 이렇게 들어가게 되는데 숫자는 9999까지 된다.
	    while (!createNewFile(f) && count < 9999) {  
	    	count++;
	    	String newName = body + "[" + count + "]" + ext;
	    	f = new File(f.getParent(), newName);
	    }

	    return f;
	}
	
	private static boolean createNewFile(File f) { 
		try {
			return f.createNewFile(); //존재하는 파일이 아니면
		} catch (IOException ignored) {
			return false;
		}
	}
	
	
}