package cn.azure.chatbot.classifier

import com.github.jfasttext.JFastText
import com.huaban.analysis.jieba.JiebaSegmenter
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode
import com.intel.analytics.bigdl.nn.Module
import com.intel.analytics.bigdl.nn.abstractnn.{AbstractModule, Activity}
import com.intel.analytics.bigdl.tensor.Tensor
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer

object Classifier {
  private val log = LoggerFactory.getLogger("Classifier")
  private val SEQUENCE_LEN = 500
  private val DIMENSIONS = 300
  private val PADDING_VEC: List[Array[Float]] = List.fill(SEQUENCE_LEN)(Array.fill(DIMENSIONS)(0))
  //  TODO:
  private var CATEGORIES: Array[String] = Array() //Array("", "CCS", "IoT", "Resource health", "SQL Database", "active-directory", "analysis-services", "api-management", "app-service", "application-gateway", "automation", "azure-portal", "azure-resource", "azure-resource-manager", "backup", "batch", "billing", "cdn", "cosmos-db", "downloads", "languages", "machine-learning", "multiple", "mysql", "open-resource", "scheduler", "security", "site-recovery", "storage", "virtual-machines", "virtual-network")


  private var model: AbstractModule[Activity, Activity, Float] = _
  private var top: Int = 10

  private def docToVecs(s: String): List[Array[Float]] = (WordVectorizer.docToVecs(s) ++ PADDING_VEC).take(SEQUENCE_LEN)

  private def vecToTensor(vec: List[Array[Float]]): Tensor[Float] = Utils.concatArray(vec).resize(SEQUENCE_LEN, DIMENSIONS)

  private def docToTensor(s: String): Tensor[Float] = vecToTensor(docToVecs(s))

  // TODO: Map to category names
  private def indexToCategoryName(idx: Int): String = {
    CATEGORIES(idx)
  }

  def initModel(jftPath: String, bigdlPath: String, bigdlWeightPath: String, categories: java.util.List[String], top: Int): Unit = {
    CATEGORIES = categories.toList.toArray
    log.debug(s"Categories are : [${categories.mkString(".")}]")
    WordVectorizer.initModel(jftPath)
    log.debug(s"Loading BigDL model '$bigdlPath' and '$bigdlWeightPath'")
    model = Module.loadModule(bigdlPath, bigdlWeightPath)
    log.debug("BigDL model loaded.")
    model.evaluate()
    log.debug(s"Set returning category number to $top.")
    this.top = top
  }

  def classifyString(s: String): java.util.List[String] = {
    val probs = model.forward(docToTensor(s))
      .asInstanceOf[Tensor[Float]].storage().array()
    log.debug(s"probs=[${probs.mkString(",")}]")
    probs.zipWithIndex.sortBy(-_._1)  // Reverse order
      .map(_._2).toList.map(indexToCategoryName).filter(_.nonEmpty).take(top).asJava
  }

  // Test
  def main(argv: Array[String]): Unit = {
    initModel("./cc.zh.300.bin",
      "./faqmodel.bigdl",
      "./faqmodel.bin",
      ListBuffer("Computer-vision",
        "DB",
        "Emotion",
        "Net",
        "T0",
        "VM",
        "active directory",
        "api management",
        "app service",
        "application gateway",
        "backup",
        "classic",
        "cloud services",
        "event hubs",
        "expressroute",
        "icp",
        "iot suite",
        "linux",
        "power bi-workspace-collections",
        "redis cache",
        "service bus-messaging",
        "service bus-relay",
        "service health",
        "site recovery",
        "virtual network",
        "vpn-gateway",
        "windows",
        "一般问题",
        "执行与维护",
        "注册问题",
        "计费、订阅和发票"),
      10)

    val tests = Array("将经典部署 Azure 虚拟机备份到备份保管库",
      "通过API查看MySQL Database on Azure数据库信息",
      "Microsoft Azure 网络安全概述"
    )
    tests.foreach(
      s => println(s"classify($s) => ${classifyString(s)}")
    )
  }
}
