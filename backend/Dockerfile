FROM tomcat:8.5
RUN apt-get update && apt-get install -y \
  wget \
  curl \
  dnsutils
RUN pwd && ls -al
COPY target/cddb4.war /usr/local/tomcat/webapps/cddb.war