Java:
 - Download java last version from http://www.oracle.com/technetwork/java/javase/downloads/index.html
 - Unzip it into some folder
 - Use file with environment variables to (~/bashrc, /etc/environment, ...) to export `JAVA_HOME=unziped_folder`
 - Give appropriate permissions, if you don't understand or simply don't care, just run `sudo chmod -R 777 $JAVA_HOME`
 - Add it to your PATH: `PATH=$JAVA_HOME/bin:$PATH`

MySQL:
 - Install it: `sudo apt-get install mysql-server-5.5`
 - Log into MySQL terminal, it may look like `mysql -uroot -proot`
 - Create database: `create database jtalks character set utf8`

Tomcat:
 - Download it: `wget http://apache.softded.ru/tomcat/tomcat-7/v7.0.35/bin/apache-tomcat-7.0.35.zip`
 - Unzip: `unzip apache-tomcat-7.0.35.zip`
 - When you want to start it, use `$TOMCAT_HOME/bin/startup.sh`
 - When you want to shut it down, use `$TOMCAT_HOME/bin/shutdown.sh`
