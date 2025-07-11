FROM node:18-alpine AS builder

WORKDIR /app
COPY package.json ./
RUN npm install -g vite@4.5.0
RUN npm install
COPY . .
RUN npx vite build


FROM nginx:stable-alpine

RUN rm -rf /usr/share/nginx/html/*

COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
