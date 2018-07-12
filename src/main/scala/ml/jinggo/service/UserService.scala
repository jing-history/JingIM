package ml.jinggo.service

import javax.servlet.http.HttpServletRequest

import ml.jinggo.common.SystemConstant
import ml.jinggo.domain.{AddInfo, FriendList, GroupList, GroupMember}
import ml.jinggo.entity._
import ml.jinggo.repository.UserMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ml.jinggo.util.{DateUtil, SecurityUtil, UUIDUtil}
import org.apache.ibatis.annotations.{Delete, Param, Select}
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.cache.annotation.{CacheEvict, Cacheable}

import scala.collection.JavaConversions
import scala.collection.JavaConverters._
import java.util.List

/**
  * 用户信息相关操作
  * Created by gz12 on 2018-07-09.
  */
@Service
class UserService @Autowired()(private var userMapper: UserMapper) {

  /**
    * description 退出群
    * param groupMember
    */
  @CacheEvict(value = Array("findUserById","findFriendGroupsById","findUserByGroupId"), allEntries = true)
  def leaveOutGroup(gid: Integer, uid: Integer): Boolean = userMapper.leaveOutGroup(new GroupMember(gid, uid)) == 1

  /**
    * description 添加群成员
    * param gid 群编号
    * param uid 用户编号
    * param messageBoxId 消息盒子Id
    */
  @Transactional
  def addGroupMember(gid: Integer, uid: Integer, messageBoxId: Integer): Boolean = {
    if (gid == null || uid == null ) {
      return false
    } else {
      userMapper.addGroupMember(new GroupMember(gid, uid)) == 1
      updateAddMessage(messageBoxId, 1)
    }
  }

  /**
    * description 根据群名模糊查询群
    * param groupName
    * return
    */
  def findGroup(groupName: String): List[GroupList] = userMapper.findGroup(groupName)

  /**
    * description 根据群名模糊统计
    * param groupName
    * return
    */
  def countGroup(groupName: String): Int = userMapper.countGroup(groupName)

  /**
    * description 删除好友
    * param friendId 好友Id
    * param uId 个人Id
    * return Boolean
    */
  @CacheEvict(value = Array("findUserById","findFriendGroupsById","findUserByGroupId"), allEntries = true)
  def removeFriend(friendId: Integer, uId: Integer): Boolean = {
    if (friendId == null || uId == null)
      return false
    else
      userMapper.removeFriend(friendId, uId) == 1
  }

  /**
    * description 添加好友操作
    * param mid 我的id
    * param mgid 我设定的分组
    * param tid 对方的id
    * param tgid 对方设定的分组
    * param messageBoxId 消息盒子的消息id
    */
  @Transactional
  @CacheEvict(value = Array("findUserById","findFriendGroupsById","findUserByGroupId"), allEntries = true)
  def addFriend(mid: Integer, mgid: Integer, tid: Integer, tgid: Integer, messageBoxId: Integer): Boolean = {
    val add = new AddFriends(mid, mgid, tid, tgid)
    if (userMapper.addFriend(add) != 0) {
      updateAddMessage(messageBoxId, 1)
    }
    false
  }

  /**
    * description 更新好友、群组信息请求
    * param addMessage
    * return
    */
  @Transactional
  def updateAddMessage(messageBoxId: Integer, agree: Integer): Boolean = {
    var addMessage = new AddMessage
    addMessage.setAgree(agree)
    addMessage.setId(messageBoxId)
    userMapper.updateAddMessage(addMessage) == 1
  }

  /**
    * description 统计消息
    * param uid
    */
  def countUnHandMessage(uid: Integer, agree: Integer): Integer = userMapper.countUnHandMessage(uid, agree)

  /**
    * description 查询添加好友、群组信息
    * param uid
    * return List[AddInfo]
    */
  def findAddInfo(uid: Integer): List[AddInfo] = {
    val list = userMapper.findAddInfo(uid)
    JavaConversions.collectionAsScalaIterable(list).foreach { info => {
      if (info.Type == 0) {
        info.setContent("申请添加你为好友")
      } else {
        val group: GroupList = userMapper.findGroupById(info.getFrom_group)
        info.setContent("申请加入 '" + group.getGroupname + "' 群聊中!")
      }
      info.setHref(null)
      info.setUser(findUserById(info.getFrom))
      LOGGER.info(info.toString())
    } }
    list
  }

  /**
    * description 添加好友、群组信息请求
    * param addMessage
    * return
    */
  def saveAddMessage(addMessage: AddMessage): Int = userMapper.saveAddMessage(addMessage)

  /**
    * description 根据用户名和性别查询用户
    * param username
    * param sex
    */
  def findUsers(username: String, sex: Integer): List[User] = userMapper.findUsers(username, sex)

  /**
    * description 根据用户名和性别统计用户
    * param username
    * param sex
    */
  def countUsers(username: String, sex: Integer): Int = userMapper.countUser(username, sex)

  /**
    * 根据群组ID查询群里用户的信息
    * @param gid
    * @return List[User]
    */
  @Cacheable(value = Array("findUserByGroupId"), keyGenerator = "wiselyKeyGenerator")
  def findUserByGroupId(gid: Int): List[User] = userMapper.findUserByGroupId(gid)

  /**
    * 保存用户聊天记录
    * @param receive  聊天记录信息
    * @return
    */
  def saveMessage(receive: Receive): Int = userMapper.saveMessage(receive)

  /**
    * description 用户更新签名
    *
    * @param user
    * @return Boolean
    */
  def updateSing(user: User): Boolean = {
    if (user == null || user.getSign == null || user.getId == null) {
      return false
    } else {
      return userMapper.updateSign(user.getSign, user.getId) == 1
    }
  }

  /**
    * description 根据ID查询用户好友分组列表信息
    * param uid 用户ID
    * return List[FriendList]
    */
  @Cacheable(value = Array("findFriendGroupsById"), keyGenerator = "wiselyKeyGenerator")
  def findFriendGroupsById(uid: Int): List[FriendList] = {
    var friends = userMapper.findFriendGroupsById(uid)
    //封装分组列表下的好友信息
    JavaConversions.collectionAsScalaIterable(friends).foreach {
      friend:FriendList => {
        friend.list = userMapper.findUsersByFriendGroupIds(friend.getId)
      }
    }
    friends
  }

  /**
    * description 根据ID查询用户群组信息
    * param id
    * return List[Group]
    */
  @Cacheable(value = Array("findGroupsById"), keyGenerator = "wiselyKeyGenerator")
  def findGroupsById(id: Int): List[GroupList] = {
    userMapper.findGroupsById(id)
  }

  /**
    * description 根据ID查询用户信息
    * param id
    * return User
    */
  @Cacheable(value = Array("findUserById"), keyGenerator = "wiselyKeyGenerator")
  def findUserById(id: Integer): User = {
    if (id != null) userMapper.findUserById(id) else null
  }

  /**
    * 用户邮件和密码是否匹配
    *
    * @param user
    * @return
    */
  def matchUser(user: User): User = {
    if (user == null || user.getEmail == null) {
      return null
    }
    val u: User = userMapper.matchUser(user.getEmail)
    //密码不匹配
    if(u == null || !SecurityUtil.matchs(user.getPassword, u.getPassword)){
      return null
    }
    u
  }

  def existEmail(email: String): Boolean = {
    if (email == null || "".equals(email))
      return false
    else
      userMapper.matchUser(email) != null
  }


  private final val LOGGER: Logger = LoggerFactory.getLogger(classOf[UserService])

  def createFriendGroup( groupName: String, uid: Integer): Boolean = {
    if (uid == null || groupName == null || "".equals(uid) || "".equals(groupName))
      return false
    else
      userMapper.createFriendGroup(new FriendGroup(uid, groupName)) == 1
  }

  @Transactional
  def saveUser(user: User, request: HttpServletRequest): Boolean = {
    if (user == null || user.getUsername == null || user.getPassword == null || user.getEmail == null) {
      return false
    } else {
      //激活码
      val activeCode = UUIDUtil.getUUID64String
      user.setActive(activeCode)
      user.setCreateDate(DateUtil.getDate)
      //加密密码
      user.setPassword(SecurityUtil.encrypt(user.getPassword))
      userMapper.saveUser(user)
      LOGGER.info("userid = " + user.getId)
      //创建默认的好友分组
      createFriendGroup(SystemConstant.DEFAULT_GROUP_NAME, user.getId)
      //@Todo 邮件发送功能还没有实现
    }
    true
  }
}
