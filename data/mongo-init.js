db.createUser({
  user: 'printsyUser',
  pwd: 'printsyPassword',
  roles: [
    {
      role: 'readWrite',
      db: 'printsy',
    },
  ],
});