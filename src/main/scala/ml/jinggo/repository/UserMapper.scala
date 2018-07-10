package ml.jinggo.repository

import ml.jinggo.domain.{FriendList, GroupList}
import ml.jinggo.entity.{FriendGroup, User}
import org.apache.ibatis.annotations._
import java.util.List

/**
  * trait 当成java 的抽象类，但是trait 支持多重继承
  * Created by gz12 on 2018-07-09.
  */
trait UserMapper {

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
