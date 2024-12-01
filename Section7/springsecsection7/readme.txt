This project tells us how to extend the AuthenticationProvider interface and then do password
validation. We can also do some custom validation here like if age is < 18 etc

We are also using profiles here, here in the config class we have two classes that implement the
AuthenticationProvider interface, one is EazyBankUserDetails and one is EazyBankProdUserDetails

So in the normal one we dont do any difficult password checks as it will run in the default
dev env. But in the prod one we do some checks because it will run in the prod env

We can test if we set our profile in spring->build configuration as default we would see we will
go in the eazyBankUserDetails class and would get output even if we put something rubbish as password

But if we set the profile in spring -> build configuration to prod we would go in the eazyBankProd
class and would get an error, only way to pass is provide the right password


Same profile concept is used in the projectSecutiry(prod)config class, here we can use
the profiles to permit all apis in the non prod suffixed class. We havent dont it here but we can