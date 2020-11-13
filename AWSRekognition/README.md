#Tarea Rekognition AWS

## Requisitos

Python 3 y las siguientes librerías:

`logging
pprint
boto3
requests
PIL
io
string`

## Instalación
Modificar script y agregar las keys y tokens necesarios en el objeto `AWS_S3_CREDS`. Luego, correr el script. El programa retornará true si todos los textos de la secuencia de control se encuentran en la segunda secuencia, falso de lo contrario. El programa además no realizará ninguna comparativa si el porcentaje de confidencia es menor al 97% respecto a la imagen de control o la de prueba.

## Licencia
[MIT](https://choosealicense.com/licenses/mit/)

