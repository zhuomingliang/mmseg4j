package com.chenlb.mmseg4j.lucene;

import java.io.IOException;

import junit.framework.TestCase;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import com.chenlb.mmseg4j.analysis.SimpleAnalyzer;

public class LuceneUseSimpleAnalyzerTest extends TestCase {

	Directory dir;
	SimpleAnalyzer analyzer;
	
	@Override
	protected void setUp() throws Exception {
		String txt = "京华时报1月23日报道 昨天，受一股来自中西伯利亚的强冷空气影响，本市出现大风降温天气，白天最高气温只有零下7摄氏度，同时伴有6到7级的偏北风。";
		analyzer = new SimpleAnalyzer();
		dir = new RAMDirectory();
		IndexWriter iw = new IndexWriter(dir, analyzer);
		Document doc = new Document();
		doc.add(new Field("txt", txt, Field.Store.YES, Field.Index.ANALYZED));
		iw.addDocument(doc);
		iw.commit();
		iw.optimize();
		iw.close();
	}

	public void testSearch() {
		try {
			IndexSearcher searcher = new IndexSearcher(dir);
			QueryParser qp = new QueryParser("txt", analyzer);
			Query q = qp.parse("中西伯利亚");
			System.out.println(q);
			TopDocs tds = searcher.search(q, 10);
			System.out.println("======size:"+tds.totalHits+"========");
			for(ScoreDoc sd : tds.scoreDocs) {
				System.out.println(sd.score);
				System.out.println(searcher.doc(sd.doc).get("txt"));
			}
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
