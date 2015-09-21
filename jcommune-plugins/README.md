JCommune Plugins
---

Plugins are designed to extend the functionality of JCommune forum engine. They can be installed by putting plugin jar 
file into special folder `JCOMMUNE_PLUGIN_FOLDER` that configured in `$TOMCAT_HOME/conf/Catalina/localhost/jcommune.xml`
file. By default it points to user home dir. So to install a plugin:

- Download it [from Nexus](http://repo.jtalks.org/content/repositories/builds/org/jtalks/jcommune/) 
 (version should be the same as forum version) or generate its jar file: `mvn package`
- Copy it from `jcommune-plugins/plugin-name/target/plugin-name*.jar` into `JCOMMUNE_PLUGIN_FOLDER` (by default: `~/`)
- Log in into forum as admin (by default: admin/admin)
- Go into Administration -> Plugins and enabled it there.

## Dev Info

Now we provides some velocity macros for plugins:

**bbeditor macro**

Allows your plugin to use jcommune-style text input form with bbcode support and preview possibility

```velocity
#bbeditor($labelForAction $locale $back $postText $bodyParameterName $showSubmitButton)
    $labelForAction - text which will be displayed on submit button
    $locale - locale of current user for label and tooltips translation
      (now we suppurt English ("en"), Russian ("ru"), Ukrainian ("uk") and Spanish ("es"))
    $back - url to which user will be redirected when clicks "back" button after submiting form
    $postText - text which will be displayed in textarea
    $bodyParameterName - name parameter of text area
    $showSubmitButton - boolean flag which indicates is submit button will be shown
```