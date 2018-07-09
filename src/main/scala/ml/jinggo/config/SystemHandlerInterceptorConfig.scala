package ml.jinggo.config

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import org.slf4j.{Logger, LoggerFactory}
import org.springframework.web.servlet.{HandlerInterceptor, ModelAndView}

/**
  * 系统拦截器配置
  */
class SystemHandlerInterceptorConfig extends HandlerInterceptor {

  private final val LOGGER: Logger = LoggerFactory.getLogger(classOf[SystemHandlerInterceptorConfig])

  /**
    * 前置处理器，在请求处理之前调用
    * @param request
    * @param response
    * @param handler
    * @return Boolean
    */
  override def preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Object): Boolean = {
    LOGGER.info("前置处理器，在请求处理之前调用")
    /*if(request.getSession.getAttribute("user") == null) {
      response.sendRedirect("/")
      return false
    } else {
      return true
    }*/
    return true
  }

  /**
    * 请求处理之后进行调用，但是在视图被渲染之前(Controller方法调用之后)
    * @param request
    * @param response
    * @param handler
    * @param modelAndView
    */
  override def postHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Object,
                          modelAndView: ModelAndView): Unit = {
  }

  /**
    * 后置处理器，渲染视图完成
    * @param request
    * @param response
    * @param handler
    * @param ex
    */
  override def afterCompletion(request: HttpServletRequest, response: HttpServletResponse, handler: Object,
                               ex: Exception) = {

  }
}
