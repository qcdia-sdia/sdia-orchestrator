FROM openjdk:11
COPY ./target/orchestrator-release*.jar .
RUN mv orchestrator-release-*-info.jar  orchestrator-info.jar && mv orchestrator-release-*.jar  orchestrator.jar
CMD jar -xf orchestrator.jar BOOT-INF/classes/application.properties && \
    sed -ie "s#^message.broker.host=.*#message.broker.host=$RABBITMQ_HOST#" BOOT-INF/classes/application.properties && \ 
    sed -ie "s#^db.host=.*#db.host=$MONGO_HOST#" BOOT-INF/classes/application.properties && \ 
    sed -ie "s#^sure-tosca.base.path=.*#sure-tosca.base.path=$SURE_TOSCA_BASE_PATH#" BOOT-INF/classes/application.properties && \
    sed -ie "s#^credential.secret=.*#credential.secret=$CREDENTIAL_SECRET#" BOOT-INF/classes/application.properties && \
    cat BOOT-INF/classes/application.properties && \
    jar -uf orchestrator.jar BOOT-INF/classes/application.properties && \
    java -jar orchestrator.jar
