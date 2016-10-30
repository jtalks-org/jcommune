/**
 * Copyright (C) 2011  JTalks.org Team
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
/**
  * Copyright (C) 2011  JTalks.org Team
  * This library is free software; you can redistribute it and/or
  * modify it under the terms of the GNU Lesser General Public
  * License as published by the Free Software Foundation; either
  * version 2.1 of the License, or (at your option) any later version.
  * This library is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  * Lesser General Public License for more details.
  * You should have received a copy of the GNU Lesser General Public
  * License along with this library; if not, write to the Free Software
  * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
  */

package org.jtalks.jcommune.performance.tests

import io.gatling.core.Predef._
import org.jtalks.jcommune.performance.model.User.Role.{Anonymous, Registered}
import org.jtalks.jcommune.performance.utils.ScnBuilder._
import scala.concurrent.duration.FiniteDuration

class OpenRecentActivityPage extends Simulation {

  val TEST_DURATION: FiniteDuration = 60  //Simulation duration in seconds
  val NUM_OF_ANONYMOUS_USERS: Int = 1     // Number of Anonymous users generated per second
  val NUM_OF_REGISTERED_USERS: Int = 1    // Number of Registered users generated per second

  setUp(
    scnOpenRecentActivityPage(Anonymous)
      .inject(
        constantUsersPerSec(NUM_OF_ANONYMOUS_USERS)
          .during(TEST_DURATION)),

    scnOpenRecentActivityPage(Registered)
      .inject(
        constantUsersPerSec(NUM_OF_REGISTERED_USERS)
          .during(TEST_DURATION)
      ))
    .assertions(
      global.successfulRequests.percent.is(100),
      global.responseTime.percentile1.lessThan(1500))
    .protocols(httpProtocol)
}
