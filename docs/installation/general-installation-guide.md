Installation Guide
---
You have 4 options:
 - If you want just _to see the app_, take a look at one of our environments like [UAT](http://uat.jtalks.org/jcommune). You can register a user, create topics, code review, etc.
 - You can start a local virtual environment without pulluting your OS and without tedious configuration. See our
 [JTalks VM project](https://github.com/jtalks-org/jtalks-vm)
 - If you'd like to _run it locally_, install the project from binaries (war file), see below.
 - If you want to _contribute_, you may want to build it from sources.

So if you want to run the app locally (from binaries or sources), you need to install basic environment:
 - [Instruction for Debian/Ubuntu](linux/basic-environment.md)
 - [Instruction for Windows](windows/basic-environment.md)

####Installing from Binaries
 - Download latest version from our [repository](http://repo.jtalks.org/content/repositories/deployment-pipeline/deployment-pipeline/jcommune/), you're interested in *.war files inside of directories.
 - Rename it to `jcommune.war`, put it into your $TOMCAT_HOME$/webapps (don't start Tomcat yet).
 - Now you need to configure DB access and other stuff. It's done via configuration file, the sample can be found [here](jcommune.xml), its name should be the same as the war-file name and it should be placed into `$TOMCAT_HOME/conf/Catalina/localhost`. For more details, read comments inside of the file per se.
 - What's left is to start the Tomcat which is described in basic environment installation
 - If you don't want to install [Poulpe](https://github.com/jtalks-org/poulpe) to administrate the forum (create branches, give permissions to user groups), you can use our SQL files that fill DB for you so that you can test it. You'll need to apply [these scripts](https://github.com/jtalks-org/jcommune/blob/master/jcommune-model/src/main/resources/org/jtalks/jcommune/model/sample_sections.sql).

####Building from sources
 - You'll need to install a dev environment ([linux instructions](linux/dev-environment.md), [windows instructions](windows/dev-environment.md)). Alternatively to Git, you may want to download the sources [directly as a zip](https://github.com/jtalks-org/jcommune/archive/master.zip) if you don't have Git and you don't want to install it. This won't allow you to contribute to the project though.
 - Clone the project: `git@github.com:jtalks-org/jcommune.git`. Now you can work with that project from your IDE.
 - If you want to deploy it from command line.. Step into the folder and build it: `mvn clean package`
 - Repeat everything from _Installing from Binaries_ but instead of downloading war-file, just grab it from `jcommune/jcommune-view/jcommunew-web-view/target`