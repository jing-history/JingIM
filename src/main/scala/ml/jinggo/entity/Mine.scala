package ml.jinggo.entity

import scala.beans.BeanProperty

/**
  * 我发送的消息和我的信息
  * Created by gz12 on 2018-07-11.
  */
class Mine {

  //我的id
  @BeanProperty var id: Int = _

  //我的昵称
  @BeanProperty var username: String = _

  //是否我发的消息
  @BeanProperty var mine: Boolean = _

  //我的头像
  @BeanProperty var avatar: String = _

  //消息内容
  @BeanProperty var content: String = _

  override def toString = "id = " + id + ", username = " + username + ", mine = " + mine + ", avatar = " + avatar + ", content = " + content
}
