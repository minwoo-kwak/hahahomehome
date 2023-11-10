import { createRouter, createWebHistory } from 'vue-router'
import MainView from '../views/MainView.vue'
import UserView from '../views/UserView.vue'
import ApartView from '../views/ApartView.vue'

import LoginComponent from '../components/user/LoginComponent.vue'
import SignUpComponent from '../components/user/SignUpComponent.vue'
import MyPageComponent from '../components/user/MyPageComponent.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'main',
      component: MainView,
    },
    {
      path: '/user',
      name: 'user',
      component: UserView,
      children: [
        {
          path: 'login',
          name: 'login',
          component: LoginComponent,
        },
        {
          path: 'signup',
          name: 'signup',
          component: SignUpComponent,
        },
        {
          path: 'mypage',
          name: 'mypage',
          component: MyPageComponent,
        }
      ]
    },
    {
      path:'/apart/:code?',
      name:'apart',
      component: ApartView,
    },
    {
      path:'/calculate',
      name:'calculate',
      component:()=>import('../views/CalculatorPopUpView.vue')
    }
    
  ]
})

export default router
