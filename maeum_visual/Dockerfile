FROM python:3.8


COPY ./maeum_visual /app
WORKDIR /app

RUN apt-get -y update
RUN apt-get install -y --fix-missing \
    build-essential \
    cmake \
    gfortran \
    git \
    wget \
    curl \
    ffmpeg \
    graphicsmagick \
    libgraphicsmagick1-dev \
    libatlas-base-dev \
    libavcodec-dev \
    libavformat-dev \
    libgtk2.0-dev \
    libjpeg-dev \
    liblapack-dev \
    libswscale-dev \
    pkg-config \
    python3-dev \
    python3-numpy \
    software-properties-common \
    zip \
    && apt-get clean && rm -rf /tmp/* /var/tmp/*

# Install dependencies
RUN pip3 install -U pip wheel cmake setuptools
RUN pip3 install -r requirements.txt
RUN pip3 install Flask
RUN pip3 install dlib
RUN pip3 install opencv-python
RUN pip3 install fer
RUN pip3 install moviepy
RUN pip3 install imutils
RUN pip3 install tensorflow
RUN pip3 install imageai
RUN pip3 install face_recognition

EXPOSE 5001

CMD ["python3", "main.py"]
