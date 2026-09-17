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
ARG RI_VERSION="1.12.14"
ENV RELEASE_URL="https://10.1.98.30:5080/umd/umetadata/UMetadata/releases/download/${RI_VERSION}-release/umetadata-${RI_VERSION}.tar.gz"

RUN mkdir -p /opt/umetadata && \
    wget ${RELEASE_URL} && \
    tar zxvf umetadata-*.tar.gz -C /opt/umetadata --strip-components 1 && \
    rm umetadata-*.tar.gz

# Final stage
FROM alpine:3
ARG RI_VERSION="1.12.14"
ARG BUILD_DATE
ARG COMMIT_ID
LABEL maintainer="UMetadata"
LABEL org.u-metadata.image.authors="support@wondersgroup.com"
LABEL org.u-metadata.vendor="UMetadata"
LABEL org.u-metadata.release-version="$RI_VERSION"
LABEL org.u-metadata.description="UMetadata is an open source platform for metadata management and discovery."
LABEL org.u-metadata.url="https://wondersgroup.com/"
LABEL org.u-metadata.vcs-url="https://10.1.98.30:5080/umd/umetadata/UMetadata"
LABEL org.u-metadata.build-date=$BUILD_DATE
LABEL org.u-metadata.commit-id=$COMMIT_ID

EXPOSE 8585 8586
RUN adduser -D umetadata && apk update && \
    apk upgrade && \
    apk add --update --no-cache bash openjdk21-jre

COPY --chown=umetadata:umetadata --from=build /opt/umetadata /opt/umetadata
COPY --chmod=755 docker/umetadata-start.sh ./
USER umetadata

WORKDIR /opt/umetadata
ENTRYPOINT [ "/bin/bash" ]
CMD ["/umetadata-start.sh"]
