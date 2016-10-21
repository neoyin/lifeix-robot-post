package com.app.doc;

import java.io.FileInputStream;
import java.io.IOException;

public class DocParser {

	public static void readDoc() throws IOException{
		FileInputStream in = new FileInputStream("/home/neoyin/Downloads/test.doc");
		/*POIFSFileSystem pfs = new POIFSFileSystem(in);
		HWPFDocument hwpf = new HWPFDocument(pfs);
		hwpf.getPicturesTable();*/
	
		/*WordExtractor extractor = new WordExtractor(in);
		
		String temp[] = extractor.
		for (String str : temp) {
			System.out.println(str);
		}*/
		
	
	}

	public static void main(String[] args) throws IOException {
		readDoc();
	}

}
