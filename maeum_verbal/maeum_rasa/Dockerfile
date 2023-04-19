# syntax=docker/dockerfile:experimental
FROM --platform=$BUILDPLATFORM rasa/rasa:2.8.1-full

COPY ./maeum_verbal/maeum_rasa /app

RUN apt-get update && \
    apt-get install -y git && \
    pip install --no-cache-dir git+https://github.com/huggingface/transformers.git@e09d8786568280f6e0b6f9f6d187e0e210d75318#egg=transformers && \
    pip install --no-cache-dir -r /app/requirements.txt && \
    apt-get autoremove -y git && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

EXPOSE 5005/tcp
EXPOSE 5055/tcp

ENTRYPOINT [ "sh", "-c", "source /opt/venv/bin/activate && rasa run --enable-api" ]
