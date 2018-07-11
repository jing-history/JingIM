package ml.jinggo.repository

import ml.jinggo.domain.{AddInfo, FriendList, GroupList}
import ml.jinggo.entity._
import org.apache.ibatis.annotations._
import java.util.List

/**
  * trait 当成java 的抽象类，但是trait 支持多重继承
  * Created by gz12 on 2018-07-09.
  */
trait UserMapper {

  /**
    * description 更新好友、群组信息请求
    * param addMessage
    * return
    */
  @Update(Array("update t_add_message set agree = #{agree} where id = #{id}"))
  def updateAddMessage(addMessage: AddMessage): Int

  /**
    * description 添加好友操作
    * param mgid 分组id
    * param tid 对方用户id
    * param mid 自己的id
    * param tgid 对方分组id
    */
  @Insert(Array("insert into t_friend_group_friends(fgid,uid) values(#{mgid},#{tid}),(#{tgid},#{mid})"))
  def addFriend(addFriends: AddFriends): Int

  /**
    * description 根据群id查询群信息
    * param gid
    * return
    */
  @Select(Array("select id,group_name,avatar,create_id from t_group where id = #{gid}"))
  def findGroupById(@Param("gid") gid: Integer): GroupList

  /**
    * description 统计未处理的消息
    * param uid
    */
  @Select(Array("<script> select count(*) from t_add_message where to_uid=#{uid} <if test='agree!=null'> and agree=#{agree} </if> </script>"))
  def countUnHandMessage(@Param("uid") uid: Integer, @Param("agree") agree: Integer): Integer

  /**
    * description 查询添加好友、群组信息
    * param uid
    * return List[AddInfo]
    */
  @Select(Array("select * from t_add_message where to_uid = #{uid} order by time desc"))
  @Results(value = Array(new Result(property="from",column="from_uid"),
    new Result(property="uid",column="to_uid"),
    new Result(property="read",column="agree"),
    new Result(property="from_group",column="group_id"))
  )
  def findAddInfo(@Param("uid") uid: Integer): List[AddInfo]

  /**
    * description 添加好友、群组信息请求
    * param addMessage
    * return
    */
  @Insert(Array("insert into t_add_message(from_uid,to_uid,group_id,remark,agree,type,time) values (#{fromUid},#{toUid},#{groupId},#{remark},#{agree},#{Type},#{time}) ON DUPLICATE KEY UPDATE remark=#{remark},time=#{time},agree=#{agree};"))
  def saveAddMessage(addMessage: AddMessage): Int

  /**
    * description 根据用户名和性别查询用户
    * param username
    * param sex
    */
  @Select(Array("<script> select id,username,status,sign,avatar,email from t_user where 1=1 <if test='username != null'> and username like '%${username}%'</if><if test='sex != null'> and sex=#{sex}</if></script>"))
  def findUsers(@Param("username")  username: String, @Param("sex") sex: Integer): List[User]

  /**
    * description 根据用户名和性别统计用户
    * param username
    * param sex
    */
  @Select(Array("<script> select count(*) from t_user where 1 = 1 <if test='username != null'> and username like '%${username}%'</if><if test='sex != null'> and sex=#{sex}</if></script>"))
  def countUser(@Param("username")  username: String, @Param("sex") sex: Integer): Int

  /**
    * description 根据群组ID查询群里用户的信息
    * param gid
    * return List[User]
    */
  @Select(Array("select id,username,status,sign,avatar,email from t_user where id in(select uid from t_group_members where gid = #{gid})"))
  def findUserByGroupId(gid: Int): List[User]

  /**
    * 保存用户聊天记录
    * @param receive 聊天记录信息
    * @return
    */
  @Insert(Array("insert into t_message(mid,toid,fromid,content,type,timestamp,status) values(#{id},#{toid},#{fromid},#{content},#{type},#{timestamp},#{status})"))
  def saveMessage(receive: Receive): Int

  /**
    * description 更新签名
    */
  @Update(Array("update t_user set sign = #{sign} where id = #{uid}"))
  def updateSign(@Param("sign") sign: String, @Param("uid") uid: Integer): Int

  /**
    * description 根据好友列表ID查询用户信息
    * param fgid
    * return List[User]
    */
  @Select(Array("select id,username,avatar,sign,status,email,sex from t_user where id in(select uid from t_friend_group_friends where fgid = #{fgid})"))
  def findUsersByFriendGroupIds(fgid: Int): List[User]

  /**
    * description 根据ID查询用户好友分组列表
    * param uid 用户ID
    * return List[FriendList]
    */
  @Select(Array("select id, group_name from t_friend_group where uid = #{uid}"))
  def findFriendGroupsById(uid: Int): List[FriendList]

  /**
    * description 根据ID查询用户群组列表,不管是自己创建的还是别人创建的
    * param uid 用户ID
    * return List[Group]
    */
  @Results(value = Array(new Result(property="createId",column="create_id")))
  @Select(Array("select id,group_name,avatar,create_id from t_group where id in(select distinct gid from t_group_members where uid = #{uid})"))
  def findGroupsById(uid: Int): List[GroupList]

  /**
    * description 根据ID查询用户信息
    * param id
    * return User
    */
  @Select(Array("select id,username,status,sign,avatar,email,sex,create_date from t_user where id = #{id}"))
  def findUserById(id: Int): User

  /**
    * description 创建好友分组列表
    * param uid
    * param groupName
    */
  @Insert(Array("insert into t_friend_group(group_name,uid) values(#{groupName},#{uid})"))
  def createFriendGroup(friendGroup: FriendGroup): Int

  /**
    * description 保存用户信息
    *
    * @param user
    * @return Int
    */
  @Insert(Array("insert into t_user(username,password,email,create_date,active) values(#{username},#{password},#{email},#{createDate},#{active})"))
  @Options(useGeneratedKeys = true, keyProperty = "id")
  def saveUser(user: User): Int = user.getId

  @Select(Array("select id,username,email,avatar,sex,sign,password,status,active from t_user where email = #{email}"))
  def matchUser(email: String): User
}
