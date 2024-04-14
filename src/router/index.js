import { createRouter, createWebHistory } from 'vue-router';

const routes = [
  {
    path: "/",
    redirect: "/admin/login"
  },
  {
    path: '/abyss',
    name: "Abyss",
    component: () => import("@/views/module/root/index"),
    children: [
      {
        path: '/abyss/',
        name: 'Home',
        meta: { title: '首页' },
        component: () => import("@/views/common/home/index")
      }, {
        path: '/abyss/article/:id',
        name: 'Article',
        component: () => import("@/views/common/article/index")
      },
      {
        path: '/abyss/new_article/0/1',
        name: 'NewAticle',
        meta: { title: '发布帖子' },
        component: () => import("@/views/module/new/new-article/index")
      },
      {
        path: '/abyss/new_article/0/2',
        name: 'NewImage',
        meta: { title: '发布图片' },
        component: () => import("@/views/module/new/new-image/index")
      }, {
        path: '/abyss/new_article/0/3',
        name: 'NewVideo',
        meta: { title: '发布视频' },
        component: () => import("@/views/module/new/new-video/index")
      },
      {
        path: '/abyss/accountCenter',
        name: 'AccountCenter',
        component: () => import('@/views/module/account-center/root/index'),
        children: [{
          path: '/abyss/accountCenter/postList',
          name: 'PostList',
          meta: { title: '个人中心-我的帖子' },
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
          meta: { title: '编辑个人资料' },
          component: () => import('@/views/module/account-center/edit-myself/index')
        }]
      }, {
        path: '/abyss/notifications',
        component: () => import('@/views/notifications/index/index'),
        children: [{
          path: '/abyss/notifications/system',
          name: 'System',
          meta: { title: '系统消息' },
          component: () => import('@/views/notifications/system/index')
        }, {
          path: '/abyss/notifications/like',
          name: 'Reply',
          meta: { title: '收到的赞' },
          component: () => import('@/views/notifications/like/index')
        }]
      }]
  },
  {
    path: '/login',
    name: 'Login',
    meta: { title: '登录' },
    component: () => import('@/views/module/login/index.vue')
  },
  {
    path: '/register',
    name: 'Register',
    meta: { title: '注册' },
    component: () => import('@/views/module/register/index')
  },
  {
    path: "/test",
    name: 'Test',
    component: () => import('@/test/index'),
    meta: { title: '测试' }
  },
  {
    path: '/chat',
    name: 'Chat',
    meta: { title: '聊天' },
    component: () => import("@/views/common/chat/index"),
    children: [{
      path: '/chat/group',
      component: () => import("@/views/module/group-chat/index")
    },
    ]
  },
  {
    path: '/group/',
    name: 'Group',
    component: () => import("@/views/module/group-chat/index")
  },
  {
    path: '/admin/login',
    name: 'AdmainLogin',
    meta: { title: '登录' },
    component: () => import('@/views/admin/login/index')
  },
  {
    path: '/admin',
    name: 'Admin',
    meta: { title: '游戏社区后台管理系统' },
    component: () => import('@/views/admin/home/index'),
    children: [
      {
        path: '/admin/index',
        name: 'Index',
        meta: { title: '首页' },
        component: () => import('@/views/admin/index/index'),
      },
      {
        path: '/admin/manage/user',
        name: 'UserManage',
        meta: { title: '用户管理' },
        component: () => import('@/views/admin/user-manage/index')
      },
      {
        path: '/admin/manage/article',
        name: 'ArticleManage',
        meta: { title: '帖子管理' },
        component: () => import('@/views/admin/article-manage/index')
      },
      {
        path: '/admin/manage/comment',
        name: 'CommentManage',
        meta: { title: '评论管理' },
        component: () => import('@/views/admin/comment-manage/index')
      },
      {
        path: '/admin/manage/reply',
        name: 'ReplyManage',
        meta: { title: '回复管理' },
        component: () => import('@/views/admin/reply-manage/index')
      },
      {
        path: '/admin/manage/recommend',
        name: 'Recommend',
        meta: { title: '推荐管理' },
        component: () => import('@/views/admin/recommend-manage/index')
      }
    ]
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
    meta: {title: '404'},
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
