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

package org.jtalks.jcommune.performance.utils

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import org.jtalks.jcommune.performance.model.User.Role.{Registered, Role}
import org.jtalks.jcommune.performance.model.User._
/**
  * @author Oleg Tkachenko
  */

object ScnBuilder {

  val serverUrl: String = {
    val url: String = System.getProperty("performance.url")
    if (url == null) "http://performance.jtalks.org/jcommune" else url
  }

  val scnPerformLogin: ScenarioBuilder = scenario("Perform Login").exec(performLogin)

  def scnOpenForumMainPage(role: Role): ScenarioBuilder = {
    scenario(role + " User open forum main page")
      .doIf(role == Registered) {
        exec(performLogin)
      }
      .exec(openForumMainPage(role))
  }

  def scnOpenRecentActivityPage(role: Role): ScenarioBuilder = {
    scenario(role + " User open recent activity page")
      .doIf(role == Registered) {
        exec(performLogin)
      }
      .exec(openRecent(role))
  }

  def scnOpenBranchPage(role: Role): ScenarioBuilder = {
    scenario(role + " User open branch page")
      .doIf(role == Registered) {
        exec(performLogin)
      }
      .exec(openForumMainPage(role))
      .exec(openBranch(role))
  }

  def scnOpenTopicPage(role: Role): ScenarioBuilder = {
    scenario(role + " User open topic page")
      .doIf(role == Registered) {
        exec(performLogin)
      }
      .exec(openForumMainPage(role))
      .exec(openBranch(role))
      .exec(openRandomTopic(role))
  }

  val httpProtocol = http
    .baseURL(serverUrl)
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0")

}
