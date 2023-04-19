# Maeum AI
Maeum je soubor slabých umělých inteligencí, jež mají simulovat určité aspekty silné umělé inteligence, tvořící komunikačního a emotivního robota.

## Jak spustit
Robota spustit lze pomocí iTermocil, či manuálně
1. **Spusťte robota Maeum Nestor**
    1. Připojte obě elektrické vidlice do zásuvek
    2. Zdroj připojte do barrel jacku
    3. Spuštění robota zjistíte točícím se ventilátorem
    4. Nyní robot startuje, vyčkejte přibližně půl minuty
2. **Spusťte software stack uvnitř robota**
    1. >ssh vebot@maxr-vebot (nebo IP)
    2. Zadejte heslo a dokončete připojení
    3. > /home/vebot/mjpg-streamer/mjpg-streamer-experimental/somewhere/run-mjpg-streamer.sh (pokud již neběží)
    4. >node /home/vebot/vebotruntime/index.js
    5. >cd vebotruntime
    6. > gunicorn -w 4 -b 127.0.0.1:5000 siphona:app
    7. Robot je plně načten
3. **Nyní nastartujte AI stack**
    1. Nejdříve nastartujte Maeum Matter
        1. > cd maeum_matter
        2. > npm start
    2. Dále visuální kortex
        1. > cd maeum_visual
        2. > source ./venv/bin/activate
        3. > python3 main.py
    3. Dále Synapse
        1. > cd maeum_synapse/vebot_controller
        2. > npm run serve
    4. Nyní Rasa
