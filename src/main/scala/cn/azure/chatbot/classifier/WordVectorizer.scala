package cn.azure.chatbot.classifier

import com.github.jfasttext.JFastText
import com.huaban.analysis.jieba.JiebaSegmenter
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

object WordVectorizer {
  private val log = LoggerFactory.getLogger("WordVectorizer")
  private var jft: JFastText = _
  private val cutter = new JiebaSegmenter()

  private def docToWords(s: String) = cutter.process(s, SegMode.SEARCH).toList.map(_.word)

  private def wordsToVecs(words: List[String]) = words.map(jft.getWordVector(_).asScala.map(_.toFloat).toArray[Float])

  def docToVecs(s: String): List[Array[Float]] = wordsToVecs(docToWords(s))

  def initModel(jftPath: String): Unit = {
    jft = new JFastText()
    log.debug(s"Loading FastText model '$jftPath'...")
    jft.loadModel(jftPath)
    log.debug("FastText model loaded.")
  }
}
