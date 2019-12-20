FROM nginx:stable
RUN apt-get update && apt-get install -y \
  wget \
  curl \
  dnsutils
COPY src /usr/share/nginx/html
COPY resources/nginx.conf /etc/nginx/