package com.secoo;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

public class IndexingTest {

	private String ids[] = { "1", "2", "3" };
	private String citys[] = { "qingdao2", "nanjing2", "shanghai2" };
	private String descs[] = {
			"Qingdao is a beautiful city.", 
			"Nanjing is a city of culture.",
			"Shanghai is a bustling city." };

	public Directory getDirectory() throws IOException {
		return FSDirectory.open(Paths.get("D:\\data\\lucene2"));
	}

	@Test
	public void initIndex() throws Exception {
		IndexWriter writer = getWriter(getDirectory());
		writeDocument(writer);
		closeWriter(writer);
	}

	/**
	 * 写索引
	 * 
	 * @throws Exception<br/>
	 * @author qiuwanchi<br/>
	 * @date: 2016年4月18日 <br/>
	 */
	public void writeDocument(IndexWriter writer) throws Exception {
		for (int i = 0; i < ids.length; i++) {
			Document doc = new Document();
			doc.add(new StringField("id", ids[i], Field.Store.YES));
			doc.add(new StringField("city", citys[i], Field.Store.YES));
			doc.add(new TextField("desc", descs[i], Field.Store.YES));
			writer.addDocument(doc); // 添加文档
		}
	}

	/**
	 * 获取IndexReader对象
	 * 
	 * @param dir
	 * @return
	 * @throws IOException<br/>
	 * @author qiuwanchi<br/>
	 * @date: 2016年4月18日 <br/>
	 */
	public IndexReader getIndexReader(Directory dir) throws IOException {
		return DirectoryReader.open(dir);
	}

	/**
	 * 获取索引写
	 * 
	 * @param dir
	 * @return
	 * @throws Exception<br/>
	 * @author qiuwanchi<br/>
	 * @date: 2016年4月18日 <br/>
	 */
	private IndexWriter getWriter(Directory dir) throws Exception {
		Analyzer analyzer = new StandardAnalyzer(); // 标准分词器
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		IndexWriter writer = new IndexWriter(dir, iwc);
		return writer;
	}

	/**
	 * 测试写了几个文档
	 * 
	 * @throws Exception<br/>
	 * @author qiuwanchi<br/>
	 * @date: 2016年4月18日 <br/>
	 */
	@Test
	public void testHowMachWriteDocument() throws Exception {
		IndexWriter writer = getWriter(getDirectory());
		System.out.println("有" + writer.numDocs() + "个文档");
		closeWriter(writer);
	}

	/**
	 * 测试读取文档
	 * 
	 * @throws Exception<br/>
	 * @author qiuwanchi<br/>
	 * @date: 2016年4月18日 <br/>
	 */
	@Test
	public void testReaderDocument() throws Exception {
		IndexReader reader = getIndexReader(getDirectory());
		System.out.println("最大文档数:" + reader.maxDoc());
		System.out.println("实际文档数:" + reader.numDocs());
		closeReader(reader);
	}

	/**
	 * 测试删除在合并前
	 * 
	 * @throws Exception<br/>
	 * @author qiuwanchi<br/>
	 * @date: 2016年4月18日 <br/>
	 */
	@Test
	public void testDeleteBeforeMerge() throws Exception {
		IndexWriter writer = getWriter(getDirectory());

		System.out.println("删除前文档数:" + writer.numDocs());
		writer.deleteDocuments(new Term("id", "1"));
		writer.commit();
		System.out.println("writer.maxDoc():" + writer.maxDoc());
		System.out.println("writer.numDocs():" + writer.numDocs());
		
		closeWriter(writer);
	}

	/**
	 * 测试删除合并后
	 * 
	 * @throws Exception<br/>
	 * @author qiuwanchi<br/>
	 * @date: 2016年4月18日 <br/>
	 */
	@Test
	public void testDeleteAfterMerge() throws Exception {
		IndexWriter writer = getWriter(getDirectory());

		System.out.println("删除前文档数:" + writer.numDocs());
		writer.deleteDocuments(new Term("id", "2"));
		writer.forceMergeDeletes(); // 强制合并
		writer.commit();
		System.out.println("writer.maxDoc():" + writer.maxDoc());
		System.out.println("writer.numDocs():" + writer.numDocs());
		
		closeWriter(writer);
	}

	/**
	 * 更新索引
	 * 
	 * @throws Exception<br/>
	 * @author qiuwanchi<br/>
	 * @date: 2016年4月18日 <br/>
	 */
	@Test
	public void testUpdate() throws Exception {
		IndexWriter writer = getWriter(getDirectory());

		Document doc = new Document();
		doc.add(new StringField("id", "2", Field.Store.YES));
		doc.add(new StringField("city", "qingdao", Field.Store.YES));
		doc.add(new TextField("desc", "qiuwanchi2 is a city.", Field.Store.YES));
		writer.updateDocument(new Term("id", "2"), doc);
		
		closeWriter(writer);
	}

	/**
	 * 关闭资源
	 * 
	 * @throws Exception<br/>
	 * @author qiuwanchi<br/>
	 * @date: 2016年4月18日 <br/>
	 */
	public void closeWriter(IndexWriter writer) throws Exception {
		if (writer != null) {
			writer.close();
		}
	}
	
	public void closeReader(IndexReader reader) throws Exception {
		if (reader != null) {
			reader.close();
		}
	}
}
