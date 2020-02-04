FROM tomcat:8.5
RUN apt-get update && apt-get install -y \
  wget \
  curl \
  dnsutils
COPY target/cddb4.war /usr/local/tomcat/webapps/cddb.war