package ml.jinggo.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Bean
import org.springframework.boot.web.servlet.ServletRegistrationBean
import com.alibaba.druid.support.http.StatViewServlet
import scala.collection.immutable.Map
import scala.collection.JavaConversions
import scala.collection.immutable.List
import org.springframework.boot.web.servlet.FilterRegistrationBean
import com.alibaba.druid.support.http.WebStatFilter


/**
  * Alibaba Druid数据源配置
  * Created by gz12 on 2018-07-06.
  */
@Configuration
class DruidConfig {

  /**
    * druid配置访问路径和用户名密码
    * @return
    */
  @Bean
  def statViewServlet(): ServletRegistrationBean = {
    var druid = new ServletRegistrationBean()
    druid.setServlet(new StatViewServlet())
    druid.setUrlMappings(JavaConversions.asJavaCollection(List("/druid/*")))
    var params = Map("loginUsername" -> "admin", "loginPassword" -> "admin", "allo" -> "", "resetEnable" -> "false")
    druid.setInitParameters(JavaConversions.mapAsJavaMap(params))
    druid
  }

  /**
    * 拦截器配置
    * @return
    */
  @Bean
  def webStatFilter():FilterRegistrationBean = {
    var filter = new FilterRegistrationBean()
    filter.setFilter(new WebStatFilter())
    filter.setUrlPatterns(JavaConversions.asJavaCollection(List("/*")))
    filter.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*")
    filter
  }
}
