FROM python:3.8-slim

ENV PYTHONUNBUFFERED True

WORKDIR /app

COPY . ./

RUN pip install -r requirements.txt

CMD exec gunicorn --bind :$PORT --workers 1 --threads 8 --timeout 0 app:app