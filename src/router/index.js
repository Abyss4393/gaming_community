import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'

const routes = [
  {
    path: "/",
    redirect: "/abyss/"
  },
  {
    path: '/test',
    component: () => import("@/views/other/index")
  },
  {
    path: '/abyss',
    name: "Abyss",
    component: () => import("@/views/module/root/index"),
    children: [{
      path: '/abyss/',
      name: 'Home',
      component: () => import("@/views/common/home/index")
    }, {
      path: '/abyss/article/:id',
      name: 'Article',
      component: () => import("@/views/common/article/index")
    },
    {
      path: '/abyss/new_article/0/1',
      name: 'NewAticle',
      component: () => import("@/views/module/new/new-article/index")
    },
    {
      path: '/abyss/new_article/0/2',
      name: 'NewImage',
      component: () => import("@/views/module/new/new-image/index")
    }, {
      path: '/abyss/new_article/0/3',
      name: 'NewVideo',
      component: () => import("@/views/module/new/new-video/index")
    },
    {
      path: '/abyss/accountCenter',
      name: 'AccountCenter',
      component: () => import('@/views/module/account-center/root/index'),
      children: [{
        path: '/abyss/accountCenter/postList',
        name: 'PostList',
        component: () => import('@/views/module/account-center/my-post/index')
      },
      {
        path: '/abyss/accountCenter/replyList',
        name: 'ReplyList',
        component: () => import('@/views/module/account-center/my-reply/index')
      },
      {
        path: '/abyss/accountCenter/comments',
        name: 'Comments',
        component: () => import('@/views/module/account-center/my-comment/index')
      },

      {
        path: '/abyss/accountCenter/collections',
        name: 'Collections',
        component: () => import('@/views/module/account-center/my-collection/index')
      }, {
        path: '/abyss/accountCenter/edit',
        name: 'Edit',
        component: () => import('@/views/module/account-center/edit-myself/index')
      }]
    }]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/module/login/index.vue')
  },
  {
    path: "/test",
    name: "Text",
    component: () => import("@/test/Test"),
    meta: {
      title: '测试'
    }
  },
  {
    path: '/chat',
    name: 'Chat',
    component: () => import("@/views/common/chat/index"),
    children: [{
      path: '/friends',
      component:
        () => import("@/views/module/friend-list/index")
    },
    ]
  },
  {
    path: '/group/',
    name: 'Group',
    component: () => import("@/views/module/group-chat/index")
  },
  {
    path: '/500.html',
    component: () => import('@/pages/server_exception/ServerException')

  },
  {
    path: '/:pathMatch(.*)',
    redirect: '/404.html',
    hidden: true
  },
  {
    path: '/404.html',
    component: () => import('@/pages/not_found/NotFound')
  }
]

const router = createRouter({
  mode: 'history',
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  if (to.meta.title) {
    document.title = to.meta.title
  };
  next();
})

export default router
