FROM openjdk:8-jdk-alpine
VOLUME /tmp
RUN addgroup -S omarc && adduser -S omarc -G omarc
USER omarc:omarc
ARG DEPENDENCY=build/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","com.dmv.footballheadz.Application"]