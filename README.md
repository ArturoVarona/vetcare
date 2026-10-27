# VetCare
Sistema de escritorio desarrollado en Java y JavaFX para la gestión integral de una clínica veterinaria, utilizando PostgreSQL en Supabase como base de datos y Supabase Auth para la autenticación.

## Requisitos
- JDK (versión indicada por el curso) y Maven
- Proyecto en Supabase con los scripts de la carpeta `/database` ejecutados (`01_schema.sql` y `02_seed.sql`)

## Instalación y Configuración
1. Clonar el repositorio.
2. Configurar las credenciales de conexión a la base de datos y las llaves de Supabase en tus archivos de configuración o variables de entorno.
3. Asegurarte de tener la estructura de directorios y dependencias correctas gestionadas mediante Maven.

## Ejecución
Puedes compilar y ejecutar la aplicación de escritorio ejecutando el siguiente comando en la terminal:
```bash
mvn clean javafx:run
