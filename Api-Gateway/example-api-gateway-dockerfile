# Use the official Node.js 16 image.
# https://hub.docker.com/_/node
FROM node:16.20.0-alpine3.18

# Create and change to the app directory.
WORKDIR /usr/src/app

# Copy application dependency manifests to the container image.
COPY package*.json ./

# Install production dependencies.
RUN npm install --only=production

# Copy local code to the container image.
COPY . .

# Copy the entrypoint script and make it executable
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

# Set the script as the entrypoint
ENTRYPOINT ["/entrypoint.sh"]

# Specify the command to run the application
CMD [ "node", "--inspect=0.0.0.0:9229", "ApiGateway.js" ]
