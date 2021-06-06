# :cloud: Cloud Getting Started

## :gear: Prerequisites

- Clone this repo
- Google Cloud Platform Account and Project

  You must have a Google Cloud Platform account and enable billing, if you only want to try it Google Cloud Platform offer $300 free for new account.

  - Create Bucket in Cloud Storage, more information in [here](https://cloud.google.com/storage/docs/creating-buckets).
  - Create Collection in Firestore, more information in [here](https://cloud.google.com/firestore/docs/concepts/structure-data).

- Local Environtment (if you want to modification or try in local)
  - GCP Service Account:
    - [Create Service Account](https://cloud.google.com/iam/docs/creating-managing-service-accounts), give roles by needs e.g `Cloud Datastore User, Storage Object Creator`
    - [Create and Download Generated Keys](https://cloud.google.com/iam/docs/creating-managing-service-account-keys) rename it to `keys.json`
    - Move `keys.json` to `Cloud Run` folder
  - Go:
    - [Install Go](https://golang.org/doc/install)
  - Python:
    - [Install Python](https://www.python.org/downloads/)
    - Crete and Using Virtual Environment **[RECOMMENDED]**
  - Create `.env` for Cloud Run
    - Copy `.env.example` and rename it to `.env`
    - Fill blank data in `.env` with your data

## :computer: Try in local

### Gin (Go)
  
**Root folder for gin is `cloud/Cloud Run`**

- Go
  - in terminal type:

    ```bash
    go mod download && go run main.go
    ```

- Docker
  Because `Dockerfile` create specific for deploy at production if you want to try in local you need edit `Dockerfile` a little bit, add this command:

  ```Dockerfile
  ...
  # Deploy
  FROM alpine:latest
  RUN apk update && apk add ca-certificates && rm -rf /var/cache/apk/*

  # Add this command
  COPY .env /app/.env
  COPY keys.json ./`
  ```

- Run this command to build images:
  
  ```bash
  docker build -t [YOUR_IMAGE_NAME] .
  ```

- Run this command to run docker container:

    ```bash
    docker run -p 8080:8080 [YOUR_IMAGE_NAME]
    ```

Change `[YOUR_IMAGE_NAME]` with your choosen name e.g `lorem`

### Flask (Python)

- Create `virtual environment`
  - Linux

    ```bash
    python3 -m venv [NAME_VENV]
    ```

    Change `[NAME_VENV]` to your choosen name

    For Windows, or MacOS maybe different
  - Install Depedencies

    ```bash
    pip install -r requirements.txt
    ```

  - Locate Model
    - if the Model not located at same root as flask app (`cloud/flask`), you can change it at `app.py`

      ```python
      model_path = 'YOUR_MODEL_PATH'
      ```

  - Run Flask App
    - Python
      - type at terminal:

        ```bash
        python3 app.py
        ```

    - Gunicorn
      - type at terminal:

        ```bash
        gunicorn -w 4 -b 0.0.0.0:8000 app:app
        ```

## :cloud: Deploy to Google Cloud Platform

### Cloud Run

Create Service in [here](https://console.cloud.google.com/run/create)

- Enter Name of Service e.g `lorem`
- Choose Region where you want to store the service e.g `asia-southeast2`
- Manage Continous Deployment, docs in [here](https://cloud.google.com/run/docs/continuous-deployment-with-cloud-build)
  - Build Type choose `Dockerfile` (without edit for local usage) and change `Source Location` to `Dockerfile` saved, in this case at `/cloud/Cloud Run/Dockerfile`
- Advance Settings
  - Container
    - Change `request timeout` from `300` to `30` seconds
    - For autoscaling it's depends on you, we use minimum `1` and maximum `10` number of instances, if you want to save costs you can lower e.g choose minimum `0` and maximum `3`
  - Variable & Secrets
    - Create Environment Variables like file `.env` except `API_KEY`
    - Add Variable Name `GIN_MODE` with Value `release`
    - Add `API Key` to `Secret Manager` in [here](https://console.cloud.google.com/security/secret-manager)
    - Reference Secret API Key already created as `Environtment Variable` with Name like in `.env` (API_KEY)
- Service triggered `Allow all trafic` and `Allow unauthenticated invocations`
- Create and deployed

For more information about: [deploy cloud run](https://cloud.google.com/run/docs/deploying), [secrets manager](https://cloud.google.com/secret-manager/docs/creating-and-accessing-secrets)

### Compute Engine

- Create VM and change Firewall settings to `allow http traffic`, more information [here](https://cloud.google.com/compute/docs/instances/create-start-instance)
- Flask App
  Same as [Local Flask App](#flask-python) but **Run Flask App use `Gunicorn`**
- Nginx
  - Install nginx

    ```bash
    sudo apt-get update && sudo apt-get upgrade -y && sudo apt-get install nginx -y
    ```

  - edit file `/etc/nginx/site-available/default` to something like this:

    ```conf
    ...
    server {
      listen 80;
      server_name example.org;
      access_log  /var/log/nginx/example.log;

      location / {
          proxy_pass http://127.0.0.1:8000;
          proxy_set_header Host $host;
          proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
      }
    }
    ...
    ```

  - Restart nginx service

    ```bash
    sudo systemctl restart nginx
    ```
