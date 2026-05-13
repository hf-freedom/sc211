import { createRouter, createWebHistory } from 'vue-router'
import Activities from '../views/Activities.vue'
import UserCoupons from '../views/UserCoupons.vue'
import Statistics from '../views/Statistics.vue'
import Blacklist from '../views/Blacklist.vue'
import RiskRecords from '../views/RiskRecords.vue'
import Users from '../views/Users.vue'
import Products from '../views/Products.vue'

const routes = [
  { path: '/', redirect: '/activities' },
  { path: '/activities', component: Activities },
  { path: '/user-coupons', component: UserCoupons },
  { path: '/statistics', component: Statistics },
  { path: '/users', component: Users },
  { path: '/products', component: Products },
  { path: '/blacklist', component: Blacklist },
  { path: '/risk-records', component: RiskRecords }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
