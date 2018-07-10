package ml.jinggo.entity

import scala.beans.BeanProperty

/**
  * 好友分组对象
  * Created by gz12 on 2018-07-10.
  */
class FriendGroup {

  //用户id
  @BeanProperty var uid: Integer = _

  //群组名称
  @BeanProperty var groupName: String = _

  def this(uid: Integer, groupName: String) = {
    this
    this.uid = uid
    this.groupName = groupName
  }

  override def toString = "uid = " + uid + ",groupName = " + groupName
}
