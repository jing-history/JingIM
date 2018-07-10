package ml.jinggo.service

import javax.servlet.http.HttpServletRequest

import ml.jinggo.common.SystemConstant
import ml.jinggo.domain.{FriendList, GroupList}
import ml.jinggo.entity.{FriendGroup, User}
import ml.jinggo.repository.UserMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ml.jinggo.util.{DateUtil, SecurityUtil, UUIDUtil}
import org.apache.ibatis.annotations.Select
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.cache.annotation.Cacheable

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
    * description 用户更新签名
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
