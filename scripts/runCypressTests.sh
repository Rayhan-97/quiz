#!/bin/zsh

cleanupDockerContainers()
{
  docker stop cypress frontend backend
  docker rm cypress frontend backend
  docker network remove cypress-network
}

cleanupDockerContainers > /dev/null 2>&1

(cd backend && docker-compose up -d)
(cd frontend-react && docker-compose up -d)

IP=$(ipconfig getifaddr en0)
/usr/X11/bin/xhost + $IP
DISPLAY=$IP:0

cd tests-cypress

headless=${1:-}
if [[ ${headless} == "--headless=true" ]]; then
  docker-compose -f docker-compose.yml -f cy-headless.yml up --exit-code-from cypress
else
  docker-compose up --exit-code-from cypress
fi

cleanupDockerContainers > /dev/null 2>&1