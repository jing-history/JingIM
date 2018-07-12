package ml.jinggo.util

import java.util
import java.util._
import java.util.Collections

import ml.jinggo.MyIMApplication
import org.slf4j.{Logger, LoggerFactory}
import javax.websocket.Session

import com.google.gson.Gson
import ml.jinggo.common.SystemConstant
import ml.jinggo.entity._
import ml.jinggo.service.{RedisService, UserService}
import ml.jinggo.websocket.domain.Domain

import scala.beans.BeanProperty
import scala.collection.JavaConversions

/**
  * WebSocket工具 单例
  * Created by gz12 on 2018-07-11.
  */
object WebSocketUtil {

  private final val LOGGER: Logger = LoggerFactory.getLogger(WebSocketUtil.getClass)

  private final lazy val application = MyIMApplication.getApplicationContext

  @BeanProperty final var sessions = Collections.synchronizedMap(new HashMap[Integer, Session]())

  private lazy val redisService: RedisService = application.getBean(classOf[RedisService])

  private lazy val userService: UserService = application.getBean(classOf[UserService])

  private final val gson: Gson = new Gson

  def sendMessage(message: Message): Unit = synchronized {
    LOGGER.info("发送好友消息和群消息!");
    //封装返回消息格式
    var gid = message.getTo.getId
    val receive = WebSocketUtil.getReceiveType(message)
    val key: Integer = message.getTo.getId
    //聊天类型，可能来自朋友或者群组
    if("friend".equals(message.getTo.getType)){
      //是否在线
      if(WebSocketUtil.getSessions.containsKey(key)){
        val seesion: Session = WebSocketUtil.getSessions.get(key)
        receive.setStatus(1)
        WebSocketUtil.sendMessage(gson.toJson(receive).replaceAll("Type","type"), seesion)
      }
      //保存为离线消息,默认是为离线消息
      userService.saveMessage(receive)
    }else{
      //群组消息
      receive.setId(gid)
      //找到群组id里面的所有用户
      val users: List[User] = userService.findUserByGroupId(gid)
      //过滤掉本身的uid
      JavaConversions.collectionAsScalaIterable(users).filter(_.id != message.getMine.getId)
        .foreach{ user => {
          //是否在线
          if(WebSocketUtil.getSessions.containsKey(user.getId)){
            val session: Session = WebSocketUtil.getSessions.get(user.getId)
            receive.setStatus(1)
            WebSocketUtil.sendMessage(gson.toJson(receive).replaceAll("Type","type"), session)
          } else {
            receive.setId(key)
          }
        }}
      //保存为离线消息,默认是为离线消息
      userService.saveMessage(receive)
    }
  }

  /**
    * description 统计离线消息数量
    * param uid
    */
  def countUnHandMessage(uid: Integer): HashMap[String, String] = synchronized{
    val count = userService.countUnHandMessage(uid, 0)
    LOGGER.info("count = " + count)
    var result = new HashMap[String, String]
    result.put("type", "unHandMessage")
    result.put("count", count + "")
    result
  }

  /**
    * description 拒绝添加群
    * param mess
    */
  def refuseAddGroup(mess: Message): Unit = {
    val refuse = gson.fromJson(mess.getMsg, classOf[Domain.AgreeAddGroup])
    userService.updateAddMessage(refuse.getMessageBoxId, 2)
  }

  /**
    * description 用户在线切换状态
    * param uid 用户id
    * param status 状态
    */
  def changeOnline(uid: Integer, status: String) = synchronized {
    if ("online".equals(status)) {
      redisService.setSet(SystemConstant.ONLINE_USER, uid + "")
    } else {
      redisService.removeSetValue(SystemConstant.ONLINE_USER, uid + "")
    }
  }

  /**
    * 监测某个用户的离线或者在线
    * @param message
    * @param session
    * @return
    */
  def checkOnline(message: Message, session: Session): HashMap[String, String] = synchronized {
    LOGGER.info("监测在线状态" + message.getTo.toString)
    val uids = redisService.getSets(SystemConstant.ONLINE_USER)
    var result = new HashMap[String, String]
    result.put("type","checkOnline")
    if(uids.contains(message.getTo.getId.toString))
      result.put("status", "在线")
    else
      result.put("status", "离线")
    result
  }

  /**
    * description 同意添加好友
    * param mess
    */
  def agreeAddGroup(mess: Message): Unit = {
    val agree = gson.fromJson(mess.getMsg, classOf[Domain.AgreeAddGroup])
    userService.addGroupMember(agree.getGroupId, agree.getToUid, agree.getMessageBoxId)
  }

  /**
    * description 添加群组
    * param uid
    * param message
    */
  def addGroup(uid: Integer, message: Message):Unit = synchronized {
    val addMessage = new AddMessage
    val mine = message.getMine
    val to = message.getTo
    val t = gson.fromJson(message.getMsg, classOf[Domain.Group])
    addMessage.setFromUid(mine.getId)
    addMessage.setToUid(to.getId)
    addMessage.setTime(DateUtil.getDateTime)
    addMessage.setGroupId(t.getGroupId)
    addMessage.setRemark(t.getRemark)
    addMessage.setType(1)
    userService.saveAddMessage(addMessage)
    var result = new HashMap[String, String]
    if (sessions.get(to.getId) != null) {
      result.put("type", "addGroup")
      sendMessage(gson.toJson(result), sessions.get(to.getId))
    }
  }
  /**
    * description 通知对方删除好友
    * param uId我的id
    * param friendId 对方Id
    */
  def removeFriend(uId: Integer, friendId: Integer) = synchronized {
    //对方是否在线，在线则处理，不在线则不处理
    var result = new HashMap[String, String]
    if(sessions.get(friendId) != null) {
      result.put("type", "delFriend");
      result.put("uId", uId + "");
      WebSocketUtil.sendMessage(gson.toJson(result), sessions.get(friendId))
    }
  }

  def addFriend(uid: Integer, message: Message): Unit = synchronized {
    val mine = message.getMine
    val addMessage = new AddMessage
    addMessage.setFromUid(mine.getId)
    addMessage.setTime(DateUtil.getDateTime)
    addMessage.setToUid(message.getTo.getId)
    val add = gson.fromJson(message.getMsg(), classOf[Add])
    addMessage.setRemark(add.getRemark)
    addMessage.setType(add.getType)
    addMessage.setGroupId(add.getGroupId)
    userService.saveAddMessage(addMessage)
    var result = new HashMap[String, String]
    //如果对方在线，则推送给对方
    if (sessions.get(message.getTo.getId) != null) {
      result.put("type", "addFriend")
      sendMessage(gson.toJson(result), sessions.get(message.getTo.getId))
    }
  }

  /**
    * 发送消息
    * @param message
    * @param session
    */
  def sendMessage(message: String, session: Session):Unit = synchronized{
    session.getBasicRemote().sendText(message)
  }

  /**
    * description 封装返回消息格式
    * param Message
    * return Receive
    */
  def getReceiveType(message: Message): Receive = {
    val mine = message.getMine
    val to = message.getTo
    var receive = new Receive
    receive.setId(mine.getId)
    receive.setFromid(mine.getId)
    receive.setToid(to.getId)
    receive.setUsername(mine.getUsername)
    receive.setType(to.getType)
    receive.setAvatar(mine.getAvatar)
    receive.setContent(mine.getContent)
    receive.setTimestamp(DateUtil.getLongDateTime)
    receive
  }
}
