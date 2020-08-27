FROM ubuntu:20.04

RUN apt-get update && apt-mark hold openjdk-11-jre-headless && env DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends python3 openjdk-8-jdk-headless ant libz3-java ruby git make zip unzip wget locales && apt-get clean && rm -rf /var/lib/apt/lists/*

ENV LANG C.UTF-8
ENV LC_ALL C.UTF-8

RUN useradd --create-home regel
USER regel

WORKDIR /home/regel

COPY --chown=regel:regel . .

WORKDIR /home/regel/sempre

# Using `yes' since zip may ask for permission to overwrite
RUN yes | ./pull-dependencies core && yes | ./pull-dependencies corenlp && yes | ./pull-dependencies freebase && yes | ./pull-dependencies tables && ant regex

WORKDIR /home/regel
