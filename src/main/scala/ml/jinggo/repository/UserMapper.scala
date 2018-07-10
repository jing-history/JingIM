package ml.jinggo.repository

import ml.jinggo.entity.User
import org.apache.ibatis.annotations.{Insert, Options, Select}
import org.springframework.stereotype.Repository

/**
  * trait 当成java 的抽象类，但是trait 支持多重继承
  * Created by gz12 on 2018-07-09.
  */
trait UserMapper {

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
