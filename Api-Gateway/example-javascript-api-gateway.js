// ApiGateway.js

// Require the Express module
const express = require('express');
const cors = require('cors');
const jwt = require('jsonwebtoken');
const axios = require('axios');
const axiosRetry = require('axios-retry');
const rateLimit = require('express-rate-limit');
const fs = require('fs');
const https = require('https');
const { createProxyMiddleware } = require('http-proxy-middleware');
const cookieParser = require('cookie-parser')

// Create a new Express application
const app = express();

// Allow CORS for all routes
app.use(cors());

app.use((req, res, next) => {
  if (!req.secure) {
    return res.redirect(['https://', req.get('Host'), req.url].join(''));
  }
  next();
});

const privateKey = fs.readFileSync('/usr/src/app/certs/privkey.pem', 'utf8');
const certificate = fs.readFileSync('/usr/src/app/certs/fullchain.pem', 'utf8');

const credentials = {
  key: privateKey,
  cert: certificate
};

// Creates a rate limiter that allows 100 requests per 15 minutes.
const limiter = rateLimit({
  windowMs: 15 * 60 * 1000,
  max: 100
});

const server = https.createServer(credentials, app);

app.use(limiter);

// Retry failed requests 3 times
axiosRetry(axios, { retries: 3 }); 

// Middleware to extract JWT from cookie and place into Authorization header
app.use(cookieParser());
app.use((req, res, next) => {
  if (req.cookies?.jwt) {
    req.headers.authorization = `Bearer ${req.cookies.jwt}`;
  }
  next();
});

// Extract secret from environment variable
const secret = process.env.JWT_SECRET;

app.use((req, res, next) => {
  console.log(`Request: ${req.method} - doc${req.path}`);

  // Regex pattern to match paths starting with /users/preferences and followed by another segment or not
  const preferencesPathPattern = /^\/users\/preferences(\/.*)?$/;
  const communityPostsPathPattern = /^\/community\/posts$/;
  const publicUserProfilesPattern = /^\/users\/public-profiles(\/.*)?$/;

  // Check if it's a request that does not require token
  if (preferencesPathPattern.test(req.path) || (communityPostsPathPattern.test(req.path) && req.method === 'POST') || publicUserProfilesPattern.test(req.path)) {
    // No token required, go to the next middleware
    next();
  }
  // Check if it's a request to /users or a non-GET request to /community
  else if (req.path.startsWith('/users') || (req.path.startsWith('/community') && req.method !== 'GET')) {

    // Extract the token from the Authorization header
    const authHeader = req.headers.authorization;
    const token = authHeader?.split(' ')[1];

    if (token == null) {
      // No token in the request, respond with 401 Unauthorized
      console.log('No token in the request');
      return res.sendStatus(401);
    }

    jwt.verify(token, secret, (err, user) => {
      console.log('Verifying token...');
      if (err) {
        // Token is invalid, respond with 403 Forbidden
        console.log(`Token verification failed: ${err}`);
        return res.sendStatus(403);
      }

      // Token is valid, add user to request for use in other routes
      console.log('Token verified successfully');
      req.user = user;
      next();
    });
  } else {
    console.log('No token required');
    // If the request path does not require authentication, proceed to the next middleware
    next();
  }
});

// Define a route for the root path ("/")
app.get('/', (req, res) => {
  const { name = "user" } = req.query;
  res.send(`Hello, ${name}!`);
});

// Define a route for accessing the machine learning service
app.use('/busyness', createProxyMiddleware({
  target: 'http://busyness-service:80',
  changeOrigin: true,
  pathRewrite: { '^/busyness': '' },
  onProxyReq: function(proxyReq, req, res) {
    console.log(`New request: ${req.method} ${req.url}`);
    console.log(`Being proxied to: ${proxyReq.url}`);
  },
  onError: function(err, req, res) {
    console.log(`Request to ${req.url} failed`);
    console.error(err);
  },
  onProxyRes: function(proxyRes, req, res) {
    if (proxyRes.statusCode >= 400) {
      console.log(`Request to ${req.url} responded with ${proxyRes.statusCode}`);
    }
  }
}));

// Define a route for accessing the map service
app.use('/map', createProxyMiddleware({
  target: 'http://map-service:80',
  pathRewrite: { '^/map': '' },
  changeOrigin: true,
  onProxyReq: function(proxyReq, req, res) {
    console.log(`New request: ${req.method} ${req.url}`);
    console.log(`Being proxied to: ${proxyReq.url}`);
  },
  onError: function(err, req, res) {
    console.log(`Request to ${req.url} failed`);
    console.error(err);
  },
  onProxyRes: function(proxyRes, req, res) {
    if (proxyRes.statusCode >= 400) {
      console.log(`Request to ${req.url} responded with ${proxyRes.statusCode}`);
    }
  }
}));


// Define a route for accessing the user service
app.use('/auth', createProxyMiddleware({
  target: 'http://authentication-service:8080',
  changeOrigin: true,
  pathRewrite: { '^/auth': '' },
  onProxyReq: function(proxyReq, req, res) {
    console.log(`New request: ${req.method} ${req.url}`);
    console.log(`Being proxied to: ${proxyReq.url}`);
  },
  onError: function(err, req, res) {
    console.log(`Request to ${req.url} failed`);
    console.error(err);
  },
  onProxyRes: function(proxyRes, req, res) {
    if (proxyRes.statusCode >= 400) {
      console.log(`Request to ${req.url} responded with ${proxyRes.statusCode}`);
    }
  }
}));

// Define a route for accessing the user service
app.use('/user', createProxyMiddleware({
  target: 'http://user-service:8080',
  changeOrigin: true,
  pathRewrite: { '^/user': '' },
  onProxyReq: function(proxyReq, req, res) {
    console.log(`New request: ${req.method} ${req.url}`);
    console.log(`Being proxied to: ${proxyReq.url}`);
  },
  onError: function(err, req, res) {
    console.log(`Request to ${req.url} failed`);
    console.error(err);
  },
  onProxyRes: function(proxyRes, req, res) {
    if (proxyRes.statusCode >= 400) {
      console.log(`Request to ${req.url} responded with ${proxyRes.statusCode}`);
    }
  }
}));

// Define a route for accessing the user service
app.use('/community', createProxyMiddleware({
  target: 'http://community-service:8080',
  changeOrigin: true,
  pathRewrite: { '^/community': '' },
  onProxyReq: function(proxyReq, req, res) {
    console.log(`New request: ${req.method} ${req.url}`);
    console.log(`Being proxied to: ${proxyReq.url}`);
  },
  onError: function(err, req, res) {
    console.log(`Request to ${req.url} failed`);
    console.error(err);
  },
  onProxyRes: function(proxyRes, req, res) {
    if (proxyRes.statusCode >= 400) {
      console.log(`Request to ${req.url} responded with ${proxyRes.statusCode}`);
    }
  }
}));

// Define a route for accessing the user service
app.use('/places', createProxyMiddleware({
  target: 'http://places-service:80',
  changeOrigin: true,
  pathRewrite: { '^/places': '' },
  onProxyReq: function(proxyReq, req, res) {
    console.log(`New request: ${req.method} ${req.url}`);
    console.log(`Being proxied to: ${proxyReq.url}`);
  },
  onError: function(err, req, res) {
    console.log(`Request to ${req.url} failed`);
    console.error(err);
  },
  onProxyRes: function(proxyRes, req, res) {
    if (proxyRes.statusCode >= 400) {
      console.log(`Request to ${req.url} responded with ${proxyRes.statusCode}`);
    }
  }
}));

// Middleware for catching all errors!
app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(500).send('Something went wrong!');
});

// Start the server
server.listen(8080, () => {
  console.log('Server started on https://localhost:8080');
});