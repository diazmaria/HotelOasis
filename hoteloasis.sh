#! /bin/bash

echo "Introduzca usuario MySQL:"
read user

echo -n "[MySQL] Introduzca la contraseña de $user de MySQL: "
read -s CLAVE

echo -n "Creando base de datos..."
if mysql -u root --password="$CLAVE" < hoteloasis.sql; then
  echo "*** Base de datos creada ***"
else
  echo "*** Se ha producido un error al crear la base de datos ***"
fi

echo "Realizando el despliegue de la aplicación... "

mvn tomcat:run

