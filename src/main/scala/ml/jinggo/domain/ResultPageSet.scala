package ml.jinggo.domain

import scala.beans.BeanProperty

/**
  * 分页结果集
  * Created by gz12 on 2018-07-11.
  */
class ResultPageSet[T] extends ResultSet[T] {

  @BeanProperty var pages: Int = _

  def this(data: T) = {
    this
    this.data = data
  }

  def this(data: T, pages: Int) = {
    this
    this.data = data
    this.pages = pages
  }
}
