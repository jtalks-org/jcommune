# Commit message should contain

- All lines except first one should be restricted to 75 symbols, not more.
- First line is a description - general info about commit. It includes JIRA ID at the beginning. It should be 65 symbols max.
- Then an empty line goes
- Then a more detailed description goes - it describes what changes are done and (it's very important) why these
  changes were done and why they are done in this particular way.
- If the task is not finished, it's very useful to leave Next Steps.
- All of the above is important either for tools (like github and others) or for other developers in your team.

# Example

```
JC-1144 Added interface for plugins that to handle HTTP requests

All plugins that implement WebControllerPlugin interface can process
http requests:
- Plugin generates html code that will be inserted in the template
 ("plugin/plugin.jsp").
- Plugin should specify the subPath for requests it processes

All logic for searching proper plugin for the request is located in the
PluginController.

Next Step: create separate HandlerMapping for the plugin request handling.
```

A good commit message should eliminate communication with colleagues. If afterwards someone asks you anything about
your commit, then your commit message wasn't full enough (or the guy is just an asshole and didn't read the commit message)
