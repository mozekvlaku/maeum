<template>
  <div class="hello">
    <div class="columns">
      <div class="column is-narrow">
        <div class="box is-paddingless overflow-h">
          <div class="imageview" @click="rea">
            <img :src="camerastream" v-if="resetcomplete" class="flipped" />
          </div>
        </div>
      </div>

      <div class="column">
        <div class="columns" style="height:98px">
          <div class="column is-narrow">

            <p v-if="matterConnected">
              <img :src="require('../assets/emotion_'+ matterState.ep.em_state.toLowerCase() +'.png')"
                style="height:100px" />

            </p>



          </div>
          <div class="column is-position-relative">
            <p class="statemessage">
              <span v-if="matterState.ep.em_state == 'Neutral'">Jsem v pohodě</span>
              <span v-if="matterState.ep.em_state == 'Happy'">Mám radost</span>
              <span v-if="matterState.ep.em_state == 'Sad'">Jsem smutný</span>
              <span v-if="matterState.ep.em_state == 'Angry'">Jsem naštvaný</span>
              <span v-if="matterState.ep.em_state == 'Curious'">To mě zajímá</span>
              <span v-if="matterState.ep.em_state == 'Disgusted'">Fuj, jsem znechucen</span>
              <span v-if="matterState.ep.em_state == 'Fearful'">Bojím se</span>
              <span v-if="matterState.ep.em_state == 'Suspicious'">Podezřívám tě</span>
              <span v-if="matterState.ep.em_state == 'Surprised'">Wow, jsem překvapen</span>
              <span v-if="matterState.ep.em_state == 'Sleepy'">Jsem ospalý</span>

            </p>
          </div>
          <div class="column is-narrow" v-if="matterConnected">

            <div class="bo">
              <div class="">
                <div class="columns">
                  <div class="column has-text-centered">
                    <b-badge :type="matterState.vp.visual_online ? 'is-primary' : 'is-danger'" class="mb-2">
                      <b-icon icon="eye"></b-icon>
                    </b-badge>
                    <p class="mb-1">Visual</p>
                    <small class="isis" :class="matterState.vp.visual_online ? 'has-text-primary' : 'has-text-danger'">
                      {{ matterState.vp.visual_online ? 'online' : 'offline' }}
                    </small>
                  </div>
                  <div class="column has-text-centered">
                    <b-badge :type="matterState.vr.rasa_online ? 'is-primary' : 'is-danger'" class="mb-2">
                      <b-icon icon="message"></b-icon>
                    </b-badge>
                    <p class="mb-1">Verbal</p>
                    <small class="isis" :class="matterState.vr.rasa_online ? 'has-text-primary' : 'has-text-danger'">
                      {{ matterState.vr.rasa_online ? 'online' : 'offline' }}
                    </small>
                  </div>
                  <div class="column has-text-centered">
                    <b-badge :type="matterState.mm.nestor_online ? 'is-primary' : 'is-danger'" class="mb-2">
                      <b-icon icon="emoticon"></b-icon>
                    </b-badge>
                    <p class="mb-1">Nestor</p>
                    <small class="isis" :class="matterState.mm.nestor_online ? 'has-text-primary' : 'has-text-danger'">
                      {{ matterState.mm.nestor_online ? 'online' : 'offline' }}
                    </small>
                  </div>
                  <div class="column has-text-centered">
                    <b-badge type="is-primary" class="mb-2">
                      <b-icon icon="book"></b-icon>
                    </b-badge>
                    <p class="mb-1">Matter</p>
                    <small class="isis has-text-primary">online</small>
                  </div>
                  <div class="column has-text-centered">
                    <b-badge :type="matterState.vr.tts_online ? 'is-primary' : 'is-danger'" class="mb-2">
                      <b-icon icon="speaker"></b-icon>
                    </b-badge>
                    <p class="mb-1">TTS</p>
                    <small class="isis" :class="matterState.vr.tts_online ? 'has-text-primary' : 'has-text-danger'">
                      {{ matterState.vr.tts_online ? 'online' : 'offline' }}
                    </small>
                  </div>
                </div>
              </div>

            </div>
          </div>


        </div>


        <div class="box chat is-paddingless" v-if="matterConnected">
          <Chat :participants="participants" :myself="myself" :messages="matterState.vr.message_history"
            v-if="matterState.vr.message_history != null" :placeholder="placeholder" :colors="colors"
            :border-style="borderStyle" :hide-close-button="hideCloseButton"
            :close-button-icon-size="closeButtonIconSize" :submit-icon-size="submitIconSize" :async-mode="asyncMode"
            :scroll-bottom="scrollBottom" :display-header="false" :send-images="false"
            :profile-picture-config="profilePictureConfig" :timestamp-config="timestampConfig"
            @onMessageSubmit="onMessageSubmit" />
        </div>
      </div>

    </div>
    <div class="columns">
      <div class="column is-narrow">
        <div class="box">
          <p class="has-text-weight-bold has-text-grey">Lidé v zorném poli</p><br>
          <div class="columns">
            <div class="column is-narrow has-text-centered"
              style="display: flex;flex-direction: column;align-items: center;" v-for="person in people_in_view"
              :key="person.id">

              <p class="has-text-centered">
                <avatar :username="person.name" :customStyle="{ marginLeft: 'calc(50 % - 25px)' }"></avatar>
              </p>
              <p class="has-text-weight-bold" style="margin-top: 5px; margin-bottom: -8px">{{ person.name }}</p>
              <small class="has-text-centered" v-if="person.emotion_current == 'neutral'">je v pohodě</small>
              <small class="has-text-centered" v-if="person.emotion_current == 'happy'">má radost</small>
              <small class="has-text-centered" v-if="person.emotion_current == 'sad'">je smutný</small>
              <small class="has-text-centered" v-if="person.emotion_current == 'angry'">je naštvaný</small>
              <small class="has-text-centered" v-if="person.emotion_current == 'curious'">je zamyšlený</small>
              <small class="has-text-centered" v-if="person.emotion_current == 'disgusted'">je znechucen</small>
              <small class="has-text-centered" v-if="person.emotion_current == 'fear'">bojí se</small>
              <small class="has-text-centered" v-if="person.emotion_current == 'suspicious'">podezírá mne</small>
              <small class="has-text-centered" v-if="person.emotion_current == 'surprised'">je překvapený</small>
            </div>
          </div>
        </div>
      </div>
      <div class="column">
          <div class="box">
            <p class="has-text-weight-bold has-text-grey">Objekty v zorném poli</p><br>
            <div class="columns">
              <div class="column is-narrow has-text-centered"
                style="display: flex;flex-direction: column;align-items: center;" v-for="object in matterState.vm.memory.objects"
                :key="object.id">

                <p class="has-text-centered is-size-3">
                  {{ object.icon }}
                </p>
                <p class="has-text-weight-bold" style="margin-top: 5px; margin-bottom: -8px">{{ object.count }}x {{ object.name }}</p>
                
                <small class="has-text-centered">P: {{ object.probability }}%, R: {{ object.relation }}</small>
              </div>
            </div>
          </div>
        </div>
    </div>


  </div>
</template>

<script>
  import axios from 'axios';
  //import io from 'socket.io-client';
  import Avatar from 'vue-avatar'
  import {
    Buffer
  } from "buffer";
  window.Buffer = window.Buffer || Buffer;
  import {
    Chat
  } from 'vue-quick-chat'
  import 'vue-quick-chat/dist/vue-quick-chat.css';
  export default {
    name: 'ManualControl',
    props: {
      server: String,
      matterConnected: Boolean,
      matterState: Object
    },
    data() {
      return {
        latestFrame: null,
        socket: null,
        participants: [

          {
            name: 'Nestor',
            id: "r",
            profilePicture: 'https://lh3.googleusercontent.com/-G1d4-a7d_TY/AAAAAAAAAAI/AAAAAAAAAAA/AAKWJJPez_wX5UCJztzEUeCxOd7HBK7-jA.CMID/s83-c/photo.jpg'
          }
        ],
        myself: {
          name: 'Uživatel',
          id: "u",
          profilePicture: 'https://upload.wikimedia.org/wikipedia/en/thumb/a/a1/NafSadh_Profile.jpg/768px-NafSadh_Profile.jpg'
        },

        placeholder: 'Odešlete zprávu',
        colors: {
          header: {
            bg: '#58287F',
            text: '#fff'
          },
          message: {
            myself: {
              bg: '#ddd',
              text: '#000'
            },
            others: {
              bg: '#58287F',
              text: '#fff'
            },
            messagesDisplay: {
              bg: '#fff'
            }
          },
          submitIcon: '#58287F',
          submitImageIcon: '#58287F',
        },
        borderStyle: {
          topLeft: "10px",
          topRight: "10px",
          bottomLeft: "10px",
          bottomRight: "10px",
        },
        hideCloseButton: false,
        submitIconSize: 25,
        closeButtonIconSize: "20px",
        asyncMode: false,

        scrollBottom: {
          messageSent: true,
          messageReceived: true
        },
        displayHeader: true,
        profilePictureConfig: {
          others: false,
          myself: false,
          styles: {
            width: '30px',
            height: '30px',
            borderRadius: '50%'
          }
        },
        timestampConfig: {
          format: 'HH:mm',
          relative: true
        },
        resetcomplete: true
      }
    },
    components: {
      Chat,
      Avatar
    },
    computed: {

       camerastream: function () {
      return 'http://localhost:5001/video_feed';
    },
      people_in_view: function () {
        if (this.matterConnected) {
          let people = this.matterState.vm.view.people;
          let foreground = this.matterState.vm.view.foreground;
          let memory = this.matterState.vm.memory.people;

          let res = [];

          people.forEach((person) => {
            let in_foreground = false;
            if (person == foreground)
              in_foreground = true;

            let personobj = {
              id: person
            }
            memory.forEach((mem) => {
              if (mem.id == person) {
                personobj = mem;
              }
            });

            personobj.foreground = in_foreground

            res.push(personobj);
          });
          return res;
        } else {
          return []
        }
      }
    },

    methods: {

      blinking_change: function (c) {
        axios.post("http://" + this.server + ":3000/matter/lifesimulator/blinking/" + c)
      },
      onMessageSubmit: function (message) {

        axios.post("http://" + this.server + ":3000/matter/verbal/input?text=" + message.content)


      },
       rea: function () {
      this.resetcomplete = false;
      setTimeout(() => {
        this.resetcomplete = true;
      }, 1000);
    }

    },

    mounted() {
      /* // Připojit se k Socket.io serveru
       this.socket = io("http://" + this.server + ":3001");

       // Přijmout snímky ze Socket.io serveru
       this.socket.on('image', (data) => {
         // Aktualizovat nejnovější snímek
         
         let base64String = Buffer.from(data, 'ascii');

         this.latestFrame = 'data:image/jpeg;base64,' + base64String;

         
       });*/
    },
    beforeDestroy() {
      // Odpojit se od Socket.io serveru při zničení komponenty

    },

  }
</script>

<style scoped>
  .imageview {
    height: calc(900px / 2);
    width: calc(1600px / 2);
    background-image: url("../assets/loading.png");
    background-size: contain;
  }

  .chat,
  .quick-chat-container {

    height: calc(699px / 2) !important;
  }

  .mb-1 {
    margin-bottom: -7px !important;
    font-size: .8rem;
  }

  .isis {
    font-size: .6rem;
  }

  .bo {
    padding: 15px
  }

  .statemessage {
    bottom: 0;
    font-weight: bold;
    font-size: 1.4rem;
    line-height: 1.4rem;
    position: absolute;
  }

  .is-position-relative {
    position: relative;
  }

  .branding {
    height: 117px;
    background-position: right;
    background-image: url("../assets/branding.png");
    background-size: cover;
  }

  .overflow-h {
    overflow: hidden;
  }
</style>