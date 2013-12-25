### Why do we need both html and plain text representations of mails?

Some clients do not support HTML or it can be switched off and therefore we need to send a plain text version. We don't
know whether client can understand HTML (SMTP does not support this) therefore both versions are always sent to the
client.

How is that possible? Both versions are put into the mail body with a special separator (called boundary) into a
so-called multipart message. In the end the mail body looks like this:

```
Content-type: multipart/mixed; boundary="random-boundary"
--random-boundary
Content-type: text/plain;

This a plain text representation of our mail!

--random-boundary
Content-type: text/html;

<h1>This is an HTML representation of the mail</h1>

--random-boundary--
```

The MIME-compliant client can understand both boundaries and Content-types and therefore knows how to render the
message.

Find more [in the spec!](http://www.w3.org/Protocols/rfc1341/7_2_Multipart.html)