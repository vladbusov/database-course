# регистр на docker hub с ubuntu
FROM ubuntu:16.04
MAINTAINER Vlad Busov

RUN apt-get -y update

# типо версию засовываем в $PGVER
ENV PGVER 9.5

#ставим постгрес на машинке нашей
RUN apt-get install -y postgresql-$PGVER

# меняем пользователя на postgres, чтобы зайти в postgresql
USER postgres

# запускаем psql и создаем пользователя docker с паролем docker
RUN /etc/init.d/postgresql start &&\
    psql --command "CREATE USER docker WITH SUPERUSER PASSWORD 'docker';" &&\
    createdb -O docker docker &&\
    /etc/init.d/postgresql stop

# даем доступ всем ко всем базам данных по всем IP4 адрессам с md5 хешированием
RUN echo "host all  all    0.0.0.0/0  md5" >> /etc/postgresql/$PGVER/main/pg_hba.conf

# чтобы сервер принимал подключения не только с локального интерфейса
RUN echo "listen_addresses='*'" >> /etc/postgresql/$PGVER/main/postgresql.conf

# Во многих случаях отключение synchronous_commit
# для некритичных транзакций может дать больший выигрыш в скорости
RUN echo "synchronous_commit = off" >> /etc/postgresql/$PGVER/main/postgresql.conf

# Порт для базы данных
EXPOSE 5432

# емкость для резервного копирования конфигураций, журналов и бд
VOLUME  ["/etc/postgresql", "/var/log/postgresql", "/var/lib/postgresql"]

# возвращаемся в root пользователя
USER root

# устанавливаем java
RUN apt-get install -y openjdk-8-jdk-headless

# устанавливаем maven
RUN apt-get install -y maven

# Копируем исходный код в Docker-контейнер
ENV WORK /opt/database-proj
ADD docker-spring-boot/ $WORK/docker-spring-boot/

# Заходим в дирректорию
WORKDIR $WORK/docker-spring-boot

# Запускаем maven для сборки jar
RUN mvn package

# Объявлем порт сервера
EXPOSE 5000

# стартуем с jar файла на 5000
CMD service postgresql start && java -Xmx300M -Xmx300M -jar $WORK/docker-spring-boot/target/database-docker.jar

# sudo docker build -f Dockerfile -t database-docker .
# sudo docker run -p 5000:5000 database-docker
# FROM openjdk:8
# ADD target/database-docker.jar database-docker.jar
# EXPOSE 8080
# ENTRYPOINT ["java","-jar","database-docker.jar"]
