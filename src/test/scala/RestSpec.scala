import akka.http.scaladsl.model.{HttpEntity, HttpMethods, HttpRequest, MediaTypes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.ByteString
import com.spandigital.RestService
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.ExecutionContextExecutor

class RestSpec extends AnyWordSpec with Matchers with ScalatestRouteTest with RestService {

  override implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  "Orders API" should {
    "Posting to /create-order should add the item" in {
      val jsonRequest = ByteString(
        s"""[{"name":"hhgtg","id":42}]""".stripMargin)

      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/create-order",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )
      postRequest ~> route ~> check {
        status.isSuccess() shouldEqual true
      }
    }

    "Posting to /item/1 should have an item" in {
      val getRequest = HttpRequest(
        HttpMethods.GET,
        uri = "/item/1"
      )
      getRequest ~> route ~> check {
        status.isSuccess() shouldEqual true
      }
    }
  }

}
