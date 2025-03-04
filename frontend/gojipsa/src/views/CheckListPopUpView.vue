<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { writeChecklist, loadSavedChecklist } from '@/api/checklist'
import html2pdf from 'html2pdf.js'
const route = useRoute()
const apartcode = route.params.apartcode
const houseInfo = ref({
  apartmentName: '',
  aptCode: '',
  buildYear: '',
  dong: '',
  roadName: '',
  jibun: ''
})
const tickLabels = {
  0: '나쁨',
  1: '약간 나쁨',
  2: '보통',
  3: '약간 좋음',
  4: '좋음'
}
const scores = [
  ref(2),
  ref(2),
  ref(2),
  ref(2),
  ref(2),
  ref(2),
  ref(2),
  ref(2),
  ref(2),
  ref(2),
  ref(2)
]
const description = ref('')

const contentToConvert = ref(null)

/**
 * 현재 체크리스트를 PDF로 변경합니다.
 */
const convertToPdf = () => {
  const content = contentToConvert.value
  const opt = {
    margin: 10,
    filename: `[${houseInfo.value.apartmentName}]체크리스트`,
    image: { type: 'jpeg', quality: 0.98 },
    html2canvas: { scale: 1 },
    jsPDF: { unit: 'mm', format: 'a4', orientation: 'portrait' },
    pagebreak: { mode: ['css', 'legacy', 'avoid-all'] }
  }
  html2pdf().from(content).set(opt).save()
}
onMounted(() => {
  loadSavedChecklist(
    apartcode,
    ({ data }) => {
      houseInfo.value.apartmentName = data.houseInfo.apartmentName
      houseInfo.value.aptCode = data.houseInfo.aptCode
      houseInfo.value.buildYear = data.houseInfo.buildYear
      houseInfo.value.dong = data.houseInfo.dong
      houseInfo.value.roadName = data.houseInfo.roadName
      houseInfo.value.jibun = data.houseInfo.jibun
      console.log(data)
      if (data.content != null) {
        // 기존 값으로 넣어야 함
        let savedScores = data.content.score.split(',')
        // 마지막은 의미가 없으므로 지움
        savedScores.pop()
        for (var idx = 0; idx < savedScores.length; idx++) {
          scores[idx].value = Number(savedScores[idx])
        }
        description.value = data.content.description
      }
    },
    (err) => {
      console.log(err)
    }
  )
})
const postChecklist = () => {
  let score = ''
  for (var scoreIdx = 0; scoreIdx <= 10; scoreIdx++) {
    score += scores[scoreIdx].value + ','
  }
  const newChecklist = {
    aptCode: apartcode,
    userId: '',
    score: score,
    description: description.value
  }
  console.log('write checklist')
  // 저장된 값을 DB에 저장한다.
  // axios post
  writeChecklist(
    newChecklist,
    (response) => {
      console.log(response)
      alert('체크리스트 등록 성공!')
      window.close()
    },
    (error) => {
      console.log(error)
      alert('체크리스트 등록 실패!')
    }
  )
}
</script>

<template>
  <div class="check-list p-3" ref="contentToConvert">
    <div class="title mb-10 d-flex justify-content-between">
      <h1>매물 체크리스트</h1>
      <v-btn @click="convertToPdf" color="red-darken-2"
        >PDF <v-tooltip activator="parent" location="bottom">PDF로 저장하기</v-tooltip></v-btn
      >
    </div>
    <p>아파트 명 : {{ houseInfo.apartmentName }}</p>
    <p>건축연도 : {{ houseInfo.buildYear }}</p>
    <p>위치 : {{ houseInfo.roadName }} {{ houseInfo.dong }} {{ houseInfo.jibun }}</p>
    <div class="content mt-10">
      <div class="about-outdoor mb-15">
        <h3 class="mb-5">건물 내/외부</h3>
        <div class="question">
          <p>1. 관리실이 있나요</p>
          <v-slider
            class="score-slider"
            :ticks="tickLabels"
            :max="4"
            color="deep-purple-lighten-3"
            v-model="scores[0].value"
            show-ticks="always"
            step="1"
            tick-size="3"
          ></v-slider>
        </div>
        <div class="question">
          <p>2. 주 출입구에 방범시설이 있나요</p>
          <v-slider
            class="score-slider"
            color="deep-purple-lighten-2"
            :ticks="tickLabels"
            :max="4"
            v-model="scores[1].value"
            show-ticks="always"
            step="1"
            tick-size="3"
          ></v-slider>
        </div>
        <div class="question">
          <p>3. CCTV가 있나요</p>
          <v-slider
            class="score-slider"
            :ticks="tickLabels"
            :max="4"
            color="deep-purple-lighten-1"
            v-model="scores[2].value"
            show-ticks="always"
            step="1"
            tick-size="3"
          ></v-slider>
        </div>
        <div class="question">
          <p>4. 주변에 편의시설이 있나요</p>
          <v-slider
            class="score-slider"
            :ticks="tickLabels"
            :max="4"
            color="deep-purple-darken-1"
            v-model="scores[3].value"
            show-ticks="always"
            step="1"
            tick-size="3"
          ></v-slider>
        </div>
      </div>
      <div class="about-indoor mb-10">
        <h3>집안 체크</h3>
        <div class="question">
          <p>1. 현관의 잠금장치가 잘 되어 있나요</p>
          <v-slider
            class="score-slider"
            :ticks="tickLabels"
            :max="4"
            color="indigo-lighten-3"
            v-model="scores[4].value"
            show-ticks="always"
            step="1"
            tick-size="3"
          ></v-slider>
        </div>
        <div class="question">
          <p>2. 수납공간은 넉넉한가요</p>
          <v-slider
            class="score-slider"
            :ticks="tickLabels"
            :max="4"
            color="indigo-lighten-2"
            v-model="scores[5].value"
            show-ticks="always"
            step="1"
            tick-size="3"
          ></v-slider>
        </div>
        <div class="question">
          <p>3. 창문의 크기 및 위치, 채광상태는 좋은가요</p>
          <v-slider
            class="score-slider"
            :ticks="tickLabels"
            :max="4"
            color="indigo-lighten-1"
            v-model="scores[6].value"
            show-ticks="always"
            step="1"
            tick-size="3"
          ></v-slider>
        </div>
        <div class="question">
          <p>4. 에어컨 근처나 방구석에 곰팡이가 있나요</p>
          <v-slider
            class="score-slider"
            :ticks="tickLabels"
            :max="4"
            color="indigo-darken-1"
            v-model="scores[7].value"
            show-ticks="always"
            step="1"
            tick-size="3"
          ></v-slider>
        </div>
        <div class="question">
          <p>5. 창문마다 방충망과 방범창이 있나요</p>
          <v-slider
            class="score-slider"
            :ticks="tickLabels"
            :max="4"
            color="indigo-darken-2"
            v-model="scores[8].value"
            show-ticks="always"
            step="1"
            tick-size="3"
          ></v-slider>
        </div>
        <div class="question">
          <p>6. 난방은 잘 되어 있나요</p>
          <v-slider
            class="score-slider"
            :ticks="tickLabels"
            :max="4"
            color="indigo-darken-3"
            v-model="scores[9].value"
            show-ticks="always"
            step="1"
            tick-size="3"
          ></v-slider>
        </div>
        <div class="question">
          <p>7. 수압 및 배수상태는 양호한가요</p>
          <v-slider
            class="score-slider"
            :ticks="tickLabels"
            :max="4"
            color="indigo-darken-4"
            v-model="scores[10].value"
            show-ticks="always"
            step="1"
            tick-size="3"
          ></v-slider>
        </div>
      </div>
      <div class="specific-content">
        <h3>특이 사항</h3>
        <div class="question">
          <v-textarea
            class="text-area"
            variant="filled"
            label="파손된 가구 등 특이사항을 작성해주세요"
            v-model="description"
            rows="10"
            row-height="60"
            shaped
          ></v-textarea>
        </div>
      </div>
    </div>
    <div class="d-flex justify-center">
      <v-btn @click="postChecklist" color="indigo-darken-4">저장</v-btn>
    </div>
  </div>
</template>

<style scoped>
.content {
  width: 36rem;
}
.question {
  margin: 2rem 0;
}
.score-slider {
  width: 35rem;
}
.text-area {
  width: 36rem;
}
</style>
