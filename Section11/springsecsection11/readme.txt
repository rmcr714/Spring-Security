This project is about using jwt tokens in spring

The filters JWTTokenGeneratorFilter is used to generate the token and out it in the header of the response which can be used
by the front end for sending subsequent requests

The filter JWTTokenValidatorFilter is used to validate the token passed and if there is any tampering in the jwt payload
then the signatures don't match as we use a secret for checking the signature which only this backend know

The jwt_secret environment variable comes from the run configuration environment  variable value. U can put any value
in run config environment variables for testing. In our case we have used JWT_KEY=jxgEQeXHuPq8VdbyYFNkANdudQ53YUn4

We have also created an /apiLogin class to simulate the situation in production apps where there is a dedicated api for
logging in. This would just give the caller the jwt and can be used for subsequent requests.

Btw this jwt that we are creating has a validity of 8 days but can be changed also the flow is same in ProjectSecurityConfig
first we check for cors -> csrf filters ->  Basic authentication (username,password validation)  -> once that succeds
we will return jwt token . F
For subsequent calls from the front end this jwt token will need to be present in the header.


JWT's are safer as once if someone gets the token they have limited lifestyle and will be rendered useless after that

