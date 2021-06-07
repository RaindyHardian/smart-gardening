# :cloud: Cloud Getting Started Lite

## :gear: Prerequisites

1. Clone this repo
2. Google Cloud Platform Account and Project
   - You must have a Google Cloud Platform account and enable billing
   - Create Bucket in Cloud Storage, more information in [here](https://cloud.google.com/storage/docs/creating-buckets).
   - Create Collection in Firestore, more information in [here](https://cloud.google.com/firestore/docs/concepts/structure-data).
3. Copy `.env.example` and rename it to `.env`
4. Fill blank data in `.env` with your data

## :cloud: Deploy to Google Cloud Platform

### Cloud Run (GIN REST API)

Create Service in [here](https://console.cloud.google.com/run/create)

1. Enter Name of Service
2. Choose Region
3. Manage Continous Deployment, docs in [here](https://cloud.google.com/run/docs/continuous-deployment-with-cloud-build)
   - Change `Source Location` to saved `Dockerfile` directory
4. Advance Settings
   - Container
     - Change `request timeout` to `30` seconds
     - Change autoscaling to minimum `1` and maximum `10`
   - Variable & Secrets
     - Create Environment Variables like file `.env` except `API_KEY`
     - Add Variable Name `GIN_MODE` with Value `release`
     - Add `API Key` to `Secret Manager` in [here](https://console.cloud.google.com/security/secret-manager)
     - Reference Secret API Key as `Environtment Variable` with Name `API_KEY`
5. Service triggered `Allow all trafic` and `Allow unauthenticated invocations`
6. Create and deployed

For more information about: [deploy cloud run](https://cloud.google.com/run/docs/deploying), [secrets manager](https://cloud.google.com/secret-manager/docs/creating-and-accessing-secrets)

### Compute Engine (Flask Prediction API)

1. Create VM and change Firewall settings to `allow http traffic`, more information [here](https://cloud.google.com/compute/docs/instances/create-start-instance)
2. Flask App
   - Create `virtual environment`
   - Install Depedencies
   - Locate Model and change path model in `app.py`
   - Run Flask App using `gunicorn`

3. Nginx
   - Install nginx
   - edit file `/etc/nginx/site-available/default` to proxy traffic Flask App
   - Restart nginx service
