# echo-httpd #

HTTP service based on akka-http for demo and testing purposes.
Usually echoes requests, i.e. responses contain the path of the request's URL.

There are the following special cases:

- Path suffix is `who`: Response contains the interface and port the service is bound to 
- Path suffix is `shutdown`: The service is shut down

## Contribution policy ##

Contributions via GitHub pull requests are gladly accepted from their original author. Along with any pull requests, please state that the contribution is your original work and that you license the work to the project under the project's open source license. Whether or not you state this explicitly, by submitting any copyrighted material via pull request, email, or other means you agree to license the material under the project's open source license and warrant that you have the legal authority to do so.

## License ##

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
