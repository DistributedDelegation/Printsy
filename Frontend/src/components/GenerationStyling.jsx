import { useEffect, useState } from 'react';

const GenerationStyling = () => {
  const [features, setFeatures] = useState([]);
  let galleryGraphqlEndpoint = "http://localhost:8080/generation/graphql";

  const getAllFeatures = () => {
    const query = JSON.stringify({
      query: "{ fetchFeatures { name options } }",
    });

    fetch(galleryGraphqlEndpoint, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: query
    })
      .then(response => {
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
      })
      .then(data => {
        if (data && data.data && data.data.fetchFeatures) {
          setFeatures(data.data.fetchFeatures);
        } else {
          console.log('No features data found:', data);
        }
      })
      .catch(error => {
        console.error('Error fetching the features:', error);
      });
  }


  useEffect(() => {
    getAllFeatures();
  }, []);

  return (
    <div className="generationDropdowns">
      {features.map((feature, index) => (
        <div key={index} className="feature-container">
          <select className="generationSylting" id={feature.name} defaultValue="" onChange={(e) => onFeatureSelect(feature.name, e.target.value)}>
            <option value="" disabled>-</option>
            {feature.options.map((option, idx) => (
              <option key={idx} value={option}>{option}</option>
            ))}
          </select>
          <label htmlFor={feature.name} className="feature-label">{feature.name}</label>
        </div>
      ))}
    </div>
  )
}

export default GenerationStyling;
