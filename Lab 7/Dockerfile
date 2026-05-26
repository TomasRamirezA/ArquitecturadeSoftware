# Stage 1: Build
FROM node:20-alpine AS build

WORKDIR /app

# Install dependencies
COPY package*.json ./
RUN npm install

# Copy source and build
COPY ../../Downloads/Lab-7---Parte-4-BluePrints-API---React---UI-Socket/Lab-7---Parte-4-BluePrints-API---React---UI-Socket .
RUN npm run build

# Stage 2: Serve
FROM nginx:stable-alpine

# Copy custom nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy built files to nginx
COPY --from=build /app/dist /usr/share/nginx/html

# Expose port 80
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
