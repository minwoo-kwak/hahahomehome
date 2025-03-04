package com.ssafy.house.info.model.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.springframework.stereotype.Service;

import com.ssafy.house.info.controller.WordCloudController;

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;

@Service
public class WordCloudServiceImpl implements WordCloudService {

	/**
	 * PDF를 단어별로 파싱해옵니다.
	 * @return 파싱된 텍스트
	 */
	@Override
	public String parsePDF() {
		String filePath = "C:/report.pdf";
		File file = new File(filePath);

		try (PDDocument document = PDDocument.load(file);) {

			PDFTextStripperByArea stripper = new PDFTextStripperByArea();
			stripper.setSortByPosition(true);
			PDFTextStripper Tstripper = new PDFTextStripper();
			Tstripper.setStartPage(8);
			Tstripper.setEndPage(73);

			String text = Tstripper.getText(document);

			// 출력
//			System.out.println(text);

			// 정규식을 사용하여 한글, 영문, 숫자 이외의 문자를 공백으로 대체하고, 앞뒤 공백을 제거합니다.
			String replace_text = text.replaceAll("[^가-힣a-zA-Z0-9]", " ");
			String trim_text = replace_text.trim();

			FileWriter fileWriter = new FileWriter("C:/parserdtext.txt");
			fileWriter.write(trim_text);

			fileWriter.close();

			return trim_text;
		} catch (IOException e) {
			System.out.println("에러");
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 형태소 분석기를 활용해서 파싱된 텍스트에서 명사들만 추출합니다.
	 */
	@Override
	public List<String> getNounList(String text) {

		Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);
		String strToAnalyze = text;

		KomoranResult analyzeResultList = komoran.analyze(strToAnalyze);

		List<String> nounList = analyzeResultList.getNouns();
		return nounList;
	}

	/**
	 * 추출된 명사 리스트에서 명사들의 개수를 셉니다.
	 */
	@Override
	public List<Map<String, Object>> doWordCount(List<String> pList) throws Exception {

		if (pList == null) {
			pList = new ArrayList<String>();
		}

		Set<String> rSet = new HashSet<String>(pList);
		Iterator<String> it = rSet.iterator();
		List<Map<String, Object>> rList = new ArrayList<>();

		while (it.hasNext()) {
			Map<String, Object> rMap = new HashMap<>();
			String word = it.next();
			if (isKeyword(word)) {
				continue;
			}

			int frequency = Collections.frequency(pList, word);
			if (frequency > 12) {
				rMap.put("text", word);
				rMap.put("size", frequency);
				rList.add(rMap);
			}
		}

		return rList;
	}

	/**
	 * 해당 키워드들은 제외하기 위해 설정해줍니다.
	 * @param word
	 * @return 해당 키워드에 속하면 true, 아니면 false
	 */
	public static boolean isKeyword(String word) {
		// 특정 키워드들
		Set<String> keywords = new HashSet<>(
				Arrays.asList("시장", "지수", "분기", "주택", "부동산", "국면", "전", "국", "주", "기", "소"));

		// 대소문자 구분 없이 비교
		return keywords.contains(word);
	}

	/**
	 * 파싱된 텍스트를 읽어와, 명사들을 추출하고 명사들의 개수를 세어서, 명사와 명사 개수를 담은 객체리스트를 반환합니다.
	 */
	@Override
	public List<Map<String, Object>> doWordAnalysis() throws Exception {
		// 파일 경로를 지정합니다.
		String filePath = "data.txt";

		// 파일을 읽어오기 위해 ClassLoader를 사용합니다.
		ClassLoader classLoader = WordCloudController.class.getClassLoader();
		URL resource = classLoader.getResource(filePath);

		// 파일의 경로를 얻습니다.
		Path path = Paths.get(resource.toURI());

		// 파일의 내용을 문자열로 읽어옵니다.
		String content = new String(Files.readAllBytes(path));

		// 읽어온 내용을 출력하거나 다른 작업을 수행합니다.
		System.out.println("파일 내용:\n" + content);
		List<String> pList = this.getNounList(content);

		if (pList == null) {
			pList = new ArrayList<String>();
		}

		List<Map<String, Object>> rList = this.doWordCount(pList);

		if (rList == null) {
			rList = new ArrayList<>();
		}

		return rList;
	}

}
