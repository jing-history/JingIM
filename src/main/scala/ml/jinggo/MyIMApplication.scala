package ml.jinggo

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import springfox.documentation.swagger2.annotations.EnableSwagger2

/**
  * Created by gz12 on 2018-07-03.
  * scala 使用教程 https://blog.csdn.net/universsky2015/article/details/77965527
  */
@SpringBootApplication
@EnableSwagger2
@EntityScan(Array("ml.jinggo.domain"))
class MyIMApplication {

}

object MyIMApplication{
  def main(args: Array[String]): Unit = {
    SpringApplication.run(classOf[MyIMApplication],args: _*)
  }
}
