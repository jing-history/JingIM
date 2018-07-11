package ml.jinggo

import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.support.SpringBootServletInitializer
import org.springframework.context.ApplicationContext
import springfox.documentation.swagger2.annotations.EnableSwagger2

import scala.beans.BeanProperty

/**
  * Created by gz12 on 2018-07-03.
  * scala 使用教程 https://blog.csdn.net/universsky2015/article/details/77965527
  */
@SpringBootApplication
@EnableSwagger2
@EntityScan(Array("ml.jinggo.entity"))
@MapperScan(Array("ml.jinggo.repository"))
class Config

object MyIMApplication extends SpringBootServletInitializer {

  @BeanProperty var applicationContext: ApplicationContext = null

  def main(args: Array[String]) = applicationContext = SpringApplication.run(classOf[Config])

  override protected def configure(builder: SpringApplicationBuilder) = builder.sources(MyIMApplication)
}


/* 第二种启动spring boot Scala 的方法
class MyIMApplication {

}

object MyIMApplication{
  def main(args: Array[String]): Unit = {
    SpringApplication.run(classOf[MyIMApplication],args: _*)
  }
}*/
