<template>
  <div class="hello">
    <div class="columns is-centered">
      <div class="column is-half">
        <div class="box">
      <b-field label="Vstup">
        <b-input maxlength="800" type="textarea" v-model="message"></b-input>
      </b-field>
      </div></div>
      <div class="column is-narrow">
        <div class="box">
          <b-field label="Matter Server">
            <b-input v-model="ttsServer" maxlength="120"></b-input>
          </b-field>
          <div class="buttons">
            <b-button type="is-primary" expanded icon-left="account-voice" @click="say(); message = ''">Odeslat
            </b-button>
            <b-button type="is-light" expanded icon-left="backspace" @click="message = ''">Vyčistit</b-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import axios from 'axios';
  export default {
    name: 'TextToSpeech',
    props: {
    },
    data() {
      return {
        resetcomplete: true, 
        message: "",
        ttsServer: "localhost"
      }
    },
    methods: {
      say: function () {
        const vue = this;
        axios.post("http://"+this.ttsServer+":3000/matter/verbal/input?text=" + this.message).then(function (response) {
          vue.$buefy.toast.open({
            message: response.data.returned,
            type: 'is-success'
          })
        }).catch(function (error) {
           vue.$buefy.toast.open({
            duration: 5000,
            message: error,
            position: 'is-bottom',
            type: 'is-danger'
          })
        })
      }
    }
  }
</script>

<style scoped>
</style>