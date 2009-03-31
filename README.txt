# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

1、mmseg4j 用 Chih-Hao Tsai 的 MMSeg 算法(http://technology.chtsai.org/mmseg/)实现的中文分词器，并实现 lucene 的 analyzer 和 solr 的TokenizerFactory 以方便在Lucene和Solr中使用。

2、MMSeg 算法有两种分词方法：Simple和Complex，都是基于正向最大匹配。Complex 加了四个规则过虑。官方说：词语的正确识别率达到了 98.41%。mmseg4j 已经实现了这两种分词算法，1.5版的分词速度simple算法是 1100kb/s左右、complex算法是 700kb/s左右，（测试机：AMD athlon 64 2800+ 1G内存 xp）。

3、在 com.chenlb.mmseg4j.example包里的类示例了两种分词效果。

4、在 com.chenlb.mmseg4j.analysis包里扩展lucene analyzer。MMSegAnalyzer默认使用complex方式分词。

5、在 com.chenlb.mmseg4j.solr包里扩展solr tokenizerFactory。
在 solr的 schema.xml 中定义 field type如：
	<fieldType name="textComplex" class="solr.TextField" >
      <analyzer>
        <tokenizer class="com.chenlb.mmseg4j.solr.MMSegTokenizerFactory" dicPath="dic"/>
      </analyzer>
    </fieldType>
	<fieldType name="textSimple" class="solr.TextField" >
      <analyzer>
        <tokenizer class="com.chenlb.mmseg4j.solr.MMSegTokenizerFactory" mode="simple" dicPath="my_dic"/>
      </analyzer>
    </fieldType>
    
dicPath 指定词库位置（每个MMSegTokenizerFactory可以指定不同的目录，当是相对目录时，是相对 solr.home 的目录），mode 指定分词模式（simple|complex，默认是complex）。

6、运行，词典用mmseg.dic.path属性指定或在当前目录下的data目录，默认是 ./data 目录。
java -Dmmseg.dic.path=./data -jar mmseg4j-1.5.jar 这里是字符串
或
java -cp .;mmseg4j-1.5.jar com.chenlb.mmseg4j.example.Simple 这里是字符串

7、一些字符的处理
英文、俄文、希腊、数字（包括①㈠⒈）的分出一连串的。目前版本没有处理小数字问题
如ⅠⅡⅢ是单字分，字库(chars.dic)中没找到也单字分。

8、词库：
  * data/chars.dic 是单字与语料中的频率，一般不用改动，1.5版本中已经加到mmseg4j的jar里了，我们不需要关心它，当然你在词库目录放这个文件可能覆盖它。
  * data/words.dic 是词库文件，一行一词，当然你也可以使用自己的，1.5版本使用 sogou 词库，1.0的版本是用 rmmseg 带的词库。

在 solr 1.3 与 lucene 2.4 测试过，官方博客 http://blog.chenlb.com/topics/category/mmseg4j ， 如果发现问题或bug与我联系 chenlb2008@gmail.com 。