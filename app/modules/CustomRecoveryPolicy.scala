package modules

/**
  * modules.CustomRecoveryPolicy.
  * Created at 10/24/2018 10:27 PM
  * by @author Timur Isachenko
  *
  * @isatimur | † be patient and test it! †
  *
  */

import javax.inject.Singleton
import play.api.cache.redis._

import scala.concurrent.Future

@Singleton
class CustomRecoveryPolicy extends RecoveryPolicy {

  def recoverFrom[T](rerun: => Future[T], default: => Future[T], failure: RedisException) = {
    // recover with default neutral value
    default
  }
}