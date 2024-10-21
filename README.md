# Flujo de trabajo

## Branches (ramas)
A la hora de implementar las nuevas funcionalidades, y con la intención de que no haya problemas ni sustos, cada uno se creará una rama con el nombre de la funcionalidad nueva que se quiere implementas. Esta rama estará creada a partir de una rama llamada dev (desarrollo) con el fin de volcar los nuevos cambios a esta rama. Una vez se verifiquen los nuevos cambios, esta se mergeará (hacer un `git merge`) con main, dejando main como una rama de producción.

![image](https://github.com/user-attachments/assets/84ab59dd-cac7-4442-863d-5ae4228120a8)

# Instrucciones instalación del entorno (Docker)
Para realizar la instalación de la infraestructura del proyecto, la cual la vamos a hacer mediante docker, para evitar problemas individuales. Los pasos a seguir para ello son los siguientes:
1. Iremos a nuestra carpeta de IW, o a la carpeta donde queremos tener el proyecto.
2. Clonar el repositorio con `git clone <git@github.com:<usuario>/iw2024-2025-Grupo7.git>` (ojo, hacerlo con ssh, así se evitará tener que meter las credenciales cada vez que se haga un `push`. Tutorial instalación SSH key: https://www.youtube.com/watch?v=EoLrCX1VVog).
3. Entramos en la carpeta que nos ha aparecido: `cd iw2024-2025-Grupo7`
4. Una vez en la carpeta del proyecto, construiremos el entorno mediante el comando `make build` (se recomienda observar el archivo `makefile` para entender que es lo que se hace con cada comando).
5. Por último, tras construir la imagen, la levantaremos con `make up`, esto nos levantará toda la infraestructura (la base de datos, el phpmyadmin para manejarla y un debian con lo necesario para usar maven y node).
