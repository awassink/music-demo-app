# Running the music demo application on AWS

## Running the MySQL database server in EC2
Create an EC2 **t2.micro** instance using the **MySQL 5.7** AMI of Jetware in the AWS marketplace.
Connect to the MySQL instance with ssh using your key file. Details can be found here: https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/AccessingInstancesLinux.html

The image will start MySQL out-of-the-box so next step is to create the database and schema user.
```bash
[ec2-user@ip-172-12-34-123 ~]$ mysql
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 34
Server version: 5.7.18 MySQL Community Server (GPL)

Copyright (c) 2000, 2017, Oracle and/or its affiliates. All rights reserved.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql>
```
```sql
mysql> create database cddb_quintor;
Query OK, 1 row affected (0.00 sec)

mysql> CREATE USER 'cddb_quintor'@'%' IDENTIFIED BY 'quintor_pw';
Query OK, 0 rows affected (0.02 sec)

mysql> GRANT ALL PRIVILEGES ON cddb_quintor.* TO 'cddb_quintor'@'%';
Query OK, 0 rows affected (0.01 sec)
```
### Create security group for MySQL access 
Create a Security Group named `Music-demo-app-mysql-access` that other EC2 instanced can be assigned tot to allow MySQL access to this instance.
Edit the `MySQL 5-7-170503-mysqld_5_7_18-AutogenByAWSMP-` security group associated to this instance and add an inbound rule of type `MySql/Aurora` (port 3306) and enter the previous created `Music-demo-app-mysql-access` security group as source.

## Running the Java/Tomcat backend application in EC2
Create an EC2 **t2.micro** instance using the **Tbitnami-tomcatstack-8.0.30-0-linux-ubuntu-14.04.3-x86_64-hvm-ebs (ami-01aeb36d)** AMI of Bitnami in the AWS marketplace. https://aws.amazon.com/marketplace/pp/B00NN8YIHM?ref=cns_srchrow
In the launch wizard remove the firewall rules for ports 80 and 443 access.

### Setup access to MySQL
Connect to the Tomcat instance with ssh using your key file and `bitnami` as the user.
Append the hosts file so the cddb-mysql hostname resolves to the private ip-address of the MySQL instance.
```bash
bitnami@ip-172-12-34-321:~$ sudo su -
root@ip-172-12-34-321:~# echo "172.12.34.123 cddb-mysql" >> /etc/hosts
root@ip-172-12-34-321:~# exit
logout
```
Change the security group of this instance to add the `Music-demo-app-mysql-access` security group.
This will allow this Java/Tomcat instance to connect to the MySQL instance.
```bash
bitnami@ip-172-12-34-321:~$ telnet cddb-mysql 3306
Trying 172.12.34.123...
Connected to cddb-mysql.
Escape character is '^]'.
J
5.7.18??VT
738?!OM'`2}bn,^ysql_native_password^CConnection closed by foreign host.
```

### Enable HTTP access on port 8080 on Tomcat
Uncomment the HTTP connector on port 8080 in the server.xml configuration file of Tomcat.
```xml
<Connector port="8080" URIEncoding="UTF-8" protocol="HTTP/1.1" connectionTimeout="20000" redirectPort="8443"/>
```
```bash
bitnami@ip-172-12-34-321:~$ sudo su tomcat -
bash: cannot set terminal process group (-1): Inappropriate ioctl for device
bash: no job control in this shell
tomcat@172-12-34-321:~s vi /opt/bitnami/apache-tomcat/conf/server.xml
tomcat@172-12-34-321:~s exit
bitnami@ip-172-12-34-321:~$ sudo /opt/bitnami/ctlscript.sh restart tomcat
Using CATALINA_BASE:   /opt/bitnami/apache-tomcat
Using CATALINA_HOME:   /opt/bitnami/apache-tomcat
Using CATALINA_TMPDIR: /opt/bitnami/apache-tomcat/temp
Using JRE_HOME:        /opt/bitnami/java
Using CLASSPATH:       /opt/bitnami/apache-tomcat/bin/bootstrap.jar:/opt/bitnami/apache-tomcat/bin/tomcat-juli.jar
Using CATALINA_PID:    /opt/bitnami/apache-tomcat/temp/catalina.pid
NOTE: Picked up JDK_JAVA_OPTIONS:   --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.io=ALL-UNNAMED --add-opens=java.rmi/sun.rmi.transport=ALL-UNNAMED
Tomcat stopped.
/opt/bitnami/apache-tomcat/scripts/ctl.sh : tomcat stopped
Using CATALINA_BASE:   /opt/bitnami/apache-tomcat
Using CATALINA_HOME:   /opt/bitnami/apache-tomcat
Using CATALINA_TMPDIR: /opt/bitnami/apache-tomcat/temp
Using JRE_HOME:        /opt/bitnami/java
Using CLASSPATH:       /opt/bitnami/apache-tomcat/bin/bootstrap.jar:/opt/bitnami/apache-tomcat/bin/tomcat-juli.jar
Using CATALINA_PID:    /opt/bitnami/apache-tomcat/temp/catalina.pid
Tomcat started.
/opt/bitnami/apache-tomcat/scripts/ctl.sh : tomcat started
```
### Deploy the Java backend application
Secure remote copy the cddb4.war file in the target directory to the Java/Tomcat instance.
```bash
$ scp -i ~/.ssh/<your-pem-file> target/cddb4.war bitnami@<your-instance-public-address>:.
```
```bash
bitnami@ip-172-31-22-105:~$ chmod g+w cddb4.war 
bitnami@ip-172-31-22-105:~$ sudo chown :tomcat cddb4.war 
bitnami@ip-172-31-22-105:~$ sudo mv cddb4.war /opt/bitnami/apache-tomcat/webapps/cddb.war
```
### Create security group for Tomcat access 
Create a Security Group named `Music-demo-app-tomcat-access` that other EC2 instanced can be assigned tot to allow Tomcat access to this instance.
Edit the `Launch-wizard` security group associated to this instance and add an inbound rule of type `Custom TCP Rule` and port range `8080` and enter the previous created `Music-demo-app-tomcat-access` security group as source.

## Running the Angular Frontend and reverse proxy
Create an EC2 **t2.micro** instance using the **NGINX Open Source Certified** AMI of Bitnami in the AWS marketplace. https://aws.amazon.com/marketplace/pp/B00NPHKI3Y?ref=cns_srchrow
Connect to the MySQL instance with ssh using your key file.

### Setup access to the backend
Connect to the NGINX instance with ssh using your key file and `bitnami` as the user.
Append the hosts file so the cddb-backend hostname resolves to the private ip-address of the Java/Tomcat instance.
```bash
bitnami@ip-172-12-34-222:~$ sudo su -
root@ip-172-12-34-222:~# echo "172.12.34.321 cddb-backend" >> /etc/hosts
root@ip-172-12-34-222:~# exit
logout
```
Change the security group of this instance to add the `Music-demo-app-tomcat-access` security group.
This will allow this NGINX instance to connect to the Java/Tomcat instance.
```bash
bitnami@ip-172-12-34-222:~$ curl http://cddb-backend:8080/cddb/rest/
[]
```

### Deploy the reverse proxy
Secure remote copy the `nginx-bitnami.conf` file in the resources directory to the NGINX instance.
Copy the file into the `conf` directory of nginx and configure it as the primairy configuration.
```bash
$ scp -i ~/.ssh/<your-pem-file> resources/nginx-bitnami.conf bitnami@<your-instance-public-address>:.
```
```bash
bitnami@ip-172-12-34-222:~$ sudo cp nginx-bitnami.conf /opt/bitnami/nginx/conf/
bitnami@ip-172-12-34-222:~$ sudo sed -i 's:bitnami/bitnami.conf:nginx-bitnami.conf:g' /opt/bitnami/nginx/conf/nginx.conf
bitnami@ip-172-12-34-222:~$ sudo /opt/bitnami/ctlscript.sh restart nginx
Unmonitored nginx
/opt/bitnami/nginx/scripts/ctl.sh : Nginx not running
/opt/bitnami/nginx/scripts/ctl.sh : Nginx started
Monitored nginx
```
The backend API is now available from a browser at http://<your-instance-public-address>/cddb/rest/

### Deploy the AngularJS frontend application
Package the `src` directory of the frontend into a gzipped tar file.
Secure remote copy the gzipped tar file to the NGINX instance.
Extract the gzipped tar file on the instance and move it into the `cddb-frontend` directory of nginx.
```bash
$ tar czf cddb-frontend.tar.gz src/
$ scp -i ~/.ssh/<your-pem-file> cddb-frontend.tar.gz bitnami@<your-instance-public-address>:.
```
```bash
bitnami@ip-172-12-34-222:~$ tar xzf cddb-frontend.tar.gz
bitnami@ip-172-12-34-222:~$ sudo mv src /opt/bitnami/nginx/cddb-frontend
```
The frontend application is now available from a browser at http://<your-instance-public-address>/