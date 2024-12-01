This project tells us how to use the custom filters in spring and how we can configure them before or after a given filter

Here for example we have configured a filter call requestValidationBeforeFilter in ProjectSecurityConfig class.
This filter would check the presence of test in the email and reject it if its present. This would run before authentication


Then we have two more filters named authorititesLoggingAfter and before and they would log some info from the security
context after and before the basic authentication


The flow is like this when we get a request with basic username and password the filters run like this .This can change sometime

Cors check -> Csrf check -> RequestValidationBeforeFiler -> Basic authentication i.e. calling the authenticate method of
EazyBankUserDetailsService class to authenticate the username and password  -> The controller classes to return the data
required