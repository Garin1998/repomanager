# repomanager

Simple application to retrieve data about repositories' information about branches with last commit's SHA with provided GitHub login.

## How to run with Docker
- **Docker must be installed on yours device!**
- Clone this repository with link: `https://github.com/Garin1998/repomanager.git`
- Provide variables to `.env` file inside project or use already provided
- Open terminal with path to project folder and type: `docker-compose up --build`

  ### Additional Information
    - default active profile in this scenario is `prod`

## How to run with on local machine
- Download or clone repositroy with link: `https://github.com/Garin1998/repomanager.git`
- Open project with IDE
- Create new package with maven by using `mvn clean package -P dev`
- Open terminal inside `./target` folder with command `java -jar *.jar` where `*` is the name of created jar package by Maven

In both scenarios under endpoint `/swagger-ui/index.html` there is an OpenAPI 3.0 documentation