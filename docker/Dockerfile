FROM registry.atlas.b2w:5000/b2wdigital/tomcat7-openjdk7:7.0.59

# Deploy application
ADD *.war /opt/tomcat/7.0.59/webapps/ROOT.war

RUN echo America/Sao_Paulo > /etc/timezone && dpkg-reconfigure --frontend noninteractive tzdata
