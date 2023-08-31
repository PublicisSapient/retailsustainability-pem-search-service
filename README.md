## Search Service
The Search Service is a Spring Boot application that provides search functionalities. It utilizes Apache Solr for fetching products and integrates with Swagger for API documentation.

## Table of Contents
 - [Features](#features)
 - [Technologies Used](#technologies-used)
 - [Getting Started](#getting-started)
 - [Configuration](#configuration)
 - [API Documentation](#api-documentation)
 - [Error Messages](#error-messages)
 - [Contributing](#contributing)
 - [License](#license)

## Features
- Getting products based on the keyword search
- Fetching products by applying the filters and sort
- Getting suggestions for the search bar
- API documentation using Swagger
- Error messages for common scenarios

## Technologies Used
- Java 17
- Spring Boot 3.0.6
- Swagger (OpenAPI)
- Solr 8.x

## Getting Started
To get started with the Search Service, follow these steps:

- Clone the repository: git clone https://github.com/PublicisSapient/retailsustainability-pem-search-service.git
- Clone the repository: git clone https://github.com/PublicisSapient/retailsustainability-pem-common-framework.git
- Navigate to the common framework directory: cd common-framework
- Build the common-framework: mvn clean build
- Configure the environment variables to your environment (see Configuration)
- Build the project: mvn clean build
- Navigate to the target directory: cd target
- Run the application: java -jar search-service.jar

The service will start running on the configured port (default: 9005). You can access the APIs using the base URL http://localhost:9005/api/v1/search-service and Swagger UI: http://localhost:9005/api/v1/search-service/swagger-ui

## Configuration
The application can be configured using the following properties:

- **SVC_SOLR:** The Url of the Solr (eg: localhost:8983)
- **solrUsername:** The Username of the Solr
- **solrPassword:** The Password of the Solr

Modify the core name in the Solr Url in the application.yml file as per the core that you have created.

- **solrUrl:** http://${SVC_SOLR}/solr/**CORE_NAME**/

Ensure that you have Solr installed and running before starting the service.

## API Documentation
The search Service provides API documentation using Swagger(Open API). You can access the Swagger UI by navigating to http://localhost:9005/api/v1/search-service/swagger-ui in your web browser. This UI provides detailed information about the available API endpoints, request/response schemas

## Error Messages
The application defines a set of error messages for common scenarios. These messages are configurable and can be found in the application.yml file. You can customize the error messages according to your needs.

## Contributing
Contributions to the Search Service are welcome! If you find any issues or have suggestions for improvements, please open an issue or submit a pull request.

- Fork the repository.
- Create your feature branch: git checkout -b feature/my-new-feature.
- Commit your changes: git commit -m 'Add some feature'.
- Push to the branch: git push origin feature/my-new-feature.
- Submit a pull request.

## License
The Search Service is open-source and available under the MIT License.

Feel free to modify and adapt the code to suit your needs.