#  Copyright 2021 Collate
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#  http://www.apache.org/licenses/LICENSE-2.0
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.

# Build stage
FROM alpine:3 AS build

COPY umetadata-dist/target/umetadata-*.tar.gz /

RUN mkdir -p /opt/umetadata && \
    tar zxvf umetadata-*.tar.gz -C /opt/umetadata --strip-components 1 && \
    rm umetadata-*.tar.gz

# Final stage
FROM alpine:3

EXPOSE 8585

RUN adduser -D umetadata && \
    apk update && \
    apk upgrade && \
    apk add --update --no-cache bash openjdk21-jre

COPY --chown=umetadata:umetadata --from=build /opt/umetadata /opt/umetadata
COPY --chmod=755 docker/umetadata-start.sh /

USER umetadata

WORKDIR /opt/umetadata
ENTRYPOINT [ "/bin/bash" ]
CMD ["/umetadata-start.sh"]
