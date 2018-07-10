package ml.jinggo.service

import javax.servlet.http.HttpServletRequest

import ml.jinggo.entity.User
import ml.jinggo.repository.UserMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ml.jinggo.util.{DateUtil, SecurityUtil, UUIDUtil}
import org.slf4j.{Logger, LoggerFactory}

/**
  * 用户信息相关操作
  * Created by gz12 on 2018-07-09.
  */
@Service
class UserService @Autowired()(private var userMapper: UserMapper) {

  /**
    * 用户邮件和密码是否匹配
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
      true
    }
  }
}
