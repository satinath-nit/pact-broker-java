
# pact-broker-java
Example for using Pact Broker in Java using JUnit

![image](https://user-images.githubusercontent.com/32492604/128455297-703b335b-2b70-4bf8-bad5-fbd140551336.png)

•	Let’s use Amazon EC2 instance and install pact broker
1.	Login to AWS management console and go to EC2 under Compute services.
2.	Create a free tier instance(Amazon Linux) and install docker
  
$sudo yum update -y
$sudo amazon-linux-extras install docker
$docker –version

3.	Install docker compose
$sudo curl -L https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m) -o /usr/local/bin/docker-compose
$sudo chmod +x /usr/local/bin/docker-compose
$docker-compose version

4.	Create a docker-compose.yml file to build and run the docker images necessary to deploy pact-broker docker container in docker environment

docker-compose.yml
---------------------------------------------------------

version: "3"

services:
  postgres:
    image: postgres
    healthcheck:
      test: psql postgres --command "select 1" -U postgres
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: password
      POSTGRES_DB: postgres

  pact-broker:
    image: dius/pact-broker
    # build:
    #   context: .
    ports:
      - "80:80"
    depends_on:
      - postgres
    environment:
      PACT_BROKER_DATABASE_USERNAME: postgres
      PACT_BROKER_DATABASE_PASSWORD: password
      PACT_BROKER_DATABASE_HOST: postgres
      PACT_BROKER_DATABASE_NAME: postgres
      PACT_BROKER_LOG_LEVEL: INFO
    # If you remove nginx, enable the following
    # ports:
    #  - "80:80"

![image](https://user-images.githubusercontent.com/32492604/128455593-b8bf12bb-f5eb-479c-a7ff-a2d6c600a5f0.png)

-----------------------------------------------------------------------

5.	Run the docker compose 
![image](https://user-images.githubusercontent.com/32492604/128455449-7a68ee24-cd76-4ee0-a9b4-df679651e9b5.png)

6.	Check the pact url using AWS public DNS 
![image](https://user-images.githubusercontent.com/32492604/128455485-62dc07d4-3db9-4365-9eed-f45938bf104c.png)


![image](https://user-images.githubusercontent.com/32492604/128455492-d4758341-620a-4764-9340-188333d1053d.png)


7.	Pact Broker is live in AWS EC2 docker environment
