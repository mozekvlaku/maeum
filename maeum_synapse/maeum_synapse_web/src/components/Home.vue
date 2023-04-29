<template>
  <div class="hello">
    <div class="columns">
      <div class="column">

        <div class="box fh">
          <p class="has-text-centered"><b>Ovládání očí</b></p>
          <br>
          <div class="columns is-variable is-1 is-mobile is-vcentered is-multiline">
            <div class="column is-one-third has-text-centered">
              <b-button type="is-light" icon-right="arrow-top-left" @click="y_eyes += increment,x_eyes -= increment">
              </b-button>
            </div>
            <div class="column is-one-third has-text-centered">
              <b-button type="is-light" icon-right="arrow-up" @click="y_eyes += increment" size="is-medium"></b-button>
            </div>
            <div class="column is-one-third has-text-centered">
              <b-button type="is-light" icon-right="arrow-top-right" @click="y_eyes += increment,x_eyes += increment">
              </b-button>
            </div>

            <div class="column is-one-third has-text-centered">
              <b-button type="is-light" icon-right="arrow-left" @click="x_eyes -= increment" size="is-medium">
              </b-button>
            </div>
            <div class="column is-one-third has-text-centered">
              <b-button type="is-light is-primary" icon-right="circle" @click="x_eyes = 90,y_eyes = 90"
                size="is-medium"></b-button>
            </div>
            <div class="column is-one-third has-text-centered">
              <b-button type="is-light" icon-right="arrow-right" @click="x_eyes += increment" size="is-medium">
              </b-button>
            </div>

            <div class="column is-one-third has-text-centered">
              <b-button type="is-light" icon-right="arrow-bottom-left"
                @click="y_eyes -= increment, x_eyes -= increment"></b-button>
            </div>
            <div class="column is-one-third has-text-centered">
              <b-button type="is-light" icon-right="arrow-down" @click="y_eyes -= increment" size="is-medium">
              </b-button>
            </div>
            <div class="column is-one-third has-text-centered">
              <b-button type="is-light" icon-right="arrow-bottom-right"
                @click="y_eyes -= increment, x_eyes += increment"></b-button>
            </div>

          </div>
          <div class="columns is-vcentered fw">
            <div class="column is-narrow"><b>X</b></div>
            <div class="column">
              <b-slider size="is-small" v-model="x_eyes" indicator :min="0" :max="180">
              </b-slider>
            </div>
          </div>
          <div class="columns is-vcentered fw">
            <div class="column is-narrow"><b>Y</b></div>
            <div class="column">
              <b-slider size="is-small" v-model="y_eyes" indicator :min="0" :max="180">
              </b-slider>
            </div>
          </div>

        </div>
      </div>
      <div class="column is-narrow">
        <div class="box is-paddingless overflow-h">
          <div class="imageview">
            <img  v-if="true" class="flipped" />
          </div>
        </div>
      </div>
      <div class="column">
        <div class="box branding"></div>
        
        <div class="box fhr has-text-centered">
          <div class="buttons">
            <b-button type="is-light" expanded icon-left="refresh" @click="rea">Aktualizovat kameru</b-button>
            
            <b-button type="is-light is-danger" expanded icon-left="exclamation"
              @click="motorsOn">Spustit motory</b-button>
          
          </div>
          <b-field>
            <b-switch v-model="passive">Pasivní mód</b-switch>
          </b-field>
        </div>
        <div class="box">
        <b-field label="Krk">
          <b-slider size="is-small" v-model="neck" indicator :min="0" :max="180">
          </b-slider>
        </b-field>
        </div>
      </div>
    </div>
    <div class="columns">
      <div class="column">
        <div class="box">
          <div class="columns is-vcentered">
            <div class="column">
              <div class="buttons">
                <b-button type="is-light" expanded icon-left="eye" @click="blink">Mrknout</b-button>
                <b-button type="is-light" expanded icon-left="eye-arrow-left">Mrknout levým okem</b-button>
                <b-button type="is-light" expanded icon-left="eye-arrow-right">Mrknout pravým okem</b-button>
              </div>
            </div>
            <div class="column">
              <b-field label="Víčka">
                <b-slider size="is-small" v-model="lids" indicator :min="0" :max="180">
                </b-slider>
              </b-field>
              <b-field label="Obočí">
                <b-slider size="is-small" v-model="brows" indicator :min="0" :max="180">
                </b-slider>
              </b-field>
            </div>
            <div class="column">
              <div class="buttons">
                <b-button type="is-light" expanded icon-left="eye" @click="blinkease">Mrknout <small>Easing</small>
                </b-button>
                <b-button type="is-light" expanded icon-left="emoticon-excited" @click="mhoureni">Přimhouřit</b-button>
                <b-button type="is-light" expanded icon-left="emoticon-dead" @click="kuleni">Vykulit</b-button>
              </div>
            </div>

          </div>
          <b-button type="is-light is-primary" expanded icon-left="eye-off" v-if="lids < 170" @click="lids = 180">Zavřít
          </b-button>
          <b-button type=" is-primary" expanded icon-left="eye" v-if="lids >= 170" @click="lids = 64">Otevřít</b-button>
        </div>
      </div>
      <div class="column">
        <div class="box">
          <div class="columns is-vcentered">
            <div class="column">
              <div class="buttons">
                <b-button type="is-light" expanded icon-left="emoticon" @click="fn_smile">Úsměv</b-button>
                <b-button type="is-light" expanded icon-left="emoticon-frown" @click="fn_sadness">Smutek</b-button>
                <b-button type="is-light" expanded icon-left="baby-face" @click="fn_angry">Naštvání</b-button>
                <b-button type="is-light" expanded icon-left="baby-face" @click="fn_creepedout">Vyděšení</b-button>
              </div>
              <b-button type="is-light is-primary" expanded icon-left="emoticon-happy" @click="mouth = 38"
                v-if="mouth < 25">Zavřít</b-button>
              <b-button type="is-primary" expanded icon-left="emoticon" v-if="mouth >= 25" @click="mouth = 20">Otevřít
              </b-button>
            </div>
            <div class="column">
              <b-field label="Čelist">
                <b-slider size="is-small" v-model="mouth" indicator :min="20" :max="38">
                </b-slider>
              </b-field>
              <b-field label="Vrchní ret">
                <b-slider size="is-small" v-model="mu" indicator :min="0" :max="180">
                </b-slider>
              </b-field>
              <b-field label="Levý horní aktuátor">
                <b-slider size="is-small" v-model="lua" indicator :min="0" :max="180">
                </b-slider>
              </b-field>
              <b-field label="Levý spodní aktuátor">
                <b-slider size="is-small" v-model="lba" indicator :min="0" :max="180">
                </b-slider>
              </b-field>

            </div>
            <div class="column">
              <b-field label="Spodní ret">
                <b-slider size="is-small" v-model="ml" indicator :min="0" :max="180">
                </b-slider>
              </b-field>
              <b-field label="Pravý horní aktuátor">
                <b-slider size="is-small" v-model="rua" indicator :min="0" :max="180">
                </b-slider>
              </b-field>
              <b-field label="Pravý spodní aktuátor">
                <b-slider size="is-small" v-model="rba" indicator :min="0" :max="180">
                </b-slider>
              </b-field>
            </div>

          </div>
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
      server: String
    },
    data() {
      return {
        lua: 0,
        lba: 44,
        rua: 30,
        rba: 69,
        mu: 180,
        ml: 180,
        neck: 90,
        x_eyes: 90,
        y_eyes: 90,
        passive: false,
        lids: 42,
        brows: 0,
        mouth: 38,
        resetcomplete: false,
        increment: 10,
        blinking: false
      }
    },
    computed: {
      camerastream: function () {
        return 'http://localhost:3001/matter/stream';
      }
    },
    watch: {
      lids: function (val) {
        if (val) {

          this.changing(1, "virtual", val);
        }
      },
      mouth: function (val) {
        if (val) {

          this.changing(0, "virtual", val);
        }
      },
      brows: function (val) {
        if (val) {

          this.changing(0, "left", val);
        }
      },
      x_eyes: function (val) {
        if (val) {

          this.changing(7, "right", val);
        }
      },
      y_eyes: function (val) {
        if (val) {

          this.changing(4, "right", val);
        }
      },
      lua: function (val) {
        if (val) {

          this.changing(4, "left", val);
        }
      },
      lba: function (val) {
        if (val) {

          this.changing(6, "left", val);
        }
      },
      rua: function (val) {
        if (val) {

          this.changing(5, "right", val);
        }
      },
      rba: function (val) {
        if (val) {

          this.changing(6, "right", val);
        }
      },
      mu: function (val) {
        if (val) {

          this.changing(0, "right", val);
        }
      },
      ml: function (val) {
        if (val) {

          this.changing(15, "left", val);
        }
      },
      neck: function (val) {
        if (val) {

          this.changing(5, "left", val);
        }
      }
    },
    methods: {
      blink: function () {
        this.lids = 180;
        setTimeout(() => {
          this.lids = 64;
        }, 300);
      },
      passiveDo: function() {
        setInterval(() => {
          if(this.passive){
              switch (Math.floor(Math.random() * 7))
              {
                case 0:
                  if(this.x_eyes < 150)
                  {
                    this.x_eyes += Math.floor(Math.random() * 20)
                  }
                  if (this.x_eyes > 20) {
                    this.x_eyes -= Math.floor(Math.random() * 20)
                  }
                break;
                case 1:
                  if (this.y_eyes < 120) {
                    this.y_eyes += Math.floor(Math.random() * 50)
                  }
                  if (this.y_eyes > 50) {
                    this.y_eyes -= Math.floor(Math.random() * 50)
                  }
                  break;
                case 2:
                  if (this.x_eyes < 120) {
                    this.x_eyes += Math.floor(Math.random() * 50)
                  }
                  if (this.x_eyes > 50) {
                    this.x_eyes -= Math.floor(Math.random() * 50)
                  }
                  break;
                case 3:
                  if (this.y_eyes < 150) {
                    this.y_eyes += Math.floor(Math.random() * 20)
                  }
                  if (this.y_eyes > 20) {
                    this.y_eyes -= Math.floor(Math.random() * 20)
                  }
                  break;
                case 4:
                  if(!this.blinking)
                    this.blinkease()
                  break;
                case 5:
                  this.fn_reset()
                  break;
                case 6:
                  if (Math.floor(Math.random() * 5) == 2) {

                    this.fn_smile()
                  }
                  break;
              }
            
          }
        }, 805);
      },
      blinkease: function () {
        this.blinking = true;
        var beforeLids = parseInt(this.lids);

        for (let i = beforeLids; i <= 180; i += 5) {
          setTimeout(() => {
            this.lids = i;
          }, 0.01);
        }
        setTimeout(() => {

          for (let y = 180; y >= beforeLids; y -= 5) {
            setTimeout(() => {
              this.lids = y;
            }, 0.01);
          }
          
        }, 300);
        setTimeout(() => {

         this.blinking = false

        },900);
      },
      mhoureni: function () {
        this.lids = 90;
      },
      kuleni: function () {
        this.lids = 21;
      },
      fn_reset: function () {
        this.rua = 30;
        this.rba = 71;
        this.lua = 8;
        this.lba = 46;
        this.mu = 180;
        this.mouth = 38;
        this.ml = 180;
        this.lids = 43;
        this.y_eyes = 90;
        this.x_eyes = 90;
      },
      motorsOn: function () {
        this.$emit('motorsOn');
      },
      rea: function () {
        this.resetcomplete = false;
        setTimeout(() => {
          this.resetcomplete = true;
        }, 1000);
      },
      changing: function (id, sd, dg) {

        axios.get("http://" + this.server + ":3000/api/servo_direct/" + sd + "/" + id + "/" + dg).catch(function (
          error) {
          this.$buefy.toast.open({
            duration: 5000,
            message: error,
            position: 'is-bottom',
            type: 'is-danger'
          })
        })
      },
      sleep: function (milliseconds) {
        const date = Date.now();
        let currentDate = null;
        do {
          currentDate = Date.now();
        } while (currentDate - date < milliseconds);
      },
      fn_smile: function () {
        this.rua = 0;
        this.rba = 116;
        this.lua = 180;
        this.lba = 30;
      },
      fn_angry: function () {
        this.rua = 0;
        this.rba = 43;
        this.lua = 184;
        this.lba = 89;
      },
      fn_sadness: function () {
        this.rua = 56;
        this.rba = 180;
        this.lua = 100;
        this.lba = 1;
      },
      fn_grossout: function () {
        this.rua = 67;
        this.rba = 172;
        this.lua = 54;
        this.lba = 89;
      },
      fn_creepedout: function () {
        this.rua = 67;
        this.rba = 172;
        this.lua = 54;
        this.lba = 2;
      },
      fn_snob: function () {
        this.rua = 0;
        this.rba = 0;
        this.lua = 180;
        this.lba = 2;
      },
      fn_trump: function () {
        this.rua = 0;
        this.rba = 0;
        this.lua = 180;
        this.lba = 160;
      },
    },
    mounted() {
      this.passiveDo();
    }
  }
</script>

<style scoped>

  .imageview {
    height: calc(900px / 2);
    width: calc(1600px / 2);
    background-image: url("../assets/loading.png");
    background-size: contain;
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