FROM sindriainc/openjdk:1.5.0-17

WORKDIR /var/www/app

ARG TAG_VERSION
ARG HOST_USER_UID
ARG TIMEZONE

LABEL \
	name="xp-policy" \
	image="sindriaproject/xp-policy" \
	tag="${TAG_VERSION}" \
	vendor="sindria"

ENV DEBIAN_FRONTEND="noninteractive"
ENV TZ=${TIMEZONE}
ENV SINDRIA_USER="sindria"
ENV SINDRIA_USER_HOME="/home/sindria"

# Install application
COPY ./target/xp-policy-0.0.1-SNAPSHOT.jar /var/www/app

# Setting Timezone and Fixing permission
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && \
    chmod -R 770 /var/www/app && \
    chown -R ${SINDRIA_USER}:${SINDRIA_USER} /var/www/app

CMD ["java", "-jar", "xp-policy-0.0.1-SNAPSHOT.jar"]
