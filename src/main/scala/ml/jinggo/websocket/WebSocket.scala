package ml.jinggo.websocket

import javax.websocket._
import javax.websocket.server.{PathParam, ServerEndpoint}

import com.google.gson.Gson
import ml.jinggo.MyIMApplication
import ml.jinggo.common.SystemConstant
import ml.jinggo.entity.Message
import ml.jinggo.service.RedisService
import ml.jinggo.util.WebSocketUtil
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.PathVariable
/**
  * websocket服务器处理消息
  * Created by gz12 on 2018-07-11.
  */
@ServerEndpoint(value = "/websocket/{uid}")
@Component
class WebSocket {

  private final val LOGGER: Logger = LoggerFactory.getLogger(classOf[WebSocket])

  private lazy val redisService: RedisService = MyIMApplication.getApplicationContext.getBean(classOf[RedisService])

  private final val gson: Gson = new Gson

  private var uid: Integer = _

  @OnMessage
  def onMessage(message: String, session: Session): Unit ={
    val mess = gson.fromJson(message.replaceAll("type","Type"),classOf[Message])
    LOGGER.info("来自客户端的消息: " + mess)
    mess.getType match {
      case "message" => {
        WebSocketUtil.sendMessage(mess)
      }
      case "addFriend" => {
        WebSocketUtil.addFriend(uid, mess)
      }
      case _ => {
        LOGGER.info("No Mapping Message!")
      }
    }
  }

  /**
    * 首次创建链接
    * @param session
    * @param uid
    */
  @OnOpen
  def onOpen(session: Session, @PathParam("uid") uid: Integer): Unit = {
    this.uid = uid
    WebSocketUtil.sessions.put(uid, session)
    LOGGER.info("userId = " + uid + ",sessionId = " + session.getId + ",新连接加入!")
    redisService.setSet(SystemConstant.ONLINE_USER, uid + "")
  }

  /**
    * 连接关闭调用
    * @param session
    */
  @OnClose
  def onClose(session: Session) = {
    LOGGER.info("userId = " + uid + ",sessionId = " + session.getId + "断开连接!")
    WebSocketUtil.getSessions.remove(uid)
    redisService.removeSetValue(SystemConstant.ONLINE_USER, uid + "")
  }

  /**
    * 服务器发送错误调用
    * @param session
    */
  @OnError
  def onError(session: Session, error: Throwable) = {
    LOGGER.info(session.getId + " 发生错误" + error.printStackTrace)
    onClose(session)
  }
}
