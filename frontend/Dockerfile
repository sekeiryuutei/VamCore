# ============================================================================
# frontend/Dockerfile
# Build multi-stage: compila con Angular CLI y sirve estático con nginx.
# ============================================================================
FROM node:20-alpine AS build
WORKDIR /build
COPY package.json ./
RUN npm install
COPY . .
RUN npm run build:prod

FROM nginx:1.27-alpine
COPY --from=build /build/dist/vamcore-frontend/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
