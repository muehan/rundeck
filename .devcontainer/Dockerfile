FROM mcr.microsoft.com/devcontainers/base:bullseye

RUN apt-get update && \
    curl -sL https://deb.nodesource.com/setup_16.x | sudo bash - && \
    apt-get install -y nodejs

RUN apt-get install -y ca-certificates-java java-common libcups2 liblcms2-2 libjpeg62-turbo libfontconfig1 libnss3 libfreetype6 libpcsclite1 libx11-6 libxext6 libxi6 libxrender1 libxtst6

RUN wget -O ~/openjre8.deb http://snapshot.debian.org/archive/debian-security/20220210T090326Z/pool/updates/main/o/openjdk-8/openjdk-8-jre-headless_8u322-b06-1~deb9u1_amd64.deb
RUN wget -O ~/openjdk8.deb http://snapshot.debian.org/archive/debian-security/20220210T090326Z/pool/updates/main/o/openjdk-8/openjdk-8-dbg_8u322-b06-1~deb9u1_amd64.deb

RUN sudo dpkg -i ~/openjre8.deb
RUN sudo dpkg -i ~/openjdk8.deb
# RUN sudo apt --fix-broken install

# RUN apt-get install -y openjdk-8-jdk