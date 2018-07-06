package ml.jinggo.config

import java.util.Properties

import com.github.pagehelper.PageHelper
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.context.annotation.{Bean, Configuration}

/**
  * Mybatis的分页插件
  * Created by gz12 on 2018-07-06.
  */
@Configuration
class MybatisConfig {

  private final val LOGGER: Logger = LoggerFactory.getLogger(classOf[MybatisConfig])

  @Bean
  def pageHelper(): PageHelper = {
    LOGGER.info("注册MyBatis分页插件PageHelper");
    val pageHelper = new PageHelper()
    val properties = new Properties()
    properties.setProperty("offsetAsPageNum", "true")
    properties.setProperty("rowBoundsWithCount", "true")
    properties.setProperty("reasonable", "true")
    pageHelper.setProperties(properties)
    pageHelper
  }
}
