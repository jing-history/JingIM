package ml.jinggo.web

import javax.servlet.http.HttpServletRequest

import com.google.gson.Gson
import io.swagger.annotations.Api
import ml.jinggo.common.SystemConstant
import ml.jinggo.domain.ResultSet
import ml.jinggo.entity.User
import ml.jinggo.service.UserService
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation._

/**
  * Created by gz12 on 2018-07-03.
  */
@RestController
@Api(value = "用户相关操作")
@RequestMapping(value = Array("/user"))
class UserController @Autowired()(private val userService : UserService){

  private final val LOGGER: Logger = LoggerFactory.getLogger(classOf[UserController])

  private final val gson: Gson = new Gson

  /**
    * 注册
    * @param user
    * @param request
    * @return
    */
  @RequestMapping(value = Array("/register"), method = Array(RequestMethod.POST))
  def register(@RequestBody user: User, request: HttpServletRequest): String = {
    if(userService.saveUser(user, request)) {
      gson.toJson(new ResultSet[String](SystemConstant.SUCCESS, SystemConstant.REGISTER_SUCCESS))
    }else{
      gson.toJson(new ResultSet[String](SystemConstant.ERROR, SystemConstant.REGISTER_FAIL))
    }
  }

  /**
    * 判断邮件是否存在
    * @param email
    * @return
    */
  @RequestMapping(value = Array("/existEmail"), method = Array(RequestMethod.POST))
  def existEmail(@RequestParam("email") email: String): String = {
    gson.toJson(new ResultSet(userService.existEmail(email)))
  }

  /**
    * 登录
    * @param user
    * @param request
    * @return
    */
  @RequestMapping(value = Array("/login"), method = Array(RequestMethod.POST))
  def login(@RequestBody user: User, request: HttpServletRequest): String = {
    val u: User = userService.matchUser(user)
    //未激活
    if (u != null && "nonactivated".equals(u.getStatus)) {
      return gson.toJson(new ResultSet[User](SystemConstant.ERROR, SystemConstant.NONACTIVED))
    } else if(u != null && !"nonactivated".equals(u.getStatus)) {
      LOGGER.info(user + "成功登陆服务器")
      request.getSession.setAttribute("user", u);
      return gson.toJson(new ResultSet[User](u))
    } else {
      var result = new ResultSet[User](SystemConstant.ERROR, SystemConstant.LOGGIN_FAIL)
      gson.toJson(result)
    }
  }
}
