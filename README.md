<a name="readme-top"></a>


[![Contributors][contributors-shield]][contributors-url]
[![Issues][issues-shield]][issues-url]

<br>

[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Grupinix_Xadrez&metric=coverage)](https://sonarcloud.io/summary/new_code?id=Grupinix_Xadrez)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=Grupinix_Xadrez&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=Grupinix_Xadrez)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=Grupinix_Xadrez&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=Grupinix_Xadrez)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=Grupinix_Xadrez&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=Grupinix_Xadrez)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=Grupinix_Xadrez&metric=bugs)](https://sonarcloud.io/summary/new_code?id=Grupinix_Xadrez)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=Grupinix_Xadrez&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=Grupinix_Xadrez)
[![SonarCloud](https://sonarcloud.io/images/project_badges/sonarcloud-white.svg)](https://sonarcloud.io/summary/new_code?id=Grupinix_Xadrez)

<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/Grupinix/Xadrez">
    <img src="frontend/public/themes/default/logo.webp" alt="Logo" width="80" height="80">
  </a>

<h3 align="center">Xadrez</h3>
  <p align="center">
    Chess game using Minimax AI, built for Software Engineering 2, a course from UFF Computer Science program.
    <br />
    <a href="https://github.com/Grupinix/Xadrez"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://app-xadrez.eterniaserver.com.br/">View Demo</a>
  </p>
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#building-the-project">Building the project</a></li>
      </ul>
    </li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#creators">Creators</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

[![Xadrez Screenshot][xadrez-screenshot]](https://app-xadrez.eterniaserver.com.br/)

<p align="right">(<a href="#readme-top">back to top</a>)</p>



### Built With

* [![Java 17][Java 17]][Java17-url]
* [![Spring Boot 3][Spring Boot 3]][SpringBoot3-url]
* [![NodeJS 18][NodeJS 18]][NodeJS18-url]
* [![Vue 3][Vue 3]][Vue3-url]
* [![Typescript 4][Typescript 4]][Typescript4-url]
* [![HTML5][HTML5]][HTML5-url]
* [![CSS3][CSS3]][CSS3-url]
* [![Postgresql 14][Postgresql 14]][Postgresql14-url]
* [![Docker][Docker]][Docker-url]

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- GETTING STARTED -->
## Getting Started

To get a local copy up and running follow these simple example steps.
This steps were done in <strong>Ubuntu 23.04</strong>.

<br />

### Prerequisites

In order to setup the project locally, you will need to install the following prerequisites:
* Java 17
  ```sh
  sudo apt update && sudo apt upgrade
  sudo apt install openjdk-17-jre
  sudo apt install openjdk-17-jdk
  java --version # just for checking if installation was successful
  ```


* NodeJS and npm
  ```sh
  curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash - &&\
  sudo apt-get install -y nodejs
  node -v && npm -v # just for checking if installation was successful
  ```


* Vue 3
  ```sh
  npm install vue
  ```

* Docker
  ```sh
  sudo apt-get update
  sudo apt-get install ca-certificates curl gnupg

  sudo install -m 0755 -d /etc/apt/keyrings
  curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
  sudo chmod a+r /etc/apt/keyrings/docker.gpg

  echo \
  "deb [arch="$(dpkg --print-architecture)" signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  "$(. /etc/os-release && echo "$VERSION_CODENAME")" stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

  sudo apt-get update
  sudo apt-get install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

  sudo docker run hello-world # just for checking if installation was successful
  docker --version && docker compose version # just for checking if installation was successful

  ```

<br />

### Building the project

In order to build and run the project, it's recommended that you use an IDE. We recommend to use <strong>JetBrains IntelliJ IDEA Ultimate Edition 2023.1.2</strong>. The following steps will help you to setup the project using IntelliJ.

1. Clone the project using 'Get from VCS' option as soon as you open IntelliJ.
   ```sh
   git@github.com:Grupinix/Xadrez.git
   ```

2. Click on 'Run' and then 'Edit Configurations...'

3. Add 'npm' configuration, with package.json located in *frontend/package.json* and select *dev* for Scripts.

4. <strong>IF IT'S THE FIRST TIME BUILDING THE PROJECT: run the following code on IntelliJ terminal inside frontend folder:</strong>
   ```sh
   npm install
   ```

5. Now, run Docker compose setup, using the following code on IntelliJ terminal
    ```sh
   docker compose -f docker-compose-dev.yml up
   ```

6. Run the backend part using the Spring configuration that should be already configurated when you cloned the repository. *In other words, run BackendApplication.java*.

7. And finally, run the frontend part that you configurated for npm. 

8. To access the application, just head to http://localhost:5173/.


<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- ROADMAP -->
## Roadmap

- [ ] Themes
- [ ] Rooms
- [ ] Multiplayer
- [ ] Score system
- [ ] Turn-based system

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- CONTACT -->
## Creators

- [@Fernando Moreira](https://github.com/nandinhomsf)
- [@Felipe Salles](https://github.com/felipemsalles)
- [@Henrique Porto](https://github.com/henriporto)
- [@Paulo Felipe](https://github.com/Feliope)
- [@Rodrigo Barroso](https://github.com/rbarrosodev)
- [@Yuri Nogueira](https://github.com/yurinogueira)

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/Grupinix/Xadrez?logo=GitHub&style=for-the-badge
[contributors-url]: https://github.com/Grupinix/Xadrez/graphs/contributors
[issues-shield]: https://img.shields.io/github/issues/Grupinix/Xadrez?logo=GitHub&style=for-the-badge
[issues-url]: https://github.com/Grupinix/Xadrez/issues

[xadrez-screenshot]: frontend/public/game-screenshot.png

[Java 17]: frontend/public/readme-badges/java-17.svg
[Java17-url]: https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html

[Spring Boot 3]: frontend/public/readme-badges/spring-boot-3.svg
[SpringBoot3-url]: https://spring.io/projects/spring-boot

[NodeJS 18]: frontend/public/readme-badges/nodejs-18.svg
[NodeJS18-url]: https://nodejs.org/en

[Vue 3]: frontend/public/readme-badges/vue-3.svg
[Vue3-url]: https://vuejs.org/

[Typescript 4]: frontend/public/readme-badges/typescript-4.svg
[Typescript4-url]: https://www.typescriptlang.org/

[HTML5]: frontend/public/readme-badges/html-5.svg
[HTML5-url]: https://github.com/Grupinix/Xadrez#

[CSS3]: frontend/public/readme-badges/css-3.svg
[CSS3-url]: https://github.com/Grupinix/Xadrez#

[Postgresql 14]: frontend/public/readme-badges/postgresql-14.svg
[Postgresql14-url]: https://www.postgresql.org/

[Docker]: frontend/public/readme-badges/docker.svg
[Docker-url]: https://www.docker.com/
