package ml.jinggo.domain

import ml.jinggo.entity.User

import scala.beans.BeanProperty

/**
  * 返回添加好友、群组消息
  * Created by gz12 on 2018-07-03.
  */
class AddInfo {

  @BeanProperty val id: Integer = _

  @BeanProperty var uid: Integer = _

  @BeanProperty var content: String = _

  @BeanProperty var from: Integer = _

  @BeanProperty var from_group: Int = _

  @BeanProperty var Type: Int = _

  @BeanProperty var remark: String = _

  @BeanProperty var href: String = _

  @BeanProperty var read: Integer = _

  @BeanProperty var time: String = _

  @BeanProperty var user: User = _

  override def toString = "id=" + id + ",uid=" + uid + ",content="+ content + ",from=" + from + ",from_group=" + from_group + ",Type=" + Type + ",remark=" + remark + ",href=" + href + ",read=" + read + ",time=" + time + ",user=" + user
}
