package ml.jinggo.entity

import scala.beans.BeanProperty

/**
  * 聊天记录返回消息
  * Created by gz12 on 2018-07-12.
  */
class ChatHistory {
  //用户id
  @BeanProperty var id: Integer = _

  //用户名
  @BeanProperty var username: String = _

  //用户头像
  @BeanProperty var avatar: String = _

  //消息内容
  @BeanProperty var content: String = _

  //时间
  @BeanProperty var timestamp: Long = _

  def this(id: Integer, username: String, avatar: String, content: String, timestamp: Long) = {
    this
    this.id = id
    this.username = username
    this.avatar = avatar
    this.content = content
    this.timestamp = timestamp
  }
}
