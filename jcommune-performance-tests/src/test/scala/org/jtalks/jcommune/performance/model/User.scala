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

package org.jtalks.jcommune.performance.model

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import org.jtalks.jcommune.performance.model.User.Role.Role
import org.jtalks.jcommune.performance.utils.ScnBuilder.urlPath
/**
  * @author Oleg Tkachenko
  */

object User {

  object Role extends Enumeration {
    type Role = Value
    val Anonymous, Registered = Value
  }

  private val header = Map("Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")

  val performLogin = scenario("Perform Login")
    .feed(csv("credentials.csv").circular)
      .exec(http("Perform Login (" + "${username}" + ")")
      .post("/login_ajax")
      .formParam("userName", "${username}")
      .formParam("password", "${password}")
      .formParam("_spring_security_remember_me", "on")
      .check(status.is(200)))

  def openForumMainPage(role: Role): ChainBuilder = exec(http("Open main forum page (" + role + ")")
    .get("/")
    .check(regex(urlPath + "/branches/([\\d]{1,4})").find(0).saveAs("branchId"))
    .headers(header)
    .check(status.is(200))
  )

  def openRecent(role: Role): ChainBuilder = exec(http("Open recent (" + role + ")")
    .get("/topics/recent")
    .headers(header)
    .check(regex(urlPath + "/topics/([\\d]{1,6})").find(0).saveAs("topicId"))
    .check(status.is(200))
  )

  def openBranch(role: Role): ChainBuilder = {
    doIf("${branchId.exists()}") {
      exec(http("Open branch (id: " + "${branchId}" + ") by (" + role + ")")
        .get("/branches/${branchId}")
        .headers(header)
        .check(status.is(200))
        .check(regex(urlPath + "/topics/([\\d]{1,6})").find(0).saveAs("topicId")))
    }
  }

  def openRandomTopic(role: Role): ChainBuilder = {
    doIf("${topicId.exists()}") {
      exec(http("Open topic (id: " + "${topicId}" + ") by (" + role + ")")
        .get("/topics/${topicId}")
        .headers(header)
        .check(status.in(200, 304)))
    }
  }
}
