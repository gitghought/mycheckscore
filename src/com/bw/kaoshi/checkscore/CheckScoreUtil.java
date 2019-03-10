package com.bw.kaoshi.checkscore;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.aspectj.util.FileUtil;

public class CheckScoreUtil {
	
	
	private static String[] singleAnswers = null;
	private static String[] multiAnswers = null;
	private static String[] chooseAnswers = null;
	
	//起始索引
	private static	int danRowIndex = 4;
	private static	int duoRowIndex = 26;
	private static	int panRowIndex = 38;
	
	
	//数量
	private static	int danSize = 20;
	private static	int duoSize = 10;
	private static	int panSize = 10;
	
	
	//错题数量
	private static	int danWrongSize = 0;
	private static	int duoWrongSize = 0;
	private static	int panWrongSize = 0;
	
	static {
		
		//单选题
		String singleAnswersStr = "D,B,A,A,D,D,D,C,C,D,B,A,C,A,B,A,A,A,B,B";
		singleAnswers = singleAnswersStr.split(",");
		
		//多选题
		String multiAnswersStr = "ABD,ABC,AC,ABC,ABCD,AB,BD,ABC,BCD,ABCD";
		multiAnswers = multiAnswersStr.split(",");
		
		//判断题
		String chooseAnswersStr = "A,A,B,A,B,A,B,B,A,A";
		chooseAnswers = chooseAnswersStr.split(",");
		
	}

	
	public static List<String> checkSingle(List<String> studentSingleAnswers) {
		
		List<String> resultList = new ArrayList<String>();
		List<Integer> wrongIndex = new ArrayList<Integer>();
		
		for (int i = 0; i < danSize; i++) {
			String rightAnswer = singleAnswers[i];
			String stuAnswer = studentSingleAnswers.get(i);
			if(stuAnswer != null) {
				stuAnswer = stuAnswer.trim();
			}
			
			if(!rightAnswer.equalsIgnoreCase(stuAnswer)) {
				danWrongSize++;
				wrongIndex.add(i+1);
			}
		}
		
		String finalMsg = "单选题总共答错"+danWrongSize+"道题,错题编号为："+Arrays.toString(wrongIndex.toArray());
		
		resultList.add(finalMsg);
		return resultList;
	}
	
	public static List<String> checkChoose(List<String> studentChooseAnswers) {

		List<String> resultList = new ArrayList<String>();
		List<Integer> wrongIndex = new ArrayList<Integer>();
		
		for (int i = 0; i < panSize; i++) {
			String rightAnswer = chooseAnswers[i];
			
			String[] rightAnswerArr = new String[4];
			if(rightAnswer.equals("A")) {
				rightAnswerArr = new String[]{"A","a","对","V","√"};
			}else {
				rightAnswerArr = new String[]{"B","b","错","X","×"};
			}
			
			String stuAnswer = studentChooseAnswers.get(i);
			
			if(stuAnswer != null) {
				stuAnswer = stuAnswer.trim();
			}
			
			
			List<String> rightAnswerList = Arrays.asList(rightAnswerArr); 
			
			if(!rightAnswerList.contains(stuAnswer)) {
				panWrongSize++;
				wrongIndex.add(i+1);
			}
		}
		
		String finalMsg = "判断题总共答错"+panWrongSize+"道题,错题编号为："+Arrays.toString(wrongIndex.toArray());
		
		resultList.add(finalMsg);
		return resultList;
	}
	
	public static List<String> checkMulti(List<String> studentMultiAnswers) {
		List<String> resultList = new ArrayList<String>();
		List<Integer> wrongIndex = new ArrayList<Integer>();
		
		for (int i = 0; i < duoSize; i++) {
			String rightAnswer = multiAnswers[i];
			String stuAnswer = studentMultiAnswers.get(i);
			if(stuAnswer != null) {
				stuAnswer = stuAnswer.trim();
			}
			
			if(!rightAnswer.equalsIgnoreCase(stuAnswer)) {
				duoWrongSize++;
				wrongIndex.add(i+1);
			}
		}
		
		String finalMsg = "多选题总共答错"+duoWrongSize+"道题,错题编号为："+Arrays.toString(wrongIndex.toArray());
		
		resultList.add(finalMsg);
		return resultList;
	}
	
	
	
	
	public static void main(String[] args) {
		
		//1.读取答题卡文件列表
		File dir = new File("D:\\月考判卷\\1606D理论");
		File[] stuFiles = dir.listFiles();
		
		
		
		//2.遍历，逐一判卷
		for(int i=0; i<stuFiles.length; i++) {
			
			danWrongSize = 0;
			duoWrongSize = 0;
			panWrongSize = 0;
			
			
			File stuFile = stuFiles[i];
			
			String fileName = stuFile.getName();//abc.xlst
			String srcFileName = fileName.substring(0, fileName.lastIndexOf("."));
			
			Map<QuestionType, List<String>> stuAnswerMap = readStuFile(stuFile);
			
			
			List<String> stuSingleList = stuAnswerMap.get(QuestionType.SINGLE);
//			List<String> stuMultiList = stuAnswerMap.get(QuestionType.MULTI);
//			List<String> stuChooseList = stuAnswerMap.get(QuestionType.CHOOSE);
			
			
			List<String> resultList = new ArrayList<String>();
			List<String> singleResultList = checkSingle(stuSingleList);
//			List<String> multiResultList = checkMulti(stuMultiList);
//			List<String> chooseResultList = checkChoose(stuChooseList);
			
			
			resultList.addAll(singleResultList);
//			resultList.addAll(multiResultList);
//			resultList.addAll(chooseResultList);
			
			//计算总分
			int totalWrong = danWrongSize*QuestionType.SINGLE.getScore()
					+ duoWrongSize*QuestionType.MULTI.getScore()
					+ panWrongSize*QuestionType.CHOOSE.getScore();
			
			int total = 100 - totalWrong;
			resultList.add("==============="+srcFileName+"的总分为"+total+"================================");

			File targetFile = new File("D:\\月考判卷\\1606D理论",srcFileName+".txt");
			String resultStr = String.join(System.getProperty("line.separator"), resultList);
//			FileUtil.writeAsString(targetFile, resultStr);
		}
		
		//3.判卷
		/**
		 * a.读取到Map<QuestionType,List<String>>
		 * b.计算各个类型的分值，汇总出总分
		 * c.将判卷的日志写在一个txt文件中
		 */
	}

	private static Map<QuestionType, List<String>> readStuFile(File stuFile) {
		
		Map<QuestionType, List<String>> map = new HashMap<QuestionType, List<String>>();

		
		
		 try {
	            HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(stuFile));

	            //HSSFSheet sheet = wb.getSheetAt(0);
	            HSSFSheet sheet = wb.getSheetAt(0);
	            
	            List<String> stuSingleList = new ArrayList<String>();
	            List<String> stuMultiList = new ArrayList<String>();
	            List<String> stuChooseList = new ArrayList<String>();
	            

	            for(int i=danRowIndex;i<danRowIndex+danSize;i++) {
	                HSSFRow row = sheet.getRow(i);
	                HSSFCell cell = row.getCell(1);
	                System.out.println("cell = " + cell.getStringCellValue());
	                stuSingleList.add(cell.getStringCellValue());	                
	            }

//	            for(int i=duoRowIndex;i<duoRowIndex+duoSize;i++) {
//	                HSSFRow row = sheet.getRow(i);
//	                HSSFCell cell = row.getCell(1);
//	                stuMultiList.add(cell.getStringCellValue());	                
//	            }
//
//	            for(int i=panRowIndex;i<panRowIndex+panSize;i++) {
//	                HSSFRow row = sheet.getRow(i);
//	                HSSFCell cell = row.getCell(1);
//	                stuChooseList.add(cell.getStringCellValue());	                
//	            }
//	            
	            map.put(QuestionType.SINGLE, stuSingleList);
//	            map.put(QuestionType.MULTI, stuMultiList);
//	            map.put(QuestionType.CHOOSE, stuChooseList);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		
		return map;
	}
	
}
