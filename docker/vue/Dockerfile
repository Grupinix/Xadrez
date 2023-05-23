FROM node:lts-alpine as xadrez-vue-builder
LABEL org.opencontainers.image.source="https://github.com/Grupinix/Xadrez"

WORKDIR /app

COPY ./frontend/package*.json ./

RUN npm install

COPY ./frontend/ .

RUN npm run build

# Run the web
FROM nginx:stable-alpine as xadrez-vue
LABEL maintainer="yurinogueira@id.uff.br"

LABEL vendor="Grupinix"

COPY --from=xadrez-vue-builder /app/dist /usr/share/nginx/html
COPY ./docker/nginx/prod.conf /temp/prod.conf

RUN envsubst /app < /temp/prod.conf > /etc/nginx/conf.d/default.conf

CMD ["nginx", "-g", "daemon off;"]
