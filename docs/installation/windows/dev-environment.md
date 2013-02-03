####Git
- Download and install [Git for Windows](http://msysgit.github.com), leave default options. Most of the time you'll be working in Git command line terminal.
- If you don't have an SSH key, you'll need to generate it. Start Git console and run: `ssh-keygen -t rsa -C "your_email@your_domain.org"`, you can type your password there but then you'll need to enter it each time you want to push your changes to central Git repo; alternatively you you'd want to leave passphrase empty.
- Now you need to have a GitHub account (register if you don't have it) and enter your public SSH key (from `Documents and Settings/.ssh/id_rsa.pub`) to `Account Settings -> SSH Public keys`
- To check it enter `ssh git@github.com` in your terminal. Press `yes` and if you see next text, then everything is okay:
```
PTY allocation request failed on channel 0
Hi %username%! You've successfully authenticated, but GitHub does not provide shell access.
Connection to github.com closed.
```

Basic commands while working with Git:
 - Get the project: `git clone git@github.com:jtalks-org/jcommune.git`
 - If you changed something, you need to add it to Git: `git add .`
 - When you want to commit your changes (still locally though): `git commit -m 'meaningful message'`
 - When you want to push it to the server: `git push`. Note, that you must have permissions for that, so either contact us if you want to become a part of the team or use Pull Requests on GitHub.

####Maven
 - Download [latest Maven](http://maven.apache.org/download.cgi)
 - Unpack it to some folder and change environment variables to configure `M2_HOME=folder_with_maven` and also change PATH variable to include `%M2_HOME%/bin`.
 - Run `mvn --version` to check whether your command line now sees Maven executables.