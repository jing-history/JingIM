package ml.jinggo.util

import java.util.UUID

/**
  * UUID工具
  * Created by gz12 on 2018-07-09.
  */
object UUIDUtil {
  /**
    * 64位随机UUID
    * @return String
    */
  def getUUID64String(): String = (UUID.randomUUID.toString + UUID.randomUUID.toString).replace("-", "")

  /**
    * 32位随机UUID
    * @return String
    */
  def getUUID32String(): String = UUID.randomUUID.toString.replace("-", "")

}
