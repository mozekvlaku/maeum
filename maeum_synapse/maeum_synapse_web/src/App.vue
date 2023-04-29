<template>
  <div id="app">

    <div class="container">
      <b-navbar :centered="true">
        <template #brand>
          <b-navbar-item tag="div">
            <a class="button is-light" @click="settings">
              &nbsp;&nbsp;
              <b-icon icon="cog" />&nbsp;&nbsp;
            </a>
          </b-navbar-item>
          <b-navbar-item>
            <img src="./assets/logo.svg" class="logo">
          </b-navbar-item>
        </template>
        <template #start>
          <b-navbar-item tag="div" v-if="activeTab == 1">
            <b-field>
              <b-switch v-model="rightPower" @input="rightPowerL"  passive-type='is-dark' type='is-danger'>{{ rightPower ? "Pravá str. napájena" :
    "Pravá str. vypnuta" }}</b-switch>
            </b-field>
          </b-navbar-item>
          <b-navbar-item tag="div" v-if="activeTab == 1">
            <b-field>
              <b-switch v-model="leftPower" @input="leftPowerL" passive-type='is-dark' type='is-danger'>{{ leftPower ? "Levá str. napájena" :
    "Levá str. vypnuta" }}</b-switch>
            </b-field>
          </b-navbar-item>
        </template>

        <template #end>
          <b-navbar-item tag="div" v-if="matterConnected">
            <div class="has-text-success">●</div>&nbsp;Připojeno k&nbsp;<b>{{matterAddress}}</b>
          </b-navbar-item>
          <b-navbar-item tag="div" v-if="!matterConnected">
            <div class="has-text-warning">●</div>&nbsp;Připojuji se k Matter...
          </b-navbar-item>
          <b-navbar-item tag="div">
            <div class="buttons">
              <a class="button is-light is-danger" @click="emergencyStop">
                &nbsp;&nbsp;&nbsp;&nbsp;
                <b-icon icon="car-brake-alert" />&nbsp;&nbsp;&nbsp;&nbsp;
              </a>
            </div>
          </b-navbar-item>

        </template>
      </b-navbar>
    </div>
    <b-tabs type="is-boxed" v-model="activeTab" position="is-centered">
      <b-tab-item>
        <template #header>
          <b-icon icon="home"></b-icon>
          <span> Přehled</span>
        </template>

        <div class="container">
          <Overview :matterState="matterState" :matterConnected="matterConnected"
            @motorsOn="leftPowerC(true);rightPowerC(true)" :server="matterAddress" />
        </div>
      </b-tab-item>
      <b-tab-item>
        <template #header>
          <b-icon icon="cake"></b-icon>
          <span> Jednoduché řízení</span>
        </template>

        <div class="container">
          <Home @motorsOn="leftPowerC(true);rightPowerC(true)" :server="serverAddress" />
        </div>
      </b-tab-item>
      <b-tab-item>
        <template #header>
          <b-icon icon="cog"></b-icon>
          <span>Manuální ovládání</span>
        </template>
        <div class="container">
          <ManualControl :serverAddress="serverAddress" :matterState="matterState" />
        </div>
      </b-tab-item>
      <b-tab-item>
        <template #header>
          <b-icon icon="bug"></b-icon>
          <span>Debug</span>
        </template>
        <div class="container">
          <DebugPage :matterState="matterState" />
        </div>
      </b-tab-item>
    </b-tabs>


  </div>
</template>

<script>
  import ManualControl from './components/ManualControl.vue'
  import Home from './components/Home.vue'
  import Overview from './components/Overview.vue'
  import DebugPage from './components/DebugPage.vue'
  import axios from 'axios';
  import io from 'socket.io-client'
  export default {
    name: 'App',
    components: {
      ManualControl,
      Home,
      Overview,
      DebugPage
    },
    data() {
      return {
        rightPower: true,
        activeTab: 0,
        leftPower: true,
        connected: false,
        serverAddress: "maxr-vebot",
        socket: null,
        matterAddress: "192.168.102.171",
        matterState: {

        },
        matterConnected: false
      }
    },
    computed: {
      loading: function () {
        return !this.connected;
      }
    },
    methods: {
      getMatterState: function () {
        axios.get("http://" + this.matterAddress + ":3000/matter/state").then((result) => {
          this.matterState = result.data.result;
          console.log(this.matterState)
          this.matterConnected = true;
        }).catch(() => {
          this.matterConnected = false;
        });
      },
      pingMatter: function () {
        /*this.getMatterState();
        setTimeout(()=> this.pingMatter(), 3000);*/
        // Připojit se k Socket.io serveru
        this.socket = io("http://" + this.matterAddress + ":3001");

        // Přijmout snímky ze Socket.io serveru
        this.socket.on('state', (data) => {
          this.matterState = data
          this.leftPower = data.mm.nestor_state.left;
          this.rightPower = data.mm.nestor_state.right;
          console.log(data)

        });

        this.socket.on('connect', () => {
          this.matterConnected = true;
          this.getMatterState();
        });

        this.socket.on('disconnect', () => {
          this.matterConnected = false;
        });
      },


      emergencyStop: function () {
        this.leftPowerC(false)
        this.rightPowerC(false)
        this.activeTab = 0;
        this.$buefy.snackbar.open({
          duration: 5000,
          message: 'Nestor byl úspěšně nouzově vypnut.',
          type: 'is-danger',
          position: 'is-bottom-left',
          queue: false
        })
      },
      settings: function () {
        this.$buefy.dialog.prompt({
          message: `Adresa serveru`,
          inputAttrs: {
            placeholder: '192.168.102.171:3000',
            maxlength: 100,
            value: this.matterAddress
          },
          trapFocus: true,
          onConfirm: (value) => {

            this.$buefy.toast.open(`Připojuji k ${value}`);
            this.matterAddress = value;
            this.connected = false;
            this.heartbeat();
          }
        })

      },
      heartbeat: function () {
        const vue = this;
        axios.get("http://" + this.serverAddress + ":3000/api/ping").then(function (response) {
          if (response.data == "Pong") {
            vue.connected = true;
          } else {
            vue.connected = false;
          }
        }).catch(function () {
          this.$buefy.toast.open({
            duration: 5000,
            message: "Připojení k robotovi je ztraceno, zkouším za další minutu.",
            position: 'is-bottom',
            type: 'is-danger'
          })
          vue.connected = false;
        })


        setTimeout(() => {
          this.heartbeat()
        }, 30000)
      },
      rightPowerL: function() { this.rightPowerC(this.rightPower)},
    leftPowerL: function() { this.leftPowerC(this.leftPower)},
      rightPowerC: function (val) {
        if (val) {

          axios.post("http://" + this.matterAddress + ":3000/matter/motoric/right/on").catch(function (
            error) {
            this.$buefy.toast.open({
              duration: 5000,
              message: error,
              position: 'is-bottom',
              type: 'is-danger'
            })
          })
        } else {
          axios.post("http://" + this.matterAddress + ":3000/matter/motoric/right/off").catch(function (
            error) {
            this.$buefy.toast.open({
              duration: 5000,
              message: error,
              position: 'is-bottom',
              type: 'is-danger'
            })
          })
        }
      },
      leftPowerC: function (val) {
        if (val) {

          axios.post("http://" + this.matterAddress + ":3000/matter/motoric/left/on").catch(function (
            error) {
            this.$buefy.toast.open({
              duration: 5000,
              message: error,
              position: 'is-bottom',
              type: 'is-danger'
            })
          })
        } else {
          axios.post("http://" + this.matterAddress + ":3000/matter/motoric/left/off").catch(function (
            error) {
            this.$buefy.toast.open({
              duration: 5000,
              message: error,
              position: 'is-bottom',
              type: 'is-danger'
            })
          })
        }
      }
    },
    mounted() {
      this.heartbeat();
      this.pingMatter();
    }
  }
</script>

<style>
  .logo {
    height: 100px;
  }

  .fh {
    height: 100%;
    display: flex !important;
    flex-direction: column;
    align-items: center;
    justify-content: center;
  }

  .fhr {
    display: flex;
    align-items: stretch;
  }

  .fw {
    width: 100%;
  }
</style>