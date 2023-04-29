<template>
  <div class="hello">
    <div class="columns">
            <div class="column is-narrow">
              <h4 class="title is-4">Emotional Cortexlet</h4>
              <table class="table is-striped">
                <thead>
                  <tr>
                    <th>Type</th>
                    <th>Origin</th>
                    <th>Type String</th>
                    <th>Cadence</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(step, index) in matterState.ep.pipeline" :key="index">
                    <td>{{ step.type }}</td>
                    <td>{{ step.origin }}</td>
                    <td>{{ step.type_string }}</td>
                    <td>{{ step.cadence }}</td>
                  </tr>
                </tbody>
              </table>
              <p><b>Current:</b> {{ matterState.ep.em_state }} ({{ matterState.ep.em_state_int }})</p>
              
            </div>
            <div class="column is-narrow">
                <h4 class="title is-4">Motoric Cortexlet</h4>
                <p><b>Nestor Online:</b> {{ matterState.mm.nestor_online }}</p>
                <p><b>Left Online:</b> {{ matterState.mm.nestor_state.left }}</p>
                <p><b>Right Online:</b> {{ matterState.mm.nestor_state.right }}</p>
                <br>
                 <h4 class="title is-4">Verbal Cortexlet</h4>
                  <p><b>Rasa Online:</b> {{ matterState.vr.rasa_online }}</p>
                  <p><b>TTS Online:</b> {{ matterState.vr.tts_online }}</p>
                  <br>
                   <h4 class="title is-4">Biomimetic Cortexlet</h4>
                    <p><b>Blinking:</b> {{ matterState.vb.blinking }}</p>
                    <p><b>Mimics:</b> {{ matterState.me.doing_mimics }}</p>
                      <p><b>Looking:</b> {{ matterState.le.activated_looking }}</p>
                      <p><b>X eyes:</b> {{ matterState.le.x_eyes }}</p>
                          <p><b>X axis:</b> {{ matterState.le.x_axis }}</p>
              </div>
             <div class="column is-narrow">
                  <h4 class="title is-4">Memoric Cortexlet</h4>
                  <table class="table is-striped">
                  <thead>
                    <tr>
                      <th>Icon</th>
                      <th>Name</th>
                      <th>Relation</th>
                      <th>Id</th>
                      <th>Probability</th>
                      <th>Count</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="(step, index) in matterState.vm.memory.objects" :key="index">
                      <td>{{ step.icon }}</td>
                      <td>{{ step.name }}</td>
                      <td>{{ step.relation }}</td>
                      <td>{{ step.id }}</td>
                      <td>{{ step.probability }}</td>
                      <td>{{ step.count }}</td>
                      
                    </tr>
                  </tbody>
                </table>
                <table class="table is-striped">
                    <thead>
                      <tr>
                        <th>Name</th>
                        <th>Emotion</th>
                        <th>Id</th>
                        <th>In view</th>
                        <th>Relation</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="(step, index) in matterState.vm.memory.people" :key="index">
                
                        <td>{{ step.name }}</td>
                        <td>{{ step.emotion_current }}</td>
                        <td>{{ step.id }}</td>
                        <td>{{ step.in_view }}</td>
                        <td>{{ step.maeum_emotional_relation }}</td>
                      
                      </tr>
                    </tbody>
                  </table>
                  
                </div>
          </div>
                 <b-collapse :open="false" aria-id="contentIdForA11y1">
              <template #trigger="props">
                  <b-button
                      label="Open raw data"
                      type="is-primary"
                      aria-controls="contentIdForA11y1"
                      :aria-expanded="props.open" />
              </template>
              <div class="notification">
                  <div class="content">
                      <pre>
      {{ matterState }}
    </pre>
                  </div>
              </div>
          </b-collapse>
    
  </div>
</template>

<script>
  export default {
    name: 'PredefinedActions',
    props: {
      matterState: Object

    },
    data() {
      return {
      
      }
    }
  }
</script>

<style scoped>
  .flipped {
    transform: rotate(18deg) scale(1.5)
  }

  .imageview {
    height: calc(900px / 1.5);
    width: calc(1600px / 1.5);
    background-image: url("../assets/loading.png");
    background-size: contain;
  }

  .overflow-h {
    overflow: hidden;
  }
</style>