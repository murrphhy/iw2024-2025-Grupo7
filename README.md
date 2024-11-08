# Flujo de trabajo

## Branches (ramas)
A la hora de implementar las nuevas funcionalidades, y con la intención de que no haya problemas ni sustos, cada uno se creará una rama con el nombre de la funcionalidad nueva que se quiere implementas. Esta rama estará creada a partir de una rama llamada dev (desarrollo) con el fin de volcar los nuevos cambios a esta rama. Una vez se verifiquen los nuevos cambios, esta se mergeará (hacer un `git merge`) con main, dejando main como una rama de producción.

![image](https://github.com/user-attachments/assets/84ab59dd-cac7-4442-863d-5ae4228120a8)

# Instrucciones instalación del entorno (Docker)
Para realizar la instalación de la infraestructura del proyecto, la cual la vamos a hacer mediante docker, para evitar problemas individuales. Los pasos a seguir para ello son los siguientes:

1. Iremos a nuestra carpeta de IW, o a la carpeta donde queremos tener el proyecto.
2. Clonar el repositorio con `git clone <git@github.com:<usuario>/iw2024-2025-Grupo7.git>`. OJO, hacerlo con ssh, así se evitará tener que meter las credenciales cada vez que se haga un `push`. Tutorial instalación SSH key:
   - Linux: https://www.youtube.com/watch?v=EoLrCX1VVog.
   - Windows: https://www.youtube.com/watch?v=X40b9x9BFGo
4. Entramos en la carpeta que nos ha aparecido: `cd iw2024-2025-Grupo7`.
5. Una vez en la carpeta del proyecto, construiremos el entorno mediante el comando `make build` (se recomienda observar el archivo `makefile` para entender que es lo que se hace con cada comando).
6. Por último, tras construir la imagen, la levantaremos con `make up`, esto nos levantará toda la infraestructura (la base de datos y el phpmyadmin para manejarla).

![image](https://github.com/user-attachments/assets/0d72faa9-7140-4d74-b3ff-f7a387673994)


# Comandos Básicos de git (descripciones informales)
Antes de empezar por los comandos, os dejo un tutorial de instalación y configuración inicial de git, por si no lo tenéis instalado: 
- Linux: https://www.youtube.com/watch?v=bc3_FL9zWWs
- Windows: https://www.youtube.com/watch?v=4xqVv2lTo40
  ## Comandos
- `git pull`: cargamos el contenido del repositorio que hay en github en nuestro repositorio, en esencia es actualizar nuestro repositorio local con los últimos cambios que se han hecho.
- `git add .`: añadimos los archivos modificados al repositorio local.
- `git commit -m "mensaje corto explicando los cambios hechos"`: se guardan los cambios hechos en nuestro repositorio local (como hacer un snapshot del nuestro repositorio local). 
- `git push`: subir los cambios que hemos hecho en nuestro repositorio local (nuestra máquina) al repositorio "global" (github).
- `git checkout <rama>`: cambiamos la rama en la que estamnos trabajando.
- `git branch -D <nombre-rama>`: borramos la rama con `nombre-rama`.
## Información adicional (cheat sheet)
https://education.github.com/git-cheat-sheet-education.pdf
