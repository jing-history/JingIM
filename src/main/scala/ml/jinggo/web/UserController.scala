package ml.jinggo.web

import com.google.gson.Gson
import io.swagger.annotations.Api
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.web.bind.annotation.{RequestMapping, RequestMethod, RestController}

/**
  * Created by gz12 on 2018-07-03.
  */
@RestController
@Api(value = "用户相关操作")
@RequestMapping(value = Array("/user"))
class UserController {

  private final val LOGGER: Logger = LoggerFactory.getLogger(classOf[UserController])

  private final val gson: Gson = new Gson

  @RequestMapping(value = Array("/test"), method = Array(RequestMethod.GET))
  def test(symbol: String) : String = {
    val msg = "Welcome to my IM"
    msg
  }
}
