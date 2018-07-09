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
import org.springframework.web.bind.annotation.{RequestBody, RequestMapping, RequestMethod, RestController}

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

  @RequestMapping(value = Array("/test"), method = Array(RequestMethod.GET))
  def test(symbol: String) : String = {
    val msg = "Welcome to my IM"
    msg
  }
}
