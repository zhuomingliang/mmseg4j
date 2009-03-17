package com.chenlb.mmseg4j.solr;

import java.io.File;
import java.io.Reader;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.lucene.analysis.TokenStream;
import org.apache.solr.analysis.BaseTokenizerFactory;
import org.apache.solr.common.ResourceLoader;
import org.apache.solr.core.SolrResourceLoader;
import org.apache.solr.util.plugin.ResourceLoaderAware;

import com.chenlb.mmseg4j.ComplexSeg;
import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.Seg;
import com.chenlb.mmseg4j.SimpleSeg;
import com.chenlb.mmseg4j.analysis.MMSegTokenizer;

public class MMSegTokenizerFactory extends BaseTokenizerFactory implements ResourceLoaderAware {

	static final Logger log = Logger.getLogger(MMSegTokenizerFactory.class.getName());
	
	private ThreadLocal<Seg> segLocal;
	private Dictionary dic = null;
	
	@Override
	public void init(Map<String, String> args) {
		super.init(args);
		log.info("init ...");
		final Map<String, String> myArgs = args;
		segLocal = new ThreadLocal<Seg>() {

			@Override
			protected Seg initialValue() {
				return initSeg(myArgs);
			}
			
		};
	}

	private Seg initSeg(Map<String, String> args) {
		Seg seg = null;
		log.info("create new seg ...");
		//default complex
		String mode = args.get("mode");
		if("simple".equals(mode)) {
			log.info("use simple mode");
			seg = new SimpleSeg(dic);
		} else {
			log.info("use complex mode");
			seg = new ComplexSeg(dic);
		}
		return seg;
	}
	
	public TokenStream create(Reader input) {

		return new MMSegTokenizer(segLocal.get(), input);
	}

	public void inform(ResourceLoader loader) {
		String dicPath = args.get("dicPath");
		
		if(dicPath != null) {
			File f = new File(dicPath);
			if(!f.isAbsolute() && loader instanceof SolrResourceLoader) {	//相对目录
				SolrResourceLoader srl = (SolrResourceLoader) loader;
				dicPath = srl.getInstanceDir()+dicPath;
				f = new File(dicPath);
			}
			log.info("dic load... in="+dicPath);
			dic = new Dictionary(f);
		} else {
			dic = new Dictionary();
		}
		
	}

	
}
