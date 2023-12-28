db.createUser({
  user: 'blockchainUser',
  pwd: 'blockchainPassword',
  roles: [
    {
      role: 'readWrite',
      db: 'blockchain',
    },
  ],
});