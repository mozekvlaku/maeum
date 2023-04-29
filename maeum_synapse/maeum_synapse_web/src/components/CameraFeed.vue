<template>
  <div class="hello">
    <div class="columns is-centered">
      <div class="column has-text-centered is-narrow">
       <div class="box is-paddingless overflow-h">
          <div class="imageview">
            <img :src="camerastream" v-if="resetcomplete" class="flipped" />
          </div>
        </div>
        
        <b-button type="is-light"  icon-left="refresh" @click="rea">Aktualizovat kameru</b-button>
      </div>
    </div>
  </div>
</template>

<script>
  /*import axios from 'axios';*/
  export default {
    name: 'CameraFeed',
    props: {

      serverAddress: String
    },
    data() {
      return {
        resetcomplete: false
      }
    },
   computed: {
      camerastream: function () {
       return 'http://localhost:5001/video_feed';
      }
    }, methods: {
      rea: function () {
        this.resetcomplete = false;
        setTimeout(() => {
          this.resetcomplete = true;
        }, 1000);
      }
    }
  }
</script>

<style scoped>

.imageview
{
  height: calc(900px / 1.5);
  width: calc(1600px / 1.5);
  background-image: url("../assets/loading.png");
  background-size: contain;
}
.overflow-h {
  overflow: hidden;
}
</style>