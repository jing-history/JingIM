package ml.jinggo.web

import javax.servlet.http.HttpServletRequest

import com.google.gson.Gson
import io.swagger.annotations.{Api, ApiOperation}
import ml.jinggo.common.SystemConstant
import ml.jinggo.domain.{FriendAndGroupInfo, ResultSet}
import ml.jinggo.entity.User
import ml.jinggo.service.UserService
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation._

/**
  * Created by gz12 on 2018-07-03.
  */
@Controller
@Api(value = "用户相关操作")
@RequestMapping(value = Array("/user"))
class UserController @Autowired()(private val userService : UserService){

  private final val LOGGER: Logger = LoggerFactory.getLogger(classOf[UserController])

  private final val gson: Gson = new Gson

  /**
    * description 更新签名
    * @param sign
    *
    */
  @ResponseBody
  @RequestMapping(value = Array("/updateSign"), method = Array(RequestMethod.POST))
  def updateSign(request: HttpServletRequest, @RequestParam("sign") sign: String): String = {
    val user:User = request.getSession.getAttribute("user").asInstanceOf[User]
    user.setSign(sign)
    if(userService.updateSing(user)) {
      gson.toJson(new ResultSet)
    } else {
      gson.toJson(new ResultSet(SystemConstant.ERROR, SystemConstant.ERROR_MESSAGE))
    }
  }

  /**
    * 初始化主界面数据
    */
  @ResponseBody
  @ApiOperation("初始化聊天界面数据，分组列表好友信息、群列表")
  @RequestMapping(value = Array("/init/{userId}"), method = Array(RequestMethod.POST))
  def init(@PathVariable("userId") userId: Int): String = {
    var data = new FriendAndGroupInfo
    //用户信息
    var user = userService.findUserById(userId)
    user.setStatus("online")
    data.mine = user
    //用户群组列表
    data.group = userService.findGroupsById(userId)
    //用户好友列表
    data.friend = userService.findFriendGroupsById(userId)
    gson.toJson(new ResultSet[FriendAndGroupInfo](data))
  }

  /**
    * 跳转主页
    */
  @RequestMapping(value = Array("/index"), method = Array(RequestMethod.GET))
  def index(model: Model, request: HttpServletRequest): String = {
    val user = request.getSession.getAttribute("user")
    model.addAttribute("user", user)
    LOGGER.info("用户" + user + "登陆服务器")
    "index"
  }

  /**
    * 注册
    * @param user
    * @param request
    * @return
    */
  @RequestMapping(value = Array("/register"), method = Array(RequestMethod.POST))
  @ResponseBody
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
  @ResponseBody
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
  @ResponseBody
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
