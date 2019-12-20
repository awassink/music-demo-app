# Music demo application project

- Offers REST service on /cddb/rest
- Offers HTML/AngularJS client

This demo project consists of a music metadata database server and a client for interacting with it. 
The server is written in Java and uses the Spring (4.2.4) and Hibernate (5.0.7) frameworks to manage an underlying MySQL database and expose a REST service. 
The client is written separately in HTML5, CSS3 and AngularJS (1.4.9). 
The two communicate through HTTP verbs and pass JSON objects to each other. 
The project runs on an Apache Tomcat server (8.0) and NGINX. 
The database structure is maintained by Hibernate.

## Building the backend
The backend can be build using the Maven pom.xml file.
This requires maven to be installed locally.
```bash
cd backend
maven clean install
``` 
This creates the cddb4.war file in the target directory, which is the deployable unit.

### Creating the Docker image
A `Dockerfile` is provided to create a Docker image of the backend application.
```bash
cd backend
docker build .
```
You can use `docker tag <image> <name:tag>` command to name and tag the created image so you can refer to it locally.

## Packaging the frontend
The frontend application has no buildscript as it needs no processing of the sources and resources.
You can `zip` or `tar` the `src` directory to package the application for deployment.
The `resources/nginx.conf` file provides an NGINX configuration for hosting the frontend application and a reverse proxy to the backend API.
This way CORS issues are circumvented as both the frontend and backend API are hosted on the same hostname.

### Creating the Docker image
A `Dockerfile` is provided to create a Docker image of the frontend application.
```bash
cd backend
docker build .
```
You can use `docker tag <image> <name:tag>` command to name and tag the created image so you can refer to it locally.
 
## Running the whole application
A `docker-compose.yml` file is provided to run the whole three tier application at once.
```bash
docker-compose up
```
It comprises of a MySQL container, the backend container and a frontend container.
The MySQL container uses a volume mount so that database data is preserved even when the MySQL container is removed.
The application accessible on the localsystem at port 888.
http://localhost:888/

The application can be stopped by entering `control+C` in the active docker-compose shell, or by:
```bash
docker-compose down
``` 

### Running your own build images
If you've build you own docker images for the frontend and/or backend you can adapt the `image` properties accordingly in the `docker-compose.yml`.
