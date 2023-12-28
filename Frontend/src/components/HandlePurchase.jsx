import { useState, useEffect } from 'react';

const HandlePurchase = async () => {
    // GraphQL mutation to cancel the scheduled task
    const mutation = `
      mutation cancelScheduledTask($userId: ID!) {
        cancelScheduledTask(userId: $userId)
      }
    `;
  
    const userId = 1; // Replace with actual logic to get userId
    const variables = { userId };

    const sendCartItemsToTransactionGateway = () => {
        cartItems.forEach(item => {
          // Assuming you have a function to send individual cart items
          sendToTransactionGateway(item);
        });
      };      
  
    try {
      const response = await fetch('http://localhost:8085/graphql', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ query: mutation, variables }),
      });
      const data = await response.json();
      if (data.data.cancelScheduledTask) {
        // Successfully cancelled, proceed with sending to Transaction-Gateway
        sendCartItemsToTransactionGateway();
      }
    } catch (error) {
      console.error('Error cancelling scheduled task:', error);
    }
  };
  
export default HandlePurchase;