package ml.jinggo.web

import java.util._
import javax.servlet.http.HttpServletRequest

import com.github.pagehelper.PageHelper
import com.google.gson.Gson
import io.swagger.annotations.{Api, ApiOperation}
import ml.jinggo.common.SystemConstant
import ml.jinggo.domain.{FriendAndGroupInfo, ResultPageSet, ResultSet}
import ml.jinggo.entity.User
import ml.jinggo.service.UserService
import ml.jinggo.util.FileUtil
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation._
import org.springframework.web.multipart.MultipartFile

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
    * description 客户端上传文件
    * param file
    * param request
    * return
    */
  @ResponseBody
  @RequestMapping(value = Array("/upload/file"), method = Array(RequestMethod.POST))
  def uploadFile(@RequestParam("file") file:MultipartFile,request: HttpServletRequest): String = {
    if (file.isEmpty()) {
      return gson.toJson(new ResultSet(SystemConstant.ERROR, SystemConstant.UPLOAD_FAIL))
    }
    val path = request.getServletContext.getRealPath("/")
    val src = FileUtil.upload(SystemConstant.FILE_PATH, path, file)
    var result = new HashMap[String, String]
    //文件的相对路径地址
    result.put("src", src)
    result.put("name", file.getOriginalFilename)
    LOGGER.info("文件" + file.getOriginalFilename + "上传成功")
    gson.toJson(new ResultSet[HashMap[String, String]](result))
  }

  /**
    * description 客户端上传图片
    * param file
    * param request
    * return
    */
  @ResponseBody
  @RequestMapping(value = Array("/upload/image"), method = Array(RequestMethod.POST))
  def uploadImage(@RequestParam("file") file: MultipartFile, request: HttpServletRequest): String = {
    if (file.isEmpty()) {
      return gson.toJson(new ResultSet(SystemConstant.ERROR, SystemConstant.UPLOAD_FAIL))
    }
    val path = request.getServletContext.getRealPath("/")
    val src = FileUtil.upload(SystemConstant.IMAGE_PATH, path, file)
    var result = new HashMap[String, String]
    //图片的相对路径地址
    result.put("src", src)
    LOGGER.info("图片" + file.getOriginalFilename + "上传成功")
    gson.toJson(new ResultSet[HashMap[String, String]](result))
  }

  /**
    * description 弹出聊天记录页面
    * param id 与谁的聊天记录id
    * param Type 类型，可能是friend或者是group
    */
  @RequestMapping(value = Array("/chatLogIndex"), method = Array(RequestMethod.GET))
  def chatLogIndex(@RequestParam("id") id: Integer, @RequestParam("Type") Type: String,
                   model: Model, request: HttpServletRequest): String = {
    model.addAttribute("id", id)
    model.addAttribute("Type", Type)
    val user = request.getSession.getAttribute("user").asInstanceOf[User]
    var pages: Int = userService.countHistoryMessage(user.getId, id, Type)
    pages = if (pages < SystemConstant.SYSTEM_PAGE) pages else (pages / SystemConstant.SYSTEM_PAGE + 1)
    model.addAttribute("pages", pages)
    "chatLog"
  }

  /**
    * description 退出群
    * param groupId 群编号
    * param request
    * return
    */
  @ResponseBody
  @RequestMapping(value = Array("/leaveOutGroup"), method = Array(RequestMethod.POST))
  def leaveOutGroup(@RequestParam("groupId") groupId: Integer, request: HttpServletRequest): String = {
    val user = request.getSession.getAttribute("user").asInstanceOf[User]
    val result = userService.leaveOutGroup(groupId, user.getId)
    gson.toJson(new ResultSet(result))
  }

  /**
    * description 分页查找群组
    * param page 第几页
    * param name 群名称
    */
  @ResponseBody
  @RequestMapping(value = Array("/findGroups"), method = Array(RequestMethod.GET))
  def findGroups(@RequestParam(value = "page",defaultValue = "1") page: Int,
                 @RequestParam(value = "name", required = false) name: String): String = {
    val count = userService.countGroup(name)
    val pages = if (count < SystemConstant.USER_PAGE) 1 else (count / SystemConstant.USER_PAGE + 1)
    PageHelper.startPage(page, SystemConstant.USER_PAGE)
    val groups = userService.findGroup(name)
    var result = new ResultPageSet(groups)
    result.setPages(pages)
    gson.toJson(result)
  }

  /**
    * description 删除好友
    * param friendId
    * return
    */
  @ResponseBody
  @RequestMapping(value = Array("/removeFriend"), method = Array(RequestMethod.POST))
  def removeFriend(@RequestParam("friendId") friendId: Integer,request: HttpServletRequest): String = {
    val user = request.getSession.getAttribute("user").asInstanceOf[User]
    val result = userService.removeFriend(friendId, user.getId)
    gson.toJson(new ResultSet(result))
  }

  /**
    * description 拒绝添加好友
    * param request
    * param messageBoxId 消息盒子的消息id
    * return
    */
  @ResponseBody
  @RequestMapping(value = Array("/refuseFriend"), method = Array(RequestMethod.POST))
  def refuseFriend(@RequestParam("messageBoxId") messageBoxId: Integer,request: HttpServletRequest): String = {
    val result = userService.updateAddMessage(messageBoxId, 2)
    gson.toJson(new ResultSet(result))
  }

  /**
    * description 添加好友
    * param uid 对方用户ID
    * param fromGroup 对方设定的好友分组
    * param group 我设定的好友分组
    * param messageBoxId 消息盒子的消息id
    * return String
    */
  @ResponseBody
  @RequestMapping(value = Array("/agreeFriend"), method = Array(RequestMethod.POST))
  def agreeFriend(@RequestParam("uid") uid: Integer,@RequestParam("from_group") fromGroup: Integer,
                  @RequestParam("group") group: Integer, @RequestParam("messageBoxId") messageBoxId: Integer,
                  request: HttpServletRequest): String = {
    val user = request.getSession.getAttribute("user").asInstanceOf[User]
    val result = userService.addFriend(user.getId, group, uid, fromGroup, messageBoxId)
    gson.toJson(new ResultSet(result))
  }

  /**
    * description 查询消息盒子信息
    * param uid
    * param page
    */
  @ResponseBody
  @RequestMapping(value = Array("/findAddInfo"), method = Array(RequestMethod.GET))
  def findAddInfo(@RequestParam("uid") uid: Integer, @RequestParam("page") page: Int): String = {
    PageHelper.startPage(page, SystemConstant.ADD_MESSAGE_PAGE)
    val list = userService.findAddInfo(uid)
    val count = userService.countUnHandMessage(uid, null).toInt
    val pages = if (count < SystemConstant.ADD_MESSAGE_PAGE) 1 else count / SystemConstant.ADD_MESSAGE_PAGE + 1
    gson.toJson(new ResultPageSet(list, pages)).replaceAll("Type", "type")
  }
  /**
    * description 分页查找好友
    * param page 第几页
    * param name 好友名字
    * param sex 性别
    */
  @ResponseBody
  @RequestMapping(value = Array("/findUsers"), method = Array(RequestMethod.GET))
  def findUsers(@RequestParam(value = "page",defaultValue = "1") page: Int,
                @RequestParam(value = "name", required = false) name: String,
                @RequestParam(value = "sex", required = false) sex: Integer): String ={
    val count = userService.countUsers(name, sex)
    val pages = if(count < SystemConstant.USER_PAGE) 1 else (count / SystemConstant.USER_PAGE + 1)
    PageHelper.startPage(page, SystemConstant.USER_PAGE)
    val users = userService.findUsers(name, sex)
    var result = new ResultPageSet(users)
    result.setPages(pages)
    gson.toJson(result)
  }

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
