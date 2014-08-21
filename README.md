# echo-httpd #

```
echo-httpd$ sbt stage
...
[success] Total time: 0 s, completed Aug 21, 2014 9:18:23 PM
echo-httpd$ target/universal/stage/bin/echo-httpd

(next terminal)

echo-httpd$ curl 192.168.178.20:8080/hello
/hello
echo-httpd$ curl 192.168.178.20:8080/shutdown
Shutting down ...
```

## Contribution policy ##

Contributions via GitHub pull requests are gladly accepted from their original author. Along with any pull requests, please state that the contribution is your original work and that you license the work to the project under the project's open source license. Whether or not you state this explicitly, by submitting any copyrighted material via pull request, email, or other means you agree to license the material under the project's open source license and warrant that you have the legal authority to do so.

## License ##

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
