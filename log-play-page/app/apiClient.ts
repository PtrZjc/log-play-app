const BASE_URL = "https://n74ocgxymi.execute-api.eu-central-1.amazonaws.com/Prod";

export function getGamesLog() {
    const requestOptions = {
        method: 'GET',
        headers: {
            // 'Content-Type': 'application/json',
            'X-Api-Key': 'sllk2!'
        }
    };

    // Log the request details
    console.log("Making a fetch request to:", BASE_URL + "/games/log");
    console.log("Request options:", JSON.stringify(requestOptions, null, 2));

    fetch(BASE_URL + "/games/log", requestOptions)
        .then(response => {
            // Log the raw response
            console.log("Raw response:", JSON.stringify(response, null, 2));

            if (!response.ok) throw new Error('Network response was not ok');
            return response.json();
        })
        .then(json => {
            // Log the JSON response
            console.log("JSON response:", json);
        })
        .catch(error => {
            // Log any errors
            console.log('Fetch error:', error);
        });
}