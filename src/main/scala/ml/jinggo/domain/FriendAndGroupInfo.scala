package ml.jinggo.domain

import ml.jinggo.entity.User

import scala.beans.BeanProperty
import java.util.List

/**
  * Created by gz12 on 2018-07-10.
  */
class FriendAndGroupInfo {

  //我的信息
  @BeanProperty
  var mine: User = _

  //好友列表
  @BeanProperty
  var friend: List[FriendList] = _

  //群组分组
  @BeanProperty
  var group: List[GroupList] = _
}
