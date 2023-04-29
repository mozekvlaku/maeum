<template>
  <div class="hello">
    <div class="has-text-centered"><b-field>
            <b-switch v-model="change" passive-type='is-dark' type='is-danger'>{{ change ? "Manuální ovládání je zapnuto. Dávejte si pozor!" :
            "Robot je v ochranném režimu. Nezapínejte manuální ovládání, pokud nevíte, co děláte." }}</b-switch>
    </b-field></div>
    <br>
  <div class="columns is-centered"> <b-field grouped>
              <b-input placeholder="Název souboru" v-model="filename" type="text"></b-input>
              <p class="control">
                  <b-button class="button is-primary" @click="generateFile">Stáhnout</b-button>
              </p>
          </b-field></div>

    <br>
    <div class="columns">
      <div class="column">
        <p class="has-text-centered"><img src="../assets/left.svg" class="topimg"/></p>
        <b>Levá strana</b>
        <div v-for="index in 16" :key="index">
          <span class="demonstration">Servo {{(index-1)}}: {{servos_left['s'+(index-1)]}}º<span v-if="matterState.mm"><span v-if="matterState.mm.servos.left['s' + (index - 1)]">; {{ matterState.mm.servos.left['s' + (index - 1)].friendly }}</span></span></span>
          <b-slider v-model="servos_left['s'+(index-1)]" @input="changing((index-1), 'left')" :min="0" :max="180"></b-slider>
        </div>
      </div>
      <div class="column">
        <p class="has-text-centered"><img src="../assets/right.svg" class="topimg" /></p>
        <b>Pravá strana</b>
        <div v-for="index in 16" :key="(index)">
          <span class="demonstration">Servo {{(index-1)}}: {{servos_right['s'+(index-1)]}}º<span v-if="matterState.mm"><span v-if="matterState.mm.servos.right['s' + (index - 1)]">; {{ matterState.mm.servos.right['s' + (index - 1)].friendly }}</span></span></span>
          <b-slider v-model="servos_right['s'+(index-1)]" @input="changing((index-1), 'right')" :min="0" :max="180"></b-slider>
        </div>
      </div>
      <div class="column">
        <p class="has-text-centered"><img src="../assets/virtual.svg" class="topimg" /></p>
        <b>Virtuální serva</b>
        <div>
          <span class="demonstration">Virtual 0: {{servos_virtual['s0']}}º; Jaw</span>
          <b-slider v-model="servos_virtual['s0']" @input="changing(0, 'virtual')" :min="20" :max="38">
          </b-slider>
        </div>
        <div>
          <span class="demonstration">Virtual 1: {{servos_virtual['s1']}}º; AllEyes</span>
          <b-slider v-model="servos_virtual['s1']" @input="changing(1, 'virtual')" :min="0" :max="180">
          </b-slider>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import axios from 'axios';
  export default {
    name: 'ManualControl',
    props: {

      serverAddress: String,
      matterState: Object
    },
    data() {
      return {
        filename: "",
        change: false,
        servos_virtual: {
          s0: 38,
          s1: 85,
        },
        servos_left: {
          s0: 4,
          s1: 35,
          s2: 42,
          s3: 0,
          s4: 0,
          s5: 60,
          s6: 0,
          s7: 0,
          s8: 0,
          s9: 0,
          s10:0,
          s11:0,
          s12:0,
          s13:0,
          s14:0,
          s15:180
        },
        servos_right: {
          s0: 176,
          s1: 25,
          s2: 25,
          s3: 180,
          s4: 90,
          s5: 30,
          s6: 69,
          s7: 90,
          s8: 0,
          s9: 0,
          s10:0,
          s11:0,
          s12:0,
          s13:0,
          s14:0,
          s15:0
        }
      }
    },
    methods: {
      generateFile: function() {
              const blob = JSON.stringify(this.matterState.mm.servos);
        const elem = window.document.createElement('a');
      elem.href = window.URL.createObjectURL(blob);
      elem.download = this.filename;
      document.body.appendChild(elem);
      elem.click();
      document.body.removeChild(elem);
      },
      changing: function(id,side) {
        if(this.change) {
        console.log(id);
        console.log(side);
        var sd = "left";
        var dg = this.servos_left["s"+id];
        if(side == "right"){
          sd = "right";
          dg = this.servos_right["s"+id];
        }
        if (side == "virtual") {
          sd = "virtual";
          dg = this.servos_virtual["s" + id];
        }
        axios.get("http://"+this.serverAddress+":3000/api/servo_direct/"+sd+"/"+id+"/" + dg).catch(function(error) {
           this.$buefy.toast.open({
            duration: 5000,
            message: error,
            position: 'is-bottom',
            type: 'is-danger'
          })
        })
      }
    }
  }}
</script>

<style scoped>
.topimg
{
  height: 200px;
}
</style>