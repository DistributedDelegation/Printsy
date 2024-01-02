import { useState, useEffect, useContext } from 'react';
import { AuthContext } from '../AuthContext';

const HandlePurchase = async () => {
    // GraphQL mutation to cancel the scheduled task
    const mutation = `
      mutation cancelScheduledTask($userId: ID!) {
        cancelScheduledTask(userId: $userId)
      }
    `;
  
    const authContext = useContext(AuthContext);
    const userId = authContext.userID;
    const variables = { userId };

    const sendCartItemsToTransactionGateway = () => {
        cartItems.forEach(item => {
          // Assuming you have a function to send individual cart items
          sendToTransactionGateway(item);
        });
      };      
  
    try {
      const response = await fetch('http://localhost:8080/authentication/graphql', {
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