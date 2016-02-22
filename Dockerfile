FROM java:8

RUN apt-get update -y

RUN apt-get install -y cron

ENV SCALA_VERSION 2.11.7

# Install Scala
RUN \
  cd /root && \
  curl -o scala-$SCALA_VERSION.tgz http://downloads.typesafe.com/scala/$SCALA_VERSION/scala-$SCALA_VERSION.tgz && \
  tar -xf scala-$SCALA_VERSION.tgz && \
  rm scala-$SCALA_VERSION.tgz && \
  echo >> /root/.bashrc && \
  echo 'export PATH=~/scala-$SCALA_VERSION/bin:$PATH' >> /root/.bashrc

ADD target/scala-2.11/google-stock-price-assembly-0.1-SNAPSHOT.jar /root/

CMD /root/scala-2.11.7/bin/scala /root/google-stock-price-assembly-0.1-SNAPSHOT.jar
