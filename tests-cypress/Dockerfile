FROM cypress/included:9.4.1


RUN npm i -g typescript ts-loader
#        $ tsc -v
#        4.6.4
ENV NODE_PATH=/usr/local/lib/node_modules \
    DISPLAY=:0

ENTRYPOINT ["cypress", "run"]