<script setup>
import { storeToRefs } from 'pinia'
import { useUserStore } from '../../stores/user'
import { ref } from 'vue'
const userStore = useUserStore()
// "userStore"에서 "menuList"를 가져와서 reactive reference로 사용.
const { menuList } = storeToRefs(userStore)
const dongcode = ref('')
/**
 * 로그아웃 함수
 */

async function logout() {
  console.log('로그아웃 실행')
  // JWT 토큰 삭제
  userStore.logout()
}
</script>

<template>
  <v-app-bar>
    <img src="@/assets/img/logo.png" style="width: 4rem; height: 4rem" alt="logo" />
    <v-app-bar-title
      text="Go!집사"
      style="margin: 0; padding-left: 0"
      class="title v-col-1"
      @click="$router.push('/')"
    >
    </v-app-bar-title>
    <v-list class="v-col-3">
      <router-link :to="{ name: 'apart', params: { code: dongcode } }">실거래가 확인</router-link>
      <router-link :to="{ name: 'analysis' }">고집사의 경제분석실</router-link>
    </v-list>
    <v-list class="v-col-7">
      <v-row class="justify-end">
        <!-- "menuList"에 대한 루프를 수행하여 메뉴 항목을 생성합니다. -->
        <template v-for="menu in menuList">
          <list-item v-if="menu.loginStatus" :key="menu.name">
            <!-- "menu.name"이 '로그아웃'인 경우 로그아웃 링크를 생성합니다. -->
            <template v-if="menu.name == '로그아웃'">
              <router-link to="/" @click="logout()">{{ menu.name }}</router-link>
            </template>
            <template v-else-if="menu.routeName === 'hi'">
              {{ menu.name }}
            </template>
            <!-- 그 외 메뉴 항목은 해당 routeName으로 라우팅 링크를 생성합니다. -->
            <template v-else>
              <router-link :to="{ name: menu.routeName }">{{ menu.name }}</router-link>
            </template>
          </list-item>
        </template>
      </v-row>
    </v-list>
  </v-app-bar>
</template>

<style scoped>
/* 헤더 스타일 */

.v-toolbar {
  display: fixed;
  width: 100%; /* 전체 너비 차지 */
  /* margin-bottom: 15rem; */
}

a {
  text-decoration: none;
  margin: 0.5rem;
  color: #606c8c;
}
</style>
