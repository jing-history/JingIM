package ml.jinggo.domain

import ml.jinggo.common.SystemConstant

import scala.beans.BeanProperty

/**
  * 返回值信息
  * code 状态，0表示成功，其他表示失败
  * msg 额外信息
  * Created by gz12 on 2018-07-09.
  */
class ResultSet[T](c: Int = SystemConstant.SUCCESS, m: String = SystemConstant.SUCCESS_MESSAGE) {

  @BeanProperty var code = c

  @BeanProperty var msg = m

  @BeanProperty var data: T = _

  def this() = {
    this(SystemConstant.SUCCESS, SystemConstant.SUCCESS_MESSAGE)
  }

  def this(data: T) = {
    this
    this.data = data
  }
}
