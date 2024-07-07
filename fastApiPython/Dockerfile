FROM python:3.11-slim

# Establece el directorio de trabajo para la aplicación
WORKDIR /app

# Copia el archivo de requerimientos si existe, para instalar las dependencias (opcional)
# COPY requirements.txt .

# Instala FastAPI y Uvicorn directamente
RUN pip install --no-cache-dir fastapi uvicorn

# Copia el archivo main.py al contenedor
COPY main.py .

# Expone el puerto que usa la aplicación FastAPI
EXPOSE 8000

# Comando para ejecutar la aplicación con Uvicorn
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]
