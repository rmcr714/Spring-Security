This project tells us how to prevent apps from csrf attacks and how to allow a domain on differnt host or port to call this
backend api basically cors.

Cors: In the cors example we are allowing the port with localhost 4200 to use this api eventhough the port are different
for both the apps.


CSRF: The spring app prevents any put or post opertaion by default. We had initally disable the csrf in projectSecurityConfig

But we need to enable it for production cases.


The basic gist of csrf is instead of expecting the csrf token in the cookie of the request that we get we expect it
in the request header as well.

Say a malicious user gets u to click on a netflix.com link from his website and u have a cookie attached to that domain
in the browser then the browser will attach that cookie to the request and this can cause issues


So to prevent that we add the csrf token in the header of the request as well and a malicious user cannot insert
this token in the header because he doesnt know it as its the browser which knows it to attach it not the malicious attacker


So when u malicious user gets u to click on netflix.com the browser will attach the cookie with csrf token but the token
wont be there in the request header and the request will get rejected.