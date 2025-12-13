# Base image
FROM node:20-slim AS build

# Set working directory
WORKDIR /app

# Copy dependencies to container
COPY package*.json ./

# Install depencencies
RUN npm install

# Copy rest code
COPY . .

# Build
RUN npx ng build --configuration=production

# Web server
FROM nginx:stable-alpine

# Copy builded app to Nginx
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html

# Copy configuration of Nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose
EXPOSE 80