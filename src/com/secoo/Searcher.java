package com.secoo;

import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * 查询器
 * @author qiuwanchi<br/>
 * @date: 2016年4月18日 <br/>
 */
public class Searcher {

	/**
	 * 查询
	 * @param indexDir
	 * @param q
	 * @throws Exception<br/>
	 * @author qiuwanchi<br/>
	 * @date: 2016年4月18日 <br/>
	 */
	public void search(String indexDir,String q) throws Exception{
		Directory dir = FSDirectory.open(Paths.get(indexDir));
		IndexReader reader = DirectoryReader.open(dir);//读取索引
		IndexSearcher search = new IndexSearcher(reader);//创建索引查询器
		
		Analyzer analyzer = new StandardAnalyzer();//标准分词器
		QueryParser parser = new QueryParser("contents", analyzer);
		Query query = parser.parse(q);//解析查询的内容成为一个Query
		
		long start = System.currentTimeMillis();
		TopDocs hits = search.search(query, 10);//查询
		long end = System.currentTimeMillis();
		System.out.println("查询到:" + hits.totalHits + "个文档;花费时间为:" + (end - start));
		
		for(ScoreDoc scoreDoc : hits.scoreDocs){
			Document doc = search.doc(scoreDoc.doc);
			
			System.out.println(doc.get("fullPath"));
		}
		
		reader.close();
	}
	
	public static void main(String[] args) {
		String indexDir = "D:\\data\\Lucene\\index";
		String q = "hospital";
		Searcher search = new Searcher();
		try {
			search.search(indexDir, q);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
