# RAWP
Rest Api Web Provider for Spring Boot applications

## Features

- Full web support for your api
- Auto - Generated swagger endpoint based on your api

## How to use
- Add dependency :

  	<dependency>
            <groupId>grusoftware</groupId>
            <artifactId>rawp</artifactId>
            <version>0.0.1-SNAPSHOTS</version>
        </dependency>

- Inside of your main spring boot class add 

	@Import({RestApiWebProviderConfig.class, RestApiWebProviderSwaggerConfig.class})

- Inside of your application.properties file insert properties:
  - rest.api.service (this is the package where all of your exposed api services will be)
  - rest.api.model (this is the package where all of your exposed api models will be)

## Notes

- Currently RAWP does not support:
  - higher level of generic types. For example List<List<>> or List<Map<>>
  - recursive models
