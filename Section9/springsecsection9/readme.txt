This project tells us how to provide authorization to users. The steps are

1. We have a customers table with customer_id , this table only contains the name, email and bcrypt hashed password. But for
authority we need to create a new table called Authority

2. So this authority will have a one to many relationship between customer and authority. Each user can have multiple roles

3. Then after creating this authority for a user, we would update the projectSecurityConfig, here we will set the role
for each api as ADMIN, USER etc

4. We have also updated the eazyBankUserDetailsService to get the authorityList in the userDetails object . Its just a basic db call
to the authority table

5. So the flow is, we pass the userName and passWord in the authorization section in postman. This would
go to the authenticate method in the EazyBankUserNamePwdAuthenticationProvider class, this class calls the
EazyBankUserDetailsService to get the data for the user from db. Then we compare the authorities that this user has
in the ProjectSecurityConfig class